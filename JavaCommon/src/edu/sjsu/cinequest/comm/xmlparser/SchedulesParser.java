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
import edu.sjsu.cinequest.comm.CharUtils;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/*
 * Parses a list of schedules.
 * @author: Prakash Shiwakoti
 * @author Cay Horstmann
 */
public class SchedulesParser extends BasicHandler
{

	private Vector result;

    /**
     * Parses a list of schedule items
     * @param url the URL to parse
     * @param callback the callback for progress monitoring     
     * @return the list of Schedule items
     * @throws IOException 
     * @throws SAXException 
     */
    public static Vector parseSchedule(String url, Callback callback) throws SAXException, IOException
    {
        SchedulesParser handler = new SchedulesParser();
        handler.result = new Vector();
        Platform.getInstance().parse(url, handler, callback);   
        return handler.result;
    }
    
    public void setResult(Vector result)
    {
        this.result = result;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
	    super.startElement(uri, localName, qName, attributes);
		if (lastTagName().equals("schedule") && result != null)
		{
		    Schedule schedule = new Schedule();
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
			schedule.setTitle(CharUtils.fixWin1252AndEntities(attributes.getValue("title")));
			result.addElement(schedule);
		}
	}
}
