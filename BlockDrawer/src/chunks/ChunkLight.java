package chunks;

import java.util.LinkedList;

import blockdraw.Block;
import blockdraw.BlockContainer;

import com.nshirley.engine3d.math.Vector3i;

import light.HeightChunk;
import world.ChunkData;

public class ChunkLight {
	//MOON LIGHT = 7, DAY LIGHT = 15
	public static final byte SKY_LIGHT = 7;
	
	public static final int SIZE = Chunk.SIZE;
	public static final int SIZE2 = SIZE * SIZE;
	public static final int SIZE3 = SIZE2 * SIZE;

	private byte[] vals;
	
	public ChunkLight() {
	}
	
	public void calculateLight(
			ChunkData c,
			Chunk xp, Chunk xn,
			Chunk yp, Chunk yn,
			Chunk zp, Chunk zn,
			HeightChunk hc,
			Vector3i pos) {
		LinkedList<Vector3i> active = new LinkedList<Vector3i>();
		vals = new byte[SIZE3];
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				for (int y = 0; y < SIZE; y++) {
					Block block = BlockContainer.getBlockType(c.getValue(x, y, z));
					if (block.lightValue() > 0) {
						vals[x + y * SIZE + z * SIZE2] = block.lightValue();
						active.add(new Vector3i(x, y, z));
					} else if (block.isTransparent()) {
						vals[x + y * SIZE + z * SIZE2] = 1;
					}
				}
				long height = hc.getValue(x, z);
				if (height < pos.y + Chunk.SIZE) {
					int y = pos.y + Chunk.SIZE - 1;
					while (y > height && y >= pos.y) {
						vals[x + (y - pos.y) * SIZE + z * SIZE2] = SKY_LIGHT;
						active.add(new Vector3i(x, y - pos.y, z));
						y--;
					}
				}
			}
		}
		if (xp != null && xp.lighting != null) {
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					byte l = (byte) (xp.lighting.getLight(0, i, j) - 1);
					int idx = SIZE - 1 + i * SIZE + j * SIZE2;
					if (l > vals[idx]) {
						vals[idx] = l;
						active.add(new Vector3i(SIZE - 1, i, j));
					}
				}
			}
		}
		if (yp != null && yp.lighting != null) {
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					byte l = (byte) (yp.lighting.getLight(i, 0, j) - 1);
					int idx = i + (SIZE - 1) * SIZE + j * SIZE2;
					if (l > vals[idx]) {
						vals[idx] = l;
						active.add(new Vector3i(i, SIZE - 1, j));
					}
				}
			}
		}
		if (zp != null && zp.lighting != null) {
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					byte l = (byte) (zp.lighting.getLight(i, j, 0) - 1);
					int idx = i + j * SIZE + (SIZE - 1) * SIZE2;
					if (l > vals[idx]) {
						vals[idx] = l;
						active.add(new Vector3i(i, j, SIZE - 1));
					}
				}
			}
		}
		if (xn != null && xn.lighting != null) {
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					byte l = (byte) (xn.lighting.getLight(SIZE - 1, i, j) - 1);
					int idx = i * SIZE + j * SIZE2;
					if (l > vals[idx]) {
						vals[idx] = l;
						active.add(new Vector3i(0, i, j));
					}
				}
			}
		}
		if (yn != null && yn.lighting != null) {
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					byte l = (byte) (yn.lighting.getLight(i, SIZE - 1, j) - 1);
					int idx = i + j * SIZE2;
					if (l > vals[idx]) {
						vals[idx] = l;
						active.add(new Vector3i(i, 0, j));
					}
				}
			}
		}
		if (zn != null && zn.lighting != null) {
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					byte l = (byte) (zn.lighting.getLight(i, j, SIZE - 1) - 1);
					int idx = i + j * SIZE;
					if (l > vals[idx]) {
						vals[idx] = l;
						active.add(new Vector3i(i, j, 0));
					}
				}
			}
		}
		while (active.size() > 0) {
			Vector3i cur = active.removeFirst();
			int val = vals[cur.x + cur.y * SIZE + cur.z * SIZE2];
			if (cur.x > 0) {
				int idx = cur.x - 1 + cur.y * SIZE + cur.z * SIZE2;
				if (vals[idx] < val - 1 && BlockContainer.getBlockType(c.getValue(cur.x - 1, cur.y, cur.z)).isTransparent()) {
					vals[idx] = (byte) (val - 1);
					active.add(new Vector3i(cur.x - 1, cur.y, cur.z));
				}
			}
			if (cur.x < SIZE - 1) {
				int idx = cur.x + 1 + cur.y * SIZE + cur.z * SIZE2;
				if (vals[idx] < val - 1 && BlockContainer.getBlockType(c.getValue(cur.x + 1, cur.y, cur.z)).isTransparent()) {
					vals[idx] = (byte) (val - 1);
					active.add(new Vector3i(cur.x + 1, cur.y, cur.z));
				}
			}
			if (cur.y > 0) {
				int idx = cur.x + (cur.y - 1) * SIZE + cur.z * SIZE2;
				if (vals[idx] < val - 1 && BlockContainer.getBlockType(c.getValue(cur.x, cur.y - 1, cur.z)).isTransparent()) {
					vals[idx] = (byte) (val - 1);
					active.add(new Vector3i(cur.x, cur.y - 1, cur.z));
				}
			}
			if (cur.y < SIZE - 1) {
				int idx = cur.x + (cur.y + 1) * SIZE + cur.z * SIZE2;
				if (vals[idx] < val - 1 && BlockContainer.getBlockType(c.getValue(cur.x, cur.y + 1, cur.z)).isTransparent()) {
					vals[idx] = (byte) (val - 1);
					active.add(new Vector3i(cur.x, cur.y + 1, cur.z));
				}
			}
			if (cur.z > 0) {
				int idx = cur.x + cur.y * SIZE + (cur.z - 1) * SIZE2;
				if (vals[idx] < val - 1 && BlockContainer.getBlockType(c.getValue(cur.x, cur.y, cur.z - 1)).isTransparent()) {
					vals[idx] = (byte) (val - 1);
					active.add(new Vector3i(cur.x, cur.y, cur.z - 1));
				}
			}
			if (cur.z < SIZE - 1) {
				int idx = cur.x + cur.y * SIZE + (cur.z + 1) * SIZE2;
				if (vals[idx] < val - 1 && BlockContainer.getBlockType(c.getValue(cur.x, cur.y, cur.z + 1)).isTransparent()) {
					vals[idx] = (byte) (val - 1);
					active.add(new Vector3i(cur.x, cur.y, cur.z + 1));
				}
			}
		}
	}
	
	public byte getLight(int x, int y, int z) {
		return vals[x + y * SIZE + z * SIZE2];
	}

}
