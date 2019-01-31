package com.scenekey.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.SearchEvent_Adapter;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.model.Events;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NearEvent_Fragment extends Fragment {

    private Context context;
    private HomeActivity activity;
    private ArrayList<Events> eventsList;
    private RecyclerView rcViewNearEvent;
    private String[] currentLatLng;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_near_event, container, false);
        rcViewNearEvent = v.findViewById(R.id.rcViewNearEvent);
        activity.setTitleVisibility(View.VISIBLE);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle(activity.getResources().getString(R.string.enter));
        setRecyclerView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
    }

    public void setEventsList(ArrayList<Events> eventsList) {
        this.eventsList = eventsList;
    }

    public void setNearLatLng(String[] strings) {
        currentLatLng = strings;
    }

    public void setRecyclerView() {
        if (rcViewNearEvent.getAdapter() == null) {
            SearchEvent_Adapter nearEventAdapter = new SearchEvent_Adapter(activity, eventsList, currentLatLng, new CheckEventStatusListener() {
                @Override
                public void getCheckEventStatusListener(String eventName, String event_id, String venue_name, Events object, String[] currentLatLng, String[] strings) {
                    callAddEventApi(eventName,event_id, venue_name, object, currentLatLng, strings);
                }
            });

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
            rcViewNearEvent.setLayoutManager(layoutManager);
            rcViewNearEvent.setAdapter(nearEventAdapter);
            nearEventAdapter.notifyDataSetChanged();
            rcViewNearEvent.setHasFixedSize(true);
        } else {
            rcViewNearEvent.getAdapter().notifyDataSetChanged();
        }
    }

    public void eventApiRefresh() {
        activity.checkEventAvailablity(true);
    }

    public void callAddEventApi(final  String eventNAme, final String event_id, final String venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(context);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        activity.dismissProgDialog();
                        jsonObject = new JSONObject(Response);
                        String status = jsonObject.getString("status");

                        /*String status = jsonObject.getString("status");
                        boolean isKeyInAble;

                        // If Not exist then isKeyInAble is false
                        // If exist then isKeyInAble is true
                        isKeyInAble = !status.equals("not exist");

                        if (!isKeyInAble && activity.userInfo().key_points.equals("0")) {
                            Toast.makeText(context, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {
                            Event_Fragment frg = new Event_Fragment();
                            frg.setData(event_id, venue_name, object, currentLatLng, strings, isKeyInAble);
                            activity.addFragment(frg, 0);
                        }*/

                        if(status.equals(object.getEvent().status)){


                          /*  Event_Fragment frg = Event_Fragment.newInstance("event_tab");
                            frg.setData(event_id, venue_name, object, currentLatLng, strings, true);
                            activity.addFragment(frg, 0);*/

                            Intent intent = new Intent(context,EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("event_tab", "event_tab");
                            startActivity(intent);

                        }else {

                            Intent intent = new Intent(context,EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("event_tab", "event_tab");
                            startActivity(intent);
                            /*Event_Fragment frg =  Event_Fragment.newInstance("event_tab");
                            frg.setData(event_id, venue_name, object, currentLatLng, strings, false);
                            activity.addFragment(frg, 0);*/
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
                    params.put("userid", activity.userInfo().userid);
                    params.put("eventname", object.getEvent().event_name);
                    params.put("eventid", object.getEvent().event_id);
                    params.put("Eventdate", object.getEvent().event_date);

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
