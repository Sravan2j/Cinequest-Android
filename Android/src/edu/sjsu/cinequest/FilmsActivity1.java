package edu.sjsu.cinequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/**
 * Films tab of the app, showing the films for a given date.
 * @author Prabhjeet Ghuman
 * @author Chao
 * @author Dmitri Dimov, Brian Guilardi, Charmi Shah
 */

public class FilmsActivity1 extends CinequestActivity
{
	private TextView nothingToday;
	private TextView header;
	private SortedMap<String, SortedSet<Schedule>> mSchedule_byDate;
	private SeparatedListIndexedAdapter eventsAdapter;
	private DateUtils du;
	private ListView listview;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cinequest_tab_activity_layout);
		du = new DateUtils();
		nothingToday = (TextView)FilmsActivity1.this.findViewById(R.id.msg_for_empty_schedyle);
		listview = (ListView) FilmsActivity1.this.findViewById(R.id.cinequest_tabactivity_listview);
		header = (TextView) FilmsActivity1.this.findViewById(R.id.textView1);
		header.setVisibility(View.GONE);
		registerForContextMenu(listview);
		fetchServerData();
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
				Schedule result = (Schedule) listview.getItemAtPosition( position );
				launchFilmDetail(result.getItem());
			}
		});
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
		SplashScreenActivity.getQueryManager().getSchedulesByDate(new ProgressMonitorCallback(this) {
            public void invoke(Object result) {
                super.invoke(result);
                mSchedule_byDate = (SortedMap<String, SortedSet<Schedule>>) result;

                for (final String date : mSchedule_byDate.keySet()) {
                    //List<CommonItem> items = new ArrayList<CommonItem>();
                    //for (Schedule s : mSchedule_byDate.get(date))
                    //    items.add(s.getItem());
                    // FilmletListAdapter a = new FilmletListAdapter(FilmsActivity1.this, items);
                    List<Schedule> items = new ArrayList<Schedule>(mSchedule_byDate.get(date));
                    ScheduleListAdapter1 a = new ScheduleListAdapter1(FilmsActivity1.this, items);
                    eventsAdapter.addSection(localizeHumanFormat(date), "", a);
                    //Then show it once everything has been added!
                    eventsAdapter.setAsAdapterFor(listview);
                    if (eventsAdapter.getCount() == 0) {
                        listview.setVisibility(View.GONE);
                        nothingToday.setVisibility(View.VISIBLE);
                    } else {
                        listview.setVisibility(View.VISIBLE);
                        nothingToday.setVisibility(View.GONE);
                    }
                }
            }
        });
    }



	/**
	 * Sets the message to show to user when listview is empty
	 * @param message String
	 */
	protected final void setEmptyListviewMessage(String message){
		nothingToday.setText(message);
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
}
