package physics;

import world.World;
import blockdraw.Block;
import blockdraw.BlockContainer;

import com.nshirley.engine3d.math.Vector3f;

public class Rect {

	private Vector3f pos;
	private Vector3f size;
	private Vector3f vel;
	private boolean grounded;
	
	public Rect(Vector3f size) {
		this(size, new Vector3f());
	}
	
	public Rect(Vector3f size, Vector3f pos) {
		this.size = size;
		this.pos = pos;
		this.vel = new Vector3f();
	}
	
	public void setPosition(Vector3f pos) {
		this.pos = pos;
	}
	
	public void setVelocity(Vector3f vel) {
		this.vel = vel;
	}
	
	public Vector3f getPosition() {
		return pos;
	}
	
	public void update(World world, Vector3f gravity, float delta) {
		vel.x += gravity.x * delta;
		vel.y += gravity.y * delta;
		vel.z += gravity.z * delta;
		
		pos.x += vel.x * delta;
		pos.y += vel.y * delta;
		pos.z += vel.z * delta;
		
		double lowX = pos.x - size.x/2;
		double lowY = pos.y - size.y/2;
		double lowZ = pos.z - size.z/2;
		
		double highX = pos.x + size.x/2;
		double highY = pos.y + size.y/2;
		double highZ = pos.z + size.z/2;
		
		int sX = (int) Math.floor(lowX - .5);
		int sY = (int) Math.floor(lowY - .5);
		int sZ = (int) Math.floor(lowZ - .5);
		
		int eX = (int) Math.floor(highX + .5);
		int eY = (int) Math.floor(highY + .5);
		int eZ = (int) Math.floor(highZ + .5);
		
		int xDir = (vel.x < 0) ? -1 : 1;
		int yDir = (vel.y < 0) ? -1 : 1;
		int zDir = (vel.z < 0) ? -1 : 1;
		
		if (xDir == -1) {
			int tmp = sX;
			sX = eX;
			eX = tmp;
		}
		if (yDir == -1) {
			int tmp = sY;
			sY = eY;
			eY = tmp;
		}
		if (zDir == -1) {
			int tmp = sZ;
			sZ = eZ;
			eZ = tmp;
		}
		grounded = false;
		for (int x = sX; (x <= eX && xDir == 1) || (x >= eX && xDir == -1); x+=xDir) {
			for (int y = sY; (y <= eY && yDir == 1) || (y >= eY && yDir == -1); y+=yDir) {
				for (int z = sZ; (z <= eZ && zDir == 1) || (z >= eZ && zDir == -1); z+=zDir) {
					Block block = BlockContainer.getBlockType(world.getBlockValue(x, y, z));
					if (block.isCollidable()) {
						double oX = overlap(x, x+.5, x+1, lowX, pos.x, highX);
						double oY = overlap(y, y+.5, y+1, lowY, pos.y, highY);
						double oZ = overlap(z, z+.5, z+1, lowZ, pos.z, highZ);
						double absX = Math.abs(oX);
						double absY = Math.abs(oY);
						double absZ = Math.abs(oZ);
						
						if (absX > 0 && absY > 0 && absZ > 0) {
							if (absX < absY && absX < absZ) {
								//X is least
								pos.x += oX;
								lowX += oX;
								highX += oX;
								if (oX * vel.x < 0) {
									vel.x = 0;  //set velocity to zero if velocity is going into the wall
								}
							} else if (absY < absZ) {
								//Y is least
								pos.y += oY;
								lowY += oY;
								highY += oY;
								if (oY * vel.y < 0) {
									if (vel.y < 0) {
										grounded = true;
									}
									vel.y = 0;  //set velocity to zero if velocity is going into the wall
								}
							} else {
								//Z is least
								pos.z += oZ;
								lowZ += oZ;
								highZ += oZ;
								if (oZ * vel.z < 0) {
									vel.z = 0;  //set velocity to zero if velocity is going into the wall
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	private static double overlap(double aL, double aC, double aH, double bL, double bC, double bH) {
		boolean reverse = false;
		if (aC > bC) {
			reverse = true;
			double tmpL = aL;
			double tmpH = aH;
			aL = bL;
			aH = bH;
			bL = tmpL;
			bH = tmpH;
		}
		
		if (bL < aH) {
			return (reverse) ? (bL - aH) : (aH - bL);
		}
		
		return 0;
	}

	public Vector3f getSize() {
		return size;
	}

	public Vector3f getVelocity() {
		return vel;
	}

	public boolean isGrounded() {
		return grounded;
	}
}
