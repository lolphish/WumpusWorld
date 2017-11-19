
public class Node {
	
	static enum Marker {
		UNEXPLORED,
		EXPLORED,
		BREEZE,
		STENCH,
		HAZARDOUS,
		WALL
	}
	
	private Marker marker;
	private Node above;
	private Node below;
	private Node left;
	private Node right;
	
	public Node(Marker marker) {
		this.marker = marker;
		above = null;
		below = null;
		left = null;
		right = null; 
	}
	
	public Node() {
		this(Marker.UNEXPLORED);
	}
	
	@Override
	public String toString() {
		return String.format("Node(%s)", marker);
	}
	
	public Marker getMarker() {
		return marker;
	}
	
	public boolean isDangerous() {
		return marker == marker.HAZARDOUS;
	}
	
	/* Prioritize WALL;
	 * if this.marker == WALL, don't override it.
	 *   - useful when MyAI marks neighbors of current node as hazardous
	 *     upon perceiving a breeze/stench
	 * Otherwise, set this.marker to the argument marker.
	 */
	public void setMarker(Marker marker) {
		switch (this.marker) {
			case WALL:
				break;
			default:
				this.marker = marker;
		}
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
