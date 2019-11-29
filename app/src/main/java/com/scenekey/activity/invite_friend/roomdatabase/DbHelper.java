package com.scenekey.activity.invite_friend.roomdatabase;

import com.scenekey.activity.invite_friend.roomdatabasemodel.ContactRoomModel;

import java.util.List;

/**
 * Created by Ravi Birla on 26,September,2019
 */
public interface DbHelper {

    void contactData(List<ContactRoomModel> modelList);
    List<ContactRoomModel> getContactList();
    void newContactData(ContactRoomModel contactRoomModel);


}
