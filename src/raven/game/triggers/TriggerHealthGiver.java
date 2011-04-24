package raven.game.triggers;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class TriggerHealthGiver extends TriggerRespawning<RavenBot> {
	private int healthGiven;
	
	public TriggerHealthGiver( Vector2D position, int radius, int healthGiven) {
		super(position, radius);
		this.healthGiven = healthGiven;
	}

	@Override
	public void tryTrigger(RavenBot bot) {
		if (isActive() && isTouchingTrigger(bot.pos(), bot.getBRadius())
				&& bot.isReadyForTriggerUpdate() && bot.isAlive()) {
			bot.increaseHealth(healthGiven);
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
