package edu.sjsu.cinequest;

import java.util.List;
import java.util.Vector;

import edu.sjsu.cinequest.comm.cinequestitem.Filmlet;

public class DVDActivity extends CinequestTabActivity {

	@Override
	protected void fetchServerData() {
		HomeActivity.getQueryManager().getDVDs(
				new ProgressMonitorCallback(this) {    		
			public void invoke(Object result) {
				super.invoke(result);
				refreshListContents((Vector<Filmlet>) result);
			}});
	}

	@Override
	protected void refreshListContents(List<?> listItems) {
   		setListViewAdapter(createFilmletList((List<Filmlet>) listItems));
	}
}
