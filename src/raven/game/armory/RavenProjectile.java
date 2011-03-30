package raven.game.armory;

import raven.game.MovingEntity;
import raven.math.Vector2D;

public abstract class RavenProjectile extends MovingEntity {

	public RavenProjectile(Vector2D position,
						double radius,
						Vector2D velocity,
						double maxSpeed,
						Vector2D heading,
						double mass,
						Vector2D scale,
						double turnRate,
						double maxForce) {
		super(position, radius, velocity, maxSpeed, heading, mass, scale, turnRate,
				maxForce);
		// TODO Auto-generated constructor stub
	}

}
