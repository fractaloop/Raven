package raven.game.triggers;

import java.io.Reader;
import java.io.Writer;

import raven.game.BaseGameEntity;
import raven.game.messaging.Dispatcher;
import raven.game.messaging.RavenMessage;
import raven.game.messaging.Telegram;
import raven.math.Vector2D;
import raven.ui.GameCanvas;
import raven.utils.StreamUtils;

public class TriggerOnButtonSendMsg<T extends BaseGameEntity> extends Trigger<T> {
	/** when triggered a message is sent to the entity with the given ID */
	private int receiver;
	
	/** the message that is sent */
	private RavenMessage msgToSend;
	
	public TriggerOnButtonSendMsg(Reader reader) {
		super((Integer)StreamUtils.getValueFromStream(reader));
		
		read(reader);
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
	
	@Override
	public void write(Writer write) {
	}
	
	@Override
	public void read(Reader reader) {
		receiver = (Integer)StreamUtils.getValueFromStream(reader);
		
		// TODO This needs fixing but I don't think we have to worry for now
		msgToSend = (RavenMessage)StreamUtils.getValueFromStream(reader);
		
		// Graph the position and radius
		double x, y, r;
		x = (Double)StreamUtils.getValueFromStream(reader);
		y = (Double)StreamUtils.getValueFromStream(reader);
		r = (Double)StreamUtils.getValueFromStream(reader);
		
		setPos(new Vector2D(x, y));
		setBRadius(r);
		
		// Create and set this trigger's region of influence
		addRectangularTriggerRegion(pos().sub(new Vector2D(r, r)), pos().add(new Vector2D(r, r)));
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
	
}
