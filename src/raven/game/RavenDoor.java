package raven.game;

import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import raven.game.messaging.Telegram;
import raven.math.Vector2D;
import raven.math.Wall2D;
import raven.utils.StreamUtils;

public class RavenDoor extends BaseGameEntity {
	protected enum Status {
		OPEN,
		OPENING,
		CLOSED,
		CLOSING
	}
	
	protected Status status;
	
	/** a sliding door is created from two walls, back to back. These walls
	 * must be added to a map's geometry in order for an agent to detect them.
	 */
	protected Wall2D wall1;
	protected Wall2D wall2;
	
	/** a container of the id's of the triggers able to open this door */
	protected LinkedList<Integer> switches;
	
	protected float numSecondsStayOpen;
	
	protected float numSecondsCurrentlyOpen;
	
	protected Vector2D p1;
	protected Vector2D p2;
	protected double size;
	
	protected Vector2D vectorToP2Norm;
	
	protected double currentSize;
	
	protected void open() {
		if (status == Status.OPENING) {
			if (currentSize < 2) {
				status = Status.OPEN;
				
				numSecondsCurrentlyOpen = numSecondsStayOpen;
				
				return;
			}
		}
		
		// reduce the current size
		currentSize -= 1.0/60;
		
		currentSize = Math.min(Math.max(0, currentSize), size);
		
		changePosition(p1, p1.add(vectorToP2Norm).mul(currentSize));
	}
	
	protected void close() {
		if (status == Status.CLOSING) {
			if (currentSize == currentSize) {
				status = Status.CLOSED;
				return;
			}
		}
		
		// reduce the current size
		currentSize += 1.0/60;
		
		currentSize = Math.min(Math.max(0, currentSize), size);
		
		changePosition(p1, p1.add(vectorToP2Norm).mul(currentSize));
	}
	
	protected void changePosition(Vector2D newP1, Vector2D newP2) {
		// TODO
	}
	
	public RavenDoor(RavenMap map, Reader reader) {
		super((Integer)StreamUtils.getValueFromStream(reader));

		status = Status.CLOSED;
		numSecondsStayOpen = 1;
		
		read(reader);
		
		vectorToP2Norm = p2.sub(p1);
		vectorToP2Norm.normalize();
		currentSize = size = p2.distance(p1);
	}
	
	public void addSwitch(int ID) {
		
	}
	
	List<Integer> getSwitchIDs() {
		return switches;
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(double delta) {
		// TODO
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		// TODO
		return false;
	}
	
	public void read(Reader readaer) {
		// TODO
	}
}
