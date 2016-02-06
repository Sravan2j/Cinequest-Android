package edu.sjsu.cinequest;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;

public class HotPicksActivity extends CinequestActivity {

	private enum IndexType {TRENDING, VIDEOS}

	private static List<CommonItem> mTrending_byTitle;
	private static List<CommonItem> mVideos_byTitle;
	private Button trendingButton;
	private Button videosButton;
	private ListView listview;
	private TextView mEmptyListViewMessage;
	private IndexType currentType;


    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hotpicks_activity_layout);
		listview = (ListView) findViewById(R.id.hotpicks_activity_listview);
		mEmptyListViewMessage  = (TextView)this.findViewById(R.id.msg_for_empty_schedule);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
                if (currentType == IndexType.TRENDING) {
                    CommonItem result = mTrending_byTitle.get(position);
                    launchFilmDetail(result);
                }
                else {
                    CommonItem result = mVideos_byTitle.get(position);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result.getVideoURL())));
                }

			}
		});

		trendingButton = (Button) findViewById(R.id.hotpicks_activity_trending_button);
		videosButton = (Button) findViewById(R.id.hotpicks_activity_videos_button);

		trendingButton.setEnabled(true);
		currentType = IndexType.TRENDING;

		trendingButton.setBackgroundResource(R.drawable.enabledfilmsbutton);
		trendingButton.setTextColor(Color.WHITE);
		videosButton.setBackgroundResource(R.drawable.disabledeventsbutton);
		videosButton.setTextColor(Color.RED);

		registerForContextMenu(listview);

		fetchServerData(currentType);
	}

	/**
	 * Gets called when user returns to this tab. Also gets called once after the
	 * onCreate() method too.
	 */

	@Override
	public void onResume(){
		super.onResume();

		if(currentType == IndexType.TRENDING){
			refreshListContents(mTrending_byTitle);
		}else{
            refreshListContents(mVideos_byTitle);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// searchText.setText("");
	};

	private void fetchServerData(IndexType indexType) {
		if (indexType == IndexType.TRENDING) {
			SplashScreenActivity.getQueryManager().getTrending(
					new ProgressMonitorCallback(this) {
						public void invoke(Object result) {
							super.invoke(result);
							mTrending_byTitle = (List<CommonItem>) result;
							refreshListContents(mTrending_byTitle);
						}
					});
		} else {
			SplashScreenActivity.getQueryManager().getVideos(
					new ProgressMonitorCallback(this) {
						public void invoke(Object result) {
							super.invoke(result);
							mVideos_byTitle = (List<CommonItem>) result;
							refreshListContents(mVideos_byTitle);
						}
					});
		}
	}

	private void refreshListContents(List<?> listItems) {
		if (listItems == null)
			return;
		ListAdapter adp = createListWithIcons((List<CommonItem>) listItems);
		listview.setAdapter(adp);
		// if there are no items in the list, hide the listview,
		// and show the emptytextmsg, and vice versa
		if (adp.getCount() == 0) {
			listview.setVisibility(View.GONE);
			mEmptyListViewMessage.setVisibility(View.VISIBLE);
		} else {
			listview.setVisibility(View.VISIBLE);
			mEmptyListViewMessage.setVisibility(View.GONE);
		}
	}

	public void trendingButtonOnClick(View view){
		// searchText.setText("");
		currentType = IndexType.TRENDING;
		trendingButton.setEnabled(false);
		videosButton.setEnabled(true);

		trendingButton.setBackgroundResource(R.drawable.enabledfilmsbutton);
		trendingButton.setTextColor(Color.WHITE);
		videosButton.setBackgroundResource(R.drawable.disabledeventsbutton);
		videosButton.setTextColor(Color.RED);

		fetchServerData(currentType);
	}

	public void videosButtonOnclick(View view){
		// searchText.setText("");
		currentType = IndexType.VIDEOS;
		videosButton.setEnabled(false);
		trendingButton.setEnabled(true);

		videosButton.setBackgroundResource(R.drawable.enabledeventsbutton);
		videosButton.setTextColor(Color.WHITE);
		trendingButton.setBackgroundResource(R.drawable.disabledfilmsbutton);
		trendingButton.setTextColor(Color.RED);

		fetchServerData(currentType);
	}

}
