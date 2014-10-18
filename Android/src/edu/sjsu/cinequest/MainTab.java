package edu.sjsu.cinequest;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
/*
 * Main tab is a tab that displays contents of News,Films, Events, Forums, Schedule
 * to the consumer. For easy navigation and information lookup.
 * This tab is th head tab from which consumer can navigate all necessary film related
 * information.
 * @author Dmitri Dimov
 * */
public class MainTab extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {    	    	
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Get host object from super class
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, HomeActivity.class);
		spec = tabHost.newTabSpec("news").setIndicator("News",getResources().getDrawable(R.drawable.news)).setContent(intent);
		tabHost.addTab(spec);

		// Create the intent associated with the activity
		intent = new Intent().setClass(this, FilmsActivity.class);
		intent.putExtra("tab", "films");
		// Create a new TabSpec with a name, an icon and intent
		spec = tabHost.newTabSpec("films").setIndicator("Films",getResources().getDrawable(R.drawable.film_icon)).setContent(intent);
		// Add it to the tab
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FilmsActivity.class);
		intent.putExtra("tab", "events");
		spec = tabHost.newTabSpec("events").setIndicator("Events",getResources().getDrawable(R.drawable.events_icon)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FilmsActivity.class);
		intent.putExtra("tab", "forums");
		spec = tabHost.newTabSpec("forums").setIndicator("Forums",getResources().getDrawable(R.drawable.forums_icon)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ScheduleActivity.class);        
		spec = tabHost.newTabSpec("schedule").setIndicator("Schedule",getResources().getDrawable(R.drawable.schedule_icon)).setContent(intent);
		tabHost.addTab(spec);

		// Default tab is the first tab.
		int tab = getIntent().getIntExtra("open_tab", 0);
		tabHost.setCurrentTab(tab);                
	}
}
