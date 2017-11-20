import java.util.HashSet;

public class Node {
	
	static enum Marker {
		UNEXPLORED,
		EXPLORED,
		PITWARNING,
		PIT,
		WUMPUSWARNING,
		WUMPUS,
		WALL
	}
	
	private HashSet<Marker> markers;
	private Node above;
	private Node below;
	private Node left;
	private Node right;
	
	public Node(Marker marker) {

		markers.add(marker);
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
		return String.format("Node(%s)", markers);
	}
	
	public HashSet<Marker> getMarkers() {
		return markers;
	}
	
	public boolean containsMarker(Marker marker) {
		return markers.contains(marker);
	}
	
	public boolean isDangerous() {
		return markers.contains(Marker.PIT) || markers.contains(Marker.WUMPUS) || markers.contains(Marker.PITWARNING) || markers.contains(Marker.WUMPUSWARNING);
	}
	
	/* Prioritize WALL;
	 * if this.marker == WALL, don't override it.
	 *   - useful when MyAI marks neighbors of current node as hazardous
	 *     upon perceiving a breeze/stench
	 * Otherwise, set this.marker to the argument marker.
	 */
	public void setMarkers(Marker marker) {
		markers.add(marker);
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
