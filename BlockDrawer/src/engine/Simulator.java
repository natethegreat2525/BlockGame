package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.math.Vector3f;

import chunks.ChunkViewport;
import physics.PhysSim;
import world.ChunkBuilderThread;
import world.World;

public class Simulator {

	public World world;
	public ChunkBuilderThread[] builders;
	public ChunkViewport cv;
	public PhysSim physics;
	public EntityManager em;
	
	public Simulator(World w, ChunkViewport cv, Vector3f gravity, Mesh box, EntityManager em) {
		world = w;
		this.cv = cv;
		this.em = em;
		
		builders = new ChunkBuilderThread[6];
		for (int i = 0; i < builders.length; i++) {
			builders[i] = new ChunkBuilderThread(cv);
			Thread builderThread = new Thread(builders[i]);
			builderThread.start();
		}
		
		physics = new PhysSim(world, gravity, box);
		em.setSimulator(this);
	}
	

	
	public void update(float delta) {
		//manage chunks
		if (cv.getNumToTriangulate() < 4 || world.hasUpdates()) {
			//triggers another thread to build more
			for (int i = 0; i < builders.length; i++) {
				builders[i].loadMore();
			}
		}
		physics.step(delta);
		//update entities
		em.update(delta);
	}
	
	public void render(Vector3f camPos, Vector3f direction) {
		this.render(camPos, direction, 0);
	}
	
	public void render(Vector3f camPos, Vector3f direction, int pass) {
		if (pass == 0) {
			long maxMilliseconds = 2;
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() < start + maxMilliseconds && cv.getChunkQueueSize() > 0) {
				cv.triangulateNextChunk();
			}
		}
		
		
		if (pass == 0) {
			physics.render();
			
			//render world
			cv.render(camPos, direction);
		}
		
		if (pass == 0) {
			em.render(camPos, direction, pass);
		}

	}
	
	public void finish() {
		for (int i = 0; i < builders.length; i++) {
			builders[i].finish();
		}
	}
}
