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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Festival class represents the complete information of the Festival
 *
 * @author Snigdha Mokkapati
 *
 * @version 0.1
 */

public class Festival implements Serializable {
	private Vector programItems;
	private Vector films;
	private Vector schedules;
	private Vector venueLocations;
	private String lastChanged;
	private Vector events;
	
	private Vector commonItems;
	
	private Vector c_films;
	private Vector c_events;
	private Vector c_forums;
	

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
	
	public Vector getC_films() {
		return c_films;
	}

	public Vector getC_events() {
		return c_events;
	}

	public Vector getC_forums() {
		return c_forums;
	}
	
	public Festival()
	{
		programItems = new Vector();
		films = new Vector();
		schedules = new Vector();
		venueLocations = new Vector();
		events = new Vector();
		lastChanged = "";
		
		commonItems = new Vector();
		commonItemsMap = new HashMap<Integer, CommonItem>();
		c_films = new Vector();
		c_events = new Vector();
		c_forums = new Vector();
		
		filmDates = new TreeSet<String>();
		eventDates = new TreeSet<String>();
		forumDates = new TreeSet<String>();
		
		filmsByDateMap = new HashMap<String, List<CommonItem>>();
		eventsByDateMap = new HashMap<String, List<CommonItem>>();
		forumsByDateMap = new HashMap<String, List<CommonItem>>();
	}
	
	public boolean isEmpty() { return schedules.size() == 0; }
	
	/**
	 * @return vector of ProgramItems
	 */
	public Vector getProgramItems() {
		return programItems;
	}
	/**
	 * @param programItems the vector of ProgramItems to set
	 */
	public void setProgramItems(Vector programItems) {
		this.programItems = programItems;
	}
	/**
	 * @return vector of Films
	 */
	public Vector getFilms() {
		return films;
	}
	/**
	 * @param films the vector of Films to set
	 */
	public void setFilms(Vector films) {
		this.films = films;
	}
	/**
	 * @return vector of Schedules
	 */
	public Vector getSchedules() {
		return schedules;
	}
	/**
	 * @param schedules the vector of Schedules to set
	 */
	public void setSchedules(Vector schedules) {
		this.schedules = schedules;
	}
	/**
	 * @return vector of VenueLocations
	 */
	public Vector getVenueLocations() {
		return venueLocations;
	}
	/**
	 * @param venueLocations the vector of VenueLocations to set
	 */
	public void setVenueLocations(Vector venueLocations) {
		this.venueLocations = venueLocations;
	}
	/**
	 * @return lastChanged timestamp
	 */
	public String getLastChanged() {
		return lastChanged;
	}
	/**
	 * @param lastChanged the lastUpdated timestamp 
	 */
	public void setLastChanged(String lastChanged) {
		this.lastChanged = lastChanged == null ? "" : lastChanged;
	}
	
	public List<CommonItem> getFilmsByDate(String date) {
		return getCommonItemsForDate("Film", date);
	}
	
	public List<CommonItem> getEventsByDate(String date) {
		return getCommonItemsForDate("Event", date);
	}
	
	public List<CommonItem> getForumsByDate(String date) {
		return getCommonItemsForDate("Forum", date);
	}
	
	public List<CommonItem> getCommonItemsForDate(String type, String date) {
		
		List<CommonItem> itemsByDate = null;
		
		if(type.equals("Film")) {
			
			itemsByDate = filmsByDateMap.get(date);
			
		} else if(type.equals("Event")) {
			
			
			
		} else if(type.equals("Forum")) {
			
					
		}
		
		return itemsByDate;
		
	}
	
	public Vector getSchedulesForDay(String date) {
		Vector result = new Vector();
		for (int i = 0; i < schedules.size(); i++) {
			Schedule schedule = (Schedule) schedules.elementAt(i);
			if (schedule.getStartTime().startsWith(date)) result.addElement(schedule);
		}
		return result;
	}
	
	public Film getFilmForId(int id) {
		for (int i = 0; i < films.size(); i++) {
			Film film = (Film) films.elementAt(i);
			if (film.getId() == id) return film;
		}
		return null;
	}

	public ProgramItem getProgramItemForId(int id) {
		for (int i = 0; i < programItems.size(); i++) {
			ProgramItem item = (ProgramItem) programItems.elementAt(i);
			if (item.getId() == id) return item;
		}
		return null;
	}
	
	public CommonItem getCommonItemUsingId(int id) {
		
		return commonItemsMap.get(id);
	}
	
	public Vector getEvents() {
		return events;
	}
	
	public void setEvents(Vector events) {
		this.events = events;
	}

	/**
	 * Cleans up after parsing.
	 * @return this cleaned-up Festival
	 */
	public Festival cleanup() {
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
	}	
}