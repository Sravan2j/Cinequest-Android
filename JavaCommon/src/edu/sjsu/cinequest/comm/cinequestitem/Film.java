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
import java.util.Vector;
/**
 * Film is the class that represents a Cinequest film.
 *
 * @author Kevin Ross (cs160_109)
 *
 * @version 0.1
 */

public class Film extends Filmlet
{	
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
}