package chunks;

import world.ChunkData;
import world.World;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

import drawentity.ChunkEntity;
import blockdraw.Block;
import blockdraw.BlockContainer;

public class ChunkDrawBuilder {

	public static void generateChunkEntity(Chunk c, World w, Texture tex) {
		VertexArrayBuilder vab = new VertexArrayBuilder(
				new int[] {0, 1, 2}, //positions ---> pos, texcoord, light
				new int[] {3, 2, 1} //vector sizes
				);
		
		ChunkArea chunkArea = new ChunkArea(w, c.position);

		Vector3f pos = new Vector3f();
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					
					Block block = BlockContainer.getBlockType(chunkArea.cdCenter.getValue(x, y, z));
					if (!block.isDrawn())
						continue;
					
					pos.x = x;
					pos.y = y;
					pos.z = z;

					//TODO calculate light
					block.add(vab, pos, getFaces(c, x, y, z, chunkArea), null);
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

	private static boolean[] getFaces(Chunk c, int x, int y, int z, ChunkArea ca) {
		boolean[] faces = new boolean[6];

		//TODO speed up massively
		if (BlockContainer.getBlockType(
				ca.getValueRelative(x, y + 1 ,z)
				).isTransparent()) {
			faces[0] = true;
		}
		if (BlockContainer.getBlockType(
				ca.getValueRelative(x, y - 1 ,z)
				).isTransparent()) {
			faces[1] = true;
		}
		
		if (BlockContainer.getBlockType(
				ca.getValueRelative(x + 1, y ,z)
				).isTransparent()) {
			faces[2] = true;
		}
		if (BlockContainer.getBlockType(
				ca.getValueRelative(x - 1, y ,z)
				).isTransparent()) {
			faces[3] = true;
		}
		
		if (BlockContainer.getBlockType(
				ca.getValueRelative(x, y ,z + 1)
				).isTransparent()) {
			faces[4] = true;
		}
		if (BlockContainer.getBlockType(
				ca.getValueRelative(x, y ,z - 1)
				).isTransparent()) {
			faces[5] = true;
		}
		return faces;
	}
}

class ChunkArea {
	public ChunkData cdUp, cdDown, cdFront, cdBack, cdRight, cdLeft, cdCenter;
	public ChunkArea(World w, Vector3i position) {
		cdCenter = w.getChunkData(position);
		cdUp = w.getChunkData(Vector3i.add(position, new Vector3i(0, 1, 0)));
		cdDown = w.getChunkData(Vector3i.add(position, new Vector3i(0, -1, 0)));
		cdFront = w.getChunkData(Vector3i.add(position, new Vector3i(0, 0, 1)));
		cdBack = w.getChunkData(Vector3i.add(position, new Vector3i(0, 0, -1)));
		cdRight = w.getChunkData(Vector3i.add(position, new Vector3i(1, 0, 0)));
		cdLeft = w.getChunkData(Vector3i.add(position, new Vector3i(-1, 0, 0)));
	}
	
	public short getValueRelative(int x, int y, int z) {
		if (x < 0) {
			x += Chunk.SIZE;
			return cdLeft.getValue(x, y, z);
		}
		if (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			return cdRight.getValue(x, y, z);
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			return cdDown.getValue(x, y, z);
		}
		if (y >= Chunk.SIZE) {
			y -= Chunk.SIZE;
			return cdUp.getValue(x, y, z);
		}
		
		if (z < 0) {
			z += Chunk.SIZE;
			return cdBack.getValue(x, y, z);
		}
		if (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			return cdFront.getValue(x, y, z);
		}
		
		return cdCenter.getValue(x, y, z);
	}
}
