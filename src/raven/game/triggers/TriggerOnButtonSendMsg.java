package raven.game.triggers;

import raven.game.BaseGameEntity;
import raven.game.interfaces.IRavenBot;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class TriggerOnButtonSendMsg<T extends IRavenBot> extends Trigger<T> {
	/** when triggered a message is sent to the entity with the given ID */
	private int receiver;
	
	/** the message that is sent */
	private RavenMessage msgToSend;
	
	/**
	 * When triggered, send a message to a target, specified by ID
	 * @param id this Trigger's ID
	 * @param topLeft  
	 * @param bottomRight
	 * @param message Message to send
	 * @param target Target to send the message to
	 */
	public TriggerOnButtonSendMsg(Vector2D topLeft, Vector2D bottomRight, RavenMessage message, int target) {
		super(null, 0);
		addRectangularTriggerRegion(topLeft, bottomRight);
		msgToSend = message;
		
	}

	@Override
	public void tryTrigger(T entity) {
		if (isTouchingTrigger(entity.pos(), entity.getBRadius())) {
			Dispatcher.dispatchMsg(Dispatcher.SEND_MSG_IMMEDIATELY,
					this.ID(),
					receiver,
					msgToSend,
					Dispatcher.NO_ADDITIONAL_INFO);
		}
	}
	
	@Override
	public void update(double delta) { }

	@Override
	public void render() {
		GameCanvas.orangePen();
		
		double size = getBRadius();
		
		GameCanvas.line(pos().x - size, pos().y - size, pos().x + size, pos().y - size);
		GameCanvas.line(pos().x + size, pos().y - size, pos().x + size, pos().y + size);
		GameCanvas.line(pos().x + size, pos().y + size, pos().x - size, pos().y + size);
		GameCanvas.line(pos().x - size, pos().y + size, pos().x - size, pos().y - size);
	}
}
