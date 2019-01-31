package com.scenekey.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mindiii on 15/2/18.
 */

public class Feeds implements Serializable {
    /**
     * username : Shubh
     * userid : 1203
     * userFacebookId : 1203
     * event_id : 1055666
     * ratetype :
     * user_status : 2
     * isKeyIn : 1
     * userimage : dev-1203/5AAE4C2B-A398-49E6-B615-325010CBF3A2.JPG
     * type : Comment
     * location : Indore, Madhya Pradesh, India
     * date : 2018-12-03 01:45:04
     * bio : hgfg
     * feed : Hello
     */

    public String username;
    public String userid;
    public String userFacebookId;
    public String event_id;
    public String ratetype;
    public String feedId;
    public String user_status;
    public String isKeyIn;
    public String userimage;
    public String type;
    public String location;
    public String date;
    public String bio;
    public String feed;

    public String event_name;
    public  boolean isUri;

    public ArrayList<FeedSmily> feedSmilies = new ArrayList<>();

   /* public  String id;
    public  String eventid;
    public  String feed_id;
    public  String reaction_id;
    public  String reaction_count;
    public  String reaction;
    public  String isReaction;*/

    /*public String username;
    public String userid;
    public String userFacebookId;
    public String event_id;
    public String ratetype;
    public String userimage;
    public String type;
    public String location;
    public String date;
    public String feed;

    *//**
     * In list of user attended event
     *//*
    public String event_name;*/


}
