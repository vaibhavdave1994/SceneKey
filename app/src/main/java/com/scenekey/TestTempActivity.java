package com.scenekey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestTempActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        login();
    }

    private void login() {
        final Utility utility = new Utility(this);
        String loginUrl = "http://dev.lasross.com/api/service/login";
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST,loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", "deepaksharma1@gmail.com");
                    params.put("password", "123456");
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCart() {
        final Utility utility = new Utility(this);
        String addcartnUrl = "http://dev.lasross.com/api/product/addCart";
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, addcartnUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", "deepaksharma1@gmail.com");
                    params.put("password", "123456");
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }

}
