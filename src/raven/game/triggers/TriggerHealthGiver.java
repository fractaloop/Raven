package raven.game.triggers;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.game.interfaces.IRavenBot;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.Log;

@XStreamAlias("TriggerHealthGiver")
public class TriggerHealthGiver extends TriggerRespawning<IRavenBot> {
	private int healthGiven;
	private static int respawnDelay = RavenScript.getInt("Health_RespawnDelay")*1000;
	
	/**
	 * Creates a new health giver trigger with a certain position, with a certain radius of activation, 
	 * and a certain amount of health given.
	 * @param position The position of the center point of the trigger
	 * @param radius The radius of the activation zone for this  trigger
	 * @param healthGiven The amount of health given to a bot that triggers this trigger.
	 */
	public TriggerHealthGiver( Vector2D position, int radius, int healthGiven) {
		super(position, radius);
		
		// have to multiply by 1000 because we measure game events in milliseconds.
		setRespawnDelay(respawnDelay);

		this.healthGiven = healthGiven;
		setEntityType(RavenObject.HEALTH);
		addCircularTriggerRegion(position, RavenScript.getDouble("DefaultGiverTriggerRange"));
	}

	@Override
	/**
	 * Health triggers give the defined amount of health to the bot that is touching it, if the trigger is active, and the bot is ready and alive.
	 */
	public void tryTrigger(IRavenBot bot) {
		if (isActive() && isTouchingTrigger(bot.pos(), bot.getBRadius()) && bot.isReadyForTriggerUpdate() && bot.isAlive()) {
			bot.increaseHealth(healthGiven);
			Log.debug("HealthGiver", "Added health to a bot. I should disappear now.");
			this.deactivate();
		}
	}

	@Override
	public void render() {
		if (isActive()) {
			GameCanvas.blackPen();
			GameCanvas.whiteBrush();
			final int size = 5;
			GameCanvas.rect(pos().x - size, pos().y - size, pos().x + size, pos().y + size);
			GameCanvas.redPen();
			GameCanvas.line(pos().x, pos().y - size, pos().x, pos().y + size + 1);
			GameCanvas.line(pos().x - size, pos().y, pos().x+size + 1, pos().y);
		}
	}
}
