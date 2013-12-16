/*
    Copyright 2008 San Jose State University
    
    This file is part of the Blackberry Cinequest client.

    The Blackberry Cinequest client is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Blackberry Cinequest client is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Blackberry Cinequest client.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sjsu.cinequest.comm.cinequestitem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import android.util.Log;

/**
 * Festival class represents the complete information of the Festival
 *
 * @author Snigdha Mokkapati
 * @author Rohit Vobbilisetty
 *
 * @version 1.0
 */

public class Festival implements Serializable {

	private Vector schedules;
	private Vector venueLocations;
	private String lastChanged;
	
	private Vector commonItems;
	
	private Vector films;
	private Vector events;
	private Vector forums;

	private SortedSet<String> filmDates;
	private SortedSet<String> eventDates;
	private SortedSet<String> forumDates;
	
	private HashMap<String, List<CommonItem>> filmsByDateMap;
	private HashMap<String, List<CommonItem>> eventsByDateMap;
	private HashMap<String, List<CommonItem>> forumsByDateMap;
	
	public HashMap<String, List<CommonItem>> getFilmsByDateMap() {
		return filmsByDateMap;
	}
	
	public HashMap<String, List<CommonItem>> getEventsByDateMap() {
		return eventsByDateMap;
	}

	public HashMap<String, List<CommonItem>> getForumsByDateMap() {
		return forumsByDateMap;
	}

	private Map<Integer, CommonItem> commonItemsMap;
	
	public Map<Integer, CommonItem> getCommonItemsMap() {
		return commonItemsMap;
	}

	public Vector getCommonItems() {
		return commonItems;
	}
	
	public SortedSet<String> getFilmDates() {
		
		return filmDates;
	}

	public SortedSet<String> getEventDates() {
		return eventDates;
	}

	public SortedSet<String> getForumDates() {
		return forumDates;
	}
	
	public Vector getFilms() {
		return films;
	}

	public Vector getEvents() {
		return events;
	}

	public Vector getForums() {
		return forums;
	}
	
	public Festival()
	{
		schedules = new Vector();
		venueLocations = new Vector();
		lastChanged = "";
		
		commonItems = new Vector();
		commonItemsMap = new HashMap<Integer, CommonItem>();
		films = new Vector();
		events = new Vector();
		forums = new Vector();
		
		filmDates = new TreeSet<String>();
		eventDates = new TreeSet<String>();
		forumDates = new TreeSet<String>();
		
		filmsByDateMap = new HashMap<String, List<CommonItem>>();
		eventsByDateMap = new HashMap<String, List<CommonItem>>();
		forumsByDateMap = new HashMap<String, List<CommonItem>>();
	}
	
	public boolean isEmpty() { return schedules.size() == 0; }
	

	/**
	 * @return vector of Schedules
	 */
	public Vector getSchedules() {
		return schedules;
	}

	/**
	 * @return vector of VenueLocations
	 */
	public Vector getVenueLocations() {
		return venueLocations;
	}
	
	/**
	 * @return lastChanged timestamp
	 */
	public String getLastChanged() {
		return lastChanged;
	}
	
	public Vector getFilmsByDate(String date) {
		return new Vector(getCommonItemsForDate("Film", date));
	}
	
	public Vector getEventsByDate(String date) {
		return new Vector(getCommonItemsForDate("Event", date));
	}
	
	public Vector getForumsByDate(String date) {
		return new Vector(getCommonItemsForDate("Forum", date));
	}
	
	
	/**
	 * This method returns the list of CommonItems associated with the given date. The resulting list items are sorted based on the time.
	 * 
	 * @param type Film/Event/Forum
	 * @param date The given date
	 * @return List of CommonItems
	 */
	private List<CommonItem> getCommonItemsForDate(String type, String date) {
		
		List<CommonItem> itemsByDate = null;
		
		if(type.equals("Film")) {
			
			itemsByDate = filmsByDateMap.get(date);
			
		} else if(type.equals("Event")) {
			
			itemsByDate = eventsByDateMap.get(date);
			
			
		} else if(type.equals("Forum")) {
			itemsByDate = forumsByDateMap.get(date);
					
		}
		
		if(itemsByDate != null) {
			Collections.sort(itemsByDate, new CommonItemComparator(date));
		}
		
		return itemsByDate;	
	}
	
