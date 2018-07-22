package world;

import chunks.ChunkViewport;

public class ChunkBuilderThread implements Runnable {

	private ChunkViewport chunkViewport;
	private boolean finished = false;
	
	public ChunkBuilderThread(ChunkViewport cv) {
		chunkViewport = cv;
	}
		
	@Override
	public void run() {
		while (true) {
			synchronized(this) {
				if (finished) {
					return;
				}
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (chunkViewport.getChunkQueueSize() < 50) {
				for (int i = 0; i < 5; i++) {
					chunkViewport.loadNextUnloadedChunk();
				}
			}
		}
	}
	
	public synchronized void loadMore() {
		this.notify();
	}

	public synchronized void finish() {
		this.finished = true;
		this.notify();
	}

}
