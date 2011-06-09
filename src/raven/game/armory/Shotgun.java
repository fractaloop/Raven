/**
 * 
 */
package raven.game.armory;

import java.util.List;

import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.game.interfaces.IRavenBot;
import raven.goals.fuzzy.FuzzyModule;
import raven.goals.fuzzy.FuzzyVariable;
import raven.goals.fuzzy.FzAnd;
import raven.goals.fuzzy.FzSet;
import raven.math.RandUtils;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

/**
 * @author chester
 *
 */
public class Shotgun extends RavenWeapon {

	private int numBallsInShell;
	private double spread;


	public Shotgun(IRavenBot owner){
		super(RavenObject.SHOTGUN, RavenScript.getInt("ShotGun_DefaultRounds"), RavenScript.getInt("ShotGun_MaxRoundsCarried"), RavenScript.getDouble("ShotGun_FiringFreq"),
				RavenScript.getDouble("ShotGun_IdealRange"), RavenScript.getDouble("Pellet_MaxSpeed"), owner);
		
		numBallsInShell = RavenScript.getInt("ShotGun_NumBallsInShell");
		spread = RavenScript.getDouble("ShotGun_Spread");
		
		Vector2D[] weapon = {
				new Vector2D(0, 0),
				new Vector2D(0, -2),
				new Vector2D(10, -2),
				new Vector2D(10, 0),
				new Vector2D(0, 0),
				new Vector2D(0, 2),
				new Vector2D(10, 2),
				new Vector2D(10, 0)};

		for(Vector2D v : weapon){
			// Dirty scaling hack
			getWeaponVectorBuffer().add(v.mul(1.0/10));
		}
		InitializeFuzzyModule();
	}

	@Override
	protected void InitializeFuzzyModule(){
		FuzzyVariable DistanceToTarget = getFuzzyModule().CreateFLV("DistanceToTarget");

		FzSet Target_Close = DistanceToTarget.AddLeftShoulderSet("Target_Close", 0, 25, 150);
		FzSet Target_Medium = DistanceToTarget.AddTriangularSet("Target_Medium", 25, 150, 300);
		FzSet Target_Far = DistanceToTarget.AddRightShoulderSet("Target_Far", 150, 300, 1000);

		FuzzyVariable Desirability = getFuzzyModule().CreateFLV("Desirability");

		FzSet VeryDesirable = Desirability.AddRightShoulderSet("VeryDesirable", 50, 75, 100);
		FzSet Desirable = Desirability.AddTriangularSet("Desirable", 25, 50, 75);
		FzSet Undesirable = Desirability.AddLeftShoulderSet("Undesirable", 0, 25, 50);

		FuzzyVariable AmmoStatus = getFuzzyModule().CreateFLV("AmmoStatus");
		FzSet Ammo_Loads = AmmoStatus.AddRightShoulderSet("Ammo_Loads", 30, 60, 100);
		FzSet Ammo_Okay = AmmoStatus.AddTriangularSet("Ammo_Okay", 0, 30, 60);
		FzSet Ammo_Low = AmmoStatus.AddTriangularSet("Ammo_Low", 0, 0, 30);


		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Loads), VeryDesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Okay), VeryDesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Low), VeryDesirable);

		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Loads), VeryDesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Okay), Desirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Low), Undesirable);

		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Loads), Desirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Okay), Undesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Low), Undesirable);
	}

	@Override
	public void ShootAt(Vector2D position){
		if (getRoundsRemaining() > 0 && timeUntilAvailable <= 0)
		{
			//a shotgun cartridge contains lots of tiny metal balls called pellets. 
			//Therefore, every time the shotgun is discharged we have to calculate
			//the spread of the pellets and add one for each trajectory
			for (int b = 0; b < numBallsInShell; ++b)
			{
				//determine deviation from target using a bell curve type distribution

				double deviation = RandUtils.RandInRange(0, spread) + RandUtils.RandInRange(0, spread) - spread;

				Vector2D AdjustedTarget = position.sub(getOwner().pos());

				//rotate the target vector by the deviation
				Transformations.Vec2DRotateAroundOrigin(AdjustedTarget, deviation);
				//add a pellet to the game world
				getOwner().getWorld().addShotGunPellet(getOwner(), AdjustedTarget.add(getOwner().pos()));
			}

			decrementRoundsLeft();

			UpdateTimeWeaponIsNextAvailable();

			//add a trigger to the game so that the other bots can hear this shot
			//(provided they are within range)
			getOwner().getWorld().getMap().addSoundTrigger(getOwner(), RavenScript.getDouble("ShotGun_SoundRange"));
		}
	}

	@Override
	public void render(){
		List<Vector2D> weaponTrans = Transformations.WorldTransform(getWeaponVectorBuffer(),
				getOwner().pos(),
				getOwner().facing(),
				getOwner().facing().perp(),
				getOwner().scale());

		GameCanvas.brownPen();

		GameCanvas.polyLine(weaponTrans);

	}

	@Override
	public double GetDesireability(double distanceToTarget){
		double desire = 0;  
		if (getRoundsRemaining() != 0)
		{
			//fuzzify distance and amount of ammo
			getFuzzyModule().Fuzzify("DistanceToTarget", distanceToTarget);
			getFuzzyModule().Fuzzify("AmmoStatus", (double)getRoundsRemaining());

			desire = getFuzzyModule().Defuzzify("Desirability", FuzzyModule.DefuzzifyMethod.max_av);
			setLastDesireability(desire);
		}

		return desire;
	}
}
