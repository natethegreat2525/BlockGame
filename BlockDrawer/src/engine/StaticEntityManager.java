package engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.nshirley.engine3d.math.Vector3f;

public class StaticEntityManager implements EntityManager {

	public long nextID;

	public HashMap<Long, SimEntity> entities;
	public HashMap<Long, SimEntity> newEntities;
	public ArrayList<Long> removeFlag;
	public Simulator simulator;
	
	public StaticEntityManager() {
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
		
		//render entities
		for (Long id : entities.keySet()) {
			SimEntity se = entities.get(id);
			if (se != null) {
				se.render(camPos, direction, pass);
			}
		}
	}
}
