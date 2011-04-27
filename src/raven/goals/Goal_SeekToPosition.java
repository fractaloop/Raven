package raven.goals;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class Goal_SeekToPosition extends GoalComposite<RavenBot> {
	Vector2D  m_vPosition;

	//the approximate time the bot should take to travel the target location
	double     m_dTimeToReachPos;

	//this records the time this goal was activated
	double     m_dStartTime;

	//returns true if a bot gets stuck
	// dont know how to detect this atm.
	boolean isStuck(){
		double TimeTaken = System.nanoTime()*1000 - m_dStartTime;

		if (TimeTaken > m_dTimeToReachPos) {
			System.out.println("BOT "  + m_pOwner.ID() + " IS STUCK!!");
			return true;
		}
		return false;
	}
	
	public Goal_SeekToPosition(RavenBot rbot, Vector2D target) {
		super(rbot, Goal.GoalType.goal_seek_to_position);
		m_vPosition = target;
		m_dTimeToReachPos = 0.0;

	}

	@Override
	public void activate() {
		m_iStatus = Goal.CurrentStatus.active;

		//record the time the bot starts this goal
		m_dStartTime = System.nanoTime()*1000;    

		//This value is used to determine if the bot becomes stuck 
		m_dTimeToReachPos = m_pOwner.calculateTimeToReachPosition(m_vPosition);

		//factor in a margin of error for any reactive behavior
		double MarginOfError = 1.0;
		m_dTimeToReachPos += MarginOfError;
		m_pOwner.getSteering().setTarget(m_vPosition);
		m_pOwner.getSteering().seekOn();
	}

	public raven.goals.Goal.CurrentStatus process(){
		//if status is inactive, call Activate()
		activateIfInactive();

		//test to see if the bot has become stuck
		if (isStuck()) {
			m_iStatus = Goal.CurrentStatus.failed;
		}

		//test to see if the bot has reached the waypoint. If so terminate the goal
		else {
			if (m_pOwner.isAtPosition(m_vPosition)) {
				m_iStatus = Goal.CurrentStatus.completed;
			}
		}
		return m_iStatus;
	}

	public void terminate(){
		m_pOwner.getSteering().seekOff();
		m_pOwner.getSteering().arriveOff();
		m_iStatus = Goal.CurrentStatus.completed;
	}

	public void render(){
		if (m_iStatus == Goal.CurrentStatus.active)
		{
			GameCanvas.greenBrush();
			GameCanvas.blackPen();
			GameCanvas.circle(m_vPosition, 3);
		}

		else if (m_iStatus == Goal.CurrentStatus.inactive)
		{

			GameCanvas.redBrush();
			GameCanvas.blackPen();
			GameCanvas.circle(m_vPosition, 3);
		}
	}



}
