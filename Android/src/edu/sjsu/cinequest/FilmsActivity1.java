package edu.sjsu.cinequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;

/**
 * Films tab of the app, showing the films for a given date.
 * @author Prabhjeet Ghuman
 * @author Chao
 * @author Dmitri Dimov, Brian Guilardi, Charmi Shah
 */

public class FilmsActivity1 extends CinequestActivity{
	private boolean hasFilms;
	private boolean hasEvents;
	private boolean hasForums;
	private TextView nothingToday;
	private TreeSet<String> eventsOnly;
	private TreeSet<String> filmsOnly;
	private TreeSet<String> forumsOnly;
	private TextView header;
	private TreeMap<String, List<CommonItem>> mSchedule_byDate;
	private SeparatedListIndexedAdapter eventsAdapter;
	private DateUtils du;
	private ListView listview;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cinequest_tab_activity_layout);
		du = new DateUtils();
		hasFilms = false;
		hasEvents = false;
		hasForums = false;
		nothingToday = (TextView)FilmsActivity1.this.findViewById(R.id.msg_for_empty_schedyle);
		listview = (ListView) FilmsActivity1.this.findViewById(R.id.cinequest_tabactivity_listview);
		header = (TextView) FilmsActivity1.this.findViewById(R.id.textView1);
		header.setVisibility(View.GONE);
		eventsAdapter = new SeparatedListIndexedAdapter(FilmsActivity1.this);
		listview.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem > 0)
				{
					Object[] temp = eventsAdapter.sections.keySet().toArray();
					header.setVisibility(View.VISIBLE);
					header.setText((String)temp[eventsAdapter.getPositionForSection(firstVisibleItem)]);
				}
				else
				{
					header.setVisibility(View.GONE);
				}
			}
		});
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Object result = getListview().getItemAtPosition( position );
				launchFilmDetail(result);
			}
		});
		registerForContextMenu(listview);
		fetchServerData();
	}

	/**
	 * Gets called when user returns to this tab. Also gets called once after the 
	 * onCreate() method too.
	 */

	@Override
	public void onResume(){
		super.onResume();
		//We do not want to refresh, otherwise, user has to scroll all the way up,
		//maybe the user wants to keep checking the list.
	}

	protected void fetchServerData() {
		HomeActivity.getQueryManager().getEventDates (new ProgressMonitorCallback(this) {           		 
			public void invoke(Object result) {
				super.invoke(result);
				eventsOnly = (TreeSet<String>) result;
			}
		});
		HomeActivity.getQueryManager().getFilmDates (new ProgressMonitorCallback(this) {           		 
			public void invoke(Object result) {
				super.invoke(result);
				filmsOnly = (TreeSet<String>) result;
			}
		});
		HomeActivity.getQueryManager().getForumDates (new ProgressMonitorCallback(this) {           		 
			public void invoke(Object result) {
				super.invoke(result);
				forumsOnly = (TreeSet<String>) result;

				if(eventsOnly != null) { hasEvents = true; }
				if(filmsOnly != null) { hasFilms = true; }
				if(forumsOnly != null) { hasForums = true; }

				if(hasEvents)
				{
					for(final String date: eventsOnly)
					{
						HomeActivity.getQueryManager().getEventsByDate(date,new ProgressMonitorCallback(FilmsActivity1.this) {
							@SuppressLint("NewApi") @Override
							public void invoke(Object result) {
								super.invoke(result);
								mSchedule_byDate = (TreeMap<String, List<CommonItem>>) result;
								eventsAdapter.addSection(localizeHumanFormat(date), "", refreshListContents(mSchedule_byDate));
								if(hasEvents && !hasFilms && !hasForums)
								{
									//Then show it
									eventsAdapter.setAsAdapterFor(listview);
									if(eventsAdapter.getCount() == 0){
										listview.setVisibility(View.GONE);
										nothingToday.setVisibility(View.VISIBLE);
									}else{
										listview.setVisibility(View.VISIBLE);
										nothingToday.setVisibility(View.GONE);
									}
								}
							}
						});
					}
				}
				if(hasFilms)
				{
					for(final String date: filmsOnly) //final ensures that every date will get passed to refreshListContents
					{
						HomeActivity.getQueryManager().getFilmsByDate(date,new ProgressMonitorCallback(FilmsActivity1.this) {
							@SuppressLint("NewApi") @Override
							public void invoke(Object result) {
								super.invoke(result);
								mSchedule_byDate = (TreeMap<String, List<CommonItem>>) result;
								if(eventsAdapter.haveSection(localizeHumanFormat(date))) //Ensures that new content under same day
								{ //falls under same sections
									FilmletListAdapter a = (FilmletListAdapter) eventsAdapter.getAdapterForSection(localizeHumanFormat(date));
									a.addAll(refreshListContents(mSchedule_byDate).getList());
									eventsAdapter.appendAdapter(localizeHumanFormat(date), a);
								}
								else
								{
									eventsAdapter.addSection(localizeHumanFormat(date), "", refreshListContents(mSchedule_byDate));
								}
								if(hasFilms && hasEvents && !hasForums || hasFilms && !hasEvents && !hasForums)
								{
									//Then show it once everything has been added!
									eventsAdapter.setAsAdapterFor(listview);
									if(eventsAdapter.getCount() == 0){
										listview.setVisibility(View.GONE);
										nothingToday.setVisibility(View.VISIBLE);
									}else{
										listview.setVisibility(View.VISIBLE);
										nothingToday.setVisibility(View.GONE);
									}
								}
							}
						});
					}
				}
				if(hasForums)
				{
					for(final String date: forumsOnly)
					{
						HomeActivity.getQueryManager().getForumsByDate(date,new ProgressMonitorCallback(FilmsActivity1.this) {
							@SuppressLint("NewApi") @Override
							public void invoke(Object result) {
								super.invoke(result);
								mSchedule_byDate = (TreeMap<String, List<CommonItem>>) result;
								if(eventsAdapter.haveSection(localizeHumanFormat(date))) //Ensures that new content under same day
								{ //falls under same sections
									FilmletListAdapter a = (FilmletListAdapter) eventsAdapter.getAdapterForSection(localizeHumanFormat(date));
									a.addAll((Collection<? extends CommonItem>)refreshListContents(mSchedule_byDate).getList());
									eventsAdapter.appendAdapter(localizeHumanFormat(date), a);
								}
								else
								{
									eventsAdapter.addSection(localizeHumanFormat(date), "", refreshListContents(mSchedule_byDate));
								}
								if(hasForums && !hasFilms && !hasEvents ||
										hasForums && hasEvents &&!hasFilms ||
										hasForums && hasFilms &&!hasEvents ||
										hasEvents && hasFilms && hasForums)
								{
									//Then show it once everything has been added!
									eventsAdapter.setAsAdapterFor(listview);
									if(eventsAdapter.getCount() == 0){
										listview.setVisibility(View.GONE);
										nothingToday.setVisibility(View.VISIBLE);
									}else{
										listview.setVisibility(View.VISIBLE);
										nothingToday.setVisibility(View.GONE);
									}
								}
							}
						});
					}
				}
			}
		});
	}

	@SuppressLint("NewApi")
	protected FilmletListAdapter refreshListContents(TreeMap<String, List<CommonItem>> listItems) {
		if (listItems == null) return null;
		List<CommonItem> accumulate = new ArrayList<CommonItem>();
		for(Entry<String, List<CommonItem>> item : listItems.entrySet())
		{
			accumulate.addAll(item.getValue());
		}
		return new FilmletListAdapter(this,accumulate);
	}

	/**
	 * Sets the message to show to user when listview is empty
	 * @param message String
	 */
	protected final void setEmptyListviewMessage(String message){
		nothingToday.setText(message);
	}

	/**
	 * Sets the message to show to user when listview is empty
	 * @param resourceId Integer
	 */
	protected final void setEmptyListviewMessage(int resourceId){
		this.setEmptyListviewMessage( this.getString(resourceId) );
	}

	/**
	 * @return the ListView for this activity
	 */
	protected ListView getListview() {
		return listview;
	}

	/**
	 * This method returns date in Verbal Format
	 * */
	private String localizeHumanFormat(String inputDate)
	{
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = fmt.parse(inputDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String dateInHumForm = (String)android.text.format.DateFormat.format("EEEE, MMMM dd", date);
		String[] temp = dateInHumForm.split(" ");
		int dayOfMonth = Integer.parseInt(temp[2]);
		return temp[0] + " " + temp[1] + " " + dayOfMonth;
	}

	/**
	 * This method will localize the given date according the device locale.
	 * 
	 * @param inputDate The input date.
	 * @return The equivalent date in device locale
	 */
	private String localizeDate( String inputDate ){
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = fmt.parse(inputDate);
		} catch (ParseException e) {
			return inputDate;
		}
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
		return dateFormat.format(date);	
	}
}
