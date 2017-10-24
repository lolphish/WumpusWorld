
public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point() {
		this(0, 0);
	}
	
	@Override
	public String toString() {
		return "Point(" + getX() + ", " + getY() + ")";
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
}
