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

import org.xml.sax.SAXException;

import android.util.Log;

import edu.sjsu.cinequest.comm.cinequestitem.Festival;
import edu.sjsu.cinequest.comm.xmlparser.FestivalParser;
import edu.sjsu.cinequest.comm.xmlparser.NewsFeedParser;

/**
 * @author Kevin Ross (cs160_109)
 */
public class QueryManager {
	
	private String lastUpdated="";

	private static final String imageBase = "https://mobile.cinequest.org/";
	private static final String mainImageURL = "imgs/mobile/creative.gif";
	
	public static final String showsFeedURL = "https://payments.cinequest.org/websales/feed.ashx?guid=d52499c1-3164-429f-b057-384dd7ec4b23&showslist=true";
	public static final String newsFeedURL = "https://www.cinequest.org/news.php"; // TODO: News no longer used. Move LastUpdated to venues or trending feed
	public static final String venuesFeedURL = "https://www.cinequest.org/misc/venuelist.php";

	public static final String trendingFeedURL = "https://www.cinequest.org/misc/trending/XML/MobileTrending_Top10Events.xml";
	private Festival festival = new Festival();
	private Object festivalLock = new Object();
	private boolean festivalQueryInProgress = false;
	private Object progressLock = new Object();
	
	private interface Callable {
		Object run() throws Throwable;
	}

	private void getWebData(final Callback callback, final Callable task) {
		if (callback == null || task == null)
			throw new NullPointerException();
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Object result = task.run();
					Platform.getInstance().invoke(callback, result);
				} catch (Throwable e) {
					Platform.getInstance().failure(callback, e);
				}
			}
		});
		t.start();
	}

	public void prefetchFestival() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Platform.getInstance().log("Prefetching festival");
					getFestival(new Callback() {
						public void failure(Throwable t) {
							Platform.getInstance().log(t);
						}

						public void invoke(Object result) {
						}

						public void starting() {
						}
					});
					Platform.getInstance().log("Done prefetching festival");
				} catch (Exception e) {
					Platform.getInstance().log(e);
				}
			}
		});
		t.start();
	}
	
	public String getlastUpdated() {
		return lastUpdated;
	}

	public void getAllFilms(final Callback callback) {
		getWebData(callback, new Callable() {
			public Object run() throws Throwable {
				return getFestival(callback).getFilms();
			}
		});
	}

	public void getAllEvents(final Callback callback){
		getWebData(callback, new Callable() {
			public Object run() throws Throwable {
                return getFestival(callback).getEvents();
			}
		});
	}

	public void getVideos(final Callback callback) {
		getWebData(callback, new Callable() {
			public Object run() throws Throwable {
				return getFestival(callback).getVideos();
			}
		});
	}

	public void getSchedulesByDate(final Callback callback) {
        getWebData(callback, new Callable() {
            public Object run() throws Throwable {
                return getFestival(callback).getSchedulesByDate();
            }
        });
    }

    public void getTrending(final Callback callback) {
        getWebData(callback, new Callable() {
            public Object run() throws Throwable {
                int[] ids = FestivalParser.parseShowIds(trendingFeedURL, callback);
                Festival festival = getFestival(callback);
                festival.setTrendingIds(ids);
                return festival.getTrending();
            }
        });
    }


	/**
	 * Resolves a relative image URL
	 * 
	 * @param url
	 *            the relative URL to resolve
	 * @return the absolute URL for fetching the image
	 */
	public String resolveRelativeImageURL(String url) {
		if (url.startsWith("http://"))
			return url;
		else
			return imageBase + url;
	}

	/**
	 * Gets the URL for the main image (in the entry screen)
	 */
	public String getMainImageURL() {
		return resolveRelativeImageURL(mainImageURL);
	}
	
	public void loadFestival(final Callback callback) {
		getWebData(callback, new Callable() {
			public Object run() throws Throwable {
				return getFestival(callback);
			}
		});
	}
	
	/**
	 * Gets the complete data of the Festival. Call only inside run method of
	 * getWebData.
	 */
	private Festival getFestival(final Callback callback) throws SAXException,
			IOException {
		synchronized (progressLock) {
			if (festivalQueryInProgress)
				Platform.getInstance()
						.log("Festival query called while another query in progress.");
			Platform.getInstance().starting(callback);
		}
		synchronized (festivalLock) {
			String updatedDate = NewsFeedParser.getLastpdated(newsFeedURL, callback);
			Log.i("QueryManager:getFestiva","LastUpdated from news feed: " + updatedDate + ", lastUpdated: " + lastUpdated);
			
			if (updatedDate.equalsIgnoreCase(lastUpdated) && (!festival.isEmpty()))
				return festival;

			synchronized (progressLock) {
				festivalQueryInProgress = true;
			}
			try {
				Festival result = FestivalParser.parseFestival(showsFeedURL, callback);
				if (!result.isEmpty()) {
					festival = result;
					lastUpdated = updatedDate;
				} else {
					Log.i("QueryManager:getFestiva","Festival object is empty");
				}

			} finally {
				synchronized (progressLock) {
					festivalQueryInProgress = false;
				}
			}
			return festival;
		}
	}
}
