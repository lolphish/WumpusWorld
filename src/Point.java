
public class Point {
	private int x;
	private int y;
	
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
}
