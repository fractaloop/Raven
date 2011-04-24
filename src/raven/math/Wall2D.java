package raven.math;

import java.io.IOException;
import java.io.Writer;

import raven.ui.GameCanvas;

public class Wall2D {
	protected Vector2D vA, vB, vN;
	
	protected void calculateNormal() {
		Vector2D temp = vB.sub(vA);
		temp.normalize();
		
		vN = new Vector2D(-temp.x, temp.y);
	}
	
	public Wall2D() {}
	
	public Wall2D(Vector2D a, Vector2D b) {
		vA = new Vector2D(a);
		vB = new Vector2D(b);
		
		calculateNormal();
	}
	
	public Wall2D(Vector2D a, Vector2D b, Vector2D n) {
		vA = a;
		vB = b;
		vN = n;
	}
	
	public void render() {
		render(false);
	}
	
	public void render(boolean renderNormals) {
		GameCanvas.line(vA, vB);
		
		if (renderNormals) {
			int midX = (int)((vA.x + vB.x) / 2);
			int midY = (int)((vA.y + vB.y) / 2);
			
			GameCanvas.line(midX, midY, (int)(midX + (vN.x * 5)), (int)(midY + (vN.y * 5)));
		}
	}
	
	// Accessors
	
	public Vector2D from() { return vA; }
	public void setFrom(Vector2D v) { vA = v; calculateNormal(); }
	
	public Vector2D to() { return vB; }
	public void setTo(Vector2D v) { vB = v; calculateNormal(); }
	
	public Vector2D normal() { return vN; }
	public void setNormal(Vector2D v) { vN = v; calculateNormal(); }
	
	public Vector2D center() { return vA.add(vB).mul(0.5); }
	
	public Writer write(Writer writer) throws IOException {
		writer.write("\n" + from() + "," + to() + "," + normal());
		
		return writer;
	}
}
