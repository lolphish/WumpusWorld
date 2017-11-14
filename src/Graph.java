import java.util.HashMap;
import java.util.Map;


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
	 */
	public Node addNode(Node origin, MyAI.Direction direction, Point currentPoint) {
		Node destination = getAdjacentNode(origin, direction);
		if (destination != null) {
			return destination;
		}
		
		destination = new Node();
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
				throw new RuntimeException("unexpected direction");
		}
		
		Point copyPoint = new Point(currentPoint);
		nodes.put(copyPoint, destination);
		setNeighbors(copyPoint, destination);
		return destination;
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
				throw new RuntimeException("unexpected direction");
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
}
