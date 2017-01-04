package world;

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
//	public static int mx, my, mz, Mx, My, Mz;
	public ChunkData getChunkData(Vector3i pos) {
//		mx = Math.min(mx, pos.x);
//		my = Math.min(my, pos.y);
//		mz = Math.min(mz, pos.z);
//		Mx = Math.max(Mx, pos.x);
//		My = Math.max(My, pos.y);
//		Mz = Math.max(Mz, pos.z);
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
	
	public Raycast raycast(Vector3f start, Vector3f dir) {
		return new Raycast();
	}
}
