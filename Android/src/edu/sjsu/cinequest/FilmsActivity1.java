package edu.sjsu.cinequest;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import android.os.Bundle;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;

/**
 * Films tab of the app, showing the films for a given date.
 * @author Prabhjeet Ghuman
 * @author Chao
 */
public class FilmsActivity1 extends CinequestTabActivity {	
	private String target;
	private String tab;
	private static Vector<CommonItem> mFilms_byTitle;
	private static TreeMap<String, List<CommonItem>> mSchedule_byDate;
	private DateUtils du;
	//unique id's for menu options
	// private static final int SORT_MENUOPTION_ID = Menu.FIRST;
	//private static final int ADD_CONTEXTMENU_ID = Menu.FIRST + 1;

	private boolean isByDate() { return !target.equals(FilmsActivity.ALPHA); }

	public void onCreate(Bundle savedInstanceState) {    	
		target = getIntent().getExtras().getString("target");
		tab = getIntent().getExtras().getString("tab");		
		super.onCreate(savedInstanceState);
		du = new DateUtils();
	}

	/**
	 * Gets called when user returns to this tab. Also gets called once after the 
	 * onCreate() method too.
	 */

	@Override
	public void onResume(){
		super.onResume();

		//refresh the listview
		if(target.equals(FilmsActivity.ALPHA)) {
			refreshListContents(mFilms_byTitle);      		  		
		} else {
			refreshListContents(mSchedule_byDate);
		}    	    		
	}

	@Override
	protected void fetchServerData() {
		if(isByDate()) {
			if (tab.equalsIgnoreCase("films")){	
				//Get Filmes by date from server
				HomeActivity.getQueryManager().getFilmsByDate(target, new ProgressMonitorCallback(this) {
					@Override
					public void invoke(Object result) {
						super.invoke(result);
						mSchedule_byDate = (TreeMap<String, List<CommonItem>>) result;
						refreshListContents(mSchedule_byDate);
					}
				});
			}
			else if(tab.equalsIgnoreCase("events")){
				//Get Events by date from server
				HomeActivity.getQueryManager().getEventsByDate(target, new ProgressMonitorCallback(this) {
					@Override
					public void invoke(Object result) {
						super.invoke(result);
						mSchedule_byDate = (TreeMap<String, List<CommonItem>>) result;
						refreshListContents(mSchedule_byDate);
					}
				});
			}
			else if(tab.equalsIgnoreCase("forums"))
			{
				//Get Forums by date from server
				HomeActivity.getQueryManager().getForumsByDate(target, new ProgressMonitorCallback(this) {
					@Override
					public void invoke(Object result) {
						super.invoke(result);
						mSchedule_byDate = (TreeMap<String, List<CommonItem>>) result;
						refreshListContents(mSchedule_byDate);
					}
				});
			}
		}
		else
		{
			if (tab.equalsIgnoreCase("films")){	
				//Get all filmes from server
				HomeActivity.getQueryManager().getAllFilms (new ProgressMonitorCallback(this) {           		 
					public void invoke(Object result) {
						super.invoke(result);
						mFilms_byTitle = (Vector<CommonItem>) result;
						refreshListContents(mFilms_byTitle);
					}
				});      
			}
			else if(tab.equalsIgnoreCase("events"))
			{
				//Get all events from server
				HomeActivity.getQueryManager().getAllEvents (new ProgressMonitorCallback(this) {           		 
					public void invoke(Object result) {
						super.invoke(result);
						mFilms_byTitle = (Vector<CommonItem>) result;
						refreshListContents(mFilms_byTitle);
					}
				});
			}
			else if(tab.equalsIgnoreCase("forums"))
			{
				//Get all forums from server
				HomeActivity.getQueryManager().getAllForums (new ProgressMonitorCallback(this) {           		 
					public void invoke(Object result) {
						super.invoke(result);
						mFilms_byTitle = (Vector<CommonItem>) result;
						refreshListContents(mFilms_byTitle);
					}
				});
			}
		}     			
	}

	@Override
	protected void refreshListContents(List<?> listItems) {
		if (listItems == null) return;

		setListViewAdapter(createFilmletList((List<CommonItem>) listItems));

	}
	protected void refreshListContents(TreeMap<String, List<CommonItem>> listItems) {
		boolean is24HourFormat=android.text.format.DateFormat.is24HourFormat(this);		
		if (listItems == null) return;		  
		SeparatedListIndexedAdapter adapter = new SeparatedListIndexedAdapter(this);
		String formattedTime="";
		for (String titleInit : listItems.keySet()) { 
			if(!is24HourFormat)
			{
				formattedTime=du.formatTime(titleInit);				
			}
			else
			{
				formattedTime=titleInit;
			}
			adapter.addSection(
					formattedTime, titleInit,
					//titleInit, titleInit,	
					new FilmletListAdapter(this, listItems.get(titleInit)));
		}

		setListViewAdapter(adapter);
	}
}
