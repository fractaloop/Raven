package raven.math;

/**
 * 2D Vector class
 * 
 * Ported from Mat Buckland's "Programming Game AI by Example" 
 * 
 * @author Logan Lowell
 */
public class Vector2D {
	private double x, y;
	
	public Vector2D() {
		x = y = 0;
	}
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/** Set x and y to zero */
	public void Zero() {
		x = y = 0;
	}
	
	/** Returns true if both x and y are zero */
	public boolean isZero() {
		return (x * x + y * y) < Double.MIN_NORMAL;
	}
	
	/**
	 * Calculate the length of the vector
	 * @return the length of the vector
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Calculate the length squared of the vector (avoid the square root)
	 * @return the squared length of the vector
	 */
	public double lengthSq() {
		return x * x + y * y;
	}
	
	/** Resize the vector to length 1 */
	public void normalize() {
		double vector_length = length();
		
		if (vector_length > Double.MIN_VALUE) {
			x /= vector_length;
			y /= vector_length;
		}
	}
	
	/**
	 * Compute the dot product between this vector and another.
	 * @param v2 the vector to compare to
	 * @return the dot product of the two vectors
	 */
	public double dot(final Vector2D v2) {
		return x * v2.x + y * v2.y;
	}
	
	public static final int ANTICLOCKWISE = 1;
	public static final int CLOCKWISE = 1;
	/**
	 * Calculates if another vector is clockwise or anticlockwise from this
	 * vector. This assumes the Y-axis points down and the X-axis points to
	 * the right, as in a JPanel.
	 * 
	 * @param v2 the vector to be tested
	 * @return positive if v2 is clockwise of this vector, negative if
	 * anticlockwise
	 */
	public int sign(final Vector2D v2) {
		if (y * v2.x > x * v2.y) {
			return ANTICLOCKWISE;
		} else {
			return CLOCKWISE;
		}
	}
	
	/** Return a vector perpendicular to this one */
	public Vector2D perp() {
		return new Vector2D(-y, x);
	}
	
	/** Adjust x and y so the length is not greater than max */
	public void truncate(double max) {
		if (length() > max) {
			normalize();
			
			Vector2D result = mul(max);
			x = result.x;
			y = result.y;
		}
	}
	
	/**
	 * Calculate the distance from this vector to another.
	 * @param v2 the vector to find the distance to
	 * @return the distance
	 */
	public double distance(final Vector2D v2) {
		double dx = x - v2.x;
		double dy = y - v2.y;
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Calculate the squared distance from this vector to another. This avoids
	 * an expensive square root. 
	 * @param v2 the vector to find the squared distance to
	 * @return the squared distance
	 */
	public double distanceSq(final Vector2D v2) {
		double dx = x - v2.x;
		double dy = y - v2.y;
		
		return dx * dx + dy * dy;
	}
	
	/**
	 * given a normalized vector this method reflects the vector it
	 * is operating upon. (like the path of a ball bouncing off a wall)
	 * @param norm a vector representing the axis to flip over
	 */
	public void reflect(final Vector2D norm) {
		Vector2D result = add(getReverse().mul(dot(norm)).mul(2.0));
		x = result.x;
		y = result.y;
	}
	
	/**
	 * Find the vector that is the reverse of this one
	 * @return the reverse of this vector
	 */
	public Vector2D getReverse() {
		return new Vector2D(-x, -y);
	}
	
	/*
	 * Operators... I wish we could do some operator overloading right now!
	 */

	public Vector2D add(final Vector2D v2) {
		return new Vector2D(x + v2.x, y + v2.y);
	}

	public Vector2D sub(final Vector2D v2) {
		return new Vector2D(x - v2.x, y - v2.y);
	}
	
	public Vector2D mul(double scalar) {
		return new Vector2D(x * scalar, y * scalar);
	}
	
	public Vector2D div(double scalar) {
		return mul(1 / scalar);
	}
}
