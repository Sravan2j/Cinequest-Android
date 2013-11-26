package edu.sjsu.cinequest.comm.cinequestitem;

public class News extends CinequestItem{
	
	String name;

	String eventImage;
	
	String thumbImage;

	public String getThumbImage() {
		return thumbImage;
	}
	public void setThumbImage(String thumbImage) {
		this.thumbImage = thumbImage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEventImage() {
		return eventImage;
	}
	public void setEventImage(String eventImage) {
		this.eventImage = eventImage;
	}
//	public String getInfoLink() {
//		return infoLink;
//	}
//	public void setInfoLink(String infoLink) {
//		this.infoLink = infoLink;
//	}
	

}
