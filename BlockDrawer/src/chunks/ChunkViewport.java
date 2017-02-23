package chunks;

import light.HeightChunk;
import light.HeightChunkViewport;
import world.ChunkData;
import world.World;

import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

import blockdraw.BlockContainer;

public class ChunkViewport {

	public static short NULL_CHUNK_VALUE = BlockContainer.NUM_BLOCK_TYPES - 1;
	
	private Chunk[] chunks;
	private Vector3i center, radialSize;
	private int xSize, ySize, zSize;
	private ChunkQueue chunkQueue; //pseudo priority queue to store chunks to be generated
	private Texture texture;
	private World world;
	
	public World getWorld() { return world; }
	
	private HeightChunkViewport heightMap;
	
	/**
	 * 
	 * @param center center chunk position
	 * @param radialSize not including the center chunk
	 */
	public ChunkViewport(Vector3i center, Vector3i radialSize, World world, Texture tex) {
		this.center = center;
		this.radialSize = radialSize;
		this.xSize = radialSize.x * 2 + 1;
		this.ySize = radialSize.y * 2 + 1;
		this.zSize = radialSize.z * 2 + 1;
		chunks = new Chunk[xSize * ySize * zSize];
		this.chunkQueue = new ChunkQueue();
		this.texture = tex;
		this.world = world;
		heightMap = new HeightChunkViewport(xSize, zSize, true);
	}
	
	public void loadNextUnloadedChunk() {
		Vector3i nextPos = this.getNextUnloadedChunk();

		if (nextPos != null) {
			Chunk chunk = new Chunk(nextPos);
			this.setChunkGlobalPos(chunk, nextPos.x, nextPos.y, nextPos.z);
			chunkQueue.addLowPriority(chunk);
			
			for (int i = -1; i < 2; i+= 2) {
				Chunk cx = this.getChunkGlobalPos(nextPos.x + i, nextPos.y, nextPos.z);
				Chunk cy = this.getChunkGlobalPos(nextPos.x, nextPos.y + i, nextPos.z);
				Chunk cz = this.getChunkGlobalPos(nextPos.x, nextPos.y, nextPos.z + i);

				if (cx != null && cx.countLoadedNeighbors() == 6) {
					chunkQueue.addLowPriority(cx);
				}
				if (cy != null && cy.countLoadedNeighbors() == 6) {
					chunkQueue.addLowPriority(cy);
				}
				if (cz != null && cz.countLoadedNeighbors() == 6) {
					chunkQueue.addLowPriority(cz);
				}
			}
			
		}
	}
	
	public void triangulateNextChunk() {
		Chunk chunk = chunkQueue.pop();
		if (chunk == null) {
			return;
		}
		this.heightMap.getChunk(chunk.lpx, chunk.lpz).putChunk(world.getChunkData(chunk.position), chunk.position.y * Chunk.SIZE);
		chunk.calculateLight();
		
		ChunkDrawBuilder.generateChunkEntity(chunk, world, texture);
	}
	
