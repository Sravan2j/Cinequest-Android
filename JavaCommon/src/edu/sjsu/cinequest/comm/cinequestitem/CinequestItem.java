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


/*
 * Represents a Cinequest entity.
 * 
 * 
 * @author Kevin Ross
 * @author Aaditya Bhatia
 */

public abstract class CinequestItem implements Serializable
{
	private int id;
	private String title, imageURL, thumbImageURL, description, infoLink, videoURL;

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
	
	public String getInfoLink()
	{
		return infoLink;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public void setInfoLink(String infoLink)
	{
		this.infoLink = infoLink;
	}
	
	public String toString()
	{
		return getClass().getName() + "[id=" + id + "title=" + title + ",description=" + description + "]";
	}
}
