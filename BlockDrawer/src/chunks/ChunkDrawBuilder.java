package chunks;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.math.Vector3f;

import drawentity.ChunkEntity;
import blockdraw.Block;
import blockdraw.BlockContainer;

public class ChunkDrawBuilder {

	public static void generateChunkEntity(Chunk c, Texture tex) {
		VertexArrayBuilder vab = new VertexArrayBuilder(
				new int[] {0, 1, 2}, //positions ---> pos, texcoord, light
				new int[] {3, 2, 1} //vector sizes
				);
		
		Vector3f pos = new Vector3f();
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					
					Block block = BlockContainer.getBlockType(c.getValue(x, y, z));
					if (!block.isDrawn())
						continue;
					
					pos.x = x;
					pos.y = y;
					pos.z = z;

					//TODO calculate light
					block.add(vab, pos, getFaces(c, x, y, z), null);
				}
			}
		}
				
		ChunkEntity e = new ChunkEntity(vab.build(), tex);
		//TODO: set model matrix only once here
		//e.setModelMatrix(Matrix4f.translate(new Vector3f(c.lpx * Chunk.SIZE, c.lpy * Chunk.SIZE, c.lpz * Chunk.SIZE)));
		Entity oldEnt = c.getEntity();
		c.setEntity(null);
		if (oldEnt != null) {
			c.freeEntity();
		}
		c.setEntity(e);
	}

	private static boolean[] getFaces(Chunk c, int x, int y, int z) {
		boolean[] faces = new boolean[6];

		if (BlockContainer.getBlockType(
				c.chunkViewport.getValueRelative(
						c.lpx, c.lpy, c.lpz, x, y + 1 ,z
						)
				).isTransparent()) {
			faces[0] = true;
		}
		if (BlockContainer.getBlockType(
				c.chunkViewport.getValueRelative(
						c.lpx, c.lpy, c.lpz, x, y - 1 ,z
						)
				).isTransparent()) {
			faces[1] = true;
		}
		
		if (BlockContainer.getBlockType(
				c.chunkViewport.getValueRelative(
						c.lpx, c.lpy, c.lpz, x + 1, y ,z
						)
				).isTransparent()) {
			faces[2] = true;
		}
		if (BlockContainer.getBlockType(
				c.chunkViewport.getValueRelative(
						c.lpx, c.lpy, c.lpz, x - 1, y ,z
						)
				).isTransparent()) {
			faces[3] = true;
		}
		
		if (BlockContainer.getBlockType(
				c.chunkViewport.getValueRelative(
						c.lpx, c.lpy, c.lpz, x, y ,z + 1
						)
				).isTransparent()) {
			faces[4] = true;
		}
		if (BlockContainer.getBlockType(
				c.chunkViewport.getValueRelative(
						c.lpx, c.lpy, c.lpz, x, y ,z - 1
						)
				).isTransparent()) {
			faces[5] = true;
		}
		return faces;
	}
}
