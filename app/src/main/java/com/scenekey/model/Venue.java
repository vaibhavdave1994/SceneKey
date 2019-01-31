package com.scenekey.model;

import com.scenekey.helper.WebServices;

import java.io.Serializable;

/**
 * Created by mindiii on 24/4/17.
 */

public class Venue implements Serializable {
    private String venue_id;
    private String venue_name;
    private String image;
    private String category;
    private String category_id;
    private String address;
    private String city;
    private String region;
    private String country;
    private String state;
    private String latitude;
    private String longitude;
    private int     rating;
    private boolean validate;


    public String getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(String venue_id) {
        this.venue_id = venue_id;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_name) {
        this.venue_name = venue_name.trim();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if(!image.contains("https://s3-us-west-1.amazonaws.com")){
            this.image = WebServices.VENUE_IMAGE+image;
        return;}
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}
