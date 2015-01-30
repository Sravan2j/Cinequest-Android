package edu.sjsu.cinequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
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
	private SeparatedListIndexedAdapter adp;
	private ClearableEditText searchText;
	private boolean searchable = true;
	private IndexType currentType;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_activity_layout);
		listview = (ListView) findViewById(R.id.index_activity_listview);
		mEmptyListViewMessage  = (TextView)this.findViewById(R.id.msg_for_empty_schedule);
		adp = new SeparatedListIndexedAdapter(this); //Creating the separatedListIndexedAdapter now
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

		filmsButton.setEnabled(true);
		currentType = IndexType.FILMS;

		filmsButton.setBackgroundResource(R.drawable.enabledfilmsbutton);
		filmsButton.setTextColor(Color.WHITE);
		eventsButton.setBackgroundResource(R.drawable.disabledeventsbutton);
		eventsButton.setTextColor(Color.RED);

		registerForContextMenu(listview);

		fetchServerData(currentType);

		listview.setTextFilterEnabled(searchable);

		if (searchable) {

			searchText = (ClearableEditText) findViewById(R.id.searchText);

			searchText.setVisibility(View.VISIBLE);

			searchText.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
					if (s.length() > 0) {
						SeparatedListAdapter filterAdp = createFilterAdapter(s.toString());
						listview.setAdapter(filterAdp);
					} else {
						adp.setAsAdapterFor(listview);
					}
				}
			});
		}
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

	@Override
	protected void onPause() {
		super.onPause();
		searchText.setText("");
	};

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
		adp = (SeparatedListIndexedAdapter) createFilmletList((List<CommonItem>) listItems);
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
		searchText.setText("");
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
		searchText.setText("");
		currentType = IndexType.EVENTS;
		eventsButton.setEnabled(false);
		filmsButton.setEnabled(true);

		eventsButton.setBackgroundResource(R.drawable.enabledeventsbutton);
		eventsButton.setTextColor(Color.WHITE);
		filmsButton.setBackgroundResource(R.drawable.disabledfilmsbutton);
		filmsButton.setTextColor(Color.RED);

		fetchServerData(currentType);
	}

	private SeparatedListAdapter createFilterAdapter(String pattern) {
		Map<String, Adapter> sections = adp.sections;
		pattern = pattern.toLowerCase();
		int count = 0, numItems = 0, i;
		String headerTitle = "Found ";
		CommonItem item = null;
		ArrayList<CommonItem> list = new ArrayList<CommonItem>();
		for (Adapter adapter : sections.values()) {
			numItems = adapter.getCount();
			for (i = 0; i < numItems; i++) {
				item = (CommonItem) adapter.getItem(i);
				if (item.getTitle().toLowerCase().contains(pattern)) {
					list.add(item);
					count++;
				}
			}
		}

		if (currentType == IndexType.FILMS)
			headerTitle += count + " film(s)";
		else
			headerTitle += count + " event(s)";

		FilmletListAdapter filmAdp = new CinequestActivity.FilmletListAdapter(this, list);
		SeparatedListIndexedAdapter filterAdapter = new SeparatedListIndexedAdapter(this);
		filterAdapter.addSection(headerTitle, filmAdp);

		return filterAdapter;
	}
}
