package raven.goals;

import raven.game.RavenBot;
import raven.math.Vector2D;

public class Goal_DodgeSideToSide extends GoalComposite<RavenBot> {

	
	
	private Vector2D m_vStrafeTarget;

	 private boolean m_bClockwise;

	  private Vector2D getStrafeTarget(){
		// TODO why is this here?
		  return null;
	}

	  
	  
	public Goal_DodgeSideToSide(RavenBot m_pOwner) {
		// TODO Auto-generated constructor stub
	}


		 public void Activate(){
			  m_iStatus = Goal.curStatus.active;

			  m_pOwner.getSteering().seekOn();

			  
			    if (m_bClockwise)
			    {
			      if (m_pOwner.canStepRight(m_vStrafeTarget))
			      {
			        m_pOwner.getSteering().setTarget(m_vStrafeTarget);
			      }
			      else
			      {
			        //debug_con << "changing" << "";
			        m_bClockwise = !m_bClockwise;
			        m_iStatus = Goal.curStatus.inactive;
			      }
			    }

			    else
			    {
			      if (m_pOwner.canStepLeft(m_vStrafeTarget))
			      {
			        m_pOwner.getSteering().setTarget(m_vStrafeTarget);
			      }
			      else
			      {
			       // debug_con << "changing" << "";
			        m_bClockwise = !m_bClockwise;
			        m_iStatus = Goal.curStatus.inactive;
			      }
			    }

			   
			}



		  public Goal.curStatus Process(){
			  //if status is inactive, call Activate()
			  activateIfInactive(); 

			  //if target goes out of view terminate
			  if (!m_pOwner.getTargetSys().isTargetWithinFOV())
			  {
			    m_iStatus = Goal.curStatus.completed;
			  }

			  //else if bot reaches the target position set status to inactive so the goal 
			  //is reactivated on the next update-step
			  else if (m_pOwner.isAtPosition(m_vStrafeTarget))
			  {
			    m_iStatus = Goal.curStatus.inactive;
			  }

			  return m_iStatus;
		  }

		  public void Render(){
			   /* gdi.OrangePen();
			    gdi->HollowBrush();

			    gdi->Line(m_pOwner->Pos(), m_vStrafeTarget);
			    gdi->Circle(m_vStrafeTarget, 3);
			   x
			   
			    */
			  }


		  public void Terminate(){
			    m_pOwner.getSteering().seekOff();
			  }
		  }
