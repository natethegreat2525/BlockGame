package world;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import blockdraw.Block;
import blockdraw.BlockContainer;
import chunks.Chunk;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

public class World {
	
	private static final int CHUNK_CACHE_CAP = 7000;
	
	ChunkBuilder builder;
	ChunkCache chunkCache;
	LinkedList<Vector3i> updateChunks;
	
	public World(ChunkBuilder builder) {
		this.builder = builder;
		this.chunkCache = new ChunkCache(CHUNK_CACHE_CAP);
		this.updateChunks = new LinkedList<Vector3i>();
	}
	
	
	public synchronized void flushCache() {
		chunkCache.clear();
	}
	
	public synchronized List<Vector3i> flushUpdateChunks() {
		List<Vector3i> updateChunks = this.updateChunks;
		this.updateChunks = new LinkedList<Vector3i>();
		return updateChunks;
	}
	
	public short getBlockValue(int x, int y, int z) {
		Vector3i cPos = getChunkPos(x, y, z);
		ChunkData cd = this.getChunkData(cPos);
		int lx = x - cPos.x * Chunk.SIZE;
		int ly = y - cPos.y * Chunk.SIZE;
		int lz = z - cPos.z * Chunk.SIZE;
		return cd.getValue(lx, ly, lz);
	}
	
	public synchronized void bulkUpdate(BulkBlockUpdate bbu) {
		HashSet<Vector3i> allUpdateChunks = new HashSet<Vector3i>();
		for (SingleBlockUpdate sbu : bbu.list) {
			int x = sbu.pos.x;
			int y = sbu.pos.y;
			int z = sbu.pos.z;
			short value = sbu.val;
			Vector3i cPos = getChunkPos(x, y, z);
			ChunkData cd = this.getChunkData(cPos);
			int lx = x - cPos.x * Chunk.SIZE;
			int ly = y - cPos.y * Chunk.SIZE;
			int lz = z - cPos.z * Chunk.SIZE;
			cd.setValue(lx, ly, lz, value);
	
			allUpdateChunks.add(cPos);
			if (lx == 0) {
				allUpdateChunks.add(new Vector3i(cPos.x - 1, cPos.y, cPos.z));
			} else if (lx == Chunk.SIZE - 1) {
				allUpdateChunks.add(new Vector3i(cPos.x + 1, cPos.y, cPos.z));
			}
			
			if (ly == 0) {
				allUpdateChunks.add(new Vector3i(cPos.x, cPos.y - 1, cPos.z));
			} else if (ly == Chunk.SIZE - 1) {
				allUpdateChunks.add(new Vector3i(cPos.x, cPos.y + 1, cPos.z));
			}
			
			if (lz == 0) {
				allUpdateChunks.add(new Vector3i(cPos.x, cPos.y, cPos.z - 1));
			} else if (lz == Chunk.SIZE - 1) {
				allUpdateChunks.add(new Vector3i(cPos.x, cPos.y, cPos.z + 1));
			}
		}
		for (Vector3i pos : allUpdateChunks) {
			updateChunks.add(pos);
		}
	}
		
	public synchronized void setBlockValue(int x, int y, int z, short value) {
		//TODO save chunk value
		Vector3i cPos = getChunkPos(x, y, z);
		ChunkData cd = this.getChunkData(cPos);
		int lx = x - cPos.x * Chunk.SIZE;
		int ly = y - cPos.y * Chunk.SIZE;
		int lz = z - cPos.z * Chunk.SIZE;
		cd.setValue(lx, ly, lz, value);

		updateChunks.add(cPos);
		if (lx == 0) {
			updateChunks.add(new Vector3i(cPos.x - 1, cPos.y, cPos.z));
		} else if (lx == Chunk.SIZE - 1) {
			updateChunks.add(new Vector3i(cPos.x + 1, cPos.y, cPos.z));
		}
		
		if (ly == 0) {
			updateChunks.add(new Vector3i(cPos.x, cPos.y - 1, cPos.z));
		} else if (ly == Chunk.SIZE - 1) {
			updateChunks.add(new Vector3i(cPos.x, cPos.y + 1, cPos.z));
		}
		
		if (lz == 0) {
			updateChunks.add(new Vector3i(cPos.x, cPos.y, cPos.z - 1));
		} else if (lz == Chunk.SIZE - 1) {
			updateChunks.add(new Vector3i(cPos.x, cPos.y, cPos.z + 1));
		}
	}
	
	public void setBlockValue(Vector3i pos, short value) {
		setBlockValue(pos.x, pos.y, pos.z, value);
	}
	
//	public static int hits, misses, size;
	public ChunkData getChunkData(Vector3i pos) {
		ChunkData data = chunkCache.get(pos);
		
		//size = chunkCache.size();
		if (data != null) {
			//hits++;
			return data;
		}
		
		//misses++;
		data = builder.buildChunk(pos);
		chunkCache.put(pos, data);

		return chunkCache.get(pos);
	}
	
	public Raycast raycast(Vector3f start, Vector3f dir, double maxDistance) {
		return raycast(start, dir, maxDistance, new int[] {0});
	}

