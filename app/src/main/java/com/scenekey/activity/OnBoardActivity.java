package com.scenekey.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.adapter.Search_tag_Adapter;
import com.scenekey.adapter.VenueBoardAdapter;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.Pop_Up_Option_Follow_Unfollow;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.model.Event;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.EventDetailsForActivity;
import com.scenekey.model.Events;
import com.scenekey.model.Feeds;
import com.scenekey.model.KeyInUserModal;
import com.scenekey.model.SearchTagModal;
import com.scenekey.model.TagModal;
import com.scenekey.model.Venue;
import com.scenekey.model.VenueBoard;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OnBoardActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<EventAttendy> attendyList;
    private TextView onBoard_txt_event_name;
    private ImageView img_eventDetail_back;
    private Utility utility;
    private RelativeLayout container;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardList;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardListSpecial;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardListHappyHour;
    private ArrayList<VenueBoard.EventTagBean> venuBoardEventTagBeanList;
    private VenueBoardAdapter venueBoardAdapter;
    private RecyclerView venuRecyclerView;
    private ArrayList<VenueBoard.EventTagBean.TagListBean> venuBoardCatList;
    Event event;
    Venue venue;
    private String[] currentLatLng;
    Events object;
    private ImageView iv_tag__special_image,img_no_member,iv_group;
    private TextView tag__vanue_name;
    RelativeLayout venuName;
    boolean fromTrending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        inItView();
    }

    private void inItView() {
        utility = new Utility(OnBoardActivity.this);
        venuBoardList = new ArrayList<>();
        venuBoardCatList = new ArrayList<>();

        container = findViewById(R.id.container);
        onBoard_txt_event_name = findViewById(R.id.onBoard_txt_event_name);
        img_eventDetail_back = findViewById(R.id.img_eventDetail_back);
        venuRecyclerView = findViewById(R.id.venuRecyclerView);
        img_no_member = findViewById(R.id.img_no_member);
        venuName = findViewById(R.id.venuName);
        venuName.setVisibility(View.GONE);
        iv_group = findViewById(R.id.iv_group);
        img_no_member.setOnClickListener(this);
        iv_group.setOnClickListener(this);
        iv_tag__special_image = findViewById(R.id.iv_tag__special_image);
        tag__vanue_name = findViewById(R.id.tag__vanue_name);

//        if (getIntent().getSerializableExtra("event") != null) {
//           Event event = (Event) getIntent().getSerializableExtra("event");
//            //onBoard_txt_event_name.setText("" + event.event_name);
//        }

        img_eventDetail_back.setOnClickListener(this);

        if (getIntent().getSerializableExtra("eventid") != null) {
            Event event = (Event) getIntent().getSerializableExtra("eventid");
            this.event = event;
            Venue venuid = (Venue) getIntent().getSerializableExtra("venuid");
            venue = venuid;
            object = (Events) getIntent().getSerializableExtra("object");
            currentLatLng = (String[]) getIntent().getSerializableExtra("currentLatLng");
            onBoard_txt_event_name.setText("" + event.event_name);
            getSearchTagList(event.event_id, venuid.getVenue_id());
        }

        fromTrending = getIntent().getBooleanExtra("fromTrending",false);

        venuBoardEventTagBeanList = new ArrayList<>();

        getAllData();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
                case R.id.img_eventDetail_back:
                     onBackPressed();
                     break;

                case R.id.iv_group:
                        Intent intent = new Intent(OnBoardActivity.this, TheRoomActivity.class);
                        intent.putExtra("fromTrendingHome", event.keyInUserModalList);
                        intent.putExtra("object", object);
                        intent.putExtra("currentLatLng", currentLatLng);
                    if(fromTrending){
                        intent.putExtra("fromTrending", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        startActivity(intent);
                    }

                 break;

                case R.id.img_no_member:
                        callCheckEventStatusApi(event.event_name, event.event_id, venue,object,currentLatLng
                                ,new String[]{venue.getLatitude(), venue.getLongitude()});
                  break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getSearchTagList(final String event_id, final String venue_id) {

        if (utility.checkInternetConnection()) {
            setLoading(true);
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.VENUEBOARD_EVENT_TAG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    setLoading(false);
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        String status = jo.getString("status");
                        if (status.equals("success")) {

                            JSONArray eventTag = jo.getJSONArray("eventTag");
                            VenueBoard.EventTagBean eventTagBean;
                            VenueBoard.EventTagBean eventTagBeanSpecial = null;
                            VenueBoard.EventTagBean eventTagBeanHappyHour = null;
                            for (int i = 0; i < eventTag.length(); i++) {

                                JSONObject jsonObject1 = eventTag.getJSONObject(i);
                                JSONArray tagList = jsonObject1.getJSONArray("tagList");
                                if(tagList.length()>0){

                                    if(jsonObject1.getString("category_name").equalsIgnoreCase("Specials")){
                                        eventTagBeanSpecial = new VenueBoard.EventTagBean();
                                        eventTagBeanSpecial.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBeanSpecial.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBeanSpecial.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBeanSpecial.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBeanSpecial.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardListSpecial = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardListSpecial.add(tagListBean);
                                        }
                                        eventTagBeanSpecial.setTagList(venuBoardListSpecial);
                                    }
                                    else
                                    if(jsonObject1.getString("category_name").equalsIgnoreCase("Happy Hour")){
                                        eventTagBeanHappyHour = new VenueBoard.EventTagBean();
                                        eventTagBeanHappyHour.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBeanHappyHour.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBeanHappyHour.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBeanHappyHour.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBeanHappyHour.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardListHappyHour = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardListHappyHour.add(tagListBean);
                                        }
                                        eventTagBeanHappyHour.setTagList(venuBoardListHappyHour);
                                    }
                                    else {
                                        eventTagBean = new VenueBoard.EventTagBean();
                                        eventTagBean.setCat_id(jsonObject1.getString("cat_id"));
                                        eventTagBean.setCategory_name(jsonObject1.getString("category_name"));
                                        eventTagBean.setColor_code(jsonObject1.getString("color_code"));
                                        eventTagBean.setCategory_image(jsonObject1.getString("category_image"));
                                        eventTagBean.setColor_code(jsonObject1.getString("color_code"));

                                        venuBoardList = new ArrayList<>();
                                        for (int j = 0; j < tagList.length(); j++) {

                                            JSONObject jsonObject = tagList.getJSONObject(j);

                                            VenueBoard.EventTagBean.TagListBean tagListBean = new VenueBoard.EventTagBean.TagListBean();
                                            tagListBean.setBiz_tag_id(jsonObject.getString("biz_tag_id"));
                                            tagListBean.setTag_name(jsonObject.getString("tag_name"));
                                            tagListBean.setColor_code(jsonObject.getString("color_code"));
                                            tagListBean.setTag_text(jsonObject.getString("tag_text"));
                                            tagListBean.setTag_image(jsonObject.getString("tag_image"));
                                            tagListBean.setIs_tag_follow(jsonObject.getString("is_tag_follow"));

                                            venuBoardList.add(tagListBean);
                                        }
                                        eventTagBean.setTagList(venuBoardList);
                                        venuBoardEventTagBeanList.add(eventTagBean);
                                    }
                                }
                            }

                            if(eventTagBeanHappyHour != null){
                                venuBoardEventTagBeanList.add(eventTagBeanHappyHour);
                            }

                            if(eventTagBeanSpecial != null){
                                venuBoardEventTagBeanList.add(eventTagBeanSpecial);
                            }
                            //venueBoardAdapter.notifyDataSetChanged();
                            venueBoardAdapter = new VenueBoardAdapter(OnBoardActivity.this, venuBoardEventTagBeanList,fromTrending);
                            venuRecyclerView.setAdapter(venueBoardAdapter);
                        }

                    } catch (Exception e) {
                        setLoading(false);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    setLoading(false);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("event_id", event_id);
                    params.put("venue_id", venue_id);
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(container, getString(R.string.internetConnectivityError), 0);
        }
    }

    private void callCheckEventStatusApi(final String event_name, final String event_id, final Venue venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(this);
        showProgDialog(false, "");
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.CHECK_EVENT_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        dismissProgDialog();
                        jsonObject = new JSONObject(Response);

                        String status = jsonObject.getString("status");
                        boolean isKeyInAble;

                        // If Not exist then isKeyInAble is false
                        // If exist then isKeyInAble is true
                        isKeyInAble = !status.equals("not exist");

                        if (!isKeyInAble && userInfo().key_points.equals("0")) {
                            Toast.makeText(OnBoardActivity.this, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(OnBoardActivity.this, EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("fromTab", "trending");
                            intent.putExtra("venueName", venue_name.getVenue_name());
                            intent.putExtra("currentLatLng", currentLatLng);
                            intent.putExtra("event_name", event_name);
                            intent.putExtra("object", object);
                            intent.putExtra("venueId", venue_name.getVenue_id());
                            if(fromTrending){
                                intent.putExtra("fromTrending", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                startActivity(intent);
                            }

                         /*   Event_Fragment fragment = Event_Fragment.newInstance("trending");
                            fragment.setData(event_id, venue_name, object, currentLatLng, strings, isKeyInAble);
                            activity.addFragment(fragment, 0);*/
                        }

                    } catch (Exception ex) {
                        dismissProgDialog();
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("eventid", event_id);
                    params.put("userid", userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(OnBoardActivity.this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(OnBoardActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

    /**
     * GetALl the data for that event
     */
    public void getAllData() {

       // showProgDialog(false,"");
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LISTEVENTFEED, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.e("Responce129", response);
                    // get response
                    try {
                        if (response != null) getResponse(response);
                        else
                            Utility.showToast(OnBoardActivity.this, getString(R.string.somethingwentwrong), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("event_id", event.event_id);
                    params.put("user_id", userInfo().userid);

                   // Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1));
        } else {
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            //utility.snackBar(feedLIstRecyclerView, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    /**
     * @param response the responce provided by getAlldata()
     * @throws JSONException
     */
    private void getResponse(String response) throws Exception {
        JSONObject obj1 = new JSONObject(response);
        dismissProgDialog();
        try {
            if (obj1.has("eventattendy")) {
                Object objectType = obj1.get("eventattendy");

                if (objectType instanceof String) {
                    iv_group.setVisibility(View.VISIBLE);

                } else if (objectType instanceof JSONArray) {
                    setAttendyJson(obj1.getJSONArray("eventattendy"));
                    iv_group.setVisibility(View.GONE);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setAttendyJson(JSONArray Json) throws JSONException {
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
        setRecyclerView(attendyList);
    }

    /***
     * For setting the Grid Layout of room Persons showing at bottom of the Room
     *
     * @param list
     */
    public void setRecyclerView(final ArrayList<EventAttendy> list) {
        CircularImageView comeInUserProfile = null;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = findViewById(R.id.comeInUser_lnr);
        parent.removeAllViews();
        int loopCount  = list.size();
        if(loopCount >5){
            loopCount = 5;
        }
        if (list.size() != 0) {
            parent.setVisibility(View.VISIBLE);
            iv_group.setVisibility(View.GONE);
        } else {
            parent.setVisibility(View.GONE);
            iv_group.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < loopCount; i++) {
            assert inflater != null;
            View v = inflater.inflate(R.layout.trend_user_view, null);
            comeInUserProfile = v.findViewById(R.id.comeInProfile_t);
            TextView no_count = v.findViewById(R.id.no_count_t);
            RelativeLayout marginlayout = v.findViewById(R.id.mainProfileView_t);

            if (i == 0) {

                parent.addView(v, i);
                String image = "";

                if (!list.get(i).getUserimage().contains("dev-")) {
                    image = "dev-" + list.get(i).getUserimage();
                } else {
                    //image = keyInUserModalList.get(i).userImage;
                    image = list.get(i).getUserimage();
                }

                Glide.with(this).load(image)
                        .thumbnail(0.5f)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_img)
                        .error(R.drawable.placeholder_img)
                        .into(comeInUserProfile);


            } else {
                if (i == 1) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    parent.addView(v, i);
                    String image = "";

                    if (!list.get(i).getUserimage().contains("dev-")) {
                        image = "dev-" + list.get(i).getUserimage();
                    } else {
                        image = list.get(i).getUserimage();
                    }

                    Glide.with(this).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                }
                else
                if (i == 2) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    parent.addView(v, i);
                    String image = "";

                    if (!list.get(i).getUserimage().contains("dev-")) {
                        image = "dev-" + list.get(i).getUserimage();
                    } else {
                        image = list.get(i).getUserimage();
                    }

                    Glide.with(this).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                }
                if (i == 3) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    parent.addView(v, i);
                    no_count.setText(" +" + (list.size() - i) );
                    no_count.setVisibility(View.VISIBLE);
                }
            }
        }

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!userExistOrNotonActivty.equals("")) {
//                    cantJoinNotExixtUserDialog(userExistOrNotonActivty);
//
//                } else {
                Intent intent = new Intent(OnBoardActivity.this, TheRoomActivity.class);
                intent.putExtra("commentPesionList", list);
                intent.putExtra("eventid", object.getEvent());
                intent.putExtra("venuid", object.getVenue());
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                if(fromTrending){
                    intent.putExtra("fromTrending", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    startActivity(intent);
                }
                //  }
            }
        });
    }

    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final int callFrom) { // 0 from search, 1 for tags long press
        utility = new Utility(OnBoardActivity.this);
        if (utility.checkInternetConnection()) {
            showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        dismissProgDialog();
                        if(jo.has("status")){
                            if(jo.getString("status").equalsIgnoreCase("success")){
                                venuBoardEventTagBeanList = new ArrayList<>();
                                getSearchTagList(event.event_id, object.getVenue().getVenue_id());
                            }
                        }

                    } catch (Exception e) {
                        dismissProgDialog();
                        Utility.showToast(OnBoardActivity.this, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("biz_tag_id",biz_tag_id);
                    params.put("follow_status", String.valueOf(followUnfollow));
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(OnBoardActivity.this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }
}
