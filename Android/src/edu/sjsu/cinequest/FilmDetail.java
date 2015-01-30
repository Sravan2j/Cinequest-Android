package edu.sjsu.cinequest;
//credit goes to:http://stackoverflow.com/questions/2077008/android-intent-for-twitter-application

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.content.Intent;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.commonsware.cwac.merge.MergeAdapter;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.HParser;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

public class FilmDetail extends CinequestActivity {
	private static final String TAG = "Twitter Page!";
	public static enum ItemType {FILM, PROGRAM_ITEM, DVD}
	private ListView scheduleList;
	private ListView includeList;
	private String fbTitle;
	private String fbImage;
	private String fbUrl;
	private MergeAdapter myMergeAdapter;
	
	private int includescnt;
	public void onCreate(Bundle savedInstanceState) {    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filmdetail);
		myMergeAdapter = new MergeAdapter();
		scheduleList = (ListView)findViewById(R.id.ScheduleList);

		ListView listView = (ListView) findViewById(R.id.ScheduleList);
		View headerView = getLayoutInflater().inflate(
				R.layout.detail_layout, null);    
		listView.addHeaderView(headerView, null, false);

		View footerView = getLayoutInflater().inflate(
				R.layout.shareoptions, null);
		listView.addFooterView(footerView, null, false);

		Button fbButton = (Button) findViewById(R.id.fbshare);
		fbButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//This function posts to facebook
				postOnFacebook();
			}
		});

		Button gmailButton = (Button) findViewById(R.id.gmail);
		gmailButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				sendEmail();		
			}
		});

		Button infoButton = (Button) findViewById(R.id.moreinfo);
		infoButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				openWebPage();
			}
		});

		Button twitterButton = (Button) findViewById(R.id.twitter);
		twitterButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				checkOnTwitter();
			}
		});
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
		myMergeAdapter.addAdapter(adapter);
	}


	private void showIncludes(final ArrayList<CommonItem> includes)
	{				
		if (includes.size() == 0) {
			return;
		}
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		adapter.addSection("Includes",
				new FilmletListAdapter(this, includes));
		myMergeAdapter.addAdapter(adapter);
		includescnt=adapter.getCount();
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

		showIncludes(in.getCommonItems());
		showSchedules(in.getSchedules());
		scheduleList.setAdapter(myMergeAdapter);
		scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (position>0 && position <=includescnt){
					Object result = scheduleList.getItemAtPosition( position );
					launchFilmDetail(result);				
				}
			}
		});
	}

	//New function to to post a status on facebook
	public void postOnFacebook()
	{
		if(fbUrl == null || fbUrl == "")
			fbUrl = "http://www.cinequest.org/film-festival";
		String message = '"' + fbTitle + '"';
		message += "\nAt the Cinequest film festival";
		String facebookURL = "https://facebook.com/dialog/feed?%20app_id=145634995501895%20&display=popup&caption="+ message +
				"&link="+ fbUrl + "/dialog/feed%2Fdocs%2F%20&redirect_uri=https://www.facebook.com/";
		Intent postToFacebook = new Intent(this,PostOnFacebook.class);
		postToFacebook.putExtra("facebookURL", facebookURL);
		startActivity(postToFacebook);
	}

	public void sendEmail(){
		String email="";
		Intent i = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[] { email });
		i.putExtra(Intent.EXTRA_SUBJECT, "Cinequest Film Festival : " + fbTitle);
		i.putExtra(Intent.EXTRA_TEXT   , "You should check this movie out : " + fbUrl);
		try  {
			startActivity(Intent.createChooser(i, "Email"));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	public void openWebPage(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(fbUrl));
		startActivity(intent);
	}

	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			Log.i(TAG, "UTF-8 should always be supported", e);
			throw new RuntimeException("URLEncoder.encode() failed for " + s);
		}
	}

	public void checkOnTwitter(){
		if(fbUrl == null || fbUrl == "")
			fbUrl = "http://www.cinequest.org/film-festival";

		String message = '"' + fbTitle + '"';
		message += "at the Cinequest film festival";
		message += "\n" + fbUrl + "\n";

		String tweetUrl = 
				String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
						urlEncode(message), urlEncode("https://www.google.com/"));
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
		startActivity(intent);
	}
}
