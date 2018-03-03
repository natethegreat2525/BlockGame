package physics;

import java.util.ArrayList;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;

import world.World;

public class PhysSim {

	public ArrayList<Rect> rects;
	public World world;
	public Vector3f gravity;
	public Entity box;
	
	public PhysSim(World w, Vector3f g, Entity e) {
		world = w;
		gravity = g;
		box = e;
		rects = new ArrayList<Rect>();
	}
	
	public void AddRect(Rect rect) {
		rects.add(rect);
	}
	
	public void step(float delta) {
		for(Rect r : rects) {
			r.update(world, gravity, delta);
		}
	}
	
	public void render() {
		for (Rect r : rects) {
			box.setModelMatrix(
					Matrix4f.translate(r.getPosition()).multiply(
							Matrix4f.scale(r.getSize().mult(.5f))));
			box.render();
		}
	}
}
