package com.scenekey.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.scenekey.R;
import com.scenekey.aws_service.AWSImage;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.Permission;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.Validation;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleyMultipartRequest;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.scenekey.helper.Constant.REQUEST_ID_MULTIPLE_PERMISSIONS;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 007;
    private final String TAG = "LoginActivity";
    private Context context = this;
    private EditText etEmail, etPwd;
    private Button btnLogin, btnFB, btnGmail;
    private double latitude = 0.0, longiude = 0.0;
    private String fullAddress, city;
    private boolean checkGPS;
    private Permission permission;
    private LocationManager locationManager;
    private CustomProgressBar customProgressBar;
    private Utility utility;
    private CallbackManager objFbCallbackManager;
    private SessionManager sessionManager;
    // New Code
    private Dialog genderDialog;
    private String maleFemale = "", fbUserImage;
    private ImageView imgRegiMale, imgRegiFemale;
    private UserInfo fbUserInfo;
    private Bitmap profileImageBitmap;
    private GoogleApiClient mGoogleApiClient;
    public String loginstatus = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initSdk();
        sessionManager = new SessionManager(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setStatusBarColor();
        initView();
        utility.checkGpsStatus();

        sessionManager.setSoftKey(hasSoftKeys(getWindowManager()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        permission.requestMultiplePermission();
        initLocation();
    }

    private void initSdk() {
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setIsDebugEnabled(true);
        AppEventsLogger.activateApp(this);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        FirebaseApp.initializeApp(context);
    }

    private void initLocation() {
        try {
            // get GPS status
            checkGPS = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // get network provider status
            boolean checkNetwork = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           /* CustomPopup customPopup=new CustomPopup(LoginActivity.this);
            customPopup.setMessage(getString(R.string.eLocationPermission_new));
            customPopup.show();*/
                return;
            }
            if (!checkGPS && !checkNetwork) {
                Utility.e(TAG, "GPS & Provider not available");
                // utility.checkGpsStatus();
            } else {
                if (checkGPS) {
                    assert locationManager != null;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (checkNetwork) {
                    assert locationManager != null;
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bgImage));
        }
    }

    private void initView() {
        etEmail = findViewById(R.id.etEmail);
        etPwd = findViewById(R.id.etPwd);
        btnFB = findViewById(R.id.btnFB);
        btnGmail = findViewById(R.id.btnGmail);

        findViewById(R.id.tvSignUp).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        btnFB.setOnClickListener(this);
        btnGmail.setOnClickListener(this);

        // New Code
        findViewById(R.id.dontHaveAccountLayout).setOnClickListener(this);
        findViewById(R.id.tvForgotPassword).setOnClickListener(this);

        initMember();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initMember() {
        permission = new Permission(context);
        customProgressBar = new CustomProgressBar(context);
        utility = new Utility(context);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvSignUp:
                Intent i = new Intent(context, RegistrationActivity.class);
                startActivity(i);
                break;

            case R.id.btnLogin:
                Validation validation = new Validation(context);
                if (utility.checkInternetConnection() && permission.checkLocationPermission()) {
                    if (validation.isEmailValid(etEmail) && validation.isPasswordValid(etPwd)) {
                        String email = etEmail.getText().toString().trim();
                        String password = etPwd.getText().toString().trim();
                        doLogin(email, password);
                    }
                } else {
                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }
                break;

            case R.id.btnFB:
                if (utility.checkInternetConnection() && permission.checkLocationPermission()) {
                    if (latitude != 0.0d && longiude != 0.0d) {
                        facebookLoginApi();
                    }
                    else if (!checkGPS) {
                        utility.checkGpsStatus();
                    } else {
                        showErrorPopup("facebook");
                    }

                } else {
                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }
                break;

            case R.id.btnGmail:
                if (utility.checkInternetConnection() && permission.checkLocationPermission()) {
                    if (latitude != 0.0d && longiude != 0.0d) {
                        gmialLoginApi();
                    }else if (!checkGPS) {
                        utility.checkGpsStatus();
                    } else {
                        showErrorPopup("gmail");
                    }

                } else {
                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }
                break;


            /*New Code*/
            case R.id.dontHaveAccountLayout:
                Intent in = new Intent(context, RegistrationActivity.class);
                startActivity(in);
                break;

            case R.id.tvForgotPassword:
                //signOut();
                Intent forgotPassword = new Intent(context, ForgotPasswordActivity.class);
                startActivity(forgotPassword);
                break;

            case R.id.dialog_decline_button:
                genderDialog.dismiss();
                break;

            case R.id.btn_select_gender:
                if (!maleFemale.equalsIgnoreCase("")) {
                    fbUserInfo.userGender = maleFemale;
                    doRegistration(fbUserInfo);
                } else {
                    Toast.makeText(context, "Please select gender", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imgRegiMale:
                maleFemale = "male";

                imgRegiMale.setImageResource(R.drawable.active_male_ico);
                imgRegiFemale.setImageResource(R.drawable.inactive_female_ico);
                break;

            case R.id.imgRegiFemale:
                maleFemale = "female";

                imgRegiMale.setImageResource(R.drawable.inactive_male_ico);
                imgRegiFemale.setImageResource(R.drawable.active_female_ico);
                break;
        }
    }


    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //updateUI(false);
                        //Toast.makeText(context, "Gmail Logout", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void doLogin(final String email, final String password) {

        if (utility.checkInternetConnection()) {

            customProgressBar = new CustomProgressBar(context);
            showProgDialog(false);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.NORMAL_LOGIN, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.v("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            dismissProgDialog();

                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");
                            UserInfo userInfo = new UserInfo();

                            userInfo.userid = userDetail.getString("userid");
                            userInfo.userFacebookId = userDetail.getString("userFacebookId");
                            userInfo.socialType = userDetail.getString("socialType");
                            userInfo.userName = userDetail.getString("userName");
                            userInfo.userEmail = userDetail.getString("userEmail");
                            userInfo.mauticContactId = userDetail.getString("mauticContactId");
                            userInfo.fullname = userDetail.getString("fullname");
                            userInfo.lastName = userDetail.getString("lastName");
                            userInfo.password = userDetail.getString("password");
                            userInfo.userImage = userDetail.getString("userImage");
                            userInfo.age = userDetail.getString("age");
                            userInfo.dob = userDetail.getString("dob");
                            userInfo.gender = userDetail.getString("gender");
                            userInfo.userDeviceId = userDetail.getString("userDeviceId");
                            userInfo.deviceType = userDetail.getString("deviceType");
                            userInfo.userGender = userDetail.getString("userGender");
                            userInfo.userStatus = userDetail.getString("userStatus");
                            userInfo.userLastLogin = userDetail.getString("userLastLogin");
                            userInfo.registered_date = userDetail.getString("registered_date");
                            userInfo.usertype = userDetail.getString("usertype");
                            userInfo.artisttype = userDetail.getString("artisttype");
                            userInfo.stagename = userDetail.getString("stagename");
                            userInfo.venuename = userDetail.getString("venuename");
                            userInfo.address = userDetail.getString("address");
                            userInfo.fullAddress = userDetail.getString("fullAddress");
                            userInfo.lat = userDetail.getString("lat");
                            userInfo.longi = userDetail.getString("longi");

                            /*userInfo.adminLat = userDetail.getString("adminLat");
                            userInfo.adminLong = userDetail.getString("adminLong");*/

                            if (userDetail.getString("adminLat").isEmpty()) {
                                userInfo.adminLat = userDetail.getString("lat");
                                userInfo.adminLong = userDetail.getString("longi");
                                userInfo.currentLocation = true;
                            } else {
                                userInfo.adminLat = userDetail.getString("adminLat");
                                userInfo.adminLong = userDetail.getString("adminLong");
                                userInfo.currentLocation = false;
                            }

                            userInfo.user_status = userDetail.getString("user_status");
                            userInfo.makeAdmin = userDetail.getString("makeAdmin");
                            userInfo.key_points = userDetail.getString("key_points");
                            userInfo.bio = userDetail.getString("bio");
                            userInfo.appBadgeCount = userDetail.getString("appBadgeCount");

                            sessionManager.createSession(userInfo);
                            sessionManager.setPassword(password);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            dismissProgDialog();
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        dismissProgDialog();
                    }


                    // Old Code
                    /*try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("SUCCESS")) {
                            dismissProgDialog();

                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");
                            UserInfo userInfo = new UserInfo();
                            userInfo.userID = userDetail.getString("userid");
                            userInfo.facebookId = userDetail.getString("userFacebookId");
                            String socialType = userDetail.getString("socialType");
                            userInfo.userName = userDetail.getString("userName");
                            userInfo.email = userDetail.getString("userEmail");

                           *//* userInfo.fullName = userDetail.getString("fullname");
                            String[] split = userInfo.fullName.split(" ");
                            if (split.length == 2) {
                                userInfo.firstName = split[0].substring(0, 1).toUpperCase() + split[0].substring(1);
                                userInfo.lastName = split[1].substring(0, 1).toUpperCase() + split[1].substring(1);
                            } else {
                                userInfo.firstName = userInfo.fullName.substring(0, 1).toUpperCase() + userInfo.fullName.substring(1);
                                userInfo.lastName = "";
                            }*//*

                            // New Code
                            userInfo.fullName = userDetail.getString("fullname") + " " + userDetail.getString("lastName");
                            userInfo.firstName = userDetail.getString("fullname");
                            userInfo.lastName = userDetail.getString("lastName");

                            userInfo.password = userDetail.getString("password");
                            userInfo.userImage = userDetail.getString("userImage");
                            String age = userDetail.getString("age");
                            String dob = userDetail.getString("dob");
                            String gender = userDetail.getString("gender");
                            String userDeviceId = userDetail.getString("userDeviceId");
                            String deviceType = userDetail.getString("deviceType");
                            userInfo.userGender = userDetail.getString("userGender");
                            String userStatus = userDetail.getString("userStatus");
                            userInfo.loginTime = userDetail.getString("userLastLogin");
                            String registered_date = userDetail.getString("registered_date");
                            String usertype = userDetail.getString("usertype");
                            String artisttype = userDetail.getString("artisttype");
                            String stagename = userDetail.getString("stagename");
                            userInfo.venuName = userDetail.getString("venuename");
                            userInfo.address = userDetail.getString("address");
                            String fullAddress = userDetail.getString("fullAddress");
                            userInfo.latitude = userDetail.getString("lat");
                            userInfo.longitude = userDetail.getString("longi");
                            if (!(userDetail.getString("adminLat").isEmpty() && userDetail.getString("adminLat").isEmpty())) {
                                userInfo.latitude = userDetail.getString("adminLat");
                                userInfo.longitude = userDetail.getString("adminLong");
                            }
                            String user_status = userDetail.getString("user_status");

                            // New Code
                            userInfo.user_status = userDetail.getString("user_status");

                            userInfo.makeAdmin = userDetail.getString("makeAdmin");
                            userInfo.keyPoints = userDetail.getString("key_points");
                            userInfo.bio = userDetail.getString("bio");

                            sessionManager.createSession(userInfo);
                            sessionManager.setPassword(password);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            dismissProgDialog();
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        dismissProgDialog();
                    }*/
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Toast.makeText(LoginActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();

                    dismissProgDialog();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("userEmail", email);
                    params.put("password", password);
                    params.put("userDeviceId", FirebaseInstanceId.getInstance().getToken());
                    params.put("deviceType", "2");

                    Utility.e("Login params", params.toString());
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorPopup(final String type) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_with_btn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //      deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id

        TextView tvCancel, tvTryAgain, tvTitle, tvMessages;

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
                final CustomProgressBar dialog = new CustomProgressBar(LoginActivity.this);
                dialog.show();


                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {
                        dialog.dismiss();
                                if(type.equals("facebook")){
                                    btnFB.callOnClick();
                                }else if(type.equals("gmail")){
                                    btnGmail.callOnClick();
                                }

                    }
                }, 3 * 1000); // wait for 3 seconds
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    /* facebook api start here */
    private void facebookLoginApi() {
        loginstatus = "faceebook";
        objFbCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(objFbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    UserInfo userInfo = new UserInfo();
                                    AccessToken token = AccessToken.getCurrentAccessToken();

                                    userInfo.userFacebookId = object.get("id").toString();
                                    userInfo.userAccessToken = String.valueOf(token);
                                    userInfo.fullname = object.get("name").toString();
                                    userInfo.userImage = "https://graph.facebook.com/" + userInfo.userFacebookId + "/picture?type=large";
                                    userInfo.userGender = "";//object.getString("gender");
                                    userInfo.gender = "";//object.getString("gender");

                                    /*if (object.has("email")) {
                                        userInfo.email = object.getString("email");
                                    } else {
                                        userInfo.email = userInfo.facebookId + ".scenekey" + "@fb.com";
                                    }*/

                                    // New Code
                                    if (object.has("email")) {
                                        userInfo.userEmail = object.getString("email");
                                    } else {
                                        userInfo.userEmail = object.get("name").toString();
                                    }

                                    fbUserImage = userInfo.userImage;
                                    getBitmapFromURL(userInfo.userImage);
                                    checkSocialDetail(userInfo, loginstatus);
                                    getAddressFromLatLong(latitude, longiude);
                                    //registerSocialDetails(userInfo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                //Utility.showToast(context, getString(R.string.cancel), 1);
            }

            @Override
            public void onError(FacebookException error) {
                Utility.showToast(context, error.getMessage(), 1);
            }
        });
    }
    /* facebook api end here */


    /* gmail api start here */
    private void gmialLoginApi() {
        //sessionManager.setLoginType(loginstatus);
        loginstatus = "gmail";
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    /* gmail api end here */


    private void checkSocialDetail(final UserInfo userInfo, final String loginstatus) {
        showProgDialog(false);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.CHECK_FB_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.v("responce", response);
                    // get response
                    JSONObject jsonObject;
                    try {
                        customProgressBar.cancel();

                        jsonObject = new JSONObject(response);
                        Utility.e(" login response", response);
                        //  int statusCode = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        // New Code
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            doRegistration(userInfo);
                        } else {
                            fbUserInfo = userInfo;
                            openSelectGenderDialog();
                        }

                      /*  //registered user
                        if (statusCode == 1) {
                            registerSocialDetails(userInfo);
                        } else if (statusCode == 0) {  //already registered user
                            if (manageSession(jsonObject, userInfo))
                                callIntent(btnFB.getId(),false);
                        } else {
                            Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                        }

*/

                    } catch (Exception ex) {
                        customProgressBar.cancel();
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    customProgressBar.cancel();
                    utility.volleyErrorListner(e);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // New Code
                    params.put("userFacebookId", userInfo.userFacebookId);
                    params.put("socialType", loginstatus);

                    Utility.e("Params", params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this.getBaseContext()).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(etEmail, getString(R.string.internetConnectivityError), 0);
            customProgressBar.cancel();
        }

    }

   /* private void registerSocialDetails(final UserInfo userInfo) {
        showProgDialog(false);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        customProgressBar.cancel();
                        // System.out.println(" login response" + response);
                        jsonObject = new JSONObject(Response);
                        int statusCode = jsonObject.getInt("success");
                        String message = jsonObject.getString("msg");

                        //registered user
                        if (statusCode == 1) {
                            manageSession(jsonObject, userInfo);
                            callIntent(btnFB.getId(), true);
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        showProgDialog(false);
                                        urlToBitmap();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else if (statusCode == 0) {
                            Utility.showToast(context, message, 0);
                            Utility.e(TAG, message);
                        } else {
                            Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                        }

                    } catch (Exception ex) {
                        customProgressBar.cancel();
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    customProgressBar.cancel();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userEmail", userInfo.email);
                    params.put("facebookid", userInfo.facebookId);
                    params.put("fbusername", userInfo.fullName);
                    params.put("usertype", "Social User");
                    params.put("fullname", userInfo.fullName);
                    params.put("device_token", FirebaseInstanceId.getInstance().getToken());
                    params.put("deviceType", "2");
                    params.put("gender", userInfo.userGender);
                    params.put("ProfileImage", userInfo.userImage);
                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longiude));
                    return params;
                }
            };
            VolleySingleton.getInstance(this.getBaseContext()).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(etEmail, getString(R.string.internetConnectivityError), 0);
            customProgressBar.cancel();
        }
    }*/

    // Old Code
    private void urlToBitmap() {
        final AWSImage awsImage = new AWSImage(this);

        String image = sessionManager.getUserInfo().getUserImage();
        if (image != null && !image.equals("")) {
            Picasso.with(this).load(image).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    dismissProgDialog();
                    try {
                        if (bitmap != null)
                            awsImage.initItem(bitmap);
                    } catch (Exception e) {
                        // some action
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    dismissProgDialog();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    dismissProgDialog();
                }
            });
        }
        dismissProgDialog();
    }

    private void callIntent(int id, boolean isShow) {
        switch (id) {
            case R.id.btnFB:
                if (isShow) {
                    Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                    startActivity(intent);
                } else {

                    Intent i = new Intent(context, HomeActivity.class);
                    i.putExtra(Constant.LATITUDE, latitude + "");
                    i.putExtra(Constant.LONGITUDE, longiude + "");
                    startActivity(i);
                    finish();
                }
                break;

        }
    }

    private boolean manageSession(JSONObject jsonObject, UserInfo uInfo) {
        /*try {
            JSONObject objUserDetails = jsonObject.getJSONObject("userinfo");
            UserInfo userInfo = new UserInfo();
            userInfo.userID = (objUserDetails.getString("userID"));
            userInfo.email = (objUserDetails.getString("email"));
            userInfo.fullName = (objUserDetails.getString("fullname"));
            userInfo.userName = (objUserDetails.getString("userName"));
            userInfo.userGender = (objUserDetails.getString("userGender"));
            if (objUserDetails.getString("userImage").isEmpty()) {
                userInfo.userImage = uInfo.userImage;
            } else {
                userInfo.userImage = (objUserDetails.getString("userImage"));
            }
            userInfo.loginTime = (objUserDetails.getString("logintime"));
            userInfo.stageName = (objUserDetails.getString("stagename"));
            userInfo.venuName = (objUserDetails.getString("venuename"));
            userInfo.artistType = (objUserDetails.getString("artisttype"));
            userInfo.firstName = (objUserDetails.getString("firstname"));
            userInfo.lastName = (objUserDetails.getString("lastname"));
            userInfo.environment = (objUserDetails.getString("environment"));
            userInfo.facebookId = uInfo.facebookId;

            if (objUserDetails.has("lat")) userInfo.latitude = (objUserDetails.getString("lat"));
            if (objUserDetails.has("longi"))
                userInfo.longitude = (objUserDetails.getString("longi"));
            if (objUserDetails.has("address"))
                userInfo.address = (objUserDetails.getString("address"));
            if (objUserDetails.has("bio")) userInfo.bio = (objUserDetails.getString("bio"));
            if (objUserDetails.has("keyPoints"))
                userInfo.keyPoints = (objUserDetails.getString("keyPoints"));
            if (objUserDetails.has("makeAdmin"))
                userInfo.makeAdmin = (objUserDetails.getString("makeAdmin"));
            userInfo.userAccessToken = uInfo.userAccessToken;
            Utility.e("Auth Token", userInfo.userAccessToken);
            userInfo.firstTimeDemo = (true);

            Utility.e("session data", jsonObject.toString());
            //  Utility.showToast(context, message, 1);
            SceneKey.sessionManager.createSession(userInfo);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }*/
        return false;
    }

    private void showProgDialog(boolean b) {
        customProgressBar.setCanceledOnTouchOutside(b);
        customProgressBar.setCancelable(b);
        customProgressBar.show();
    }

    private void dismissProgDialog() {
        if (customProgressBar != null) customProgressBar.dismiss();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // Logic to handle location object
            latitude = location.getLatitude();
            longiude = location.getLongitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Utility.e("Latitude", "disable");
        if (provider.equals("network")) {
            utility.checkGpsStatus();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Utility.e("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Utility.e("Latitude", "status");
    }

    private boolean hasSoftKeys(WindowManager windowManager) {
        boolean hasSoftwareKeys = true;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = windowManager.getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    /* on activity result start */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (loginstatus.equals("faceebook")) {

            Log.v("requestCode",""+requestCode);
            objFbCallbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (loginstatus.equals("gmail")) {

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
    }
    /* on activity result end*/

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean locationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean externalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    initLocation();
                    if (cameraPermission && locationPermission && externalStoragePermission) {

                    } else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        //signOut();
        if (result.isSuccess()) {
            UserInfo userInfo = new UserInfo();

            // Signe in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String str = acct.getDisplayName();
            String[] fullName = str.split("\\s+");
            for (int i = 0; i < fullName.length; i++) {
                if (i == 0) {
                    userInfo.userName ="";
                    userInfo.userName = fullName[0];
                } else if (i == 1) {
                    userInfo.lastName="";
                    userInfo.lastName = fullName[1];
                }
            }

            userInfo.fullname = userInfo.userName+" "+ userInfo.lastName;
           /* String firstName = fullName[0];
            String lastName = fullName[1];*/

            userInfo.userFacebookId = acct.getId();
            userInfo.userImage = "";
            if (acct.getPhotoUrl() != null)
                userInfo.userImage = acct.getPhotoUrl().toString();
            userInfo.userEmail = acct.getEmail();
            userInfo.userAccessToken = FirebaseInstanceId.getInstance().getToken();
            userInfo.gender = "";

            if (userInfo.userImage != "" && userInfo.userImage != null) {
                getBitmapFromURL(userInfo.userImage);
            }

            checkSocialDetail(userInfo, loginstatus);
            getAddressFromLatLong(latitude, longiude);

        } else {
            //signOut();
        }
    }

    // New Code
    private void doRegistration(final UserInfo userInfo) {

        if (utility.checkInternetConnection()) {

            customProgressBar = new CustomProgressBar(context);
            showProgDialog(false);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.REGISTRATION, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    Log.v("respose", ""+response);
                    String data = new String(response.data);
                    Log.e("Response", data);

                    signOut();
                    LoginManager.getInstance().logOut();

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        String messageCode = jsonObject.getString("messageCode");

                        // New Code
                        if (message.equalsIgnoreCase("User registered successfully")) {

                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");
                            UserInfo userInfo = new UserInfo();
                            userInfo.userid = userDetail.getString("userid");
                            userInfo.userFacebookId = userDetail.getString("userFacebookId");
                            userInfo.socialType = userDetail.getString("socialType");
                            userInfo.userName = userDetail.getString("userName");
                            userInfo.userEmail = userDetail.getString("userEmail");
                            userInfo.mauticContactId = userDetail.getString("mauticContactId");
                            userInfo.fullname = userDetail.getString("fullname");
                            userInfo.lastName = userDetail.getString("lastName");
                            userInfo.password = userDetail.getString("password");
                            userInfo.userImage = userDetail.getString("userImage");
                            userInfo.age = userDetail.getString("age");
                            userInfo.dob = userDetail.getString("dob");
                            userInfo.gender = userDetail.getString("gender");
                            userInfo.userDeviceId = userDetail.getString("userDeviceId");
                            userInfo.deviceType = userDetail.getString("deviceType");
                            userInfo.userGender = userDetail.getString("userGender");
                            userInfo.userStatus = userDetail.getString("userStatus");
                            userInfo.userLastLogin = userDetail.getString("userLastLogin");
                            userInfo.registered_date = userDetail.getString("registered_date");
                            userInfo.usertype = userDetail.getString("usertype");
                            userInfo.artisttype = userDetail.getString("artisttype");
                            userInfo.stagename = userDetail.getString("stagename");
                            userInfo.venuename = userDetail.getString("venuename");
                            userInfo.address = userDetail.getString("address");
                            userInfo.fullAddress = userDetail.getString("fullAddress");
                            userInfo.lat = userDetail.getString("lat");
                            userInfo.longi = userDetail.getString("longi");

                            if (userDetail.getString("adminLat").isEmpty()) {
                                userInfo.adminLat = userDetail.getString("lat");
                                userInfo.adminLong = userDetail.getString("longi");
                                userInfo.currentLocation = true;
                            } else {
                                userInfo.adminLat = userDetail.getString("adminLat");
                                userInfo.adminLong = userDetail.getString("adminLong");
                                userInfo.currentLocation = false;
                            }

                            userInfo.user_status = userDetail.getString("user_status");
                            userInfo.makeAdmin = userDetail.getString("makeAdmin");
                            userInfo.key_points = userDetail.getString("key_points");
                            userInfo.bio = userDetail.getString("bio");
                            userInfo.appBadgeCount = userDetail.getString("appBadgeCount");

                            sessionManager.createSession(userInfo);

                            ImageSessionManager.getInstance().setScreenFlag(1);

                            ImageSessionManager.getInstance().createImageSession(fbUserImage, false);
                            AWSImage awsImage = new AWSImage(LoginActivity.this);

                            try {
                                if (profileImageBitmap != null) {
                                    awsImage.initItem(profileImageBitmap);
                                    dismissProgDialog();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                dismissProgDialog();
                            }
                            dismissProgDialog();

                            Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        } else if (message.equalsIgnoreCase("Logged in successfully")) {

                            JSONObject userDetail = jsonObject.getJSONObject("userDetail");
                            UserInfo userInfo = new UserInfo();
                            userInfo.userid = userDetail.getString("userid");
                            userInfo.userFacebookId = userDetail.getString("userFacebookId");
                            userInfo.socialType = userDetail.getString("socialType");
                            userInfo.userName = userDetail.getString("userName");
                            userInfo.userEmail = userDetail.getString("userEmail");
                            userInfo.mauticContactId = userDetail.getString("mauticContactId");
                            userInfo.fullname = userDetail.getString("fullname");
                            userInfo.lastName = userDetail.getString("lastName");
                            userInfo.password = userDetail.getString("password");
                            userInfo.userImage = userDetail.getString("userImage");
                            userInfo.age = userDetail.getString("age");
                            userInfo.dob = userDetail.getString("dob");
                            userInfo.gender = userDetail.getString("gender");
                            userInfo.userDeviceId = userDetail.getString("userDeviceId");
                            userInfo.deviceType = userDetail.getString("deviceType");
                            userInfo.userGender = userDetail.getString("userGender");
                            userInfo.userStatus = userDetail.getString("userStatus");
                            userInfo.userLastLogin = userDetail.getString("userLastLogin");
                            userInfo.registered_date = userDetail.getString("registered_date");
                            userInfo.usertype = userDetail.getString("usertype");
                            userInfo.artisttype = userDetail.getString("artisttype");
                            userInfo.stagename = userDetail.getString("stagename");
                            userInfo.venuename = userDetail.getString("venuename");
                            userInfo.address = userDetail.getString("address");
                            userInfo.fullAddress = userDetail.getString("fullAddress");
                            userInfo.lat = userDetail.getString("lat");
                            userInfo.longi = userDetail.getString("longi");

                            /*userInfo.adminLat = userDetail.getString("adminLat");
                            userInfo.adminLong = userDetail.getString("adminLong");*/

                            if (userDetail.getString("adminLat").isEmpty()) {
                                userInfo.adminLat = userDetail.getString("lat");
                                userInfo.adminLong = userDetail.getString("longi");
                                userInfo.currentLocation = true;
                            } else {
                                userInfo.adminLat = userDetail.getString("adminLat");
                                userInfo.adminLong = userDetail.getString("adminLong");
                                userInfo.currentLocation = false;
                            }

                            userInfo.user_status = userDetail.getString("user_status");
                            userInfo.makeAdmin = userDetail.getString("makeAdmin");
                            userInfo.key_points = userDetail.getString("key_points");
                            userInfo.bio = userDetail.getString("bio");
                            userInfo.appBadgeCount = userDetail.getString("appBadgeCount");

                            sessionManager.createSession(userInfo);
                            dismissProgDialog();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    } catch (Throwable t) {
                        dismissProgDialog();
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Toast.makeText(LoginActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    dismissProgDialog();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // New Code
                    params.put("userEmail", userInfo.userEmail);
                    params.put("password", "123456");
                    params.put("fullname", userInfo.fullname);
                    params.put("fullAddress", fullAddress);
                    params.put("address", city);
                    params.put("userGender", userInfo.userGender);
                    params.put("lat", String.valueOf(latitude));
                    params.put("longi", String.valueOf(longiude));
                    params.put("userDeviceId", FirebaseInstanceId.getInstance().getToken());
                    params.put("deviceType", "2");
                    params.put("fbusername", userInfo.fullname);
                    params.put("userFacebookId", userInfo.userFacebookId);
                    params.put("socialType", loginstatus);

                    Utility.e("Registration send data", params.toString());
                    return params;
                }

               /* @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<String, DataPart>();
                    if (profileImageBitmap != null) {
                        params.put("profileImage", new VolleyMultipartRequest.DataPart("profilePic.jpg", AppHelper.getFileDataFromDrawable(profileImageBitmap), "image/jpeg"));
                    }
                    return params;
                }*/
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }

    private void openSelectGenderDialog() {
        genderDialog = new Dialog(this);
        genderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        genderDialog.setContentView(R.layout.select_gender_dialog);

        WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
        lWindowParams.copyFrom(genderDialog.getWindow().getAttributes());
        lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        genderDialog.getWindow().setAttributes(lWindowParams);

        imgRegiMale = genderDialog.findViewById(R.id.imgRegiMale);
        imgRegiFemale = genderDialog.findViewById(R.id.imgRegiFemale);

        imgRegiMale.setOnClickListener(this);
        imgRegiFemale.setOnClickListener(this);

        genderDialog.findViewById(R.id.dialog_decline_button).setOnClickListener(this);

        genderDialog.findViewById(R.id.btn_select_gender).setOnClickListener(this);

        genderDialog.getWindow().setGravity(Gravity.CENTER);
        genderDialog.show();
    }

    private void getAddressFromLatLong(double latitude, double longiude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longiude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.get(0).getAddressLine(0) != null) {
                fullAddress = addresses.get(0).getAddressLine(0);
            } else {
                fullAddress = "";
            }

            if (addresses.get(0).getLocality() != null) {
                city = addresses.get(0).getLocality();
            } else if (addresses.get(0).getSubAdminArea() != null) {
                city = addresses.get(0).getSubAdminArea();
            } else {
                city = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBitmapFromURL(String image) {
        if (image != null && !image.equals("")) {
            Picasso.with(LoginActivity.this).load(image).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    dismissProgDialog();
                    try {
                        if (bitmap != null)
                            profileImageBitmap = bitmap;
                    } catch (Exception e) {
                        // some action
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    dismissProgDialog();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    dismissProgDialog();
                }
            });
        }
        dismissProgDialog();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
