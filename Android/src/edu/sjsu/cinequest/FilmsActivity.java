package edu.sjsu.cinequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Films tab of the app, showing the A-Z dates and the dates. 
 */
public class FilmsActivity extends CinequestActivity {
	private ListView listview;
	private ArrayAdapter adapter;
	private String tab;
	private SortedSet<String> dates;
	public static final String ALPHA = "A - Z";	
	DateUtils du = new DateUtils();

	// TODO: move menus down to CinequestActivity

	public void onCreate(Bundle savedInstanceState) {    	
		tab = getIntent().getExtras().getString("tab");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cinequest_tab_activity_layout);        
		listview = (ListView) findViewById(R.id.cinequest_tabactivity_listview);
		adapter = new ArrayAdapter<String>(this, R.layout.listitem_title_only);
	}	
	private void displayList()
	{		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String result;
				if (position > 0)
					result = (String) (dates.toArray())[position - 1];
				else
					result = ALPHA;
				Intent intent = new Intent();
				intent.setClass(FilmsActivity.this, FilmsActivity1.class);
				intent.putExtra("target", result);
				intent.putExtra("tab", tab);
				startActivity(intent);		
			}
		});

	}
	public void onResume(){
		super.onResume();
		adapter.clear();
		if (tab.equalsIgnoreCase("films"))
		{			
			HomeActivity.getQueryManager().getFilmDates (new ProgressMonitorCallback(this) {           		 
				public void invoke(Object result) {
					super.invoke(result);															
					dates = (SortedSet<String>) result;
					adapter.add(ALPHA);
					for (String date : dates) 
					{						
						adapter.add(localizeDate(date));
					}
				}
			});			
			displayList();

		}
		else if (tab.equalsIgnoreCase("events"))
		{
			HomeActivity.getQueryManager().getEventDates(new ProgressMonitorCallback(this) {           		 
				public void invoke(Object result) {
					super.invoke(result);
					dates = (SortedSet<String>) result;
					adapter.add(ALPHA);
					for (String date : dates) 
					{						
						adapter.add(localizeDate(date));
					}
				}
			});
			displayList();

		}
	}

	/**
	 * This method will localize the given date according the device locale.
	 * 
	 * @param inputDate The input date.
	 * @return The equivalent date in device locale
	 */
	private String localizeDate( String inputDate ) {

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
