package blockdraw;

import com.nshirley.engine3d.graphics.Vertex;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.graphics.VertexAttribute;
import com.nshirley.engine3d.math.Vector2f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector4f;

public class WaterBlock extends Block {

	private Vector2f texCoordLow;
	private Vector2f texCoordHigh;
	private Vector4f color;
	
	public WaterBlock(int texNum, int numTexWide, int numTexHigh, Vector4f color) {
		texCoordLow = Block.texNumToLowerRight(texNum, numTexWide, numTexHigh);
		texCoordHigh = Block.texNumToUpperLeft(texNum, numTexWide, numTexHigh);
		this.color = color;
	}
	
	@Override
	public boolean isTransparent() {
		return true;
	}
	
	@Override
	public int transparency() {
		return 0;
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}
	
	public int getPickGroup() {
		return 1;
	}
	
	public byte lightValue() {
		return 0;
	}
	
	@Override
	public void add(VertexArrayBuilder[] vabs, Vector3f offset, boolean[] faces, double[] lightValues) {
		//up down right left front back
		int baseVert1 = vabs[1].getNumVerts();
		int baseVert2 = vabs[3].getNumVerts();
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
			lightUL = (float) Math.sqrt(lightUL);
			lightUR = (float) Math.sqrt(lightUR);
			lightBR = (float) Math.sqrt(lightBR);
			lightBL = (float) Math.sqrt(lightBL);
			if (faces[i]) {
				UniformBlock.addVertex(vabs[1], i, offset.x, offset.y, offset.z, 1, 1, 1, texCoordHigh.x, texCoordHigh.y, lightBR, color.x, color.y, color.z, color.w); //TODO lighting
				UniformBlock.addVertex(vabs[1], i, offset.x, offset.y, offset.z, 0, 1, 1, texCoordLow.x, texCoordHigh.y, lightBL, color.x, color.y, color.z, color.w); //TODO lighting
				UniformBlock.addVertex(vabs[1], i, offset.x, offset.y, offset.z, 0, 1, 0, texCoordLow.x, texCoordLow.y, lightUL, color.x, color.y, color.z, color.w); //TODO lighting
				UniformBlock.addVertex(vabs[1], i, offset.x, offset.y, offset.z, 1, 1, 0, texCoordHigh.x, texCoordLow.y, lightUR, color.x, color.y, color.z, color.w); //TODO lighting
				vabs[1].addTriangle(baseVert1, baseVert1 + 2, baseVert1 + 1);
				vabs[1].addTriangle(baseVert1, baseVert1 + 3, baseVert1 + 2);
				UniformBlock.addVertex(vabs[3], i, offset.x, offset.y, offset.z, 1, 1, 1, texCoordHigh.x, texCoordHigh.y, lightBR, color.x, color.y, color.z, color.w); //TODO lighting
				UniformBlock.addVertex(vabs[3], i, offset.x, offset.y, offset.z, 0, 1, 1, texCoordLow.x, texCoordHigh.y, lightBL, color.x, color.y, color.z, color.w); //TODO lighting
				UniformBlock.addVertex(vabs[3], i, offset.x, offset.y, offset.z, 0, 1, 0, texCoordLow.x, texCoordLow.y, lightUL, color.x, color.y, color.z, color.w); //TODO lighting
				UniformBlock.addVertex(vabs[3], i, offset.x, offset.y, offset.z, 1, 1, 0, texCoordHigh.x, texCoordLow.y, lightUR, color.x, color.y, color.z, color.w); //TODO lighting
				vabs[3].addTriangle(baseVert2 + 2, baseVert2, baseVert2 + 1);
				vabs[3].addTriangle(baseVert2 + 3, baseVert2, baseVert2 + 2);
				baseVert1 += 4;
				baseVert2 += 4;
			}
		}
		
	}

}
