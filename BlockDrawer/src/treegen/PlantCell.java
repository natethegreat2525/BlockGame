package treegen;

import com.nshirley.engine3d.math.Vector3f;

public class PlantCell {
	public Vector3f pos;
	public Vector3f dir;
	public int type; //cell gene index
	public double size;
	public int len;
	
	public PlantCell(Vector3f pos, Vector3f dir, int type, double size) {
		this.pos = pos;
		this.dir = dir;
		this.type = type;
		this.size = size;
		this.len = 0;
	}
}
