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
	 * If it doesn't exist, adds a new Node relative to the direction the Agent is facing.
	 * Returns the node added.
	 * If there already is a node in "from's" direction, does nothing and returns that existing node. 
	 */
	public Node addNode(Node from, MyAI.Direction direction) {
		Edge adjacent = getAdjacentEdge(from, direction);
		if (adjacent.hasDestination()) {
			return adjacent.getDestination();
		}
		
		Node to = new Node();
		Edge edge = new Edge(from, to);
		Edge opposite = new Edge(to, from);
		switch (direction) {
			case UP:
				from.setAbove(edge);
				to.setBelow(opposite);
				break;
			case DOWN:
				from.setBelow(edge);
				to.setAbove(opposite);
				break;
			case RIGHT:
				from.setRight(edge);
				to.setLeft(opposite);
				break;
			case LEFT:
				from.setLeft(edge);
				to.setRight(opposite);
				break;
			default:
				throw new RuntimeException("unexpected direction");
		}
		nodes.add(to);
		return to;
	}
	
	/*
	 * Returns the adjacent edge relative to from's direction.
	 * For example, if direction = RIGHT, returns from.right
	 * (the edge leading to the from's right neighbor).
	 */
	private Edge getAdjacentEdge(Node from, MyAI.Direction direction) {
		Edge adjacent;
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
