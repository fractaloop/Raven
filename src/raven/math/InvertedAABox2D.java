package raven.math;

import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class InvertedAABox2D {
	private Vector2D topLeft;
	private Vector2D bottomRight;
	
	private Vector2D center;
	
	public InvertedAABox2D(Vector2D tl, Vector2D br) {
		topLeft = tl;
		bottomRight = br;
		
		center = tl.add(br).mul(0.5);
	}
	
	public boolean isOverlappedWith(InvertedAABox2D other) {
		return !((other.top() > this.bottom()) ||
				(other.bottom() < this.top()) ||
				(other.left() > this.right()) || 
				(other.right() < this.left()));
	}
	
	public Vector2D topLeft() {
		return topLeft;
	}
	
	public Vector2D bottomRight() {
		return bottomRight;
	}
	
	public double top() { return topLeft.y; }
	public double left() { return topLeft.x; }
	public double bottom() { return bottomRight.y; }
	public double right() { return bottomRight.x; }
	public Vector2D center() { return center; }
	
	public void render(boolean renderCenter) {
		GameCanvas.line(left(), top(), right(), top());
		GameCanvas.line(left(), bottom(), right(), bottom());
		GameCanvas.line(left(), top(), left(), bottom());
		GameCanvas.line(right(), top(), right(), bottom());
		
		if (renderCenter) {
			GameCanvas.circle(center, 5);
		}
	}
}
