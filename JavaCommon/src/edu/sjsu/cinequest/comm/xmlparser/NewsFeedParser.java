package edu.sjsu.cinequest.comm.xmlparser;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.News;
import edu.sjsu.cinequest.comm.cinequestitem.NewsFeed;

public class NewsFeedParser extends BasicHandler {
	
	// Set to empty string.
	private String currentBlock = "";
	
	private boolean getLastUpdated = false;	// If set to true, the entire feed is parsed. Else, only the lastUpdatedValue is fetched.
	private News news;
	private NewsFeed newsFeed;
	
	
	public static String getLastpdated(String url, Callback callback) throws SAXException, IOException {
		
		NewsFeedParser handler = new NewsFeedParser(true);
		Platform.getInstance().parse(url, handler, callback);
		return handler.getNewsFeed().getLastUpdated();
		
	}
	
	public static NewsFeed parseNewsFeed(String url, Callback callback) throws SAXException, IOException {
		
		NewsFeedParser handler = new NewsFeedParser(false);
		Platform.getInstance().parse(url, handler, callback);
		return handler.getNewsFeed();
		
	}
	
	public NewsFeedParser (boolean getLastUpdated) {
		this.getLastUpdated = getLastUpdated;
		newsFeed = new NewsFeed();
	}
	
	public NewsFeed getNewsFeed() {
		return newsFeed;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        if (lastTagName().equals("News") && !getLastUpdated ) {
            currentBlock = "News";
            news = new News();
        }
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        
        if (lastTagName().equals("LastUpdated")) {
        	
        	newsFeed.setLastUpdated(lastString());
        	
        } else if ( currentBlock.equals("News") && !getLastUpdated ) {	// Only if flag 'getLastUpdated' is set to false.
        	
        	// Perform the following only in case of a 'News'.
        	if(lastTagName().equals("Name")) {
        		news.setName(lastString());
        	} else if(lastTagName().equals("ShortDescription")) {
        		//news.setShortDescription(lastString());
        		news.setDescription(lastString());
        	} else if(lastTagName().equals("EventImage")) {
        		news.setEventImage(lastString());
        	} else if(lastTagName().equals("InfoLink")) {
        		news.setInfoLink(lastString());
        	} else if(lastTagName().equals("ThumbImage")) {
        		news.setThumbImage(lastString());
        	} else if(lastTagName().equals("News")) {
        		// End of a News Item. Add to News Feed object.
        		newsFeed.getNewsList().add(news);
        	}     	
        }
	}
}
