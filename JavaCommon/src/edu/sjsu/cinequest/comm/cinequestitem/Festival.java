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

package edu.sjsu.cinequest.comm.cinequestitem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.Log;

/**
 * Festival class represents the complete information of the Festival
 *
 * @author Snigdha Mokkapati
 * @author Rohit Vobbilisetty
 *
 * @version 1.0
 */

public class Festival implements Serializable {

	private List<CommonItem> films;
	private List<CommonItem> events;
    private List<CommonItem> videos;
    private List<CommonItem> trending;
    private SortedMap<String, SortedSet<Schedule>> schedulesByDateMap = new TreeMap<String, SortedSet<Schedule>>();

    private Map<Integer, CommonItem> commonItemsMap;
	public Map<Integer, CommonItem> getCommonItemsMap() { return commonItemsMap; }

    /**
     * Gets all items grouped by date
     * @return all items, grouped by date, and sorted by time and then title
     */
	public SortedMap<String, SortedSet<Schedule>> getSchedulesByDate() {
        return schedulesByDateMap;
	}

	public List<CommonItem> getFilms() { return films; }
	public List<CommonItem> getEvents() { return events; }
	public List<CommonItem> getVideos() { return videos; }
    public List<CommonItem> getTrending() { return trending; }

    public Festival()
	{
		commonItemsMap = new HashMap<Integer, CommonItem>();
		films = new ArrayList<CommonItem>();
		events = new ArrayList<CommonItem>();
        videos = new ArrayList<CommonItem>();
        trending = new ArrayList<CommonItem>();
        schedulesByDateMap = new TreeMap<String, SortedSet<Schedule>>();
    }
	
	public boolean isEmpty() { return schedulesByDateMap.size() == 0; }

    /**
     * Sets the trending items for this festival.
     * @param ids the ids of the trending items.
     */
    public void setTrendingIds(int[] ids) {
        trending.clear();
        for (Integer id : ids) {
            trending.add(commonItemsMap.get(id));
        }
    }

    public void addItem(CommonItem item, String type)
    {
        if ("Film".equals(type)) films.add(item);
        if ("Event".equals(type)) events.add(item);
        commonItemsMap.put(item.getId(), item);
    }

    public void addSchedule(Schedule schedule)
    {
        String date = schedule.getDate();
        SortedSet<Schedule> schedulesOnDate = schedulesByDateMap.get(date);
        if (schedulesOnDate == null) {
            schedulesOnDate = new TreeSet<Schedule>();
            schedulesByDateMap.put(date, schedulesOnDate);
        }
        schedulesOnDate.add(schedule);
    }
}