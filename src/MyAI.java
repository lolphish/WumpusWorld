// ======================================================================
// FILE:        MyAI.java
//
// AUTHOR:      Abdullah Younis
//
// DESCRIPTION: This file contains your agent class, which you will
//              implement. You are responsible for implementing the
//              'getAction' function and any helper methods you feel you
//              need.
//
// NOTES:       - If you are having trouble understanding how the shell
//                works, look at the other parts of the code, as well as
//                the documentation.
//
//              - You are only allowed to make changes to this portion of
//                the code. Any changes to other portions of the code will
//                be lost when the tournament runs your code.
// ======================================================================

import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;


/* map: stores probabilities for each square;
 *      empty = 1, hazardous = 0
 */

public class MyAI extends Agent
{  
	static enum Direction
	{
		UP,
		RIGHT,
		DOWN,
		LEFT
	}
  
	private Direction direction;
	private Point currentPoint;
	private Point lastPoint;
	private Graph cave;
	private Queue<Action> actions;
	private boolean hasArrow;
	private boolean wumpusAlive;
	private boolean climbOut;
  	private boolean justShot;
	
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		defaultMax();
		currentPoint = getStartingPoint();		// initialized to (1, 1)
		lastPoint = null;
		cave = new Graph();
		
		hasArrow = true;
		wumpusAlive = true;
		climbOut = false;
      	justShot = false;
		
		actions = new LinkedList<>();
		direction = Direction.RIGHT;
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	public Action getAction
	(
		boolean stench,
		boolean breeze,
		boolean glitter,
		boolean bump,
		boolean scream
	)
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		if (scream) {
			wumpusAlive = false;
			cave.removeWumpusWarning();
		}
		
		if (glitter) {
			climbOut = true;
			return Action.GRAB;
		}
		
		if (climbOut) {
			if (currentPoint.atStart()) {
				return Action.CLIMB;
			}
			if (actions.isEmpty()) {
				navigate(currentPoint, getStartingPoint());
			}
		}
		
		if (bump) {
			// moveForward() adds a new node
			// if a bump is perceived, a new node was added when it shouldn't have been
			// we can mark this node as a WALL, then update currentPoint to be the node that it came from
			markOutOfBounds(currentPoint.getX(), currentPoint.getY());
			cave.getNode(currentPoint).addMarker(Marker.WALL);
			currentPoint = new Point(lastPoint);
		}
      
		Set<Marker> dangers = new HashSet<>();
		
		if (cave.getNode(currentPoint) != null) {
			cave.getNode(currentPoint).addMarker(Marker.EXPLORED);
		}

		// if an upcoming action has been queued, prioritize it
		if (!actions.isEmpty()) {
			return dequeueAction();
		}
		
		if (stench && wumpusAlive && hasArrow) {
			hasArrow = false;
			justShot = true;
			return Action.SHOOT;	
		}
		
		Action action;
		if (breeze || stench) {
			if (breeze) {
				if (currentPoint.atStart()) {
					// climb out?
					climbOut = true;
					return getAction(stench, breeze, glitter, bump, scream);
				}
				dangers.add(Marker.PITWARNING);
			}
			if (stench && wumpusAlive) {
                dangers.add(Marker.WUMPUSWARNING);
			}
		}
		
		cave.addNode(currentPoint, direction, dangers);
		
		if (justShot) {
			// shot, but missed
			// remove WUMPUSWARNING from the node directly in front of the agent
			Node currentNode = cave.getNode(currentPoint);
			Node neighbor = cave.getAdjacentNode(currentNode, direction);
			if (neighbor != null) {
				neighbor.removeMarker(Marker.WUMPUSWARNING);
				Point adjacentPoint = cave.getAdjacentPoint(currentPoint, direction);
				if (!neighbor.isDangerous()) {
					cave.addToUnexplored(adjacentPoint);	
				}
			}
		}
		
		Point closestPoint = cave.getClosestUnexploredPoint(currentPoint, direction);
		if (closestPoint == null) {
			climbOut = true;
			actions.clear();
			return getAction(false, false, false, false, false);
		}
		navigate(currentPoint, closestPoint);
		action = dequeueAction();
      
		justShot = false;
		return action;
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================  
	
