package drawtest;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

import org.lwjgl.glfw.GLFW;

import world.HillsChunkBuilder;
import world.World;
import chunks.Chunk;
import chunks.ChunkViewport;

import com.nshirley.engine3d.N3D;
import com.nshirley.engine3d.entities.Camera3d;
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

	public static void main(String[] args) {
		Window win = new Window(WIDTH, HEIGHT, "Cube Test");
		win.setCursorMode(GLFW.GLFW_CURSOR_DISABLED);

		N3D.init();
		ChunkEntity.loadShader();

		Texture tx = new Texture("res/blocks.png");

		Camera3d c = new Camera3d((float) Math.toRadians(100), WIDTH, HEIGHT,
				.1f, 1000);
		
		World world = new World(new HillsChunkBuilder());
		ChunkViewport cv = new ChunkViewport(new Vector3i(), new Vector3i(10, 5, 10), world, tx);
		Vector3f camPos = new Vector3f();
		for (int i = 0; i < 150; i++) {
			cv.loadNextUnloadedChunk();
			cv.triangulateNextChunk();
		}
		while (!win.shouldClose()) {
//			System.out.println("Percent Hit " + (World.hits / (World.hits + World.misses + 0.0)) + "\t Total: " + (World.hits + World.misses) + "\t Size: " + World.size);
//			System.out.println("Min " + World.mx + " " + World.my + " " + World.mz + "\t Max " + World.Mx + " " + World.My + " " + World.Mz);
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
			
			c.setRotation(new Vector3f((float) rotV, (float) rotH, 0)); 
			c.setPosition(camPos);

			N3D.pushMatrix();
			N3D.multMatrix(c.getTotalMatrix());

			cv.render(c.getPosition(), new Vector3f(0, 0, 0));
			
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
