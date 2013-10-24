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
 * VenueLocation is the class that represents a Cinequest venue.
 *
 * @author Kevin Ross (cs160_109)
 *
 * @version 0.1
 */

public class VenueLocation extends CinequestItem
{	
	/**
	 * @return the venue abbreviation
	 */
	public String getVenueAbbreviation() {
		return venueAbbreviation;
	}
	/**
	 * @param venue the venue to set
	 */
	public void setVenueAbbreviation(String venue) {
		this.venueAbbreviation = venue;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDirectionsURL()
    {
        return directionsURL;
    }
	
	public void setDirectionsURL(String directionsURL)
    {
        this.directionsURL = directionsURL;
    }

	private String venueAbbreviation;
	private String location;
	private String directionsURL;
}