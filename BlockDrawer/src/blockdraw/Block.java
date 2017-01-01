package blockdraw;

import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.math.Vector2f;
import com.nshirley.engine3d.math.Vector3f;

//defines how a block is drawn/rendered
public abstract class Block {
	/**
	 * up+y down-y right+x left-x front+z back-z
	 * Faces go in order: up down right left front back
	 * light values hit corners in order urf urb ulf ulb drf drb dlf dlb
	 * @param vab
	 * @param offset
	 * @param faces
	 * @param lightValues
	 */
	public abstract void add(VertexArrayBuilder vab, Vector3f offset, boolean[] faces, double[] lightValues);
	
	/**
	 * True if a block is transparent and all blocks around it should still be rendered
	 * TODO more options for transparent blocks
	 * @return
	 */
	public boolean isTransparent() {
		return false;
	}
	
	/**
	 * Determines if block has block physics with player
	 * TODO make more robust collision system that allows half blocks etc
	 * @return
	 */
	public boolean isCollidable() {
		return true;
	}

	/**
	 * Returns true if the block is drawn
	 * @return
	 */
	public boolean isDrawn() {
		return true;
	}
	
	/**
	 * Finds upper left corner of texture
	 * @param texNum
	 * @param numTexWide
	 * @param numTexHigh
	 * @return
	 */
	protected static Vector2f texNumToUpperLeft(int texNum, int numTexWide, int numTexHigh) {
		int x = texNum % numTexWide;
		int y = texNum / numTexHigh;
		return new Vector2f(x * 1.0f / numTexWide, y * 1.0f / numTexHigh);
	}
	
	/**
	 * Finds lower right corner of texture
	 * @param texNum
	 * @param numTexWide
	 * @param numTexHigh
	 * @return
	 */
	protected static Vector2f texNumToLowerRight(int texNum, int numTexWide, int numTexHigh) {
		int x = 1 + (texNum % numTexWide);
		int y = 1 + (texNum / numTexHigh);
		return new Vector2f(x * 1.0f / numTexWide, y * 1.0f / numTexHigh);
	}

}
