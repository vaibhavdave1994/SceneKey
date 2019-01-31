package com.scenekey.model;

import java.io.Serializable;

/**
 * Created by mindiii on 1/3/18.
 */

public class Event_Profile_Rating implements Serializable {

    private String event_rating;
    private String venue_detail;
    private String venue_id;
    private String venue_lat;
    private String venue_long;
    private String description;
    private String event_name;
    private String interval;
    private String event_date;
    private String key_in;
    private String like;
    private String date_in_format;
    private String venue_address;

    public String getEvent_rating() {
        return event_rating;
    }

    public void setEvent_rating(String event_rating) {
        this.event_rating = event_rating;
    }

    public String getVenue_detail() {
        return venue_detail;
    }

    public void setVenue_detail(String venue_detail) {
        this.venue_detail = venue_detail.trim();
    }

    public String getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(String venue_id) {
        this.venue_id = venue_id;
    }

    public String getVenue_lat() {
        return venue_lat;
    }

    public void setVenue_lat(String venue_lat) {
        this.venue_lat = venue_lat;
    }

    public String getVenue_long() {
        return venue_long;
    }

    public void setVenue_long(String venue_long) {
        this.venue_long = venue_long;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {

        this.interval = interval.replace("-","");
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        setDate_in_format(event_date.replace("To","T").replace("T"," ").split("\\s+")[0]);
        this.event_date = event_date;
    }

    public String getKey_in() {
        return key_in;
    }

    public void setKey_in(String key_in) {
        this.key_in = key_in;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDate_in_format() {
        return date_in_format;
    }

    public void setDate_in_format(String date_in_format) {
        this.date_in_format = date_in_format;
    }

    public String getVenue_address() {
        return (venue_address==null?"":venue_address);
    }

    public void setVenue_address(String venue_address) {
        this.venue_address = venue_address;
    }

}
