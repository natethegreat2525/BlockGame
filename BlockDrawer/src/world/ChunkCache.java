package world;

import java.util.Map.Entry;
import java.util.LinkedHashMap;

import com.nshirley.engine3d.math.Vector3i;

public class ChunkCache extends LinkedHashMap<Vector3i, ChunkData> {	

	private static final long serialVersionUID = 5154057230814640677L;
	
	private int cacheSize;
	
	public ChunkCache(int cacheSize) {
		super(cacheSize, 0.75f, true);
		
		this.cacheSize = cacheSize;
	}
	
	@Override
	protected boolean removeEldestEntry(Entry<Vector3i, ChunkData> obj) {
		return this.size() > cacheSize;
	}
	
}
