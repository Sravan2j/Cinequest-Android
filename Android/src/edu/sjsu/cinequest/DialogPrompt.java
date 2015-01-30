package edu.sjsu.cinequest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The class with static methods to show various classes of dialogs  
 * @author Prabhjeet Ghuman
 *
 */
public class DialogPrompt {
	
	/**
	 * Shows a general purpose dialog
	 * @param context the context which is requesting the prompt
	 * @param message the message to display
	 */
	public static void showDialog(Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		       .setCancelable(true)
		       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                return;    		                
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Shows a toast notification with long duration
	 * @param context the context which is requesting the prompt
	 * @param msg the message to display
	 */
	// TODO: Either use consistently or eliminate
	public static void showToast(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Shows a confirmation dialog with YES/NO options
	 * @param context the context which is requesting the prompt
	 * @param message the message to display
	 * @param pButton the text of positive button
	 * @param pListener the OnClickListener for positive button
	 * @param nButton the text of negative button
	 * @param nListener the OnClickListener for negative button
	 */
	public static boolean showOptionDialog(Context context, String message, 
									String pButton, DialogInterface.OnClickListener pListener,
									String nButton, DialogInterface.OnClickListener nListener){
		final Boolean result = false;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		       .setCancelable(true)
		       .setPositiveButton(pButton, pListener)
		       .setNegativeButton(nButton, nListener);
		AlertDialog alert = builder.create();
		alert.show();
		
		return result;
	}
	
	/**
	 * Shows a confirmation dialog with YES/NO options
	 * @param context the context which is requesting the prompt
	 * @param message the message to display
	 * @param firstButton the text of first button
	 * @param firstListener the OnClickListener for first button
	 * @param secondButton the text of second button
	 * @param secondListener the OnClickListener for second button
	 * @param thirdButton the text of third button
	 * @param thirdListener the OnClickListener for third button
	 */
	public static boolean showOptionDialog(Context context, String message, 
			String firstButton, DialogInterface.OnClickListener firstListener,
			String secondButton, DialogInterface.OnClickListener secondListener,
			String thirdButton, DialogInterface.OnClickListener thirdListener){
		
		final Boolean result = false;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		       .setCancelable(true)
		       .setPositiveButton(firstButton, firstListener)
		       .setNegativeButton(secondButton, secondListener)
		       .setNeutralButton(thirdButton, thirdListener);
		AlertDialog alert = builder.create();
		alert.show();
		
		return result;    		
	}
	
	/**
	 * Shows the About dialog with information and credits about the app
	 */
	public static void showAppAboutDialog(Context ctx){
		//get display width
		 Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		 int width = display.getWidth();
		
		//set up dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.about_dialog);
        dialog.setTitle("About Cinequest");
        dialog.setCancelable(true);
        dialog.setCancelable(true);
        
        //get dialog parameters, set custom width and reset the parameters
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width= width * 90/100;
        dialog.getWindow().setAttributes(params);

        //set up image view
        ImageView logo = (ImageView) dialog.findViewById(R.id.sjsu_logo);
        logo.setImageResource(R.drawable.sjsulogo_new_sjsu4);

        TextView version = (TextView) dialog.findViewById(R.id.about_dialog_version);
        version.setText("App version " + ctx.getString(R.string.versionName));
        
        //set up scrollview
        ScrollView scroller = (ScrollView) dialog.findViewById(R.id.about_dialog_scrollview);
        scroller.getLayoutParams().height = 200;
        
        //set up button
        Button button = (Button) dialog.findViewById(R.id.about_dialog_okbutton);
        button.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //now that the dialog is set up, show it    
        dialog.show();
	}

}
