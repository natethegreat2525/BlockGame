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
			for (int i = 0; i < 15; i++)
				chunkViewport.loadNextUnloadedChunk();
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
