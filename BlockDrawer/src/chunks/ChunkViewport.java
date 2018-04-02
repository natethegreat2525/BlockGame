package chunks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

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
	
	//potential memory leak
	public synchronized void reset() {
		for (int i = 0; i < chunks.length; i++) {
			Chunk c = chunks[i];
			if (c != null)
				c.freeEntity();
		}
		chunks = new Chunk[xSize * ySize * zSize];
		chunkQueue = new ChunkQueue();
		heightMap = new HeightChunkViewport(xSize, zSize, true);
	}
	
	public int getChunkQueueSize() {
		return chunkQueue.size();
	}
	
	public void loadNextUnloadedChunk() {
		List<Vector3i> ucs = world.flushUpdateChunks();
		for (Vector3i v : ucs) {
			Chunk c = this.getChunkGlobalPos(v.x, v.y, v.z);
			if (c != null) {
				ChunkLight li = c.lighting;
				c = new Chunk(v);
				//important - migrate lighting to new chunks so light can be updated
				c.lighting = li;
				c.chunkViewport = this;
				c.highPriority = true;

				chunkQueue.addHighPriority(c);
			}
		}
		Vector3i nextPos = null;
		synchronized (this) {
			nextPos = this.getNextUnloadedChunk();
			if (nextPos != null) {
				this.setChunkGlobalPos(new Chunk(nextPos), nextPos.x, nextPos.y, nextPos.z);
			}
		}
		
		if (nextPos != null) {
			//add dummy chunk in place so next unloaded chunk isn't same
			Chunk chunk = new Chunk(nextPos);
			world.getChunkData(chunk.position);
			chunk.chunkViewport = this;
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					for (int k = -1; k < 2; k++) {
						HeightChunk hmc = this.heightMap.getChunk(
								chunk.position.x + i + radialSize.x - center.x,
								chunk.position.z + j + radialSize.z - center.z
								);
						
						if (hmc != null) {
							Vector3i cpy = chunk.position.clone();
							cpy.x += i;
							cpy.z += j;
							cpy.y += k;
							hmc.putChunk(
										world.getChunkData(cpy),
										cpy.y * Chunk.SIZE
										);
						}
					}
				}
			}
			
			chunk.calculateLight(true);

			chunkQueue.addLowPriority(chunk);

