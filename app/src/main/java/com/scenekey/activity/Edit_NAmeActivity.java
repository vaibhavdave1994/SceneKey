package com.scenekey.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.fragment.Edit_NameFragment;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.CustomeClick;
import com.scenekey.helper.Validation;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Edit_NAmeActivity extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = Edit_NameFragment.class.toString();
    private EditText et_firstName, et_lastName;
    private TextView txt_updateNAme;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private CustomProgressBar customProgressBar;
    private Animation shake;
    private ImageView img_f1_back;
    public static UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit__name);
        setContentView(R.layout.fragment_edit__name);
        initView();
    }

    private void initView() {

        utility = new Utility(this);
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);

        customProgressBar = new CustomProgressBar(this);
        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        txt_updateNAme = findViewById(R.id.txt_updateNAme);
        img_f1_back = findViewById(R.id.img_f1_back);
        txt_updateNAme.setOnClickListener(this);
        img_f1_back.setOnClickListener(this);

        String firstName = userInfo().fullname;
        String lastName =  userInfo().lastName;

        et_firstName.setText(firstName);
        et_lastName.setText(lastName);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_updateNAme:
                Validation validation = new Validation(this);
                if (validation.isFullNValid(et_firstName, shake) && validation.isLastNValid(et_lastName, shake)) {

                    String firstName = et_firstName.getText().toString().trim();
                    String lastName = et_lastName.getText().toString().trim();
                    updateName(firstName, lastName);
                } else {
                    Utility.showToast(this, getString(R.string.internetConnectivityError), 0);
                }
                break;

            case R.id.img_f1_back:
                onBackPressed();
                break;
        }
    }


    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(Edit_NAmeActivity.this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }

    private void updateName(final String firstName, final String lastName) {
        showProgDialog(false, TAG);
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.UPDATEPROFILE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    Log.v("response", response);
                    // get response
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        //String totalWallet = jsonObject.getString("totalWallet");

                        if (status.equals("1")) {
                            UserInfo userInfo = userInfo();
                            userInfo.fullname = firstName;
                            userInfo.lastName = lastName;
                            updateSession(userInfo);

                            CustomeClick.getmInctance().onTextChange(userInfo);

                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            onBackPressed();

                        } else {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    params.put("fullname", firstName);
                    params.put("lastName", lastName);
                    params.put("user_id", userInfo().userid);
                    Utility.e("Update Profile params", params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            //utility.snackBar(rcViewTrending,getString(R.string.internetConnectivityError),0);
            Toast.makeText(this, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }


    private void dismissProgDialog() {
        if (customProgressBar != null) customProgressBar.dismiss();
    }

    public void showProgDialog(boolean b, String TAG) {
        try {
            customProgressBar.setCanceledOnTouchOutside(b);
            customProgressBar.setCancelable(b);
            customProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
        try {
            //Picasso.with(this).load(userInfo.getUserImage()).placeholder(R.drawable.image_default_profile).into(img_profile);
            //tv_key_points.setText(userInfo.key_points);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
