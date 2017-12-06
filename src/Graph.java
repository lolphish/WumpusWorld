import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


public class Graph {
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
	public void addNode(Point currentPoint, MyAI.Direction direction, Set<Node.Marker> dangers) {
		Node currentNode = nodes.get(currentPoint);
		Node destination;
		
		// for starting node
		if (currentNode == null) {
			destination = new Node(Node.Marker.EXPLORED);
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
	private void expandUnexploredNeighbors(Node from, Point point, Set<Node.Marker> dangers) {
		for (Map.Entry<MyAI.Direction, Point> entry : getAdjacentDirectionalPoints(point).entrySet()) {
			MyAI.Direction direction = entry.getKey();
			Point adjacentPoint = entry.getValue();
			
			if (wumpusFound) {
				dangers.remove(Node.Marker.WUMPUSWARNING);
			}

			// If the existing node has the same warning before, then it confirms the danger
			if (nodes.containsKey(adjacentPoint)) {
				if (!adjacentPoint.outOfBounds()) {
					Node adjacentNode = nodes.get(adjacentPoint);
					
					// a pit has been isolated
					if (adjacentNode.containsMarker(Node.Marker.PITWARNING)) {
						if (dangers.contains(Node.Marker.PITWARNING)) {
							adjacentNode.addMarker(Node.Marker.PIT);
						}
						else {
							adjacentNode.removeMarker(Node.Marker.PITWARNING);
		                  	if (!adjacentNode.isDangerous()) {
		                  		unexplored.add(adjacentPoint);	
		                  	}
						}
					}
	              	
	              	// wumpus has been isolated
					if (adjacentNode.containsMarker(Node.Marker.WUMPUSWARNING)) {
						if (dangers.contains(Node.Marker.WUMPUSWARNING)) {
							adjacentNode.addMarker(Node.Marker.WUMPUS);
		                    removeWumpusWarning();
		                    wumpusFound = true;
		                    wumpusPoint = new Point(adjacentPoint);
		                    dangers.remove(Node.Marker.WUMPUSWARNING);
		                    // function to delete all WUMPUS WARNINGS AT PLACES NOT WUMPUS
						}
						else {
							adjacentNode.removeMarker(Node.Marker.WUMPUSWARNING);
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
					destination.addMarker(Node.Marker.WALL);
				}
				else {
					destination.addMarker(Node.Marker.UNEXPLORED);
                  
                  	// only add nodes that are guaranteed to be safe
					if (dangers.isEmpty()) {
						unexplored.add(adjacentPoint);
					}
					
					for (Node.Marker marker : dangers) { // if wumpus is alive after shooting arrow, do not add to current facing direction
						if (wumpusFound && marker == Node.Marker.WUMPUSWARNING) {
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
			if (node.containsMarker(Node.Marker.WUMPUSWARNING) && !node.containsMarker(Node.Marker.WUMPUS)) {
				node.removeMarker(Node.Marker.WUMPUSWARNING);
				
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

