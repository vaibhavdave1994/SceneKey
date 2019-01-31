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
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.WalletsAdapter;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.MyRedeemListener;
import com.scenekey.model.Wallets;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WalletsFragment extends Fragment {

    public final String TAG = OfferSFragment.class.toString();
    private RecyclerView walletRecyclerView;
    private ArrayList<Wallets> walletsArrayList;
    private WalletsAdapter walletsAdapter;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private ScrollView no_data_inWallet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_wallets, container, false);
        inItView(view);
        return view;
    }

    private void inItView(View view) {
        walletRecyclerView = view.findViewById(R.id.walletRecyclerView);
        no_data_inWallet = view.findViewById(R.id.no_data_inWallet);
        walletsArrayList = new ArrayList<>();
        walletsAdapter = new WalletsAdapter(context, walletsArrayList, new MyRedeemListener() {
            @Override
            public void MyReddemSelcteListenr(Wallets wallets) {

                addRedeemToWallet(wallets);
                getWalletList();
            }
        });
        walletRecyclerView.setAdapter(walletsAdapter);
        getWalletList();
        //activity.txt_notification.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
        utility = new Utility(context);
    }


    private void addRedeemToWallet(final Wallets wallets) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.REWARD_ADDTOREDEEM, new Response.Listener<String>() {
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
                            utility.showCustomPopup(getString(R.string.reward_redeem_successfully), String.valueOf(R.font.montserrat_medium));

                        } else {
                            utility.showCustomPopup(message, String.valueOf(R.font.montserrat_medium));
                        }
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
                    params.put("reward_id", wallets.reward_id);
                    params.put("wallet_id", wallets.wallet_id);
                    //params.put("wallet_id", SceneKey.sessionManager.getUserInfo().userID);
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

    private void getWalletList() {
        activity.showProgDialog(false,TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.REWARD_WALLETS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    Log.v("response", response);
                    // get response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        //String message = jsonObject.getString("message");
                        //String totalWallet = jsonObject.getString("totalWallet");

                        walletsArrayList.clear();
                        if (status.equals("success")) {
                            no_data_inWallet.setVisibility(View.GONE);
                            JSONArray data = jsonObject.getJSONArray("allWallet");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject2 = (JSONObject) data.get(i);

                                Wallets wallets = new Wallets();
                                wallets.reward_id = jsonObject2.getString("reward_id");
                                wallets.reward_language = jsonObject2.getString("reward_language");
                                wallets.exp = jsonObject2.getString("exp");
                                wallets.venue_name = jsonObject2.getString("venue_name");
                                wallets.venue_address = jsonObject2.getString("venue_address");
                                wallets.venue_image = jsonObject2.getString("venue_image");
                                wallets.venue_lat = jsonObject2.getString("venue_lat");
                                wallets.venue_long = jsonObject2.getString("venue_long");
                                wallets.business_name = jsonObject2.getString("business_name");
                                wallets.point = jsonObject2.getString("point");
                                wallets.wallet_id = jsonObject2.getString("wallet_id");

                                //wallets.totalWallet = jsonObject2.getString("totalWallet");
                                walletsArrayList.add(wallets);
                            }

                        } else {
                            no_data_inWallet.setVisibility(View.VISIBLE);
                            //utility.showCustomPopup("", String.valueOf(R.font.montserrat_medium));
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                        walletsAdapter.notifyDataSetChanged();
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

}
