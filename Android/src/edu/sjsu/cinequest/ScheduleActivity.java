package edu.sjsu.cinequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The Schedule Tab of the app 
 * 
 * @author Prabhjeet Ghuman
 *
 */

class EventData {
	public int eid;
	public String name;

	public String stime;
	public String etime;
	public EventData(int _eid,String _name, String _stime, String _etime) {
		eid = _eid;
		name = _name;
		stime = _stime;
		etime = _etime;
	}

	public String getName() {
		return name;
	}
	public String getStime() {
		return stime;
	}
	public String getEtime() {
		return etime;
	}
	public int getEId() {
		return eid;
	}    
}
public class ScheduleActivity extends Activity {
	ListView listView;
	private List<EventData> events = new ArrayList<EventData>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedulelayout);		
	}

	public void populateSchedule()
	{
		String calendarName="Cinequest Calendar";
		String m_selectedCalendarId = "Cinequest Calendar";

		String[] proj = new String[]{"_id", "calendar_displayName"};

		String calSelection = 
				"(calendar_displayName= ?) ";
		String[] calSelectionArgs = new String[] {
				calendarName                                        
		}; 

		Uri event = Uri.parse("content://com.android.calendar/calendars");        
		Cursor l_managedCursor = getContentResolver().query(event, proj, calSelection, calSelectionArgs, null );
		if (l_managedCursor.moveToFirst()) {                        

			int l_idCol = l_managedCursor.getColumnIndex(proj[0]);
			do {                
				m_selectedCalendarId = l_managedCursor.getString(l_idCol);                
			} while (l_managedCursor.moveToNext());
		}

		l_managedCursor.close();
		l_managedCursor=null;

		Uri l_eventUri;

		l_eventUri = Uri.parse("content://com.android.calendar/events");
		String[] l_projection = new String[]{"_id","title", "dtstart", "dtend"};

		l_managedCursor = this.getContentResolver().query(l_eventUri, l_projection, "calendar_id=" + m_selectedCalendarId, null, "dtstart DESC, dtend DESC");

		if (l_managedCursor.moveToFirst()) {
			String l_title;
			String l_begin;
			String l_end;
			int e_id;
			int l_colid = l_managedCursor.getColumnIndex(l_projection[0]);
			int l_colTitle = l_managedCursor.getColumnIndex(l_projection[1]);
			int l_colBegin = l_managedCursor.getColumnIndex(l_projection[2]);
			int l_colEnd = l_managedCursor.getColumnIndex(l_projection[3]);
			do {
				e_id = l_managedCursor.getInt(l_colid);
				l_title = l_managedCursor.getString(l_colTitle);
				l_begin = getDateTimeStr(l_managedCursor.getString(l_colBegin));
				l_end = getDateTimeStr(l_managedCursor.getString(l_colEnd));
				EventData edata= new EventData(e_id, l_title, l_begin, l_end);
				events.add(edata);
			} 
			while (l_managedCursor.moveToNext());
		}

		ArrayAdapter<EventData> adapter = new ArrayAdapter<EventData>(
				this.getApplicationContext(), R.layout.eventlistview, events) {
			@Override
			public View getView(final int position, View v, ViewGroup parent) {

				LayoutInflater inflater = LayoutInflater.from(getContext());
				final EventData q = getItem(position);                                
				if (v == null) v = inflater.inflate(R.layout.eventlistview, null);                                
				TextView textView = (TextView) v.findViewById(R.id.eventName);
				textView.setText(q.getName());

				TextView textView1 = (TextView) v.findViewById(R.id.startTime);
				textView1.setText(q.getStime());

				TextView textView2 = (TextView) v.findViewById(R.id.endTime);
				textView2.setText(q.getEtime());              
				Button button1 = (Button) v.findViewById(R.id.remove);
				button1.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick(View v) {
						Uri eventUri;                    
						if (Build.VERSION.SDK_INT >= 8) {
							eventUri = Uri.parse("content://com.android.calendar/events");
						} else {
							eventUri = Uri.parse("content://calendar/events");
						}                        
						Uri deleteUri = ContentUris.withAppendedId(eventUri, q.getEId());
						int rows = getContentResolver().delete(deleteUri, null, null);
						if (rows==1){
							events.remove(position);
							listView.invalidateViews();
						}
					}
				});


				return v;                                
			}                            
		};
		listView = (ListView) findViewById(R.id.schedule_listview);
		listView.setAdapter(adapter);
		
		listView.setItemsCanFocus(false);
		l_managedCursor.close();
		l_managedCursor=null;
	}

	private static final String DATE_TIME_FORMAT = "yyyy MMM dd, HH:mm:ss";
	public static String getDateTimeStr(int p_delay_min) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		if (p_delay_min == 0) {
			return sdf.format(cal.getTime());
		} else {
			Date l_time = cal.getTime();
			l_time.setMinutes(l_time.getMinutes() + p_delay_min);
			return sdf.format(l_time);
		}
	}

	public static String getDateTimeStr(String p_time_in_millis) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		Date l_time = new Date(Long.parseLong(p_time_in_millis));
		return sdf.format(l_time);
	}
	
	@Override
	protected void onResume() {
		super.onResume();  // Always call the superclass method first		
		events.clear();
		populateSchedule();
	}

}

