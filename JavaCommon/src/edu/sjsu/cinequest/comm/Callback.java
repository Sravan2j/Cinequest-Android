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


/**
 * Callback is the interface that allows us to pass a result
 * objects back to the UI class in a different thread, so we don't freeze the UI
 * for every query. 
 * 
 * @author Kevin Ross (cs160_109)
 * 
 * @version 0.2
 */
public interface Callback
{
	/**
	 * This method is called when a long-running activity is started, so
	 * that the user interface can pop up a progress dialog.
	 */
	public void starting();
    /**
     * This method returns the query result
     * @param result the query result
     */
	public void invoke(Object result);
	/**
	 * This method returns an exception that terminated the query
	 * @param t the throwable that terminated the query
	 */
	public void failure(Throwable t);
}