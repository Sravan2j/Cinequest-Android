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
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Set's up a HTTP connection, retrives the image from the specified URL,and
 * returns the image.
 * 
 * @author Aaditya Bhatia
 * @author Cay Horstmann
 * 
 * @version 1.0 
 */
public class ImageManager
{
    private Hashtable bitmapCache = new Hashtable();
    private Cache rawBytesCache;
    // key produced by: echo -n "edu.sjsu.cs160.comm.ImageManager" | md5sum | cut -c1-16
    private static final long PERSISTENCE_KEY = 0x841d1100fbe42e24L;

    public ImageManager()
    {
        rawBytesCache = (Cache) Platform.getInstance()
                .loadPersistentObject(PERSISTENCE_KEY);
        if (rawBytesCache == null)
        {
            rawBytesCache = new Cache(20);
        }
    }

    /**
     * Fetches the images and puts it into the cache. This method is called from a background thread.
     * @param imageUrl the image URL
     * @param usePersistentCache true if this image should be persistently cached, false if it should only be cached for the program run
     * @return the image
     */
    private Object fetchImage(String imageUrl, boolean usePersistentCache) throws IOException
    {
        Object image = null;
        HttpURLConnection connection = ConnectionHelper.open(imageUrl);
        if (!connection.getHeaderField("content-type").startsWith("image"))
        {
            connection.disconnect();
            return null;
        }
        byte[] response = ConnectionHelper.getBytes(connection);        
        if (response.length > 0)
        {
            if (usePersistentCache)
            {
                rawBytesCache.put(imageUrl, response);           
            }
            image = Platform.getInstance().convert(response);
            bitmapCache.put(imageUrl, image);
        }
        return image;
    }

    /**
     * Fetches the images and puts it into the cache. This method is called from the UI thread.
     * @param imageUrl the image URL
     * @param callback the callback for delivering the final image
     * @param fallback the name of the fallback resource, or null if no fallback is desired
     * @param usePersistentCache true if this image should be persistently cached, false if it should only be cached for the program run
     * @return the cached or fallback image
     */
    public Object getImage(final String imageUrl, final Callback callback,
            Object fallback, final boolean usePersistentCache)
    {
    	callback.starting();
        Object img = bitmapCache.get(imageUrl); 
        if (img != null) {
        	if (fallback == null) callback.invoke(img);
        	return img;
        }
            
        if (usePersistentCache)
        {
            byte[] bytes = (byte[]) rawBytesCache.get(imageUrl);
            if (bytes != null)
            {
                img = Platform.getInstance().convert(bytes);
                bitmapCache.put(imageUrl, img);
                if (fallback == null) callback.invoke(img);
                return img;
            }
        }
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Object result = fetchImage(imageUrl, usePersistentCache);
                    Platform.getInstance().invoke(callback, result);
                }
                catch (Throwable e)
                {
                    Platform.getInstance().failure(callback, e);
                }
            }
        });
        t.start();
        if (fallback == null) return null;
        return Platform.getInstance().getLocalImage(fallback);
    }
    
    
    /**
     * Fetches multiple images in one thread. Called from UI thread. 
     * @param imageUrls the image URLs
     * @param callback the callback for reporting progress. Each progress call delivers one image.
     * Final callback delivers vector of all images
     */
    public void getImages(final Vector imageUrls, final Callback callback)
    {
    	callback.starting();
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
            	Vector images = new Vector();
                for (int i = 0; i < imageUrls.size(); i++)
                {
                    String imageUrl = (String) imageUrls.elementAt(i);
                    try
                    {
                        Object result = fetchImage(imageUrl, true);
                        images.addElement(result);
                    }
                    catch (Throwable e)
                    {
                        Platform.getInstance().failure(callback, e);
                    }                
                }
                Platform.getInstance().invoke(callback, images);                
            }
        });
        t.start();        
    }

    /**
     * Saves the persistent cache.
     */
    public void close()
    {
        Platform.getInstance().storePersistentObject(PERSISTENCE_KEY, rawBytesCache); 
    }
}