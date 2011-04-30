package raven.game.navigation;

import raven.math.Vector2D;

public class PathEdge{
	//positions of the source and destination nodes this edge connects
	private Vector2D source;
	private Vector2D destination;

	//the behavior associated with traversing this edge
	private int behavior;

	private int doorID;

	public PathEdge(Vector2D Source, Vector2D Destination, int Behavior, int DoorID){
		source=Source;
		destination= Destination;
		behavior= Behavior;
		doorID=DoorID;
	}

	public Vector2D Destination(){return destination;}
	public void SetDestination(Vector2D NewDest){destination = NewDest;}

	public Vector2D Source(){return source;}

	public void SetSource(Vector2D NewSource){source = NewSource;}

	public int DoorID(){return doorID;}
	public int Behavior(){return behavior;}
}