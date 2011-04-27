package raven.game.triggers;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.game.RavenBot;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

@XStreamAlias("TriggerWeaponGiver")
public class TriggerWeaponGiver extends TriggerRespawning<RavenBot> {
	transient private List<Vector2D> vecRLVB;
	transient private List<Vector2D> vecRLVBTrans;
	
	public TriggerWeaponGiver(Vector2D position, int radius, int respawnDelay) {
		super(position, radius);
		
		setRespawnDelay(respawnDelay);

		readResolve();
	}
	
	private Object readResolve() {
		// create the vertex buffer for the rocket shape
		vecRLVB = new ArrayList<Vector2D>(8);
		vecRLVB.add(new Vector2D(0, 3));
		vecRLVB.add(new Vector2D(1, 2));
		vecRLVB.add(new Vector2D(1, 0));
		vecRLVB.add(new Vector2D(2, -2));
		vecRLVB.add(new Vector2D(-2, -2));
		vecRLVB.add(new Vector2D(-1, 0));
		vecRLVB.add(new Vector2D(-1, 2));
		vecRLVB.add(new Vector2D(0, 3));
		
		return this;
	}

	@Override
	public void tryTrigger(RavenBot entity) {
		if (this.isActive() && this.isTouchingTrigger(entity.pos(), entity.getBRadius())
				&& entity.isReadyForTriggerUpdate() && entity.isAlive()) {
			entity.getWeaponSys().addWeapon(this.entityType());
			this.deactivate();
		}
	}

	@Override
	public void render() {
		if (this.isActive() && pos() != null) {
			switch(this.entityType()) {
			case RAIL_GUN:
				GameCanvas.bluePen();
				GameCanvas.blueBrush();
				GameCanvas.filledCircle(pos(), 3);
				GameCanvas.line(pos(), new Vector2D(pos().x, pos().y - 9));
				break;
			case SHOTGUN:
				GameCanvas.blackBrush();
				GameCanvas.brownPen();
				final double size = 3;
				GameCanvas.filledCircle(pos().x - size, pos().y, size);
				GameCanvas.filledCircle(pos().x + size, pos().y, size);
				break;
			case ROCKET_LAUNCHER:
				Vector2D facing = new Vector2D(-1, 0);
				vecRLVBTrans = Transformations.WorldTransform(vecRLVB, pos(), facing, facing.perp(), new Vector2D(2.5, 2.5));
				GameCanvas.redPen();
				GameCanvas.closedShape(vecRLVBTrans);
				break;
			}
		}
		
	}
}
