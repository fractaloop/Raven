package raven.goals;

import raven.game.BaseGameEntity;
import raven.game.RavenBot;
import raven.game.messaging.Telegram;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class Goal<T extends BaseGameEntity> {
	public enum CurrentStatus{active, inactive, completed, failed}
	public enum GoalType{goal_explore,goal_hunt_target, goal_follow_path, goal_strafe, goal_move,goal_get,goal_attack_target,goal_get_shotgun, goal_get_railgun, goal_get_rocket_launcher, goal_get_health, goal_negotiate_door, goal_seek_to_position, goal_traverse_edge, goal_wander, goal_think}


	// reference to owner of this object.

	T m_pOwner;
	//an enumerated value indicating the goal's status (active, inactive,
	//completed, failed)
	CurrentStatus m_iStatus;

	static GoalType m_iType;

	public Goal(T PE, GoalType type ){
		m_iType = type;
		m_pOwner  = PE;
		m_iStatus = Goal.CurrentStatus.inactive;
	}





	// TODO more.
	public boolean HandleMessage(Telegram msg){return false;}



	public boolean isComplete()
	{
		if(m_iStatus == Goal.CurrentStatus.completed){
			return true;
		}
		else{
			return false;
		}
	}




	public boolean isActive(){
		if(m_iStatus == Goal.CurrentStatus.active){
			return true;
		}
		else{
			return false;
		}
	}

	boolean isInactive(){
		if(m_iStatus == Goal.CurrentStatus.inactive){
			return true;
		}
		else{
			return false;
		}
	}
	boolean hasFailed(){
		if(m_iStatus == Goal.CurrentStatus.failed){
			return true;
		}
		else{
			return false;
		}
	}




	public void Terminate() {
		// TODO Auto-generated method stub

	}
	//if m_iStatus is failed this method sets it to inactive so that the goal
	//will be reactivated (replanned) on the next update-step.
	public void reactivateIfFailed()
	{
		if (hasFailed())
		{
			m_iStatus = Goal.CurrentStatus.inactive;
		}
	}
	public void activateIfInactive()
	{
		if (isInactive())
		{
			activate();   
		}
	}

	public void activate(){

	}




	public CurrentStatus Process() {
		// TODO Auto-generated method stub
		return null;
	}
	// TODOOO
	public GoalType GetType(){
		return m_iType;
	}



	public void render(){


	}



	void renderAtPos(Vector2D pos, String tts){
		pos.y += 15;
		GameCanvas.whitePen();
		if (isComplete()) GameCanvas.greenPen();
		if (hasFailed()) GameCanvas.redPen();
		if (isActive()) GameCanvas.bluePen();

		GameCanvas.textAtPos(pos.x, pos.y, tts); 
	}






}
