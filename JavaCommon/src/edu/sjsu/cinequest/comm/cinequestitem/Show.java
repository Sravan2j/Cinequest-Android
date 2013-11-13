package edu.sjsu.cinequest.comm.cinequestitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Show {
    public String id;
    public String name;
    public int duration;
    public String shortDescription;
    public String thumbImageURL;
    public String eventImageURL;
    public String infoLink;
    public Map<String, ArrayList<String>> customProperties = new HashMap<String, ArrayList<String>>();
    public List<Showing> currentShowings = new ArrayList<Showing>();
}
