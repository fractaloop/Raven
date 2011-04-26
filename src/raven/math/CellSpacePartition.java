package raven.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import raven.math.graph.GraphNode;

public class CellSpacePartition<T extends GraphNode> implements Iterable<T> {
	protected class Cell<E> {
		/** all the entities inhabiting this cell */
		public List<E> members = new LinkedList<E>();
		
		/** the cell's bounding box (it's inverted because the Window's
		 * default co-ordinate system has a y axis that increases as it
		 * descends) */
		public InvertedAABox2D bbox;
		
		public Cell(Vector2D topLeft, Vector2D bottomRight) {
			bbox = new InvertedAABox2D(topLeft, bottomRight);
		}
	}
	
	private List<Cell<T>> cells;
	
	private List<T> neighbors;
	
	private double spaceWidth;
	private double spaceHeight;
	
	private int numCellsX;
	private int numCellsY;
	
	private double cellSizeX;
	private double cellSizeY;
	
	/** Given a 2D vector representing a position within the game world, this
	 * method calculates an index into its appropriate cell */
	private int positionToIndex(Vector2D pos) {
		int index = (int)(numCellsX * pos.x / spaceWidth) + ((int)(numCellsY * pos.y / spaceHeight) * numCellsX);
		
		return Math.min(index, cells.size() - 1);
	}
	
	public CellSpacePartition(double width, double height, int cellsX, int cellsY, int maxEntities) {
		cells = new ArrayList<Cell<T>>();
		spaceWidth = width;
		spaceHeight = height;
		numCellsX = cellsX;
		numCellsY = cellsY;
		neighbors = new ArrayList<T>(maxEntities);
		
		// Calculate the bounds of each cell
		cellSizeX = width / cellsX;
		if(cellSizeX == Double.NaN) cellSizeX = 0;
		cellSizeY = height / cellsY;
		if(cellSizeY == Double.NaN) cellSizeY = 0;
		
		// Create the cells
		for (int y = 0; y < numCellsY; y++) {
			for (int x = 0; x < numCellsX; x++) {
				double left = x * cellSizeX;
				double right = left + cellSizeX;
				double top = y * cellSizeY;
				double bottom = top + cellSizeY;
				
				cells.add(new Cell<T>(new Vector2D(left, top), new Vector2D(right, bottom)));
			}
		}
	}
	
	/** Used to add the entitys to the data structure */
	public void addEntity(T ent) {
		if (ent == null)
			throw new NullPointerException();
		
		cells.get(positionToIndex(ent.pos())).members.add(ent);
	}
	
	public void updateEntity(T ent, Vector2D oldPos) {
		int oldIndex = positionToIndex(oldPos);
		int newIndex = positionToIndex(ent.pos());
		
		if (newIndex == oldIndex) {
			return;
		}
		
		// the entity has moved into another cell so delete from current cell
		// and add to new one
		cells.get(oldIndex).members.remove(ent);
		cells.get(newIndex).members.remove(ent);
	}
	
	public void calculateNeighbors(Vector2D targetPos, double queryRadius) {
		// We are finding a new set of neighbors, so get rid of the old ones
		neighbors.clear();
		
		// create the query box that is the bounding box of the target's query
		// area
		InvertedAABox2D queryBox = new InvertedAABox2D(targetPos.sub(new Vector2D(queryRadius, queryRadius)), targetPos.add(new Vector2D(queryRadius, queryRadius)));
		
		// iterate through each cell and test to see if its bounding box
		// overlaps with the query box. If it does and it also contains
		// entities then make further proximity tests.
		for (Cell<T> cell : cells) {
			// test to see if this cell contains members and if it overlaps
			// the query box
			if (cell.bbox.isOverlappedWith(queryBox) && !cell.members.isEmpty()) {
				// add any entities found within query radius to the neighbor
				// list
				for (T entity : cell.members) {
					if (entity.pos().distanceSq(targetPos) < queryRadius * queryRadius) {
						neighbors.add(entity);
					}
				}
			}
		}
	}
	
	public void emptyCells() {
		for (Cell<T> cell : cells) {
			cell.members.clear();
		}
	}
	
	public void renderCells() {
		for (Cell<T> cell : cells) {
			cell.bbox.render(false);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return neighbors.iterator();
	}
	
	@Override
	public boolean equals(Object o){
		if( this == o ) return true;
		if(!(o instanceof CellSpacePartition<?>)) return false;
	
		CellSpacePartition<?> other = (CellSpacePartition<?>) o;
		return (cells.equals(other.cells) &&
				neighbors.equals(other.neighbors) &&
				Double.compare(spaceHeight, other.spaceHeight) == 0 && 
				Double.compare(spaceWidth, other.spaceWidth) == 0 &&
				numCellsX == other.numCellsX &&
				numCellsY == other.numCellsY &&
				Double.compare(cellSizeX, other.cellSizeX) == 0 &&
				Double.compare(cellSizeY, other.cellSizeY) == 0);
		
	}
	
}
