Raven
======

Raven is a shooter simulation written entirely in stock Java 1.6.  
It is meant to serve as a sandbox for the bots to play in, responding to changes in the environment, such as other bots, obstacles, and powerups.

ARCHITECTURE
------------

MAIN CLASSES
------------
Raven is written as a main-loop style game, with some notification for important 
events.  The game is broken into to large pieces: The RavenGame class and the 
RavenUI class.

The RavenGame is a data representation of the state of the game.  It 
encapsulates the updating of game state, as well as keeping track of the bots 
that are in the game, the selected bot, and global state. The RavenGame is 
responsible for creating and maintaining the data in the game.  It clears the 
data, adds new bots, delegates updating of status of all the bots, handles 
loading and setting up data from the map, and adding various weapons and 
powerups to the game.  It also has utility methods that are used by the bots 
to help them determine their course of action.

The RavenUI handles the display of the data calculated by RavenGame. It handles 
the menu creation, and is responsible for making sure that menu events are 
handled.  It also handles any user input on the window, such as clicks and 
keypresses.


MODEL
----- 

The data model for Raven is quite large.  All displayed objects in the game 
implement either BaseGameEntity, or its child, MovingEntity.  These classes 
expose functionality that is used by RavenGame and RavenUI to update and 
render the game ui.  Because these classes expose all the functionality these 
classes need, RavenGame and RavenUI do not need to know the details of 
implementing classes.  BaseGameEntity exposes information about entity type and 
entity id.  Moving Entity exposes even more information about course and speed.

RavenBot is the actor of this game. It is very complex conceptually, because a 
bot must be able to fire, have goals, move, target, and use senses to inform the
choice of goals.  To handle this complexity, the bot uses an instance of the 
GroupThink class to handle goal choosing and updating, an instance of the 
RavenSensoryMemory class to respond to sounds and sights, an instance of 
RavenSteering to move, an instance of RavenPathPlanner to decide where to move,
a RavenTargetingSystem to decide where to aim, and a RavenWeaponSystem to 
decide which weapon to fire, and when to fire.

RavenBot also has several regulators, which apply a weighting to the set of 
activities that the bot could do.  Assigning different weights to these 
regulators are instanciation is what gives different bots different behaviors.  

RavenWeapon

RavenProjectile

