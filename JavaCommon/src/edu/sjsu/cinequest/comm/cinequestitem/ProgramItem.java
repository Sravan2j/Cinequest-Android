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

import java.util.ArrayList;

/**
 * ProgramItem represents a Cinequest program.
 *
 * @author Kevin Ross (cs160_109)
 *
 * @version 0.1
 */

public class ProgramItem extends CinequestItem
{	
	/**
	 * Constructor initializes class with appropriate values.
	 */
	public ProgramItem()
	{
		films = new ArrayList<Film>();
	}

	/**
	 * @return the vector of films
	 */
	public ArrayList<Film> getFilms() {
		return films;
	}

	/**
	 * Return this item's image URL if present, or, if not,
	 * the first non-null image URL in the associated films
	 */
	public String getImageURL() 
	{
		String url = super.getImageURL();
		if (url != null) return url;
		if (films != null)
		{
			for (int i = 0; i < films.size(); i++)
			{
				Film film = (Film) films.get(0);
				url = film.getImageURL();
				if (url != null) return url;
			}
		}
		return null;
	}
	
	private ArrayList<Film> films;
	
    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation 
     * of this object.
     */
    public String toString()
    {
        return super.toString() + "[films=" + films + "]";
    }
}