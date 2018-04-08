package physics;

import com.nshirley.engine3d.math.Vector3f;

public class PhysRect extends Rect {
	//public boolean terrainCollision;
//	public double friction;
//	public double maxSpeed;
//	public double elasticity;
	
	public PhysCallback callback;
	public boolean render;
	
	public PhysRect(Vector3f size) {
		super(size);
	}

	public PhysRect(Vector3f size, Vector3f pos, PhysCallback cb) {
		super(size, pos);
		callback = cb;
	}

}
