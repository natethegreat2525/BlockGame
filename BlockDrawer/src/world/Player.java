package world;

import physics.Rect;

import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;

public class Player {

	private Mesh box;
	private Rect rect;
	private float angle;

	public Player(Mesh e, Vector3f pos, Vector3f size) {
		box = e;
		rect = new Rect(size);
		rect.setPosition(pos.clone());
	}
	
	public void setAngle(float val) {
		angle = val;
	}
	
	public void setPosition(Vector3f pos) {
		rect.setPosition(pos);
	}
	
	public void setPositionX(float x) {
		Vector3f newPos = rect.getPosition();
		newPos.x = x;
		rect.setPosition(newPos);
	}
	public void setPositionY(float y) {
		Vector3f newPos = rect.getPosition();
		newPos.y = y;
		rect.setPosition(newPos);
	}
	public void setPositionZ(float z) {
		Vector3f newPos = rect.getPosition();
		newPos.z = z;
		rect.setPosition(newPos);
	}
	
	public void setVelocityX(float x) {
		Vector3f newVel = rect.getVelocity();
		newVel.x = x;
		rect.setVelocity(newVel);
	}
	
	public void setVelocityY(float y) {
		Vector3f newVel = rect.getVelocity();
		newVel.y = y;
		rect.setVelocity(newVel);
	}
	
	public void setVelocityZ(float z) {
		Vector3f newVel = rect.getVelocity();
		newVel.z = z;
		rect.setVelocity(newVel);
	}
	
	public void setVelocityXZRel(float x, float z) {
		float a = (float) Math.toRadians(angle);
		Vector3f newVel = rect.getVelocity();
		newVel.x = (float) (x * Math.cos(a) - z * Math.sin(a));
		newVel.z = (float) (z * Math.cos(a) + x * Math.sin(a));
	}
	
	public void update(World world, Vector3f gravity, float delta) {
		rect.update(world, gravity, delta);
	}
	
	public void render() {
		box.setModelMatrix(
				Matrix4f.translate(rect.getPosition()).multiply(
						Matrix4f.scale(rect.getSize().mult(.5f)).multiply(
								Matrix4f.rotateY(-angle))));
		box.render();
	}

	public boolean isGrounded() {
		return rect.isGrounded();
	}

	public Vector3f getPosition() {
		return rect.getPosition();
	}
}
