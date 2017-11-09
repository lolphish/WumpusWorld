
public class Edge {
	private Node origin;
	private Node destination;
	
	public Edge(Node origin, Node destination) {
		setOrigin(origin);
		setDestination(destination);
	}
	
	/*
	 * Initialize a node with no neighbor - e.g., edge of the cave.
	 */
	public Edge(Node origin) {
		this(origin, null);
	}
	
	@Override
	public String toString() {
		return String.format("Edge(origin=%s, dst=%s)", getOrigin(), getDestination());
	}
	
	/*
	 * Returns true if this edge has a destination node.
	 */
	public boolean hasDestination() {
		return destination != null;
	}

	public Node getOrigin() {
		return origin;
	}

	public void setOrigin(Node origin) {
		this.origin = origin;
	}

	public Node getDestination() {
		return destination;
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}
}
