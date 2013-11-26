package edu.sjsu.cinequest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import edu.sjsu.cinequest.comm.cinequestitem.News;
import edu.sjsu.cinequest.imageutils.ImageLoader;


public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private String[] data;
    private List<News> news;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, String[] d, List<News> news) {
        activity = a;
        data=d;
        this.news=news;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.row_listview_item, null);

        TextView text=(TextView)vi.findViewById(R.id.text);;
        ImageView image=(ImageView)vi.findViewById(R.id.image);
        text.setText(news.get(position).getName());
        imageLoader.DisplayImage(data[position], image);
        Button moreinfo = (Button) vi.findViewById(R.id.infoicon);			       
		moreinfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					    Uri.parse(news.get(position).getInfoLink()));			
				activity.startActivity(intent);
			}
			
		});

        return vi;
    }
}