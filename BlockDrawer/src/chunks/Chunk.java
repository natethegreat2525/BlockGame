package chunks;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Vector3i;

public class Chunk {
	
	public static final int SIZE = 16;

	public Vector3i position;
	
	/**
	 * Rendered chunk entity
	 */
	private Entity renderedChunk;
	
	/**
	 * Position of chunk within locality
	 */
	protected int lpx, lpy, lpz;
	
	protected ChunkViewport chunkViewport;
		
	public Chunk(Vector3i position) {
		this.position = position;
	}
	
	public void setEntity(Entity e) {
		this.renderedChunk = e;
	}
	
	public Entity getEntity() {
		return this.renderedChunk;
	}

	public void setLocalityPosition(int x, int y, int z) {
		lpx = x;
		lpy = y;
		lpz = z;
	}
	
	public void render() {
		if (renderedChunk != null) {
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