	private Vector3i getNextUnloadedChunk() {
		Vector3i closest = null;
		double minDist = xSize + ySize + zSize;
		Vector3f mid = new Vector3f((xSize - 1) / 2.0f, (ySize - 1) / 2.0f, (zSize - 1) / 2.0f);
		
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				for (int z = 0; z < zSize; z++) {
					if (this.getChunk(x, y, z) == null) {
						double dx = mid.x - x;
						double dy = mid.y - y;
						double dz = mid.z - z;
						double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
						if (dist < minDist) {
							minDist = dist;
							if (closest == null) {
								closest = new Vector3i();
							}
							closest.x = x;
							closest.y = y;
							closest.z = z;
						}
					}
				}
			}
		}
		if (closest != null) {
			closest.x += center.x - radialSize.x;
			closest.y += center.y - radialSize.y;
			closest.z += center.z - radialSize.z;
		}
		return closest;
	}
	
	public void setChunkGlobalPos(Chunk c, int x, int y, int z) {
		x = x + radialSize.x - center.x;
		y = y + radialSize.y - center.y;
		z = z + radialSize.z - center.z;
		setChunk(c, x, y, z);
	}
	
	public Chunk getChunkGlobalPos(int x, int y, int z) {
		return getChunk(
				x + radialSize.x - center.x,
				y + radialSize.y - center.y,
				z + radialSize.z - center.z);
	}
	
	public boolean setChunk(Chunk c, int x, int y, int z) {
		c.chunkViewport = this;
		if (!checkBounds(x, y, z)) {
			return false;
		}
		c.setLocalityPosition(x, y, z);
		chunks[x * ySize * zSize + y * zSize + z] = c;
		return true;
	}
	
	public Chunk getChunk(int x, int y, int z) {
		if (!checkBounds(x, y, z)) {
			return null;
		}
		return chunks[x * ySize * zSize + y * zSize + z];
	}
	
	/**
	 * Gets value relative to chunk at x y z at the relative block position ox oy oz
	 * Only works 1 chunk away in each directionn
	 */
	public short getValueRelative(int x, int y, int z, int ox, int oy, int oz) {
		if (ox >= Chunk.SIZE) {
			ox -= Chunk.SIZE;
			x++;
		}
		if (oy >= Chunk.SIZE) {
			oy -= Chunk.SIZE;
			y++;
		}
		if (oz >= Chunk.SIZE) {
			oz -= Chunk.SIZE;
			z++;
		}
		if (ox < 0) {
			ox += Chunk.SIZE;
			x--;
		}
		if (oy < 0) {
			oy += Chunk.SIZE;
			y--;
		}
		if (oz < 0) {
			oz += Chunk.SIZE;
			z--;
		}
		
		ChunkData c = world.getChunkData(new Vector3i(x, y, z));
		if (c == null) {
			return NULL_CHUNK_VALUE;
		}
		return c.getValue(ox, oy, oz);
	}
	
	private boolean checkBounds(int x, int y, int z) {
		return x >= 0 && y >= 0 && z >= 0 &&
				x < xSize && y < ySize && z < zSize;
	}
	
	public void setCenter(int x, int y, int z) {
		if (
				x == center.x &&
				y == center.y &&
				z == center.z) {
			return;
		}
		
		int dx = x - center.x;
		int dy = y - center.y;
		int dz = z - center.z;
		
		this.shift(dx, dy, dz);
	}
	
	public void shift(int x, int y, int z) {
		if (x != 0 || z != 0)
			heightMap.shift(x, z);
		
		Chunk[] oldChunks = chunks;
		chunks = new Chunk[xSize * ySize * zSize];
		
		for (Chunk c : oldChunks) {
			if (c != null) {
				if (!this.setChunk(c, c.lpx - x, c.lpy - y, c.lpz - z)) {
					c.freeEntity();
				}
			}
		}
		center.x += x;
		center.y += y;
		center.z += z;
	}
	
	public void render(Vector3f camPos, Vector3f direction) {
		int cx = (int) Math.floor(camPos.x / Chunk.SIZE);
		int cy = (int) Math.floor(camPos.y / Chunk.SIZE);
		int cz = (int) Math.floor(camPos.z / Chunk.SIZE);
		
		this.setCenter(cx, cy, cz);
		
		//TODO cull chunks behind camera
		int ox = center.x - radialSize.x;
		int oy = center.y - radialSize.y;
		int oz = center.z - radialSize.z;
		for (int i = 0; i < chunks.length; i++) {
			Chunk c = chunks[i];
			
			if (c == null || c.getEntity() == null)
				continue;
			
			c.getEntity().setModelMatrix(
					Matrix4f.translate(
							new Vector3f(
									(c.lpx + ox) * Chunk.SIZE,
									(c.lpy + oy) * Chunk.SIZE,
									(c.lpz + oz) * Chunk.SIZE)));
			c.render();
		}
	}

	public HeightChunk getHeightChunk(int lpx, int lpz) {
		return heightMap.getChunk(lpx, lpz);
	}
}
