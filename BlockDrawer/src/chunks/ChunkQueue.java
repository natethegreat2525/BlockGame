package chunks;

import java.util.LinkedList;

public class ChunkQueue {

	private LinkedList<Chunk> lowPriority;
	private LinkedList<Chunk> highPriority;
	
	public ChunkQueue() {
		lowPriority = new LinkedList<Chunk>();
		highPriority = new LinkedList<Chunk>();
	}
	
	public synchronized void addLowPriority(Chunk c) {
		lowPriority.add(c);
	}
	
	public synchronized void addHighPriority(Chunk c) {
		highPriority.add(c);
	}
	
	public synchronized Chunk pop() {
		if (highPriority.size() > 0) {
			Chunk c = highPriority.removeFirst();
			if (c != null)
				c.highPriority = true;
			return c;
		}
		
		if (lowPriority.size() > 0) {
			Chunk c = lowPriority.removeFirst();
			if (c != null)
				c.highPriority = false;
			return c;
		}
		
		return null;
	}
	
	public synchronized int size() {
		return lowPriority.size() + highPriority.size();
	}
}
