package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Vector3f;

import chunks.ChunkViewport;
import physics.PhysSim;
import world.ChunkBuilderThread;
import world.World;

public class Simulator {

	public long nextID;
	public HashMap<Long, SimEntity> entities;
	public HashMap<Long, SimEntity> newEntities;
	public ArrayList<Long> removeFlag;
	public World world;
	public ChunkBuilderThread[] builders;
	public ChunkViewport cv;
	public PhysSim physics;
	
	public Simulator(World w, ChunkViewport cv, Vector3f gravity, Entity box) {
		entities = new HashMap<Long, SimEntity>();
		newEntities = new HashMap<Long, SimEntity>();
		removeFlag = new ArrayList<Long>();
		world = w;
		this.cv = cv;
		
		builders = new ChunkBuilderThread[6];
		for (int i = 0; i < builders.length; i++) {
			builders[i] = new ChunkBuilderThread(cv);
			Thread builderThread = new Thread(builders[i]);
			builderThread.start();
		}
		
		physics = new PhysSim(world, gravity, box);
	}
	
	public void add(SimEntity e) {
		e.setID(nextID);
		newEntities.put(nextID, e);
		nextID++;
		e.setUp(this);
	}
	
	public void setRemoveFlag(long id) {
		removeFlag.add(id);
	}
	
	public void update(float delta) {
		//manage chunks
		if (cv.getNumToTriangulate() < 10 || world.hasUpdates()) {
			//triggers another thread to build more
			for (int i = 0; i < builders.length; i++) {
				builders[i].loadMore();
			}
		}
		physics.step(delta);
		//update entities
		for (Long id : entities.keySet()) {
			entities.get(id).update(this, delta);
		}
		
		entities.putAll(newEntities);
		newEntities.clear();
		
		for (Long id : removeFlag) {
			SimEntity e = entities.remove(id);
			e.tearDown(this);
		}
		removeFlag.clear();
	}
	
	public void render(Vector3f camPos, Vector3f direction) {
		this.render(camPos, direction, 0);
	}
	
	public void render(Vector3f camPos, Vector3f direction, int pass) {
		if (pass == 0) {
			long maxMilliseconds = 20;
			long start = System.currentTimeMillis();
			int cnt = 0;
			int real = 0;
			while (System.currentTimeMillis() < start + maxMilliseconds && cv.getChunkQueueSize() > 0) {
				cnt++;
				int t = cv.triangulateNextChunk() ? real++ : 0;
			}
			//System.out.println(cnt + "\t" + real + "\t" + (System.currentTimeMillis() - start));
		}
		
		//render entities
		for (Long id : entities.keySet()) {
			SimEntity se = entities.get(id);
			if (se != null) {
				se.render(pass);
			}
		}
		
		if (pass == 0) {
			physics.render();
			
			//render world
			cv.render(camPos, direction);
		}

	}
	
	public void finish() {
		for (int i = 0; i < builders.length; i++) {
			builders[i].finish();
		}
	}
}
