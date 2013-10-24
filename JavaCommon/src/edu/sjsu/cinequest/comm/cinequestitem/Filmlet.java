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
 * Filmlet is a class that may prove useful as something to pass to the UI
 * team when all the details of a film are not necessary, such as in a schedule.
 * 
 * @author Kevin Ross (cs160_109)
 *
 * @version 0.1
 */

public class Filmlet extends CinequestItem
{
    public boolean isDVD() { return get(DVD); }
    public boolean isDownload() { return get(DOWNLOAD); }
    public void setDVD(boolean newValue) { set(DVD, newValue); }
    public void setDownload(boolean newValue) { set(DOWNLOAD, newValue); }
    
    //quick and dirty fix for prefix searching
    public String toString() { return getTitle(); }
    
    private void set(int mask, boolean newValue) { if (newValue) type |= mask; else type &= ~mask; }
    private boolean get(int mask) { return (type & mask) != 0; }
    private static final int DVD = 1;
    private static final int DOWNLOAD = 2;
    private int type;
}
