package com.scenekey.listener;


import com.scenekey.model.Events;

/**
 * Created by mindiii on 29/11/18.
 */

public interface CheckEventStatusListener {
    void getCheckEventStatusListener(String eventName,String event_id, String venue_name, Events object, String[] currentLatLng, String[] strings);
}
