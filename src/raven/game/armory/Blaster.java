/**
 * 
 */
package raven.game.armory;

import java.util.List;
import java.util.Vector;

import raven.Raven;
import raven.game.RavenBot;
import raven.game.RavenObject;
import raven.goals.FuzzyModule;
import raven.goals.FuzzyVariable;
import raven.goals.FzSet;
import raven.goals.FzVery;
import raven.math.Transformations;
import raven.math.Vector2D;
import raven.script.RavenScript;
import raven.ui.GameCanvas;

/**
 * @author chester
 *
 */
public class Blaster extends RavenWeapon {

	private static int blasterDefaultRounds = RavenScript.getInt("blaster_defaultRounds");
	private static int blasterMaxRounds = RavenScript.getInt("blaster_maxRoundsCarried");
	private static double blasterFiringFreq = RavenScript.getDouble("blaster_firingFreq");
	private static double blasterIdealRange = RavenScript.getDouble("blaster_idealRange");
	private static double blasterMaxSpeed = RavenScript.getDouble("blaster_maxSpeed");
	
	public Blaster(RavenBot owner)
	{
		super(RavenObject.BLASTER, blasterDefaultRounds, blasterMaxRounds, blasterFiringFreq, blasterIdealRange, blasterMaxSpeed, owner);
		
		//setupVertexBuffers
		final int numberWeaponBuffers = 4;
		final Vector2D[] weapon = {new Vector2D(0, -1),
										new Vector2D(10, -1),
										new Vector2D(10 , 1),
										new Vector2D(0, 1)};
		
		for(Vector2D v : weapon){
			getWeaponVectorBuffer().add(v);
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
		if(isReadyForNextShot()){
			getOwner().getWorld().addBolt(getOwner(), position);
		}
	}
	
	@Override
	public double GetDesireability(double distanceToTarget){
		getFuzzyModule().Fuzzify("DistToTarget", distanceToTarget);
		double desire = getFuzzyModule().Defuzzify("Desireability", FuzzyModule.maxAv);
		setLastDesireability(desire);
		
		return desire;
		
	}
	
	private void InitializeFuzzyModule(){
		FuzzyVariable DistToTarget = getFuzzyModule().CreateFLV("DistToTarget");
		FzSet Target_Close = DistToTarget.AddLeftShoulderSet("Target_Close", 0, 25, 150);
		FzSet Target_Medium = DistToTarget.AddTriangularSet("Target_Medium", 25, 150, 300);
		FzSet Target_Far = DistToTarget.AddRightShoulderSet("Target_Far", 150, 300, 1000);
	
		FuzzyVariable Desirability = getFuzzyModule().CreateFLV("Desirability"); 
		FzSet VeryDesirable = Desirability.AddRightShoulderSet("VeryDesirable", 50, 75, 100);
		FzSet Desirable = Desirability.AddTriangularSet("Desirable", 25, 50, 75);
		FzSet Undesirable = Desirability.AddLeftShoulderSet("Undesirable", 0, 25, 50);
		
		getFuzzyModule().AddRule(Target_Close, Desirable);
		getFuzzyModule().AddRule(Target_Medium, new FzVery(Undesirable));
		getFuzzyModule().AddRule(Target_Far, new FzVery(Undesirable));
	}
}
