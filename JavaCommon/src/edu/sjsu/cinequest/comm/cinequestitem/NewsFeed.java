package edu.sjsu.cinequest.comm.cinequestitem;

import java.util.ArrayList;
import java.util.List;

public class NewsFeed extends CinequestItem{
	
	String lastUpdated;

	List<News> newsList ;
	
	public NewsFeed() {
		newsList = new ArrayList<News>();
	}
	
	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public List<News> getNewsList() {
		return newsList;
	}

}
