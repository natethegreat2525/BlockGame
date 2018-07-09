package physics;

import java.util.ArrayList;

import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;

import world.World;

public class PhysSim {

	public ArrayList<PhysRect> rects;
	public World world;
	public Vector3f gravity;
	public Mesh box;
	
	public PhysSim(World w, Vector3f g, Mesh e) {
		world = w;
		gravity = g;
		box = e;
		rects = new ArrayList<PhysRect>();
	}
	
	public void AddRect(PhysRect rect) {
		rects.add(rect);
	}
	
	public void RemoveRect(PhysRect rect) {
		rects.remove(rect);
	}
	
	
	public void step(float delta) {
		for(Rect r : rects) {
			r.update(world, gravity, delta);
		}
	}
	
	public void render() {
		for (PhysRect r : rects) {
			if (r.render) {
				box.setModelMatrix(
						Matrix4f.translate(r.getPosition()).multiply(
								Matrix4f.scale(r.getSize().mult(.5f))));
				box.render();
			}
		}
	}
}
