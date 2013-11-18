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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Festival;
import edu.sjsu.cinequest.comm.cinequestitem.Film;
import edu.sjsu.cinequest.comm.cinequestitem.ProgramItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.Show;
import edu.sjsu.cinequest.comm.cinequestitem.Showing;
import edu.sjsu.cinequest.comm.cinequestitem.Venue;
import edu.sjsu.cinequest.comm.cinequestitem.VenueLocation;

/**
 * Parses the complete information of the Festival
 */
public class FestivalParser extends BasicHandler {
    private List<Show> shows = new ArrayList<Show>();
    private String currentBlock = "";
    private Show show;
    private Showing showing;
    private String propName;

    /**
     * Parses the complete Festival information
     * @param url
     * @param callback
     * @return parsed festival
     * @throws SAXException
     * @throws IOException
     */
    public static Festival parseFestival(String url, Callback callback) throws SAXException, IOException {
    	Log.e("FestivalParser.java", "Within parseFestival(), url:" + url);
    	
        List<Show> shows = parseShows(url, callback);
        
        // Parse the list of Venues using the Venue XML feed.
        Map<String, Venue> venues = VenuesParser.parse("http://www.cinequest.org/venuelist.php", callback);
        
        Log.e("FestivalParser.java", "Parsed Shows, Size:" + shows.size());
        return new FestivalConverter(shows, venues).convert();
    }

    public static List<Show> parseShows(String url, Callback callback) throws SAXException, IOException {
        FestivalParser handler = new FestivalParser();        
        Platform.getInstance().parse(url, handler, callback);
        return handler.getShows();
    }

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

    public static class FestivalConverter {
        private List<Show> shows;
        private Map<String, Venue> venues;
        private Festival festival = new Festival();
        //private Map<String, ArrayList<Film>> shortFilms = new LinkedHashMap<String, ArrayList<Film>>();
        
        private Map<Integer, Film> shortsMap = new LinkedHashMap<Integer, Film>();


        public FestivalConverter(List<Show> shows, Map<String, Venue> venues) {
            this.shows = shows;
            this.venues = venues;
        }

        private static Pattern parentTitlePattern = Pattern.compile("(Part of|Plays (with|before) the feature film) ([^.]+)\\..*");

        private static String getParentTitle(String description) {
            Matcher matcher = parentTitlePattern.matcher(description);
            return matcher.matches() ? matcher.group(3) : null;
        }

        public Festival convert() {
            Set<String> uniqueVenues = new HashSet<String>();

            // Remove the partial shows from shows
            // Add them to partialShows, grouped by their title

            Iterator<Show> iter = shows.iterator();
            while (iter.hasNext()) {
                Show show = iter.next();
                if (show.currentShowings.size() == 0) {
                    iter.remove();
                    
                    // Collect all the Short Films. This list will be used to add each ShortFilm to its matching ProgramItem.
                    Film shortFilm = getFilm(show);
                    
                    if( !shortsMap.containsKey(shortFilm.getId())) {
                    	shortsMap.put(shortFilm.getId(), shortFilm);
                    }
                    
                    // Older logic to collect ShortFilms
                   /* String title = getParentTitle(show.shortDescription);
                    if (title != null) {
                        ArrayList<Film> filmsWithTitle = shortFilms.get(title);
                        if (filmsWithTitle == null) {
                            filmsWithTitle = new ArrayList<Film>();
                            shortFilms.put(title, filmsWithTitle);
                        }
                        filmsWithTitle.add(getFilm(show));
                    }*/
                }
            }

            for (Show show : shows) {
                ProgramItem item = getProgramItem(show);
                festival.getProgramItems().add(item);
                List<String> typeOfFilm = show.customProperties.get("Type of Film");
                if (typeOfFilm == null || !typeOfFilm.contains("Shorts Program")) {
                    Film film = getFilm(show);
                    item.getFilms().add(film);
                    festival.getFilms().add(film);
                }

                // Now, find out if this ProgramItem has a list of ShortFilms associated with it.
                List<String> associatedShortIds = show.customProperties.get("ShortID");
                
                if(associatedShortIds != null && associatedShortIds.size() > 0) {
                	
                	// So, this ProgramItem has a list of associated ShortFilms.
                	
                	for(String shortID : associatedShortIds) {
                		
                		// The required ShortFilm exists in the Map. Retrieve it and add to the ProgramItem.
                		if(shortsMap.containsKey(Integer.parseInt(shortID))) {
                			
                			Film film = shortsMap.get(Integer.parseInt(shortID));
                			
                			item.getFilms().add(film);
                            festival.getFilms().add(film);
                		}		
                	}       	
                }
                
                // Older logic to add the ShortFilm to its matching ProgramItem.
                /*if (shortFilms.keySet().contains(item.getTitle())) {
                    for (Film film : shortFilms.get(item.getTitle())) {
                        item.getFilms().add(film);
                        festival.getFilms().add(film);
                    }
                }*/

                for (Showing showing : show.currentShowings) {
                    Schedule schedule = getSchedule(showing, item);
                    festival.getSchedules().add(schedule);
                    if (uniqueVenues.add(schedule.getVenue())) // Added for the first time
                        festival.getVenueLocations().add(getVenueLocation(showing.venue));
                    for (Film film : item.getFilms()) film.getSchedules().add(schedule);
                }
            }
            return festival;
        }

        private static String venueAbbr(String name) {
        	Log.e("FestivalPArser.java", "VenueAbbr=" + name);
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

        private Schedule getSchedule(Showing showing, ProgramItem item) {
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
            	Log.e("FestivalPArser.java", "Venue ID:"  + showing.venue.id);
            	Log.e("FestivalParser.java", "Location:" + venues.get(showing.venue.id).location);
            } else {
            	// Else use the older logic to compute the venue abbreviation.
            	schedule.setVenue(venueAbbr(showing.venue.name));
            }
            
            return schedule;
        }

        private ProgramItem getProgramItem(Show show) {
            ProgramItem item = new ProgramItem();
            item.setId(Integer.parseInt(show.id));
            item.setTitle(show.name);
            item.setDescription(show.shortDescription);
            item.setInfoLink(show.infoLink);
            return item;
        }

        private Film getFilm(Show show) {
            Film film = new Film();
            /* TODO: tagline and filmInfo seem unused
             * TODO: What should we do with the executive producers?
             */
            film.setId(Integer.parseInt(show.id));
            film.setTitle(show.name);
            film.setDescription(show.shortDescription);
            film.setImageURL(show.thumbImageURL);
            film.setDirector(get(show.customProperties, "Director"));
            film.setProducer(get(show.customProperties, "Producer"));
            film.setCinematographer(get(show.customProperties, "Cinematographer"));
            film.setEditor(get(show.customProperties, "Editor"));
            film.setCast(get(show.customProperties, "Cast"));
            film.setCountry(get(show.customProperties, "Production Country"));
            film.setLanguage(get(show.customProperties, "Language"));
            film.setGenre(get(show.customProperties, "Genre"));
            film.setInfoLink(show.infoLink);
            return film;
        }

        private static String get(Map<String, ArrayList<String>> custom, String key) {
            ArrayList<String> value = custom.get(key);
            if (value == null) return "";
            if (value.size() == 1) return value.get(0);
            String result = value.toString();
            return result.substring(1, result.length() - 1);
        }
    }
}