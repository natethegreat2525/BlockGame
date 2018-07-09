package chunks;

import world.ChunkData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.math.Vector3i;

import light.HeightChunk;

public class Chunk {
	
	public static final int SIZE = 16;

	public Vector3i position;
	public ChunkLight lighting;
	public boolean highPriority;
	
	/**
	 * Rendered chunk entity
	 */
	private Mesh renderedChunk;
	
	/**
	 * Second pass render (usually for water)
	 */
	private Mesh renderedChunkSecondPass;
	
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
	
	public void setEntity(Mesh e, Mesh e2) {
		this.freeEntity();
		
		this.renderedChunk = e;
		this.renderedChunkSecondPass = e2;
		if (e != null || e2 != null) {
			free = false;
		}
	}
	
	public Mesh getEntity(int pass) {
		if (pass == 0) {
			return this.renderedChunk;
		}
		if (pass == 1) {
			return this.renderedChunkSecondPass;
		}
		return null;
	}

	public void setLocalityPosition(int x, int y, int z) {
		lpx = x;
		lpy = y;
		lpz = z;
	}
	
	public void render(int pass) {
		if (pass == 0) {
			if (renderedChunk != null) {
				renderedChunk.render();
			}
		} else if (pass == 1) {
			if (renderedChunkSecondPass != null) {
				renderedChunkSecondPass.render();
			}
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
		
		if (calculateRecursively) {
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					for (int k = -1; k < 2; k++) {
						Vector3i cpy = position.clone();
						cpy.x += i;
						cpy.z += j;
						cpy.y += k;
						HeightChunk hmc = chunkViewport.getHeightChunkGlobalPos(cpy.x, cpy.z);
						
						if (hmc != null) {
							hmc.putChunk(
										chunkViewport.getWorld().getChunkData(cpy),
										cpy.y * Chunk.SIZE
										);
						}
					}
				}
			}
		}
		
		LinkedList<Vector3i> updateChunks = new LinkedList<Vector3i>();
		HashSet<Vector3i> listed = new HashSet<Vector3i>();
		
		updateChunks.add(this.position);
		listed.add(this.position);
		
		HashMap<Vector3i, ChunkLight> lightCache = new HashMap<Vector3i, ChunkLight>();
		
		while (updateChunks.size() > 0) {
			Chunk chunk;
			Vector3i pos = updateChunks.removeFirst();
			listed.remove(pos);
			if (this.position.equals(pos)) {
				chunk = this;
			} else {
				chunk = chunkViewport.getChunkGlobalPos(pos.x, pos.y, pos.z);
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
			if (xp != null && xp.lighting == null) {
				if (lightCache.containsKey(xp.position)) {
					xp.lighting = lightCache.get(xp.position);
				}
			}
			if (yp != null && yp.lighting == null) {
				if (lightCache.containsKey(yp.position)) {
					yp.lighting = lightCache.get(yp.position);
				}
			}
			if (zp != null && zp.lighting == null) {
				if (lightCache.containsKey(zp.position)) {
					zp.lighting = lightCache.get(zp.position);
				}
			}
			if (xn != null && xn.lighting == null) {
				if (lightCache.containsKey(xn.position)) {
					xn.lighting = lightCache.get(xn.position);
				}
			}
			if (yn != null && yn.lighting == null) {
				if (lightCache.containsKey(yn.position)) {
					yn.lighting = lightCache.get(yn.position);
				}
			}
			if (zn != null && zn.lighting == null) {
				if (lightCache.containsKey(zn.position)) {
					zn.lighting = lightCache.get(zn.position);
				}
			}
			
			if (chunk.lighting == null) {
				if (lightCache.containsKey(chunk.position)) {
					chunk.lighting = lightCache.get(chunk.position);
				} else {
					chunk.lighting = new ChunkLight();
				}
			}
			lightCache.put(chunk.position, chunk.lighting);

			HeightChunk hc = chunkViewport.getHeightChunkGlobalPos(chunk.position.x, chunk.position.z);
			
			ArrayList<Vector3i> chunks = chunk.lighting.calculateLight(c, xp, xn, yp, yn, zp, zn, hc, new Vector3i(chunk.position.x * SIZE, chunk.position.y * SIZE, chunk.position.z * SIZE), chunkViewport, chunk.highPriority, true);
			
			for (Vector3i ucv : chunks) {
				if (
						Math.abs(ucv.x - position.x) > 1 ||
						Math.abs(ucv.y - position.y) > 1 ||
						Math.abs(ucv.z - position.z) > 1
						)
					continue;
				if (listed.contains(ucv))
					continue;
				updateChunks.add(ucv);
				listed.add(ucv);
			}
			if(!calculateRecursively) {
				for (Vector3i vec : chunks) {
					chunkViewport.addLightUpdate(vec);
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
		if (renderedChunkSecondPass != null) {
			renderedChunkSecondPass.free();
			renderedChunkSecondPass = null;
		}
		free = true;
	}
}
