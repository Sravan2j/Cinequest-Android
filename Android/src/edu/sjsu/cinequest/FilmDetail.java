package edu.sjsu.cinequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.HParser;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;

import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

public class FilmDetail extends CinequestActivity {
	public static enum ItemType {FILM, PROGRAM_ITEM, DVD}
	private ListView scheduleList;
	private ListView includeList;
	private String fbTitle;
	private String fbImage;
	private String fbUrl;

	public void onCreate(Bundle savedInstanceState) {    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filmdetail);

		scheduleList = (ListView)findViewById(R.id.ScheduleList);

		includeList = (ListView)findViewById(R.id.IncludeList);

		View headerView = getLayoutInflater().inflate(
				R.layout.detail_layout, null);        
		ListView listView = (ListView) findViewById(R.id.ScheduleList);
		listView.addHeaderView(headerView, null, false);

		View footerView = getLayoutInflater().inflate(
				R.layout.shareoptions, null);
		listView.addFooterView(footerView, null, false);


		Button fbButton = (Button) findViewById(R.id.fbshare);
		fbButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//Check if a facebook session is available from existing app if not open one. // tanuvir-12Nov13
				loginToFacebook();
				//call the graph api to post to Users wall //tanuvir-12Nov13
				postToWall();	
			}
		});

		Button gmailButton = (Button) findViewById(R.id.gmail);
		gmailButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				sendEmail();		
			}
		});
		/*   
        Button infoButton = (Button) findViewById(R.id.moreinfo);
        infoButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				openWebPage();
			}
		});
		 */ 

		fetchServerData(getIntent().getExtras());	
	}

	private void fetchServerData(Bundle b){
		Object target = b.getSerializable("target");

		if (target instanceof CommonItem) {
			// This happens when showing a film inside a program item
			showFilm((CommonItem) target);
		} 

	}	

	private void showSchedules(Vector<Schedule> schedules)
	{
		if (schedules.size() == 0) {
			scheduleList.setAdapter(new ScheduleListAdapter(this, schedules));
			return;
		}
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		adapter.addSection("Schedules",
				new ScheduleListAdapter(this, schedules) {
			@Override
			protected void formatContents(View v, TextView title, TextView time, TextView venue, DateUtils du, Schedule result) {
				title.setText(du.format(result.getStartTime(), DateUtils.DATE_DEFAULT));
				title.setTypeface(null, Typeface.NORMAL);
			}
		});

		//toggle the checkbox upon list-item click
		/*		scheduleList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				((CheckBox)view.findViewById(R.id.myschedule_checkbox)).toggle();				
			}
		});*/
		scheduleList.setAdapter(adapter);
	}


	private void showIncludes(final ArrayList<CommonItem> includes)
	{		
		Log.i("FilmDetail:showIncludes",includes.size()+"");
		if (includes.size() == 0) {
			//scheduleList.setAdapter(new ScheduleListAdapter(this, includes));
			includeList.setAdapter(createFilmletList((List<CommonItem>) includes));
			return;
		}
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		adapter.addSection("Includes",
				new FilmletListAdapter(this, includes));
		includeList.setAdapter(adapter);
		includeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Object result = includes.get( position );
				launchFilmDetail(result);				
			}
		});

		
		/*SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		adapter.addSection("Includes",
		    createFilmletList((List<CommonItem>) includes));						
		//toggle the checkbox upon list-item click
		scheduleList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				((CheckBox)view.findViewById(R.id.myschedule_checkbox)).toggle();				
			}
		});
		includeList.setAdapter(adapter);
		 */	

		/*ArrayAdapter<CommonItem> adapter = new ArrayAdapter<CommonItem>(
				FilmDetail.this, R.layout.listitem_title_only, includes) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {

				LayoutInflater inflater = LayoutInflater.from(getContext());
				CommonItem q = getItem(position);                                
				if (v == null) v = inflater.inflate(R.layout.listitem_title_only, null);                                
				TextView textView = (TextView) v.findViewById(R.id.listitem_titletext);
				textView.setText(q.getTitle());                                                                
				return v;                                
			}                            
		};


		scheduleList.setAdapter(adapter);


		Log.i("Title",includes.get(0).getTitle());
		Log.i("TitleId",includes.get(0).getId()+"");
		Log.i("Title",includes.get(1).getTitle());
		Log.i("TitleId",includes.get(1).getId()+"");
		Log.i("Title",includes.get(2).getTitle());
*/
		//FilmletListAdapter adapter = new FilmletListAdapter(this, includes);

		/*ListAdapter adapter=createFilmletList((List<CommonItem>) includes);
		if(adapter==null){Log.i("NullisIncludes","NullisIncludes");}*/
		//includeList.setAdapter(adapter);	
		
		//scheduleList.setAdapter(createFilmletList((List<CommonItem>) includes));
		
	}



	private void showFilms(ArrayList<? extends CommonItem> films)
	{
		FilmletListAdapter section = new FilmletListAdapter(this, (List<CommonItem>) films);
		if (films.size() == 0) {
			scheduleList.setAdapter(section);
			return;
		}
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		adapter.addSection("Films", section);				
		scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Object result = scheduleList.getItemAtPosition( position );
				launchFilmDetail(result);				
			}
		});
		scheduleList.setAdapter(adapter);
	}


	private static void addEntry(SpannableStringBuilder ssb, String tag, String s) {
		if (s == null || s.equals("")) return;
		ssb.append(tag);
		ssb.append(": ");
		int end = ssb.length();
		int start = end - tag.length() - 2;
		ssb.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
		ssb.append(s);
		ssb.append("\n");
	}

	private void showImage(final String imageURL, Vector urls) {
		if (imageURL == null) return;
		Bitmap bmp = (Bitmap) HomeActivity.getImageManager().getImage(imageURL, new Callback() {
			@Override
			public void invoke(Object result) {
				Bitmap bmp = (Bitmap) result;
				((ImageView) findViewById(R.id.Image)).setImageBitmap(bmp);											  		
			}

			@Override
			public void starting() {
			}

			@Override
			public void failure(Throwable t) {	
				Platform.getInstance().log(t);
				// Try once more
				HomeActivity.getImageManager().getImage(imageURL, new Callback() {
					@Override
					public void invoke(Object result) {
						Bitmap bmp = (Bitmap) result;
						((ImageView) findViewById(R.id.Image)).setImageBitmap(bmp);											  		
					}

					@Override
					public void starting() {
					}

					@Override
					public void failure(Throwable t) {	
						Platform.getInstance().log(t);
					}   
				}, null, true);												
			}   
		}, R.drawable.fetching, true);					
		((ImageView) findViewById(R.id.Image)).setImageBitmap(bmp);											  		
	}

	private SpannableString createSpannableString(HParser parser) 
	{
		SpannableString spstr = new SpannableString(parser.getResultString());
		byte[] attributes = parser.getAttributes();
		int[] offsets = parser.getOffsets();
		for (int i = 0; i < offsets.length - 1; i++) {
			int start = offsets[i];
			int end = offsets[i + 1];
			byte attr = attributes[i];
			int flags = 0;
			if ((attr & HParser.BOLD) != 0)
				spstr.setSpan(new StyleSpan(Typeface.BOLD), start, end, flags);
			if ((attr & HParser.ITALIC) != 0)
				spstr.setSpan(new StyleSpan(Typeface.ITALIC), start, end, flags);
			if ((attr & HParser.LARGE) != 0)
				spstr.setSpan(new RelativeSizeSpan(1.2F), start, end, flags);					
			if ((attr & HParser.RED) != 0)
				spstr.setSpan(new ForegroundColorSpan(Color.RED), start, end, flags);
		}
		return spstr;
	}



	public void showFilm(CommonItem in) {
		fbTitle = in.getTitle();
		fbImage = in.getImageURL();
		fbUrl = in.getInfoLink();
		SpannableString title = new SpannableString(in.getTitle());
		title.setSpan(new RelativeSizeSpan(1.2F), 0, title.length(), 0);
		((TextView) findViewById(R.id.Title)).setText(title);

		TextView tv = (TextView) findViewById(R.id.Description);
		HParser parser = new HParser();
		parser.parse(in.getDescription());

		tv.setText(createSpannableString(parser));

		showImage(in.getImageURL(), parser.getImageURLs());


		SpannableStringBuilder ssb = new SpannableStringBuilder();

		addEntry(ssb, "Director", in.getDirector());
		addEntry(ssb, "Producer", in.getProducer());
		addEntry(ssb, "Editor", in.getEditor());
		addEntry(ssb, "Writer", in.getWriter());
		addEntry(ssb, "Cinematographer", in.getCinematographer());
		addEntry(ssb, "Cast", in.getCast());
		addEntry(ssb, "Country", in.getCountry());
		addEntry(ssb, "Language", in.getLanguage());
		addEntry(ssb, "Genre", in.getGenre());
		addEntry(ssb, "Film Info", in.getFilmInfo());

		((TextView) findViewById(R.id.Properties)).setText(ssb);
		showSchedules(in.getSchedules());
		showIncludes(in.getCommonItems());
		
	}

	public void showProgramItem(CommonItem item) 
	{
		ArrayList<CommonItem> films = item.getCommonItems();//.getFilms();

		if (films.size() == 1)
		{
			showFilm(films.get(0));
		} 
		else  						
		{
			// Show just the ProgramItem data
			fbTitle = item.getTitle();
			fbUrl = item.getInfoLink();
			SpannableString title = new SpannableString(item.getTitle());
			title.setSpan(new RelativeSizeSpan(1.2F), 0, title.length(), 0);
			((TextView) findViewById(R.id.Title)).setText(title);

			TextView tv = (TextView) findViewById(R.id.Description);
			HParser parser = new HParser();
			parser.parse(item.getDescription());

			tv.setText(createSpannableString(parser));

			showImage(item.getImageURL(), parser.getImageURLs());			
			showFilms(films);
		}
	}
	//New function to login to facebook // tanuvir-12Nov13
	void loginToFacebook(){
		try {
			Log.d("MyFunc", "Before Login");
			Session.openActiveSession(this, true, new Session.StatusCallback() {

				// callback when session changes state
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {

					Log.d("MyFunc", "Session Token=" + session.getAccessToken());
					Log.d("MyFunc", "Session Open or not =" + session.isOpened());

					if (session.isOpened()) {

						// make request to the /me API
						Request.executeMeRequestAsync(session,
								new Request.GraphUserCallback() {

							// callback after Graph API response with user
							// object
							@Override
							public void onCompleted(GraphUser user,
									Response response) {
								Log.d("MyFunc", "Request");
								if (user != null) {
									Log.d("MyFunc", "User: "+ user.getName());
								}
							}
						});
					}
				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("MyFunc", e.toString());
		}
	}

	//new function to post a status on facebook //tanuvir 12Nov13
	void postToWall(){
		try{
			if(fbImage == null || fbImage == "")
				fbImage = "http://www.cinequest.org/sites/default/files/styles/highlights/public/cqff24hero_970x360.jpg";
			if(fbUrl == null || fbUrl == "")
				fbUrl = "http://www.cinequest.org/film-festival";
			Log.d("MyFunc", "Before posting");
			Bundle params = new Bundle();
			params.putString("name","Is going to " + '"' + fbTitle + '"' );
			params.putString("caption", "at the Cinequest film festival");
			params.putString("description", "Cinequest provides the finest discovery bastion of international film premieres, technology, and more.");
			params.putString("link", fbUrl);
			params.putString("picture", fbImage);
			params.putString("message", "Is going to this movie");

			Log.d("Myfunc", "Params Created");

			WebDialog feedDialog =  (
					new WebDialog.FeedDialogBuilder(FilmDetail.this,
							Session.getActiveSession(),
							params))
							.setOnCompleteListener(new OnCompleteListener() {

								public void onComplete(Bundle values,
										FacebookException error) {
									if (error == null) {
										// When the story is posted, echo the success
										// and the post Id.
										final String postId = values.getString("post_id");
										if (postId != null) {
											Toast.makeText(FilmDetail.this,"Successfully posted to your wall." ,Toast.LENGTH_SHORT).show();
											Log.d("Myfunc", "Post Id= " + postId);
										} else {
											// User clicked the Cancel button
											Toast.makeText(FilmDetail.this,"Share cancelled",Toast.LENGTH_SHORT).show();
											Log.d("Myfunc", "Post Cancelled");
										}
									} else if (error instanceof FacebookOperationCanceledException) {
										// User clicked the "x" button
										Toast.makeText(FilmDetail.this, "Share cancelled",Toast.LENGTH_SHORT).show();
										Log.d("Myfunc", "User Closed the Dialog");
									} else {
										// Generic, ex: network error
										Toast.makeText(FilmDetail.this,"Share failed.",Toast.LENGTH_SHORT).show();
									}
								}

							}).build();
			feedDialog.show();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("MyFunc", e.toString());
		}

	}
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.d("Myfunc", "Logged in...");
		} else if (state.isClosed()) {
			Log.d("MyFunc", "Logged out...");
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("MyFunc", "onActivityResult");
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	public void sendEmail(){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"testreciever@example.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_TEXT   , "body of email goes here");
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}

	}
	public void openWebPage(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("www.google.com"));
		startActivity(intent);
	}

}
