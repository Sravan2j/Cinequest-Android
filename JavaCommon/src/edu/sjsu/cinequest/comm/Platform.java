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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Manages services that are specific to either a RIM or JavaSE application
 * @author Cay Horstmann
 */
public abstract class Platform
{
    private static Platform platform;
    
    /**
     * Call this once whe program starts
     * @param appl A subclass of Platform, typically RIMPlatform or JavaSEPlatform
     */
    public static void setInstance(Platform appl) 
    { 
        platform = appl; 
    } 
    
    /**
     * Gets the singleton platform instance.
     * @return the platform instance
     */
    public static Platform getInstance() { return platform; }
    
    /**
     * Converts an array of bytes into an application-specific image object
     * @param bytes an array of image data
     * @return an object suitable for images in this application (Bitmap for RIM, Image for Java SE)
     */
    public abstract Object convert(byte[] bytes);
        
    public abstract Object getLocalImage(Object imageId);
    
    /**
     * Parses XML data
     * @param url the URL at which the XML data is located
     * @param handler the SAX handler to process the XML events
     * @param callback the callback to report parsing progress
     * @throws SAXException
     * @throws IOException
     */
    public abstract void parse(final String url, DefaultHandler handler,
            Callback callback) throws SAXException, IOException;
    
    /**
     * @return the retrieved document (for error reporting)
     */
	public String parse(String url, Hashtable postData, DefaultHandler handler,
			Callback callback) throws SAXException, IOException {
		starting(callback);
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new SAXException(e.toString());
		}
		HttpURLConnection connection = ConnectionHelper.open(url);
		ConnectionHelper.setPostParameters(connection, postData);
		byte[] response = ConnectionHelper.getBytes(connection);
		String doc = new String(response);
		if (response.length == 0) {
			Platform.getInstance().log(
					"AndroidPlatform.parse: No data received from server");
			throw new IOException("No data received from server");
		}
		InputSource inputSource = new InputSource(new ByteArrayInputStream(
				response));
		sp.parse(inputSource, handler);
		return doc;
	}
    
    /**
     * Calls the Callback's starting method
     * @param callback the callback whose method is being called
     */
    public abstract void starting(Callback callback);

    /**
     * Calls the Callback's invoke method
     * @param callback the callback whose method is being called
     * @param arg the argument to be provided
     */
    public abstract void invoke(Callback callback, Object arg);
    /**
     * Calls the Callback's failure method
     * @param callback the callback whose method is being called
     * @param arg the argument to be provided
     */
    public abstract void failure(Callback callback, Throwable arg);
    
    /**
     * Persistently stores an object. The object must be persistable (RIM) or serializable (Java SE)
     * @param key the key used for locating the object
     * @param object the object to store. 
     */
    public abstract void storePersistentObject(long key, Object object);
    
    /**
     * Loads a persisted object. 
     * @param key the key used for locating the object
     * @return the retrieved object 
     */
    public abstract Object loadPersistentObject(long key);
    
	public abstract MessageDigest getMessageDigestInstance(String name);
	
	/** 
	 * @param obj the object to be encrypted or decrypted
	 * @param decrypt true for decryption
	 * @return the encrypted or decrypted object
	 */
	public Object crypt(Object obj, boolean decrypt)
	{
	   return obj;
	}
	
	public abstract void log(String message);
	
	public abstract void log(Throwable ex);
	
	public boolean isNetworkAvailable() { return true; }
	
	public abstract void close();
}
