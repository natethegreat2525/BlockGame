package blockdraw;

import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.math.Vector3f;

public class NullBlock extends Block {

	@Override
	public void add(VertexArrayBuilder[] vabs, Vector3f offset, boolean[] faces,
			double[] lightValues) {
	}
	
	@Override
	public boolean isTransparent() {
		return false;
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}
	
	@Override
	public boolean isDrawn() {
		return false;
	}

}