	/* Helper function;
	 * returns Action.TURN_LEFT, and updates the AI's direction.
	 */
	private Action turnLeft() {
		switch (direction) {
			case UP:
				direction = Direction.LEFT;
				break;
			case LEFT:
				direction = Direction.DOWN;
				break;
			case DOWN:
				direction = Direction.RIGHT;
				break;
			case RIGHT:
				direction = Direction.UP;
				break;
		}
		return Action.TURN_LEFT;
	}
	
	/* Helper function;
	 * returns Action.TURN_RIGHT, and updates the AI's direction.
	 */
	private Action turnRight() {
		switch (direction) {
			case UP:
				direction = Direction.RIGHT;
				break;
			case RIGHT:
				direction = Direction.DOWN;
				break;
			case DOWN:
				direction = Direction.LEFT;
				break;
			case LEFT:
				direction = Direction.UP;
				break;
		}
		return Action.TURN_RIGHT;
	}
	
	private Action moveForward() {
		lastPoint = new Point(currentPoint);
		switch (direction) {
			case UP:
				currentPoint.addY(1);
				break;
			case DOWN:
				currentPoint.addY(-1);
				break;
			case RIGHT:
				currentPoint.addX(1);
				break;
			case LEFT:
				currentPoint.addX(-1);
				break;
		}
		return Action.FORWARD;
	}
	
	/* Assumes actions is NOT empty.
	 * Pops the next Action from the actions queue, and returns it.
	 */
	private Action dequeueAction() throws NoSuchElementException {
		Action nextAction = actions.remove();
		switch (nextAction) {
			case TURN_LEFT:
				nextAction = turnLeft();
				break;
			case TURN_RIGHT:
				nextAction = turnRight();
				break;
			case FORWARD:
				nextAction = moveForward();
			default:
				break;
		}
		return nextAction;
	}
  
	/* Queues turn Actions until the Agent is facing targetDirection.
	 * TODO: optimize
	 */
	private void faceDirection(Direction currentDirection, Direction targetDirection) {
		if (currentDirection == targetDirection) {
			return;
		}
		switch (currentDirection) {
			case UP:
				switch (targetDirection) {
					case DOWN:
						actions.add(Action.TURN_RIGHT);
						actions.add(Action.TURN_RIGHT);
						break;
					case LEFT:
						actions.add(Action.TURN_LEFT);
						break;
					case RIGHT:
						actions.add(Action.TURN_RIGHT);
						break;
				}
				break;
			case DOWN:
				switch (targetDirection) {
					case UP:
						actions.add(Action.TURN_RIGHT);
						actions.add(Action.TURN_RIGHT);
						break;
					case LEFT:
						actions.add(Action.TURN_RIGHT);
						break;
					case RIGHT:
						actions.add(Action.TURN_LEFT);
						break;
				}
				break;
			case LEFT:
				switch (targetDirection) {
					case UP:
						actions.add(Action.TURN_RIGHT);
						break;
					case DOWN:
						actions.add(Action.TURN_LEFT);
						break;
					case RIGHT:
						actions.add(Action.TURN_RIGHT);
						actions.add(Action.TURN_RIGHT);
						break;
				}
				break;
			case RIGHT:
				switch (targetDirection) {
					case UP:
						actions.add(Action.TURN_LEFT);
						break;
					case DOWN:
						actions.add(Action.TURN_RIGHT);
						break;
					case LEFT:
						actions.add(Action.TURN_RIGHT);
						actions.add(Action.TURN_RIGHT);
						break;
				}
				break;
		}
	}
	
	  /*
	 * convert stack of point directions to actions and add them to the actions
	 * queue
	 * 
	 * TODO: test this method for walking through multiple Points
	 */
	private void getActionsFromPoints(Stack<Point> points) {
		Direction currentDirection = direction, nextDirection = null;
		Point currentLocationPoint = currentPoint, nextPoint = null;
		while (!points.isEmpty()) {
			nextPoint = points.pop();
			switch (currentLocationPoint.getX() - nextPoint.getX()){
				case 1:
					nextDirection = Direction.LEFT;
					break;
				case -1:
					nextDirection = Direction.RIGHT;
					break;
			}
			switch (currentLocationPoint.getY() - nextPoint.getY()){
				case 1:
					nextDirection = Direction.DOWN;
					break;
				case -1:
					nextDirection = Direction.UP;
					break;
			}
			faceDirection(currentDirection, nextDirection);
			actions.add(Action.FORWARD);
			currentDirection = nextDirection;
			currentLocationPoint = nextPoint;
		}
	}
	
