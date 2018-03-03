package light;

public class HeightChunkViewport {

	public int uSize, vSize;
	public boolean isMax;
	
	public HeightChunk[] heightChunks;
	
	public HeightChunkViewport(int uSize, int vSize, boolean isMax) {
		this.isMax = isMax;
		this.uSize = uSize;
		this.vSize = vSize;
		
		heightChunks = new HeightChunk[uSize * vSize];
		for (int i = 0; i < heightChunks.length; i++) {
			heightChunks[i] = new HeightChunk(isMax);
		}
	}
	
	public HeightChunk getChunk(int u, int v) {
		if (u < 0 || v < 0 || u >= uSize || v >= vSize) {
			return null;
		}
		
		return heightChunks[u + v * uSize];
	}
	
	public void shift(int uOffset, int vOffset) {
		HeightChunk[] newHeightChunks = new HeightChunk[uSize * vSize];
		for (int u = 0; u < uSize; u++) {
			for (int v = 0; v < vSize; v++) {
				int nu = u + uOffset;
				int nv = v + vOffset;
				if (nu >= 0 && nu < uSize && nv >= 0 && nv < vSize) {
					newHeightChunks[u + v * uSize] = heightChunks[nu + nv * uSize];
				} else {
					newHeightChunks[u + v * uSize] = new HeightChunk(isMax);
				}
			}
		}
		heightChunks = newHeightChunks;
	}
	
}
