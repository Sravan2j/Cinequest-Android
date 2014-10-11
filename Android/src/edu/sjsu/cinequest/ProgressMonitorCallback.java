package edu.sjsu.cinequest;

import android.app.ProgressDialog;
import android.content.Context;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.CallbackException;
import edu.sjsu.cinequest.comm.Platform;

/**
 * Callback implementation for the Cinequest App
 */

public class ProgressMonitorCallback implements Callback {
	private ProgressDialog dialog;
	private Context context;
	private String message;

        /**
         * Calls second constructor method, which sets private variables
         * to context and "Fetching data"
         * @param context 
         */
	public ProgressMonitorCallback(Context context) {
		this(context, "Fetching Data");
	}
        
        /**
         * Main constructor. Sets private variables context and message 
         * with input parameters
         * @param context variable to be used in class methods
         * @param message message to be displayed in class methods
         */
	public ProgressMonitorCallback(Context context, String message) {
		this.context = context;
		this.message = message;
	}
        
        /**
         * Displays information dialog. Called on start up
         */
	@Override
	public void starting() {
		if (dialog == null)
			dialog = ProgressDialog.show(context, "Cinequest", message);
	}

        /**
         * Dismisses dialog (if not null)
         * @param result unused input
         */
	@Override
	public void invoke(Object result) {
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}

        /**
         * Called if failure encountered.
         * Throwable t is converted to message and displayed using a dialog 
         * prompt
         * @param t the thrown error/exception to be shown to the user
         */
	@Override
	public void failure(Throwable t) {
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
		// TODO: For some classes of Throwable, just pop the dialog?
		// E.g. user canceling login dialog

		if (t instanceof CallbackException) {
			int level = ((CallbackException) t).getLevel();
			if (level == CallbackException.ERROR)
				DialogPrompt.showDialog(context, t.getMessage());
			else if (level == CallbackException.WARNING)
				DialogPrompt.showToast(context, t.getMessage());
		} else {
			String message = "Application Error";
			if (t.getMessage() != null && !t.getMessage().equals("null"))
				message += ": " + t.getMessage();
			DialogPrompt.showDialog(context, message);
			Platform.getInstance().log(t);
		}
	}
}
