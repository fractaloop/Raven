package raven.math;

import java.util.List;

import raven.utils.Log;

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
	
	/**
	 * given a line segment AB and a circle position and radius, this function
	 * determines if there is an intersection and stores the position of the
	 * closest intersection in the reference IntersectionPoint
	 * @param a
	 * @param b
	 * @param pos
	 * @param radius
	 * @return null if no intersection point is found
	 */
	public Vector2D getLineSegmentCircleClosestIntersectionPoint(
			Vector2D a, Vector2D b, Vector2D pos, double radius) {
		
		Vector2D toBNorm = b.sub(a);
		toBNorm.normalize();

		// move the circle into the local space defined by the vector B-A with
		// origin at A
		Vector2D localPos = Transformations.pointToLocalSpace(pos, toBNorm, toBNorm.perp(), a);

		// if the local position + the radius is negative then the circle lays
		// behind point A so there is no intersection possible. If the local x
		// pos minus the radius is greater than length A-B then the circle
		// cannot intersect the line segment
		if ( (localPos.x + radius >= 0) && ( (localPos.x - radius) * (localPos.x - radius) <= b.distanceSq(a)) )
		{
			//if the distance from the x axis to the object's position is less
			//than its radius then there is a potential intersection.
			if (Math.abs(localPos.y) < radius)
			{
				//now to do a line/circle intersection test. The center of the 
				//circle is represented by A, B. The intersection points are 
				//given by the formulae x = A +/-sqrt(r^2-B^2), y=0. We only 
				//need to look at the smallest positive value of x.
				double x = localPos.x;
				double y = localPos.y;       

				double ip = x - Math.sqrt(radius * radius - y * y);

				if (ip <= 0)
				{
					ip = x + Math.sqrt(radius * radius - y * y);
				}

				return a.add(toBNorm.mul(ip));
			}
		}

		return null;
	}

	public static boolean lineIntersection2D(Vector2D A, Vector2D B, Vector2D C, Vector2D D, Double distToThisIP, Vector2D point) {
		double rTop = (A.y-C.y)*(D.x-C.x)-(A.x-C.x)*(D.y-C.y);
		double rBot = (B.x-A.x)*(D.y-C.y)-(B.y-A.y)*(D.x-C.x);

		double sTop = (A.y-C.y)*(B.x-A.x)-(A.x-C.x)*(B.y-A.y);
		double sBot = (B.x-A.x)*(D.y-C.y)-(B.y-A.y)*(D.x-C.x);

		if ( (rBot == 0) || (sBot == 0))
		{
			//lines are parallel
			return false;
		}

		double r = rTop/rBot;
		double s = sTop/sBot;

		if( (r > 0) && (r < 1) && (s > 0) && (s < 1) )
		{
			distToThisIP = A.distance(B) * r;

			point = A.add(B.sub(A).mul(r));

			return true;
		}

		else
		{
			distToThisIP = 0.0;

			return false;
		}
	}

	public static Double FindClosestPointOfIntersectionWithWalls(Vector2D A, Vector2D B, Vector2D impactPoint, List<Wall2D> walls) {
		if(A == null || B == null || impactPoint == null || walls == null) {
			Log.error("Geometry", "FindClosestPointToWalls - Null value passed.");
		}
		double distance = Double.MAX_VALUE;
		
		for (Wall2D wall : walls)
		{
			double dist = 0.0;
			Vector2D point = new Vector2D();

			if (lineIntersection2D(A, B, wall.from(), wall.to(), dist, point))
			{
				if (dist < distance)
				{
					distance = dist;
					if(impactPoint == null) impactPoint = new Vector2D();
					impactPoint.setValue(point);
				}
			}
		}

		if (distance < Double.MAX_VALUE)
			return distance;

		return null;
	}

	public static Vector2D GetLineSegmentCircleClosestIntersectionPoint(Vector2D A, Vector2D B, Vector2D pos, double radius) {
		  Vector2D toBNorm = new Vector2D(B.sub(A));
		  toBNorm.normalize();

		  //move the circle into the local space defined by the vector B-A with origin
		  //at A
		  Vector2D LocalPos = Transformations.pointToLocalSpace(pos, toBNorm, toBNorm.perp(), A);

		  //if the local position + the radius is negative then the circle lays behind
		  //point A so there is no intersection possible. If the local x pos minus the 
		  //radius is greater than length A-B then the circle cannot intersect the 
		  //line segment
		  if ( (LocalPos.x+radius >= 0) &&
		     ( (LocalPos.x-radius)*(LocalPos.x-radius) <= B.distanceSq(A)) )
		  {
		     //if the distance from the x axis to the object's position is less
		     //than its radius then there is a potential intersection.
		     if (Math.abs(LocalPos.y) < radius)
		     {
		        //now to do a line/circle intersection test. The center of the 
		        //circle is represented by A, B. The intersection points are 
		        //given by the formulae x = A +/-sqrt(r^2-B^2), y=0. We only 
		        //need to look at the smallest positive value of x.
		        double a = LocalPos.x;
		        double b = LocalPos.y;       

		        double ip = a - Math.sqrt(radius*radius - b*b);

		        if (ip <= 0)
		        {
		          ip = a + Math.sqrt(radius*radius - b*b);
		        }

		        return A.add(toBNorm.mul(ip));
		     }
		   }
		  
		  return null;
	}
}
