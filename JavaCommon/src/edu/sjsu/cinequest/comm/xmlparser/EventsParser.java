package edu.sjsu.cinequest.comm.xmlparser;

import java.io.IOException;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;

public class EventsParser extends SchedulesParser
{
    private String type;
    private Vector result;
    
    /**
     * 
     * @param url the URL to parse
     * @param type null (for all events), "forums", or "special_events
     * @param callback the callback for progress reporting
     * @return a vector of Schedule items for all matching events
     * @throws SAXException
     * @throws IOException
     */
    public static Vector parseEvents(String url, String type, Callback callback) throws SAXException, IOException
    {
        Vector result = new Vector();        
        EventsParser handler = new EventsParser(type, result);
        Platform.getInstance().parse(url, handler, callback);
        return result;
    }

    public EventsParser(String type, Vector result)
    {
        this.type = type;
        this.result = result;
        setResult(result);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, qName, attributes);
        if (type != null && (lastTagName().equals("forums") || lastTagName().equals("special_events")))
        {
            if (type.equals(lastTagName())) setResult(result); else setResult(null);
        }
    }
}
