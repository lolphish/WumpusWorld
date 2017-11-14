import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


public class Graph {
	private Set<Node> nodes;
	
	public Graph() {
		nodes = new LinkedHashSet<>();
	}
	
	public int size() {
		return nodes.size();
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	/*
	 * If it doesn't exist, adds a new Node relative to the direction the Agent is facing;
	 *   returns the node added.
	 * If there already is a node in "from's" direction, does nothing;
	 *   returns that existing node. 
	 */
	public Node addNode(Node origin, MyAI.Direction direction) {
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
		addNode(destination);
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
}
