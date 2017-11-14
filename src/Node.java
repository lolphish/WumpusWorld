import java.util.Arrays;
import java.util.List;

public class Node {
	public static final double UNEXPLORED = -1;
	public static final double EMPTY = 1;
	public static final double HAZARDOUS = 0;
	public static final double SENSED = 0.33;
	
	private boolean startingPoint;
	private double acceptanceProbability;
	private Node above;
	private Node below;
	private Node left;
	private Node right;
	
	public Node(double acceptanceProbability, boolean startingPoint) {
		this.startingPoint = startingPoint;
		setAcceptanceProbability(acceptanceProbability);
		above = null;
		below = null;
		left = null;
		right = null; 
	}
	
	public Node() {
		this(UNEXPLORED, false);
	}
	
	public Node(double acceptanceProbability) {
		this(acceptanceProbability, false);
	}
	
	public Node(boolean startingPoint) {
		this(UNEXPLORED, startingPoint);
	}
	
	@Override
	public String toString() {
		return String.format("Node(probability=%.2f, starting=%s)", getAcceptanceProbability(), startingPoint);
	}
	
	/*
	 * Convenience method.
	 * Returns a list of all of this node's neighbors.
	 * [above, below, right, left]
	 */
	public List<Node> getNeighbors() {
		return Arrays.asList(getAbove(), getBelow(), getRight(), getLeft());
	}

	public boolean isStartingPoint() {
		return startingPoint;
	}

	public double getAcceptanceProbability() {
		return acceptanceProbability;
	}

	public void setAcceptanceProbability(double acceptanceProbability) {
		this.acceptanceProbability = acceptanceProbability;
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
