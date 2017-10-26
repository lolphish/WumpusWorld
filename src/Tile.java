
/* TODO: comment on class description
 */
public class Tile {
	public static final double SAFE = 1;
	public static final double HAZARD = 0;
	
	private double probability;
	private boolean stench;
	private boolean breeze;
	private boolean glitter;
	private boolean bump;
	
	
	public Tile(double probability) {
		this.probability = probability;
		stench = false;
		breeze = false;
		glitter = false;
		bump = false;
	}
	
	public Tile() {
		this(SAFE);
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public boolean isStench() {
		return stench;
	}

	public void setStench(boolean stench) {
		this.stench = stench;
	}

	public boolean isBreeze() {
		return breeze;
	}

	public void setBreeze(boolean breeze) {
		this.breeze = breeze;
	}

	public boolean isGlitter() {
		return glitter;
	}

	public void setGlitter(boolean glitter) {
		this.glitter = glitter;
	}

	public boolean isBump() {
		return bump;
	}

	public void setBump(boolean bump) {
		this.bump = bump;
	}
}
