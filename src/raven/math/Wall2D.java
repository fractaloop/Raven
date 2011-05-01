package raven.math;

import java.io.IOException;
import java.io.Writer;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.ui.GameCanvas;

@XStreamAlias("Wall2D")
public class Wall2D {
	@XStreamAlias("from")
	protected Vector2D vA;
	@XStreamAlias("to")
	protected Vector2D vB;
	transient protected Vector2D vN;
	
	private Object readResolve() {
		calculateNormal();
		
		return this;
	}
	protected void calculateNormal() {
		Vector2D temp = vB.sub(vA);
		temp.normalize();
		
		vN = new Vector2D(-temp.y, temp.x);
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
