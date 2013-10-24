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

import java.util.Date;
import java.util.Hashtable;

import net.rim.device.api.util.Persistable;

/**
 * A simple cache that keeps the last n recently used items
 * @author Cay Horstmann
 */
public class Cache implements Persistable
{       
    /**
     * Constructs an empty cache. The cached objects must be persistable on the RIM platform, serializable on Java SE
     * @param maxsize the maximum size of this cache
     */
    public Cache(int maxsize)
    {
        this.maxsize = maxsize;
    }

    /**
     * Gets a cached value for a given key
     * @param key the key for the object
     * @return the cached value, or null if it is not present (either never entered or not recently used)
     */
    public synchronized Object get(String key)
    {
        CacheEntry entry = (CacheEntry) table.get(key);
        if (entry == null) return null;
        relink(entry);
        return entry.value;
    }
    
    /**
     * Gets a cached value for a given key, provided it is not too old
     * @param key the key for the object
     * @param maxMillisec the maximum age of the object, or 0 to take anything
     * @return the cached value, or null if it is not present or too old
     */
    public synchronized Object get(String key, long maxMillisec)
    {
    	if (maxMillisec == 0) return get(key);
        CacheEntry entry = (CacheEntry) table.get(key);
        if (entry == null) return null;
        long now = new Date().getTime();
        if (now - entry.timestamp > maxMillisec) 
        {
        	unlink(entry);
        	return null;
        }
        else
        {
        	relink(entry);
        	return entry.value;
        }
    }

    /**
     * Puts a cached value for a given key. 
     * @param key the key for the object
     * @param value the associated object
     */
    public synchronized void put(String key, Object value)
    {
        CacheEntry entry = (CacheEntry) table.get(key);
        if (entry == null) 
        {
            entry = new CacheEntry();
            entry.key = key;
            table.put(key, entry);
            link(entry);
            if (table.size() > maxsize)
            {
                remove(oldest.key);
            }
        }
        else relink(entry);
        entry.value = value;
    }

    /**
     * Removes a cached value
     * @param key the key for the value to be removed
     */
    public synchronized void remove(String key)
    {
        CacheEntry entry = (CacheEntry) table.remove(key);
        if (entry != null) unlink(entry);         
    }
    
    private void unlink(CacheEntry entry)
    {        
        if (entry == oldest) { oldest = entry.next; }
        if (entry == newest) { newest = entry.previous; }
        if (entry.previous != null) entry.previous.next = entry.next;
        if (entry.next != null) entry.next.previous = entry.previous;
    }
    
    private void relink(CacheEntry entry)
    {
    	entry.timestamp = new Date().getTime();
        if (entry == newest) return;
        unlink(entry);
        link(entry);
    }
    
    private void link(CacheEntry entry)
    {
        entry.previous = newest;
        entry.next = null;
        if (oldest == null) oldest = entry;
        if (newest != null) newest.next = entry;
        newest = entry;        
    }
    
    private class CacheEntry implements Persistable
    {
        Object value;
        CacheEntry next;
        CacheEntry previous;
        String key;
        long timestamp;
        
        CacheEntry()
        {
        	timestamp = new Date().getTime();
        }
    }    
    
    public synchronized String toString()
    {
        String ret = getClass().getName() + "[";
        CacheEntry entry = oldest;
        while (entry != null)
        {
            ret += entry.key + "->" + entry.value + " @" + entry.timestamp;
            if (entry.next != null) ret += ",";
            entry = entry.next;
        }
        
        return ret + "]";        
    }
    
    private Hashtable table = new Hashtable();
    private CacheEntry newest;
    private CacheEntry oldest;
    private int maxsize;
}


