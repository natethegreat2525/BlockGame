package chunks;

import com.nshirley.engine3d.math.Vector3i;

public class RandomChunkBuilder implements ChunkBuilder {

	@Override
	public Chunk buildChunk(Vector3i pos, ChunkViewport cv) {
		Chunk chunk = new Chunk();
		
		cv.setChunkGlobalPos(chunk, pos.x, pos.y, pos.z);
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					if (Math.random() > .9) {
						short val = (short)(Math.random() * 3 + 1);
						chunk.setValue(i, j, k, val);
					}
				}
			}
		}
		return chunk;
	}

}
