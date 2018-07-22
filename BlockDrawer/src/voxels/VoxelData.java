package voxels;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	
	public void setValue(int x, int y, int z, int value) {
		data[x + y * size.x + z * size.x * size.y] = value;
	}
	
	public int getValueSafe(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= size.x || y >= size.y || z >= size.z)
			return 0;
		return data[x + y * size.x + z * size.x * size.y];
	}
	
	public void setValueSafe(int x, int y, int z, int value) {
		if (x < 0 || y < 0 || z < 0 || x >= size.x || y >= size.y || z >= size.z)
			return;
		data[x + y * size.x + z * size.x * size.y] = value;
	}
	
	public static VoxelData fromStream(InputStream is) {
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String val = s.next();
		s.close();
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.fromJson(val, VoxelData.class);
	}
}
