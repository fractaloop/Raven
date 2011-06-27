////////////////////////////////////////////////////////////
var DefaultNodeSpacing = 20;

// edges will only be added if two nodes are less than this length apart
var DefaultMaxEdgeLength = Math.sqrt(2) * DefaultNodeSpacing;

// a node must be at least this far away from a wall or obstacle. To prevent
// your agents bumping into walls it's wise to set this to a value slightly 
// larger than their bounding radius (in raven BRadius is 10 * scale found in
// params.js)
NodeMargin = 10;

// how close the mouse cursor has to be to an object to select it
SelectionTolerance = 5;

////////////////////////-[[ giver-trigger parameters ]]////////////////////////////-
//////////////////////////////////////////////////////////////////////////////-

//how close a bot must be to a giver-trigger for it to affect it
var DefaultGiverTriggerRange    = 10;
//amount of health given by a giver
var Health_AmountGiven          = 50;

//how many seconds before a giver-trigger reactivates itself
var Health_RespawnDelay         = 10;
var Weapon_RespawnDelay         = 15;

////////////////////////[[ General game parameters ]]/////////////////////////
//////////////////////////////////////////////////////////////////////////////

//the number of bots the game instantiates

var NumBots = 2;

//this is the maximum number of search cycles allocated to *all* current path
// planning searches per update
var MaxSearchCyclesPerUpdateStep = 1000;
var StartMap = "maps/default.raven";

//cell space partitioning defaults
var NumCellsX = 10;
var NumCellsY = 10;

//how long the graves remain on screen
var GraveLifetime = 5;


////////////////////////[[ bot parameters ]]//////////////////////////////////
//////////////////////////////////////////////////////////////////////////////-

var Bot_Scale           = 8;            // 2*scale = 1/3meter (bounding radius)
var PixelsPerMeter      = 2 * Bot_Scale / (1.0 / 3.0);
var Bot_MaxHealth       = 100;
var Bot_MaxSpeed        = 80;    // pixels/second
var Bot_Mass            = 80;           // mass = force / acceleration
var Bot_MaxForce        = Bot_Mass / 2; // force = mass * acceleration
var Bot_MaxHeadTurnRate = 2 * Math.PI;  // Bots can make 1 turn per second
// 
//special movement speeds (unused)
var Bot_MaxSwimmingSpeed = Bot_MaxSpeed * 0.2;
var Bot_MaxCrawlingSpeed = Bot_MaxSpeed * 0.6;
// 
//the number of times a second a bot 'thinks' about weapon selection
var Bot_WeaponSelectionFrequency = 2;

//the number of times a second a bot 'thinks' about changing strategy
var Bot_GoalAppraisalUpdateFreq = 4;

//the number of times a second a bot updates its target info
var Bot_TargetingUpdateFreq = 2;

//the number of times a second the triggers are updated
var Bot_TriggerUpdateFreq = 8;

//the number of times a second a bot updates its vision
var Bot_VisionUpdateFreq = 4;

// note that a frequency of -1 will disable the feature and a frequency of zero
// will ensure the feature is updated every bot update

//the bot's field of view (in degrees)
var Bot_FOV = 180;

//the bot's reaction time (in seconds)
var Bot_ReactionTime = 0.2;

//how long (in seconds) the bot will keep pointing its weapon at its target
//after the target goes out of view
var Bot_AimPersistance = 1;

//how accurate the bots are at aiming. 0 is very accurate, (the value represents
// the max deviation in range (in radians))
var Bot_AimAccuracy = 0.0;

//how long a flash is displayed when the bot is hit
var HitFlashTime = 0.2;

//how long (in seconds) a bot's sensory memory persists
var Bot_MemorySpan = 5;

//goal tweakers
var Bot_HealthGoalTweaker       = 1.0;
var Bot_ShotgunGoalTweaker      = 1.0;
var Bot_RailgunGoalTweaker      = 1.0;
var Bot_RocketLauncherTweaker   = 1.0;
var Bot_AggroGoalTweaker        = 1.0;

////////////////////////-[[ steering parameters ]]////////////////////////////-
//////////////////////////////////////////////////////////////////////////////-

//use these values to tweak the amount that each steering force
//contributes to the total steering force
var SeparationWeight            = 10.0;
var WallAvoidanceWeight         = 10.0;
var WanderWeight                = 1.0;
var SeekWeight                  = 0.5;
var ArriveWeight                = 1.0;
var PursuitWeight				= 5.0;

//how close a neighbour must be before an agent considers it
//to be within its neighborhood (for separation)
var ViewDistance                =  15.0;

//max feeler length
var WallDetectionFeelerLength   = 3.0 * Bot_Scale;

//used in path following. Determines how close a bot must be to a waypoint
//before it seeks the next waypoint
var WaypointSeekDist            = 5;

////////////////////////-[[ weapon parameters ]]//////////////////////////////
//////////////////////////////////////////////////////////////////////////////

var Blaster_FiringFreq          = 3;
var Blaster_MaxSpeed            = 5;
var Blaster_DefaultRounds       = 0; //not used, a blaster always has ammo
var Blaster_MaxRoundsCarried    = 0; //as above
var Blaster_IdealRange          = 50;
var Blaster_SoundRange          = 100;

var Bolt_MaxSpeed               = 5;
var Bolt_Mass                   = 1;
var Bolt_MaxForce               = 100.0;
var Bolt_Scale                  = Bot_Scale;
var Bolt_Damage                 = 15;

var RocketLauncher_FiringFreq   = 1.2;
var RocketLauncher_DefaultRounds    = 15;
var RocketLauncher_MaxRoundsCarried = 50;
var RocketLauncher_IdealRange   = 150;
var RocketLauncher_SoundRange   = 400;

var Rocket_BlastRadius          = 20;
var Rocket_MaxSpeed             = 3;
var Rocket_Mass                 = 1;
var Rocket_MaxForce             = 10.0;
var Rocket_Scale                = Bot_Scale;
var Rocket_Damage               = 100;
var Rocket_ExplosionDecayRate   = 2.0; // how fast the explosion occurs (in secs)

var RailGun_FiringFreq          = 1;
var RailGun_DefaultRounds       = 15;
var RailGun_MaxRoundsCarried    = 50;
var RailGun_IdealRange          = 200;
var RailGun_SoundRange          = 400;

var Slug_MaxSpeed               = 5000;
var Slug_Mass                   = 0.1;
var Slug_MaxForce               = 10000.0;
var Slug_Scale                  = Bot_Scale;
var Slug_Persistance            = 1.0;
var Slug_Damage                 = 100;

var ShotGun_FiringFreq          = 1;
var ShotGun_DefaultRounds       = 15;
var ShotGun_MaxRoundsCarried    = 50;
var ShotGun_NumBallsInShell     = 20;
var ShotGun_Spread              = 0.05;
var ShotGun_IdealRange          = 100;
var ShotGun_SoundRange          = 400;

var Pellet_MaxSpeed             = 5000;
var Pellet_Mass                 = 0.1;
var Pellet_MaxForce             = 1000.0;
var Pellet_Scale                = Bot_Scale;
var Pellet_Persistance          = 1.0;
var Pellet_Damage               = 6;
