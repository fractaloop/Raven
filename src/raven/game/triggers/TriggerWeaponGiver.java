package raven.game.triggers;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import raven.game.RavenBot;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.StreamUtils;

public class TriggerWeaponGiver extends TriggerRespawning<RavenBot> {
	private List<Vector2D> vecRLVB;
	private List<Vector2D> vecRLVBTrans;
	
	public TriggerWeaponGiver(Reader reader) {
		super((Integer)StreamUtils.getValueFromStream(reader));
		
		read(reader);

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
	}

	@Override
	public void tryTrigger(RavenBot entity) {
		if (this.isActive() && this.isTouchingTrigger(entity.pos(), entity.getBRadius())) {
			entity.getWeaponSys().addWeapon(this.entityType());
			
			this.deactivate();
		}
	}

	@Override
	public void render() {
		if (this.isActive()) {
			switch(this.entityType()) {
			case RAIL_GUN:
				GameCanvas.bluePen();
				GameCanvas.blueBrush();
				GameCanvas.circle(pos(), 3);
				GameCanvas.line(pos(), new Vector2D(pos().x, pos().y - 9));
				break;
			case SHOTGUN:
				GameCanvas.blackBrush();
				GameCanvas.brownPen();
				final double size = 3;
				GameCanvas.circle(pos().x - size, pos().y, size);
				GameCanvas.circle(pos().x + size, pos().y, size);
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
	
	@Override
	public void read(Reader reader) {
		double x, y, r;
		int graphNodeIndex;
		
		x = (Double)StreamUtils.getValueFromStream(reader);
		y = (Double)StreamUtils.getValueFromStream(reader);
		r = (Double)StreamUtils.getValueFromStream(reader);
		graphNodeIndex = (Integer)StreamUtils.getValueFromStream(reader);
		
		setPos(new Vector2D(x, y));
		setBRadius(r);
		setGraphNodeIndex(graphNodeIndex);
		
		// create this trigger's region of fluence
		addCircularTriggerRegion(pos(), RavenScript.getDouble("DefaultGiverTriggerRange"));
		
		setRespawnDelay(RavenScript.getDouble("Weapon_RespawnDelay"));
		
	}
}
