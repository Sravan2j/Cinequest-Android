package edu.sjsu.cinequest;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/**
 * This superclass has convenience methods for making lists of schedules and
 * filmlets.
 * 
 * It also contains ListAdapters ScheduleListAdapter and FilmletListAdapter. It
 * handles MenuOptions: Home, Schedule, and About
 * 
 */
public class CinequestActivity extends Activity {
	private static final int HOME_MENUOPTION_ID = Menu.FIRST + 11;
	private static final int SCHEDULE_MENUOPTION_ID = Menu.FIRST + 12;
	private static final int ABOUT_MENUOPTION_ID = Menu.FIRST + 13;
	public static String calendarName = "Cinequest Calendar";
	public static String m_selectedCalendarId = "Cinequest Calendar";

	/**
	 * Launches the FilmDetail activity for the given object.
	 * 
	 * @param result
	 *            Object; Can be Schedule, Filmlet etc
	 */
	protected void launchFilmDetail(Object result) {
		Intent intent = new Intent();
		intent.setClass(this, FilmDetail.class);
		intent.putExtra("target", (Serializable) result);
		startActivity(intent);
	}

	/**
	 * Creates a list of schedules
	 * 
	 * @param listItems
	 *            the list items
	 * @return the list adapter
	 */
	protected ListAdapter createFilmletList(List<? extends CommonItem> listItems) {
		if (listItems.size() == 0) {
			return new SeparatedListAdapter(this);
		}

		SeparatedListIndexedAdapter adapter = new SeparatedListIndexedAdapter(
				this);
		TreeMap<String, ArrayList<CommonItem>> filmsTitleMap = new TreeMap<String, ArrayList<CommonItem>>();
		String titleInitial = "";
		for (CommonItem f : listItems) {
			titleInitial = getTitleInitial(f.getTitle(), titleInitial);

			if (!filmsTitleMap.containsKey(titleInitial))
				filmsTitleMap.put(titleInitial, new ArrayList<CommonItem>());
			filmsTitleMap.get(titleInitial).add(f);
		}

		for (String titleInit : filmsTitleMap.keySet()) {
			adapter.addSection(titleInit, titleInit, new FilmletListAdapter(
					this, filmsTitleMap.get(titleInit)));
		}
		return adapter;
	}

	/**
	 * A method to get the first (relevant) letter of a title
	 * 
	 * @param title
	 *            The title of the film
	 * @param previousInitial
	 *            the previous initital
	 * @return The initial of the movie title
	 */
	private static String getTitleInitial(String title, String previousInitial) {
		String ucTitle = title.toUpperCase();
		if (ucTitle.startsWith("A ") || ucTitle.startsWith("AN ")
				|| ucTitle.startsWith("THE "))
			ucTitle = ucTitle.substring(ucTitle.indexOf(' ') + 1);
		String initial = ucTitle.substring(0, 1);
		if (initial.compareTo(previousInitial) < 0)
			Platform.getInstance().log(
					"Didn't expect " + title + " after section "
							+ previousInitial);
		return initial;
	}

	/**
	 * An adapter for a list of schedule items. These lists occur (1) in the
	 * Films tab (when sorted by date), Events and Forums tabs, (2) in each film
	 * detail, and (3) in the Schedule tab.
	 */
	protected class ScheduleListAdapter extends ArrayAdapter<Schedule> {
		private static final int RESOURCE_ID = R.layout.listitem_titletimevenue;
		private DateUtils du = new DateUtils();
		boolean is24HourFormat = android.text.format.DateFormat
				.is24HourFormat(getContext());

		public ScheduleListAdapter(Context context, List<Schedule> list) {
			super(context, RESOURCE_ID, list);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(RESOURCE_ID, null);
			}

