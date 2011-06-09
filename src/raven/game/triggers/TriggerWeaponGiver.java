package raven.game.triggers;

import java.util.ArrayList;
import java.util.List;

import raven.game.RavenBot;
import raven.game.interfaces.IRavenBot;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TriggerWeaponGiver")
public class TriggerWeaponGiver extends TriggerRespawning<IRavenBot> {
	transient private List<Vector2D> vecRLVB = new ArrayList<Vector2D>(8);
	transient private List<Vector2D> vecRLVBTrans;
	
	/**
	 * Creates a new weapon giver trigger with a certain position and with a certain radius of activation
	 * @param position The position of the center point of the trigger
	 * @param radius The radius of the activation zone for this  trigger
	 */
	public TriggerWeaponGiver(Vector2D position, int radius) {
		super(position, radius);
	
		// have to multiply by 1000 because we measure game events in milliseconds.
		setRespawnDelay(RavenScript.getInt("Weapon_RespawnDelay")*1000);

		vecRLVB.add(new Vector2D(0, 3));
		vecRLVB.add(new Vector2D(1, 2));
		vecRLVB.add(new Vector2D(1, 0));
		vecRLVB.add(new Vector2D(2, -2));
		vecRLVB.add(new Vector2D(-2, -2));
		vecRLVB.add(new Vector2D(-1, 0));
		vecRLVB.add(new Vector2D(-1, 2));
		vecRLVB.add(new Vector2D(0, 3));
	}

	@Override
	public void tryTrigger(IRavenBot entity) {
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
				if(vecRLVB == null) return;
				Vector2D facing = new Vector2D(-1, 0);
				vecRLVBTrans = Transformations.WorldTransform(vecRLVB, pos(), facing, facing.perp(), new Vector2D(2.5, 2.5));
				GameCanvas.redPen();
				GameCanvas.closedShape(vecRLVBTrans);
				break;
			}
		}
		
	}
}
