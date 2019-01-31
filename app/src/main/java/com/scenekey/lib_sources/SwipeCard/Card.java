package com.scenekey.lib_sources.SwipeCard;

import android.graphics.Bitmap;
import android.net.Uri;

import com.scenekey.helper.WebServices;

import java.io.Serializable;

public class Card implements Serializable{
    /*public String name;
    public int imageId;
    public String text;
    public String imageUrl;
    public String userImage;
    public int imageint;
    public int uploadImage;
    public String date;
    public Bitmap bitmap;
    public String user_status;

    public String getUserImage() {
        return (userImage.contains("https:")?userImage: WebServices.USER_IMAGE +userImage);
    }*/

    public String name = "";
    public int imageId;
    public String text = "";
    public String imageUrl = "";
    public String userImage = "";
    public int imageint;
    public String date = "";
    public Bitmap bitmap;
    public int uploadImage;
    public String user_status = "";
    public Uri uri;
    public String type ="";
    public String facebookId ="";
    public String userId ="";

    public String getUserImage() {
        return (userImage.contains("https:")?userImage: WebServices.USER_IMAGE +userImage);
    }
}
