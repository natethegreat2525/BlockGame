package physics;

public interface PhysCallback {

	//rect collided with another rect
	//returns whether collision should be handled or not
	public boolean collide(PhysRect other);
	
	//rect collided with terrain
	public boolean terrain();
}
