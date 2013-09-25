package edu.sjsu.cinequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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

	public SeparatedListIndexedAdapter(Context context) {
		super(context);
		alphaIndexer = new HashMap<String, Integer>();
	}
	
	//if you want keys sorted first, use this constructor, and set sortkeys=true
	public SeparatedListIndexedAdapter(Context context, boolean sortkeys) {
		this(context);
		SortKeysFirst = sortkeys;
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
	
	/**
	 * build the sectionKeys array, which will hold the values of keys to 
	 * display on screen
	 */
	public void buildIndex(){
		if(SortKeysFirst == true){
			Set<String> keys = alphaIndexer.keySet(); 
			keyList = new ArrayList<String>();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
	            String key = it.next();
	            keyList.add(key);
			}
	
			Collections.sort(keyList);
		}
		
		//now create the array containing keys which will be returned next
		sectionKeys = new String[keyList.size()]; 
		keyList.toArray(sectionKeys);
		
		//fix key's screen placement bug
		fixScreenKeyPlacement();
	}
	
	/**
	 * Using such SectionIndexer with changing data sections does not refresh the
	 * sections cache and it keeps reusing the first set of sections. In order
	 * to make it recreate sections, we can do listview.setFastScrollEnabled(false)
	 * and then listview.setFastScrollEnabled(true), but this will start creating
	 * the sections keys to appear in top left corner half-hidden. Using the method
	 * below is a hard-wired fix for such issue. Any better and optimal fix is 
	 * yet not known.
	 * The solution is - every time the data set changes, change the listview
	 * width by 1pixel at least. So if it is filling screen, reduce it one pixel
	 * else make it fill screen again. We are using "currWidthFillParent" boolean to 
	 * keep track of screen width.
	 * 
	 * problem:
	 * http://stackoverflow.com/questions/3898749/re-index-refresh-a-sectionindexer
	 * 
	 * solution:
	 * http://groups.google.com/group/android-developers/browse_thread/thread/
	 * 2c24970bf355c556/a47dd42737dd5ce4?show_docid=a47dd42737dd5ce4
	 */
	private void fixScreenKeyPlacement(){
		
		//this method needs to alter listview width, so if listview is null, return
		if(listview == null)
			return;
		
		int newWidth = currWidthFillParent ? 
				LinearLayout.LayoutParams.FILL_PARENT : listview.getWidth() - 1; 
		LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(newWidth, 
		                               LinearLayout.LayoutParams.FILL_PARENT); 
		listview.setLayoutParams( l );
		//toggle our boolean
		currWidthFillParent = currWidthFillParent ? false : true;
		
		//save the current state of currWidthFillParent inside listview
		if(currWidthFillParent)
			listview.setId(1);
		else
			listview.setId(0);
	}

	@Override
	public int getPositionForSection(int section) {
		String letter = sectionKeys[section];			 
        return alphaIndexer.get(letter);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		buildIndex();
		return sectionKeys;
	}
	
}