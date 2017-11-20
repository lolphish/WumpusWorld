import java.util.HashSet;
import java.util.Set;

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
	
	private Set<Marker> markers;
	private Node above;
	private Node below;
	private Node left;
	private Node right;
	
	public Node(Marker marker) {
		markers = new HashSet<>();
		markers.add(marker);
		above = null;
		below = null;
		left = null;
		right = null; 
	}
	
	public Node() {
		this(Marker.UNEXPLORED);
	}
	
	public Node(Node other) {
		markers = other.markers;
		setAbove(above);
		setBelow(below);
		setLeft(left);
		setRight(right);
	}
	
	@Override
	public String toString() {
		return String.format("Node(%s)", markers);
	}
	
	public Set<Marker> getMarkers() {
		return markers;
	}
	
	public boolean containsMarker(Marker marker) {
		return markers.contains(marker);
	}
	
	public boolean isDangerous() {
		return markers.contains(Marker.PIT) || markers.contains(Marker.WUMPUS) || markers.contains(Marker.PITWARNING) || markers.contains(Marker.WUMPUSWARNING);
	}
	
	/* If trying to mark the node as EXPLORED,
	 * automatically unmark it as UNEXPLORED.
	 */
	public void addMarker(Marker marker) {
		if (marker == Marker.EXPLORED && containsMarker(Marker.UNEXPLORED)) {
			removeMarker(Marker.UNEXPLORED);
		}
		markers.add(marker);
	}
	
	public void removeMarker(Marker marker) {
		markers.remove(marker);
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
