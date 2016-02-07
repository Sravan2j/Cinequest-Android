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
import java.util.List;

/**
 * Represents a Film/Event
 * 
 * @author Rohit Vobbilisetty
 *
 */
public class CommonItem implements java.io.Serializable {
	// Picked from Film
	private int id;
    private String type;
	private String title;
	private String imageURL;
    private String thumbImageURL;
    private String description;
    private String videoURL;
	private String genre;
	private String director;
	private String producer;
	private String writer;
	private String cinematographer;
	private String editor;
	private String cast;
	private String country;
	private String language;
	private String infoLink;
	private List<CommonItem> childItems;
    private List<Schedule> schedules;

    /**
	 * @return id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return videoURL
	 */
	public String getVideoURL()
	{
		return videoURL;
	}

	/**
	 * @param videoURL the URL to set
	 */
	public void setVideoURL(String videoURL)
	{
		if (videoURL != null && videoURL.length() > 0)
			this.videoURL = videoURL;
	}

	/**
	 * @return imageURL
	 */
	public String getImageURL()
	{
		return imageURL;
	}

	/**
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(String imageURL)
	{
		this.imageURL = imageURL;
	}

	public String getThumbImageURL() {
		return thumbImageURL;
	}

	public void setThumbImageURL(String thumbImageURL) {
		this.thumbImageURL = thumbImageURL;
	}

	/**
	 * @return description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}


	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getCinematographer() {
		return cinematographer;
	}
	
	public void setCinematographer(String cinematographer) {
		this.cinematographer = cinematographer;
	}
	
	public String getEditor() {
		return editor;
	}
	
	public void setEditor(String editor) {
		this.editor = editor;
	}
	
	public String getCast() {
		return cast;
	}
	
	public void setCast(String cast) {
		this.cast = cast;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getInfoLink()
	{
		return infoLink;
	}
	public void setInfoLink(String infoLink) {
		this.infoLink = infoLink;
	}

    public List<CommonItem> getChildItems() {
        if (childItems == null) childItems = new ArrayList<CommonItem>();
        return childItems;
    }

	public List<Schedule> getSchedules()
	{
	    if (schedules == null) schedules = new ArrayList<Schedule>();
		return schedules;
	}

    public String toString()
    {
        return getClass().getName() + "[id=" + id + "title=" + title + ",description=" + description + ",items=" + childItems + "]";
    }
}