package edu.sjsu.cinequest.comm;

public class CallbackException extends RuntimeException {
    private int level;
    public static final int IGNORE = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    public CallbackException(String message, int level) {
    	super(message);
    	this.level = level;
    }
   
	public int getLevel() {
		return level;
	}
}
