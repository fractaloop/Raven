package raven.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transformations {

	public static void Vec2DRotateAroundOrigin(Vector2D v, double angle) {
		C2DMatrix mat = new C2DMatrix();
		
		mat.rotate(angle);
		
		mat.transformVector2Ds(v);
	}
	
	public static List<Vector2D> WorldTransform(List<Vector2D> points, Vector2D pos, Vector2D forward, Vector2D side, Vector2D scale) {
		List<Vector2D> results = new ArrayList<Vector2D>(points.size());
		for (Vector2D point : points) {
			results.add(new Vector2D(point));
		}
		
		
		C2DMatrix matTransform = new C2DMatrix();
		
		// scale
		if (scale.x != 1.0 || scale.y != 1.0) {
			matTransform.scale(scale.x, scale.y);
		}
		
		// rotate
		matTransform.rotate(forward, side);
		
		// and translate
		matTransform.translate(pos.x, pos.y);
		
		// now transform
		matTransform.transformVector2Ds(results);
		
		return results;
	}

	public static Vector2D pointToLocalSpace(Vector2D pos, Vector2D agentHeading, Vector2D agentSide, Vector2D agentPosition) {

		// make a copy of the point
		Vector2D transPoint = new Vector2D(pos);

		// create a transformation matrix
		C2DMatrix matTransform = new C2DMatrix();

		double Tx = -agentPosition.dot(agentHeading);
		double Ty = -agentPosition.dot(agentSide);

		// create the transformation matrix
		matTransform._11(agentHeading.x);
		matTransform._12(agentSide.x);
		matTransform._21(agentHeading.y);
		matTransform._22(agentSide.y);
		matTransform._31(Tx);
		matTransform._32(Ty);

		// now transform the vertices
		matTransform.transformVector2Ds(transPoint);

		return transPoint;
	}
}
