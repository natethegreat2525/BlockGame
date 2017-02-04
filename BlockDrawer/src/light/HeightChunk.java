package light;

import chunks.Chunk;

public class HeightChunk {
	
	private static final int SIZE = Chunk.SIZE;
	private static final int arrayLength = SIZE * SIZE;

	private static final long[] defaultHeightsMax = new long[arrayLength];
	private static final long[] defaultHeightsMin = new long[arrayLength];
	static {
		for (int i = 0; i < arrayLength; i++) {
			defaultHeightsMax[i] = Long.MAX_VALUE;
			defaultHeightsMin[i] = Long.MIN_VALUE;
		}
	}
		
	private long[] heights;
	
	private long max, min;
	
	public HeightChunk(boolean isMax) {

		if (isMax)
			//Set defaults to minimums to find the max value
			this.heights = defaultHeightsMin.clone();
		else
			//Set defaults to maximums to find the max value
			this.heights = defaultHeightsMax.clone();

		max = Long.MIN_VALUE;
		min = Long.MAX_VALUE;
	}
	
	//Put value if it is a new maximum
	public void putValueMax(int a, int b, long val) {
		int idx = a + b * SIZE;
		
		if (val > heights[idx]) {
			heights[idx] = val;
			if (val > max) {
				max = val;
			}
			if (val < min) {
				min = val;
			}
		}
	}
	
	//Put value if it is a new minimum
	public void putValueMin(int a, int b, long val) {
		int idx = a + b * SIZE;
		
		if (val < heights[idx]) {
			heights[idx] = val;
			if (val > max) {
				max = val;
			}
			if (val < min) {
				min = val;
			}
		}
	}
	
	public long getValue(int a, int b) {
		return heights[a + b * SIZE];
	}
}