			TextView title = (TextView) v.findViewById(R.id.titletext);
			TextView time = (TextView) v.findViewById(R.id.timetext);
			TextView venue = (TextView) v.findViewById(R.id.venuetext);
			Button button = (Button) v.findViewById(R.id.myschedule_checkbox);
			final Schedule result = getItem(position);
			title.setText(result.getTitle());
			if (result.isSpecialItem())
				title.setTypeface(null, Typeface.ITALIC);
			String startTime = du.format(result.getStartTime(),
					DateUtils.TIME_SHORT);
			String endTime = du.format(result.getEndTime(),
					DateUtils.TIME_SHORT);
			if (!is24HourFormat) {
				startTime = du.formatTime(startTime);
				if (startTime.length() == 7)
					startTime = "0" + startTime;
				endTime = du.formatTime(endTime);
				if (endTime.length() == 7)
					endTime = "0" + endTime;
			}
			time.setText("Time: " + startTime + " - " + endTime);
			venue.setText("Venue: " + result.getVenue());
			formatContents(v, title, time, venue, du, result);
			v.setTag(result);
			button.setTag(result);
			button.setText("-");
			populateCalendarID();
			configureCalendarIcon(v, button, result);
			Button directions = (Button) v.findViewById(R.id.directionsURL);
			directions.setTag(result);
			directions.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(
							android.content.Intent.ACTION_VIEW, Uri
							.parse(result.getDirectionsURL()));
					startActivity(intent);
				}

			});
			return v;
		}

		/**
		 * Override to change the formatting of the contents
		 * 
		 * @param View
		 *            to be formated, title, time, venue, du, result
		 */
		protected void formatContents(View v, TextView title, TextView time,
				TextView venue, DateUtils du, Schedule result) {
		}

		// Calendar code for adding/removing events from Device Calendar
		/**
		 * Configures the Calendar code for adding/removing events from Device
		 * Calendar
		 * 
		 * @param The
		 *            View to configure the button and Schedule
		 */
		protected void configureCalendarIcon(View v, final Button button,
				Schedule result) {
			button.setVisibility(View.VISIBLE);

			Schedule s = result;

			SimpleDateFormat formatter;
			if (s.getStartTime().charAt(10) == 'T')
				formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
			else
				formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date startDate = null;
			Date endDate = null;

			try {
				startDate = (Date) formatter.parse(s.getStartTime());
				endDate = (Date) formatter.parse(s.getEndTime());
			} catch (ParseException e) {

				e.printStackTrace();
			}
			long begin = startDate.getTime();
			long end = endDate.getTime();
			String[] proj = new String[] { "_id", "title", "dtstart", "dtend" };

			String calSelection = "((calendar_id= ?) " + "AND ("
					+ "((dtstart= ?) " + "AND (dtend= ?) " + "AND (title= ?) "
					+ ") " + ")" + ")";
			String[] calSelectionArgs = new String[] { m_selectedCalendarId,
					begin + "", end + "", s.getTitle() };

			Uri event = null;
			Cursor l_managedCursor = null;

			if (Build.VERSION.SDK_INT >= 8) {
				event = Uri.parse("content://com.android.calendar/events");
			} else {
				// Calendar code for API level < 8, needs lot of testing.
				// May be some of the paramters (that we are populating above),
				// have different naming conventions in different API Levels
				event = Uri.parse("content://calendar/events");
			}
			try {
				l_managedCursor = getContentResolver().query(event, proj,
						calSelection, calSelectionArgs,
						"dtstart DESC, dtend DESC");
			} catch (Exception e) {
				Log.i("CinequestActivity:configureCalendarIcon",
						"Error while retrieving Event details from Calendar");
			}

			if (l_managedCursor.getCount() > 0) {
				button.setBackgroundResource(R.drawable.incalendar);
				button.setHint("exists");
			} else {
				button.setBackgroundResource(R.drawable.notincalendar);
				button.setHint("notexist");
			}
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Schedule s = (Schedule) v.getTag();
					SimpleDateFormat formatter;
					if (s.getStartTime().charAt(10) == 'T')
						formatter = new SimpleDateFormat(
								"yyyy-MM-dd'T'hh:mm:ss");
					else
						formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

					Date startDate = null;
					Date endDate = null;

					try {
						startDate = (Date) formatter.parse(s.getStartTime());
						endDate = (Date) formatter.parse(s.getEndTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					long begin = startDate.getTime();
					long end = endDate.getTime();

					if (button.getHint().toString() == "exists") {
						String[] proj = new String[] { "_id", "title",
								"dtstart", "dtend" };

						String calSelection = "((calendar_id= ?) " + "AND ("
								+ "((dtstart= ?) " + "AND (dtend= ?) "
								+ "AND (title= ?) " + ") " + ")" + ")";
						String[] calSelectionArgs = new String[] {
								m_selectedCalendarId, begin + "", end + "",
								s.getTitle() };

						Uri event = null;
						Cursor l_managedCursor = null;
						if (Build.VERSION.SDK_INT >= 8) {
							event = Uri
									.parse("content://com.android.calendar/events");
						} else {
							// Calendar code for API level < 8, needs lot of
							// testing.
							// May be some of the paramters (that we are
							// populating above), have different naming
							// conventions in different API Levels
							event = Uri.parse("content://calendar/events");
						}
						try {
							l_managedCursor = getContentResolver().query(event,
									proj, calSelection, calSelectionArgs,
									"dtstart DESC, dtend DESC");
						} catch (Exception e) {
							Log.i("CinequestActivity:configureCalendarIcon",
									"Error while retrieving Event details from Calendar");
						}

						int e_id = 0;
						if (l_managedCursor.moveToFirst()) {
							int l_colid = l_managedCursor
									.getColumnIndex(proj[0]);
							do {
								e_id = l_managedCursor.getInt(l_colid);
							} while (l_managedCursor.moveToNext());
						}
						Uri eventUri;
						if (Build.VERSION.SDK_INT >= 8) {
							eventUri = Uri
									.parse("content://com.android.calendar/events");
						} else {
							// Calendar code for API level < 8, needs lot of
							// testing.
							// May be some of the paramters (that we are
							// populating above), have different naming
							// conventions in different API Levels
							eventUri = Uri.parse("content://calendar/events");
						}
						Uri deleteUri = ContentUris.withAppendedId(eventUri,
								e_id);
						try {
							int rows = getContentResolver().delete(deleteUri,
									null, null);
							if (rows == 1) {
								button.setBackgroundResource(R.drawable.notincalendar);
								button.setHint("notexist");
								Toast toast = Toast.makeText(getContext(),
										"Event removed from calendar",
										Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (Exception e) {
							Log.i("CinequestActivity:configureCalendarIcon",
									"Error while removing Events from Calendar");
						}
						l_managedCursor.close();
						l_managedCursor = null;
					}

					else {
						ContentValues l_event = new ContentValues();
						l_event.put("calendar_id", m_selectedCalendarId);
						l_event.put("title", s.getTitle());
						l_event.put("description", s.getTitle());
						l_event.put("eventLocation", s.getVenue());
						// l_event.put("dtstart", System.currentTimeMillis());
						// l_event.put("dtend", System.currentTimeMillis() +
						// 1800*1000);
						l_event.put("dtstart", begin);
						l_event.put("dtend", end);
						l_event.put("allDay", 0);
						// status: 0~ tentative; 1~ confirmed; 2~ canceled
						l_event.put("eventStatus", 1);
						// 0~ default; 1~ confidential; 2~ private; 3~ public
						// l_event.put("visibility", 1);
						// 0~ opaque, no timing conflict is allowed; 1~
						// transparency, allow overlap of scheduling
						// l_event.put("transparency", 0);
						// 0~ false; 1~ true
						l_event.put("hasAlarm", 1);
						l_event.put("eventTimezone", TimeZone.getDefault()
								.getID());
						Uri l_eventUri;
						if (Build.VERSION.SDK_INT >= 8) {
							l_eventUri = Uri
									.parse("content://com.android.calendar/events");
						} else {
							// Calendar code for API level < 8, needs lot of
							// testing.
							// May be some of the paramters (that we are
							// populating above), have different naming
							// conventions in different API Levels
							l_eventUri = Uri.parse("content://calendar/events");
						}
						try {
							Uri l_uri = getContentResolver().insert(l_eventUri,
									l_event);
							Toast toast = Toast.makeText(getContext(),
									"Event added to calendar",
									Toast.LENGTH_SHORT);
							toast.show();
							button.setBackgroundResource(R.drawable.incalendar);
							button.setHint("exists");
						} catch (Exception e) {
							Log.i("CinequestActivity:configureCalendarIcon",
									"Error while adding Events in Calendar");
						}
					}

				}

			});
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Schedule s = (Schedule) v.getTag();
					SimpleDateFormat formatter;
					if (s.getStartTime().charAt(10) == 'T')
						formatter = new SimpleDateFormat(
								"yyyy-MM-dd'T'hh:mm:ss");
					else
						formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

					Date startDate = null;
					Date endDate = null;

					try {
						startDate = (Date) formatter.parse(s.getStartTime());
						endDate = (Date) formatter.parse(s.getEndTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					long begin = startDate.getTime();
					long end = endDate.getTime();

					if (button.getHint().toString() == "exists") {
						String[] proj = new String[] { "_id", "title",
								"dtstart", "dtend" };

						String calSelection = "((calendar_id= ?) " + "AND ("
								+ "((dtstart= ?) " + "AND (dtend= ?) "
								+ "AND (title= ?) " + ") " + ")" + ")";
						String[] calSelectionArgs = new String[] {
								m_selectedCalendarId, begin + "", end + "",
								s.getTitle() };

						Uri event = null;
						Cursor l_managedCursor = null;
						if (Build.VERSION.SDK_INT >= 8) {
							event = Uri
									.parse("content://com.android.calendar/events");
						} else {
							// Calendar code for API level < 8, needs lot of
							// testing.
							// May be some of the paramters (that we are
							// populating above), have different naming
							// conventions in different API Levels
							event = Uri.parse("content://calendar/events");
						}
						try {
							l_managedCursor = getContentResolver().query(event,
									proj, calSelection, calSelectionArgs,
									"dtstart DESC, dtend DESC");
						} catch (Exception e) {
							Log.i("CinequestActivity:configureCalendarIcon",
									"Error while retrieving Event details from Calendar");
						}

						int e_id = 0;
						if (l_managedCursor.moveToFirst()) {
							int l_colid = l_managedCursor
									.getColumnIndex(proj[0]);
							do {
								e_id = l_managedCursor.getInt(l_colid);
							} while (l_managedCursor.moveToNext());
						}
						Uri eventUri;
						if (Build.VERSION.SDK_INT >= 8) {
							eventUri = Uri
									.parse("content://com.android.calendar/events");
						} else {
							// Calendar code for API level < 8, needs lot of
							// testing.
							// May be some of the paramters (that we are
							// populating above), have different naming
							// conventions in different API Levels
							eventUri = Uri.parse("content://calendar/events");
						}
						Uri deleteUri = ContentUris.withAppendedId(eventUri,
								e_id);
						try {
							int rows = getContentResolver().delete(deleteUri,
									null, null);
							if (rows == 1) {
								button.setBackgroundResource(R.drawable.notincalendar);
								button.setHint("notexist");
								Toast toast = Toast.makeText(getContext(),
										"Event removed from calendar",
										Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (Exception e) {
							Log.i("CinequestActivity:configureCalendarIcon",
									"Error while removing Events from Calendar");
						}
						l_managedCursor.close();
						l_managedCursor = null;
					}

					else {
						ContentValues l_event = new ContentValues();
						l_event.put("calendar_id", m_selectedCalendarId);
						l_event.put("title", s.getTitle());
						l_event.put("description", s.getTitle());
						l_event.put("eventLocation", s.getVenue());
						// l_event.put("dtstart", System.currentTimeMillis());
						// l_event.put("dtend", System.currentTimeMillis() +
						// 1800*1000);
						l_event.put("dtstart", begin);
						l_event.put("dtend", end);
						l_event.put("allDay", 0);
						// status: 0~ tentative; 1~ confirmed; 2~ canceled
						l_event.put("eventStatus", 1);
						// 0~ default; 1~ confidential; 2~ private; 3~ public
						// l_event.put("visibility", 1);
						// 0~ opaque, no timing conflict is allowed; 1~
						// transparency, allow overlap of scheduling
						// l_event.put("transparency", 0);
						// 0~ false; 1~ true
						l_event.put("hasAlarm", 1);
						l_event.put("eventTimezone", TimeZone.getDefault()
								.getID());
						Uri l_eventUri;
						if (Build.VERSION.SDK_INT >= 8) {
							l_eventUri = Uri
									.parse("content://com.android.calendar/events");
						} else {
							// Calendar code for API level < 8, needs lot of
							// testing.
							// May be some of the paramters (that we are
							// populating above), have different naming
							// conventions in different API Levels
							l_eventUri = Uri.parse("content://calendar/events");
						}
						try {
							Uri l_uri = getContentResolver().insert(l_eventUri,
									l_event);
							Toast toast = Toast.makeText(getContext(),
									"Event added to calendar",
									Toast.LENGTH_SHORT);
							toast.show();
							button.setBackgroundResource(R.drawable.incalendar);
							button.setHint("exists");
						} catch (Exception e) {
							Log.i("CinequestActivity:configureCalendarIcon",
									"Error while adding Events in Calendar");
						}
					}

				}

			});

		}
	}

	/**
	 * An adapter for a list of films. These lists occur (1) in the Films tab
	 * (when sorted by name) (2) in the DVDs tab and (3) in the detail view of a
	 * program item with multiple films.
	 */
	protected static class FilmletListAdapter extends ArrayAdapter<CommonItem> {
		private static final int RESOURCE_ID = R.layout.listitem_title_only;
		private static List<CommonItem> referenceToList;
		public FilmletListAdapter(Context context,
				List<CommonItem> accumulate) {
			super(context, RESOURCE_ID, referenceToList = (List<CommonItem>) accumulate);
		}
		
		/**
		 * Returns the list that was passed
		 * */
		public List<CommonItem> getList()
		{
			return referenceToList;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 * 
		 * @param position (int), view, and the parent ViewGroup
		 * 
		 * @return the view
		 */
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(RESOURCE_ID, null);
			}

			CommonItem result = getItem(position);
			TextView title = (TextView) v.findViewById(R.id.listitem_titletext);
			title.setText(result.getTitle());
			formatContents(v, title, result);
			return v;
		}

		/**
		 * Override to change the formatting of the contents
		 */
		protected void formatContents(View v, TextView title, CommonItem result) {
		}
	}

	/**
	 * Takes the user to home activity from the current activity.
	 */
	private void goHome() {

		Intent i = new Intent();
		i.setClass(this, MainTab.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	/**
	 * Takes the user to the schedule activity from the current activity.
	 */
	private void goSchedule() {
		Intent i = new Intent();
		i.setClass(this, ScheduleActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra("open_tab", 4);
		startActivity(i);
	}

	/**
	 * Create a menu to be displayed when user hits Menu key on device
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, HOME_MENUOPTION_ID, 0, "Home").setIcon(R.drawable.home);
		menu.add(0, SCHEDULE_MENUOPTION_ID, 0, "Schedule").setIcon(
				R.drawable.schedule_icon);
		menu.add(0, ABOUT_MENUOPTION_ID, 0, "About").setIcon(R.drawable.about);

		return true;
	}

	/** Menu Item Click Listener */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case HOME_MENUOPTION_ID:
			goHome();
			return true;
		case SCHEDULE_MENUOPTION_ID:
			goSchedule();
			return true;
		case ABOUT_MENUOPTION_ID:
			DialogPrompt.showAppAboutDialog(this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/**
	 * Gets the calendar id for Cinequest and populates it.
	 */
	public void populateCalendarID() {
		String[] proj = new String[] { "_id", "calendar_displayName" };
		String calSelection = "(calendar_displayName= ?) ";
		String[] calSelectionArgs = new String[] { calendarName };

		Uri event = null;

		if (Build.VERSION.SDK_INT >= 8) {
			event = Uri.parse("content://com.android.calendar/calendars");
		} else {
			// Calendar code for API level < 8, needs lot of testing.
			// May be some of the paramters (that we are populating above), have
			// different naming conventions in different API Levels
			event = Uri.parse("content://calendar/calendars");
		}
		Cursor l_managedCursor = null;
		try {
			l_managedCursor = getContentResolver().query(event, proj,
					calSelection, calSelectionArgs, null);

			if (l_managedCursor.moveToFirst()) {
				int l_idCol = l_managedCursor.getColumnIndex(proj[0]);
				do {
					m_selectedCalendarId = l_managedCursor.getString(l_idCol);
				} while (l_managedCursor.moveToNext());
			}
		} catch (Exception e) {
			Log.i("CinequestActivity:populateCalendarID",
					"Error while retrieving Cinequest Calendar ID from device Calendar");
		}

		l_managedCursor.close();
		l_managedCursor = null;
	}
}
