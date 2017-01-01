package chunks;

import java.util.LinkedList;

public class ChunkQueue {

	private LinkedList<Chunk> lowPriority;
	private LinkedList<Chunk> highPriority;
	
	public ChunkQueue() {
		lowPriority = new LinkedList<Chunk>();
		highPriority = new LinkedList<Chunk>();
	}
	
	public void addLowPriority(Chunk c) {
		lowPriority.add(c);
	}
	
	public void addHighPriority(Chunk c) {
		highPriority.add(c);
	}
	
	public Chunk pop() {
		if (highPriority.size() > 0) {
			return highPriority.removeFirst();
		}
		
		if (lowPriority.size() > 0) {
			return lowPriority.removeFirst();
		}
		
		return null;
	}
}
