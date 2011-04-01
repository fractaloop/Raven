package raven.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import raven.math.Transformations;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class GraveMarkers {
	private class GraveRecord {
		public Vector2D position;
		public double timeLeft;
		
		public GraveRecord(Vector2D position) {
			this.position = position;
			timeLeft = lifetime;
		}
	}
	
	/** how long a grave remains on screen */
	private double lifetime;
	
	/** when a bot dies, a grave is rendered to mark the spot. */
	private List<Vector2D> vecRIPVB;
	private List<Vector2D> vecRIBVBTrans;
	
	private List<GraveRecord> graveList = new LinkedList<GraveRecord>();
	
	
	public GraveMarkers(double lifetime) {
		this.lifetime = lifetime;
		
		vecRIPVB = new ArrayList<Vector2D>();
		vecRIPVB.add(new Vector2D(-4, -5));
		vecRIPVB.add(new Vector2D(-4, 3));
		vecRIPVB.add(new Vector2D(-3, 5));
		vecRIPVB.add(new Vector2D(-1, 6));
		vecRIPVB.add(new Vector2D(1, 6));
		vecRIPVB.add(new Vector2D(3, 5));
		vecRIPVB.add(new Vector2D(4, 3));
		vecRIPVB.add(new Vector2D(4, -5));
		vecRIPVB.add(new Vector2D(-4, -5));
	}

	public void update(double delta) {
		long currentTime = System.nanoTime();
		
		Set<GraveRecord> toRemove = new HashSet<GraveRecord>();
		for (GraveRecord grave : graveList) {
			grave.timeLeft -= delta;
			if (grave.timeLeft <= 0) {
				toRemove.add(grave);
			}
		}
		graveList.removeAll(toRemove);
	}

	public void render() {
		final Vector2D facing = new Vector2D(-1, 0);
		final Vector2D scale = new Vector2D(1, 1);
		
		for (GraveRecord grave : graveList) {
			vecRIBVBTrans = Transformations.WorldTransform(vecRIPVB, grave.position, facing, facing.perp(), scale);
			
			GameCanvas.brownPen();
			GameCanvas.closedShape(vecRIBVBTrans);
			GameCanvas.textAtPos(grave.position.x - 10, grave.position.y - 5, "RIP");
		}
	}

	public void addGrave(Vector2D pos) {
		graveList.add(new GraveRecord(pos));
	}

}
