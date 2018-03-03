package creature;

import java.util.ArrayList;

import physics.Rect;

import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;

public class Box {

	public double sticky;
	public ArrayList<Joint> joints;
	
	public Vector3f position;
	public Vector3f size;
	
	public Rect rect;
	
	public Box(double sticky, Vector3f size) {
		this.size = size;
		this.sticky = sticky;
		this.joints = new ArrayList<Joint>();
		this.position = new Vector3f();
		this.rect = new Rect(size, position);
	}
	
	public void render(Vector3f offs, Entity cube) {
		cube.setModelMatrix(
				Matrix4f.translate(position.add(offs)).multiply(
						Matrix4f.scale(size.mult(.5f))));
		cube.render();
		for (Joint j : joints) {
			j.attached.render(offs, cube);
		}
	}
	
	public void calcPositions(Vector3f offs, Vector3f globalOffs) {
		position = offs;
		for (Joint j : joints) {
			Vector3f joffs = offs.add(j.origin).add(j.direction.normalize().mult((float) (j.length * j.value)));
			j.attached.calcPositions(joffs, globalOffs);
		}
		rect.setPosition(position.add(globalOffs));
	}
}
