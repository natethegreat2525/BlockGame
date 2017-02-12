package blockdraw;

import com.nshirley.engine3d.graphics.Vertex;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.graphics.VertexAttribute;
import com.nshirley.engine3d.math.Vector2f;
import com.nshirley.engine3d.math.Vector3f;

public class UniformBlock extends Block {

	private Vector2f texCoordLow;
	private Vector2f texCoordHigh;
	
	public UniformBlock(int texNum, int numTexWide, int numTexHigh) {
		texCoordLow = Block.texNumToLowerRight(texNum, numTexWide, numTexHigh);
		texCoordHigh = Block.texNumToUpperLeft(texNum, numTexWide, numTexHigh);
	}
	
	@Override
	public void add(VertexArrayBuilder vab, Vector3f offset, boolean[] faces, double[] lightValues) {
		//up down right left front back
		int baseVert = vab.getNumVerts();
		for (int i = 0; i < 6; i++) {
			float light = 0;
			switch (i) {
			case 0:
				light = (float) lightValues[16];
				break;
			case 1:
				light = (float) lightValues[10];
				break;
			case 2:
				light = (float) lightValues[14];
				break;
			case 3:
				light = (float) lightValues[12];
				break;
			case 4:
				light = (float) lightValues[22];
				break;
			case 5:
				light = (float) lightValues[4];
				break;
			}
			light = light / 128;
			if (faces[i]) {
				addVertex(vab, i, offset.x, offset.y, offset.z, 1, 1, 1, texCoordHigh.x, texCoordHigh.y, light); //TODO lighting
				addVertex(vab, i, offset.x, offset.y, offset.z, 0, 1, 1, texCoordLow.x, texCoordHigh.y, light); //TODO lighting
				addVertex(vab, i, offset.x, offset.y, offset.z, 0, 1, 0, texCoordLow.x, texCoordLow.y, light); //TODO lighting
				addVertex(vab, i, offset.x, offset.y, offset.z, 1, 1, 0, texCoordHigh.x, texCoordLow.y, light); //TODO lighting
				vab.addTriangle(baseVert, baseVert + 2, baseVert + 1);
				vab.addTriangle(baseVert, baseVert + 3, baseVert + 2);
				baseVert += 4;
			}
		}
		
	}
	
	private static void addVertex(VertexArrayBuilder vab, int idx, float ox, float oy, float oz, float x, float y, float z, float u, float v, float light) {
		float x1 = x, y1 = y, z1 = z;
		switch (idx) {
		//case 0:
			//up
			//do nothing	
			//break;
		case 1:
			//down
			x1 = 1 - x;
			y1 = 1 - y;
			break;
		case 2:
			//right
			x1 = y;
			y1 = 1 - x;
			break;
		case 3:
			//left
			y1 = 1 - x;
			z1 = 1 - z;
			x1 = 1 - y;
			break;
		case 4:
			//front
			z1 = y;
			y1 = 1 - x;
			x1 = 1 - z;
			break;
		case 5:
			//back
			y1 = 1 - x;
			z1 = 1 - y;
			x1 = z;
		}
		vab.add(
				new Vertex(
						new VertexAttribute(new float[] {x1 + ox, y1 + oy, z1 + oz}),
						new VertexAttribute(new float[] {u, v}),
						new VertexAttribute(new float[] {light})
				));
	}

}
