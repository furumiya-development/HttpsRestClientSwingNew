package main.java.desk;

public class OriginalBusinessException extends Exception {
	private static final long serialVersionUID = 1L;

	public OriginalBusinessException(String message) {
		super(message);
	}
	
	public OriginalBusinessException(Throwable cause) {
		super(cause);
	}
	
	public OriginalBusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}