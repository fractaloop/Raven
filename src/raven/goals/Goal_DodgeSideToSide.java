package raven.goals;

import java.util.Random;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;

public class Goal_DodgeSideToSide extends GoalComposite<RavenBot> {

	
	
	private Vector2D m_vStrafeTarget;

	 private boolean m_bClockwise;

	  private Vector2D getStrafeTarget(RavenBot m_pOwner){
		return m_vStrafeTarget;
		
		  //TODO THIS NEEDS TO BE DONE
		  
		  //m_pOwner
	}

	  
	  
	public Goal_DodgeSideToSide(RavenBot m_pOwner) {
		Random Randomgen = new Random();
		m_bClockwise = Randomgen.nextBoolean();
		this.m_vStrafeTarget = getStrafeTarget(m_pOwner);
	}


		 public void Activate(){
			  m_iStatus = Goal.curStatus.active;

			  m_pOwner.getSteering().SeekOn();

			  
			    if (m_bClockwise)
			    {
			      if (m_pOwner.canStepRight(m_vStrafeTarget))
			      {
			        m_pOwner.getSteering().SetTarget(m_vStrafeTarget);
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
			        m_pOwner.getSteering().SetTarget(m_vStrafeTarget);
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

		  public void render(){
			    GameCanvas.orangePen();
			    GameCanvas.hollowBrush();

			    GameCanvas.line(m_pOwner.pos(), m_vStrafeTarget);
			    GameCanvas.circle(m_vStrafeTarget, 3);
			  }


		  public void Terminate(){
			    m_pOwner.getSteering().SeekOff();
			  }



		@Override
		public void renderAtPos(Vector2D p) {
			// // do nothing
			
		}
		  }
