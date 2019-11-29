package com.scenekey.activity.invite_friend.roomdatabase;

import android.content.Context;

import com.scenekey.activity.invite_friend.roomdatabasemodel.ContactRoomModel;

import java.util.List;

/**
 * Created by Ravi Birla on 26,September,2019
 */
public class AppDbHelper implements DbHelper {

    private static AppDbHelper instance;

    private final MyAppDatabase myAppDatabase;

    private AppDbHelper(Context context) {
        this.myAppDatabase = MyAppDatabase.getDatabaseInstance(context);
    }

    public synchronized static DbHelper getDbInstance(Context context) {
        if (instance == null) {
            instance = new AppDbHelper(context);
        }
        return instance;
    }

    @Override
    public void contactData(List<ContactRoomModel> modelList) {
        myAppDatabase.contactDao().contactData(modelList);
    }

    @Override
    public List<ContactRoomModel> getContactList() {
        return myAppDatabase.contactDao().getContactList();
    }

    @Override
    public void newContactData(ContactRoomModel contactRoomModel) {
        myAppDatabase.contactDao().newContactData(contactRoomModel);

    }
}