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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.QueryManager;
import edu.sjsu.cinequest.comm.cinequestitem.CommonItem;
import edu.sjsu.cinequest.comm.cinequestitem.Festival;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.Show;
import edu.sjsu.cinequest.comm.cinequestitem.Showing;
import edu.sjsu.cinequest.comm.cinequestitem.Venue;
import edu.sjsu.cinequest.comm.cinequestitem.VenueLocation;

/**
 * Parses the complete information of the Festival
 */
public class FestivalParser extends BasicHandler {
    
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
   
        ArrayList<String> shortsNotFound = festivalConverter.getShortsNotFound();
        
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
    public static List<Show> parseShows(String url, Callback callback) throws SAXException, IOException {
        FestivalParser handler = new FestivalParser();        
        Platform.getInstance().parse(url, handler, callback);
        return handler.getShows();
    }

    /**
     * Getter for list of Shows
     * 
     * @return List of Shows
     */
    public List<Show> getShows() {
        return shows;
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
            ArrayList<String> values = show.customProperties.get(propName);
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
        private ArrayList<String> shortsNotFound = new ArrayList<String>();
        
        public Map<String, Show> getShowsWithNoEventType() {
			return showsWithNoEventType;
		}

		public Map<String, Show> getInvalidShorts() {
			return invalidShorts;
		}

		public ArrayList<String> getShortsNotFound() {
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
            this.populateFestivalItems("Forum", showsMap);

            return festival;
        }

        private static String venueAbbr(String name) {
            return name.replaceAll("[^A-Z0-9]", "");
        }

        private VenueLocation getVenueLocation(Venue venue) {
        	
        	VenueLocation loc = new VenueLocation();
        	
        	// Venues List contains the required Venue.  Set the VenueLocation's VenueAbbreviation and DirectionsURL.
        	if( venues.containsKey(venue.id)) {
        		venue = venues.get(venue.id);
        		
        		loc.setVenueAbbreviation(venue.shortName);
        		loc.setDirectionsURL(venue.location);
        		
        	} else {
        		
        		// Else set the Venue abbreviation using older logic.
        		loc.setVenueAbbreviation(venueAbbr(venue.name));
        	}
            
            loc.setId(Integer.parseInt(venue.id));
            
            loc.setTitle(venue.name);
            loc.setLocation(venue.address);
            
            return loc;
        }
        
        private Schedule getSchedule(Showing showing, CommonItem item) {
            Schedule schedule = new Schedule();
            schedule.setId(Integer.parseInt(showing.id));
            schedule.setItemId(item.getId()); // TODO: Why not just set the item reference???
            schedule.setTitle(item.getTitle());
            schedule.setStartTime(showing.startDate);
            schedule.setEndTime(showing.endDate);
            
            //schedule.setVenue(venueAbbr(showing.venue.name)); // TODO: Why not the Venue object?
            
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
            commonItem.setImageURL(show.thumbImageURL);
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

        	Iterator<Show> iter = shows.iterator();

        	while(iter.hasNext()) {

        		Show show = iter.next();
        		
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
        						(show.customProperties.get("EventType").contains("Special") 
        						|| show.customProperties.get("EventType").contains("Screening")))) {
        			filteredShows.add(show);
        		} else if(type.equals("Forum") &&
        				(show.customProperties.containsKey("EventType") && show.customProperties.get("EventType").contains("Forum"))) {
        			filteredShows.add(show);
        		}
        	}
        	
