
public class Point {
	
	public static Point getStartingPoint() {
		return new Point(1, 1);
	}
	
	private int x;
	private int y;
	private static final int minX = 1;
	private static int maxX = 7;
	private static final int minY = 1;
	private static int maxY = 7;
	
	public Point(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public Point(Point other) {
		setX(other.getX());
		setY(other.getY());
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

	public static int getMaxX() {
		return maxX;
	}

	public static void setMaxX(int maxX) {
		Point.maxX = maxX;
	}

	public static int getMaxY() {
		return maxY;
	}

	public static void setMaxY(int maxY) {
		Point.maxY = maxY;
	}

	public static int getMinX() {
		return minX;
	}

	public static int getMinY() {
		return minY;
	}
}
