package com.scenekey.listener;

import com.scenekey.model.EventAttendy;

import java.util.ArrayList;

public interface RoomListener {
    void getRoomData(int pos,ArrayList<EventAttendy> list,String eventId);
}
