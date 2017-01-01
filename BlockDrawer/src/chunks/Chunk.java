package chunks;

import com.nshirley.engine3d.entities.Entity;

public class Chunk {

	public static final int SIZE = 16;
	public static final int SIZE2 = SIZE * SIZE;
	public static final int DATA_LENGTH = SIZE * SIZE * SIZE;
	
	/**
	 * State of chunk
	 */
	private boolean empty;
	private short[] data;
	
	/**
	 * Rendered chunk entity
	 */
	private Entity renderedChunk;
	
	/**
	 * Position of chunk within locality
	 */
	protected int lpx, lpy, lpz;
	
	protected ChunkViewport chunkViewport;
		
	public Chunk() {
		empty = true;
	}
	
	public Chunk(short[] data) {
		if (data.length != DATA_LENGTH) {
			throw new IllegalArgumentException("Invalid chunk data length");
		}
		
		this.data = data;
		this.empty = false;
	}
	
	public void setEntity(Entity e) {
		this.renderedChunk = e;
	}
	
	public Entity getEntity() {
		return this.renderedChunk;
	}
	
	public short[] getData() {
		return data;
	}
	
	public void setValue(int x, int y, int z, short value) {
		if (empty) {
			if (value == 0) {
				return;
			}
			this.data = new short[DATA_LENGTH];
			empty = false;
		}
		
		data[x + y * SIZE + z * SIZE2] = value;
	}
	
	public short getValue(int x, int y, int z) {
		if (empty) {
			return 0;
		}
		
		return data[x + y * SIZE + z * SIZE2];
	}

	public void setLocalityPosition(int x, int y, int z) {
		lpx = x;
		lpy = y;
		lpz = z;
	}
	
	public void render() {
		if (renderedChunk != null && !empty) {
			renderedChunk.render();
		}
	}
	
	public int countLoadedNeighbors() {
		int count = 0;
		count += chunkViewport.getChunk(lpx + 1, lpy, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy + 1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy, lpz + 1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx - 1, lpy, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy - 1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy, lpz - 1) != null ? 1 : 0;
		return count;
	}

	public void freeEntity() {
		if (renderedChunk != null) {
			renderedChunk.free();
			renderedChunk = null;
		}
	}
}
