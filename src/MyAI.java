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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


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
	
	enum Marker {
		UNEXPLORED,
		EXPLORED,
		PITWARNING,
		PIT,
		WUMPUSWARNING,
		WUMPUS,
		WALL
	}
  
	private Direction direction;
	private Point currentPoint;
	private Node currentNode;
	private Graph cave;
	private Queue<Action> actions;
	private boolean hasArrow;
	private boolean wumpusAlive;
	private boolean climbOut;
	
	private static final int minX = 1;
	private static int maxX = Integer.MAX_VALUE;
	private static final int minY = 1;
	private static int maxY = Integer.MAX_VALUE;
	
	public static Point getStartingPoint() {
		return new Point(1, 1);
	}
	
	public static void setMaxX(int maxX) {
		MyAI.maxX = maxX;
	}
	
	public static void setMaxY(int maxY) {
		MyAI.maxY = maxY;
	}
	
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		currentPoint = getStartingPoint();		// initialized to (1, 1)
//		currentNode = new Node(Marker.EXPLORED);
		currentNode = null;
		cave = new Graph();
		
		hasArrow = true;
		wumpusAlive = true;
		climbOut = false;
		
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
			// TODO: add right/top wall
			markOutOfBounds();
			cave.getNode(currentPoint).addMarker(Marker.WALL);
			currentPoint = getLocalOriginPoint(currentPoint);
			
			// TODO: turn to a valid position
		}
      
		Set<Marker> dangers = new HashSet<>();

		// if an upcoming action has been queued, prioritize it
		if (!actions.isEmpty()) {
			return dequeueAction(dangers);
		}
		
		// attempt to kill the Wumpus
		if (stench && wumpusAlive && hasArrow) {
//            hasArrow = false;
//            return Action.SHOOT;
		}
		
		Action action;
