package drawentity;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;

import com.nshirley.engine3d.N3D;
import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.graphics.FramebufferObject;
import com.nshirley.engine3d.graphics.Shader;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.graphics.VertexArray;
import com.nshirley.engine3d.math.Vector3f;

public class ChunkEntity extends Mesh {

	public static Shader TerrainShader;
		
	public ChunkEntity(VertexArray va, Texture tex) {
		super(va, tex);
	}

	public void render() {
		//TODO: only bind textures and shaders once per frame
		TerrainShader.enable();
		tex.bind(0);
		TerrainShader.setUniformMat4f("vw_matrix", N3D.peekMatrix());
		TerrainShader.setUniformMat4f("ml_matrix", this.mlMatrix);
		float xStr = .1f, zStr = .1f;
		float wavelength = .6f;
		float phase = (System.currentTimeMillis() % 10000) * 3.1415f * 2 / 10000.0f;
		TerrainShader.setUniform4f("sway", xStr, wavelength, zStr, phase);
		TerrainShader.setUniform1i("tex", 0);
		va.render();
	}

	public static void loadShader() {
		TerrainShader = new Shader(
				ChunkEntity.class.getClassLoader().getResourceAsStream("shaders/terrain.vert"),
				ChunkEntity.class.getClassLoader().getResourceAsStream("shaders/terrain.frag"));
	}
}
