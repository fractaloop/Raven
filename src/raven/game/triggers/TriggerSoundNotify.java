package raven.game.triggers;

import raven.game.interfaces.IRavenBot;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.script.RavenScript;

public class TriggerSoundNotify extends TriggerLimitedLifetime<IRavenBot> {
	
	private IRavenBot soundSource;
	
	public TriggerSoundNotify(IRavenBot source, double range) {
		super(source.pos(), (int)range, RavenScript.getDouble("Bot_TriggerUpdateFreq"), null);
		
		soundSource = source;
		
		// set position and range
		setPos(soundSource.pos());
		setBRadius(range);
		
		// create and set this trigger's region of influence
		addCircularTriggerRegion(pos(), getBRadius());
	}
	
	@Override
	public void tryTrigger(IRavenBot entity) {
		if (isTouchingTrigger(entity.pos(), entity.getBRadius())
				&& entity.isReadyForTriggerUpdate() && entity.isAlive()) {
			Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
					Dispatcher.SENDER_ID_IRRELEVANT,
					entity.ID(),
					RavenMessage.MSG_GUNSHOT_SOUND, soundSource);
		}

	}

	/** Sound triggers are invisible */
	@Override
	public void render() {}

}
