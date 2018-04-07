package voxels;
import com.nshirley.engine3d.math.Vector3i;

public class VoxelData {

	public Vector3i size;
	public int[] data;
	
	public VoxelData(Vector3i size, int[] data) {
		this.size = size;
		this.data = data;
	}
	
	public int getValue(int x, int y, int z) {
		return data[x + y * size.x + z * size.x * size.y];
	}
	public int getValueSafe(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= size.x || y >= size.y || z >= size.z)
			return 0;
		return data[x + y * size.x + z * size.x * size.y];
	}
}
