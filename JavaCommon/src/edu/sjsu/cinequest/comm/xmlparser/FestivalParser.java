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

package edu.sjsu.cinequest.comm.xmlparser;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.QueryManager;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;
import edu.sjsu.cinequest.comm.cinequestitem.Festival;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.Venue;

/**
 * Parses the complete information of the Festival
 */
public class FestivalParser extends BasicHandler {
    private class Showing {
        public String id;
        public String startDate;
        public String endDate;
        public String shortDescription;
        public Venue venue = new Venue();
    }

    private class Show {
        public String id;
        public String name;
        public int duration;
        public String shortDescription;
        public String thumbImageURL;
        public String eventImageURL;
        public String infoLink;
        public Map<String, List<String>> customProperties = new HashMap<String, List<String>>();
        public List<Showing> currentShowings = new ArrayList<Showing>();
    }
    
	/**
     * Contains the List of Shows parsed from the XML feed. 
     */
    private List<Show> shows = new ArrayList<Show>();
    
    
    /**
     * Represents the current block being processed within the XML feed.
     */
    private String currentBlock = "";
    
    // Temporary obejtcs required while parsing the XML feed.
    private Show show;
    private Showing showing;
    private String propName;


    /**
     * Parses the complete Festival information from the XML feed.
     * 
     * @param url The URL which contains the XML feed.
     * @param callback The callback object
     * @return The parsed Festival object.
     * @throws SAXException
     * @throws IOException
     */
    public static Festival parseFestival(String url, Callback callback) throws SAXException, IOException {
    	Log.d("FestivalParser.java", "Within parseFestival(), url:" + url);
    	
        List<Show> shows = parseShows(url, callback);
        
        // Parse the list of Venues using the Venue XML feed.
        Map<String, Venue> venues = VenuesParser.parse(QueryManager.venuesFeedURL, callback);
        
        Log.d("FestivalParser.java", "Parsed Shows, Size:" + shows.size());
        return new FestivalConverter(shows, venues).convert();
    }
    
    /**
     * This method will log error if any within the XML feed. 
     * Errors include Shows with no EventType, ShortIDs that do not exist or Shorts having a atleast 1 Showing. 
     * 
     * @param url The URL which contains the XML feed.
     * @param callback The callback object
     * @throws SAXException
     * @throws IOException
     */
    public static void logErrors(String url, Callback callback) throws SAXException, IOException {
    	Log.d("FestivalParser.java", "Within logErrors(), url:" + url);
    	
        List<Show> shows = parseShows(url, callback);
        
        // Parse the list of Venues using the Venue XML feed.
        Map<String, Venue> venues = VenuesParser.parse(QueryManager.venuesFeedURL, callback);
        
        Log.d("FestivalParser.java", "Parsed Shows, Size:" + shows.size());
        FestivalConverter festivalConverter  = new FestivalConverter(shows, venues);
        
        festivalConverter.convert();
        
        festivalConverter.getShowsWithNoEventType();
        festivalConverter.getShortsNotFound();
        
        festivalConverter.getInvalidShorts();
     
        Set<String> showsWithNoEventType = festivalConverter.getShowsWithNoEventType().keySet();
        
        for(String showWithNoEventType: showsWithNoEventType) {
        	
        	Log.e("FestivalParser.java", "Show with missing EventType: " + showWithNoEventType);
        }
   
        List<String> shortsNotFound = festivalConverter.getShortsNotFound();
        
        for(String shortNotFound: shortsNotFound) {

        	Log.e("FestivalParser.java", "ShortId not found: " + shortNotFound);
        	
        }
  
        Set<String> invalidShorts = festivalConverter.getInvalidShorts().keySet();
        
        for(String invalidShort: invalidShorts) {
        	
        	Log.e("FestivalParser.java", "Invalid Short (Showing not empty): " + invalidShort);      	
        }
        
    }
    
    /**
     * Parses the XML feed using the given URL and returns a list of Shows.
     * 
     * @param url The URL which contains the XML feed.
     * @param callback The callback object
     * @return The list of Shows
     * @throws SAXException
     * @throws IOException
     */
    private static List<Show> parseShows(String url, Callback callback) throws SAXException, IOException {
        FestivalParser handler = new FestivalParser();        
        Platform.getInstance().parse(url, handler, callback);
        return handler.shows;
    }

