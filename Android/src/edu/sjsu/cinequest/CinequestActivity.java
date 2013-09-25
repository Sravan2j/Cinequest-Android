package edu.sjsu.cinequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Filmlet;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/*
 * This superclass has convenience methods for making lists of schedules and
 * filmlets.
 */
public class CinequestActivity extends Activity
{
	private static final int HOME_MENUOPTION_ID = Menu.FIRST + 11;
	private static final int ABOUT_MENUOPTION_ID = Menu.FIRST + 12;
	
	/**
	 * Launches the FilmDetail activity for the given object.
	 * @param result Object; Can be Schedule, Filmlet etc
	 */
	protected void launchFilmDetail(Object result) {
		Intent intent = new Intent();
		intent.setClass(this, FilmDetail.class);
		intent.putExtra("target", (Serializable) result);
		startActivity(intent);		
	}
	
	/**
	 * Creates a list of schedules
	 * @param listItems the list items
	 * @param isChecked a function to determine when to check a checkbox, or null for no checkboxes
	 * @param listener the listener for checkboxes, or null for no checkboxes
	 * @return the list adapter
	 */
	
	protected ListAdapter createScheduleList(List<Schedule> listItems) {
		if (listItems.size() == 0) {
     		return new SeparatedListAdapter(this);
     	}
		SeparatedListIndexedAdapter adapter  = new SeparatedListIndexedAdapter(this);
  		 
	   	 
    	TreeMap<String, ArrayList<Schedule>> filmsMap 
    						= new TreeMap<String, ArrayList<Schedule>>();
 		
 		for(Schedule s : listItems) {
 			String day = s.getStartTime().substring(0, 10);
 			
 			if (!filmsMap.containsKey(day))
 				filmsMap.put(day, new ArrayList<Schedule>());
			filmsMap.get(day).add(s);
 		}
 			
		DateUtils du = new DateUtils();
 		for (String day : filmsMap.keySet()) { 
 			ArrayList<Schedule> filmsForDay = filmsMap.get(day); 			
 			String header = du.format(day, DateUtils.DATE_DEFAULT); 			
 			String key = du.format(day, DateUtils.DAY_ONLY); 			
			adapter.addSection(header, key, new ScheduleListAdapter(this, filmsForDay));
 		}
 	    return adapter;
   	 }
     	
	protected ListAdapter createFilmletList(List<? extends Filmlet> listItems) {
		if (listItems.size() == 0) {
     		return new SeparatedListAdapter(this);
     	} 

		SeparatedListIndexedAdapter adapter = new SeparatedListIndexedAdapter(this);
   		TreeMap<String, ArrayList<Filmlet>> filmsTitleMap 
   							= new TreeMap<String, ArrayList<Filmlet>>();
   		String titleInitial = "";
   		for (Filmlet f : listItems) {
 			titleInitial = getTitleInitial(f.getTitle(), titleInitial);
 			
 			if (!filmsTitleMap.containsKey(titleInitial))
 				filmsTitleMap.put(titleInitial, new ArrayList<Filmlet>());
			filmsTitleMap.get(titleInitial).add(f);
 		}
   		
 		for (String titleInit : filmsTitleMap.keySet()) { 
 			adapter.addSection(
				titleInit, titleInit,	
				new FilmletListAdapter(this, filmsTitleMap.get(titleInit)));
 		}
 		return adapter;
    }
	
	private static String getTitleInitial(String title, String previousInitial) {
		String ucTitle = title.toUpperCase();
		if (ucTitle.startsWith("A ") || ucTitle.startsWith("AN ") || ucTitle.startsWith("THE "))
			ucTitle = ucTitle.substring(ucTitle.indexOf(' ') + 1);
		String initial = ucTitle.substring(0,1);
		if (initial.compareTo(previousInitial) < 0) Platform.getInstance().log("Didn't expect " + title + " after section " + previousInitial);
		return initial;
	}
	
	/**
	 * An adapter for a list of schedule items. These lists occur (1) in the Films tab 
	 * (when sorted by date), Events and Forums tabs, (2) in each film detail,
	 * and (3) in the Schedule tab.
	 */
	protected static class ScheduleListAdapter extends ArrayAdapter<Schedule> {
		private static final int RESOURCE_ID = R.layout.listitem_titletimevenue;
		private DateUtils du = new DateUtils();
		
