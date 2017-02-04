package world;

import chunks.Chunk;

import com.nshirley.engine3d.math.Vector3i;

public class FlatChunkBuilder implements ChunkBuilder {

	@Override
	public ChunkData buildChunk(Vector3i pos) {
		ChunkData chunk = new ChunkData(pos);
		
		//cv.setChunkGlobalPos(chunk, pos.x, pos.y, pos.z);
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					
					int x = pos.x * Chunk.SIZE + i;
					int y = pos.y * Chunk.SIZE + j;
					int z = pos.z * Chunk.SIZE + k;
					if ( (pos.x == 0 && pos.y == 0 && pos.z == 0)) {
						short blockVal = (short) (Math.random() * 3 + 1);
						chunk.setValue(i, j, k, blockVal);
					}
				}
			}
		}
		return chunk;
	}

}
