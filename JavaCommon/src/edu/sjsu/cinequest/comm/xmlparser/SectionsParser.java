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
import edu.sjsu.cinequest.comm.cinequestitem.Section;
import edu.sjsu.cinequest.comm.cinequestitem.MobileItem;

/**
 * Parses a sequence of sections containing informational items
 * @author Cay Horstmann
 */
public class SectionsParser extends BasicHandler
{
    private MobileItem item;
    private Vector result = new Vector();
    private Section section = new Section();

    /**
     * Parses a sequence of sections
     * @param url the URL to parse
     * @param callback the callback for progress reporting
     * @return a vector of sections
     * @throws IOException
     * @throws SAXException
     */
    public static Vector parse(String url, Callback callback)
            throws SAXException, IOException
    {
        SectionsParser handler = new SectionsParser();
        Platform.getInstance().parse(url, handler, callback);
        return handler.result;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, qName, attributes);
        if (lastTagName().equals("section"))
        {
            section = new Section();
            section.setTitle(CharUtils.fixWin1252AndEntities(attributes.getValue("name")));
            result.addElement(section);
        }
        else if (lastTagName().equals("item"))
        {
            item = new MobileItem();
            section.addItem(item);
        }
        else if (lastTagName().equals("link"))
        {
            item.setLinkType(attributes.getValue("type"));
            String id = attributes.getValue("id");
            if (id != null)
                item.setLinkId(Integer.parseInt(id));
        }
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
    	super.endElement(uri, localName, qName);
        if (lastTagName().equals("title"))
        {
            item.setTitle(lastString());
        }
        else if (lastTagName().equals("imageURL"))
        {
            item.setImageURL(lastString());
        }
        else if (lastTagName().equals("url"))
        {
            item.setLinkURL(lastString());
        }
        else if (lastTagName().equals("description"))
        {
            item.setDescription(lastString());
        }
    }
}