        	return filteredShows;
        }
        
        /**
         * Collects the Short Films from the given List of Shows and also validates the Short Films 
         * (i.e. a Short Film should not contain a Showing)
         * 
         * @param type The type of entity (Film/Event/Forum)
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
        

        private static String get(Map<String, ArrayList<String>> custom, String key) {
            ArrayList<String> value = custom.get(key);
            if (value == null) return "";
            if (value.size() == 1) return value.get(0);
            String result = value.toString();
            return result.substring(1, result.length() - 1);
        }
        
        /**
         * Returns a list of timestamps associated with the schedules within the CommonItem.
         * NOTE: This method processes the schedule.getStartTime().
         * 
         * @param item The given ComonItem
         * @return The list of timestamps.
         */
        private SortedSet<String> getDatesFromCommonItem(CommonItem item) {
        	
        	String isoDatePattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$";
        	
        	SortedSet<String> dates = new TreeSet<String>();
        	
        	List<Schedule> itemSchedules = item.getSchedules();
        	
        	for(Schedule schedule : itemSchedules) {
        		
        		String startTime = schedule.getStartTime();
        		
        		Matcher m = Pattern.compile(isoDatePattern).matcher(startTime);
        		
        		if(m.matches()) {
        			// Time is in ISO format
        			String date = startTime.split("T")[0];
        			
        			if( !dates.contains(date) ) {
        				dates.add(date);
        			}		
        		}      		
        	}
        	
        	return dates;
        }
        
        /**
         * This method populates the Films/Events/Forums using the given type and list of Shows.
         * 
         * @param type The entity type (Film/Event/Forum)
         * @param showsMap Contains the Shows parsed from the XML feed.
         */
        private void populateFestivalItems(String type, Map<String, Show> showsMap) {
        	
            Set<String> uniqueVenues = new HashSet<String>();            

            List<Show> filteredShows = this.filterShows(type);
            
            Map<Integer, CommonItem> shortsMap = this.collectShortsAndValidate(type, filteredShows, showsMap);

            this.removeShorts(filteredShows, shortsMap);

            for (Show show : filteredShows) {
            	
                CommonItem item = getCommonItem(type, show);
                
                // Before adding the 'item' to commonItems within the Festival object, a redundancy check need to be performed.
                
                if( !festival.getCommonItemsMap().containsKey(item.getId()) ) {
                	
                	festival.getCommonItemsMap().put(item.getId(), item);               	
                	         	
                }
              
                List<String> typeOfFilm = show.customProperties.get("Type of Film");
                
                // FIXME - The Xml feed has Shows with 'Type Of Film' as 'Shorts Program'. Not ignoring these entries for the moment.
                //if (typeOfFilm == null || !typeOfFilm.contains("Shorts Program")) {
                    
                   // item.getCommonItems().add(item);   // FIXME - WHY ?????? why add to itself ??
                    
                    // Populate the individual lists
                    if(type.equals("Film")) {
                    	festival.getFilms().add(item);
  	
                    } else if(type.equals("Event")) {
                    	festival.getEvents().add(item);
                    	
                    } else if(type.equals("Forum")) {
                    	festival.getForums().add(item);

                    }
               // }

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
                    festival.getSchedules().add(schedule);
                    
                    if (uniqueVenues.add(schedule.getVenue())) { // Added for the first time
                        festival.getVenueLocations().add(getVenueLocation(showing.venue));
                    }
                    
                    for (CommonItem children : item.getCommonItems()) { 
                    	children.getSchedules().add(schedule);

                    }
                   
                    // FIXME - Add the schedule to the main item
                    item.getSchedules().add(schedule);
                }
            }  	
            
            
            // Populate the Films/Events/ Forums by Date depending on the Type
            
            Vector v = null;
            
            if(type.equals("Film")) {
            	v = festival.getFilms();
            } else if(type.equals("Event")) {
            	v = festival.getEvents();
            } else if(type.equals("Forum")) {
            	v = festival.getForums();
            }
            
            for(Object obj : v) {
            	
            	CommonItem item = (CommonItem) obj;
            	
            	// Populate the film dates
            	SortedSet<String> filmDates = null;
            	
            	if(type.equals("Film")) {
            		filmDates = festival.getFilmDates(); 
            	} else if(type.equals("Event")) {
            		filmDates = festival.getEventDates(); 
            	} else if(type.equals("Forum")) {
            		filmDates = festival.getForumDates(); 
            	}
            	
            	 
            	SortedSet<String> dates = getDatesFromCommonItem(item);
            	filmDates.addAll(dates);
            	
            	this.populateItemsByDate(type, dates, item);    	
            }
            
        }
        
        /**
         * Populates the Film/Events/Forums by Date. Required by the layout.
         * 
         * @param type The entity type (Film/Event/Forum)
         * @param dates The 
         * @param item
         */
        private void populateItemsByDate(String type, SortedSet<String> dates, CommonItem item) {
        	
        	// Now populate the Map which stores CommonItems by Dates
        	
        	Map<String, List<CommonItem>> itemsByDateMap = null;
        	
        	if(type.equals("Film")) {
        		itemsByDateMap = festival.getFilmsByDateMap();
        	} else if(type.equals("Event")) {
        		itemsByDateMap = festival.getEventsByDateMap();
        	} else if(type.equals("Forum")) {
        		itemsByDateMap = festival.getForumsByDateMap();
        	}
        	
        	for(String date : dates) {
        		
        		List<CommonItem> itemsList;
        		boolean found = false;
        		
        		if(itemsByDateMap.containsKey(date)) {
        			itemsList = itemsByDateMap.get(date);
        			found = true;
        		} else {
        			itemsList = new ArrayList<CommonItem>();
        		}
        		
        		itemsList.add(item);
        		
        		if(!found) {
        			itemsByDateMap.put(date, itemsList);
        		}       		
        	}     	
        }
    }
}