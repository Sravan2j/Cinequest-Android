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
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Venue;

/**
 * 
 * @author Prakash Shiwakoti
 * @author Cay Horstmann 
 */
public class VenuesParser extends BasicHandler
{
	//private VenueLocation venueLocation = new VenueLocation();
	private Venue venue;
	private Map<String, Venue> venues = new HashMap<String, Venue>();

	/**
     * Parses a list of venue locations
     * @param url the URL to parse
     * @param callback the callback for progress reporting
     * @return a map of venue names to venues
     * @throws IOException 
     * @throws SAXException  
	 */
	public static Map<String, Venue> parse(String url, Callback callback) throws SAXException, IOException
	{	
		Log.e("VenueParser.java", url);
		
	    VenuesParser handler = new VenuesParser();
		Platform.getInstance().parse(url, handler, callback);
		return handler.venues;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
        super.startElement(uri, localName, qName, attributes);
        
		if (lastTagName().equals("Venue")) {
		    venue = new Venue();
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException
	{		
		super.endElement(uri, localName, qName);
		
		if (lastTagName().equals("Venue")) {
			
			if( venue != null ) {
				venues.put(venue.id, venue);
			}
			
		} else if (lastTagName().equals("ID")) {
			
			venue.id = lastString();
		}
		else if (lastTagName().equals("Name"))
		{
			venue.name = lastString();
		}
		else if (lastTagName().equals("ShortName"))
		{
			venue.shortName = lastString();
		}
        else if (lastTagName().equals("location"))
        {
        	venue.location = lastString();
        }
        else if (lastTagName().equals("Address"))
        {
        	venue.address = lastString();
        }
	}
}