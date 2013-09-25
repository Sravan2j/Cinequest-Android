package edu.sjsu.cinequest;

import android.app.ProgressDialog;
import android.content.Context;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.CallbackException;
import edu.sjsu.cinequest.comm.Platform;

public class ProgressMonitorCallback implements Callback {
	private ProgressDialog dialog;
	private Context context;
	private String message;

	public ProgressMonitorCallback(Context context) {
		this(context, "Fetching Data");
	}

	public ProgressMonitorCallback(Context context, String message) {
		this.context = context;
		this.message = message;
	}

	@Override
	public void starting() {
		if (dialog == null)
			dialog = ProgressDialog.show(context, "Cinequest", message);
	}

	@Override
	public void invoke(Object result) {
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}

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
