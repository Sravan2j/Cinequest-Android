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

/*
 * Parses a list of program items.
 * @author Cay Horstmann
 */
public class ProgramItemsParser extends BasicHandler
{

	private Vector result = new Vector();
	private ProgramItem item;

    /**
     * Parses a list of schedule items
     * @param url the URL to parse
     * @param callback the callback for progress monitoring     
     * @return the list of Schedule items
     * @throws IOException 
     * @throws SAXException 
     */
    public static Vector parse(String url, Callback callback) throws SAXException, IOException
    {
        ProgramItemsParser handler = new ProgramItemsParser();
        handler.result = new Vector();
        Platform.getInstance().parse(url, handler, callback);
        return handler.result;
    }
        
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
	    super.startElement(uri, localName, qName, attributes);
		if (lastTagName().equals("program") || lastTagName().equals("program_item")) // TODO: Clarify spec
		{
		    item = new ProgramItem();
		    String id = attributes.getValue("id");
		    if (id != null)
		       item.setId(Integer.parseInt(id));
		    // TODO: Ignoring sort for now
		}
	}

    public void endElement(String uri, String localName, String qName)
      throws SAXException
	{
	    super.endElement(uri, localName, qName);
		if (lastTagName().equals("program") || lastTagName().equals("program_item")) // TODO: Clarify spec
		{
            item.setTitle(lastString());
		    result.addElement(item);
		}
	}
}
