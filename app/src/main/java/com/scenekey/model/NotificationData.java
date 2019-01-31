package com.scenekey.model;

import com.scenekey.helper.WebServices;

import java.io.Serializable;

/**
 * Created by mindiii on 28/2/18.
 */

public class NotificationData implements Serializable {

    public int img;

    public String nudges;
    public String user_id;
    public String facebook_id;
    public String username;
    public String userimage;
    public String bio;
    public String user_status;
    public String nudgeId;

    public boolean message;

    public NotificationData(){
//blank constructor
    }

   /* public NotificationData(int img, String nudges ) {
        this.img = img;
        this.nudges = nudges;
    }*/

    public NotificationData(int img, String nudges ,String username ) {
        this.img = img;
        this.nudges = nudges;
        this.username = username;
    }

    public String getUserImage() {
        return (userimage.contains("https:")?userimage: WebServices.USER_IMAGE +userimage);
    }
}
