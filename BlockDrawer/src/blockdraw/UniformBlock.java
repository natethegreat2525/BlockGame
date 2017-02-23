package blockdraw;

import com.nshirley.engine3d.graphics.Vertex;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.graphics.VertexAttribute;
import com.nshirley.engine3d.math.Vector2f;
import com.nshirley.engine3d.math.Vector3f;

public class UniformBlock extends Block {

	private Vector2f texCoordLow;
	private Vector2f texCoordHigh;
	private int texNum;
	
	public UniformBlock(int texNum, int numTexWide, int numTexHigh) {
		this.texNum = texNum;
		texCoordLow = Block.texNumToLowerRight(texNum, numTexWide, numTexHigh);
		texCoordHigh = Block.texNumToUpperLeft(texNum, numTexWide, numTexHigh);
	}
	
	public byte lightValue() {
		if (texNum == 1) {
			return 15;
		}
		return 0;
	}
	
	@Override
	public void add(VertexArrayBuilder vab, Vector3f offset, boolean[] faces, double[] lightValues) {
		//up down right left front back
		int baseVert = vab.getNumVerts();
		for (int i = 0; i < 6; i++) {
			float lightUL = 0;
			float lightUR = 0;
			float lightBR = 0;
			float lightBL = 0;
			switch (i) {
			case 0:
				lightUL = (float) lightValues[6] + (float) lightValues[7] + (float) lightValues[15] + (float) lightValues[16];
				lightUR = (float) lightValues[7] + (float) lightValues[8] + (float) lightValues[17] + (float) lightValues[16];
				lightBR = (float) lightValues[17] + (float) lightValues[25] + (float) lightValues[26] + (float) lightValues[16];
				lightBL = (float) lightValues[15] + (float) lightValues[24] + (float) lightValues[25] + (float) lightValues[16];
				break;
			case 1:
				lightUR = (float) lightValues[0] + (float) lightValues[1] + (float) lightValues[9] + (float) lightValues[10];
				lightUL = (float) lightValues[1] + (float) lightValues[2] + (float) lightValues[10] + (float) lightValues[11];
				lightBL = (float) lightValues[10] + (float) lightValues[11] + (float) lightValues[19] + (float) lightValues[20];
				lightBR = (float) lightValues[9] + (float) lightValues[10] + (float) lightValues[18] + (float) lightValues[19];
				break;
			case 2:
				lightUR = (float) lightValues[2] + (float) lightValues[5] + (float) lightValues[11] + (float) lightValues[14];
				lightUL = (float) lightValues[5] + (float) lightValues[8] + (float) lightValues[14] + (float) lightValues[17];
				lightBL = (float) lightValues[14] + (float) lightValues[17] + (float) lightValues[23] + (float) lightValues[26];
				lightBR = (float) lightValues[11] + (float) lightValues[14] + (float) lightValues[20] + (float) lightValues[23];
				break;
			case 3:
				lightBR = (float) lightValues[0] + (float) lightValues[3] + (float) lightValues[9] + (float) lightValues[12];
				lightBL = (float) lightValues[3] + (float) lightValues[6] + (float) lightValues[12] + (float) lightValues[15];
				lightUL = (float) lightValues[12] + (float) lightValues[15] + (float) lightValues[21] + (float) lightValues[24];
				lightUR = (float) lightValues[9] + (float) lightValues[12] + (float) lightValues[18] + (float) lightValues[21];
				break;
			case 4:
				lightBR = (float) lightValues[18] + (float) lightValues[19] + (float) lightValues[21] + (float) lightValues[22];
				lightUR = (float) lightValues[19] + (float) lightValues[20] + (float) lightValues[22] + (float) lightValues[23];
				lightUL = (float) lightValues[22] + (float) lightValues[23] + (float) lightValues[25] + (float) lightValues[26];
				lightBL = (float) lightValues[21] + (float) lightValues[22] + (float) lightValues[24] + (float) lightValues[25];
				break;
			case 5:
				lightUR = (float) lightValues[0] + (float) lightValues[1] + (float) lightValues[3] + (float) lightValues[4];
				lightBR = (float) lightValues[1] + (float) lightValues[2] + (float) lightValues[4] + (float) lightValues[5];
				lightBL = (float) lightValues[4] + (float) lightValues[5] + (float) lightValues[7] + (float) lightValues[8];
				lightUL = (float) lightValues[3] + (float) lightValues[4] + (float) lightValues[6] + (float) lightValues[7];
				break;
			}
			lightUL = lightUL / (15 * 4);
			lightUR = lightUR / (15 * 4);
			lightBR = lightBR / (15 * 4);
			lightBL = lightBL / (15 * 4);
//			lightUL = (float) Math.sqrt(lightUL);
//			lightUR = (float) Math.sqrt(lightUR);
//			lightBR = (float) Math.sqrt(lightBR);
//			lightBL = (float) Math.sqrt(lightBL);
			lightUL *= lightUL;
			lightBL *= lightBL;
			lightUR *= lightUR;
			lightBR *= lightBR;
			if (faces[i]) {
				addVertex(vab, i, offset.x, offset.y, offset.z, 1, 1, 1, texCoordHigh.x, texCoordHigh.y, lightBR); //TODO lighting
				addVertex(vab, i, offset.x, offset.y, offset.z, 0, 1, 1, texCoordLow.x, texCoordHigh.y, lightBL); //TODO lighting
				addVertex(vab, i, offset.x, offset.y, offset.z, 0, 1, 0, texCoordLow.x, texCoordLow.y, lightUL); //TODO lighting
				addVertex(vab, i, offset.x, offset.y, offset.z, 1, 1, 0, texCoordHigh.x, texCoordLow.y, lightUR); //TODO lighting
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
