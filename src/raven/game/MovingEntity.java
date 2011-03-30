package raven.game;

import raven.math.C2DMatrix;
import raven.math.Vector2D;

public abstract class MovingEntity extends BaseGameEntity {
	protected Vector2D velocity;
	
	/** a normalized vector pointing in the direction the entity is heading */
	protected Vector2D heading;
	
	/** a vector perpendicular to the heading vector */
	protected Vector2D side;
	
	double mass;
	
	/** the maximum speed this entity may travel at */
	double maxSpeed;
	
	/** the maximum force this entity can produce to power itself (think
	 * rockets and thrust) */
	double maxForce;
	
	/** the maximum rate (radians per second)this vehicle can rotate */
	double maxTurnRate;
	
	RavenGame world;
	
	public MovingEntity(Vector2D position,
						double radius,
						Vector2D velocity,
						double maxSpeed,
						Vector2D heading,
						double mass,
						Vector2D scale,
						double turnRate,
						double maxForce,
						RavenGame game) {
		super(BaseGameEntity.getNextValidID());
		this.heading = heading;
		this.velocity = velocity;
		this.mass = mass;
		this.side = heading.perp();
		this.maxSpeed = maxSpeed;
		this.maxTurnRate = turnRate;
		this.maxForce = maxForce;
		this.position = position;
		this.boundingRadius = radius;
		this.scale = scale;
		this.world = game;
	}
	
	// Accessors
	
	public Vector2D velocity() { return velocity; }
	public void setVelocity(Vector2D newVel) { velocity = newVel; }
	
	public double mass() { return mass; };
	
	public Vector2D side() { return side; }
	
	public double maxSpeed() { return maxSpeed; }
	public void setMaxSpeed(double newSpeed) { maxSpeed = newSpeed; }
	public double getMaxSpeed() { return maxSpeed; }
	
	public double maxForce() { return maxForce; }
	public void setMaxForce(double mf) { maxForce = mf; }
	
	public boolean isSpeedMaxedOut() { return maxSpeed * maxSpeed >= velocity.lengthSq(); }
	public double speed() { return velocity.length(); }
	public double speedSq() { return velocity.lengthSq(); }
	
	public Vector2D heading() { return heading; }
	public void setHeading(Vector2D newHeading) {
		if (newHeading.lengthSq() - 1.0 < 0.00001) {
			System.err.println("Warning: Heading set to 0!");
			return;
		}
		
		heading = newHeading;
		side = heading.perp();
	}
	public boolean rotateHeadingToFacePosition(Vector2D target) {
		Vector2D toTarget = target.sub(position);
		toTarget.normalize();
		
		double dot = heading.dot(toTarget);
		
		// Ensure valid value for acos
		dot = Math.min(-1, Math.max(1, dot));
		
		// first determine the angle between the heading vector and the target
		double angle = Math.acos(dot);
		
		// return true if the player is facing the target
		if (angle < 0.00001)
			return true;
		
		// clamp the amount to turn to the max turn rate
		if (angle > maxTurnRate)
			angle = maxTurnRate;
		
		// The next few lines use a rotation matrix to rotate the player's
		// heading vector accordingly
		C2DMatrix rotationMatrix = new C2DMatrix();
		
		// notice how the direction of rotation has to be determined when
		// creating the rotation matrix
		rotationMatrix.rotate(angle * heading.sign(toTarget));	
		rotationMatrix.transformVector2Ds(heading);
		rotationMatrix.transformVector2Ds(velocity);
		
		side = heading.perp();
		
		return false;		
	}
	
	public RavenGame GetWorld() { return this.world; }
	
	public double maxTurnRate() { return maxTurnRate; }
	public void setMaxTurnRate(double val) { maxTurnRate = val; }
}
