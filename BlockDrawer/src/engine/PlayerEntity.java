package engine;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_POLYGON_MODE;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glGetInteger;

import org.lwjgl.glfw.GLFW;

import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Mesh;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.window.Input;
import com.nshirley.engine3d.window.Mouse;

import blockdraw.Block;
import blockdraw.BlockContainer;
import physics.Rect;
import world.Player;
import world.Raycast;

public class PlayerEntity extends SimEntity {

	public Player player;
	public Raycast rc;
	public Mesh box;
	public Vector3f headPos;
	public Camera3d cam;
	public Vector3f[] outline;
	
	public PlayerEntity(Mesh box, Vector3f pos, Vector3f size, Camera3d cam) {
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
		if (rc != null) {
			Block b = BlockContainer.getBlockType(s.world.getBlockValue(rc.blockPosition));
			if (b.specialBoundingBox()) {
				outline = b.getSpecialBoundingBox();
			} else {
				outline = new Vector3f[] {new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)};
			}
		}
	}
	
	@Override
	public void render(int pass) {
		if (pass == 0) {
			player.render();
			
			if (rc != null) {
				box.setModelMatrix(Matrix4f.translate(rc.position).multiply(Matrix4f.scale(new Vector3f(.1f, .1f, .1f))));
				box.render();
				
				Vector3f blockPos = new Vector3f(rc.blockPosition.x, rc.blockPosition.y, rc.blockPosition.z);
				outline[1] = outline[1].add(blockPos);
				outline[0] = outline[0].add(blockPos);
				Vector3f outlineSize = outline[1].sub(outline[0]).mult(.501f);
				Vector3f outlinePos = outline[1].add(outline[0]).mult(.5f);
				box.setModelMatrix(Matrix4f.translate(outlinePos).multiply(Matrix4f.scale(outlineSize)));
				int oldPolyMode = glGetInteger( GL_POLYGON_MODE );
				glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
				box.render();
				glPolygonMode( GL_FRONT_AND_BACK, oldPolyMode );
			}
		}
	}
}
