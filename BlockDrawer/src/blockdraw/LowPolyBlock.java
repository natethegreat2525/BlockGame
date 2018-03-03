package blockdraw;

import com.nshirley.engine3d.graphics.Vertex;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.graphics.VertexAttribute;
import com.nshirley.engine3d.math.Vector2f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector4f;

public class LowPolyBlock extends Block {

	private Vector2f texCoordLow;
	private Vector2f texCoordHigh;
	private Vector4f color;
	
	public LowPolyBlock(int texNum, int numTexWide, int numTexHigh, Vector4f color) {
		texCoordLow = Block.texNumToLowerRight(texNum, numTexWide, numTexHigh);
		texCoordHigh = Block.texNumToUpperLeft(texNum, numTexWide, numTexHigh);
		this.color = color;
	}
	
	public boolean isCollidable() {
		return false;
	}
	
	public boolean isTransparent() {
		return true;
	}
	
	public boolean blocksSun() {
		return false;
	}
	
	public int getPickGroup() {
		return 2;
	}
	
	@Override
	public void add(VertexArrayBuilder[] vabs, Vector3f offset, boolean[] faces, double[] lightValues) {
		//up down right left front back
		int baseVert = vabs[2].getNumVerts();
		
		float light = (float) Math.sqrt(lightValues[13] / 15.0);

		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 1, 1, 1, 1, texCoordHigh.x, texCoordHigh.y, light, color.x, color.y, color.z, color.w);
		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 0, 1, 0, 1, texCoordHigh.x, texCoordLow.y, light, color.x, color.y, color.z, color.w);
		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 0, 0, 0, 0, texCoordLow.x, texCoordLow.y, light, color.x, color.y, color.z, color.w);
		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 1, 0, 1, 0, texCoordLow.x, texCoordHigh.y, light, color.x, color.y, color.z, color.w);
		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 1, 1, 1, 0, texCoordHigh.x, texCoordHigh.y, light, color.x, color.y, color.z, color.w);
		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 0, 1, 0, 0, texCoordHigh.x, texCoordLow.y, light, color.x, color.y, color.z, color.w);
		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 0, 0, 0, 1, texCoordLow.x, texCoordLow.y, light, color.x, color.y, color.z, color.w);
		addVertex(vabs[2], 0, offset.x, offset.y, offset.z, 1, 0, 1, 1, texCoordLow.x, texCoordHigh.y, light, color.x, color.y, color.z, color.w);
		
		vabs[2].addTriangle(baseVert, baseVert + 2, baseVert + 1);
		vabs[2].addTriangle(baseVert, baseVert + 3, baseVert + 2);
		vabs[2].addTriangle(baseVert, baseVert + 1, baseVert + 2);
		vabs[2].addTriangle(baseVert, baseVert + 2, baseVert + 3);
		baseVert += 4;
		vabs[2].addTriangle(baseVert, baseVert + 2, baseVert + 1);
		vabs[2].addTriangle(baseVert, baseVert + 3, baseVert + 2);
		vabs[2].addTriangle(baseVert, baseVert + 1, baseVert + 2);
		vabs[2].addTriangle(baseVert, baseVert + 2, baseVert + 3);
	}
	
	private static void addVertex(VertexArrayBuilder vab, int idx, float ox, float oy, float oz, float w, float x, float y, float z, float u, float v, float light, float r, float g, float b, float a) {		
		vab.add(
				new Vertex(
						new VertexAttribute(new float[] {x + ox, y + oy, z + oz, w}),
						new VertexAttribute(new float[] {u, v}),
						new VertexAttribute(new float[] {light}),
						new VertexAttribute(new float[] {r, g, b, a})
				));
	}

}
