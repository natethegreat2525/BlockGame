package drawtest;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

import java.util.Vector;

import org.lwjgl.glfw.GLFW;

import physics.Rect;
import terraingenerators.FlatChunkBuilder;
import terraingenerators.HillsChunkBuilder;
import terraingenerators.SimplexLandBuilder;
import world.ChunkBuilderThread;
import world.Player;
import world.Raycast;
import world.World;
import chunks.Chunk;
import chunks.ChunkDrawBuilder;
import chunks.ChunkViewport;

import com.nshirley.engine3d.N3D;
import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.entities.shapes.Shape;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;
import com.nshirley.engine3d.window.Input;
import com.nshirley.engine3d.window.Mouse;
import com.nshirley.engine3d.window.Window;

import drawentity.ChunkEntity;

public class ChunkViewportTest {

	public static int WIDTH = 1024, HEIGHT = 768;
	//public static int WIDTH = 800, HEIGHT = 600;

	public static void main(String[] args) {
		Window win = new Window(WIDTH, HEIGHT, "Cube Test");
		win.setCursorMode(GLFW.GLFW_CURSOR_DISABLED);

		N3D.init();
		ChunkEntity.loadShader();

		Texture tx = new Texture("res/blocks.png");

		Camera3d c = new Camera3d((float) Math.toRadians(100), WIDTH, HEIGHT,
				.1f, 1000);
		
		World world = new World(new SimplexLandBuilder());
		
		ChunkViewport cv = new ChunkViewport(new Vector3i(), new Vector3i(10, 6, 10), world, tx);
		
		Mesh box = new Mesh(Shape.cube(), tx);
		
		ChunkBuilderThread builder = new ChunkBuilderThread(cv);
		Thread builderThread = new Thread(builder);
		builderThread.start();
		
		Vector3f camPos = new Vector3f();

		Player player = new Player(box, new Vector3f(), new Vector3f(.5f, 1.5f, .5f));
		 
		long time = System.currentTimeMillis();
		int count = 0;
		long deltaTime = System.currentTimeMillis();
		double delta = 1;
		while (!win.shouldClose()) {
			long newDelta = System.currentTimeMillis();
			delta = (newDelta - deltaTime) / (1000 / 60.0);
			deltaTime = newDelta;
			delta = Math.min(delta, 4);
			
			count++;
			if (count == 100) {
				count = 0;
				System.out.println(100.0 / ((System.currentTimeMillis() - time) / 1000.0));
				time = System.currentTimeMillis();
			}
			
			if (cv.getNumToTriangulate() < 15 || world.hasUpdates()) {
				//triggers another thread to build more
				builder.loadMore();
			}
			for (int i = 0; i < 15; i++) {
				cv.triangulateNextChunk();
			}
			if (cv.getNumToTriangulate() < 15) {
				//triggers another thread to build more
				builder.loadMore();
			}
			
			win.clear();
			win.pollEvents();
			
			float rotH = (float) Mouse.X * .3f;
			float rotV = (float) Mouse.Y * .3f;
			float rotHR = (float) Math.toRadians(rotH);
			float rotVR = (float) Math.toRadians(rotV);
			
			float speed = .3f;
			if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
				camPos.x -= speed * Math.cos(rotHR);
				camPos.z -= speed * Math.sin(rotHR);
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
				camPos.x += speed * Math.cos(rotHR);
				camPos.z += speed * Math.sin(rotHR);
			}
			float upAmt = (float) Math.sin(rotVR);
			float fwdAmt = (float) Math.cos(rotVR);
			if (Input.isKeyDown(GLFW.GLFW_KEY_UP)) {
				camPos.z -= speed * Math.cos(rotHR) * fwdAmt;
				camPos.x += speed * Math.sin(rotHR) * fwdAmt;
				camPos.y -= speed * upAmt;
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
				camPos.z += speed * Math.cos(rotHR) * fwdAmt;
				camPos.x -= speed * Math.sin(rotHR) * fwdAmt;
				camPos.y += speed * upAmt;
			}
			
			
			c.setRotation(new Vector3f((float) rotV, (float) rotH, 0)); 
			c.setPosition(camPos);
			
			Vector3f lookNorm = c.getLookDir().normalize();
			Vector3f lookSpd = lookNorm.mult(speed);

			N3D.pushMatrix();
			N3D.multMatrix(c.getTotalMatrix());

			cv.render(c.getPosition(), lookNorm);			

			Vector3f startRay = camPos.clone();
			Vector3f rayDir = lookSpd;

			Raycast rc = world.raycast(startRay, rayDir, 40);
			if (rc != null) {
				box.setModelMatrix(Matrix4f.translate(rc.position).multiply(Matrix4f.scale(new Vector3f(.1f, .1f, .1f))));
				box.render();
				if (Input.isKeyDown(GLFW.GLFW_KEY_P)) {
					world.setBlockValue(rc.blockPosition.x, rc.blockPosition.y, rc.blockPosition.z, (short) 0);
				}
			}
			
			float plSpeed = .05f;
			float jump = .2f;
			float xspd = 0, zspd = 0;
			if (Input.isKeyDown(GLFW.GLFW_KEY_W)) {
				zspd += 1;
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_S)) {
				zspd -= 1;
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_A)) {
				xspd += 1;
			}
			if (Input.isKeyDown(GLFW.GLFW_KEY_D)) {
				xspd -= 1;
			}
			xspd *= plSpeed;
			zspd *= plSpeed;
			
			if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
				if (player.isGrounded())
					player.setVelocityY(jump);
			}
			
			player.render();

			player.setVelocityX(xspd);
			player.setVelocityZ(zspd);
			
			player.update(world, new Vector3f(0, -.01f, 0), (float) delta);
			camPos = player.getPosition().add(new Vector3f(0, .7f, 0));
			int i = glGetError();
			if (i != GL_NO_ERROR) {
				System.out.println(i);
			}

			N3D.popMatrix();

			win.flip();
			if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				break;
		}
		builder.finish();
	}
}
