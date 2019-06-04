package com.scenekey.model;

import com.scenekey.helper.WebServices;

import java.io.Serializable;

public class KeyInUserModal implements Serializable {

    public String userid;
    public String userImage;
    public String userName;
    public String bio;
    public String userFacebookId;
    public String keyIn;


    public String getUserimage() {
        return (userImage.contains("https:")?userImage: WebServices.USER_IMAGE +userImage);
    }
}
