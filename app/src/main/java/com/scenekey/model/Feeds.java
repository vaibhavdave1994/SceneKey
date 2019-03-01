package com.scenekey.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mindiii on 15/2/18.
 */

public class Feeds implements Serializable {

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
}
