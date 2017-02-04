package world;

import chunks.Chunk;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

public class World {
	
	private static final int CHUNK_CACHE_CAP = 7000;
	
	ChunkBuilder builder;
	ChunkCache chunkCache;
	
	public World(ChunkBuilder builder) {
		this.builder = builder;
		this.chunkCache = new ChunkCache(CHUNK_CACHE_CAP);
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
		chunkCache.put(pos, builder.buildChunk(pos));
		return data;
	}
	
	public Raycast raycast(Vector3f start, Vector3f dir, double maxDistance, Entity e) {
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
			//e.render();
			//if iterations + 1 > maxdistance check maxdistance
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
			if (currentChunk.getValue(lx, ly, lz) != 0) {
				return new Raycast(
						pos,
						new Vector3f(
								oldX - blockX,
								oldY - blockY,
								oldZ - blockZ),
						new Vector3i(blockX, blockY, blockZ)
					);
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
	
	public int intToChunk(int x) {
		if (x >= 0) {
			return x / Chunk.SIZE;
		}
		return -((15 - x) / Chunk.SIZE);
	}
}
