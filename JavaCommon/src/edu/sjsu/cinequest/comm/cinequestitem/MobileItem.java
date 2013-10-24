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

package edu.sjsu.cinequest.comm.cinequestitem;

/**
 * An item that is not described in the Cinequest database but in a separate XML file. 
 * @author Cay Horstmann
 */

public class MobileItem extends CinequestItem
{
    public void setLinkType(String linkType)
    {
        this.linkType = linkType;
    }
    
    public String getLinkType()
    {
        return linkType;
    }
    
    public void setLinkId(int linkId)
    {
        this.linkId = linkId;
    }
    
    public int getLinkId()
    {
        return linkId;
    }
    
    public void setLinkURL(String linkURL)
    {
        this.linkURL = linkURL;
    }
    
    public String getLinkURL()
    {
        return linkURL;
    }
    
    private String linkType;    
    private int linkId;    
    private String linkURL;
}