	public TreeMap<String, List<CommonItem>> getFilmsByDateGroupedByTime(String date) {
		return getCommonItemsForDateGroupedByTime("Film", date);
	}
	
	public TreeMap<String, List<CommonItem>> getEventsByDateGroupedByTime(String date) {
		return getCommonItemsForDateGroupedByTime("Event", date);
	}
	
	public TreeMap<String, List<CommonItem>> getForumsByDateGroupedByTime(String date) {
		return getCommonItemsForDateGroupedByTime("Forum", date);
	}
	
	/**
	 * This method returns the list of CommonItems associated with the given date. The resulting list items are sorted based on the time.
	 * 
	 * @param type Film/Event/Forum
	 * @param date The given date
	 * @return List of CommonItems
	 */
	private TreeMap<String, List<CommonItem>> getCommonItemsForDateGroupedByTime(String type, String date) {
		
		List<CommonItem> itemsByDate = null;
		
		if(type.equals("Film")) {
			
			itemsByDate = filmsByDateMap.get(date);
			
		} else if(type.equals("Event")) {
			
			itemsByDate = eventsByDateMap.get(date);
			
			
		} else if(type.equals("Forum")) {
			itemsByDate = forumsByDateMap.get(date);
					
		}
		
		/*if(itemsByDate != null) {
			Collections.sort(itemsByDate, new CommonItemComparator(date));
		}*/
		
		// The List of CommonItems will be converted into a HashMap (Key:Time; Value: List of CommonItems)
		
		TreeMap<String, List<CommonItem>> categorizedItems = new TreeMap<String, List<CommonItem>>();
		// Collect the List of StartTimes. This will act as the key to the Map.
		
		
		for(CommonItem item : itemsByDate) {
			SortedSet<String> startTimes = item.getStartTimes(date);
			
			for(String startTime : startTimes) {
				
				List<CommonItem> itemsByTime = null;
				boolean found = true;
				
				if(categorizedItems.containsKey(startTime)) {
					itemsByTime = categorizedItems.get(startTime);
				} else {
					itemsByTime = new ArrayList<CommonItem>();
					found = false;
				}
				
				itemsByTime.add(item);
				
				// Also sort the list based on the Title of the CommonItem
				Collections.sort(itemsByTime, new CommonItemComparator(date));
				
				if(!found) {
					categorizedItems.put(startTime, itemsByTime);
				}			
			}
		}
		
		return categorizedItems;	
	}

	
	public CommonItem getCommonItemUsingId(int id) {
		
		return commonItemsMap.get(id);
	}

	/**
	 * Cleans up after parsing.
	 * @return this cleaned-up Festival
	 */
	/*public Festival cleanup() {
		for (int i = 0; i < programItems.size(); i++) {
			ProgramItem item = (ProgramItem) programItems.elementAt(i);
			ArrayList<Film> films = item.getFilms(); 
			for (int j = 0; j < films.size(); j++) {
				Film film = films.get(j);
				Film replacement = getFilmForId(film.getId());
				if (replacement == null) // TODO: That should never happen
				{
					films.remove(j);
					j--;
				}
				else
					films.set(j, replacement);
			}
		}
		for (int i = 0; i < schedules.size(); i++) {
			Schedule schedule = (Schedule) schedules.elementAt(i);
			ProgramItem item = getProgramItemForId(schedule.getItemId());
			if (item != null) { // TODO: Could it be a mobile item otherwise?
				schedule.setTitle(item.getTitle());
				ArrayList<Film> films = item.getFilms(); 
				for (int j = 0; j < films.size(); j++) {
					Film film = films.get(j);
					film.getSchedules().addElement(schedule);
				}
			}
		}
					
		return this;
	}*/	
	
	/**
	 * This Comparator will first compare the Earliest Time associated with the CommonItem. 
	 * If they are equal, their respective titles will be compared.
	 *
	 */
	class CommonItemComparator implements Comparator<CommonItem> {
		String date = null;
		public CommonItemComparator(String date) {
			this.date = date;
		}
		
	    public int compare(CommonItem item1, CommonItem item2) {
	    	
	    	/*if(item1.getEarliestTime(date).equals(item2.getEarliestTime(date))) {
	    		return item1.getTitle().compareTo(item2.getTitle());
	    	}*/
	    	
	        return item1.getEarliestTime(date).compareTo(item2.getEarliestTime(date));
	    }
	}
}