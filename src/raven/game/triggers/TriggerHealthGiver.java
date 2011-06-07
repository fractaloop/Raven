package raven.game.triggers;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.utils.Log;

@XStreamAlias("TriggerHealthGiver")
public class TriggerHealthGiver extends TriggerRespawning<RavenBot> {
	private int healthGiven;
	
	public TriggerHealthGiver( Vector2D position, int radius, int healthGiven, int respawnDelay) {
		super(position, radius);
		
		// have to multiply by 1000 because we measure game events in milliseconds.
		setRespawnDelay(RavenScript.getInt("Health_RespawnDelay")*1000);

		this.healthGiven = healthGiven;
		setEntityType(RavenObject.HEALTH);
		addCircularTriggerRegion(position, RavenScript.getDouble("DefaultGiverTriggerRange"));
	}

	@Override
	/**
	 * Health triggers give the defined amount of health to the bot that is touching it, if the trigger is active, and the bot is ready and alive.
	 */
	public void tryTrigger(RavenBot bot) {
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
