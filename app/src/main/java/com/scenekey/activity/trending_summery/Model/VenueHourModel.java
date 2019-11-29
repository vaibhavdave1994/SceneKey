package com.scenekey.activity.trending_summery.Model;

public class VenueHourModel {

     String day = "";
     String value = "";

    public VenueHourModel(String day, String value) {
        this.day = day;
        this.value = value;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
