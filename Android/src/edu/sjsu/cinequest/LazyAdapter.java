package edu.sjsu.cinequest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.News;
import edu.sjsu.cinequest.imageutils.ImageLoader;


public class LazyAdapter extends BaseAdapter {
    private String[] imageURLs;
    private String[] itemStrings;

    private static LayoutInflater inflater=null;
    // public ImageLoader imageLoader;
    
    public LazyAdapter(Activity activity, String[] imageURLs, String[] itemStrings) {
        this.imageURLs = imageURLs;
        this.itemStrings = itemStrings;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    /**
     * @return the length of the String array imageURLs
     */
    public int getCount() {
        return imageURLs.length;
    }

    /**
     * @return the position of a particular element
     */
    public Object getItem(int position) {
        return position;
    }

    /**
     * @return the associated Item ID of a particular element
     */
    public long getItemId(int position) {
        return position;
    }
    
    /**
     * @return a converts a View into TextView and an ImageView a specified location
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        //if the passed convertView doesn't exist, this will create a new instance
        if(convertView==null)
            vi = inflater.inflate(R.layout.row_listview_item, null);

        //Generate the correct Views for the activity
        TextView text=(TextView)vi.findViewById(R.id.text);;
        final ImageView image=(ImageView)vi.findViewById(R.id.image);
        text.setText(itemStrings[position]);
        //loads an image from the requested location

        // TODO: Either go back to using ImageLoader and eliminate ImageManager, or the other way around

        // imageLoader.displayImage(imageURLs[position], image);
        SplashScreenActivity.getImageManager().getImage(imageURLs[position], new Callback() {
            @Override
            public void invoke(Object result) {
                Bitmap bmp = (Bitmap) result;
                image.setImageBitmap(bmp);
            }

            @Override
            public void starting() {
            }

            @Override
            public void failure(Throwable t) {
                Platform.getInstance().log(t);
            }
        }, null, true);
        return vi;
    }
}