    /**
     * Parse just the show IDs. This is needed for the trending feed.
     * @param url
     * @param callback
     * @return an array of all show IDs in the feed
     */
    public static int[] parseShowIds(String url, Callback callback) throws SAXException, IOException {
        List<Show> shows = parseShows(url, callback);
        int[] ids = new int[shows.size()];
        for (int i = 0; i < ids.length; i++) ids[i] = Integer.parseInt(shows.get(i).id);
        return ids;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (lastTagName().equals("Show")) {
            currentBlock = "Show";
            show = new Show();
        } else if (lastTagName().equals("Showing")) {
            currentBlock = "Showing";
            showing = new Showing();
        } else if (lastTagName().equals("CustomProperty")) {
            currentBlock = "CustomProperty";
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (lastTagName().equals("ID")) {
            if (currentBlock.equals("Show")) {
                show.id = lastString();
            } else if (currentBlock.equals("Showing")) {
                showing.id = lastString();
            }
        } else if (lastTagName().equals("Name")) {
            if (currentBlock.equals("Show")) {
                show.name = lastString();
            } else if (currentBlock.equals("CustomProperty")) {
                propName = lastString();
            }
        } else if (lastTagName().equals("Value")) {
            List<String> values = show.customProperties.get(propName);
            if (values == null) {
                values = new ArrayList<String>();
                show.customProperties.put(propName, values);
            }
            values.add(lastString());
        } else if (lastTagName().equals("ShortDescription")) {
            if (currentBlock.equals("Show")) {
                show.shortDescription = lastString();
            } else if (currentBlock.equals("Showing")) {
                showing.shortDescription = lastString();
            }
        } else if (lastTagName().equals("ThumbImage")) {
            show.thumbImageURL = lastString();
        } else if (lastTagName().equals("EventImage")) {
            show.eventImageURL = lastString();
        } else if (lastTagName().equals("InfoLink")) {
            show.infoLink = lastString();
        } else if (lastTagName().equals("StartDate")) {
            showing.startDate = lastString();
        } else if (lastTagName().equals("EndDate")) {
            showing.endDate = lastString();
        } else if (lastTagName().equals("VenueID")) {
            showing.venue.id = lastString();
        } else if (lastTagName().equals("VenueName")) {
            showing.venue.name = lastString();
        } else if (lastTagName().equals("VenueAddress1")) {
            showing.venue.address = lastString();
        } else if (lastTagName().equals("Show")) {
            shows.add(show);
            show = null;
        } else if (lastTagName().equals("Showing")) {
            show.currentShowings.add(showing);
            showing = null;
        }
    }

    /**
     * This class will convert the List of Shows into a Festival object.
     *
     */
    public static class FestivalConverter {
    	
    	// Some collections used to populate the required information within a Festival.
        private List<Show> shows;
        private List<Show> actualShows;
        private Map<String, Venue> venues;
        private Festival festival = new Festival();
        
        private Map<String, Show> showsWithNoEventType = new HashMap<String, Show>();
		private Map<String, Show> invalidShorts = new HashMap<String, Show>();
        private List<String> shortsNotFound = new ArrayList<String>();
        
        public Map<String, Show> getShowsWithNoEventType() {
			return showsWithNoEventType;
		}

		public Map<String, Show> getInvalidShorts() {
			return invalidShorts;
		}

		public List<String> getShortsNotFound() {
			return shortsNotFound;
		}
        
        public FestivalConverter(List<Show> shows, Map<String, Venue> venues) {
            this.shows = shows;
            this.venues = venues;
            
            actualShows = new ArrayList<Show>(this.shows);
            
            Collections.copy(actualShows, this.shows);
        }

        public Festival convert() {
        	
            // This contains the actual list of Shows in a map.
            Map<String, Show> showsMap = new HashMap<String, Show>();
            
            // Populate the Shows Map. Would be useful later.
            for(Show show : shows) {

            	showsMap.put(show.id, show);       	
            }
            
            this.populateFestivalItems("Film", showsMap);
            this.populateFestivalItems("Event", showsMap);

            return festival;
        }

        private static String venueAbbr(String name) {
            return name.replaceAll("[^A-Z0-9]", "");
        }

        private Schedule getSchedule(Showing showing, CommonItem item) {
            Schedule schedule = new Schedule();
            schedule.setId(Integer.parseInt(showing.id));
            schedule.setItem(item);
            schedule.setTitle(item.getTitle());
            schedule.setStartTime(showing.startDate);
            schedule.setEndTime(showing.endDate);
            
            // If 'venues' contains showing.venue,
            // set the Schedule's Venue as the 'this.venues' shortName
            // set the Schedule's directionsURL, using this.venue's Location
            if(venues.containsKey(showing.venue.id)) {
            	schedule.setVenue(venues.get(showing.venue.id).shortName);
            	schedule.setDirectionsURL(venues.get(showing.venue.id).location);
            } else {
            	// Else use the older logic to compute the venue abbreviation.
            	schedule.setVenue(venueAbbr(showing.venue.name));
            }
            
            return schedule;
        }
        
        private CommonItem getCommonItem(String type, Show show) {
        	
        	CommonItem commonItem = new CommonItem();
            /* TODO: tagline and filmInfo seem unused
             * TODO: What should we do with the executive producers?
             */
            commonItem.setId(Integer.parseInt(show.id));
            commonItem.setTitle(show.name);
            commonItem.setDescription(show.shortDescription);
            commonItem.setThumbImageURL(show.thumbImageURL);
            commonItem.setImageURL(show.eventImageURL);
            commonItem.setVideoURL(get(show.customProperties, "Videofeed"));
            commonItem.setDirector(get(show.customProperties, "Director"));
            commonItem.setProducer(get(show.customProperties, "Producer"));
            commonItem.setCinematographer(get(show.customProperties, "Cinematographer"));
            commonItem.setEditor(get(show.customProperties, "Editor"));
            commonItem.setCast(get(show.customProperties, "Cast"));
            commonItem.setCountry(get(show.customProperties, "Production Country"));
            commonItem.setLanguage(get(show.customProperties, "Language"));
            commonItem.setGenre(get(show.customProperties, "Genre"));
            commonItem.setInfoLink(show.infoLink);
            
            commonItem.setType(type);
            
            return commonItem;
        	
        }
        
        /**
         * Returns the List of Shows based on the input type.
         * 
         * @param type The type of Shows to be considered
         * @return The filtered list of Shows.
         */
        private List<Show> filterShows(String type) {
        	
        	List<Show> filteredShows = new ArrayList<Show>();

        	for (Show show : shows) {
        		
        		if(type.equals("Film") && 
        				( (show.customProperties.containsKey("EventType") && show.customProperties.get("EventType").contains("Film")) ||
        						!show.customProperties.containsKey("EventType") ) ) {
        			
        			filteredShows.add(show);
        			
        			if(!show.customProperties.containsKey("EventType")) {
        				// Log as ERROR
        				showsWithNoEventType.put(show.id, show);
        			}
        			
        		} else if(type.equals("Event") && 
        				(show.customProperties.containsKey("EventType") && 
        						!(show.customProperties.get("EventType").contains("Film")))) {
        			filteredShows.add(show);
        		}
        	}
        	
        	return filteredShows;
        }
        
        /**
         * Collects the Short Films from the given List of Shows and also validates the Short Films 
         * (i.e. a Short Film should not contain a Showing)
         * 
         * @param type The type of entity (Film/Event)
         * @param filteredShows The filtered list if Shows
         * @param showsMap Map containing the Show Id and the associated Show Object.
         * @return Map containing the Shorts
         */
        private Map<Integer, CommonItem> collectShortsAndValidate( String type, List<Show> filteredShows, Map<String, Show> showsMap) {

        	Map<Integer, CommonItem> shortsMap = new HashMap<Integer, CommonItem>();
        	
        	Iterator it = filteredShows.iterator();

        	while(it.hasNext()) {

        		Show show = (Show)it.next();

        		// This is a Film. Does this Film have Short Films ? 
        		// If yes, validate that each ShortFilm does not have any current Showing.        		
        		if(show.customProperties.containsKey("ShortID")) {
        			List<String> shortFilmsIDs = show.customProperties.get("ShortID");

        			for(String shortFilmId : shortFilmsIDs) {

        				// Fetch the Show using the ShortFilmID
        				if(showsMap.containsKey(shortFilmId)) {

        					Show shortFilmToBeChecked = showsMap.get(shortFilmId);

        					if(shortFilmToBeChecked.currentShowings.isEmpty()) {
        						// This is a valid ShortFilm
        						// Add to shortsMap
        						
        						CommonItem shortsItem = this.getCommonItem(type, shortFilmToBeChecked);

        						if(!shortsMap.containsKey(shortsItem.getId())){
        							shortsMap.put(shortsItem.getId(), shortsItem);
        						}
        						
        					} else {
        						// A ShortFilm should not have any CurrentShowing
        						// FIXME - Log this an error
        						
        						invalidShorts.put(shortFilmToBeChecked.id, shortFilmToBeChecked);
        					}

        				} else {
        					// FIXME - The ShortID does not exist. Should this be Logged as an ERROR ?
        					if(shortsNotFound.contains(shortFilmId)) {
        						shortsNotFound.add(shortFilmId);
        					}
        					
        				}
        			}         			
        		}
        	}
        	
        	return shortsMap;

        }
        
        /**
         * This method removes allShort Films from the List of Shows.
         * 
         * @param filteredShows The List of Shoes
         * @param shortsMap Contains the Shorts.
         */
        private void removeShorts(List<Show> filteredShows, Map<Integer, CommonItem> shortsMap) {
        	
        	Iterator iter = filteredShows.iterator();
            
            // Remove all ShortFilms from the list of Shows.
            while( iter.hasNext() ) {
            	
            	Show show = (Show)iter.next();
            	
            	if(shortsMap.containsKey(Integer.parseInt(show.id))) {
            		iter.remove();
            	}
            }   	
        }
        

        private static String get(Map<String, List<String>> custom, String key) {
            List<String> value = custom.get(key);
            if (value == null) return "";
            if (value.size() == 1) return value.get(0);
            String result = value.toString();
            return result.substring(1, result.length() - 1);
        }
        
        /**
         * This method populates the Films/Events using the given type and list of Shows.
         * 
         * @param type The entity type (Film/Event)
         * @param showsMap Contains the Shows parsed from the XML feed.
         */
        private void populateFestivalItems(String type, Map<String, Show> showsMap) {
        	
            List<Show> filteredShows = filterShows(type);
            
            Map<Integer, CommonItem> shortsMap = collectShortsAndValidate(type, filteredShows, showsMap);

            removeShorts(filteredShows, shortsMap);

            for (Show show : filteredShows) {
            	
                CommonItem item = getCommonItem(type, show);
                festival.addItem(item, type);

                // Now, find out if this ProgramItem has a list of ShortFilms associated with it.
                List<String> associatedShortIds = show.customProperties.get("ShortID");
                
                if(associatedShortIds != null && associatedShortIds.size() > 0) {
                	
                	// So, this ProgramItem has a list of associated ShortFilms.
                	
                	for(String shortID : associatedShortIds) {
                		
                		// The required ShortFilm exists in the Map. Retrieve it and add to the ProgramItem.
                		if(shortsMap.containsKey(Integer.parseInt(shortID))) {

                			CommonItem shortsItem = shortsMap.get(Integer.parseInt(shortID));                			
                			item.getCommonItems().add(shortsItem);
                		}		
                	}       	
                }

                for (Showing showing : show.currentShowings) {
                    Schedule schedule = getSchedule(showing, item);
                    festival.addSchedule(schedule);
                    
                    for (CommonItem children : item.getCommonItems()) {
                    	children.getSchedules().add(schedule);

                    }
                   
                    // Add the schedule to the main item
                    item.getSchedules().add(schedule);
                }

                if (item.getVideoURL() != null) festival.getVideos().add(item);
            }  	
        }
    }
}