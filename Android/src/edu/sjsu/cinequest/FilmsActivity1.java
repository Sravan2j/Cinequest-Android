package edu.sjsu.cinequest;

import java.util.List;
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
	private static Vector<CommonItem> mSchedule_byDate;

	//unique id's for menu options
	// private static final int SORT_MENUOPTION_ID = Menu.FIRST;
	//private static final int ADD_CONTEXTMENU_ID = Menu.FIRST + 1;

	private boolean isByDate() { return !target.equals(FilmsActivity.ALPHA); }

	public void onCreate(Bundle savedInstanceState) {    	
		target = getIntent().getExtras().getString("target");
		tab = getIntent().getExtras().getString("tab");		
		super.onCreate(savedInstanceState);
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
				HomeActivity.getQueryManager().getFilmsByDate(target, new ProgressMonitorCallback(this) {
					@Override
					public void invoke(Object result) {
						super.invoke(result);
						mSchedule_byDate = (Vector<CommonItem>) result;
						refreshListContents(mSchedule_byDate);
					}
				});
			}
			else if(tab.equalsIgnoreCase("events")){
				HomeActivity.getQueryManager().getEventsByDate(target, new ProgressMonitorCallback(this) {
					@Override
					public void invoke(Object result) {
						super.invoke(result);
						mSchedule_byDate = (Vector<CommonItem>) result;
						refreshListContents(mSchedule_byDate);
					}
				});
			}
			else if(tab.equalsIgnoreCase("forums"))
			{
				HomeActivity.getQueryManager().getForumsByDate(target, new ProgressMonitorCallback(this) {
					@Override
					public void invoke(Object result) {
						super.invoke(result);
						mSchedule_byDate = (Vector<CommonItem>) result;
						refreshListContents(mSchedule_byDate);
					}
				});
			}
		}
		else
		{
			if (tab.equalsIgnoreCase("films")){				
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
		if(isByDate()) {
			//Here the listview should be sorted by time instead of arranging them alphabetically  
			setListViewAdapter(createFilmletList((List<CommonItem>) listItems));
		}
		else {
			setListViewAdapter(createFilmletList((List<CommonItem>) listItems));
		}
	}

}
