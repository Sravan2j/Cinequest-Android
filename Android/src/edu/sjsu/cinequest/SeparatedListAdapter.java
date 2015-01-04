package edu.sjsu.cinequest;

// http://blogingtutorials.blogspot.com/2010/09/separating-lists-with-headers-in.html
// http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/
// just added the second link that has the same content as the first link, but it shows the image of separating lists with headers.
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/*
 * A separating list adapter with headers that can be used in listview.
 */
public class SeparatedListAdapter extends BaseAdapter
{
	public TreeMap<String, Adapter> sections = new TreeMap<String,Adapter>(new Comparator<String>() {
		public int compare(String a, String b) {
			String[] temp1 = a.split(" ");
			String[] temp2 = b.split(" ");
			if(temp1.length > 1 && temp2.length > 1 && temp1[2].matches("\\d+(\\.\\d+)?") && temp2[2].matches("\\d+(\\.\\d+)?"))
			{
				if(Integer.parseInt(temp1[2]) > Integer.parseInt(temp2[2]))
				{ return 1; }
				else if(Integer.parseInt(temp1[2]) == Integer.parseInt(temp2[2])) 
				{ return 0; }
				else { return -1; }
			}
			else{
				return a.compareTo(b);
			}
		}
	});
	public Vector<Schedule> list;
	public final static int TYPE_SECTION_HEADER = 0;
	public Context adapterContext;
	public SeparatedListAdapter(Context context) {
		adapterContext = context;
	}

	public void setList(Vector<Schedule> list)
	{
		this.list = list;
	}

	public void addSection(String section, Adapter adapter) {
		this.sections.put(section, adapter);
	}

	/**
	 * Removes a section from sections including headers
	 * */
	public void removeSection(String section)
	{
		if(section.contains(section))
		{
			sections.remove(section);
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
		Object[] temp = sections.keySet().toArray();
		for(Object section : this.sections.keySet()) {  
			Adapter adapter = sections.get(section);  
			int size = adapter.getCount() + 1;

			if(position == 0)
			{
				View v = View.inflate(adapterContext, R.layout.list_header, null);
				TextView t = (TextView) v.findViewById(R.id.list_header_title);
				t.setText((String)temp[sectionnum]);
				return v;
			}
			else if(position < size) return adapter.getView(position - 1, convertView, parent);  

			// otherwise jump into next section  
			position -= size;
			sectionnum++;
		}
		return null;
	}
}
