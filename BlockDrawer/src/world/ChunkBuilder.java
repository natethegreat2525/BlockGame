package world;

import com.nshirley.engine3d.math.Vector3i;

public interface ChunkBuilder {
	public ChunkData buildChunk(Vector3i pos);
}
