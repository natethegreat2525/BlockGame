package engine;

import com.nshirley.engine3d.math.Vector3f;

public interface EntityManager {

	public void update(float delta);
	public void render(Vector3f camPos, Vector3f direction, int pass);
	public void add(SimEntity e);
	public void setRemoveFlag(long id);
	public void setSimulator(Simulator sim);
}
