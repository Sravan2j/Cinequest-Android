package edu.sjsu.cinequest.comm.xmlparser;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;

// Only used to get lastUpdated, to avoid reparsing show feed
// TODO: Move lastUpdated into show feed, or choose some other caching strategy

public class NewsFeedParser extends BasicHandler {
    private String lastUpdated;

    public static String getLastpdated(String url, Callback callback) throws SAXException, IOException {
        NewsFeedParser handler = new NewsFeedParser();
        Platform.getInstance().parse(url, handler, callback);
        return handler.lastUpdated;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (lastTagName().equals("LastUpdated")) {
            lastUpdated = lastString();
        }
    }
}