package raven.goals;

import raven.game.BaseGameEntity;
import raven.game.messaging.Telegram;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

abstract public class Goal<T extends BaseGameEntity> {
	public enum curStatus{active, inactive, completed, failed}
	public enum goalType{goal_explore, goal_move,goal_get,goal_attack_target,goal_get_shotgun, goal_get_railgun, goal_get_rocket_launcher, goal_get_health}
	

	// reference to owner of this object.

	T m_pOwner;
	//an enumerated value indicating the goal's status (active, inactive,
	//completed, failed)
	curStatus m_iStatus;
	
	goalType m_iType;
	
	
	
	// TODO more.
	  public boolean HandleMessage(Telegram msg){return false;}



	public boolean isComplete()
	{
		if(m_iStatus == Goal.curStatus.completed){
			return true;
		}
		else{
			return false;
		}
	}




	public boolean isActive(){
		if(m_iStatus == Goal.curStatus.active){
			return true;
		}
		else{
			return false;
		}
	}

	boolean isInactive(){
		if(m_iStatus == Goal.curStatus.inactive){
			return true;
		}
		else{
			return false;
		}
	}
	boolean hasFailed(){
		if(m_iStatus == Goal.curStatus.failed){
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
	     m_iStatus = Goal.curStatus.inactive;
	  }
	}
	public void activateIfInactive()
	{
	  if (isInactive())
	  {
	    activate();   
	  }
	}
	
	abstract public void activate();




	public curStatus Process() {
		// TODO Auto-generated method stub
		return null;
	}
		// TODOOO
	 public goalType GetType(){
		return m_iType;
	 }



	abstract public void render();



	void renderAtPos(Vector2D pos, String tts){
		  pos.y += 15;
		  GameCanvas.transparentText();
		  if (isComplete()) GameCanvas.textColor(0,255,0);
		  if (isInactive()) GameCanvas.textColor(0,0,0);
		  if (hasFailed()) GameCanvas.textColor(255,0,0);
		  if (isActive()) GameCanvas.textColor(0,0,255);

		  GameCanvas.textAtPos(pos.x, pos.y, tts); 
	}

	
	



}
