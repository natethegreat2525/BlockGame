package drawtest;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

import org.lwjgl.glfw.GLFW;

import world.FlatChunkBuilder;
import world.HillsChunkBuilder;
import world.Raycast;
import world.SimplexLandBuilder;
import world.World;
import chunks.Chunk;
import chunks.ChunkViewport;

import com.nshirley.engine3d.N3D;
import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Entity;
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

	//public static int WIDTH = 1024, HEIGHT = 768;
	public static int WIDTH = 800, HEIGHT = 600;

	public static void main(String[] args) {
		Window win = new Window(WIDTH, HEIGHT, "Cube Test");
		win.setCursorMode(GLFW.GLFW_CURSOR_DISABLED);

		N3D.init();
		ChunkEntity.loadShader();

		Texture tx = new Texture("res/blocks.png");

		Camera3d c = new Camera3d((float) Math.toRadians(100), WIDTH, HEIGHT,
				.1f, 1000);
		
		World world = new World(new SimplexLandBuilder());
		
		ChunkViewport cv = new ChunkViewport(new Vector3i(), new Vector3i(7, 4, 7), world, tx);
		Vector3f camPos = new Vector3f();
		for (int i = 0; i < 150; i++) {
			cv.loadNextUnloadedChunk();
			cv.triangulateNextChunk();
		}
		
		Entity box = new Entity(Shape.cube(), tx);
		
		Vector3f startRay = new Vector3f(1, 0, 1);
		Vector3f rayDir = new Vector3f(1, -1, 0);
		while (!win.shouldClose()) {
			for (int i = 0; i < 15; i++) {
				cv.loadNextUnloadedChunk();
				cv.triangulateNextChunk();
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
			
			float dirZ = (float) (-speed * Math.cos(rotHR) * fwdAmt);
			float dirX = (float) (speed * Math.sin(rotHR) * fwdAmt);
			float dirY = (float) (-speed * upAmt);
			
			c.setRotation(new Vector3f((float) rotV, (float) rotH, 0)); 
			c.setPosition(camPos);

			N3D.pushMatrix();
			N3D.multMatrix(c.getTotalMatrix());

			cv.render(c.getPosition(), new Vector3f(0, 0, 0));
			
			startRay = camPos.clone();
			rayDir = new Vector3f(dirX, dirY, dirZ);

			if (((System.currentTimeMillis() / 30) % 300) == 0)
				System.out.println(Math.random());
			Raycast rc = world.raycast(startRay, rayDir, 40, box);
			if (rc != null) {
				box.setModelMatrix(Matrix4f.translate(rc.position).multiply(Matrix4f.scale(new Vector3f(.1f, .1f, .1f))));
				box.render();
				if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
					world.setBlockValue(rc.blockPosition.x, rc.blockPosition.y, rc.blockPosition.z, (short) 0);
				}
			}
			
			int i = glGetError();
			if (i != GL_NO_ERROR) {
				System.out.println(i);
			}

			N3D.popMatrix();

			win.flip();
			if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				break;
		}
	}
}
