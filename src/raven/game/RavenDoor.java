package raven.game;

import java.util.LinkedList;
import java.util.List;

import raven.math.Vector2D;
import raven.math.Wall2D;
import raven.ui.GameCanvas;

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
			status = Status.CLOSED;
			return;
		}
		
		// reduce the current size
		currentSize += 1.0/60;
		
		currentSize = Math.min(Math.max(0, currentSize), size);
		
		changePosition(p1, p1.add(vectorToP2Norm).mul(currentSize));
	}
	
	protected void changePosition(Vector2D newP1, Vector2D newP2) {
		// TODO
	}
	
	public RavenDoor(int id, Vector2D pos1, Vector2D pos2, int timeout) {
		super(id);

		status = Status.CLOSED;
		numSecondsStayOpen = timeout;
		
		vectorToP2Norm = pos2.sub(pos1);
		vectorToP2Norm.normalize();
		currentSize = size = pos2.distance(pos1);
	}
	
	public void addSwitch(int ID) {
		switches.add(ID);
	}
	
	List<Integer> getSwitchIDs() {
		return switches;
	}

	@Override
	public void render() {
		if(status == Status.OPEN) return;
		
		GameCanvas.brownPen();
		//TODO: change length of drawn line pased on state(closing/opening/open/closed
		if(status == Status.CLOSED) GameCanvas.line(p1, p2);
	}
}
