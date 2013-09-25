package edu.sjsu.cinequest;

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
	public static final String ALPHA = "A - Z";

	// TODO: move menus down to CinequestActivity
	
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinequest_tab_activity_layout);
        listview = (ListView) findViewById(R.id.cinequest_tabactivity_listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listitem_title_only);
        final String[] dates = DateUtils.getFestivalDates();
        DateUtils du = new DateUtils();
        adapter.add(ALPHA);
        for (String date : dates) adapter.add(du.format(date, DateUtils.DATE_LONG));
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String result;
				if (position > 0)
					result = dates[position - 1];
				else
					result = ALPHA;
				Intent intent = new Intent();
				intent.setClass(FilmsActivity.this, FilmsActivity1.class);
				intent.putExtra("target", result);
				startActivity(intent);		
			}
		});
	}	
}
