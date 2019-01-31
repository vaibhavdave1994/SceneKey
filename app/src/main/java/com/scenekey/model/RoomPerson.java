package com.scenekey.model;

/**
 * Created by mindiii on 26/2/18.
 */

public class RoomPerson {

    public String android_version_name;
    public String android_image_url;
    public String status;

    public RoomPerson(String android_version_name, String android_image_url, String status) {
        this.android_version_name=android_version_name;
        this.android_image_url=android_image_url;
        this.status=status;
    }
}
