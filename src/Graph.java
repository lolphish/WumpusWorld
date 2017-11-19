import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


public class Graph {
	private Map<Point,Node> nodes; // Map to store points
	
	public Graph() {
		nodes = new HashMap<>();
	}
	
	public Graph(Node startingNode) {
		this();
		Point initialPoint = new Point(1, 1);
		nodes.put(initialPoint, startingNode);
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
	 * @param currentPoint - has already been updated to the coordinates of destination
	 */
	public Node addNode(Node origin, MyAI.Direction direction, Point currentPoint) {
		Node destination = getAdjacentNode(origin, direction);
		if (destination != null) {
			return destination;
		}
		
		destination = branchDestinationNode(origin, direction);
		
		// insert a copy of currentPoint into the map;
		// otherwise its x/y values will update inside the map
		Point copyPoint = new Point(currentPoint);
		nodes.put(copyPoint, destination);
		expandUnexploredNeighbors(destination, currentPoint);
//		setNeighbors(copyPoint, destination);
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
	private void expandUnexploredNeighbors(Node from, Point point) {
		for (Map.Entry<MyAI.Direction, Point> entry : getAdjacentDirectionalPoints(point).entrySet()) {
			MyAI.Direction direction = entry.getKey();
			Point adjacentPoint = entry.getValue();
			if (!nodes.containsKey(adjacentPoint)) {
				Node destination = branchDestinationNode(from, direction);
				destination.setMarker(adjacentPoint.outOfBounds() ? Node.Marker.WALL : Node.Marker.UNEXPLORED);
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
	
	/* Marks the node at 'point' with 'marker', if it exists.
	 * Does nothing if it doesn't exist.
	 */
	public void markNodeAtPoint(Point point, Node.Marker marker) {
		Node target = nodes.get(point);
		if (target != null) {
			target.setMarker(marker);
		}
	}
	
	/* Public accessor method for retrieving a node by its point.
	 * Returns the Node, if it exists, or null if it doesn't.
	 */
	public Node getNode(Point point) {
		return nodes.get(point);
	}
	
	public static int getManhattanDistance(Point point1, Point point2) {
		return Math.abs(point1.getX() - point2.getX()) + Math.abs(point1.getY() + point2.getY());
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


