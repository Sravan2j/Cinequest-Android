package edu.sjsu.cinequest.comm;

/**
 * An action is a unit of work that eventually yields a result or failure.
 * The reason for having actions is to be able to combine them to composite actions.
 */
public interface Action {
	/**
	 * Starts the action
	 * @param in the input to the action
	 * @param cb the callback for monitoring progress and yielding the result or failure
	 */
	public void start(Object in, Callback cb);
}
