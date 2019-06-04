package com.scenekey.model;

import com.google.gson.JsonArray;
import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.fragment.Event_Fragment;
import com.scenekey.helper.Constant;
import com.scenekey.lib_sources.SwipeCard.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class EventDetailsForActivity {

    private ArrayList<Feeds> feedList;
    private ArrayList<EventAttendy> attendyList;
    private Event_Profile_Rating profile_rating;
    private String nudges_count;
    private ArrayList<NotificationData>nudgeModalArrayList;

    private ArrayList<Tags> tagList;

    public ArrayList<Tags> getTagList() {
        return tagList;
    }

    /*public void setTagList(JSONArray tagList,Event_Fragment fragment) throws JSONException {
        this.tagList = new ArrayList<>();
        for(int i = 0; i< tagList.length(); i++){
            JSONObject obj = tagList.getJSONObject(i);
            Tags tags = new Tags();
            tags.id = obj.getString("id");
            tags.tag = obj.getString("tag");
            this.tagList.add(tags);
        }

        fragment.addChips(this.tagList);
    }*/

    public void setTagList(JSONArray tagList, com.scenekey.activity.EventDetailsActivity eventDetailsActivity) throws JSONException {
        this.tagList = new ArrayList<>();
        for(int i = 0; i< tagList.length(); i++){
            JSONObject obj = tagList.getJSONObject(i);
            Tags tags = new Tags();
            tags.id = obj.getString("id");
            tags.tag = obj.getString("tag");
            this.tagList.add(tags);
        }

        //eventDetailsActivity.addChips(this.tagList);
    }


    /*public void setAttendyJson(JSONArray Json,Event_Fragment fragment) throws JSONException {
        if (attendyList != null) attendyList.clear();
        if (attendyList == null) attendyList = new ArrayList<>();
        for (int i = 0; i < Json.length(); i++) {
            EventAttendy attendy = new EventAttendy();
            JSONObject attendyJosn = Json.getJSONObject(i);
            if (attendyJosn.has("username"))
                attendy.username=(attendyJosn.getString("username"));
            if (attendyJosn.has("userFacebookId"))
                attendy.userFacebookId=(attendyJosn.getString("userFacebookId"));
            if (attendyJosn.has("userid")) attendy.userid=(attendyJosn.getString("userid"));
            if (attendyJosn.has("user_status"))
                attendy.user_status=(attendyJosn.getString("user_status"));
            if (attendyJosn.has("usertype"))
                attendy.usertype=(attendyJosn.getString("usertype"));
            if (attendyJosn.has("rating")) attendy.rating=(attendyJosn.getInt("rating") + "");
            if (attendyJosn.has("stagename"))
                attendy.stagename=(attendyJosn.getString("stagename"));
            if (attendyJosn.has("bio"))
                attendy.bio=(attendyJosn.getString("bio"));
            if (attendyJosn.has("userimage"))
                attendy.setUserimage(attendyJosn.getString("userimage"));
            attendyList.add(attendy);
        }
        fragment.setRecyclerView(attendyList);
    }*/


    public void setAttendyJson(JSONArray Json, com.scenekey.activity.EventDetailsActivity eventDetailsActivity) throws JSONException {
        if (attendyList != null) attendyList.clear();
        if (attendyList == null) attendyList = new ArrayList<>();
        for (int i = 0; i < Json.length(); i++) {
            EventAttendy attendy = new EventAttendy();
            JSONObject attendyJosn = Json.getJSONObject(i);
            if (attendyJosn.has("username"))
                attendy.username=(attendyJosn.getString("username"));
            if (attendyJosn.has("userFacebookId"))
                attendy.userFacebookId=(attendyJosn.getString("userFacebookId"));
            if (attendyJosn.has("userid")) attendy.userid=(attendyJosn.getString("userid"));
            if (attendyJosn.has("user_status"))
                attendy.user_status=(attendyJosn.getString("user_status"));
            if (attendyJosn.has("usertype"))
                attendy.usertype=(attendyJosn.getString("usertype"));
            if (attendyJosn.has("rating")) attendy.rating=(attendyJosn.getInt("rating") + "");
            if (attendyJosn.has("stagename"))
                attendy.stagename=(attendyJosn.getString("stagename"));
            if (attendyJosn.has("bio"))
                attendy.bio=(attendyJosn.getString("bio"));
            if (attendyJosn.has("userimage"))
                attendy.setUserimage(attendyJosn.getString("userimage"));
            attendyList.add(attendy);
        }
        eventDetailsActivity.setRecyclerView(attendyList);
    }


    public void setNudgeJson(JSONArray Json,Event_Fragment fragment) throws JSONException {
        if (nudgeModalArrayList != null) nudgeModalArrayList.clear();
        if (nudgeModalArrayList == null) nudgeModalArrayList = new ArrayList<>();
        for (int i = 0; i < Json.length(); i++) {
            NotificationData notificationModal = new NotificationData();
            JSONObject nudgeJosn = Json.getJSONObject(i);

            if (nudgeJosn.has("nudges"))
                notificationModal.nudges=(nudgeJosn.getString("nudges"));
            if (nudgeJosn.has("user_id"))
                notificationModal.user_id=(nudgeJosn.getString("user_id"));
            if (nudgeJosn.has("nudgeId"))
                notificationModal.nudgeId=(nudgeJosn.getString("nudgeId"));
            if (nudgeJosn.has("facebook_id"))
                notificationModal.facebook_id=(nudgeJosn.getString("facebook_id"));
            if (nudgeJosn.has("username"))
                notificationModal.username=(nudgeJosn.getString("username"));
            if (nudgeJosn.has("bio"))
                notificationModal.bio=(nudgeJosn.getString("bio"));
            if (nudgeJosn.has("user_status"))
                notificationModal.user_status=(nudgeJosn.getString("user_status"));
            if (nudgeJosn.has("userimage"))
                notificationModal.userimage=(nudgeJosn.getString("userimage"));
            nudgeModalArrayList.add(notificationModal);
        }
        fragment.setNudgeListinMethod(nudgeModalArrayList);
    }

    public void setFeedsJson(JSONArray Json,EventDetailsActivity eventDetailsActivity) throws JSONException {
        eventDetailsActivity.cardsList.clear();
        if (feedList == null) feedList = new ArrayList<>();
        else feedList.clear();
        for (int i = 0; i < Json.length(); i++) {
            JSONObject feedsJson = Json.getJSONObject(i);
            Feeds feeds = new Feeds();
            if (feedsJson.has("username")) feeds.username=(feedsJson.getString("username"));
            if (feedsJson.has("userid")) feeds.userid=(feedsJson.getString("userid"));
            if (feedsJson.has("userFacebookId"))
                feeds.userFacebookId=(feedsJson.getString("userFacebookId"));
            if (feedsJson.has("event_id")) feeds.event_id=(feedsJson.getString("event_id"));
            if (feedsJson.has("feed_id")) feeds.feedId=(feedsJson.getString("feed_id"));
            if (feedsJson.has("ratetype")) feeds.ratetype=(feedsJson.getString("ratetype"));

            if(feedsJson.has("user_status")) feeds.user_status = feedsJson.getString("user_status");
            if(feedsJson.has("isKeyIn")) feeds.isKeyIn = feedsJson.getString("isKeyIn");

            if (feedsJson.has("userimage"))
                feeds.userimage=(feedsJson.getString("userimage"));
            if (feedsJson.has("type")) feeds.type=(feedsJson.getString("type"));
            if (feedsJson.has("location")) feeds.location=(feedsJson.getString("location"));
            if (feedsJson.has("date")) feeds.date=(feedsJson.getString("date"));
            if (feedsJson.has("feed")) feeds.feed=(feedsJson.getString("feed"));

            //eventDetailsActivity.feedlikeList.clear();
            if(feedsJson.has("feedReaction")) {
                JSONArray jsonArray = feedsJson.getJSONArray("feedReaction");
                for (int r = 0; r < jsonArray.length(); r++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(r);
                    FeedSmily feedSmily = new FeedSmily();
                    if (jsonObject.has("id")) feedSmily.id = (jsonObject.getString("id"));
                    if (jsonObject.has("event_id"))
                        feedSmily.event_id = (jsonObject.getString("event_id"));
                    if (jsonObject.has("feed_id"))
                        feedSmily.feed_id = (jsonObject.getString("feed_id"));
                    if (jsonObject.has("reaction_id"))
                        feedSmily.reaction_id = (jsonObject.getString("reaction_id"));
                    if (jsonObject.has("reaction_count"))
                        feedSmily.reaction_count = (jsonObject.getString("reaction_count"));
                    if (jsonObject.has("reaction"))
                        feedSmily.reaction = (jsonObject.getString("reaction"));
                    if (jsonObject.has("isReaction"))
                        feedSmily.isReaction = (jsonObject.getString("isReaction"));
                    feeds.feedSmilies.add(feedSmily);
//                  eventDetailsActivity.feedlikeList.add(feedSmily);
                }
            }
              Collections.reverse(feeds.feedSmilies);
              ///cREATE lIST AND INSERT vALUE

            if (feeds.type.equals(Constant.FEED_TYPE_PICTURE)) {
                Card card = new Card();
                card.imageUrl = feeds.feed;
                card.userImage = feeds.userimage;
                card.date = feeds.date;
                eventDetailsActivity.cardsList.add(card);
            } else {
                Card card = new Card();
                card.imageUrl = null;
                card.userImage = feeds.userimage;
                card.text = feeds.feed;
                card.date = feeds.date;
                eventDetailsActivity.cardsList.add(card);
            }

            feedList.add(feeds);
        }

        //  fragment.setCardAdapter(fragment.cardsList);

    }

    public void setProfile_ratingJSon(JSONObject JSon,EventDetailsActivity fragment) throws JSONException {
        this.profile_rating = new Event_Profile_Rating();
        if (JSon.has("username"))
            profile_rating.setEvent_rating(JSon.getString("event_rating"));
        if (JSon.has("venue_detail")) {
            profile_rating.setVenue_detail(JSon.getString("venue_detail"));
            // txt_address_i1.setText(profile_rating.getVenue_detail().trim());
        }
        if (JSon.has("venue_id")) profile_rating.setVenue_id(JSon.getString("venue_id"));
        if (JSon.has("venue_lat")) {
            profile_rating.setVenue_lat(JSon.getString("venue_lat"));
            fragment.latitude = Double.parseDouble(profile_rating.getVenue_lat());
        }
        if (JSon.has("venue_long")) {
            profile_rating.setVenue_long(JSon.getString("venue_long"));
            fragment.longitude = Double.parseDouble(profile_rating.getVenue_long());
        }if (JSon.has("venue_address")) {
            profile_rating.setVenue_address(JSon.getString("venue_address"));
        }
        if (JSon.has("description")) {
            profile_rating.setDescription(JSon.getString("description"));
            //txt_discipI_f2.setText(dateSplit[0] + " " + dateSplit[1] + " " + dateSplit[2]);
        }
        if (JSon.has("event_name")) {
            profile_rating.setEvent_name(JSon.getString("event_name"));
            if (profile_rating.getEvent_name().length() < 26){}
                //fragment.txt_event_name.setText(profile_rating.getEvent_name());
            else{}
               // fragment.txt_event_name.setText(profile_rating.getEvent_name().substring(0, 26) + "...");//Changed

        }
        if (JSon.has("interval")) profile_rating.setInterval(JSon.getDouble("interval") + "");
        if (JSon.has("event_date")) profile_rating.setEvent_date(JSon.getString("event_date"));
        if (JSon.has("key_in")) profile_rating.setKey_in(JSon.getString("key_in"));
        if (JSon.has("like")) {
            profile_rating.setLike(JSon.getInt("like") + "");
            if (profile_rating.getLike().equals("1")) {
                //fragment.fabMenu1_like.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.active_like));
            }
        }
    }

    public ArrayList<Feeds> getFeedList() {
        return feedList;
    }

    public ArrayList<EventAttendy> getAttendyList() {
        return attendyList;
    }

    public Event_Profile_Rating getProfile_rating() {
        return profile_rating;
    }

    public String getNudges_count() {
        return nudges_count;
    }

    public void setNudges_count(String nudges_count) {
        this.nudges_count = nudges_count;
    }

    public ArrayList<NotificationData> getNudgeList() {
        return nudgeModalArrayList;
    }
}
