package com.scenekey.fragment;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.SearchEvent_Adapter;
import com.scenekey.adapter.Tag_Adapter;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.model.Events;
import com.scenekey.model.Tags;
import com.scenekey.model.Venue;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mindiii on 15/2/18.
 */

public class Event_Search_Tag_Fragment extends Fragment implements View.OnClickListener {

    private final String TAG = Event_Search_Tag_Fragment.class.toString();

    private Context context;
    private HomeActivity activity;
    private String lat = "", lng = "";
    private String selected;

    private ArrayList<Tags> list;
    // private ArrayList<EventsBTag> list_events;
    private ArrayList<Events> list_events;

    private CheckEventStatusListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // activity.setBBVisibility(View.GONE,this.getClass().getSimpleName());

        View view = inflater.inflate(R.layout.fragment_event_search_tag, container, false);
        setVisibility();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(this);
        activity.showProgDialog(false, TAG);
        view.findViewById(R.id.img_f1_back).setOnClickListener(this);

        setRecyclerView();
        getSearched();
        ((TextView) view.findViewById(R.id.tv_key_points)).setText(activity.userInfo().key_points);
    }

    Event_Search_Tag_Fragment setData(String lat, String lng, String selected, ArrayList<Tags> list) {
        this.lat = lat;
        this.lng = lng;
        this.selected = selected;
        this.list = getSelectedList(list);
        return this;
    }

    @Override
    public void onDestroy() {
        activity.setBBVisibility(View.VISIBLE, this.getClass().getSimpleName());
        super.onDestroy();
    }

    private void getSearched() {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("userId", activity.userInfo().userid);
            jsonBody.put("tags", selected);
            jsonBody.put("lat", lat);
            jsonBody.put("long", lng);

            final String mRequestBody = jsonBody.toString();
            Utility.e("RequestBody", mRequestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServices.EVENT_BY_TAG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utility.e("LOG_VOLLEY R", response);
                    try {
                        JSONArray jA = new JSONArray(response);
                        if (list_events == null) list_events = new ArrayList<>();
                        else list_events.clear();
                        for (int i = 0; i < jA.length(); i++) {
                            JSONObject jO = jA.getJSONObject(i);
                            Events events = new Events();
                            if (jO.has("venue"))
                                events.setVenueJSON(jO.getJSONObject("venue"));
                            if (jO.has("artists"))
                                events.setArtistsArray(jO.getJSONArray("artists"));
                            if (jO.has("events"))
                                events.setEventJson(jO.getJSONObject("events"));
                            try {
                                events.setOngoing(events.checkWithTime(events.getEvent().event_date, events.getEvent().interval));
                            } catch (Exception e) {
                                Utility.e("Date exception", e.toString());
                            }
                            try {
                                int time_format = 0;
                                try {
                                    time_format = Settings.System.getInt(context.getContentResolver(), Settings.System.TIME_12_24);
                                } catch (Settings.SettingNotFoundException e) {
                                    e.printStackTrace();
                                }

                                events.settimeFormat(time_format);
                            } catch (Exception e) {
                                Utility.e("Exception time", e.toString());
                            }
                            try {
                                events.setRemainingTime();
                            } catch (Exception e) {
                                Utility.e("Exception Remaining", e.toString());
                            }

                            list_events.add(events);
                        }
                        if (list_events.size() <= 0) {
                            Utility.showToast(context, "No Event found near your location", 0);
                        } else {
                           // activity.keyPointsUpdate();
                        }
                        setRecyclerViewEvent();
                        activity.dismissProgDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utility.e("LOG_VOLLEY E", error.toString());
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes();
                    } catch (Exception uee) {
                        //VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = new String(response.data);
                        //Util.printLog("RESPONSE", responseString.toString());
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, 0));
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            activity.dismissProgDialog();
        }
    }

    private ArrayList<Tags> getSelectedList(ArrayList<Tags> list) {
        ArrayList<Tags> resultList = new ArrayList<>();
        for (Tags tags : list) {
            if (tags.selected) resultList.add(tags);
        }
        return resultList;
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = this.getView().findViewById(R.id.recycler_view1);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.setAdapter(new Tag_Adapter(context, list));
    }

    private void setRecyclerViewEvent() {
        RecyclerView recyclerView = this.getView().findViewById(R.id.recycler_view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new SearchEvent_Adapter(activity, list_events, new String[]{lat, lng}, new CheckEventStatusListener() {
            @Override
            public void getCheckEventStatusListener(String eventName, String eventId, Venue event_id, Events object, String[] currentLatLng, String[] strings) {
               /* Search_Fragment  Search_Fragment = new Search_Fragment();
                Search_Fragment.*/
                callCheckEventStatusApi(eventName,eventId, event_id.getVenue_name(), object, currentLatLng, strings);
            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_f1_back:
                activity.onBackPressed();
                break;
        }
    }

    public void setVisibility() {
        activity.setBBVisibility(View.GONE, TAG);
        activity.frm_bottmbar.setVisibility(View.VISIBLE);
        activity.showStatusBar();
    }

    // New Code
    private void callCheckEventStatusApi(final String eventName,final String event_id, final String venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(context);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.CHECK_EVENT_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        activity.dismissProgDialog();
                        jsonObject = new JSONObject(Response);

                        String status = jsonObject.getString("status");
                        boolean isKeyInAble;

                        // If Not exist then isKeyInAble is false
                        // If exist then isKeyInAble is true
                        isKeyInAble = !status.equals("not exist");

                        if (!isKeyInAble && activity.userInfo().key_points.equals("0")) {
                            Toast.makeText(context, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {


                            Search_Event_Details_Fragment fragment = Search_Event_Details_Fragment.newInstance("trending");
                            fragment.setData(event_id, venue_name, object, currentLatLng, strings, isKeyInAble);
                            activity.addFragment(fragment, 0);
                        }

                    } catch (Exception ex) {
                        activity.dismissProgDialog();
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("eventid", event_id);
                    params.put("userid", activity.userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }
}