package chunks;

import com.nshirley.engine3d.math.Vector3i;

import light.HeightChunk;
import world.ChunkData;

public class ChunkLight {
	
	public static final int SIZE = Chunk.SIZE;
	public static final int SIZE2 = SIZE * SIZE;
	public static final int SIZE3 = SIZE2 * SIZE;

	private byte[] vals;
	
	public ChunkLight() {
	}
	
	public void calculateLight(
			Chunk xp, Chunk xn,
			Chunk yp, Chunk yn,
			Chunk zp, Chunk zn,
			HeightChunk hc,
			Vector3i pos) {
		vals = new byte[SIZE3];
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				for (int y = 0; y < SIZE; y++) {
					vals[x + y * SIZE + z * SIZE2] = 80;
				}
				long height = hc.getValue(x, z);
				if (height < pos.y + Chunk.SIZE) {
					int y = pos.y + Chunk.SIZE - 1;
					while (y > height && y >= pos.y) {
						vals[x + (y - pos.y) * SIZE + z * SIZE2] = 127;
						y--;
					}
				}
			}
		}
	}
	
	public byte getLight(int x, int y, int z) {
		return vals[x + y * SIZE + z * SIZE2];
	}

}