	public Raycast raycast(Vector3f start, Vector3f dir, double maxDistance, int[] groups) {
		int blockX = (int) Math.floor(start.x);
		int blockY = (int) Math.floor(start.y);
		int blockZ = (int) Math.floor(start.z);
		
		int oldX = blockX;
		int oldY = blockY;
		int oldZ = blockZ;
		
		Vector3f pos = start.clone();
		
		boolean right = dir.x > 0;
		boolean up = dir.y > 0;
		boolean forward = dir.z > 0;
		
		int iterations = 0;
		
		ChunkData currentChunk = this.getChunkData(this.getChunkPos(blockX, blockY, blockZ));

		while (true) {
			//increment real pos
			int pX = right ? blockX + 1 : blockX;
			int pY = up ? blockY + 1 : blockY;
			int pZ = forward ? blockZ + 1 : blockZ;
			
			double pxt = (pX - pos.x) / dir.x;
			double pyt = (pY - pos.y) / dir.y;
			double pzt = (pZ - pos.z) / dir.z;
			if (!Double.isFinite(pxt)) {
				pxt = Double.MAX_VALUE;
			}
			if (!Double.isFinite(pyt)) {
				pyt = Double.MAX_VALUE;
			}
			if (!Double.isFinite(pzt)) {
				pzt = Double.MAX_VALUE;
			}
			boolean newChunk = false;
			
			if (pxt < pyt && pxt < pzt) {
				//x lowest
				pos.x += pxt * dir.x;
				pos.y += pxt * dir.y;
				pos.z += pxt * dir.z;
				blockX += right ? 1 : -1;
				if (intToChunk(blockX) != intToChunk(oldX))
					newChunk = true;
			} else if (pyt < pzt) {
				//y lowest
				pos.x += pyt * dir.x;
				pos.y += pyt * dir.y;
				pos.z += pyt * dir.z;
				blockY += up ? 1 : -1;
				if (intToChunk(blockY) != intToChunk(oldY))
					newChunk = true;
			} else {
				//z lowest
				pos.x += pzt * dir.x;
				pos.y += pzt * dir.y;
				pos.z += pzt * dir.z;
				blockZ += forward ? 1 : -1;
				if (intToChunk(blockZ) != intToChunk(oldZ))
					newChunk = true;
			}

			//e.setModelMatrix(Matrix4f.translate(pos).multiply(Matrix4f.scale(new Vector3f(.1f, .1f, .1f))));
			//e.setModelMatrix(Matrix4f.translate(new Vector3f(blockX, blockY, blockZ)).multiply(Matrix4f.scale(new Vector3f(.1f, .1f, .1f))));
			if (iterations + 1 > maxDistance) {
				double dx = pos.x - start.x;
				double dy = pos.y - start.y;
				double dz = pos.z - start.z;
				if (dx*dx + dy*dy + dz*dz > maxDistance * maxDistance) {
					return null;
				}
			}
			
			//check new chunk
			if (newChunk) {
				currentChunk = this.getChunkData(this.getChunkPos(blockX, blockY, blockZ));
			}
			if (currentChunk == null) {
				return null;
			}
			
			//calculate local coords
			int lx = posMod(blockX, Chunk.SIZE);
			int ly = posMod(blockY, Chunk.SIZE);
			int lz = posMod(blockZ, Chunk.SIZE);

			//check value of block
			short type = currentChunk.getValue(lx, ly, lz);
			if (type != 0) {
				Block block = BlockContainer.getBlockType(type);
				int group = block.getPickGroup();
				boolean hit = false;
				for (int i = 0; i < groups.length; i++) {
					if (group == groups[i]) {
						hit = true;
						break;
					}
				}
				if (hit) {
					if (block.specialBoundingBox()) {
						Vector3f[] norms = block.collide(start, dir);
						if (norms != null) {
							Raycast r = new Raycast(
									norms[0],
									norms[1],
									new Vector3i(blockX, blockY, blockZ)
								);
							if (Float.isFinite(r.position.x) && Float.isFinite(r.position.y) && Float.isFinite(r.position.y))
								return r;
							return null;
						}
					} else {
						Raycast r = new Raycast(
								pos,
								new Vector3f(
										oldX - blockX,
										oldY - blockY,
										oldZ - blockZ),
								new Vector3i(blockX, blockY, blockZ)
							);
						if (Float.isFinite(r.position.x) && Float.isFinite(r.position.y) && Float.isFinite(r.position.y))
							return r;
						System.out.println("null");
						return null;
					}
				}
			}
			
			oldX = blockX;
			oldY = blockY;
			oldZ = blockZ;
			
			iterations++;
		}
	}
	
	public int posMod(int val, int mod) {
		int ret = val % mod;
		if (ret < 0) {
			ret = (mod - ((-1 - ret) % mod)) - 1;
		}
		return ret;
	}
	
	/**
	 * Returns the position of a chunk that a given block resides in
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vector3i getChunkPos(int x, int y, int z) {
		return new Vector3i(
				intToChunk(x),
				intToChunk(y),
				intToChunk(z));
	}
	
	public static int intToChunk(int x) {
		if (x >= 0) {
			return x / Chunk.SIZE;
		}
		return -(((Chunk.SIZE - 1) - x) / Chunk.SIZE);
	}

	public boolean hasUpdates() {
		return updateChunks.size() > 0;
	}

	public short getBlockValue(Vector3i b) {
		return this.getBlockValue(b.x, b.y, b.z);
	}
}
