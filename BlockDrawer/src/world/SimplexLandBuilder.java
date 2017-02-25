package world;

import simplex.Simplex;
import chunks.Chunk;

import com.nshirley.engine3d.math.Vector3i;

public class SimplexLandBuilder implements ChunkBuilder {

	private Simplex s = new Simplex((int) (Math.random() * Integer.MAX_VALUE));
	@Override
	public ChunkData buildChunk(Vector3i pos) {
		ChunkData chunk = new ChunkData(pos);
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					double x = (pos.x * Chunk.SIZE + i);
					double y = (pos.y * Chunk.SIZE + j);
					double z = (pos.z * Chunk.SIZE + k);
					double n = s.noise(x / 64.0, y / 64.0, z / 64.0) - y / 48.0;
					if (n > .5) {
						//short blockVal = (short) (Math.random() * 3 + 1);
						if (i == 0 || j == 0 || k == 0 || i == 15 || j == 15 || k == 15) {
							chunk.setValue(i, j, k, (short) 3);
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
