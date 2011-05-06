package raven.goals;

import raven.game.BaseGameEntity;
import raven.game.messaging.Telegram;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public abstract class Goal<T extends BaseGameEntity> {
	public enum CurrentStatus{active, inactive, completed, failed}
	public enum GoalType{goal_explore,goal_hunt_target, goal_follow_path, goal_strafe, goal_move,goal_get,goal_attack_target,goal_get_shotgun, goal_get_railgun, goal_get_rocket_launcher, goal_get_health, goal_negotiate_door, goal_seek_to_position, goal_traverse_edge, goal_wander, goal_think}


	// reference to owner of this object.

	T m_pOwner;
	//an enumerated value indicating the goal's status (active, inactive,
	//completed, failed)
	CurrentStatus m_iStatus;

	GoalType m_iType;

	//if m_iStatus is failed this method sets it to inactive so that the goal
	//will be reactivated (replanned) on the next update-step.
	protected void reactivateIfFailed()
	{
		if (hasFailed())
		{
			m_iStatus = Goal.CurrentStatus.inactive;
		}
	}
	
	protected void activateIfInactive()
	{
		if (isInactive())
		{
			activate();   
		}
	}
	
	public Goal(T PE, GoalType type ){
		m_iType = type;
		m_pOwner  = PE;
		m_iStatus = Goal.CurrentStatus.inactive;
	}

	public abstract void activate();
	public abstract CurrentStatus process(double delta);
	public abstract void terminate();
	
	public boolean handleMessage(Telegram msg){return false;}

	public boolean isComplete() { return m_iStatus == Goal.CurrentStatus.completed; }
	public boolean isActive(){ return m_iStatus == Goal.CurrentStatus.active; }
	boolean isInactive(){ return m_iStatus == Goal.CurrentStatus.inactive; }
	boolean hasFailed(){ return m_iStatus == Goal.CurrentStatus.failed; }

	public GoalType GetType(){ return m_iType;}

	public void render(){}

	void renderAtPos(Vector2D pos){
		pos.y += 15;
		GameCanvas.whitePen();
		if (isComplete()) GameCanvas.greenPen();
		if (isInactive()) GameCanvas.blackPen();
		if (hasFailed()) GameCanvas.redPen();
		if (isActive()) GameCanvas.bluePen();

		GameCanvas.textAtPos(pos.x, pos.y, m_iType.toString()); 
	}






}
