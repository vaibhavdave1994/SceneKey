package com.scenekey.model;

import com.scenekey.helper.WebServices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 2/5/17.
 */

public class EventAttendy implements Serializable {

    public String username;
    public String userid;
    public String userFacebookId;
    public String user_status;
    public String usertype;
    public String rating;
    public String stagename;
    public String bio;
    public String userimage;

    public String getUserimage() {
       // return WebServices.USER_IMAGE+userimage;  old
        return (userimage.contains("https:")?userimage: WebServices.USER_IMAGE +userimage);
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

}
