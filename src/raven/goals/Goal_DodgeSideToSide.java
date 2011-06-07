package raven.goals;

import java.util.Random;

import raven.game.RavenBot;
import raven.math.Vector2D;
import raven.ui.GameCanvas;
import raven.utils.Log;

public class Goal_DodgeSideToSide extends GoalComposite<RavenBot> {

	private Vector2D m_vStrafeTarget;
	private boolean m_bClockwise;

	public Goal_DodgeSideToSide(RavenBot m_pOwner) {
		super(m_pOwner, Goal.GoalType.goal_strafe);
		m_bClockwise = Math.random() > 0.5;
	}


	@Override
	public void activate(){
		m_iStatus = Goal.CurrentStatus.active;
		m_pOwner.getSteering().seekOn();
		if (m_bClockwise) {
			Vector2D result = m_pOwner.canStepRight(); 
			if (result != null) {
				m_vStrafeTarget = result;
				m_pOwner.getSteering().setTarget(m_vStrafeTarget);
			} else {
				//debug_con << "changing" << "";
				m_bClockwise = !m_bClockwise;
				m_iStatus = Goal.CurrentStatus.inactive;
			}
		} else {
			Vector2D result = m_pOwner.canStepLeft(); 
			if (result != null) {
				m_vStrafeTarget = result;
				m_pOwner.getSteering().setTarget(m_vStrafeTarget);
			} else {
				// debug_con << "changing" << "";
				m_bClockwise = !m_bClockwise;
				m_iStatus = Goal.CurrentStatus.inactive;
			}
		}
	}

	@Override
	public CurrentStatus process(double delta) {
		//if status is inactive, call Activate()
		activateIfInactive(); 

		//if target goes out of view terminate
		if (!m_pOwner.getTargetSys().isTargetWithinFOV()) {
			m_iStatus = Goal.CurrentStatus.completed;
		}

		//else if bot reaches the target position set status to inactive so the goal 
		//is reactivated on the next update-step
		else if (m_iStatus == CurrentStatus.active && m_pOwner.isAtPosition(m_vStrafeTarget)) {
			m_iStatus = Goal.CurrentStatus.inactive;
		}
		return m_iStatus;
	}

	@Override
	public void render() {
		if(m_pOwner.pos() == null || m_vStrafeTarget == null) {
			return;
		}
		GameCanvas.orangePen();
		GameCanvas.hollowBrush();
		GameCanvas.line(m_pOwner.pos(), m_vStrafeTarget);
		GameCanvas.circle(m_vStrafeTarget, 3);
	}

	@Override
	public void terminate() {
		m_pOwner.getSteering().seekOff();
		m_iStatus = Goal.CurrentStatus.completed;
	}
}
