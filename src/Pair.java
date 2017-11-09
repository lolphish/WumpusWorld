
public class Pair<First, Second> {
	private First first;
	private Second second;
	
	public Pair(First first, Second second) {
		setFirst(first);
		setSecond(second);
	}
	
	@Override
	public String toString() {
		return "Pair(" + first.toString() + ", " + second.toString() + ")";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair<?, ?> rhs = (Pair<?, ?>)other;
			return getFirst() == rhs.getFirst() && getSecond() == rhs.getSecond();
		}
		return false;
	}
	
	public First getFirst() {
		return first;
	}
	
	public Second getSecond() {
		return second;
	}
	
	public void setFirst(First first) {
		this.first = first;
	}
	
	public void setSecond(Second second) {
		this.second = second;
	}
}
