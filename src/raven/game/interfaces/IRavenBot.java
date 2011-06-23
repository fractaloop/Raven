package raven.game.interfaces;

import raven.game.RavenGame;
import raven.game.RavenObject;
import raven.game.RavenSensoryMemory;
import raven.game.RavenSteering;
import raven.game.RavenTargetingSystem;
import raven.game.RavenWeaponSystem;
import raven.game.messaging.Telegram;
import raven.goals.GoalThink;
import raven.math.Vector2D;

public interface IRavenBot {

	public boolean isAlive();
	public boolean isReadyForTriggerUpdate();
	public Vector2D pos();
	public double getBRadius();
	public void increaseHealth(int healthGiven);
	public int health();
	public RavenWeaponSystem getWeaponSys();
	public int ID();
	public RavenGame getWorld();
	public boolean canWalkBetween(Vector2D pos, Vector2D pos2);
	public boolean canWalkTo(Vector2D targetPos);
	public void tag();
	public void unTag();
	public void changeWeapon(RavenObject weapon);
	public boolean rotateFacingTowardPosition(Vector2D clientCursorPosition,
			double delta);
	public boolean isPossessed();
	public void exorcise();
	public void fireWeapon(Vector2D p);
	public GoalThink getBrain();
	public void takePossession();
	public Vector2D facing();
	public double fieldOfView();
	public Vector2D scale();
	public RavenSensoryMemory getSensoryMem();
	public RavenSteering getSteering();
	public void update(double delta);
	public void setSpawning();
	public boolean isDead();
	public boolean isSpawning();
	public void spawn(Vector2D pos);
	public IRavenBot getTargetBot();
	public void render();
	public double getMaxSpeed();
	public Vector2D velocity();
	public RavenObject entityType();
	public IRavenTargetingSystem getTargetSys();
	public boolean hasLOSto(Vector2D aimingPos);
	public Vector2D heading();
	public boolean handleMessage(Telegram msg);
	public void setBrain(GoalThink think);
	
}
