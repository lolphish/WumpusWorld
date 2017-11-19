
public class Point {
	
	public static Point getStartingPoint() {
		return new Point(1, 1);
	}
	
	private int x;
	private int y;
	private final int minX;
	private int maxX;
	private final int minY;
	private int maxY;
	
	public Point(int x, int y) {
		setX(x);
		setY(y);
		this.minX = 1;
		this.minY = 1;
		setMaxX(Integer.MAX_VALUE);
		setMaxY(Integer.MAX_VALUE);
	}
	
	public Point(Point other) {
		setX(other.getX());
		setY(other.getY());
		setMaxX(other.getMaxX());
		setMaxY(other.getMaxY());
		minX = other.minX;
		minY = other.minY;
	}
	
	@Override
	public String toString() {
		return "Point(" + getX() + ", " + getY() + ")";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Point)) {
			return false;
		}
		Point otherPoint = (Point)other;
		return getX() == otherPoint.getX() && getY() == otherPoint.getY();
	}
	
	@Override
	public int hashCode() {
		String asString = String.format("%d%d", getX(), getY());
		return asString.hashCode();
	}
	
	public boolean outOfBounds() {
		return getX() < getMinX() || getX() > getMaxX() || getY() < getMinY() || getY() > getMaxY();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void addX(int steps) {
		x += steps;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void addY(int steps) {
		y += steps;
	}
	
	public void set(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public boolean atStart() {
		return getX() == 1 && getY() == 1;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}
}
