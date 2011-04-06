package raven.math;

import java.util.List;

public class Transformations {

	public static void Vec2DRotateAroundOrigin(Vector2D v, double angle) {
		C2DMatrix mat = new C2DMatrix();
		
		mat.rotate(angle);
		
		mat.transformVector2Ds(v);
	}
	
	public static List<Vector2D> WorldTransform(List<Vector2D> vecBotVB,
			Vector2D pos, Vector2D facing, Vector2D perp, Vector2D scale) {
		// TODO Auto-generated method stub
		return null;
	}
}
