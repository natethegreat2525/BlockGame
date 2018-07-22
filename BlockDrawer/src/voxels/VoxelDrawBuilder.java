package voxels;

import world.ChunkData;
import world.World;

import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.graphics.VertexArrayBuilder;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

import drawentity.ChunkEntity;
import blockdraw.Block;
import blockdraw.BlockContainer;

public class VoxelDrawBuilder {

	public static ChunkEntity generateChunkEntity(VoxelData vd, Texture tex) {
		
		VertexArrayBuilder[] vabs = new VertexArrayBuilder[6];
		for (int i = 0; i < vabs.length; i++) {
			vabs[i] = new VertexArrayBuilder(
					new int[] {0, 1, 2, 3}, //positions ---> pos, texcoord, light, transparency
					new int[] {4, 2, 1, 4} //vector sizes
					);
		}
		
		Vector3f pos = new Vector3f();
		for (int x = 0; x < vd.size.x; x++) {
			for (int y = 0; y < vd.size.y; y++) {
				for (int z = 0; z < vd.size.z; z++) {
					
					Block block = BlockContainer.getBlockType(vd.getValue(x, y, z));
					if (!block.isDrawn())
						continue;
					
					pos.x = x;
					pos.y = y;
					pos.z = z;
					
					double[] light = new double[27];
					for (int xt = 0; xt < 3; xt++) {
						for (int yt = 0; yt < 3; yt++) {
							for (int zt = 0; zt < 3; zt++) {
								light[xt + yt*3 + zt*9] = (vd.getValueSafe(x + xt - 1, y + yt - 1, z + zt - 1)) == 0 ? 15 : 0;
							}
						}
					}

					block.add(vabs, pos, getFaces(vd, x, y, z), light);
				}
			}
		}
		for (int i = 1; i < vabs.length; i++) {
			vabs[0].concatenate(vabs[i]);
		}
		ChunkEntity e = new ChunkEntity(vabs[0].build(), tex);
		return e;
	}

	private static boolean[] getFaces(VoxelData vd, int x, int y, int z) {
		boolean[] faces = new boolean[6];

		//TODO speed up massively
		Block mid = BlockContainer.getBlockType(vd.getValueSafe(x, y, z));
		Block b = BlockContainer.getBlockType(vd.getValueSafe(x, y + 1 ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[0] = true;
		}
		
		b = BlockContainer.getBlockType(vd.getValueSafe(x, y - 1 ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[1] = true;
		}
		
		b = BlockContainer.getBlockType(vd.getValueSafe(x + 1, y ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[2] = true;
		}
		
		b = BlockContainer.getBlockType(vd.getValueSafe(x - 1, y ,z));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[3] = true;
		}
		
		b = BlockContainer.getBlockType(vd.getValueSafe(x, y ,z + 1));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[4] = true;
		}
		
		b = BlockContainer.getBlockType(vd.getValueSafe(x, y ,z - 1));
		if (b.isTransparent() && !b.equals(mid)) {
			faces[5] = true;
		}
		return faces;
	}
}