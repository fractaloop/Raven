package raven.math;

import java.util.List;

public class C2DMatrix {
	private class Matrix {
		double _11, _12, _13;
		double _21, _22, _23;
		double _31, _32, _33;

		Matrix() {
			_11 = _12 = _13 = 0.0;
			_21 = _22 = _23 = 0.0;
			_31 = _32 = _33 = 0.0;
		}
	}

	Matrix matrix;

	private void matrixMultiply(Matrix in) {
		Matrix tempMat = new Matrix();

		tempMat._11 = (matrix._11 * in._11) + (matrix._12 * in._21) + (matrix._13 * in._31);
		tempMat._12 = (matrix._11 * in._12) + (matrix._12 * in._22) + (matrix._13 * in._32);
		tempMat._13 = (matrix._11 * in._13) + (matrix._12 * in._23) + (matrix._13 * in._33);

		tempMat._21 = (matrix._21 * in._11) + (matrix._22 * in._21) + (matrix._23 * in._31);
		tempMat._22 = (matrix._21 * in._12) + (matrix._22 * in._22) + (matrix._23 * in._32);
		tempMat._23 = (matrix._21 * in._13) + (matrix._22 * in._23) + (matrix._23 * in._33);

		tempMat._31 = (matrix._31 * in._11) + (matrix._32 * in._21) + (matrix._33 * in._31);
		tempMat._32 = (matrix._31 * in._12) + (matrix._32 * in._22) + (matrix._33 * in._32);
		tempMat._33 = (matrix._31 * in._13) + (matrix._32 * in._23) + (matrix._33 * in._33);

		matrix = tempMat;		
	}

	public C2DMatrix() {
		matrix = new Matrix();
		identity();
	}

	public void identity() {
		matrix._11 = matrix._22 = matrix._33 = 1;
		matrix._12 = matrix._13 = matrix._21 = matrix._23 = matrix._31 = matrix._32 = 0;
	}

	// create a transformation matrix
	public void translate(double x, double y) {
		Matrix mat = new Matrix();

		mat._11 = 1;
		mat._22 = 1;
		mat._31 = x;
		mat._32 = y;
		mat._33 = 1;
		

		matrixMultiply(mat);
	}

	// create a scale matrix
	public void scale(double xScale, double yScale) {
		Matrix mat = new Matrix();

		mat._11 = xScale;
		mat._22 = yScale;
		mat._33 = 1;

		matrixMultiply(mat);
	}

	// create a rotation matrix
	public void rotate(double rotation) {
		Matrix mat = new Matrix();

		double sin = Math.sin(rotation);
		double cos = Math.cos(rotation);

		mat._11 = cos;
		mat._12 = sin;
		mat._21 = -sin;
		mat._22 = cos;
		mat._33 = 1;

		matrixMultiply(mat);
	}

	// create a rotation matrix from a fwd and side 2D vector
	public void rotate(Vector2D fwd, Vector2D side) {
		Matrix mat = new Matrix();

		mat._11 = fwd.x;
		mat._12 = fwd.y;
		mat._21 = side.x;
		mat._22 = side.y;
		mat._33 = 1;

		matrixMultiply(mat);
	}

	// applies a transformation matrix to a list of points
	public void transformVector2Ds(List<Vector2D> points) {
		for (Vector2D point : points) {
			double tempX = (matrix._11 * point.x) + (matrix._21 * point.y) + matrix._31; 
			double tempY = (matrix._12 * point.x) + (matrix._22 * point.y) + matrix._32;
			point.x = tempX;
			point.y = tempY;
		}
	}

	// applies a transformation matrix to a point
	public void transformVector2Ds(Vector2D point) {
		double tempX = (matrix._11 * point.x) + (matrix._21 * point.y) + matrix._31; 
		double tempY = (matrix._12 * point.x) + (matrix._22 * point.y) + matrix._32;
		point.x = tempX;
		point.y = tempY;
	}

	// Accssors

	public void _11(double val) { matrix._11 = val; }
	public void _12(double val) { matrix._12 = val; }
	public void _13(double val) { matrix._13 = val; }
	public void _21(double val) { matrix._21 = val; }
	public void _22(double val) { matrix._22 = val; }
	public void _23(double val) { matrix._23 = val; }
	public void _31(double val) { matrix._31 = val; }
	public void _32(double val) { matrix._32 = val; }
	public void _33(double val) { matrix._33 = val; }	  
}
