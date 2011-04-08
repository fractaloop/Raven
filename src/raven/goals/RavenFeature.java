package raven.goals;

import raven.game.RavenBot;
import raven.game.RavenWeaponSystem;
import raven.goals.Goal.weaponType;
import raven.script.RavenScript;

public class RavenFeature {

		  //returns a value between 0 and 1 based on the bot's health. The better
		  //the health, the higher the rating
		  public static double Health(RavenBot pBot){
			  return (double)pBot.health() / (double)pBot.maxHealth();
		  }
		  
		  //returns a value between 0 and 1 based on the bot's closeness to the 
		  //given item. the further the item, the higher the rating. If there is no
		  //item of the given type present in the game world at the time this method
		  //is called the value returned is 1
		  public static double DistanceToItem(RavenBot pBot, int ItemType){
			  //determine the distance to the closest instance of the item type
			  double DistanceToItem = pBot.getPathPlanner().GetCostToClosestItem(ItemType);

			  //if the previous method returns a negative value then there is no item of
			  //the specified type present in the game world at this time.
			  if (DistanceToItem < 0 ) return 1;

			  //these values represent cutoffs. Any distance over MaxDistance results in
			  //a value of 0, and value below MinDistance results in a value of 1
			  double MaxDistance = 500.0;
			  double MinDistance = 50.0;

			  Clamp(DistanceToItem, MinDistance, MaxDistance);

			  return DistanceToItem / MaxDistance;}
		  
		  //returns a value between 0 and 1 based on how much ammo the bot has for
		  //the given weapon, and the maximum amount of ammo the bot can carry. The
		  //closer the amount carried is to the max amount, the higher the score
		  
		  
		  
		  
		  
		  
		  public static double IndividualWeaponStrength(RavenBot pBot, Goal.weaponType WeaponType) throws Exception{
			  
			  //grab a pointer to the gun (if the bot owns an instance)
			  RavenWeaponSystem wp = pBot.getWeaponSys().getWeaponFromInventory(WeaponType);

			  if (wp != null)
			  {
			    return wp.NumRoundsRemaining() / GetMaxRoundsBotCanCarryForWeapon(WeaponType);
			  }

			  else
			  {
			   return 0.0;
			  }
		  }

		  //returns a value between 0 and 1 based on the total amount of ammo the
		  //bot is carrying each of the weapons. Each of the three weapons a bot can
		  //pick up can contribute a third to the score. In other words, if a bot
		  //is carrying a RL and a RG and has max ammo for the RG but only half max
		  //for the RL the rating will be 1/3 + 1/6 + 0 = 0.5
		  public static double TotalWeaponStrength(RavenBot pBot) throws Exception{
			 double MaxRoundsForShotgun = GetMaxRoundsBotCanCarryForWeapon(Goal.weaponType.type_shotgun);
			  double MaxRoundsForRailgun = GetMaxRoundsBotCanCarryForWeapon(Goal.weaponType.type_rail_gun);
			  double MaxRoundsForRocketLauncher = GetMaxRoundsBotCanCarryForWeapon(Goal.weaponType.type_rocket_launcher);
			  double TotalRoundsCarryable = MaxRoundsForShotgun + MaxRoundsForRailgun + MaxRoundsForRocketLauncher;

			  double NumSlugs      = (double)pBot.getWeaponSys().getAmmoRemainingForWeapon(Goal.weaponType.type_rail_gun);
			  double NumCartridges = (double)pBot.getWeaponSys().getAmmoRemainingForWeapon(Goal.weaponType.type_shotgun);
			  double NumRockets    = (double)pBot.getWeaponSys().getAmmoRemainingForWeapon(Goal.weaponType.type_rocket_launcher);

			  //the value of the tweaker (must be in the range 0-1) indicates how much
			  //desirability value is returned even if a bot has not picked up any weapons.
			  //(it basically adds in an amount for a bot's persistent weapon -- the blaster)
			  double Tweaker = 0.1;

			  return Tweaker + (1-Tweaker)*(NumSlugs + NumCartridges + NumRockets)/(MaxRoundsForShotgun + MaxRoundsForRailgun + MaxRoundsForRocketLauncher);
		  }
		  
		  
		  static double GetMaxRoundsBotCanCarryForWeapon(Goal.weaponType WeaponType) throws Exception{
		    switch(WeaponType)
		    {
		    case type_rail_gun:

		      return RavenScript.getDouble("RailGun_MaxRoundsCarried");

		    case type_rocket_launcher:

		      return RavenScript.getDouble("RocketLauncher_MaxRoundsCarried");

		    case type_shotgun:

		      return RavenScript.getDouble("ShotGun_MaxRoundsCarried");

		    default:

		      throw new Exception("trying to calculate of unknown weapon");

		    }//end switch
		  }
		  
		  
		  
}
		  

