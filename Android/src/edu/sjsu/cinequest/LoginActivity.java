package edu.sjsu.cinequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The screen for getting login credentials from user
 * @author Prabhjeet Ghuman
 *
 */
public class LoginActivity extends Activity {
	
	// TODO: Move to resources
	//Registration URL
	private static String REGISTRAION_URL = "http://www.cinequest.org/isch_reg.php";

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Rename
        setContentView(R.layout.login_layout_portrait);

        Button syncScheduleButton = (Button) this.findViewById(R.id.sync_button);
        Button cancelButton = (Button) this.findViewById(R.id.sync_cancel_button);
        Button accountSignupButton = (Button) this.findViewById(R.id.accountsignup_button);
        
        final EditText emailBox = (EditText) this.findViewById(R.id.login_email_box);
        final EditText passwordBox = (EditText) this.findViewById(R.id.login_password_box);
        
        emailBox.setText(getIntent().getStringExtra("email"));
        passwordBox.setText(getIntent().getStringExtra("password"));
        
        syncScheduleButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra("email", emailBox.getText().toString());
				i.putExtra("password", passwordBox.getText().toString());
				setResult(RESULT_OK, i);
	            finish(); // finish the activity and return to sync
			}        	
        });        
        
        cancelButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				setResult(RESULT_CANCELED, i);
	            finish(); // finish the activity and return to sync
			}        	
        });        
        
        
        //Launch the Registration page 
        accountSignupButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				setResult(RESULT_CANCELED, i);
	            finish(); // finish this activity 
		    	i = new Intent(LoginActivity.this, RegistrationActivity.class);
		    	i.putExtra("url", REGISTRAION_URL);
				startActivity(i);
			}        	
        });
    }
 }
