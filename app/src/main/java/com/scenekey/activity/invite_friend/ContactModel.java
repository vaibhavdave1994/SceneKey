package com.scenekey.activity.invite_friend;

import androidx.room.Entity;

import android.net.Uri;

/**
 * Created by Ravi Birla on 10,September,2019
 */
public class ContactModel {
    public String id;
    public String name;
    public String mobileNumber;
    public String photo;
    public Uri photoURI;
    public int flag;

    public ContactModel(String id, String name,  String photo,String mobileNumber) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.photo = photo;

    }

    public boolean isIsselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public boolean isselect = false;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ContactModel))
            return false;

        return name.equals(((ContactModel) obj).name);
    }

    @Override
    public int hashCode() {
        return (name == null) ? 0 : name.hashCode();
    }
}