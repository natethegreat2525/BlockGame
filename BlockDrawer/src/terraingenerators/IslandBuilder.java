package terraingenerators;

import simplex.Simplex;
import world.ChunkBuilder;
import world.ChunkData;
import chunks.Chunk;

import com.nshirley.engine3d.math.Vector3i;

public class IslandBuilder implements ChunkBuilder {

	private Simplex s1 = new Simplex((int) (Math.random() * Integer.MAX_VALUE));
	private Simplex s2 = new Simplex((int) (Math.random() * Integer.MAX_VALUE));
	private Simplex s3 = new Simplex((int) (Math.random() * Integer.MAX_VALUE));
	private Simplex s4 = new Simplex((int) (Math.random() * Integer.MAX_VALUE));
	private double scale;
	private double height;
	private double radius;
	private int waterLevel;
	
	public IslandBuilder(double scale, double height, double radius, int waterLevel) {
		this.scale = scale;
		this.height = height;
		this.radius = radius;
		this.waterLevel = waterLevel;
	}
	
	@Override
	public ChunkData buildChunk(Vector3i pos) {
		ChunkData chunk = new ChunkData(pos);
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					double x = (pos.x * Chunk.SIZE + i);
					double y = (pos.y * Chunk.SIZE + j);
					double z = (pos.z * Chunk.SIZE + k);
					double h = Math.pow((s1.noise(x/scale/64.0, z/scale/64.0) + 1) / 4 +
							(s2.noise(x/scale/32.0, z/scale/32.0)+1) / 8 +
							(s3.noise(x/scale/16.0, z/scale/16.0)+1) / 16 +
							(s4.noise(x/scale/8.0, z/scale/8.0)+1) / 32, 3);
					h = h*height - Math.sqrt(x*x + z*z)*height/radius;
					//double n = s.noise(x / scale, y / scale, z / scale) - y / height;
					//if (n > .5) {
					double d = s1.noise(x/32.0, y/32.0, z/32.0);
					if (y < h + d*10) {
						//short blockVal = (short) (Math.random() * 3 + 1);
						if (y <= waterLevel + 2) {
							chunk.setValue(i, j, k, (short) 13);
						} else {
							chunk.setValue(i, j, k, (short) 10);
						}
					} else if (y <= waterLevel) {
						chunk.setValue(i, j, k, (short) 7);
					}
				}
			}
		}
		return chunk;
	}

}
