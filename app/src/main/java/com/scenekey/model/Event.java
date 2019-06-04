package com.scenekey.model;

import com.scenekey.helper.WebServices;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mindiii on 24/4/17.
 */

public class Event implements Serializable {
    public String distance;
    public String event_id;
    public String event_name;
    public String category;
    public String description;
    public String category_id;
    public String event_date;
    public String event_time;
    public String rating;
    private String image;
    public Double interval;
    public String status;
    public String trending_point;
    public int strStatus = 0;
    public String returnDay;
    public String isEventLike;
    public String likeCount;
    public int isFeed;


    public ArrayList<ImageSlidModal> imageslideList = new ArrayList<>();
    public ArrayList<KeyInUserModal> keyInUserModalList = new ArrayList<>();


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if(!image.contains("https://s3-us-west-1.amazonaws.com")){
            this.image = WebServices.EVENT_IMAGE+image;
            return;
        }
        this.image = image;
    }


}
