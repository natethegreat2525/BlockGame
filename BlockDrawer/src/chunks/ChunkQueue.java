package chunks;

import java.util.HashSet;
import java.util.LinkedList;

import com.nshirley.engine3d.math.Vector3i;

public class ChunkQueue {

	private LinkedList<Chunk> lowPriority;
	private LinkedList<Chunk> highPriority;
	
	private HashSet<Vector3i> contained;
	
	public ChunkQueue() {
		lowPriority = new LinkedList<Chunk>();
		highPriority = new LinkedList<Chunk>();
		contained = new HashSet<Vector3i>();
	}
	
	public synchronized void addLowPriority(Chunk c) {
		if (contained.contains(c.position)) {
			return;
		}
		lowPriority.add(c);
		contained.add(c.position);
	}
	
	public synchronized void addHighPriority(Chunk c) {
		if (contained.contains(c.position)) {
			return;
		}
		highPriority.add(c);
		contained.add(c.position);
	}
	
	public synchronized Chunk pop() {
		if (highPriority.size() > 0) {
			Chunk c = highPriority.removeFirst();
			if (c != null)
				c.highPriority = true;
			contained.remove(c.position);
			return c;
		}
		
		if (lowPriority.size() > 0) {
			Chunk c = lowPriority.removeFirst();
			if (c != null)
				c.highPriority = false;
			contained.remove(c.position);
			return c;
		}
		
		return null;
	}
	
	public synchronized int size() {
		return lowPriority.size() + highPriority.size();
	}
}
