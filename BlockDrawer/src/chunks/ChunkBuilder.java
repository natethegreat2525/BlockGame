package chunks;

import com.nshirley.engine3d.math.Vector3i;

public interface ChunkBuilder {
	public Chunk buildChunk(Vector3i pos, ChunkViewport cv);
}