	/* Marks the unexplored neighbors of the Node at 'point' as HAZARDOUS.
	 * Called on currentPoint when a stench/breeze is perceived. 
	 */
	
	/* Identifies a neighbor of 'point' that has been marked EXPLORED.
	 * Adds actions to the actions queue to navigate to that Point.
	 */
	private void goToExploredNeighborOf(Point point) {
		for (Point adjacentPoint : cave.getKnownAdjacentPoints(point)) {
			Node adjacentNode = cave.getNode(adjacentPoint);
			if (adjacentNode.containsMarker(Marker.EXPLORED)) {
				navigate(point, adjacentPoint);
				break;
			}
		}
	}

	/* Navigates a path from origin to destination.
     * Adds all necessary actions to the actions queue.
	 */
	private void navigate(Point origin, Point destination) {
		Stack<Point> path = cave.getPath(origin, destination, direction);
		getActionsFromPoints(path);
	}

	/* Sets Point.maxX and Point.maxY to their respective arguments.
	 * Note that Point.maxX and Point.maxY are inclusive bounds.
	 * But, this method mandates that you pass currentPoint.getX(), .getY() directly.
	 * Deletes out-of-bounds nodes in the cave.
	 */
	private void markOutOfBounds(int x, int y) {
		switch (direction) {
			case RIGHT:
				setMaxX(x - 1);
				break;
			case UP:
				setMaxY(y - 1);
				break;
			default:
				break;
		}
		cave.deleteOutOfBounds();
	}
	
	// ======================================================================
	// BEGIN ADDITIONAL CLASSES
	// ======================================================================
	
  	/*
     * Calculates the amount of extra cost due to turns for a current point
     * to another. Adds that to heuristic
     */
	public static int getTurnHeuristic(Point point1, Point point2, MyAI.Direction direction) {
		int dX = point1.getX() - point2.getX();
		int dY = point1.getY() - point2.getY();
		int cost = 0; // cost of the turning
    
		if (dX > 0) {
			switch (direction) {
				case RIGHT:
			  		cost += 2;
			  		break;
		        case UP:
		        case DOWN:
		        		++cost;
		        		break;
		        	default:
		        		break;
			}
		}
		else if (dX < 0) {
			switch(direction) {
				case LEFT:
					cost += 2;
					break;
				case UP:
				case DOWN:
					++cost;
					break;
				default:
					break;
			}
		}
		else if (dY > 0) {
			switch(direction) {
				case UP:
					cost += 2;
					break;
				case LEFT:
				case RIGHT:
					++cost;
					break;
				default:
					break;
			}
		}
		else if(dY < 0) {
			switch(direction) {
	  			case DOWN:
	  				cost += 2;
	  				break;
	  			case LEFT:
	  			case RIGHT:
	  				++cost;
	  				break;
	  			default:
	  				break;
			}
		}
		return cost;
	}
  
	public static int getManhattanDistance(Point point1, Point point2) {
		return Math.abs(point1.getX() - point2.getX()) + Math.abs(point1.getY() - point2.getY());
	}

    /* Returns true if "origin" is facing the same direction as "target"
     */
    public static boolean isPointFacing(Point origin, Point target, MyAI.Direction direction) {
        switch (direction) {
            case UP:
                if (origin.getX() == target.getX() && (target.getY() - origin.getY() > 0)) {
                    return true;
                }
                break;
          	case DOWN:
                if (origin.getX() == target.getX() && (target.getY() - origin.getY() < 0)) {
                    return true;
                }
                break;
          	case RIGHT:
                if (origin.getY() == target.getY() && target.getX() - origin.getX() > 0)
                    return true;
                break;
          	case LEFT:
          		if (origin.getY() == target.getY() && target.getX() - origin.getX() < 0)
                    return true;
          		break;
        }
		return false;
    }
    
    public static Point getStartingPoint() {
		return new Point(1, 1);
	}
    
    enum Marker {
		UNEXPLORED,
		EXPLORED,
		PITWARNING,
		PIT,
		WUMPUSWARNING,
		WUMPUS,
		WALL
	}
    
    private static final int minX = 1;
	private static int maxX = 7;
	private static final int minY = 1;
	private static int maxY = 7;
	
