package com.scenekey.activity.invite_friend.RoomDao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.scenekey.activity.invite_friend.roomdatabasemodel.ContactRoomModel;

import java.util.List;

/**
 * Created by Ravi Birla on 26,September,2019
 */
@Dao
public interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void contactData(List<ContactRoomModel> modelList);

    @Query("SELECT * FROM ContactList WHERE contactid")
    List<ContactRoomModel> getContactList();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void newContactData(ContactRoomModel contactRoomModel);

}
