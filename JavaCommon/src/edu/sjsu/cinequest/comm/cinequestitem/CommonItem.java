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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import android.util.Log;

/**
 * Represents a Film/Event/Forum
 * 
 * @author Rohit Vobbilisetty
 *
 */
public class CommonItem extends CinequestItem {	
	
	private String type;	// Picked from Filmlet
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private ArrayList<CommonItem> commonItems;
	
	// Picked from Film
	private Vector schedules;    
	private String tagline;
	private String genre;
	private String director;
	private String producer;
	private String writer;
	private String cinematographer;
	private String editor;
	private String cast;
	private String country;
	private String language;
	private String filmInfo;	
	private String infoLink;
	
	/**
	 * Constructor initializes class with appropriate values.
	 */
	public CommonItem()
	{
		commonItems = new ArrayList<CommonItem>();
	}

	/**
	 * @return the vector of films
	 */
	public ArrayList<CommonItem> getCommonItems() {
		return commonItems;
	}
	
	/**
	 * @return the tagline
	 */
	public String getTagline() {
		return tagline;
	}
	/**
	 * @param tagline the tagline to set
	 */
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	/**
	 * @return the director
	 */
	public String getDirector() {
		return director;
	}
	/**
	 * @param director the director to set
	 */
	public void setDirector(String director) {
		this.director = director;
	}
	/**
	 * @return the producer
	 */
	public String getProducer() {
		return producer;
	}
	/**
	 * @param producer the producer to set
	 */
	public void setProducer(String producer) {
		this.producer = producer;
	}
	/**
	 * @return the writer
	 */
	public String getWriter() {
		return writer;
	}
	/**
	 * @param writer the writer to set
	 */
	public void setWriter(String writer) {
		this.writer = writer;
	}
	/**
	 * @return the cinematographer
	 */
	public String getCinematographer() {
		return cinematographer;
	}
	
	/**
	 * @param cinematographer the cinematographer to set
	 */
	public void setCinematographer(String cinematographer) {
		this.cinematographer = cinematographer;
	}
	
	/**
	 * @return the editor
	 */
	public String getEditor() {
		return editor;
	}
	
	/**
	 * @param editor the editor to set
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	
	/**
	 * @return the cast
	 */
	public String getCast() {
		return cast;
	}
	
	/**
	 * @param cast the cast to set
	 */
	public void setCast(String cast) {
		this.cast = cast;
	}
	
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * @return filmInfo
	 */
	public String getFilmInfo()
	{
		return filmInfo;
	}
	public String getInfoLink()
	{
		return infoLink;
	}
	public void setInfoLink(String infoLink) {
		this.infoLink = infoLink;
	}
	
	/**
	 * @param filmInfo the filmInfo to set
	 */
	public void setFilmInfo(String filmInfo)
	{
		this.filmInfo = filmInfo;
	}
	
	public Vector getSchedules()
	{
	    if (schedules == null) schedules = new Vector();
		return schedules;
	}
	
	public void setSchedules(Vector schedules)
    {
        this.schedules = schedules;
    }
	
    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation 
     * of this object.
     */
    public String toString()
    {
        return super.toString() + "[items=" + commonItems + "]";
    }
    
    /**
     * Returns the startTime of the earliest Schedule for a given date
     * 
     * @param date The given date
     * @return The earliest time
     */
    public String getEarliestTime( String date ) {
    	
    	String earliestTime = null;
    	
    	Iterator it = schedules.iterator();
    	
    	while(it.hasNext()) {
    		
    		Schedule schedule = (Schedule)it.next();
    		
    		String startTime = schedule.getStartTime().split("T")[1];
    		
    		if(earliestTime == null) {
    			earliestTime = startTime;
    		} else if(earliestTime.compareTo(startTime) > 0) {
    			earliestTime = startTime;
    		}
    		
    	}
    	
    	return earliestTime;  	
    }
    
    /**
     *  Returns the list of StartTimes for the given Date.
     * 
     * @param date The date on which this item has a schedule
     * @return The associated list of StartTimes
     */
    public SortedSet<String> getStartTimes(String date) {
    	
    	SortedSet<String> startTimes = new TreeSet<String>();
    	
    	Iterator it = schedules.iterator();
    	
    	while(it.hasNext()) {
    	
    		Schedule schedule = (Schedule) it.next();
    		
    		String[] values = schedule.getStartTime().split("T");
    		String startDate = values[0];
    		
    		// Consider the StartTime only if their Dates match.
    		if(date.equals(startDate)) {
    		
    			String startTime = values[1];	
    			startTimes.add(startTime);
    		}
    		
    	}
    	
    	return startTimes;
    }
}