package com.scenekey.model;

import com.scenekey.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mindiii on 24/4/17.
 */

public class Events implements Serializable {
    //For Trending Adapter
    public String timeFormat;
    public String remainingTime;
    public boolean isOngoing;
    private Venue venue;
    private ArrayList<Artists> artistsArrayList;
    private Event event;

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public ArrayList<Artists> getArtistsArrayList() {
        return artistsArrayList;
    }

    public void setArtistsArrayList(ArrayList<Artists> artistsArrayList) {
        this.artistsArrayList = artistsArrayList;
    }

    public void setVenueJSON(JSONObject venue) throws JSONException {
        Venue v = new Venue();
        if (venue.has("venue_id")) v.setVenue_id(venue.getString("venue_id"));
        if (venue.has("venue_name")) v.setVenue_name(venue.getString("venue_name"));
        if (venue.has("image")) v.setImage(venue.getString("image"));
        if (venue.has("category")) v.setCategory(venue.getString("category"));
        if (venue.has("category_id")) v.setCategory_id(venue.getString("category_id"));
        if (venue.has("address")) v.setAddress(venue.getString("address"));
        if (venue.has("city")) v.setCity(venue.getString("city"));
        if (venue.has("region")) v.setRegion(venue.getString("region"));
        if (venue.has("country")) v.setCountry(venue.getString("country"));
        if (venue.has("latitude")) v.setLatitude(venue.getString("latitude"));
        if (venue.has("longitude")) v.setLongitude(venue.getString("longitude"));
        if (venue.has("rating")) v.setRating(venue.getInt("rating"));
        if (venue.has("is_tag_follow")) v.setIs_tag_follow(venue.getString("is_tag_follow"));
        if (venue.has("biz_tag_id")) v.setBiz_tag_id(venue.getString("biz_tag_id"));
        this.venue = v;
    }

    public void setArtistsArray(JSONArray artists) throws JSONException {
        if (this.artistsArrayList == null) artistsArrayList = new ArrayList<>();
        for (int i = 0; i < artists.length(); i++) {
            Artists artists1 = new Artists();
            JSONObject object = artists.getJSONObject(i);
            if (object.has("artist_id")) artists1.setArtist_id(object.getString("artist_id"));
            if (object.has("artist_name")) artists1.setArtist_name(object.getString("artist_name"));
            if (object.has("rating")) artists1.setRating(object.getString("rating"));
            artistsArrayList.add(artists1);
        }
    }

    public void setEventJson(JSONObject events) throws JSONException {
        Event event = new Event();
        if (events.has("distance")) event.distance = (events.getString("distance"));
        if (events.has("event_id")) event.event_id = (events.getString("event_id"));
        if (events.has("event_name")) event.event_name = (events.getString("event_name"));
        if (events.has("category")) event.category = (events.getString("category"));
        if (events.has("description")) event.description = (events.getString("description"));
        if (events.has("category_id")) event.category_id = (events.getString("category_id"));
        if (events.has("event_date")) event.event_date = (events.getString("event_date"));
        if (events.has("event_time")) event.event_time = (events.getString("event_time"));
        if (events.has("rating")) event.rating = (events.getString("rating"));
        if (events.has("image")) event.setImage(events.getString("image"));

        if (events.has("feedPost")) {
            JSONArray feedPost = events.getJSONArray("feedPost");

            for (int i = 0; i < feedPost.length(); i++) {

                JSONObject jsonObject = feedPost.getJSONObject(i);

                ImageSlidModal imageSlidModal = new ImageSlidModal();

                imageSlidModal.id = jsonObject.getString("id");
                imageSlidModal.feed_image = jsonObject.getString("feed_image");

                event.imageslideList.add(imageSlidModal);
            }
        }

        if (events.has("isFeed")) event.isFeed = (events.getInt("isFeed"));

        if (events.has("keyInUser")) {
            JSONArray feedPost = events.getJSONArray("keyInUser");

            for (int i = 0; i < feedPost.length(); i++) {

                JSONObject jsonObject = feedPost.getJSONObject(i);

                KeyInUserModal keyInUserModal = new KeyInUserModal();


                keyInUserModal.userid = jsonObject.getString("userid");
                keyInUserModal.userImage = jsonObject.getString("userImage");
                keyInUserModal.userName = jsonObject.getString("userName");
                keyInUserModal.bio = jsonObject.getString("bio");
                keyInUserModal.userFacebookId = jsonObject.getString("userFacebookId");
                keyInUserModal.keyIn = jsonObject.getString("keyIn");

                event.keyInUserModalList.add(keyInUserModal);
            }
        }


        if (events.has("interval")) {
            try {
                event.interval = (events.getDouble("interval"));
            } catch (JSONException e) {
                try {
                    event.interval = (Double.valueOf("0" + (events.getString("interval")).replace("Hour", "")));
                } catch (Exception ee) {
                    e.printStackTrace();
                }
            }
        }
        if (events.has("isEventLike")) event.isEventLike = (events.getString("isEventLike"));
        if (events.has("likeCount")) event.likeCount = (events.getString("likeCount"));
        if (events.has("status")) event.status = (events.getString("status"));
        if (events.has("trending_point"))
            event.trending_point = (events.getString("trending_point"));
        this.event = event;
    }


    public Event getEvent() {
        return this.event;
    }

    //For trending Adapter

    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }

    public void settimeFormat(int time_format) {
        timeFormat = convertDate(event.event_date, time_format);
    }

    public void setRemainingTime() {
        try {
            remainingTime = convertTime(event.event_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param date date of the event check format before use tie
     * @return
     * @throws ParseException
     */
    public boolean checkWithTime(final String date, Double interval) throws ParseException {
        String[] dateSplit = (date.replace("TO", "T")).replace(" ", "T").split("T");
        Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(dateSplit[0] + " " + dateSplit[1]);
        Date endTime = new Date(startTime.getTime() + (int) (interval * 60 * 60 * 1000));
        // Util.printLog("TrendingAdapter ",startTime +"  : "+endTime);
        long currentTime = java.util.Calendar.getInstance().getTime().getTime();
        /*if (currentTime < endTime.getTime() && currentTime > startTime.getTime()) {
            return true;
        }*/
        if (currentTime > startTime.getTime()) {
            return true;
        }
        return false;
    }

    private String convertDate(String date, int time_format) {
        String[] str;
        str = date.split("TO");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = format.parse(str[0].replace("T", " "));

            if (time_format == 12) {
                return new SimpleDateFormat("MMM dd, yyyy hh:mm ").format(date1) + (date1.getHours() < 12 ? " AM" : " PM");
            } else {
                return new SimpleDateFormat("MMM dd, yyyy HH:mm ").format(date1);

            }

        } catch (ParseException e) {
            Utility.e("Error time ", e.toString());
            return date;
        }

    }

    private String convertTime(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(time);
        Date date2 = new Date();
        int milis = Math.abs(date2.getHours() - date1.getHours());
        if (milis == 1) {
            milis = 60 - Math.abs(date2.getMinutes() - date1.getMinutes());
            return milis + " min";
        }
        return milis + " hr";
    }
}
