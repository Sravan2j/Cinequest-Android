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
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.ProgramItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/**
 * Parses a ProgramItem
 * @author Prakash Shiwakoti
 * @author Cay Horstmann
 */
public class ProgramItemParser extends FilmParser
{
	private ProgramItem item;
	private Vector schedules;
    
    /**
     * Parses a program item
     * @param url the URL to parse
     * @param callback the callback for progress reporting
     * @return the item that was parsed
     * @throws IOException 
     * @throws SAXException 
     */
	public static ProgramItem parseProgramItem(String url, Callback callback) throws SAXException, IOException
	{
        ProgramItemParser handler = new ProgramItemParser();
        handler.item = new ProgramItem();
        Platform.getInstance().parse(url, handler, callback);
        return handler.item;
	}
	
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
    	super.startElement(uri, localName, qName, attributes);
        if (lastTagName().equals("program_item"))
        {
            item.setId(Integer.parseInt(attributes.getValue("id")));
            schedules = new Vector();
        }
        else if (lastTagName().equals("schedule") && getFilm() == null)
        {
            Schedule schedule = new Schedule();
            schedule.setItemId(item.getId());  
            schedule.setTitle(item.getTitle());
            schedule.setId(Integer.parseInt(attributes.getValue("id")));
            schedule.setStartTime(attributes.getValue("start_time"));
            schedule.setEndTime(attributes.getValue("end_time"));
            schedule.setVenue(attributes.getValue("venue"));
            schedules.addElement(schedule);         
        }
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{        
		super.endElement(uri, localName, qName);
        if (lastTagName().equals("film"))
        {
            item.getFilms().add(getFilm());
            setFilm(null);
        }
        else if (lastTagName().equals("program_item"))
        {
            // attach all schedules to the individual films
            for (int i = 0; i < item.getFilms().size(); i++)
            {
                item.getFilms().get(i).setSchedules(schedules);
            }
        }
        else if (lastTagName().equals("title") && getFilm() == null)
        {
                item.setTitle(lastString());
        }
        else if (lastTagName().equals("description") && getFilm() == null)
        {
            item.setDescription(lastString());
        }
        else if (lastTagName().equals("imageURL") && getFilm() == null)
        {
            item.setImageURL(lastString());
        }
	}
}
