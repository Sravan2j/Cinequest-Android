package edu.sjsu.cinequest;

import android.graphics.Color;
import android.app.TabActivity;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
/**
 * Main tab is a tab that displays contents of News,Films, Events, Forums, Schedule
 * to the consumer. For easy navigation and information lookup.
 * This tab is th head tab from which consumer can navigate all necessary film related
 * information.
 */
public class MainTab extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Get host object from super class
		final TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, HomeActivity.class);
		spec = tabHost.newTabSpec("news").setIndicator("News",getResources().getDrawable(R.drawable.news)).setContent(intent);
		tabHost.addTab(spec);

		// Create the intent associated with the activity
		intent = new Intent().setClass(this, IndexActivity.class);
		intent.putExtra("tab", "index");
		// Create a new TabSpec with a name, an icon and intent
		spec = tabHost.newTabSpec("index").setIndicator("Index",getResources().getDrawable(R.drawable.film_icon)).setContent(intent);
		// Add it to the tab
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FilmsActivity.class);
		intent.putExtra("tab", "events");
		spec = tabHost.newTabSpec("events").setIndicator("Events",getResources().getDrawable(R.drawable.events_icon)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ScheduleActivity.class);        
		spec = tabHost.newTabSpec("My Cinequest").setIndicator("My Cinequest",getResources().getDrawable(R.drawable.schedule_icon)).setContent(intent);
		tabHost.addTab(spec);

		// Default tab is the first tab.
		int tab = getIntent().getIntExtra("open_tab", 0);
		tabHost.setCurrentTab(tab);
	
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String arg0) {
				// TODO Auto-generated method stub
				setTabColor(tabHost); 
			}
			
		});
		setTabColor(tabHost);
    }
    
    //Set tab color
    public void setTabColor(TabHost tabHost){
    	for(int i=0; i < tabHost.getTabWidget().getChildCount(); i++)
        {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.BLACK); //inactive tabs
            
        }
    	tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.RED); //selected, active tabs
    	
    	int k = tabHost.getTabWidget().getChildCount() - 1;
    	TextView tv = (TextView) tabHost.getTabWidget().getChildAt(k).findViewById(android.R.id.title);
        tv.setTextSize(12);
    }
    
}
