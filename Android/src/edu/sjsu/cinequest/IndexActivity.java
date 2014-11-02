package edu.sjsu.cinequest;

import java.util.List;
import java.util.Vector;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;

public class IndexActivity extends CinequestActivity {
	
	private enum IndexType {FILMS, EVENTS}

	private static Vector<CommonItem> mFilms_byTitle;
	private static Vector<CommonItem> mEvents_byTitle;
	private Button filmsButton;
	private Button eventsButton;
	private ListView listview;
	private TextView mEmptyListViewMessage;
	private IndexType currentType;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.index_activity_layout);
        listview = (ListView) findViewById(R.id.index_activity_listview);
        mEmptyListViewMessage  = (TextView)this.findViewById(R.id.msg_for_empty_schedule);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Object result = listview.getItemAtPosition( position );
				launchFilmDetail(result);				
			}
		});
        
		filmsButton = (Button) findViewById(R.id.index_activity_films_button);
		eventsButton = (Button) findViewById(R.id.index_activity_events_button);
		
		filmsButton.setEnabled(false);
		currentType = IndexType.FILMS;
		
		filmsButton.setBackgroundResource(R.drawable.enabledfilmsbutton);
		filmsButton.setTextColor(Color.WHITE);
		eventsButton.setBackgroundResource(R.drawable.disabledeventsbutton);
		eventsButton.setTextColor(Color.RED);
		
		registerForContextMenu(listview);

        fetchServerData(currentType);
	}

	/**
	 * Gets called when user returns to this tab. Also gets called once after the 
	 * onCreate() method too.
	 */

	@Override
	public void onResume(){
		super.onResume();
		if(currentType == IndexType.FILMS){
			refreshListContents(mFilms_byTitle);    
		}else{
			refreshListContents(mEvents_byTitle);    
		}
		  		  		  	    		
	}

	private void fetchServerData(IndexType indexType) {
		if (indexType == IndexType.FILMS) {
			HomeActivity.getQueryManager().getAllFilms(
					new ProgressMonitorCallback(this) {
						public void invoke(Object result) {
							super.invoke(result);
							mFilms_byTitle = (Vector<CommonItem>) result;
							refreshListContents(mFilms_byTitle);
						}
					});
		} else {
			HomeActivity.getQueryManager().getAllEventsAndForums(
					new ProgressMonitorCallback(this) {
						public void invoke(Object result) {
							super.invoke(result);
							mEvents_byTitle = (Vector<CommonItem>) result;
							refreshListContents(mEvents_byTitle);
						}
					});
		}
	}

	private void refreshListContents(List<?> listItems) {
		if (listItems == null)
			return;
		SeparatedListIndexedAdapter adp = (SeparatedListIndexedAdapter) createFilmletList((List<CommonItem>) listItems);
		adp.setAsAdapterFor(listview);
		// if there are no items in the list, hide the listview,
		// and show the emptytextmsg, and vice versa
		if (adp.getCount() == 0) {
			listview.setVisibility(View.GONE);
			mEmptyListViewMessage.setVisibility(View.VISIBLE);
		} else {
			listview.setVisibility(View.VISIBLE);
			mEmptyListViewMessage.setVisibility(View.GONE);
		}
	}	
	
	public void filmsButtonOnClick(View view){
		currentType = IndexType.FILMS;
		filmsButton.setEnabled(false);
		eventsButton.setEnabled(true);
		
		filmsButton.setBackgroundResource(R.drawable.enabledfilmsbutton);
		filmsButton.setTextColor(Color.WHITE);
		eventsButton.setBackgroundResource(R.drawable.disabledeventsbutton);
		eventsButton.setTextColor(Color.RED);
		
		fetchServerData(currentType);
	}
	
	public void eventsButtonOnclick(View view){
		currentType = IndexType.EVENTS;
		filmsButton.setEnabled(true);
		eventsButton.setEnabled(false);
		
		eventsButton.setBackgroundResource(R.drawable.enabledeventsbutton);
		eventsButton.setTextColor(Color.WHITE);
		filmsButton.setBackgroundResource(R.drawable.disabledfilmsbutton);
		filmsButton.setTextColor(Color.RED);
		
		fetchServerData(currentType);
	}

}
