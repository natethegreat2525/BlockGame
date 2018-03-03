package terraingenerators;

import simplex.Simplex;
import world.ChunkBuilder;
import world.ChunkData;
import chunks.Chunk;

import com.nshirley.engine3d.math.Vector3i;

public class SimplexLandBuilder implements ChunkBuilder {

	private Simplex s = new Simplex((int) (Math.random() * Integer.MAX_VALUE));
	private double scale;
	private double height;
	
	public SimplexLandBuilder() {
		this(64, 48);
	}
	
	public SimplexLandBuilder(double scale, double height) {
		this.scale = scale;
		this.height = height;
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
					double n = s.noise(x / scale, y / scale, z / scale) - y / height;
					if (n > .5) {
						//short blockVal = (short) (Math.random() * 3 + 1);
						if (i == 0 || j == 0 || k == 0 || i == 15 || j == 15 || k == 15) {
							chunk.setValue(i, j, k, (short) 3); //edge
						} else if (n < .5001) {
							chunk.setValue(i, j, k, (short) 2);
						} else {
							chunk.setValue(i, j, k, (short) 1);
						}
					}
				}
			}
		}
		return chunk;
	}

}
