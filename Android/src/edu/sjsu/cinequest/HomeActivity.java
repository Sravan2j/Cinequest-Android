package edu.sjsu.cinequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.ImageManager;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.QueryManager;
import edu.sjsu.cinequest.comm.cinequestitem.News;
import edu.sjsu.cinequest.comm.cinequestitem.NewsFeed;
import edu.sjsu.cinequest.comm.cinequestitem.User;

// TODO: Add click for each item; show the section info

/**
 * The home screen of the app  
 * 
 * @author Prabhjeet Ghuman
 *
 */
public class HomeActivity extends Activity {	
	private ListView list;
	private String target;
	ImageView title_image;

	private static QueryManager queryManager;
	private static ImageManager imageManager;
	LazyAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);

		// TODO: Remove this to turn on test mode
		// DateUtils.setMode(DateUtils.FESTIVAL_TEST_MODE);
		Context context = getApplicationContext();
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(getPackageName(), 0);
			String version = pi.versionName;
			setTitle("Cinequest" + (version == null ? "" : " " + version));
		} catch (NameNotFoundException ex) {
			// We tried...
		}

		if (queryManager == null)
		{
			queryManager = new QueryManager();			
		}
		imageManager = new ImageManager();

		list = (ListView)this.findViewById(R.id.home_newslist);
	}

	/**
	 * Called when activity resumes
	 */
	@Override
	public void onResume(){
		super.onResume();    	
		queryManager.getSpecialScreen("ihome", new Callback(){
			@Override
			public void invoke(Object result) {
				populateNewsEventsList((NewsFeed) result);
				// TODO: Why doesn't this work???
				if (!((NewsFeed) result).getLastUpdated().equalsIgnoreCase(queryManager.getlastUpdated()))
					queryManager.prefetchFestival();
			}
			@Override public void starting() {}			
			@Override public void failure(Throwable t) {
				Platform.getInstance().log(t);				
			}        	
		});
	}

	protected void onStop(){
		imageManager.close();
		Platform.getInstance().close();
		super.onStop();
	}

	/**
	 * Display the header image and schedule to the user with 
	 * section-title being separator-header.
	 */
	
	private void populateNewsEventsList(NewsFeed newsSections)
	{				
		final List<News> news=newsSections.getNewsList();
		ArrayList<String> imgURL = new ArrayList<String>();
		for (int i=0;i<news.size();i++)
		{
			//imgURL.add(news.get(i).getEventImage());
			imgURL.add(news.get(i).getThumbImage());			
		}
		String[] imageURL=imgURL.toArray(new String[imgURL.size()]);

		adapter=new LazyAdapter(this, imageURL, news);
		list.setAdapter(adapter);		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				final Intent intent;
				if (news.get(position).getInfoLink().trim().toLowerCase().startsWith("http"))
				{

					intent = new Intent(android.content.Intent.ACTION_VIEW, 
							Uri.parse(news.get(position).getInfoLink()));			
					startActivity(intent);
				}
				else
				{
					int itemId= Integer.parseInt(news.get(position).getInfoLink());
					intent = new Intent();
					intent.setClass(HomeActivity.this, FilmDetail.class);

					HomeActivity.getQueryManager().getCommonItem(new ProgressMonitorCallback(HomeActivity.this) {
						@Override
						public void invoke(Object result) {
							super.invoke(result);							
							intent.putExtra("target", (Serializable) result);
							startActivity(intent);

						}
					}, itemId);

				}
			}
		});		
	}

	/**
	 * Get the QueryManager
	 * @return queryManager
	 */
	public static QueryManager getQueryManager() {
		return queryManager;
	}

	public static void setQueryManager(QueryManager q) {
		queryManager=q;
	}

	public static ImageManager getImageManager() {
		return imageManager;
	}

	/**
	 * Create a menu to be displayed when user hits Menu key on device
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.homeactivity_menu, menu);

		return true;
	}

	/** Menu Item Click Listener*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_option_about:
			DialogPrompt.showAppAboutDialog(this);
			return true;	        	            

		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
