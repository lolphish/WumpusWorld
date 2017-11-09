
public class Node {
	public static final double UNEXPLORED = -1;
	public static final double EMPTY = 1;
	public static final double HAZARDOUS = 0;
	public static final double SENSED = 0.5;
	
	private boolean startingPoint;
	private double acceptanceProbability;
	private Edge above;
	private Edge below;
	private Edge left;
	private Edge right;
	
	public Node(double acceptanceProbability, boolean startingPoint) {
		this.startingPoint = startingPoint;
		setAcceptanceProbability(acceptanceProbability);
		above = new Edge(this);
		below = new Edge(this);
		left = new Edge(this);
		right = new Edge(this);
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

	public Edge getAbove() {
		return above;
	}

	public void setAbove(Edge above) {
		this.above = above;
	}

	public Edge getBelow() {
		return below;
	}

	public void setBelow(Edge below) {
		this.below = below;
	}

	public Edge getLeft() {
		return left;
	}

	public void setLeft(Edge left) {
		this.left = left;
	}

	public Edge getRight() {
		return right;
	}

	public void setRight(Edge right) {
		this.right = right;
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
	
}
