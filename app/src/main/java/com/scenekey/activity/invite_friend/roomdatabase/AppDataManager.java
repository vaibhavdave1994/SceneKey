package com.scenekey.activity.invite_friend.roomdatabase;

import android.content.Context;

import com.scenekey.activity.invite_friend.roomdatabasemodel.ContactRoomModel;

import java.util.List;

/**
 * Created by Ravi Birla on 26,September,2019
 */
public class AppDataManager implements DataManager {
    private static AppDataManager instance;
    private final DbHelper mDbHelper;

    private AppDataManager(Context context) {
        mDbHelper = AppDbHelper.getDbInstance(context);
        //Gson mGson = new GsonBuilder().create();
    }

    public synchronized static AppDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppDataManager(context);
        }
        return instance;
    }

    @Override
    public void contactData(List<ContactRoomModel> modelList) {
        mDbHelper.contactData(modelList);
    }

    @Override
    public List<ContactRoomModel> getContactList() {
        return mDbHelper.getContactList();
    }

    @Override
    public void newContactData(ContactRoomModel contactRoomModel) {
        mDbHelper.newContactData(contactRoomModel);
    }
}