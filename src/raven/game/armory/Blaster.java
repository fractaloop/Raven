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
public class Blaster extends RavenWeapon {

	private static int blasterDefaultRounds = RavenScript.getInt("Blaster_DefaultRounds");
	private static final int blasterMaxRounds = RavenScript.getInt("Blaster_MaxRoundsCarried");
	private static double blasterFiringFreq = RavenScript.getDouble("Blaster_FiringFreq");
	private static double blasterIdealRange = RavenScript.getDouble("Blaster_IdealRange");
	private static double blasterMaxSpeed = RavenScript.getDouble("Blaster_MaxSpeed");

	public Blaster(IRavenBot owner)
	{
		super(RavenObject.BLASTER, blasterDefaultRounds, blasterMaxRounds, blasterFiringFreq, blasterIdealRange, blasterMaxSpeed, owner);

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
	public void render(){
		List<Vector2D> tempBuffer = Transformations.WorldTransform(getWeaponVectorBuffer(),
				getOwner().pos(),
				getOwner().facing(),
				getOwner().facing().perp(),
				getOwner().scale());

		setWeaponVectorTransBuffer(tempBuffer);

		GameCanvas.greenPen();
		GameCanvas.closedShape(tempBuffer);
	}

	@Override
	public void ShootAt(Vector2D position){
		if(timeUntilAvailable <= 0){
			getOwner().getWorld().addBolt(getOwner(), position);

			//time next available is 1second/times per second!
			UpdateTimeWeaponIsNextAvailable();

			getOwner().getWorld().getMap().addSoundTrigger(getOwner(), RavenScript.getDouble("Blaster_SoundRange"));
		}
	}

	@Override
	public double GetDesireability(double distanceToTarget){
		getFuzzyModule().Fuzzify("DistToTarget", distanceToTarget);
		double desire = getFuzzyModule().Defuzzify("Desireability", FuzzyModule.DefuzzifyMethod.max_av);
		setLastDesireability(desire);

		return desire;

	}

	@Override
	protected void InitializeFuzzyModule(){
		FuzzyVariable DistToTarget = getFuzzyModule().CreateFLV("DistToTarget");
		FzSet Target_Close = DistToTarget.AddLeftShoulderSet("Target_Close", 0, 25, 150);
		FzSet Target_Medium = DistToTarget.AddTriangularSet("Target_Medium", 25, 150, 300);
		FzSet Target_Far = DistToTarget.AddRightShoulderSet("Target_Far", 150, 300, 1000);

		FuzzyVariable Desirability = getFuzzyModule().CreateFLV("Desirability"); 
		FzSet Desirable = Desirability.AddTriangularSet("Desirable", 25, 50, 75);
		FzSet Undesirable = Desirability.AddLeftShoulderSet("Undesirable", 0, 25, 50);

		getFuzzyModule().AddRule(Target_Close, Desirable);
		getFuzzyModule().AddRule(Target_Medium, new FzVery(Undesirable));
		getFuzzyModule().AddRule(Target_Far, new FzVery(Undesirable));
	}


}
