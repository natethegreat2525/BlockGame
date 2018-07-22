package engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

import world.ChunkData;

public class ChunkRangeEntityManager implements EntityManager {

	public long nextID;

	public HashMap<Long, SimEntity> entities;
	public HashMap<Long, SimEntity> newEntities;
	public ArrayList<Long> removeFlag;
	public Simulator simulator;
	
	public ChunkRangeEntityManager() {
		entities = new HashMap<Long, SimEntity>();
		newEntities = new HashMap<Long, SimEntity>();
		removeFlag = new ArrayList<Long>();
	}
	
	public void setSimulator(Simulator sim) {
		simulator = sim;
	}
	
	public void add(SimEntity e) {
		e.setID(nextID);
		newEntities.put(nextID, e);
		nextID++;
		e.setUp(simulator);
	}
	
	public void setRemoveFlag(long id) {
		removeFlag.add(id);
	}
	
	public void update(float delta) {
		for (Long id : entities.keySet()) {
			entities.get(id).update(simulator, delta);
		}
		
		entities.putAll(newEntities);
		newEntities.clear();
		
		for (Long id : removeFlag) {
			SimEntity e = entities.remove(id);
			e.tearDown(simulator);
		}
		removeFlag.clear();
	}
	
	public void render(Vector3f camPos, Vector3f direction, int pass) {
		Vector3i chunkPos = simulator.world.getChunkPos((int) camPos.x, (int) camPos.y, (int) camPos.z);
		for (int i = -5; i <= 5; i++) {
			for (int j = -3; j <= 3; j++) {
				for (int k = -5; k <= 5; k++) {
					Vector3i nc = chunkPos.clone();
					nc.x += i;
					nc.y += j;
					nc.z += k;
					ChunkData cd = simulator.world.getChunkData(nc);
					for (SimEntity ent : cd.getEntities()) {
						if (ent != null) {
							ent.render(camPos, direction, pass);
						}
					}
				}
			}
		}
		//render entities
		for (Long id : entities.keySet()) {
			SimEntity se = entities.get(id);
			if (se != null) {
				se.render(camPos, direction, pass);
			}
		}
	}
}
