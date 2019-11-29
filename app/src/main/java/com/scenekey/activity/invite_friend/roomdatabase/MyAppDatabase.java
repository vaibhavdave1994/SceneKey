package com.scenekey.activity.invite_friend.roomdatabase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.scenekey.activity.invite_friend.RoomDao.ContactDao;
import com.scenekey.activity.invite_friend.roomdatabasemodel.ContactRoomModel;

/**
 * Created by Ravi Birla on 26,September,2019
 */
@Database(entities = {ContactRoomModel.class},version = 3, exportSchema = false)
public abstract class MyAppDatabase extends RoomDatabase {
    private static MyAppDatabase mAppDatabase;

    synchronized static MyAppDatabase getDatabaseInstance(Context context) {
        if (mAppDatabase == null) {
            mAppDatabase = Room.databaseBuilder(context, MyAppDatabase.class, "SceneKey")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return mAppDatabase;
    }

    public abstract ContactDao contactDao();
}
