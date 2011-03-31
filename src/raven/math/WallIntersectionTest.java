package raven.math;

import java.util.List;

public class WallIntersectionTest {

	public static boolean doWallsObstructLineSegment(Vector2D from, Vector2D to, List<Wall2D> walls) {
		for (Wall2D wall : walls) {
			if (Geometry.lineIntersection2D(from, to, wall.from(), wall.to())) {
				return true;
			}
		}
		
		return false;
	}

	public static boolean doWallsIntersectCircle(List<Wall2D> walls, Vector2D pos, double radius) {
		for (Wall2D wall : walls) {
			if (Geometry.lineSegmentCircleIntersection(wall.from(), wall.to(), pos, radius)) {
				return true;
			}
		}
		
		return false;
	}


}
