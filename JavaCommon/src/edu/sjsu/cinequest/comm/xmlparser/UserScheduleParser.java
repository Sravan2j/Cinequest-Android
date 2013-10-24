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
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.CharUtils;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.UserSchedule;

/*
 * Parses a user schedule.
 * @author Cay Horstmann
 */
public class UserScheduleParser extends BasicHandler
{

   private UserSchedule result;
   private int type;

   /**
    * Parses a list of schedule items
    * 
    * @param url
    *           the URL to parse
    * @param callback
    *           the callback for progress monitoring
    * @return the list of Schedule items
    * @throws IOException
    * @throws SAXException
    */
   public static UserSchedule parseSchedule(String url, Hashtable postData, Callback callback)
         throws SAXException, IOException
   {
      UserScheduleParser handler = new UserScheduleParser();
      handler.result = null; // To indicate auth error
      String doc = Platform.getInstance().parse(url, postData, handler, callback);
      if (handler.result == null) Platform.getInstance().log("UserScheduleParser.parseSchedule: " + doc);
      return handler.result;
   }

   public void startElement(String uri, String localName, String qName,
         Attributes attributes) throws SAXException
   {
      super.startElement(uri, localName, qName, attributes);
      if (lastTagName().equals("userschedule"))
      {
         result = new UserSchedule();
         result.setLastChanged(attributes.getValue("lastChanged"));
         result.setUpdated("true".equals(attributes.getValue("updated")));
      }
      else if (lastTagName().equals("confirmed"))
         type = UserSchedule.CONFIRMED;
      else if (lastTagName().equals("moved"))
         type = UserSchedule.MOVED;
      else if (lastTagName().equals("removed"))
         type = UserSchedule.REMOVED;
      else if (lastTagName().equals("schedule")) 
      {
         Schedule schedule = new Schedule();
         String id = attributes.getValue("id");
         if (id != null)
            schedule.setId(Integer.parseInt(id));

         String programItemId = attributes.getValue("program_item_id");
         if (programItemId != null)
         {
              schedule.setItemId(Integer.parseInt(programItemId));              
         }
         
        schedule.setStartTime(attributes.getValue("start_time"));
        schedule.setEndTime(attributes.getValue("end_time"));
        schedule.setVenue(attributes.getValue("venue"));
        schedule.setTitle(CharUtils.fixWin1252AndEntities(attributes.getValue("title")));         
         
         result.add(schedule, type);
      }
   }
}
