package edu.sjsu.cinequest;

import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**  
 * The super class for all the tabs
 * @author Prabhjeet Ghuman
 * A tabbed activity shows a list of items (films or schedules) and
 * shows a detail view when an item is selected. 
 */
public abstract class CinequestTabActivity extends CinequestActivity{
	private ListView listview;
	private TextView mEmptyListViewMessage;	
	
	//menu options unique id
	private static final int MOREINFO_CONTEXTMENU_ID = Menu.FIRST + 10;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinequest_tab_activity_layout);
        listview = (ListView) findViewById(R.id.cinequest_tabactivity_listview);
        mEmptyListViewMessage  = (TextView)this.findViewById(R.id.msg_for_empty_schedyle);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Object result = getListview().getItemAtPosition( position );
				launchFilmDetail(result);				
			}
		});
		registerForContextMenu(listview);

        fetchServerData();        
	}
	
	/**
	 * Run the query on server through QueryManager and then call
	 * showHeaderSeparatedFilms(List) method from the Callback's invoke
	 */
	protected abstract void fetchServerData();
	
	/**
     * Display the films to the user with either date or alphabet initial 
     * being separator-header.
     */
     protected abstract void refreshListContents(List<?> listItems);
     
     /**
      * Set a adaper for the current listview. Call this method at the end of
      * showHeaderSeparatedFilms(List) method after adaper has been populated 
      * with items.
      * @param adp adapter for the listview
      */
     protected final void setListViewAdapter(ListAdapter adp){
    	 //set correct list adapter
    	 if(adp instanceof SeparatedListIndexedAdapter){
    		 ((SeparatedListIndexedAdapter)adp).setAsAdapterFor(listview);
    	 } else {
    		 listview.setAdapter(adp);
    	 }
    	 
    	 //if there are no items in the list, hide the listview, 
    	 //and show the emptytextmsg, and vice versa 
    	 if(adp.getCount() == 0){
    		 listview.setVisibility(View.GONE);
       		 mEmptyListViewMessage.setVisibility(View.VISIBLE);
    	 }else{
    		 listview.setVisibility(View.VISIBLE);
    	  	 mEmptyListViewMessage.setVisibility(View.GONE);
    	 }
     }
     
     /**
      * Sets the message to show to user when listview is empty
      * @param message String
      */
     protected final void setEmptyListviewMessage(String message){
    	 mEmptyListViewMessage.setText(message);
     }
     
     /**
      * Sets the message to show to user when listview is empty
      * @param resourceId Integer
      */
     protected final void setEmptyListviewMessage(int resourceId){
    	 this.setEmptyListviewMessage( this.getString(resourceId) );
     }
		
	

	/**
	 * @return the ListView for this activity
	 */
	protected ListView getListview() {
		return listview;
	}

    
    /**
     * Called when creating the context menu (for our list items)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.setHeaderTitle("Choose");
      menu.add(0, MOREINFO_CONTEXTMENU_ID, 0, "More Info");      
    }
    
    /**
     * Called when an item in context menu is selected
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      
      
      switch (item.getItemId()) {
      	  case MOREINFO_CONTEXTMENU_ID:
      		  Object result = listview.getItemAtPosition(info.position);
	    	  launchFilmDetail(result);
	    	  return true;	      
      
      default:
        return super.onContextItemSelected(item);
      }
    }    
}
