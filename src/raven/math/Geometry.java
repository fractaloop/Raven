package raven.math;

public class Geometry {

	public static double distToLineSegment(Vector2D from, Vector2D to, Vector2D pos) {
		return Math.sqrt(distToLineSegmentSq(from, to, pos));
	}
	
	public static double distToLineSegmentSq(Vector2D from, Vector2D to, Vector2D pos) {
		double dotA = (pos.x - from.x) * (to.x - from.x) + (pos.y - from.y) * (to.y - from.y);
		
		// if the angle is obtuse between PA and AB is obtuse then the closest
		// vertex must be A
		if (dotA <= 0)
			return from.distanceSq(pos);
		
		// if the angle is obtuse between PB and AB is obtuse then the closest
		// vertex must be B
		double dotB = (pos.x - to.x) * (from.x - to.x) + (pos.y - to.y) * (from.y - to.y);
		
		if (dotB <= 0)
			return to.distanceSq(pos);
		
		// calculate the point along AB that is the closest to P
		Vector2D point = from.add((to.sub(from).mul(dotA).div(dotA + dotB)));
		
		return pos.distanceSq(point);
	}

	public static boolean lineIntersection2D(Vector2D A, Vector2D B, Vector2D C, Vector2D D) {
		double rTop = (A.y-C.y)*(D.x-C.x)-(A.x-C.x)*(D.y-C.y);
		double sTop = (A.y-C.y)*(B.x-A.x)-(A.x-C.x)*(B.y-A.y);

		double Bot = (B.x-A.x)*(D.y-C.y)-(B.y-A.y)*(D.x-C.x);

		// parallel
		if (Bot == 0)
		{
			return false;
		}

		double invBot = 1.0/Bot;
		double r = rTop * invBot;
		double s = sTop * invBot;

		if( (r > 0) && (r < 1) && (s > 0) && (s < 1) ) {
			//lines intersect
			return true;
		}

		//lines do not intersect
		return false;
	}

	public static boolean lineSegmentCircleIntersection(Vector2D from, Vector2D to, Vector2D pos, double radius) {
		double distToLineSq = distToLineSegmentSq(from, to, pos);
		
		if (distToLineSq < radius * radius) {
			return true;
		} else {
			return false;
		}
	}

}
