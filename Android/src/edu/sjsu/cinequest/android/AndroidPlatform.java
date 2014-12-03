package edu.sjsu.cinequest.android;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import edu.sjsu.cinequest.comm.Cache;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.CallbackException;
import edu.sjsu.cinequest.comm.MessageDigest;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.WebConnection;

// Must be created on UI thread
public class AndroidPlatform extends Platform {
	// echo -n "edu.sjsu.cinequest.android.AndroidPlatform" | md5sum | cut
	// -c1-16
	private static final long PERSISTENCE_KEY = 0x6a42ed61f192d055L;
	private Cache xmlRawBytesCache;
	private static final int MAX_CACHE_SIZE = 50;
	//private static final long MAX_CACHE_AGE = 1000L * 60 * 60 * 6; // 6 hours 
	private static final long MAX_CACHE_AGE = 1000L * 60; // modified cache time period to 5 minutes.
	private Handler handler;
	private Context context;

	public AndroidPlatform(Context context) {
		handler = new Handler();
		this.context = context;

		xmlRawBytesCache = (Cache) loadPersistentObject(PERSISTENCE_KEY);
		if (xmlRawBytesCache == null) {
			xmlRawBytesCache = new Cache(MAX_CACHE_SIZE);
		}

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				log(ex);
			}
		});
	}

	public WebConnection createWebConnection(String url) throws IOException {
		return new AndroidWebConnection(url);
	}

	@Override
	// TODO: Give better name to method
	// Returns an android.graphics.BitMap
	public Object convert(byte[] bytes) {
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	@Override
	// imageId must be an Integer containing an Android image ID, such as
	// R.drawable.hourglass
	// Returns an android.graphics.BitMap
	public Object getLocalImage(Object imageId) {
		return BitmapFactory.decodeResource(context.getResources(),
				((Integer) imageId).intValue());
	}

	@Override
	public void parse(String url, DefaultHandler handler, Callback callback)
			throws SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new SAXException(e.toString());
		}
		/*
		 * Android crap--search for
		 * http://www.google.com/search?q=android+XML+parser+8859-1 The parser
		 * can't infer the character encoding from the xml encoding attribute,
		 * so we have to hardwire 8859-1 here.
		 * Instead of 8859-1, I hardcoded the encoding to UTF-8, since it was 
		 * previously hardcoded to ISO-8895-I for the reasons stated above
		 * - Michael Singh
		 */
		if (getFromCache(url, sp, handler, MAX_CACHE_AGE))
			return;
		starting(callback);		
		WebConnection connection = null;
		try {
			connection = createWebConnection(url);
			byte[] xmlSource = (byte[]) connection.getBytes();
			if (xmlSource.length == 0)
				throw new IOException("No data received from server");

			// Store the xml source
			xmlRawBytesCache.put(url, xmlSource);
			InputSource in = new InputSource(new InputStreamReader(
					new ByteArrayInputStream(xmlSource), "UTF-8"));
			sp.parse(in, handler);
		} catch (IOException e) {
			Platform.getInstance().log(e);
			// Try to get XML from cache, no matter how old
			if (getFromCache(url, sp, handler, 0)) return;				
			throw new CallbackException("No network connection",
					CallbackException.ERROR);
		}
	}
	/*
	* Changed encoding from ISO-8895-I to UTF-8
	*/
	private boolean getFromCache(String url, SAXParser sp,
			DefaultHandler handler, long maxage) throws SAXException,
			IOException {
		byte[] bytes = (byte[]) xmlRawBytesCache.get(url, maxage);
		// XML exists in cache and isn't too old
		if (bytes != null) {
			InputSource in = new InputSource(new InputStreamReader(
					new ByteArrayInputStream(bytes), "UTF-8"));
			sp.parse(in, handler);
			Platform.getInstance().log(
					"AndroidPlatform.getFromCache: Returned cached response for "
							+ url);
			return true;
		} else
			return false;
	}

	@Override
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
		WebConnection connection = createWebConnection(url);
		connection.setPostParameters(postData);
		byte[] response = connection.getBytes();
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

	@Override
	public void starting(final Callback callback) {
		if (callback == null)
			return;
		handler.post(new Runnable() {
			public void run() {
				callback.starting();
			}
		});
	}

	@Override
	public void invoke(final Callback callback, final Object arg) {
		if (callback == null)
			return;
		handler.post(new Runnable() {
			public void run() {
				try {
					callback.invoke(arg);
				} catch (Throwable t) {

					String error = "Exception during AndroidPlatform.invoke(). Type="
							+ t.getClass().toString();
					if (t.getMessage() != null)
						error += ", Message=" + t.getMessage();

					log("AndroidPlatform.invoke: " + error);
				}
			}
		});
	}

	@Override
	public void failure(final Callback callback, final Throwable arg) {
		if (callback == null)
			return;
		handler.post(new Runnable() {
			public void run() {
				callback.failure(arg);
			}
		});
	}

	@Override
	// TODO: Add hint whether this is a cache or a truly persistent object
	public void storePersistentObject(long key, Object object) {
		try {
			File file = new File(context.getCacheDir(), key + ".ser");
			OutputStream out = new FileOutputStream(file); // context.openFileOutput(key
															// + ".ser",
															// Context.MODE_PRIVATE);
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(object);
			oout.close();
		} catch (Exception e) {
			log("AndroidPlatform.storePersistentObject: " + e.getMessage());
		}
	}

	@Override
	public Object loadPersistentObject(long key) {
		try {
			File file = new File(context.getCacheDir(), key + ".ser");
			InputStream in = new FileInputStream(file); // context.openFileInput(key
														// + ".ser");
			ObjectInputStream oin = new ObjectInputStream(in);
			Object ret = oin.readObject();
			oin.close();
			return ret;
		} catch (Exception e) {
			log("AndroidPlatform.loadPersistentObject: " + e.getMessage());
			return null;
		}
	}

	@Override
	public MessageDigest getMessageDigestInstance(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log(String message) {
		if (message == null)
			message = "null";
		Log.i("Cinequest", message);
	}

	@Override
	public void log(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		pw.close();
		log(sw.toString());
	}

	/**
	 * Check for active internet connection
	 */
	public boolean isNetworkAvailable() {
		// TODO: In that case, don't we still want to retrieve data from cache?
		ConnectivityManager cMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cMgr.getActiveNetworkInfo();

		return netInfo != null && netInfo.isAvailable();
	}

	@Override
	public void close() {
		storePersistentObject(PERSISTENCE_KEY, xmlRawBytesCache);
	}
}
