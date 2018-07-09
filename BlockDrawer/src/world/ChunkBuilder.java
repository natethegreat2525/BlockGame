package world;

import com.nshirley.engine3d.math.Vector3i;

import database.DBConnection;

public interface ChunkBuilder {
	public ChunkData buildChunk(Vector3i pos);
}
