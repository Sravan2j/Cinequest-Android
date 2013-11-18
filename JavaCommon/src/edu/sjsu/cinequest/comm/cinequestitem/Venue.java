package edu.sjsu.cinequest.comm.cinequestitem;

public class Venue {
    public String id;
    public String name;
    public String address;

    public boolean equals(Object other) {
        return id.equals(((Venue) other).id);
    }
    
    public String shortName;
    public String location;
}
