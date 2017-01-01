package drawtest;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
import chunks.Chunk;
import chunks.ChunkDrawBuilder;
import chunks.ChunkViewport;
import chunks.RandomChunkBuilder;

import com.nshirley.engine3d.N3D;
import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;
import com.nshirley.engine3d.window.Window;

import drawentity.ChunkEntity;

public class SingleChunkTest {

	public static int WIDTH = 1024, HEIGHT = 768;

	public static void main(String[] args) {
		Window win = new Window(WIDTH, HEIGHT, "Cube Test");

		N3D.init();
		ChunkEntity.loadShader();

		Texture tx = new Texture("res/blocks.png");

		Camera3d c = new Camera3d((float) Math.toRadians(100), WIDTH, HEIGHT,
				.1f, 1000);
		Chunk chunk = new Chunk();
		ChunkViewport cv = new ChunkViewport(new Vector3i(), new Vector3i(5, 5, 5), new RandomChunkBuilder(), tx);
		cv.setChunk(chunk, 5, 5, 5);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					if (Math.random() > .2) {
						short val = (short)(Math.random() * 3 + 1);
						chunk.setValue(i, j, k, val);
					}
				}
			}
		}
		chunk.setValue(0, 0, 0, (short) 1);
		ChunkDrawBuilder.generateChunkEntity(chunk, tx);
		Entity bird = chunk.getEntity();
		while (!win.shouldClose()) {
			win.clear();
			win.pollEvents();
			c.setPosition(new Vector3f(0, 0, -20));

			N3D.pushMatrix();
			N3D.multMatrix(c.getTotalMatrix());
			float t = (float) ((System.currentTimeMillis() / 10.0) % 360);


			bird.setModelMatrix(Matrix4f.rotateY(t).multiply(
					Matrix4f.translate(new Vector3f(0, 0, 0))));
			bird.render();

			bird.setModelMatrix(Matrix4f.rotateX(t).multiply(
					Matrix4f.translate(new Vector3f(3, 0, 0))));
			bird.render();

			bird.setModelMatrix(Matrix4f.translate(new Vector3f(-3, 0, 0))
					.multiply(Matrix4f.rotateZ(t)));
			bird.render();
			
			int i = glGetError();
			if (i != GL_NO_ERROR) {
				System.out.println(i);
			}

			N3D.popMatrix();

			win.flip();
		}
	}
}
