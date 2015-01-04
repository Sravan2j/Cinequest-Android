package edu.sjsu.cinequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;
import android.content.Context;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * Custom SeparatedListAdapter class with SectionIndexer.
 * Example from: http://www.anddev.org/
 * tutalphabetic_fastscroll_listview_-_similar_to_contacts-t10123.html
 * 
 * This adapter will enable the thumb on the side of the long enough listview
 * for fast scrolling and will also display section keys whenever scrolling enters 
 * a new section.
 * 
 * PreRequisite: The headers should be sorted already, otherwise the listview will
 * bounce back and forth when sections change.
 * 
 * Also, since this class sorts the section keys, don't use things like dates as keys.
 * Otherwise date 1 (from march) will fall before 25 (from february) just because 1
 * comes before 25 in sorting. Also, Apr 1 will fall before Mar 1 because of sorting 
 * order.
 * 
 * 
 * WARNING: This class alters the Id of the ListView it uses. So don't ever
 * set your own id by calling listview.setId() and don't rely on the getId() 
 * method of your listview after using this class as adapter.
 * 
 * 
 * @author Prabhjeet Ghuman
 *
 */
public class SeparatedListIndexedAdapter extends SeparatedListAdapter 
										implements SectionIndexer {
	
    private HashMap<String, Integer> alphaIndexer;
    private String[] sectionKeys;
    ArrayList<String> keyList = new ArrayList<String>();
    private int currPosition = 0;
    private boolean currWidthFillParent = true;
    private ListView listview;
    private boolean SortKeysFirst = false;
    //SeparatedListAdapter now sorts by default
	public SeparatedListIndexedAdapter(Context context) {
		super(context);
		alphaIndexer = new HashMap<String, Integer>();
	}
	
	public void setAsAdapterFor(ListView listview){
		this.listview = listview;
		
		//retrieve the id of the listview. 
		int booleanfromid = this.listview.getId();
		if(booleanfromid == 0)
			currWidthFillParent = false;
		else if(booleanfromid == 1)
			currWidthFillParent = true;
		
		this.listview.setFastScrollEnabled(false);
		this.listview.setAdapter(this);
		this.listview.setFastScrollEnabled(true);
	}
	
	public void addSection(String section, String sectionKey, Adapter adapter) {
		super.addSection(section, adapter);
		keyList.add(sectionKey);
		alphaIndexer.put(sectionKey, currPosition);
		currPosition += adapter.getCount() + 1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
	
	/**
	 * Verifies, if this sections already exists
	 * */
	public boolean haveSection(String sectionKey)
	{
		return sections.containsKey(sectionKey);
	}
	
	/**
	 * Returns an adapter for section
	 * */
	public Adapter getAdapterForSection(String section)
	{
		return sections.get(section);
	}
	
	/**
	 * Replace an adapter for current Adapter
	 * */
	public void appendAdapter(String section, Adapter adapter)
	{
		if(adapter != null)
		{
			sections.remove(section);
			sections.put(section, adapter);
		}
	}
	/**
	 * This function returns the index of section to display the appropriate header
	 * */
	@Override
	public int getPositionForSection(int position) {
		int sectionnum = 0;
		for(Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;
  
            // check if position inside this section  
            if(position < size){ return sectionnum; }
            position -= size;
            sectionnum++;
		}
		return sectionnum;
	}
	
	@Override
	public Object[] getSections() {
		return null;
	}
}