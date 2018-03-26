package world;

import java.util.ArrayList;

import com.nshirley.engine3d.math.Vector3i;

public class BulkBlockUpdate {

	public ArrayList<SingleBlockUpdate> list = new ArrayList<SingleBlockUpdate>();
	
	public void setBlockValue(int x, int y, int z, short val) {
		this.setBlockValue(new Vector3i(x, y, z), val);
	}
	
	public void setBlockValue(Vector3i vec, short val) {
		list.add(new SingleBlockUpdate(vec, val));
	}
}