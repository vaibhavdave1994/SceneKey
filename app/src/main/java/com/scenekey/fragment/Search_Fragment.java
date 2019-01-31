package com.scenekey.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.Tag_Adapter;
import com.scenekey.helper.WebServices;
import com.scenekey.model.Tags;
import com.scenekey.model.UserInfo;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.util.ArrayList;

public class Search_Fragment extends Fragment implements View.OnClickListener {

    private final String TAG = Search_Fragment.class.toString();

    private Context context;
    private HomeActivity activity;

    private Utility utility;
    private String lat="",lng="";
    private RecyclerView rcViewSearch;
    public TextView txt_search;
    private ArrayList<Tags> list;
    private ScrollView no_data_searching;
    private TextView user_searching;
    private RelativeLayout containerSearch;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_search, container, false);
        activity.setTitleVisibility(View.VISIBLE);
        txt_search = v.findViewById(R.id.txt_search);
        rcViewSearch =  v.findViewById(R.id.rcViewSearch);
        no_data_searching =  v.findViewById(R.id.no_data_searching);
        user_searching =  v.findViewById(R.id.user_searching);
        containerSearch =  v.findViewById(R.id.containerSearch);

        txt_search.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle(context.getResources().getString(R.string.search));
        view.setOnClickListener(this);
        searchData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        activity= (HomeActivity) getActivity();
        utility=new Utility(context);
        searchData();
    }

    public void searchData() {
        String[] latLng=  activity.getLatLng();
        lat=latLng[0];
        lng=latLng[1];
        if (lat.equals("0.0")&&lng.equals("0.0")){
            latLng=  activity.getLatLng();
            lat= latLng[0];
            lng= latLng[1];
            retryLocation();
        }
        else{
            activity.showProgDialog(false,TAG);
            getTags();
        }
    }


    private void retryLocation() {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_with_btn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //      deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id

        TextView tvCancel, tvTryAgain,tvTitle,tvMessages;

        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvMessages = dialog.findViewById(R.id.tvMessages);
        tvCancel = dialog.findViewById(R.id.tvPopupCancel);
        tvTryAgain = dialog.findViewById(R.id.tvPopupOk);

        tvTitle.setText(R.string.gps_new);
        tvMessages.setText(R.string.couldntGetLocation);

        tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                activity.showProgDialog(false,TAG);
                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {

                        if (utility.checkNetworkProvider()|utility.checkGPSProvider()){
                            searchData();
                        }else{
                            utility.checkGpsStatus();
                            searchData();
                        }
                    }
                }, 3 * 1000); // wait for 3 seconds
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.dismissProgDialog();
                dialog.cancel();

            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_search:
                try {
                    if(list!=null) activity.addFragment(new Event_Search_Tag_Fragment().setData(lat,lng,getSelected() , list),0);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (InvalidObjectException e){
                    e.printStackTrace();
                }
                break;
        }
    }

   private String getSelected() throws InvalidObjectException{
        String result=null;
        StringBuilder s = new StringBuilder();
        for(Tags tags :list){
            s.append(tags.selected?tags.tag.trim()+",":"");
        }
        result = s.substring(0,s.length()-1).trim();
        if(result.isEmpty()){
            new InvalidObjectException("No tag is selected") ;
        }
        return result;
    }

    private void getTags() {

        if (utility.checkInternetConnection()) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("method","POST");
                jsonBody.put("action","searchByEvntLoc");
                jsonBody.put("lat",lat);
                jsonBody.put("long",lng);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String mRequestBody = jsonBody.toString();
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_TAG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    Utility.e("LOG_VOLLEY SEARCH ", response);
                    try {
                        JSONArray tag_Arrray = new JSONArray(response);

                        if(tag_Arrray.length() == 0){
                            no_data_searching.setVisibility(View.VISIBLE);
                            txt_search.setVisibility(View.GONE);
                        }else {
                            no_data_searching.setVisibility(View.GONE);
                            txt_search.setVisibility(View.VISIBLE);
                        }

                        (list==null?list= new ArrayList<>():list).clear();
                        for(int i= 0;i<tag_Arrray.length();i++){
                            list.add(new Tags(i+"",tag_Arrray.getJSONObject(i).getString("tag"),false));
                        }
                        setRecyclerView();

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

            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        }else{
            utility.snackBar(containerSearch,getString(R.string.internetConnectivityError),0);
            activity.dismissProgDialog();
        }
    }

    private void setRecyclerView(){
        rcViewSearch.setLayoutManager(new GridLayoutManager(getContext(),3));
        rcViewSearch.setAdapter(new Tag_Adapter(context,list ,this));
    }
}
