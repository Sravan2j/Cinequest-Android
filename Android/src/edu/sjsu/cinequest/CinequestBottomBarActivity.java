package edu.sjsu.cinequest;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

public abstract class CinequestBottomBarActivity extends CinequestTabActivity {
	protected CheckBoxMap mCheckBoxMap;
	private View mBottomActionBar;
	private Button actionBarButton_01, actionBarButton_02, actionBarButton_03;
	protected boolean IGNORE_NEXT_OnCheckChanged = false;
	private boolean BOTTOM_BAR_ENABLED = false;
	protected enum ButtonType {LELT, MIDDLE, RIGHT}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckBoxMap = new CheckBoxMap(this, mCheckboxClickListener);
        
        mBottomActionBar = (View) findViewById(R.id.bottom_action_bar);
        actionBarButton_01 = (Button) findViewById(R.id.bottomActionBar_button_01);
        actionBarButton_02 = (Button) findViewById(R.id.bottomActionBar_button_02);
        actionBarButton_03 = (Button) findViewById(R.id.bottomActionBar_button_03);
        
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		int width = display.getWidth();
        int buttonwidth = (width-10)/3;
        actionBarButton_01.setWidth(buttonwidth);
        actionBarButton_02.setWidth(buttonwidth);
        actionBarButton_03.setWidth(buttonwidth);
	}
	
    /**
     * Add the button to the bottom bar. Bottom-bar can have max of three buttons
     * They can be referred as Left, Middle and Right button. Any button not added
     * will stay invisible
     * 
     * @param bt ButtonType
     * @param text Text to display on the button
     * @param cl clicklistener for the button click
     */
	protected void addBottomBarButton(ButtonType bt, String text, 
										View.OnClickListener cl){
		if(bt == ButtonType.LELT){
			actionBarButton_01.setVisibility(View.VISIBLE);
			actionBarButton_01.setText(text);
			actionBarButton_01.setOnClickListener(cl);
		} else if(bt == ButtonType.MIDDLE){
			actionBarButton_02.setVisibility(View.VISIBLE);
			actionBarButton_02.setText(text);
			actionBarButton_02.setOnClickListener(cl);
		} else if(bt == ButtonType.RIGHT){
			actionBarButton_03.setVisibility(View.VISIBLE);
			actionBarButton_03.setText(text);
			actionBarButton_03.setOnClickListener(cl);
		}
	}
	
	/**
    * Toggle the visiblity of any of the buttons of bottom bar.
    * Buttons can be referred as ButtonType.Left, Buttontype.Middle 
    * and ButtonType.Right 
    * 
    * @param bt ButtonType
    * @param visibility Either View.Visible, View.Invisible. View.Gone not supported
    * as it will interfere with the layout of the buttons
    */
	protected void setBottomBarButtonVisibility(ButtonType bt, int visibility){
		if (visibility == View.GONE)
			return;
		
		if(bt == ButtonType.LELT){
			actionBarButton_01.setVisibility(visibility);			
		} else if(bt == ButtonType.MIDDLE){
			actionBarButton_02.setVisibility(visibility);			
		} else if(bt == ButtonType.RIGHT){
			actionBarButton_03.setVisibility(visibility);			
		}
	}
	
	/**
	 * Set the bottom bar enabled or disabled.
	 * @param enabled
	 */
	protected void setBottomBarEnabled(boolean enabled){
		BOTTOM_BAR_ENABLED = enabled;
	}
	
	/**
	 * Get back either ButtonType.Left, Middle or Right button from bottom bar.
	 * @param bt ButtonType of which button to get back
	 * @return the button
	 */
	protected Button getBottomBarButton(ButtonType bt){
		if(bt == ButtonType.LELT){
			return actionBarButton_01;			
		} else if(bt == ButtonType.MIDDLE){
			return actionBarButton_02;			
		} else if(bt == ButtonType.RIGHT){
			return actionBarButton_03;			
		} else
			return null;
	}
	
	/**
     * Slide in the bottom bar with animation
     */
    protected void showBottomBar(){
    	if(!BOTTOM_BAR_ENABLED)
    		return;
    	if(mBottomActionBar.getVisibility() == View.VISIBLE){
    		return;
    	}
    	
    	Animation anim = AnimationUtils.loadAnimation(this, R.anim.bottom_up_slidein);
    	mBottomActionBar.setAnimation(anim);
    	
    	//Make the bottom bar visible    	
    	mBottomActionBar.setVisibility(View.VISIBLE);
    }
    
    /**
     * Slide out the bottom bar with animation
     */
    protected void hideBottomBar(){    	
    	if(!BOTTOM_BAR_ENABLED)
    		return;
    	
    	if(mBottomActionBar.getVisibility() == View.GONE){
    		return;
    	}
    	
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.up_down_slideout);
		mBottomActionBar.setAnimation(anim);
		
		//hide away the bottom bar
		mBottomActionBar.setVisibility(View.GONE);
    }
    
	protected void configureCheckBox(View v, CheckBox checkbox, Schedule s) {
		checkbox.setVisibility(View.VISIBLE);
		checkbox.setOnCheckedChangeListener(mCheckboxClickListener);

		if( mCheckBoxMap.containsKey( s.getId() ) ){
			IGNORE_NEXT_OnCheckChanged = true;
			checkbox.setChecked(true);
		}	//and uncheck the checkboxes if they were not checked  
		else if( !mCheckBoxMap.containsKey( s.getId() ) 
				&& checkbox.isChecked()	){
			IGNORE_NEXT_OnCheckChanged = true;
			checkbox.setChecked(false);        			
		}
	}
    	
	/**
     * Checkbox click listener for list checkboxes
     */
    private CompoundButton.OnCheckedChangeListener mCheckboxClickListener 
    							= new CompoundButton.OnCheckedChangeListener(){    	
    	
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			    			
			Schedule schedule = (Schedule) ((CheckBox)buttonView).getTag();
			String filmID = "" + schedule.getId();
			
			if(IGNORE_NEXT_OnCheckChanged){
				IGNORE_NEXT_OnCheckChanged = false;
				return;
			}			
						
			//if the checkbox is checked
			if(isChecked==true){
				
				//if the key is already contained in list of checked-checkboxes, return
				if(mCheckBoxMap.containsKey(Integer.parseInt( filmID )))
						return;
				
				//add this checkbox to the list of checked boxes
				mCheckBoxMap.put( Integer.parseInt( filmID ), (CheckBox)buttonView );
					
				//Show the BottomActionBar
				showBottomBar();
				
			} else {		//if checkbox was later unchecked
				
				//remove current checkbox from the list of checked-checkboxes
				mCheckBoxMap.remove( Integer.parseInt( filmID) );
				
				//if all the checkboxes have been unchecked, hide the bottom bar
				if(mCheckBoxMap.size() == 0)
					hideBottomBar();
			}
		}
    };
	
	
}
