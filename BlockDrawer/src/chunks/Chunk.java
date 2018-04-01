package chunks;

import world.ChunkData;

import java.util.ArrayList;

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
	
	public int countLoadedAndDiagNeighbors() {
		int count = 0;
		count += chunkViewport.getChunk(lpx + 1, lpy, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy + 1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy, lpz + 1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx - 1, lpy, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy - 1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy, lpz - 1) != null ? 1 : 0;
		
		count += chunkViewport.getChunk(lpx+1, lpy+1, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx+1, lpy+1, lpz-1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx+1, lpy-1, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx+1, lpy-1, lpz-1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy+1, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy+1, lpz-1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy-1, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy-1, lpz-1) != null ? 1 : 0;

		count += chunkViewport.getChunk(lpx+1, lpy+1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx+1, lpy-1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy+1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy-1, lpz) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx+1, lpy, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx+1, lpy, lpz-1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx-1, lpy, lpz-1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy+1, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy+1, lpz-1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy-1, lpz+1) != null ? 1 : 0;
		count += chunkViewport.getChunk(lpx, lpy-1, lpz-1) != null ? 1 : 0;

		return count;
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
	
	public void calculateLight(boolean calculateRecursively) {
		ArrayList<Vector3i> updateChunks = new ArrayList<Vector3i>();
		updateChunks.add(this.position);
		while (updateChunks.size() > 0) {
			Chunk chunk;
			Vector3i pos = updateChunks.remove(0);
			if (this.position.equals(pos)) {
				chunk = this;
			} else {
				chunk = chunkViewport.getChunk(pos.x, pos.y, pos.z);
				if (chunk == null) {
					chunk = new Chunk(pos);
				}
			}

			ChunkData c = chunkViewport.getWorld().getChunkData(chunk.position);
			chunkViewport.calcLocalPos(chunk);
			Chunk xp = chunkViewport.getChunkGlobalPos(chunk.position.x + 1, chunk.position.y, chunk.position.z);
			Chunk xn = chunkViewport.getChunkGlobalPos(chunk.position.x - 1, chunk.position.y, chunk.position.z);
			Chunk yp = chunkViewport.getChunkGlobalPos(chunk.position.x, chunk.position.y + 1, chunk.position.z);
			Chunk yn = chunkViewport.getChunkGlobalPos(chunk.position.x, chunk.position.y - 1, chunk.position.z);
			Chunk zp = chunkViewport.getChunkGlobalPos(chunk.position.x, chunk.position.y, chunk.position.z + 1);
			Chunk zn = chunkViewport.getChunkGlobalPos(chunk.position.x, chunk.position.y, chunk.position.z - 1);
			if (chunk.lighting == null) {
				chunk.lighting = new ChunkLight();
			}
			ArrayList<Vector3i> chunks = chunk.lighting.calculateLight(c, xp, xn, yp, yn, zp, zn, chunkViewport.getHeightChunkGlobalPos(chunk.position.x, chunk.position.z), new Vector3i(chunk.position.x * SIZE, chunk.position.y * SIZE, chunk.position.z * SIZE), chunkViewport, chunk.highPriority, true);
			updateChunks.addAll(chunks);
			if(!calculateRecursively) {
				for (Vector3i vec : updateChunks) {
					chunkViewport.addUpdateChunk(vec, chunk.highPriority);
				}
				return;
			}
		}
			
	}
	
	private boolean free = true;
	public void freeEntity() {
		if (renderedChunk != null) {
			renderedChunk.free();
			renderedChunk = null;
		}
		free = true;
	}
}