	public static void defaultMax() {
		setMaxX(7);
		setMaxY(7);
	}
	
	public static int getMaxX() {
		return maxX;
	}

	public static void setMaxX(int localMaxX) {
		maxX = localMaxX;
	}
	
	public static int getMaxY() {
		return maxY;
	}

	public static void setMaxY(int localMaxY) {
		maxY = localMaxY;
	}

	public static int getMinX() {
		return minX;
	}

	public static int getMinY() {
		return minY;
	}

private static class Point {
	
	private int x;
	private int y;
	
	public Point(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public Point(Point other) {
		setX(other.getX());
		setY(other.getY());
	}
	
	@Override
	public String toString() {
		return "Point(" + getX() + ", " + getY() + ")";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Point)) {
			return false;
		}
		Point otherPoint = (Point)other;
		return getX() == otherPoint.getX() && getY() == otherPoint.getY();
	}
	
	@Override
	public int hashCode() {
		String asString = String.format("%d%d", getX(), getY());
		return asString.hashCode();
	}
	
	public boolean outOfBounds() {
		return getX() < getMinX() || getX() > getMaxX() || getY() < getMinY() || getY() > getMaxY();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void addX(int steps) {
		x += steps;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void addY(int steps) {
		y += steps;
	}
	
	public void set(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public boolean atStart() {
		return getX() == 1 && getY() == 1;
	}
}

    
    private class Node {
    	
    	private Set<Marker> markers;
    	private Node above;
    	private Node below;
    	private Node left;
    	private Node right;
    	
    	public Node(Marker marker) {
    		markers = new HashSet<>();
    		markers.add(marker);
    		above = null;
    		below = null;
    		left = null;
    		right = null; 
    	}
    	
    	public Node() {
    		this(Marker.UNEXPLORED);
    	}
    	
    	public Node(Node other) {
    		markers = other.markers;
    		setAbove(above);
    		setBelow(below);
    		setLeft(left);
    		setRight(right);
    	}
    	
    	@Override
    	public String toString() {
    		return String.format("Node(%s)", markers);
    	}
    	
    	public Set<Marker> getMarkers() {
    		return markers;
    	}
    	
    	public boolean containsMarker(Marker marker) {
    		return markers.contains(marker);
    	}
    	
    	public boolean isDangerous() {
    		return markers.contains(Marker.PIT) || markers.contains(Marker.WUMPUS) || markers.contains(Marker.PITWARNING) || markers.contains(Marker.WUMPUSWARNING) || markers.contains(Marker.WALL);
    	}
    	
    	/* If trying to mark the node as EXPLORED,
    	 * automatically unmark it as UNEXPLORED.
    	 */
    	public void addMarker(Marker marker) {
    		if (marker == Marker.EXPLORED && containsMarker(Marker.UNEXPLORED)) {
    			removeMarker(Marker.UNEXPLORED);
    		}
    		markers.add(marker);
    	}
    	
    	public void removeMarker(Marker marker) {
    		markers.remove(marker);
    	}

    	public Node getAbove() {
    		return above;
    	}

    	public void setAbove(Node above) {
    		this.above = above;
    	}

    	public Node getBelow() {
    		return below;
    	}

    	public void setBelow(Node below) {
    		this.below = below;
    	}

    	public Node getLeft() {
    		return left;
    	}

    	public void setLeft(Node left) {
    		this.left = left;
    	}

    	public Node getRight() {
    		return right;
    	}

    	public void setRight(Node right) {
    		this.right = right;
    	}
    	
    }

	
	private class Graph {
		private Map<Point,Node> nodes; // Map to store points
	  	private Set<Point> unexplored;
	  	boolean wumpusFound;
	  	Point wumpusPoint;
		
		public Graph() {
			nodes = new HashMap<>();
	      	unexplored = new HashSet<>();
	      	wumpusFound = false;
	      	wumpusPoint = null;
		}
		
		public Graph(Node startingNode) {
			this();
			Point initialPoint = new Point(1, 1);
			Node copy = new Node(startingNode);
			nodes.put(initialPoint, copy);
			expandUnexploredNeighbors(copy, initialPoint, new HashSet<>());
		}
		
		public int size() {
			return nodes.size();
		}
		
		public boolean isWumpusFound() {
			return wumpusFound;
		}
		
		public Point getWumpusPoint() {
			return wumpusPoint;
		}
		
		/*
		 * If it doesn't exist, adds a new Node relative to the direction the Agent is facing;
		 *   returns the node added.
		 * If there already is a node in "from's" direction, does nothing;
		 *   returns that existing node. 
		 *   
		 * @param destinationPoint - has already been updated to the coordinates of destination
		 */
		public void addNode(Point currentPoint, MyAI.Direction direction, Set<Marker> dangers) {
			Node currentNode = nodes.get(currentPoint);
			Node destination;
			
			// for starting node
			if (currentNode == null) {
				destination = new Node(Marker.EXPLORED);
				Point copyPoint = new Point(currentPoint);
				nodes.put(copyPoint, destination);
			}
			else {
				destination = getAdjacentNode(currentNode, direction);
				if (destination == null) {
					destination = branchDestinationNode(currentNode, direction);
					
					// insert a copy of currentPoint into the map;
					// otherwise its x/y values will update inside the map
					Point copyPoint = new Point(currentPoint);
					nodes.put(copyPoint, destination);	
				}
			}
			
			expandUnexploredNeighbors(destination, currentPoint, dangers);
		}
		
		/* Branches a new Node off of origin in direction 'direction'.
		 *  e.g., direction=LEFT --> origin.LEFT = destination; destination.RIGHT = origin;
		 * Returns the destination node.
		 */
		private Node branchDestinationNode(Node origin, MyAI.Direction direction) {
			Node destination = new Node();
			switch (direction) {
				case UP:
					origin.setAbove(destination);
					destination.setBelow(origin);
					break;
				case DOWN:
					origin.setBelow(destination);
					destination.setAbove(origin);
					break;
				case RIGHT:
					origin.setRight(destination);
					destination.setLeft(origin);
					break;
				case LEFT:
					origin.setLeft(destination);
					destination.setRight(origin);
					break;
				default:
					throw new WumpusWorldException("unexpected direction");
			}
			return destination;
		}
		
		/**
		 * Expands the immediate neighbors of the argument "point".
		 * 
		 * For each immediate neighbor n of "point" p:
		 *   if n has already been explored:
		 *     check for hazards (PITWARNING, WUMPUSWARNING)
		 *     if p is safe, and n is marked as potentially dangerous:
		 *       then n is safe
		 *     otherwise:
		 *       n is guaranteed to be a PIT or WUMPUS
		 *   
		 *   else, if n is unknown:
		 *     add it to the graph
		 *     if p perceives a hazard, mark n as potentially hazardous
		 *     otherwise, if p is safe, add n to the set of unexplored, safe nodes
		 * 
		 * @param from
		 * @param point
		 * @param dangers
		 */
		private void expandUnexploredNeighbors(Node from, Point point, Set<Marker> dangers) {
			for (Map.Entry<MyAI.Direction, Point> entry : getAdjacentDirectionalPoints(point).entrySet()) {
				MyAI.Direction direction = entry.getKey();
				Point adjacentPoint = entry.getValue();
				
				if (wumpusFound) {
					dangers.remove(Marker.WUMPUSWARNING);
				}

				// If the existing node has the same warning before, then it confirms the danger
				if (nodes.containsKey(adjacentPoint)) {
					if (!adjacentPoint.outOfBounds()) {
						Node adjacentNode = nodes.get(adjacentPoint);
						
						// a pit has been isolated
						if (adjacentNode.containsMarker(Marker.PITWARNING)) {
							if (dangers.contains(Marker.PITWARNING)) {
								adjacentNode.addMarker(Marker.PIT);
							}
							else {
								adjacentNode.removeMarker(Marker.PITWARNING);
			                  	if (!adjacentNode.isDangerous()) {
			                  		unexplored.add(adjacentPoint);	
			                  	}
							}
						}
		              	
		              	// wumpus has been isolated
						if (adjacentNode.containsMarker(Marker.WUMPUSWARNING)) {
							if (dangers.contains(Marker.WUMPUSWARNING)) {
								adjacentNode.addMarker(Marker.WUMPUS);
			                    removeWumpusWarning();
			                    wumpusFound = true;
			                    wumpusPoint = new Point(adjacentPoint);
			                    dangers.remove(Marker.WUMPUSWARNING);
			                    // function to delete all WUMPUS WARNINGS AT PLACES NOT WUMPUS
							}
							else {
								adjacentNode.removeMarker(Marker.WUMPUSWARNING);
			                  	if (!adjacentNode.isDangerous()) {
			                  		unexplored.add(adjacentPoint);	
			                  	}
							}
						}
					}
					
				}
				else {
					Node destination = branchDestinationNode(from, direction);
					
					if (adjacentPoint.outOfBounds()) {
						destination.addMarker(Marker.WALL);
					}
					else {
						destination.addMarker(Marker.UNEXPLORED);
	                  
	                  	// only add nodes that are guaranteed to be safe
						if (dangers.isEmpty()) {
							unexplored.add(adjacentPoint);
						}
						
						for (Marker marker : dangers) { // if wumpus is alive after shooting arrow, do not add to current facing direction
							if (wumpusFound && marker == Marker.WUMPUSWARNING) {
								continue;
							}
							destination.addMarker(marker);
						}
					}
					
					nodes.put(adjacentPoint, destination);
				}
			}
		}
		
		public Point getAdjacentPoint(Point from, MyAI.Direction direction) {
			int x = from.getX();
			int y = from.getY();
			switch (direction) {
				case UP:
					++y;
					break;
				case DOWN:
					--y;
					break;
				case RIGHT:
					++x;
					break;
				case LEFT:
					--x;
					break;
				default:
					throw new WumpusWorldException("unexpected direction");
			}
			return new Point(x, y);
		}
		
		/*
		 * Returns the adjacent node relative to "from's" direction.
		 * For example, if direction = RIGHT, returns from.right
		 * (the node leading to the from's right neighbor).
		 */
		public Node getAdjacentNode(Node from, MyAI.Direction direction) {
			Node adjacent;
			switch (direction) {
				case UP:
					adjacent = from.getAbove();
					break;
				case DOWN:
					adjacent = from.getBelow();
					break;
				case RIGHT:
					adjacent = from.getRight();
					break;
				case LEFT:
					adjacent = from.getLeft();
					break;
				default:
					throw new WumpusWorldException("unexpected direction");
			}
			return adjacent;
		}
		
		/* Returns a map of ALL POSSIBLE immediate neighbors of 'point' - 
		 *   e.g., {LEFT: <Point to the left>, RIGHT: <Point to the right>, ...}
		 * All possible neighbors - meaning even neighbors which do not yet exist (in the graph).
		 */
		public Map<MyAI.Direction, Point> getAdjacentDirectionalPoints(Point point) {
			Point left = new Point(point.getX() - 1, point.getY());
			Point right = new Point(point.getX() + 1, point.getY());
			Point up = new Point(point.getX(), point.getY() + 1);
			Point down = new Point(point.getX(), point.getY() - 1);
			
			Map<MyAI.Direction, Point> result = new HashMap<>();
			result.put(MyAI.Direction.LEFT, left);
			result.put(MyAI.Direction.RIGHT, right);
			result.put(MyAI.Direction.UP, up);
			result.put(MyAI.Direction.DOWN, down);
			
			return result;
		}
		
		/*
		 * Returns a set of Points detailing "point's" immediate KNOWN neighbors.
		 */
		public Set<Point> getKnownAdjacentPoints(Point point) {
			Set<Point> neighbors = new HashSet<>();
			for (Point adjacentPoint : getAdjacentDirectionalPoints(point).values()) {
				if (nodes.containsKey(adjacentPoint)) {
					neighbors.add(adjacentPoint);
				}
			}
			
			return neighbors;
		}
		
		/*
		 * Returns a set of all non-null KNOWN neighbors of point.
		 */
		public Set<Node> getKnownNeighbors(Point point) {
			Set<Node> result = new HashSet<>();
			for (Point adjacentPoint : getKnownAdjacentPoints(point)) {
				Node node = nodes.get(adjacentPoint);
				if (node != null) {
					result.add(node);
				}
			}
			return result;
		}
		
		/* Public accessor method for retrieving a node by its point.
		 * Returns the Node, if it exists, or null if it doesn't.
		 */
		public Node getNode(Point point) {
			return nodes.get(point);
		}
		

		
		/*
	     * Finds and returns the closest unexplored Point from origin.
	     */
		public Point getClosestUnexploredPoint(Point origin, MyAI.Direction directionPreference) {
			Point optimal = null;
			int shortestDistance = Integer.MAX_VALUE;
			for (Point point : unexplored) {
				int distanceFromPoint = getManhattanDistance(origin, point);
				if (distanceFromPoint < shortestDistance) {
	                // if "point" is in the same Direction relative to "optimal":
	                //     optimal = point
					optimal = point;
					shortestDistance = distanceFromPoint;
				}
	          	else if (distanceFromPoint == shortestDistance && isPointFacing(origin, point, directionPreference)) {
	                optimal = point;
	                shortestDistance = distanceFromPoint;
	            }
			}
			unexplored.remove(optimal);
			return optimal;
		}
	  
		/* 
		 * Performs a greedy best-first search from origin to destination.
		 * Returns a Stack of Points of the discovered path.
		 */
		public Stack<Point> getPath(Point origin, Point destination, MyAI.Direction currentDirection) {
			Stack<Point> result = new Stack<>();
			Map<Point, Point> parents = new HashMap<>();		// map {child: parent}
			Comparator<Object> pointComparator = new PointComparator(destination, currentDirection);
			Queue<Point> frontier = new PriorityQueue<Point>(pointComparator);
		  
			frontier.add(origin);
			parents.put(origin, null);
			
			
			// if the search stops because the frontier is empty,
			// no path was found?
			while (!frontier.isEmpty()) {
				// find the least-cost node
				Point current = frontier.poll();
				if (current.equals(destination)) {
						// an optimal (greedy) path has been found
						break;
				}
			  
				// TODO: avoid hazards
				// expand all children of current node
				Set<Point> known = getKnownAdjacentPoints(current);
				for (Point child : known) {
					// graph search; ignore already-stepped-on points
					if (!parents.containsKey(child)) {
						if (!nodes.get(child).isDangerous()) {
							frontier.add(child);
						}
						parents.put(child, current);
					}
				}
			}
		  
			// find least-cost path from destination -> origin
			Point current = destination;
			while (parents.get(current) != null) {
				result.push(current);
				current = parents.get(current);
			}
			
			return result;
		}
		
		public void deleteOutOfBounds() {
			for (Iterator<Point> it = unexplored.iterator(); it.hasNext(); ) {
				Point point = it.next();
				if (point.outOfBounds()) {
					it.remove();
				}
			}
		}

		/* Iterates through the graph and removes all WUMPUSWARNING markers.
		 * Used when the wumpus is killed.
		 */
		public void removeWumpusWarning() {
			for (Map.Entry<Point, Node> entry : nodes.entrySet()) {
				Point point = entry.getKey();
				Node node = entry.getValue();
				if (node.containsMarker(Marker.WUMPUSWARNING) && !node.containsMarker(Marker.WUMPUS)) {
					node.removeMarker(Marker.WUMPUSWARNING);
					
					if (!node.isDangerous()) {
						unexplored.add(point);
					}
				}
			}
		}
		
		public void addToUnexplored(Point point) {
			unexplored.add(point);
		}
		
		public boolean containsPoint(Point point) {
			return nodes.containsKey(point);
		}
	      
	  	/* 
	  	 * Comparator to order by increasing Manhattan distance (i.e., least to greatest).
	  	 */
	  	private class PointComparator implements Comparator<Object> {
	  		private Point destination;
	      	private MyAI.Direction direction;
	  	  
	  		public PointComparator(Point destination, MyAI.Direction direction) {
	  			this.destination = destination;
	          	this.direction = direction;
	  		}
	  		
	  		@Override
	  		public int compare(Object a1, Object b1) {
	  			Point a = (Point)a1;
	  			Point b = (Point)b1;
	          
	          	int costA = getTurnHeuristic(a, destination, direction);
	          	int costB = getTurnHeuristic(b, destination, direction);
	          	int estimateA = getManhattanDistance(a, destination);
	          	int estimateB = getManhattanDistance(b, destination);
	  			
	          	return (costA + estimateA) - (costB + estimateB);
	  		}
	  	}
	  	
	}
	
	private class WumpusWorldException extends RuntimeException {
		
		public WumpusWorldException(String message) {
			super(message);
		}
		
		public WumpusWorldException() {
			this("");
		}
	}

	// ======================================================================
	// END ADDITIONAL CLASSES
	// ======================================================================

	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}

