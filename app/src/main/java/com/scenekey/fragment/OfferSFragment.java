package com.scenekey.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.OfferAdapter;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.MySelecteOfferListener;
import com.scenekey.model.Offers;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfferSFragment extends Fragment {

    public final String TAG = OfferSFragment.class.toString();
    private RecyclerView offerRecyclerView;
    private ArrayList<Offers> offerList;
    private OfferAdapter offerAdapter;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private ScrollView no_data_inOffer;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        inItView(view);
        return view;
    }

    private void inItView(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        offerRecyclerView = view.findViewById(R.id.offerRecyclerView);
        no_data_inOffer = view.findViewById(R.id.no_data_inOffer);
        offerList = new ArrayList<>();
        offerAdapter = new OfferAdapter(context, offerList, new MySelecteOfferListener() {
            @Override
            public void getSelectedOffer(Offers offers) {

                Log.v("offers", offers.reward_id);
                addToWalletList(offers);

            }
        });
        offerRecyclerView.setAdapter(offerAdapter);
        getOffersList();
        //activity.txt_notification.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
        utility = new Utility(context);
    }

    private void addToWalletList(final Offers offers) {
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.REWARD_ADDTOWALLET, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    Log.v("response", response);
                    // get response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            utility.showCustomPopup(getString(R.string.success_check_wallet), String.valueOf(R.font.montserrat_medium));

                        } else {
                            utility.showCustomPopup(message, String.valueOf(R.font.montserrat_medium));
                        }
                        getOffersList();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    params.put("rewardId", offers.reward_id);
                    params.put("userId", SceneKey.sessionManager.getUserInfo().userid + "");
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            //utility.snackBar(rcViewTrending,getString(R.string.internetConnectivityError),0);
            Toast.makeText(context, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }

    public void getOffersList() {
        activity.showProgDialog(false,TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.REWARD_OFFER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    Log.v("response", response);
                    // get response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        //String message = jsonObject.getString("message");
                        String totalWallet = jsonObject.getString("totalWallet");
                        offerList.clear();

                        if (status.equals("success")) {
                            no_data_inOffer.setVisibility(View.GONE);
                            JSONArray data = jsonObject.getJSONArray("allReward");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject2 = (JSONObject) data.get(i);

                                if(jsonObject2.getString("alert_type").equalsIgnoreCase("tag")){
                                    Offers offers = new Offers();
                                    offers.alert_type = jsonObject2.getString("alert_type");
                                    offers.cat_id = jsonObject2.getString("cat_id");
                                    offers.category_name = jsonObject2.getString("category_name");
                                    offers.biz_tag_id = jsonObject2.getString("biz_tag_id");
                                    offers.tag_name = jsonObject2.getString("tag_name");
                                    offers.venue_id = jsonObject2.getString("venue_id");
                                    offers.venue_name = jsonObject2.getString("venue_name");
                                    offers.venue_image = jsonObject2.getString("venue_image");
                                    offers.event_name = jsonObject2.getString("event_name");
                                    offers.crd = jsonObject2.getString("crd");
                                    offers.frequency = jsonObject2.getString("frequency");
                                    offers.other_type = jsonObject2.getString("other_type");
                                    offers.distance = jsonObject2.getString("distance");
                                    offers.message = jsonObject2.getString("message");
                                    if(jsonObject2.has("tag_text")){
                                        offers.tag_text = jsonObject2.getString("tag_text");
                                    }
                                    else {
                                        offers.tag_text = "";
                                    }
                                    offerList.add(offers);
                                }
                                else {
                                    Offers offers = new Offers();
                                    offers.alert_type = jsonObject2.getString("alert_type");
                                    offers.reward_id = jsonObject2.getString("reward_id");
                                    offers.reward_language = jsonObject2.getString("reward_language");
                                    offers.goes_to = jsonObject2.getString("goes_to");
                                    offers.exp = jsonObject2.getString("exp");
                                    offers.crd = jsonObject2.getString("crd");
                                    offers.venue_name = jsonObject2.getString("venue_name");
                                    offers.venue_address = jsonObject2.getString("venue_address");
                                    offers.venue_image = jsonObject2.getString("venue_image");
                                    if (jsonObject2.has("reward_image")) {
                                        offers.reward_image = jsonObject2.getString("reward_image");
                                    } else {
                                        offers.reward_image = "";
                                    }
                                    offers.venue_lat = jsonObject2.getString("venue_lat");
                                    offers.venue_long = jsonObject2.getString("venue_long");
                                    offers.distance = jsonObject2.getString("distance");
                                    offers.business_name = jsonObject2.getString("business_name");
                                    offers.point = jsonObject2.getString("point");
                                    offerList.add(offers);
                                }
                            }
                            setBadgeCountToFireBase();
                        } else {
                            no_data_inOffer.setVisibility(View.VISIBLE);
                        }
                        offerAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    params.put("lat", SceneKey.sessionManager.getUserInfo().lat);
                    params.put("long", SceneKey.sessionManager.getUserInfo().longi);
                    params.put("userId", SceneKey.sessionManager.getUserInfo().userid + "");
//                    params.put("userId", "1384");

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            //utility.snackBar(rcViewTrending,getString(R.string.internetConnectivityError),0);
            Toast.makeText(context, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }

    public void setBadgeCountToFireBase(){
        mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("0");
        activity.tv_alert_badge_count.setVisibility(View.GONE);
    }
}
