package com.scenekey.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scenekey.helper.WebServices;
import com.scenekey.util.Utility;

/**
 * Created by mindiii on 15/2/18.
 */

public class EventsBTag implements Serializable {


    @SerializedName("venue")
    @Expose
    private Venue venue;
    @SerializedName("events")
    @Expose
    private Events events;

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    //temporary
    public boolean isOngoing;
    public String timeFormat;
    public String remainingTime;

    public void setTimeFormat(){
        timeFormat = convertDate(events.eventDate);
    }
    public void setRemainingTime(){
        try {
            remainingTime = convertTime(events.eventTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }

    private String convertDate(String date) {
        String[] str;
        str = date.split("TO");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = format.parse(str[0].replace("T"," "));
            return new SimpleDateFormat("MMM dd,yyyy hh:mm ").format(date1)+( date1.getHours()<12 ? " AM" : " PM");

        } catch (ParseException e) {
            Utility.e("Error time ",e.toString());
            return date;
        }

    }

    private String convertTime(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(time);
        Date date2 = new Date();
        int milis = Math.abs(date2.getHours() - date1.getHours());
        if(milis==1){
            milis = 60 - Math.abs(date2.getMinutes() - date1.getMinutes());
            return milis + " min";
        }
        return milis + " hr";
    }


    public class Events {

        @SerializedName("distance")
        @Expose
        private String distance;
        @SerializedName("event_id")
        @Expose
        private Integer eventId;
        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("event_date")
        @Expose
        private String eventDate;
        @SerializedName("event_time")
        @Expose
        private String eventTime;
        @SerializedName("rating")
        @Expose
        private Integer rating;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("interval")
        @Expose
        private String interval;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("tags")
        @Expose
        private List<String> tags = null;

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public Integer getEventId() {
            return eventId;
        }

        public void setEventId(Integer eventId) {
            this.eventId = eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public String getEventTime() {
            return eventTime;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getImage() {
            return WebServices.EVENT_IMAGE+image;
        }

        public void setImage(String image) {
                this.image = image;
        }


        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

    }

    public class Venue {

        @SerializedName("venue_id")
        @Expose
        private Integer venueId;
        @SerializedName("venue_name")
        @Expose
        private String venueName;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("region")
        @Expose
        private String region;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("rating")
        @Expose
        private Integer rating;

        public Integer getVenueId() {
            return venueId;
        }

        public void setVenueId(Integer venueId) {
            this.venueId = venueId;
        }

        public String getVenueName() {
            return venueName;
        }

        public void setVenueName(String venueName) {
            this.venueName = venueName;
        }

        public String getImage() {
            return WebServices.VENUE_IMAGE+image;
        }

        public void setImage(String image) {
                this.image = image;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

    }
}
