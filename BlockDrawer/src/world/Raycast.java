package world;

import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

public class Raycast {
	
	public Vector3f position;
	public Vector3f normal;
	public Vector3i blockPosition;
	
	public Raycast(Vector3f position, Vector3f normal, Vector3i blockPosition) {
		this.position = position;
		this.normal = normal;
		this.blockPosition = blockPosition;
	}
	
}
