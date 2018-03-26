package world;

import com.nshirley.engine3d.math.Vector3i;

class SingleBlockUpdate {
	public SingleBlockUpdate(Vector3i pos, short val) {
		this.pos = pos;
		this.val = val;
	}
	public Vector3i pos;
	public short val;
}