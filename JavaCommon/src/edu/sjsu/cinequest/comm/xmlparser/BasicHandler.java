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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.sjsu.cinequest.comm.CharUtils;

/**
 * A SAX parser handler with convenience methods for our application
 * @author Cay Horstmann
 */
public class BasicHandler extends DefaultHandler
{
    private boolean fixed;
    private StringBuffer lastStr = new StringBuffer();
    private String tagName;
    
    public BasicHandler()
    {
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
    	if (qName != null && qName.length() > 0)
           tagName = qName;
    	else
    		tagName = localName;
        lastStr.setLength(0);
        fixed = false;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
    	if (qName != null && qName.length() > 0)
           tagName = qName;
    	else
     	   tagName = localName;
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException
    {       
        lastStr.append(ch, start, length);
    }
    
    /**
     * Gets the last encountered tag name. Works around an Android bug
     * where qName is null (http://code.google.com/p/android/issues/detail?id=990)
     * @return the name of the last encountered tag
     */
    public String lastTagName() 
    {
    	return tagName;
    }
    
    /**
     * Returns the characters of the last element, with spaces trimmed, Windows 1252 characters converted to Unicode, and entities replaced.
     * @return the cleaned-up string
     */
    public String lastString()
    {
    	if (!fixed)
    	{
	        CharUtils.trim(lastStr);
	        CharUtils.fixWin1252(lastStr);
	        CharUtils.replaceEntities(lastStr);
	        fixed = true;
    	}
        
        return lastStr.toString();
    }
}
