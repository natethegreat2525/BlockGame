package blockdraw;

/**
 * Contains all the types of blocks that will exist in a world
 * @author Nathan
 *
 */
public class BlockContainer {

	public static final int NUM_BLOCK_TYPES = 1024;
	public static Block[] blockTypes = new Block[NUM_BLOCK_TYPES];
	
	public static Block getBlockType(int idx) {
		if (idx < 0 || idx >= NUM_BLOCK_TYPES)
			return null;
		
		return blockTypes[idx];
	}
	
	static {
		//Set first block type to be air
		blockTypes[0] = new AirBlock();
		//set up next 10 block types to be uniform
		for (int i = 0; i < 10; i++) {
			blockTypes[i + 1] = new UniformBlock(i, 4, 4);
		}
		
		blockTypes[NUM_BLOCK_TYPES - 1] = new NullBlock();
	}
}
