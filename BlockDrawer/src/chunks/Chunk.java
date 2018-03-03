package chunks;

import world.ChunkData;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Vector3i;

public class Chunk {
	
	public static final int SIZE = 16;

	public Vector3i position;
	public ChunkLight lighting;
	public boolean highPriority;
	
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
	
	public void finalize() {
		if (!free) {
			System.out.println("Memory LEAK!");
		}
	}
	
	public void setEntity(Entity e) {
		this.freeEntity();
		
		this.renderedChunk = e;
		if (e != null) {
			free = false;
		}
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
	
	public void calculateLight() {
		ChunkData c = chunkViewport.getWorld().getChunkData(position);
		chunkViewport.calcLocalPos(this);
		Chunk xp = chunkViewport.getChunk(lpx + 1, lpy, lpz);
		Chunk xn = chunkViewport.getChunk(lpx - 1, lpy, lpz);
		Chunk yp = chunkViewport.getChunk(lpx, lpy + 1, lpz);
		Chunk yn = chunkViewport.getChunk(lpx, lpy - 1, lpz);
		Chunk zp = chunkViewport.getChunk(lpx, lpy, lpz + 1);
		Chunk zn = chunkViewport.getChunk(lpx, lpy, lpz - 1);
		if (lighting == null)
			lighting = new ChunkLight();
		lighting.calculateLight(c, xp, xn, yp, yn, zp, zn, chunkViewport.getHeightChunk(lpx, lpz), new Vector3i(position.x * SIZE, position.y * SIZE, position.z * SIZE), chunkViewport, highPriority);
	}
	private boolean free = false;
	public void freeEntity() {
		if (renderedChunk != null) {
			renderedChunk.free();
			renderedChunk = null;
		}
		free = true;
	}
}