//		currentNode.addMarker(Marker.EXPLORED);
		if (breeze || stench) {
			// TODO: error if a breeze/stench is perceived at the starting point
			if (currentPoint.atStart()) {
				// climb out?
				climbOut = true;
				return getAction(stench, breeze, glitter, bump, scream);
			}
			
			// TODO: what if a breeze and stench exist on the same point?
			// after the wumpus dies, the node would no longer be marked WUMPUSWARNING
			// but might still need to be marked PITWARNING
			if (breeze) {
				dangers.add(Marker.PITWARNING);
			}
			if (stench) {
				dangers.add(Marker.WUMPUSWARNING);
			}
			action = addAndGoToClosestPoint(dangers);
//			currentNode = cave.addNode(currentNode, direction, currentPoint, dangers);
//			Point closestPoint = cave.getClosestUnexploredPoint(currentPoint);
//			navigate(currentPoint, closestPoint);
//			action = dequeueAction(dangers);
		}
		else {
			action = addAndGoToClosestPoint(dangers);
//			currentNode = cave.addNode(currentNode, direction, currentPoint, dangers);
//			Point closestPoint = cave.getClosestUnexploredPoint(currentPoint);
//			navigate(currentPoint, closestPoint);
//			action = dequeueAction(dangers);
		}
		
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
	
	private Action moveForward(Set<Marker> dangers) {
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
	private Action dequeueAction(Set<Marker> dangers) throws NoSuchElementException {
		Action nextAction = actions.remove();
		switch (nextAction) {
			case TURN_LEFT:
			nextAction = turnLeft();
			break;
		case TURN_RIGHT:
			nextAction = turnRight();
			break;
		case FORWARD:
			nextAction = moveForward(dangers);
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
	/*
	 * Helper function for restoring currentPoint after bumping into a wall.
	 * After perceiving a bump, currentPoint has already been updated to out-of-bounds.
	 * Returns the valid Point that it came from.
	 * example: currentPoint = getLocalOriginPoint(currentPoint);
	 */
	private Point getLocalOriginPoint(Point point) {
		Node target = cave.getNode(point);
		if (target == null) {
			throw new WumpusWorldException("received null Node target; expected valid");
		}
		for (Point adjacentPoint : cave.getKnownAdjacentPoints(point)) {
			Node adjacentNode = cave.getNode(adjacentPoint);
			if (adjacentNode.containsMarker(Marker.EXPLORED)) {
				return adjacentPoint;
			}
		}
		
		// the wall had no explored neighbors, which should NEVER happen
		throw new WumpusWorldException("expected exactly 1 valid neighboring node");
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
		Stack<Point> path = cave.getPath(origin, destination);
		getActionsFromPoints(path);
	}

	private void markOutOfBounds() {
		switch (direction) {
			case RIGHT:
				setMaxX(currentPoint.getX() - 1);
				break;
			case UP:
				setMaxY(currentPoint.getY() - 1);
				break;
			default:
				break;
		}
		cave.deleteOutOfBounds();
	}
	
	/* Convenience method.
	 * Adds current point, finds the closest point, and returns the first necessary Action to take.
	 * If no closest point is found, begins navigating back to the start.
	 */
	private Action addAndGoToClosestPoint(Set<Marker> dangers) {
		currentNode = cave.addNode(currentNode, direction, currentPoint, dangers);
		Point closestPoint = cave.getClosestUnexploredPoint(currentPoint);
		if (closestPoint == null) {
			climbOut = true;
			actions.clear();
			return getAction(false, false, false, false, false);
		}
		navigate(currentPoint, closestPoint);
		return dequeueAction(dangers);
	}
	
	
	// ======================================================================
	// OTHER CLASSES
	// ======================================================================
	
	public class Graph {
		private Map<Point,Node> nodes; // Map to store points
	  	private Set<Point> unexplored;
		
		public Graph() {
			nodes = new HashMap<>();
	      	unexplored = new HashSet<>();
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
		
		/*
		 * If it doesn't exist, adds a new Node relative to the direction the Agent is facing;
		 *   returns the node added.
		 * If there already is a node in "from's" direction, does nothing;
		 *   returns that existing node. 
		 *   
		 * @param destinationPoint - has already been updated to the coordinates of destination
		 */
		public Node addNode(Node node, MyAI.Direction direction, Point currentPoint, Set<Marker> dangers) {
			Node destination;
			// for starting node
			if (node == null) {
				destination = new Node(Marker.EXPLORED);
				Point copyPoint = new Point(currentPoint);
				nodes.put(copyPoint, destination);
			}
			else if ((destination = getAdjacentNode(node, direction)) == null) {
				destination = branchDestinationNode(node, direction);
				
				// insert a copy of currentPoint into the map;
				// otherwise its x/y values will update inside the map
				Point copyPoint = new Point(currentPoint);
				nodes.put(copyPoint, destination);
			}
			destination.addMarker(Marker.EXPLORED);
			expandUnexploredNeighbors(destination, currentPoint, dangers);
//			setNeighbors(copyPoint, destination);
			return destination;
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
		
		/* Expands the immediate unknown neighbors of the argument node/point, marking them as UNEXPLORED.
		 */
		private void expandUnexploredNeighbors(Node from, Point point, Set<Marker> dangers) {
			for (Map.Entry<MyAI.Direction, Point> entry : getAdjacentDirectionalPoints(point).entrySet()) {
				MyAI.Direction direction = entry.getKey();
				Point adjacentPoint = entry.getValue();

				// If the existing node has the same warning before, then it confirms the danger
				if (nodes.containsKey(adjacentPoint)) {
					for (Marker marker : dangers) {
						if (marker == Marker.PITWARNING && nodes.get(adjacentPoint).containsMarker(marker)) {
							nodes.get(adjacentPoint).addMarker(Marker.PIT);
						}
						else if (marker == Marker.WUMPUSWARNING && nodes.get(adjacentPoint).containsMarker(marker)) {
							nodes.get(adjacentPoint).addMarker(Marker.WUMPUS);
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
						if (dangers.isEmpty()) {
							unexplored.add(adjacentPoint);
						}
					}
					for (Marker marker : dangers) {
						destination.addMarker(marker);
					}
					nodes.put(adjacentPoint, destination);
				}
			}
		}
		
		/*
		 * Returns the adjacent node relative to "from's" direction.
		 * For example, if direction = RIGHT, returns from.right
		 * (the node leading to the from's right neighbor).
		 */
		private Node getAdjacentNode(Node from, MyAI.Direction direction) {
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
		
		/*
		 * Finds if there are possible neighbors in the map and update
		 * current node point
		 */
		private void setNeighbors(Point currentPoint, Node destination)
		{
			Point left = new Point(currentPoint.getX()-1, currentPoint.getY());
			Point right = new Point(currentPoint.getX()+1, currentPoint.getY());
			Point up = new Point(currentPoint.getX(), currentPoint.getY()+1);
			Point down = new Point(currentPoint.getX(), currentPoint.getY()-1);
			destination.setLeft(nodes.get(left));
			destination.setRight(nodes.get(right));
			destination.setAbove(nodes.get(up));
			destination.setBelow(nodes.get(down));
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
		
		public int getManhattanDistance(Point point1, Point point2) {
			return Math.abs(point1.getX() - point2.getX()) + Math.abs(point1.getY() - point2.getY());
		}
		
		/*
	     * Finds closest unexplored node from currentValue
	     */
		public Point getClosestUnexploredPoint(Point currentPoint) {
			Point minPoint = null;
			int minManhattanDistance = Integer.MAX_VALUE; //getManhattanDistance(currentPoint, minPoint);
			for (Point point : unexplored) {
				int tempManhattanDistance = getManhattanDistance(currentPoint, point);
				if (tempManhattanDistance < minManhattanDistance) {
					minPoint = point;
					minManhattanDistance = tempManhattanDistance;
				}
			}
			unexplored.remove(minPoint);
			return minPoint;
		}
	  
		/* 
		 * Performs a greedy best-first search from origin to destination.
		 * Returns a Stack of Points of the discovered path.
		 */
		public Stack<Point> getPath(Point origin, Point destination) {
			Stack<Point> result = new Stack<>();
			Map<Point, Point> parents = new HashMap<>();
			Comparator<Object> pointComparator = new PointComparator(destination);
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
				for (Point child : getKnownAdjacentPoints(current)) {
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
	  
		/* 
		 * Comparator to order by increasing Manhattan distance (i.e., least to greatest).
		 */
		private class PointComparator implements Comparator<Object> {
			private Point destination;
		  
			public PointComparator(Point destination) {
				this.destination = destination;
			}
			
			@Override
			public int compare(Object a1, Object b1) {
				Point a = (Point)a1;
				Point b = (Point)b1;
				return getManhattanDistance(a, destination) - getManhattanDistance(b, destination);
			}
		}
	  	
	}
	
	
	public class Node {
		
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
			return markers.contains(Marker.PIT) || markers.contains(Marker.WUMPUS) || markers.contains(Marker.PITWARNING) || markers.contains(Marker.WUMPUSWARNING);
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


static class Point {
	
	
	
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

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}
}

	
	
	// ======================================================================
	// END OTHER CLASSES
	// ======================================================================
	

	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}
