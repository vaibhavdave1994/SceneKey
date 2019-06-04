package com.scenekey.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.scenekey.R;
import com.scenekey.fragment.ChangePassword_Fragment;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.Validation;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleyMultipartRequest;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = ChangePasswordActivity.class.toString();
    private EditText et_oldPassword, et_newPassword, et_confirmPassword;
    private TextView txt_updatePassword;
    private Utility utility;
    private SessionManager sessionManager;
    private CustomProgressBar customProgressBar;
    private ImageView iv_conPass,iv_newPass;
    private boolean isChecked = true;
    private boolean isnewPass = false;
    private ImageView img_f1_back;
    private Animation shake;
    public static UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_password_);
        inItView();
    }

    private void inItView() {
        utility = new Utility(this);
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);

        sessionManager = new SessionManager(this);
        customProgressBar=new CustomProgressBar(this);
        et_oldPassword = findViewById(R.id.et_oldPassword);
        et_newPassword = findViewById(R.id.et_newPassword);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        img_f1_back = findViewById(R.id.img_f1_back);
        iv_conPass = findViewById(R.id.iv_conPass);
        iv_newPass = findViewById(R.id.iv_newPass);
        txt_updatePassword = findViewById(R.id.txt_updatePassword);
        txt_updatePassword.setOnClickListener(this);
        iv_newPass.setOnClickListener(this);
        iv_conPass.setOnClickListener(this);
        img_f1_back.setOnClickListener(this);

        et_newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_updatePassword:
                updatePassword(et_oldPassword, et_newPassword, et_confirmPassword);
                break;

            case R.id.img_f1_back:
                onBackPressed();
                break;


            case R.id.iv_conPass:
                if (!isChecked) {
                    // show password
                    iv_conPass.setImageResource(R.drawable.eye_show);
                    et_confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isChecked =true;
                } else {
                    // hide password
                    isChecked =false;
                    iv_conPass.setImageResource(R.drawable.eye_hide);
                    et_confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.iv_newPass:
                if (!isnewPass) {
                    // show password
                    iv_newPass.setImageResource(R.drawable.eye_show);
                    et_newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isnewPass = true;
                } else {
                    // hide password
                    isnewPass = false;
                    iv_newPass.setImageResource(R.drawable.eye_hide);
                    et_newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                break;
        }
    }

    private void updatePassword(EditText oldPassword, EditText newPassword, EditText confirmPassword) {
        String OldPassword = oldPassword.getText().toString().trim();
        String NewPassword = newPassword.getText().toString().trim();
        String ConfirmPassword = confirmPassword.getText().toString().trim();

        String oldPass = sessionManager.getPassword();
        Log.v("oldPass", oldPass);
        Validation validation = new Validation(this);


        if (!validation.isEmpty(OldPassword)) {
            oldPassword.startAnimation(shake);
        } else if (!validation.isEmpty(NewPassword)) {
            newPassword.startAnimation(shake);
        } else if (!validation.isPasswordValid(newPassword)) {
//            Utility.showToast(context, getString(R.string.NEW_PASSWORD_VALID), 0);
            utility.showCustomPopup(this.getString(R.string.NEW_PASSWORD_VALID), String.valueOf(R.font.montserrat_medium));
            newPassword.startAnimation(shake);
        } else if (!validation.isEmpty(ConfirmPassword)) {
            confirmPassword.startAnimation(shake);
        } else if (!validation.isPasswordValid(confirmPassword)) {
            utility.showCustomPopup(this.getString(R.string.NEW_PASSWORD_VALID), String.valueOf(R.font.montserrat_medium));
            //Utility.showToast(context, getString(R.string.NEW_PASSWORD_VALID), 0);
            confirmPassword.startAnimation(shake);
        } else if (!NewPassword.equals(ConfirmPassword)) {
            //Utility.showToast(context, getString(R.string.CANNOT_SAME_PASSWORD), 0);
            utility.showCustomPopup(this.getString(R.string.CANNOT_SAME_PASSWORD), String.valueOf(R.font.montserrat_medium));
            confirmPassword.requestFocus();
        }else if (OldPassword.equals(NewPassword)) {
            //Utility.showToast(context, getString(R.string.SAME_PASSWORD), 0);
            utility.showCustomPopup(this.getString(R.string.SAME_PASSWORD), String.valueOf(R.font.montserrat_medium));
            confirmPassword.requestFocus();
        }else if (!sessionManager.getPassword().equals(OldPassword)) {
            //Utility.showToast(context, getString(R.string.VALID_PASSWORD), 0);
            utility.showCustomPopup(this.getString(R.string.VALID_PASSWORD), String.valueOf(R.font.montserrat_medium));
            oldPassword.startAnimation(shake);
        } else {
            if (utility.checkInternetConnection()) {
                updatePassword_Api(OldPassword, NewPassword, ConfirmPassword);
            } else {
                utility.showCustomPopup(this.getString(R.string.internetConnectivityError), String.valueOf(R.font.montserrat_medium));
                //Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
            }
        }
    }

    private void updatePassword_Api(final String oldPassword, final String newPassword, final String confirmPassword) {

        if (utility.checkInternetConnection()) {

            customProgressBar = new CustomProgressBar(this);
            showProgDialog(false);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.CHANGEPASSWORD, new Response.Listener<NetworkResponse>() {
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
                            Utility.showToast(ChangePasswordActivity.this, message, 0);
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChangePasswordActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    dismissProgDialog();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("oldPassword", oldPassword);
                    params.put("newPassword", newPassword);
                    params.put("userId", userInfo().userid);

                    Utility.e("Login params", params.toString());
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
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

    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(ChangePasswordActivity.this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }


}
