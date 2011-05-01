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
		super(m_pOwner, Goal.GoalType.goal_strafe);
		Random Randomgen = new Random();
		m_bClockwise = Randomgen.nextBoolean();
		this.m_vStrafeTarget = m_pOwner.pos();
	}


	@Override
	public void activate(){
		m_iStatus = Goal.CurrentStatus.active;
		m_pOwner.getSteering().seekOn();
		if (m_bClockwise) {
			if (m_pOwner.canStepRight(m_vStrafeTarget)) {
				m_pOwner.getSteering().setTarget(m_vStrafeTarget);
			} else {
				//debug_con << "changing" << "";
				m_bClockwise = !m_bClockwise;
				m_iStatus = Goal.CurrentStatus.inactive;
			}
		} else {
			if (m_pOwner.canStepLeft(m_vStrafeTarget)) {
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
		else if (m_pOwner.isAtPosition(m_vStrafeTarget)) {
			m_iStatus = Goal.CurrentStatus.inactive;
		}
		return m_iStatus;
	}

	@Override
	public void render() {
		GameCanvas.orangePen();
		GameCanvas.hollowBrush();
		GameCanvas.line(m_pOwner.pos(), m_vStrafeTarget);
		GameCanvas.circle(m_vStrafeTarget, 3);
	}

	@Override
	public void terminate() {
		m_pOwner.getSteering().seekOff();
	}
}