		public ScheduleListAdapter(Context context, List<Schedule> list) 
		{
		    super(context, RESOURCE_ID, list);
		}
		
		@Override
	    public View getView(int position, View v, ViewGroup parent) {            
	        if (v == null) {
	        	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	v = inflater.inflate(RESOURCE_ID, null);
	        }
	        
        	TextView title = (TextView) v.findViewById(R.id.titletext);
        	TextView time = (TextView) v.findViewById(R.id.timetext);
            TextView venue = (TextView) v.findViewById(R.id.venuetext);
            CheckBox checkbox = (CheckBox) v.findViewById(R.id.myschedule_checkbox);	                
	        Schedule result = getItem(position);            
	        title.setText(result.getTitle());	                 
			if (result.isSpecialItem())
               	title.setTypeface(null, Typeface.ITALIC);
        	String startTime = du.format(result.getStartTime(), DateUtils.TIME_SHORT);
        	String endTime = du.format(result.getEndTime(), DateUtils.TIME_SHORT);
	        time.setText("Time: " + startTime + " - " + endTime);
	        venue.setText("Venue: " + result.getVenue());
	        formatContents(v, title, time, venue, du, result);		        
    	    checkbox.setTag(result);	        
    	    configureCheckBox(v, checkbox, result);
	        return v;	        
		}
		
	    /**
		 * Override to change the formatting of the contents
	     */
	    protected void formatContents(View v, TextView title, TextView time, TextView venue, DateUtils du, Schedule result) {
	    }
	    
	    /**
		 * This contains the configuration of the checkbox. By default,
		 * the checkbox adds or removes the schedule item in the user's schedule.
		 * Override if you want a different behavior.
	     */
		protected void configureCheckBox(View v, CheckBox checkbox, final Schedule result) {
			checkbox.setVisibility(View.VISIBLE);
			checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Schedule s = (Schedule) buttonView.getTag();
					if (isChecked){
						HomeActivity.getUser().getSchedule().add(s);
					} else{
						HomeActivity.getUser().getSchedule().remove(s);
					}					
				}				
			});
			
			checkbox.setChecked(HomeActivity.getUser().getSchedule().contains(result));
		}
	}	

	/**
	 * An adapter for a list of films. These lists occur (1) in the Films tab 
	 * (when sorted by name) (2) in the DVDs tab and (3) in the detail view of a
	 * program item with multiple films.
	 */
	protected static class FilmletListAdapter extends ArrayAdapter<Filmlet> {
		private static final int RESOURCE_ID = R.layout.listitem_title_only;
		
		public FilmletListAdapter(Context context, List<? extends Filmlet> list) 
		{
		    super(context, RESOURCE_ID, (List<Filmlet>) list);
		}
		
		@Override
	    public View getView(int position, View v, ViewGroup parent) {            
	        if (v == null) {
	        	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	v = inflater.inflate(RESOURCE_ID, null);
	        }
	        
	        Filmlet result = getItem(position);            
        	TextView title = (TextView) v.findViewById(R.id.listitem_titletext);
	        title.setText(result.getTitle());	                 
	        formatContents(v, title, result);		        
	        return v;	        
		}
		
	    /**
		 * Override to change the formatting of the contents
	     */
	    protected void formatContents(View v, TextView title, Filmlet result) {
	    }
	}	

    /**
     * Take the user to home activity
     */
    private void goHome(){

    	Intent i = new Intent();
		setResult(RESULT_OK, i);
        finish();
    }
        
    /**
     * Create a menu to be displayed when user hits Menu key on device
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        menu.add(0, HOME_MENUOPTION_ID, 0,"Home").setIcon(R.drawable.home);
        menu.add(0, ABOUT_MENUOPTION_ID, 0,"About").setIcon(R.drawable.about);
        
        return true;
    }
    
    /** Menu Item Click Listener*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	
	        case HOME_MENUOPTION_ID:
	        	goHome();
	            return true;
	        case ABOUT_MENUOPTION_ID:
	            DialogPrompt.showAppAboutDialog(this);
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
        }
        
    }
	
}
