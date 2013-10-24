package edu.sjsu.cinequest.comm;

// TODO: How do we know that start isn't called more than once?
// TODO: How do we know that invoke/failure isn't called more than once?

/**
 * Utility methods for combining actions
 */
public class Actions {
	/**
	 * Combines two actions
	 * 
	 * @param first
	 *            an action
	 * @param second
	 *            another action
	 * @return an action that carries out first, and, if it is successful,
	 *         second. The result of first becomes the input of second. If
	 *         either action fails, the combined action fails.
	 */
	
	public static Action andThen(final Action first, final Action second) {
		return new Action() {
			public void start(Object in, final Callback cb) {
				first.start(in, new Callback() {
                    public void starting() 
                    {
        				cb.starting();
                    }

					public void failure(Throwable t) {
						cb.failure(t);
					}

					public void invoke(Object result) {
						second.start(result, cb);						
					}});
			}
		};
	}
	
	public static abstract class Step implements Action {
		public abstract Object invoke(Object in);

		public void start(Object in, Callback cb) {
			try {
				cb.invoke(invoke(in));
			} catch (Throwable t) {
				cb.failure(t);
			}
		}
	}
	
	/**
	 * Produces an action with a secondary callback (e.g. for progress reporting)
	 * @param action an action
	 * @param cb2 a secondary callback
	 * @return the decorated action
	 */
	public static Action withCallback(final Action action, final Callback cb2) {
		return new Action() {
			public void start(Object in, final Callback cb) {
				action.start(in, new Callback() {
					public void starting() {
						cb2.starting();
						cb.starting();
					}
					public void invoke(Object result) {
						cb2.invoke(result);
						cb.invoke(result);
					}
					public void failure(Throwable t) {
						cb2.failure(t);
						cb.failure(t);
					}
				});				
			}
		};
	}

	public static Action ifThenElse(final Action first, final Action second, final Action third) {
		return new Action() {
			public void start(final Object in, final Callback cb) {
				first.start(in, new Callback() {
                    public void starting() 
                    {
                    	cb.starting();
                    }
					
					public void failure(Throwable t) {
						cb.failure(t);
					}

					public void invoke(Object result) {
						if (result == Boolean.TRUE) second.start(in, cb);
						else third.start(in, cb);
					}
				});
			}
		};
	}

	public static Action ifThen(final Action first, final Action second) {
		return ifThenElse(first, second, nothing());
	}

	public static Action nothing() {
		return new Action() {
			public void start(Object in, Callback cb) {
				cb.invoke(in);
			}
		};
	}
}
