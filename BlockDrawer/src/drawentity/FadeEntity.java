package drawentity;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.graphics.Shader;
import com.nshirley.engine3d.graphics.VertexArray;

public class FadeEntity extends Entity {

	public static Shader FadeShader;
	public VertexArray fade;
	public FadeEntity() {
		super(null, null);
		fade = new VertexArray(6);
	}

	public void render(float r, float g, float b, float a) {
		//TODO: only bind textures and shaders once per frame
		FadeShader.enable();
		FadeShader.setUniform4f("col", r, g, b, a);
		fade.render();
	}

	public static void loadShader() {
		FadeShader = new Shader(
				ChunkEntity.class.getClassLoader().getResourceAsStream("shaders/fade.vert"),
				ChunkEntity.class.getClassLoader().getResourceAsStream("shaders/fade.frag"));
	}
}
