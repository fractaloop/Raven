/**
 * 
 */
package raven.game.interfaces;

import raven.math.Vector2D;

/**
 * @author chester
 *
 */
public interface IRavenTargetingSystem {

	boolean isTargetPresent();

	double getTimeTargetHasBeenVisible();

	boolean isTargetShootable();

	double getTimeTargetHasBeenOutOfView();

	IRavenBot getTarget();

	void update();

	void clearTarget();

	Vector2D getLastRecordedPosition();

	boolean isTargetWithinFOV();

}
