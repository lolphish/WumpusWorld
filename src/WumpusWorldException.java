
public class WumpusWorldException extends RuntimeException {
	
	public WumpusWorldException(String message) {
		super(message);
	}
	
	public WumpusWorldException() {
		this("");
	}
}
