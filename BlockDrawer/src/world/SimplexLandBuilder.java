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
					if (s.noise(x / 64.0, y / 64.0, z / 64.0) - y / 48.0 > .5) {
						//short blockVal = (short) (Math.random() * 3 + 1);
						if (Math.random() > .999) {
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
