package com.scenekey.helper;

import com.scenekey.model.Events;
import com.scenekey.util.Utility;

import java.util.Comparator;

/**
 * Created by mindiii on 16/2/18.
 */

public class SortByPoint implements Comparator<Events> {
    @Override
    public int compare(Events p1, Events p2) {
        int a= Integer.parseInt(p1.getEvent().trending_point);
        int b= Integer.parseInt(p2.getEvent().trending_point);
        int i= b-a;


        //Utility.e("check", p1.getEvent().trending_point+" event name"+p1.getEvent().event_name);
        return (i==0)?p1.getEvent().distance.compareTo(p2.getEvent().distance):i;
    }
}