//			for (int i = -1; i < 2; i++) {
//				for (int j = -1; j < 2; j++) {
//					for (int k = -1; k < 2; k++) {
//						if (i == 0 && j == 0 && k == 0) {
//							continue;
//						}
//						Chunk cx = this.getChunkGlobalPos(nextPos.x + i, nextPos.y + j, nextPos.z + k);
//						if (cx != null && cx.countLoadedAndDiagNeighbors() == 26) {
//							cx = new Chunk(cx.position);
//							cx.chunkViewport = this;
//							cx.calculateLight(false);
//							chunkQueue.addLowPriority(cx);
//						}
//					}
//				}
//			}

			
		}
	}
	
	public boolean triangulateNextChunk() {
		Chunk chunk = chunkQueue.pop();
		while (chunk == null && chunkQueue.size() > 0) {
			chunk = chunkQueue.pop();
		}
		if (chunk == null) {
			return false;
		}
		HeightChunk hmc = this.heightMap.getChunk(
				chunk.position.x + radialSize.x - center.x,
				chunk.position.z + radialSize.z - center.z
				);
		
		if (hmc != null) {
			hmc.putChunk(
						world.getChunkData(chunk.position),
						chunk.position.y * Chunk.SIZE
						);
		} else {
			return false;
		}
		//chunk.calculateLight(false);
		ChunkDrawBuilder.generateChunkEntity(chunk, world, texture);
		this.setChunkGlobalPos(chunk, chunk.position.x, chunk.position.y, chunk.position.z);
		return true;
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
	
	public synchronized void setChunkGlobalPos(Chunk c, int x, int y, int z) {
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
		if (c == null) {
			return false;
		}
		c.chunkViewport = this;
		if (!checkBounds(x, y, z)) {
			c.freeEntity();
			return false;
		}
		c.setLocalityPosition(x, y, z);
		int idx = x * ySize * zSize + y * zSize + z;
		if (chunks[idx] != null && chunks[idx] != c) {
			chunks[idx].freeEntity();
			chunks[idx] = c;
			return true;
		}
		chunks[idx] = c;
		return false;
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
	
	public synchronized void shift(int x, int y, int z) {
		if (x != 0 || z != 0)
			heightMap.shift(x, z);
		
		Chunk[] oldChunks = chunks;
		chunks = new Chunk[xSize * ySize * zSize];
		
		for (Chunk c : oldChunks) {
			if (c != null) {
				this.setChunk(c, c.lpx - x, c.lpy - y, c.lpz - z);
			}
		}
		center.x += x;
		center.y += y;
		center.z += z;
	}
	
	class ChunkDist {
		public Chunk chunk;
		public double dist;
		public ChunkDist(Chunk c, double d) {
			chunk = c;
			dist = d;
		}
	}
	public void render(Vector3f camPos, Vector3f direction) {
		int cx = (int) Math.floor(camPos.x / Chunk.SIZE);
		int cy = (int) Math.floor(camPos.y / Chunk.SIZE);
		int cz = (int) Math.floor(camPos.z / Chunk.SIZE);
		this.setCenter(cx, cy, cz);
		
		ArrayList<ChunkDist> distStack = new ArrayList<ChunkDist>();
		for (int i = 0; i < chunks.length; i++) {
			Chunk c = chunks[i];
			
			if (c == null || c.getEntity() == null)
				continue;

			Vector3i pos = c.position.mult(Chunk.SIZE);
			Vector3f diff = new Vector3f(
					pos.x + Chunk.SIZE/2 - (camPos.x - direction.x * Chunk.SIZE * 3),
					pos.y + Chunk.SIZE/2 - (camPos.y - direction.y * Chunk.SIZE * 3),
					pos.z + Chunk.SIZE/2 - (camPos.z - direction.z * Chunk.SIZE * 3));
			Vector3f midDiff = new Vector3f(
					pos.x + Chunk.SIZE/2 - camPos.x,
					pos.y + Chunk.SIZE/2 - camPos.y,
					pos.z + Chunk.SIZE/2 - camPos.z);
			double angle = Math.acos(diff.normalize().dot(direction));
			if (Math.abs(angle) < (Math.PI / 4) + .1) {
				distStack.add(new ChunkDist(c, midDiff.dot(midDiff)));
			}
		}
		Collections.sort(distStack, (o1, o2) -> Double.compare(o2.dist, o1.dist));
		for (ChunkDist c : distStack) {
			c.chunk.render();
		}
	}
	
	public HeightChunk getHeightChunkGlobalPos(int x, int z) {
		return heightMap.getChunk(x + radialSize.x - center.x, z + radialSize.z - center.z);
	}

	public HeightChunk getHeightChunk(int lpx, int lpz) {
		return heightMap.getChunk(lpx, lpz);
	}
	
	public int getHeight_needs_testing(int x, int z) {
		int cx = (int) Math.floor(x * 1.0 / Chunk.SIZE);
		int cz = (int) Math.floor(z * 1.0 / Chunk.SIZE);
		int lx = x - (cx * Chunk.SIZE);
		int lz = z - (cz * Chunk.SIZE);
		int lpx = cx + radialSize.x - center.x;
		int lpz = cz + radialSize.z - center.z;
		HeightChunk h = getHeightChunk(lpx, lpz);
		return (int) h.getValue(lx, lz);
	}
	
	public int getLightValue(Vector3i pos) {
		int cx = World.intToChunk(pos.x);
		int cy = World.intToChunk(pos.y);
		int cz = World.intToChunk(pos.z);
		int lx = pos.x - (cx * Chunk.SIZE);
		int ly = pos.y - (cy * Chunk.SIZE);
		int lz = pos.z - (cz * Chunk.SIZE);
		Chunk c = this.getChunkGlobalPos(cx, cy, cz);
		if (c == null || c.lighting == null) {
			return 0;
		}
		return c.lighting.getLight(lx, ly, lz);
	}

	public void addUpdateChunk(Vector3i v, boolean highPriority) {
		Chunk c = this.getChunkGlobalPos(v.x, v.y, v.z);
		if (c != null) {
			if (highPriority) {
				chunkQueue.addHighPriority(c);
			} else {
				chunkQueue.addLowPriority(c);
			}
		}
	}

	public int getNumToTriangulate() {
		return chunkQueue.size();
	}

	public void calcLocalPos(Chunk c) {
		c.lpx = c.position.x + radialSize.x - center.x;
		c.lpy = c.position.y + radialSize.y - center.y;
		c.lpz = c.position.z + radialSize.z - center.z;
	}
}
