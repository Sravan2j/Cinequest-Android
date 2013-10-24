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

import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;

/**
 * Parses the operating mode (on or off-season).
 * @author Ian Macauley
 * @author Cay Horstmann
 */
public class SeasonParser extends BasicHandler
{
	private String mode;

	/**
	 * Parses the season mode.
	 * @param url the URL to parse
     * @param callback the callback for progress reporting
	 * @return "home" if on season, "off-season" if off-season
	 * @throws IOException 
	 * @throws SAXException 
	 */
    public static String parse(String url, Callback callback) throws SAXException, IOException
	{
        SeasonParser handler = new SeasonParser();
	    Platform.getInstance().parse(url, handler, callback);
	    return handler.mode;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		super.endElement(uri, localName, qName);
		if(lastTagName().equals("mode")) mode = lastString();
	}
}
