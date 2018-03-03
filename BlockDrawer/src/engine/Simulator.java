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
	public ArrayList<Long> removeFlag;
	public World world;
	public ChunkBuilderThread builder;
	public ChunkViewport cv;
	public PhysSim physics;
	
	public Simulator(World w, ChunkViewport cv, Vector3f gravity, Entity box) {
		entities = new HashMap<Long, SimEntity>();
		removeFlag = new ArrayList<Long>();
		world = w;
		this.cv = cv;
		builder = new ChunkBuilderThread(cv);
		Thread builderThread = new Thread(builder);
		builderThread.start();
		physics = new PhysSim(world, gravity, box);
	}
	
	public void add(SimEntity e) {
		e.setID(nextID);
		entities.put(nextID, e);
		nextID++;
		e.setUp(this);
	}
	
	public void setRemoveFlag(long id) {
		removeFlag.add(id);
	}
	
	public void update(float delta) {
		//manage chunks
		if (cv.getNumToTriangulate() < 15 || world.hasUpdates()) {
			//triggers another thread to build more
			builder.loadMore();
		}
		physics.step(delta);
		//update entities
		for (Long id : entities.keySet()) {
			entities.get(id).update(this, delta);
		}
	}
	
	public void render(Vector3f camPos, Vector3f direction) {
		//Triangulate some chunks
		for (int i = 0; i < 15; i++) {
			cv.triangulateNextChunk();
		}
		
		//render entities
		for (Long id : entities.keySet()) {
			entities.get(id).render();
		}
		
		physics.render();
		
		//render world
		cv.render(camPos, direction);

	}
	
	public void finish() {
		builder.finish();
	}
}
