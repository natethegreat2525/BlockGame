package engine;

import org.lwjgl.glfw.GLFW;

import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.window.Input;
import com.nshirley.engine3d.window.Mouse;

import physics.Rect;
import world.Player;
import world.Raycast;

public class PlayerEntity extends SimEntity {

	public Player player;
	public Raycast rc;
	public Entity box;
	public Vector3f headPos;
	public Camera3d cam;
	
	public PlayerEntity(Entity box, Vector3f pos, Vector3f size, Camera3d cam) {
		player = new Player(box, pos, size);
		this.box = box;
		this.cam = cam;
	}
	
	public void setUp(Simulator s) {
		
	}
	
	public void tearDown(Simulator s) {
		
	}
	
	public void update(Simulator s, float delta) {
		float plSpeed = Input.isKeyDown(GLFW.GLFW_KEY_N) ? 1.0f : .1f;
		float jump = .2f;
		float xspd = 0, zspd = 0;
		if (Input.isKeyDown(GLFW.GLFW_KEY_W)) {
			zspd -= 1;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_S)) {
			zspd += 1;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_A)) {
			xspd -= 1;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_D)) {
			xspd += 1;
		}
		xspd *= plSpeed * delta;
		zspd *= plSpeed * delta;
		
		if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			if (player.isGrounded() || Input.isKeyDown(GLFW.GLFW_KEY_N))
				player.setVelocityY(jump);
		}
		
		player.setVelocityXZRel(xspd, zspd);
		
		player.update(s.world, s.physics.gravity, delta);
		
		headPos = player.getPosition().add(new Vector3f(0, .7f, 0));
		
		float rotH = (float) Mouse.X * .3f;
		float rotV = (float) Mouse.Y * .3f;
		
		cam.setRotation(new Vector3f((float) rotV, (float) rotH, 0));
		cam.setPosition(headPos);
		
		player.setAngle(rotH);
		
		Vector3f lookNorm = cam.getLookDir().normalize();
		Vector3f lookSpd = lookNorm.mult(0.3f);
		Vector3f startRay = headPos.clone();
		Vector3f rayDir = lookSpd;
		rc = s.world.raycast(startRay, rayDir, 40);
		
	}
	
	@Override
	public void render(int pass) {
		if (pass == 0) {
			player.render();
			
			if (rc != null) {
				box.setModelMatrix(Matrix4f.translate(rc.position).multiply(Matrix4f.scale(new Vector3f(.1f, .1f, .1f))));
				box.render();
			}
		}
	}
}
