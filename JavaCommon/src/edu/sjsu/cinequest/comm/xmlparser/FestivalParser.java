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

package edu.sjsu.cinequest.comm.xmlparser;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Festival;
import edu.sjsu.cinequest.comm.cinequestitem.Film;
import edu.sjsu.cinequest.comm.cinequestitem.ProgramItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.VenueLocation;

/**
 * Parses the complete information of the Festival
 *
 * @author Snigdha Mokkapati
 *
 * @version 0.1
 */
public class FestivalParser extends BasicHandler {
	
	private Festival festival;
	private String currentBlock = ""; //variable to keep track of which block of xml the parser is currently working on 
	private ProgramItem programItem;
	private Film film;
	private VenueLocation venueLocation;
	
	/**
	 * Parses the complete Festival information
	 * @param url
	 * @param callback
	 * @return parsed festival
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Festival parseFestival(String url, Callback callback) throws SAXException, IOException {
		Platform.getInstance().log("Festival.getFestival: start");
		FestivalParser handler = new FestivalParser();
		handler.setFestival(new Festival());
		Platform.getInstance().parse(url, handler, callback);
		Platform.getInstance().log("Festival.getFestival: parsed");
		Festival result = handler.getFestival().cleanup();
		Platform.getInstance().log("Festival.getFestival: cleaned up");
		return result;
	}

	public Festival getFestival() {
		return festival;
	}

	public void setFestival(Festival festival) {
		this.festival = festival;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);		
		if (lastTagName().equals("program_items")) {
			currentBlock = "program_items";
		} else if (lastTagName().equals("films")) {
			currentBlock = "films";
		} else if (lastTagName().equals("schedules") && !currentBlock.equals("program_items")) {
			currentBlock = "schedules"; 
		} else if (lastTagName().equals("venue_locations")) {
			currentBlock = "venue_locations";
		} else if (lastTagName().equals("festival")) {
			festival.setLastChanged(attributes.getValue("lastChanged"));
		} else if (lastTagName().equals("program_item")) {
			programItem = new ProgramItem();
			programItem.setId(Integer.parseInt(attributes.getValue("id")));
			festival.getProgramItems().addElement(programItem);	       		
		} else if (lastTagName().equals("film") && programItem != null) {
			film = new Film();
			String id = attributes.getValue("id");
		    if (id != null)
		    	film.setId(Integer.parseInt(id));
		    if (currentBlock.equals("films")) {
		    	festival.getFilms().addElement(film);
		    } else if (currentBlock.equals("program_items")) {
		    	programItem.getFilms().add(film);
		    }		    
		} else if (lastTagName().equals("schedule") && currentBlock.equals("schedules")) { 
			// TODO: && currentBlock can be removed when schedules are removed from program items
			Schedule schedule = new Schedule();
			festival.getSchedules().addElement(schedule);
			String id = attributes.getValue("id");
		    if (id != null)
		       schedule.setId(Integer.parseInt(id));
		    String programItemId = attributes.getValue("program_item_id");
	        String mobileItemId = attributes.getValue("mobile_item_id");
	        if (programItemId != null)
		    {
	            schedule.setItemId(Integer.parseInt(programItemId));	            
		    }
		    else if (mobileItemId != null) 
		    {
		        schedule.setItemId(Integer.parseInt(mobileItemId));
		        schedule.setMobileItem(true);
		    }
	        schedule.setStartTime(attributes.getValue("start_time"));
			schedule.setEndTime(attributes.getValue("end_time"));
			schedule.setVenue(attributes.getValue("venue"));
		} else if (lastTagName().equals("venue_location")) {
			venueLocation = new VenueLocation();
			festival.getVenueLocations().addElement(venueLocation);
			venueLocation.setVenueAbbreviation(attributes.getValue("venue"));			
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);		
		if (lastTagName().equals("title")) {			
			if (currentBlock.equals("program_items")) {
				programItem.setTitle(lastString());
			} else if (currentBlock.equals("films")) {
				film.setTitle(lastString());
			} 			
		} else if (lastTagName().equals("description")) {			
			if (currentBlock.equals("program_items")) {
				programItem.setDescription(lastString());
			} else if (currentBlock.equals("films")) {
				film.setDescription(lastString());
			} else if (currentBlock.equals("venue_location")) {
				venueLocation.setDescription(lastString());
			}			
		} else if (lastTagName().equals("tagline")) {			
			film.setTagline(lastString());			
		} else if (lastTagName().equals("genre")) {			
			film.setGenre(lastString());			
		} else if (lastTagName().equals("imageURL")) {			
			if (currentBlock.equals("films")) {
				film.setImageURL(lastString());
			} else if (currentBlock.equals("venue_location")) {
				venueLocation.setImageURL(lastString());
			}			
		} else if (lastTagName().equals("director")) {			
			film.setDirector(lastString());			
		} else if (lastTagName().equals("producer")) {			
			film.setProducer(lastString());		
		} else if (lastTagName().equals("writer")) {			
			film.setWriter(lastString());			
		} else if (lastTagName().equals("cinematographer")) {			
			film.setCinematographer(lastString());			
		} else if (lastTagName().equals("editor")) {			
			film.setEditor(lastString());			
		} else if (lastTagName().equals("cast")) {			
			film.setCast(lastString());		
		} else if (lastTagName().equals("country")) {			
			film.setCountry(lastString());			
		} else if (lastTagName().equals("language")) {			
			film.setLanguage(lastString());			
		} else if (lastTagName().equals("film_info")){			
			film.setFilmInfo(lastString());			
		} else if (lastTagName().equals("location")) {			
			venueLocation.setLocation(lastString());
		} else if (lastTagName().equals("directionsURL")){			
			venueLocation.setLocation(lastString());	
		} 
	}
}