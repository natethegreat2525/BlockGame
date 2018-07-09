package creature;

import java.util.ArrayList;

import world.World;

import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;

public class Creature {

	public Box body;
	public ArrayList<Box> boxes;
	public ArrayList<Joint> joints;
	public Mesh cube;
	
	public Creature(Vector3f pos, Mesh cube) {
		body = new Box(0, new Vector3f(1, .2f, 1));
		this.cube = cube;
		body.position = pos;
		
		Box leg1 = new Box(0, new Vector3f(.1f, .6f, .1f));
		Box leg2 = new Box(0, new Vector3f(.1f, .6f, .3f));
		
		Joint a = new Joint(new Vector3f(.5f, 0, .5f), new Vector3f(0, .1f, .1f), .5, 0, leg1);
		Joint b = new Joint(new Vector3f(-.5f, 0, .5f), new Vector3f(.1f, .1f, 0), .5, 0, leg2);
		body.joints.add(a);
		body.joints.add(b);
		
		joints = new ArrayList<Joint>();
		joints.add(a);
		joints.add(b);
		
		boxes = new ArrayList<Box>();
		boxes.add(body);
		boxes.add(leg1);
		boxes.add(leg2);
	}
	
	public void render() {
		body.render(new Vector3f(0, 0, 0), cube);
	}
	
	public void updatePhysics(World w, Vector3f gravity, float delta) {
		joints.get(0).value = 0.5 + Math.sin(System.currentTimeMillis() / 100.0) / 2;
		joints.get(1).value = 0.5 + Math.sin(System.currentTimeMillis() / 90.0) / 2;
		body.calcPositions(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		Vector3f sum = new Vector3f();
		for (Box b : boxes) {
			b.rect.update(w, gravity, delta);
			sum.add(b.rect.getPosition());
		}
		sum.mult(1.0f / boxes.size());
	}
}
