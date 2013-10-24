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

import java.io.IOException;
import java.util.Hashtable;

/**
 * A class that describes how to get input from an URL
 * @author Cay Horstmann
 *
 */
public abstract class WebConnection
{
    /**
     * Call this method before calling getBytes or getHeaderField if you
     * want to do a POST.
     */
    public abstract void setPostParameters(Hashtable params) throws IOException;
    
    /**
     * Closes this connection.
     * @throws IOException
     */
    public abstract void close() throws IOException;    
    
    public abstract String getHeaderField(String name) throws IOException;
    
    public abstract byte[] getBytes() throws IOException;
}
