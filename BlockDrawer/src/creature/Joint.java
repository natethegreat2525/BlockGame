package creature;

import com.nshirley.engine3d.math.Vector3f;

public class Joint {

	public Vector3f origin;
	public Vector3f direction;
	public double length;
	public double value;
	
	public Box attached;
	
	public Joint(Vector3f o, Vector3f d, double len, double val, Box a) {
		origin = o;
		direction = d;
		length = len;
		value = val;
		attached = a;
	}
}
