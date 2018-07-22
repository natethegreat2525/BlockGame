package engine;

import com.nshirley.engine3d.math.Vector3f;

public abstract class SimEntity {

	public long id;
	
	public long getID() {
		return id;
	}
	
	public void setID(long id) {
		this.id = id;
	}
	
	public void setUp(Simulator s) {
		
	}
	
	public void tearDown(Simulator s) {
		
	}
	
	public void update(Simulator s, float delta) {
		
	}
	
	public void render(Vector3f camPos, Vector3f direction, int pass) {
		
	}
}
