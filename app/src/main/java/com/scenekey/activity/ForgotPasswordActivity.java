package com.scenekey.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.scenekey.R;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.Permission;
import com.scenekey.helper.Validation;
import com.scenekey.helper.WebServices;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleyMultipartRequest;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmailForgot;
    private Button btnSubmit;
    private Context context = this;
    private Permission permission;
    private Utility utility;
    private CustomProgressBar customProgressBar;
    private ImageView ivBackIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
        initMember();
    }

    private void initView() {
        etEmailForgot = findViewById(R.id.etEmailForgot);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivBackIcon = findViewById(R.id.ivBackIcon);
        ivBackIcon.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initMember() {
        permission = new Permission(context);
        customProgressBar = new CustomProgressBar(context);
        utility = new Utility(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                Validation validation = new Validation(context);
                if (validation.isEmailValid(etEmailForgot)) {
                    //Utility.showToast(context, getString(R.string.underDevelopment), 0);
                    String email = etEmailForgot.getText().toString().trim();
                    forgotPassword(email);
                } else {
                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }
                break;

            case R.id.ivBackIcon:
                onBackPressed();
                break;
        }
    }

    private void forgotPassword(final String email) {
        if (utility.checkInternetConnection()) {

            customProgressBar = new CustomProgressBar(context);
            showProgDialog(false);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.FORGOTPASSWORD, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            dismissProgDialog();

                               /* JSONObject userDetail = jsonObject.getJSONObject("userDetail");
                                UserInfo userInfo = new UserInfo();
                                userInfo.userID = userDetail.getString("userid");
*/
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            dismissProgDialog();
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Toast.makeText(ForgotPasswordActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    dismissProgDialog();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    Utility.e("ForGotePasword params", params.toString());
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(ForgotPasswordActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgDialog(boolean b) {
        customProgressBar.setCanceledOnTouchOutside(b);
        customProgressBar.setCancelable(b);
        customProgressBar.show();
    }

    private void dismissProgDialog() {
        if (customProgressBar != null) customProgressBar.dismiss();
    }
}
