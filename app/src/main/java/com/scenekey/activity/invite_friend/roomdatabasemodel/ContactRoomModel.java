package com.scenekey.activity.invite_friend.roomdatabasemodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by Ravi Birla on 26,September,2019
 */
@Entity(tableName = "ContactList")
public class ContactRoomModel {

    public boolean isselect = false;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "contactid")
    public String contactid;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "mobileNumber")
    public String mobileNumber;
    @ColumnInfo(name = "photo")
    public String photo;
    @ColumnInfo(name = "flag")
    public int flag;

    public ContactRoomModel(String contactid, String name, String mobileNumber, String photo, int flag) {
        this.contactid = contactid;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.photo = photo;
        this.flag = flag;
    }

    public String getContactid() {
        return contactid;
    }

    public void setContactid(String contactid) {
        this.contactid = contactid;
    }

    public boolean isIsselect() {
        return isselect;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
