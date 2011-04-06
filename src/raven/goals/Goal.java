package raven.goals;

import raven.game.BaseGameEntity;
import raven.game.messaging.Telegram;

public class Goal<T extends BaseGameEntity> {
	public enum curStatus{active, inactive, completed, failed}
	public enum goalType{goal_explore}

	

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







	public raven.goals.Goal.curStatus Process() {
		// TODO Auto-generated method stub
		return null;
	}
		// TODOOO
	 public T GetType(){
		return null;
	 }

	
	



}
