package com.scenekey.model;

import androidx.room.Query;

import com.scenekey.helper.Constant;
import com.scenekey.helper.WebServices;

import java.io.Serializable;

/**
 * Created by mindiii on 31/1/18.
 */

public class UserInfo implements Serializable {

    // New Code
    public String userid ="";
    public String userFacebookId ="";
    public String socialType ="";
    public String userName ="";
    public String userEmail ="";
    public String mauticContactId ="";
    public String fullname ="";
    public String lastName ="";
    public String password ="";
    public String userImage ;
    public String age ="";
    public String dob ="";
    public String gender ="";
    public String userDeviceId ="";
    public String deviceType ="";
    public String userGender ="";
    public String userStatus = "";
    public String userLastLogin  = "";
    public String registered_date  = "";
    public String usertype  = "";
    public String artisttype  = "";
    public String stagename  = "";
    public String venuename  = "";
    public String address  = "";
    public String fullAddress  = "";
    public String lat  = "";
    public String longi  = "";
    public String adminLat  = "";
    public String adminLong  = "";
    public String user_status  = "";
    public String makeAdmin  = "";
    public String key_points  = "";
    public String bio  = "";
    public String appBadgeCount  = "";
    public String loginstatus = "";
    public boolean socialImageChanged = false;
    public  byte[] byteArray;

    public String environment  = "";
    public String userAccessToken  = "";

    public String currentDate  = "";

    public boolean firstTimeDemo;
    public boolean currentLocation;

    public String getUserImage() {

        if(userImage != null){
            if (!userImage.contains(Constant.DEV_TAG)) {
                userImage = Constant.DEV_TAG + userImage;
            }
            return (userImage.contains("https:") ? userImage : WebServices.USER_IMAGE + userImage);
        }else return WebServices.USER_IMAGE;
        //return (userImage.contains("https:") ? userImage : WebServices.USER_IMAGE + userImage);
    }
}

