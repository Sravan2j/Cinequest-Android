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

/*
 * Parses a list of genres.
 * @author Kevin Ross
 */
public class GenresParser extends BasicHandler
{
	private Vector result = new Vector();

    /**
     * Parses a list of genre items
     * @param url the URL to parse
     * @param callback the callback for progress reporting
     * @return the list of genre strings
     * @throws IOException 
     * @throws SAXException 
     */
    public static Vector parse(String url, Callback callback) throws SAXException, IOException
    {
        GenresParser handler = new GenresParser();
        Platform.getInstance().parse(url, handler, callback);
        return handler.result;
    }    
	
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
        super.startElement(uri, localName, qName, attributes);
		if (lastTagName().equals("genre"))
		{
			result.addElement(attributes.getValue("name") );
		}
	}
}