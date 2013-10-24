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
import java.util.ArrayList;
import java.util.Vector;

import net.rim.device.api.util.Persistable;

/**
 * Festival class represents the complete information of the Festival
 *
 * @author Snigdha Mokkapati
 *
 * @version 0.1
 */

public class Festival implements Persistable {
	private Vector programItems;
	private Vector films;
	private Vector schedules;
	private Vector venueLocations;
	private String lastChanged;
	private Vector events;
	
	public Festival()
	{
		programItems = new Vector();
		films = new Vector();
		schedules = new Vector();
		venueLocations = new Vector();
		events = new Vector();
		lastChanged = "";
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