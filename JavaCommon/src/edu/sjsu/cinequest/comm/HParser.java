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
package edu.sjsu.cinequest.comm;

import java.util.Vector;

/**
 * HParser converts plain strings into RichText. This follows the BlackBerry API but is also usable 
 * in general. The parser computes a list of offsets and a list of attributes. 
 * The offsets define the boundaries of the regions that have a particular font. 
 * The first offset position in the list is the beginning of the field's text (always 0), and the last offset position marks 
 * the end of the field's text (always equal to the field's text length). 
 * Each region has an attribute, a bit set of flags LARGE, BOLD, ITALIC, RED.
 * @author Cay Horstmann
 */
public class HParser
{
    // This is the result string, stripped of tags
    private String resultString;
    // This is the byte array of the attributes for the RichTextField
    // constructor
    private byte[] attributes = null;
    // This is the RichTextField constructor argument of Font offsets
    private int[] offsets = null;
    // Tracks the font by the reference byte
    
    private Vector images;
    
    public static final int RED = 8;
    public static final int LARGE = 4;
    public static final int BOLD = 2;
    public static final int ITALIC = 1;

    /**
     * Parses a string. Call the getter methods afterwards to get the parse
     * result.
     * @param input the string to be formatted to rich text
     */
    public void parse(String input)
    {
    	Vector offs = new Vector();
    	Vector attrs = new Vector();
    	
        images = new Vector();
        byte font = (byte) 0;
    	
    	boolean inTag = false;
    	int start = 0; // The start of the last tag
    	int end = -1; // The end of the last tag
    	StringBuffer result = new StringBuffer();
    	offs.addElement(new Integer(0));
    	for (int i = 0; i < input.length(); i++)
    	{	
    		char ch = input.charAt(i);
    		if (!inTag && ch == '<')
    		{
				start = i;    		
    			inTag = true;
    			
    			// add everything before the tag
    			result.append(input.substring(end + 1, start));  
    			end = i - 1;
    		}
    		else if (inTag && ch == '>') // found a tag end 
			{
    			end = i;
				inTag = false;
    			String tag = input.substring(start + 1, end);

    			if (isBreak(tag))
    			{
    				result.append('\n');
    			}
    			else if (!checkForImage(images, tag))
    			{
					byte newFont = resolveFontTag(font, tag);
					if (newFont != -1 || end == input.length() - 1)
					{
						if (result.length() > 0)
						{
			    			offs.addElement(new Integer(result.length()));
			    			attrs.addElement(new Byte(font));
						}
		    			font = newFont;
					}
    			}
			}    			
    	}
    	if (end < input.length() - 1)
    	{
			result.append(input.substring(end + 1, input.length()));
			offs.addElement(new Integer(result.length()));
			attrs.addElement(new Byte(font));
    	}
		
    	attributes = new byte[attrs.size()];
    	for (int i = 0; i < attrs.size(); i++)
    	{
    		attributes[i] = ((Byte) attrs.elementAt(i)).byteValue();
    	}

    	offsets = new int[offs.size()];
    	for (int i = 0; i < offs.size(); i++)
    	{
    		offsets[i] = ((Integer) offs.elementAt(i)).intValue();
    	}

    	resultString = result.toString();
    }

    /**
     * Returns the attributes array. 
     * @return the attributes array
     */
    public byte[] getAttributes()
    {        
        return attributes;
    }

    /**
     * Returns the offsets array.
     * @return the offset array
     */
    public int[] getOffsets()
    {
        return offsets;
    }

    /**
     * Returns the parsed string without tags.
     * @return the stripped string
     */
    public String getResultString()
    {
        return resultString;
    }

    /**
     * Returns the image URLs found in the input
     * @return a vector of image URL strings
     */
    public Vector getImageURLs()
    {
        return images;
    }

    
    private static final String[] tagStrings =
    { 
    	"b", "/b", "i", "/i", "em", "/em",
		"font color=\"red\"", "/font",
		"h1", "/h1", "h2", "/h2", "h3",
        "/h3", "h4", "/h4" 
    };
   
    /**
     * This takes a string taken from between a greater and less than character
     * this method finds if it is one of the legal tags and returns its number
     * else will return -1, to indicate a illegal, unsupported tag.
     * @param s the contents of a single tag
     * @return the index of the tag found, or -1 if illegal
     */
    private byte resolveFontTag(byte font, String s)
    {
        for (int i = 0; i < tagStrings.length; i++)
        {
            if (s.compareTo(tagStrings[i]) == 0)
            {
                return setFontFromTag(font, i);
            }
        }
        return -1;
    }

    private byte setFontFromTag(byte font, int in)
    {
        switch (in)
        {
        case 0:
            return setFont(font, BOLD, true);
        case 1:
        	return setFont(font, BOLD, false);
        case 2:
        case 4:
        	return setFont(font, ITALIC, true);
        case 3:
        case 5:
        	return setFont(font, ITALIC, false);
        case 6:
        	return setFont(font, RED, true);
        case 7:
        	return setFont(font, RED, false);
        default:
            if (in > 7)
            	return setFont(font, LARGE, in % 2 == 0);
            else
            	return -1;
        }
    }
    
    private byte setFont(byte font, int fontElement, boolean on) 
    {
    	if (on)
    		return (byte) (font | fontElement);
    	else
    		return (byte) (font & ~fontElement);
    }


    /**
     * Scans a string for img tags and deposits the src attributes in a vector
     * @param images the vector to which the src attributes are added
     * @param tagString the string to be scanned
     * @return true if an image has been found
     */
    private static boolean checkForImage(Vector images, String tagString)
    {
        if (!tagString.startsWith("img"))
            return false;
        int i = tagString.indexOf("src");
        if (i == -1)
            return false;
        i = tagString.indexOf("\"", i);
        if (i == -1)
            return false;
        int j = tagString.indexOf("\"", i + 1);
        if (j == -1)
            return false;
        images.addElement(tagString.substring(i + 1, j));
        return true;
    }

    /**
     * Checks for break tags to insert the newline
     * @param test the string of the tag in question
     * @return true if a break tag is located
     */
    private static boolean isBreak(String test)
    {
        return test.equals("br") || test.equals("br/");
    }
}