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
import raven.goals.fuzzy.FzFairly;
import raven.goals.fuzzy.FzSet;
import raven.goals.fuzzy.FzVery;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

/**
 * @author chester
 *
 */
public class Railgun extends RavenWeapon {

	private static int railgunDefaultRounds = RavenScript.getInt("RailGun_DefaultRounds");
	public static final int railgunMaxRounds = RavenScript.getInt("RailGun_MaxRoundsCarried");
	private static double railgunFiringFreq = RavenScript.getDouble("RailGun_FiringFreq");
	private static double railgunIdealRange = RavenScript.getDouble("RailGun_IdealRange");
	private static double railgunMaxSpeed = RavenScript.getDouble("Slug_MaxSpeed");

	public Railgun(IRavenBot owner){
		super(RavenObject.RAIL_GUN, railgunDefaultRounds, railgunMaxRounds, railgunFiringFreq, railgunIdealRange, railgunMaxSpeed, owner);

		final Vector2D[] weapon = {new Vector2D(0, -1),
				new Vector2D(10, -1),
				new Vector2D(10 , 1),
				new Vector2D(0, 1)};
		for(Vector2D v : weapon){
			// Dirty scaling hack
			getWeaponVectorBuffer().add(v.mul(1.0/10));
		}

		InitializeFuzzyModule();
	}

	@Override
	protected void InitializeFuzzyModule() {
		FuzzyVariable DistanceToTarget = getFuzzyModule().CreateFLV("DistanceToTarget");

		FzSet Target_Close = DistanceToTarget.AddLeftShoulderSet("Target_Close", 0, 25, 150);
		FzSet Target_Medium = DistanceToTarget.AddTriangularSet("Target_Medium", 25, 150, 300);
		FzSet Target_Far = DistanceToTarget.AddRightShoulderSet("Target_Far", 150, 300, 1000);

		FuzzyVariable Desirability = getFuzzyModule().CreateFLV("Desirability");

		FzSet VeryDesirable = Desirability.AddRightShoulderSet("VeryDesirable", 50, 75, 100);
		FzSet Desirable = Desirability.AddTriangularSet("Desirable", 25, 50, 75);
		FzSet Undesirable = Desirability.AddLeftShoulderSet("Undesirable", 0, 25, 50);

		FuzzyVariable AmmoStatus = getFuzzyModule().CreateFLV("AmmoStatus");
		FzSet Ammo_Loads = AmmoStatus.AddRightShoulderSet("Ammo_Loads", 15, 30, 100);
		FzSet Ammo_Okay = AmmoStatus.AddTriangularSet("Ammo_Okay", 0, 15, 30);
		FzSet Ammo_Low = AmmoStatus.AddTriangularSet("Ammo_Low", 0, 0, 15);



		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Loads), new FzFairly(Desirable));
		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Okay),  new FzFairly(Desirable));
		getFuzzyModule().AddRule(new FzAnd(Target_Close, Ammo_Low), Undesirable);

		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Loads), VeryDesirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Okay), Desirable);
		getFuzzyModule().AddRule(new FzAnd(Target_Medium, Ammo_Low), Desirable);

		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Loads), new FzVery(VeryDesirable));
		getFuzzyModule().AddRule(new FzAnd(Target_Far, Ammo_Okay), new FzVery(VeryDesirable));
		getFuzzyModule().AddRule(new FzAnd(Target_Far, new FzFairly(Ammo_Low)), VeryDesirable);
	}

	@Override
	public void render(){
		List<Vector2D> thisWeaponShape = Transformations.WorldTransform(getWeaponVectorBuffer(),
				getOwner().pos(),
				getOwner().facing(),
				getOwner().facing().perp(),
				getOwner().scale());
		setWeaponVectorTransBuffer(thisWeaponShape);
		GameCanvas.bluePen();
		GameCanvas.closedShape(thisWeaponShape);
	}

	@Override
	public void ShootAt(Vector2D position){
		if ((getRoundsRemaining() > 0) && timeUntilAvailable <= 0){

			//fire a round
			getOwner().getWorld().addRailGunSlug(getOwner(), position);
			UpdateTimeWeaponIsNextAvailable();
			decrementRoundsLeft();

			//add a trigger to the game so that the other bots can hear this shot
			//(provided they are within range)
			getOwner().getWorld().getMap().addSoundTrigger(getOwner(), RavenScript.getDouble("RailGun_SoundRange"));
		}
	}

	@Override
	public double GetDesireability(double distToTarget){
		double desire = 0;  
		if (getRoundsRemaining() != 0)
		{
			//fuzzify distance and amount of ammo
			getFuzzyModule().Fuzzify("DistanceToTarget", distToTarget);
			getFuzzyModule().Fuzzify("AmmoStatus", (double)getRoundsRemaining());

			desire = getFuzzyModule().Defuzzify("Desirability", FuzzyModule.DefuzzifyMethod.max_av);
			setLastDesireability(desire);
		}

		return desire;
	}

}
