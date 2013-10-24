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

import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;

public class DatesParser extends BasicHandler
{
	private static String first;
	private static String last;
	
   public static String[] parse(String url, Callback callback) throws SAXException, IOException
	{
    	 DatesParser handler = new DatesParser();
	    Platform.getInstance().parse(url, handler, callback);
	    Vector fdates = new Vector();
	    
	    for (String next = first; next.compareTo(last) <= 0; next = nextDate(next)) 
	       fdates.addElement(next);
	    
	    String[] dates = new String[fdates.size()];
	    fdates.copyInto(dates);
	    return dates;
	}
   
   private static String nextDate(String date)
   {
      int y = Integer.parseInt(date.substring(0, 4));
      int m = Integer.parseInt(date.substring(5, 7));
      int d = Integer.parseInt(date.substring(8, 10));
      d++;
      boolean leap = y % 4 == 0 && y % 100 != 0 || y % 400 == 0;
      if (m == 2 && (d > 28 && !leap || d > 29) || (m == 4 || m == 6 || m == 9 || m == 11) && d > 30 || d > 31)
      {
         d = 1;
         m++;
         if (m == 12)
         {
            m = 1;
            y++;
         }
      }
      return "" + y + (m < 10 ? "-0" : "-") + m +  (d < 10 ? "-0" : "-") + d;  
   }

	/**
	 * Add - if necesessary
	 * @param date yyyy-MM-dd or yyyyMMdd
	 * @return yyyy-MM-dd
	 */
	private static String fixDateString(String date)
	{
	   if (date.indexOf('-') >= 0) return date;
	   return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		super.endElement(uri, localName, qName);
		if(lastTagName().equals("first")) 
		   first = fixDateString(lastString());
		else if(lastTagName().equals("last")) 
		   last = fixDateString(lastString());
	}
}
