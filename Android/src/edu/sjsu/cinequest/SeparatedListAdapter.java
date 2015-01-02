package edu.sjsu.cinequest;

// http://blogingtutorials.blogspot.com/2010/09/separating-lists-with-headers-in.html
// http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/
// just added the second link that has the same content as the first link, but it shows the image of separating lists with headers.

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/*
 * A separating list adapter with headers that can be used in listview.
 */
public class SeparatedListAdapter extends BaseAdapter {
	public Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();  
	public ArrayAdapter<String> headers;
	public Vector<Schedule> list;
	public final static int TYPE_SECTION_HEADER = 0;
	public boolean SortKeys;

	public SeparatedListAdapter(Context context) {
		SortKeys = false;
		headers = new ArrayAdapter<String>(context, R.layout.list_header);
	}

	public void setList(Vector<Schedule> list)
	{
		this.list = list;
	}

	public void addSection(String section, Adapter adapter) {  
		addSectionName(section);
		this.sections.put(section, adapter);
	}

	/**
	 * Adds a section name to headers
	 **/
	public void addSectionName(String section)
	{
		if(!section.equals("") && headers.getPosition(section) == -1)
			headers.add(section);
	}
	
	/**
	 * Removes a section from sections including headers
	 * */
	public void removeSection(String section)
	{
		if(section.contains(section))
		{
			sections.remove(section);
			headers.remove(section); //remove section
		}
	}
	
	/*
	 * Get the number of items in the data set represented by this adapter.
	 * @return the number of all sections in listview.
	 */
	public int getCount() {
		// total together all sections, plus one for each section header  
		int total = 0;  
		for(Adapter adapter : this.sections.values())  
			total += adapter.getCount() + 1;  
		return total;  
	}

	/*
	 * Get the data item associated with the specified position in the data set
	 */
	public Object getItem(int position) {
		//If the list is used to store the sections, return from it
		if(list != null){
			Schedule schedule = list.get(position-1);
			return schedule;
		}
		//else return from the sections Map
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);  

			int size = adapter.getCount() + 1;  

			// check if position inside this section  
			if(position  == 0) return section;
			else if(position < size){ return adapter.getItem(position - 1); }  

			// otherwise jump into next section  
			position -= size;  
		}
		return null;
	}

	/*
	 * Get the number of types of views that will be created by getView().
	 */
	public int getViewTypeCount() {  
		// assume that headers count as one, then total all sections  
		int total = 1;  
		for(Adapter adapter : this.sections.values())  
			total += adapter.getViewTypeCount();  
		return total;  
	}

	/*
	 * Get the type of View that will be created by getView().
	 */
	public int getItemViewType(int position)
	{
		int type = 1;
		for(Object section : this.sections.keySet()) {  
			Adapter adapter = sections.get(section);  
			int size = adapter.getCount() + 1;  

			// check if position inside this section  
			if(position == 0) return TYPE_SECTION_HEADER;  
			else if(position < size) return type + adapter.getItemViewType(position - 1);  

			// otherwise jump into next section  
			position -= size;  
			type += adapter.getViewTypeCount();  
		}
		return -1;  
	}

	/*
	 * Indicates whether all the items in this adapter are selectable.
	 */
	public boolean areAllItemsSelectable() {  
		return false;  
	}  

	/*
	 * Indicates whether the item at the specified position is enabled.
	 */
	public boolean isEnabled(int position) {  
		return (getItemViewType(position) != TYPE_SECTION_HEADER);  
	}  

	/*
	 * Get the row id associated with the specified position in the list.
	 */
	public long getItemId(int position) {
		return position; 
	}

	/*
	 * Get a view that displays the data at the specified position in the data set.
	 */
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent){
		int sectionnum = 0;
		if(SortKeys)
		{
			Comparator cmp = new Comparator<String>() { //This sorts the headers in ascending order based on day
				@Override
				public int compare(String lhs, String rhs) {
					String[] temp1 = lhs.split(" ");
					String[] temp2 = rhs.split(" ");
					if(Integer.parseInt(temp1[2]) > Integer.parseInt(temp2[2]))
					{ return 1; }
					else if(Integer.parseInt(temp1[2]) == Integer.parseInt(temp2[2])) 
					{ return 0; }
					else { return -1; }
				}
			};
			Comparator cmp2 = new Comparator<Map.Entry<String, Adapter>>(){
				@Override
				public int compare(Entry<String, Adapter> lhs,
						Entry<String, Adapter> rhs) {
					String[] temp1 = lhs.getKey().split(" ");
					String[] temp2 = rhs.getKey().split(" ");
					if(Integer.parseInt(temp1[2]) > Integer.parseInt(temp2[2]))
					{ return 1; }
					else if(Integer.parseInt(temp1[2]) == Integer.parseInt(temp2[2])) 
					{ return 0; }
					else { return -1; }
				}
			};
			Vector<String> sheaders = new Vector<String>();
			for(int i = 0; i < headers.getCount(); i++)
			{
				sheaders.add(headers.getItem(i));
			}
			Collections.sort(sheaders, cmp);
			headers.clear();
			headers.addAll(sheaders);
			Vector<Map.Entry<String, Adapter>> ssections = new Vector<Map.Entry<String, Adapter>>(sections.entrySet());
			sections.clear();
			Collections.sort(ssections, cmp2);
			for(Map.Entry<String, Adapter> sentry: ssections)
			{
				sections.put(sentry.getKey(), sentry.getValue());
			}
		}
		for(Object section : this.sections.keySet()) {  
			Adapter adapter = sections.get(section);  
			int size = adapter.getCount() + 1;

			// check if position inside this section  
			if(position == 0) return headers.getView(sectionnum, convertView, parent);  
			else if(position < size) return adapter.getView(position - 1, convertView, parent);  

			// otherwise jump into next section  
			position -= size;
			sectionnum++;
		}
		return null;
	}
}
