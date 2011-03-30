package raven.game.triggers;

import java.io.Reader;

import raven.game.RavenBot;
import raven.ui.GameCanvas;
import raven.utils.StreamUtils;

public class TriggerHealthGiver extends TriggerRespawning<RavenBot> {
	private int healthGiven;
	
	public TriggerHealthGiver(Reader datafile) {
		super((Integer)StreamUtils.getValueFromStream(datafile));
		
		read(datafile);
	}

	@Override
	public void tryTrigger(RavenBot bot) {
		if (isActive() && isTouchingTrigger(bot.pos(), bot.getBRadius())) {
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
	
	@Override
	public void read(Reader reader) {
		// TODO
	}
}
