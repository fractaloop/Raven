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
import raven.script.RavenScript;
import raven.ui.GameCanvas;
import raven.math.Transformations;
import raven.math.Vector2D;
/**
 * @author chester
 *
 */
public class RocketLauncher extends RavenWeapon {

	private static int rlDefaultRounds = RavenScript.getInt("RocketLauncher_DefaultRounds");
	public static final int rlMaxRounds = RavenScript.getInt("RocketLauncher_MaxRoundsCarried");
	private static double rlFiringFreq = RavenScript.getDouble("RocketLauncher_FiringFreq");
	private static double rlIdealRange = RavenScript.getDouble("RocketLauncher_IdealRange");
	private static double rlMaxSpeed = RavenScript.getDouble("Rocket_MaxSpeed");

	public RocketLauncher(IRavenBot owner){
		super(RavenObject.ROCKET_LAUNCHER, rlDefaultRounds, rlMaxRounds, rlFiringFreq, rlIdealRange, rlMaxSpeed, owner);

		final Vector2D[] weaponVectors = {
				new Vector2D(0, -3),
				new Vector2D(6, -3),
				new Vector2D(6, -1),
				new Vector2D(15, -1),
				new Vector2D(15, 1),
				new Vector2D(6, 1),
				new Vector2D(6, 3),
				new Vector2D(0, 3)
		};
		for (Vector2D v : weaponVectors)
		{
			// Dirty scaling hack
			getWeaponVectorBuffer().add(v.mul(1.0/10));
		}

		//setup the fuzzy module
		InitializeFuzzyModule();
	}

	@Override
	protected void InitializeFuzzyModule(){
		FuzzyVariable DistToTarget = getFuzzyModule().CreateFLV("DistToTarget");

		FzSet Target_Close = DistToTarget.AddLeftShoulderSet("Target_Close",0,25,150);
		FzSet Target_Medium = DistToTarget.AddTriangularSet("Target_Medium",25,150,300);
		FzSet Target_Far = DistToTarget.AddRightShoulderSet("Target_Far",150,300,1000);

		FuzzyVariable Desirability = getFuzzyModule().CreateFLV("Desirability"); 
		FzSet VeryDesirable = Desirability.AddRightShoulderSet("VeryDesirable", 50, 75, 100);
		FzSet Desirable = Desirability.AddTriangularSet("Desirable", 25, 50, 75);
		FzSet Undesirable = Desirability.AddLeftShoulderSet("Undesirable", 0, 25, 50);

		FuzzyVariable AmmoStatus = getFuzzyModule().CreateFLV("AmmoStatus");
		FzSet Ammo_Loads = AmmoStatus.AddRightShoulderSet("Ammo_Loads", 10, 30, 100);
		FzSet Ammo_Okay = AmmoStatus.AddTriangularSet("Ammo_Okay", 0, 10, 30);
		FzSet Ammo_Low = AmmoStatus.AddTriangularSet("Ammo_Low", 0, 0, 10);


		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Loads), Undesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Okay), Undesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Low), Undesirable);

		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Loads), VeryDesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Okay), VeryDesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Low), Desirable);

		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Loads), Desirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Okay), Undesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Low), Undesirable);
	}

	@Override
	public void render(){
		List<Vector2D> weaponsTrans = Transformations.WorldTransform(getWeaponVectorBuffer(),
				getOwner().pos(),
				getOwner().facing(),
				getOwner().facing().perp(),
				getOwner().scale());

		GameCanvas.redPen();

		GameCanvas.closedShape(weaponsTrans);
	}

	@Override
	public void ShootAt(Vector2D position){
		if (getRoundsRemaining() > 0 && timeUntilAvailable <= 0)
		{
			//fire off a rocket!
			getOwner().getWorld().addRocket(getOwner(), position);
			decrementRoundsLeft();
			UpdateTimeWeaponIsNextAvailable();

			//add a trigger to the game so that the other bots can hear this shot
			//(provided they are within range)
			getOwner().getWorld().getMap().addSoundTrigger(getOwner(), RavenScript.getDouble("RocketLauncher_SoundRange"));
		}
	}

	@Override
	public double GetDesireability(double distance){
		double desire= 0;
		if (getRoundsRemaining() != 0)
		{
			//fuzzify distance and amount of ammo
			getFuzzyModule().Fuzzify("DistToTarget", distance);
			getFuzzyModule().Fuzzify("AmmoStatus", (double)getRoundsRemaining());

			desire = getFuzzyModule().Defuzzify("Desirability", FuzzyModule.DefuzzifyMethod.max_av);
			setLastDesireability(desire);
		}

		return desire;
	}
}
