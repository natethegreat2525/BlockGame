package database;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.nshirley.engine3d.math.Vector3i;

import world.ChunkData;

public class DatabaseSaver implements Runnable {

	public static DatabaseSaver start(DBConnection conn) {
		DatabaseSaver ds = new DatabaseSaver(conn);
		Thread t = new Thread(ds);
		t.start();
		return ds;
	}
	
	public DBConnection conn;
	public boolean running;
	public ConcurrentLinkedQueue<ChunkData> queue;
	
	public DatabaseSaver(DBConnection conn) {
		this.conn = conn;
		queue = new ConcurrentLinkedQueue<ChunkData>();
	}
	
	public void save(ChunkData cd) {
		queue.add(cd);
		synchronized(this) {
			this.notify();
		}
	}
	
	@Override
	public void run() {
		running = true;
		
		while (running) {
			try {
				synchronized(this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
			while (queue.size() > 0) {
				ChunkData d = queue.remove();
				Vector3i p = d.getPosition();
				if (!conn.saveChunk(d, p.x, p.y, p.z, 0)) {
					System.out.println("Failed to save chunk: " + p);
				}
			}
		}
		
	}
	
	public void stop() {
		this.running = false;
		synchronized(this) {
			this.notify();
		}
	}
	
}
