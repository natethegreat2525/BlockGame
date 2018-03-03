package chunks;

import world.ChunkData;
import world.World;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

import drawentity.ChunkEntity;
import blockdraw.Block;
import blockdraw.BlockContainer;

public class ChunkDrawBuilder {

	public static void generateChunkEntity(Chunk c, World w, Texture tex) {
		VertexArrayBuilder[] vabs = new VertexArrayBuilder[5];
		for (int i = 0; i < vabs.length; i++) {
			vabs[i] = new VertexArrayBuilder(
					new int[] {0, 1, 2, 3}, //positions ---> pos, texcoord, light, transparency
					new int[] {3, 2, 1, 4} //vector sizes
					);
		}
		
		ChunkArea chunkArea = new ChunkArea(w, c.chunkViewport, c.position);

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
					
					double[] light = new double[27];
					for (int xt = 0; xt < 3; xt++) {
						for (int yt = 0; yt < 3; yt++) {
							for (int zt = 0; zt < 3; zt++) {
								light[xt + yt*3 + zt*9] = chunkArea.getLightRelative(x + xt - 1, y + yt - 1, z + zt - 1);
							}
						}
					}

					block.add(vabs, pos, getFaces(c, x, y, z, chunkArea), light);
				}
			}
		}
		for (int i = 1; i < vabs.length; i++) {
			vabs[0].concatenate(vabs[i]);
		}
		ChunkEntity e = new ChunkEntity(vabs[0].build(), tex);
		//TODO: set model matrix only once here
		e.setModelMatrix(Matrix4f.translate(new Vector3f(c.position.x * Chunk.SIZE, c.position.y * Chunk.SIZE, c.position.z * Chunk.SIZE)));
		c.setEntity(e);
	}

	private static boolean[] getFaces(Chunk c, int x, int y, int z, ChunkArea ca) {
		boolean[] faces = new boolean[6];

		//TODO speed up massively
		Block mid = BlockContainer.getBlockType(ca.getValueRelative(x, y, z));
		Block b = BlockContainer.getBlockType(ca.getValueRelative(x, y + 1 ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[0] = true;
		}
		
		b = BlockContainer.getBlockType(ca.getValueRelative(x, y - 1 ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[1] = true;
		}
		
		b = BlockContainer.getBlockType(ca.getValueRelative(x + 1, y ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[2] = true;
		}
		
		b = BlockContainer.getBlockType(ca.getValueRelative(x - 1, y ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[3] = true;
		}
		
		b = BlockContainer.getBlockType(ca.getValueRelative(x, y ,z + 1));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[4] = true;
		}
		
		b = BlockContainer.getBlockType(ca.getValueRelative(x, y ,z - 1));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[5] = true;
		}
		return faces;
	}
}

class ChunkArea {
	
	public ChunkData cdUp, cdDown, cdFront, cdBack, cdRight, cdLeft, cdCenter;
	
	public ChunkLight[] lightChunks;
	
	public ChunkArea(World w, ChunkViewport cv, Vector3i position) {
		cdCenter = w.getChunkData(position);
		cdUp = w.getChunkData(Vector3i.add(position, new Vector3i(0, 1, 0)));
		cdDown = w.getChunkData(Vector3i.add(position, new Vector3i(0, -1, 0)));
		cdFront = w.getChunkData(Vector3i.add(position, new Vector3i(0, 0, 1)));
		cdBack = w.getChunkData(Vector3i.add(position, new Vector3i(0, 0, -1)));
		cdRight = w.getChunkData(Vector3i.add(position, new Vector3i(1, 0, 0)));
		cdLeft = w.getChunkData(Vector3i.add(position, new Vector3i(-1, 0, 0)));
		
		
		lightChunks = new ChunkLight[27];
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 3; z++) {
					Chunk c = cv.getChunkGlobalPos(position.x + x - 1, position.y + y - 1, position.z + z - 1);
					if (c != null) {
						lightChunks[x + y * 3 + z * 9] = c.lighting;
					}
				}
			}
		}
		
	}
	
	public short getLightRelative(int x, int y, int z) {
		int rx = 1;
		int ry = 1;
		int rz = 1;
		
		if (x < 0) {
			x += Chunk.SIZE;
			rx--;
		}
		if (x >= Chunk.SIZE) {
			x -= Chunk.SIZE;
			rx++;
		}
		
		if (y < 0) {
			y += Chunk.SIZE;
			ry--;
		}
		if (y >= Chunk.SIZE) {
			y -= Chunk.SIZE;
			ry++;
		}
		
		if (z < 0) {
			z += Chunk.SIZE;
			rz--;
		}
		
		if (z >= Chunk.SIZE) {
			z -= Chunk.SIZE;
			rz++;
		}
		
		ChunkLight cl = lightChunks[rx + ry * 3 + rz * 9];
		if (cl == null) {
			return 0;
		}
		return cl.getLight(x, y, z);
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
