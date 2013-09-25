package edu.sjsu.cinequest;

import java.util.List;
import java.util.Vector;

import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

public class EventsActivity extends CinequestTabActivity {

	@Override
	protected void fetchServerData() {
		HomeActivity.getQueryManager().getEventSchedules("special_events", 
				new ProgressMonitorCallback(this) {    		
			public void invoke(Object result) {
				super.invoke(result);
				refreshListContents((Vector<Schedule>) result);
			}});
	}

	@Override
	protected void refreshListContents(List<?> listItems) {
   		setListViewAdapter(createScheduleList((List<Schedule>) listItems));
	}
}
