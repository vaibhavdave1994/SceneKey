package com.scenekey.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.Retrofitprocess.RetrofitClient;
import com.scenekey.activity.invite_friend.InviteFriendsActivity;
import com.scenekey.activity.new_reg_flow.RegistrationActivityNewCreatePassword;
import com.scenekey.activity.new_reg_flow.RegistrationActivityNewEmail;
import com.scenekey.aws_service.AWSImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.Permission;
import com.scenekey.helper.Pop_Up_Option;
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
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.scenekey.helper.Constant.MY_PERMISSIONS_REQUEST_LOCATION;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "RegistrationActivity";
    private static final int RC_SIGN_IN = 007;
    private Context context = this;
    private EditText etRegiFirstName, etRegiLastName, etRegiEmail, etRegiPwd;
    private Button btnRegiSignUp, btnGmailSignUp, btnFBSignUp;
    private ImageView imgRegiMale, imgRegiFemale, imgUserImage, dialog_decline_button;
    private Utility utility;
    private CustomProgressBar customProgressBar;
    private SessionManager sessionManager;

    /*private String maleFemale = "male";*/
    private Double latitude = 0.0, longitude = 0.0;
    private String imageKey, fullAddress, city;
    private LocationManager locationManager;
    private boolean checkGPS;
    private Bitmap profileImageBitmap;
    protected Pop_Up_Option pop_up_option;
    private Permission permission;
    private String loginstatus = "";
    private GoogleApiClient mGoogleApiClient;
    // New Code
    private CheckBox cb_terms;
    private String maleFemale = "", profileImageUrl, mCurrentPhotoPath;
    private UserInfo fbUserInfo;
    private RelativeLayout registerView;
    private Dialog genderDialog;
    private CallbackManager objFbCallbackManager;
    private String fbUserImage = "";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setStatusBarColor();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        utility = new Utility(this);
        sessionManager = new SessionManager(this);
        initView();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.bgImage));
        }
    }

    private void initView() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        customProgressBar = new CustomProgressBar(context);
        etRegiFirstName = findViewById(R.id.etRegiFirstName);
        etRegiLastName = findViewById(R.id.etRegiLastName);
        etRegiEmail = findViewById(R.id.etRegiEmail);
        etRegiPwd = findViewById(R.id.etRegiPwd);
        imgRegiMale = findViewById(R.id.imgRegiMale);
        imgRegiFemale = findViewById(R.id.imgRegiFemale);
        imgUserImage = findViewById(R.id.imgUserImage);
        registerView = findViewById(R.id.registerView);

        imgRegiMale.setOnClickListener(this);
        imgRegiFemale.setOnClickListener(this);
        btnRegiSignUp = findViewById(R.id.btnRegiSignUp);
        btnGmailSignUp = findViewById(R.id.btnGmailSignUp);
        btnFBSignUp = findViewById(R.id.btnFBSignUp);
        btnRegiSignUp.setOnClickListener(this);
        btnGmailSignUp.setOnClickListener(this);
        btnFBSignUp.setOnClickListener(this);

        findViewById(R.id.tvRegiLogin).setOnClickListener(this);
        findViewById(R.id.relativeImgProfilePic).setOnClickListener(this);

        //New code
        cb_terms = findViewById(R.id.cb_terms);

        initMembers();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initMembers() {
        permission = new Permission(context);
        permission.checkLocationPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        permission.requestMultiplePermission();
        initLocation();
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

    @Override
    public void onClick(View v) {
        Utility utility = new Utility(context);

        switch (v.getId()) {
            case R.id.tvRegiLogin:
                Intent iLogin = new Intent(context, LoginActivity.class);
                // Closing all the Activities
                iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(iLogin);
                finish();
                break;

            case R.id.btnRegiSignUp:
                Validation validation = new Validation(context);
                if (utility.checkInternetConnection()) {
                    if (validation.isImageUpload(profileImageBitmap) && validation.isFirstNameValid(etRegiFirstName) && validation.isLastNameValid(etRegiLastName) && validation.isEmailValid(etRegiEmail) && validation.isPasswordValid(etRegiPwd)) {
                        String firstName = etRegiFirstName.getText().toString().trim();
                        String lastName = etRegiLastName.getText().toString().trim();
                        String email = etRegiEmail.getText().toString().trim();
                        String pwd = etRegiPwd.getText().toString().trim();

                        // New Code
                        if (latitude != 0.0d && longitude != 0.0d) {
                            getAddressFromLatLong(latitude, longitude);

                            if (cb_terms.isChecked()) {
                                doRegistration(firstName, lastName, email, pwd, maleFemale, "", profileImageBitmap, "");
                            } else {
                                //Toast.makeText(context, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
                                utility.showCustomPopup("Please accept terms and conditions.", String.valueOf(R.font.montserrat_medium));
                            }

                        } else if (!checkGPS) {
                            utility.checkGpsStatus();
                        } else {
                            showErrorPopup("register");
                        }
                    }

                } else {
                    Utility.showCheckConnPopup(context, "No network connection", "", "");
//                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }

                break;

            case R.id.btnGmailSignUp:
                if (utility.checkInternetConnection() && permission.checkLocationPermission()) {
                    if (latitude != 0.0d && longitude != 0.0d) {
                        gmialLoginApi();
                    } else if (!checkGPS) {
                        utility.checkGpsStatus();
                    } else {
                        showErrorPopup("gmail");
                    }

                } else {
                    Utility.showCheckConnPopup(context, "No network connection", "", "");
//                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }
                break;

            case R.id.btnFBSignUp:
                if (utility.checkInternetConnection() && permission.checkLocationPermission()) {
                    if (latitude != 0.0d && longitude != 0.0d) {
                        facebookLoginApi();
                    } else if (!checkGPS) {
                        utility.checkGpsStatus();
                    } else {
                        showErrorPopup("facebook");
                    }

                } else {
                    Utility.showCheckConnPopup(context, "No network connection", "", "");
//                    Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
                }

                break;

            case R.id.relativeImgProfilePic:
                pop_up_option = initializePopup();
                pop_up_option.setObject(null);
                pop_up_option.show();
                break;

            case R.id.imgRegiMale:
                maleFemale = "male";

//                imgRegiMale.setImageResource(R.drawable.active_male_ico);
//                imgRegiFemale.setImageResource(R.drawable.inactive_female_ico);
                break;

            case R.id.imgRegiFemale:
                maleFemale = "female";

//                imgRegiMale.setImageResource(R.drawable.inactive_male_ico);
//                imgRegiFemale.setImageResource(R.drawable.active_female_ico);
                break;
        }
    }

    /* facebook api start here */
    protected void facebookLoginApi() {
        if (utility.checkInternetConnection() && permission.checkLocationPermission()) {
            if (latitude != 0.0d && longitude != 0.0d) {

                showProgDialog(false);
                loginstatus = "facebook";
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
                                            userInfo.fullname = object.get("first_name").toString();
                                            userInfo.lastName = object.get("last_name").toString();
                                            userInfo.userImage = "https://graph.facebook.com/" + userInfo.userFacebookId + "/picture?type=large";
                                            userInfo.userGender = "";//object.getString("gender");
                                            userInfo.gender = "";//object.getString("gender");
                                            userInfo.loginstatus = "facebook";
                                            // New Code
                                            if (object.has("email")) {
                                                userInfo.userEmail = object.getString("email");
                                            } else {
                                                userInfo.userEmail = object.get("name").toString();
                                            }

                                            fbUserImage = userInfo.userImage;
                                            if (fbUserImage != null && !fbUserImage.equalsIgnoreCase("")) {
                                                getBitmapFromURL(userInfo.userImage, userInfo);
                                            } else {
                                                checkSocialDetail(userInfo, loginstatus, userInfo.userFacebookId, userInfo.userEmail);
                                                getAddressFromLatLong(latitude, longitude);
                                            }
                                            //registerSocialDetails(userInfo);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        //Utility.showToast(context, getString(R.string.cancel), 1);
                        dismissProgDialog();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        dismissProgDialog();
                        if (error instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        Utility.showToast(context, error.getMessage(), 1);
                        Log.e("facebook_error",error.getMessage());
                    }
                });

            } else if (!checkGPS) {
                //initLocation();
                utility.checkGpsStatus();
            } else {
                initLocation();
                showErrorPopup("facebook");
            }

        } else {
            Utility.showCheckConnPopup(context, "No network connection", "", "");
//            Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
        }
    }
    /* facebook api end here */

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    protected void gmialLoginApi() {
        if (utility.checkInternetConnection() && permission.checkLocationPermission()) {
            if (latitude != 0.0d && longitude != 0.0d) {
                loginstatus = "gmail";
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            } else if (!checkGPS) {

                utility.checkGpsStatus();
            } else {
                initLocation();
                showErrorPopup("gmail");
            }

        } else {
            Utility.showCheckConnPopup(context, "No network connection", "", "");
//            Utility.showToast(context, getString(R.string.internetConnectivityError), 0);
        }
    }

    protected void showErrorPopup(final String type) {
        final Dialog dialog = new Dialog(context,R.style.DialogTheme);
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
                final CustomProgressBar dialog = new CustomProgressBar(context);
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {
                        dialog.dismiss();

                        //btnRegiSignUp.callOnClick();
                        if (type.equals("facebook")) {
                            // btnFBSignUp.callOnClick();
                            facebookLoginApi();
                        } else if (type.equals("gmail")) {
                            // btnRegiSignUp.callOnClick();
                            gmialLoginApi();
                        } else {
                            //btnRegiSignUp.callOnClick();
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

    // New Code
    private void doNewRegistration(final String firstName, final String lastName, final String email, final String pwd, final String maleFemale, final String userFbAndGmail) {

        if (utility.checkInternetConnection()) {

            customProgressBar = new CustomProgressBar(context);
            showProgDialog(false);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.REGISTRATION, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    Log.v("respose", "" + response);
                    String data = new String(response.data);
                    Log.e("Response", data);

                    //signOut();
                    //LoginManager.getInstance().logOut();

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
                            AWSImage awsImage = new AWSImage(RegistrationActivity.this);

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

                            Intent intent = new Intent(RegistrationActivity.this, IntroActivity.class);
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

                            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
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
                    Toast.makeText(RegistrationActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    dismissProgDialog();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // New Code
                    params.put("userEmail", email);
                    params.put("password", "123456");
                    params.put("fullname", firstName);
                    params.put("fullAddress", fullAddress);
                    params.put("address", city);
                    params.put("userGender", maleFemale);
                    params.put("lat", String.valueOf(latitude));
                    params.put("longi", String.valueOf(longitude));
                    params.put("userDeviceId", FirebaseInstanceId.getInstance().getToken());
                    params.put("deviceType", "2");
                    params.put("fbusername", firstName);
                    params.put("userFacebookId", userFbAndGmail);
                    params.put("socialType", loginstatus);

                    Utility.e("Registration send data", params.toString());
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(RegistrationActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
//            Toast.makeText(RegistrationActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }


    //Old Api
    protected void doRegistration(final String firstName, final String lastName, final String email, final String pwd, final String maleFemale, final String userFbAndGmail
            , Bitmap profileImageBitmap1, final String loginstatus) {

        profileImageBitmap = profileImageBitmap1;

        if (latitude != 0.0d && longitude != 0.0d) {
            getAddressFromLatLong(latitude, longitude);
        } else if (!checkGPS) {
            RegistrationActivityNewCreatePassword.click =0;
            utility.checkGpsStatus();
            return;
        } else {
            RegistrationActivityNewCreatePassword.click =0;
            showErrorPopup("register");
            return;
        }
        showProgDialog(false);
        if (utility.checkInternetConnection()) {

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebServices.REGISTRATION, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    //Logout From Facebook and gmail
//                    ............................................................................
                    //LoginManager.getInstance().logOut();
                    //signOut();
                    // New Code
                    ImageSessionManager.getInstance().setScreenFlag(1);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {

                            if (message.equalsIgnoreCase("Logged in successfully")) {
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
                                sessionManager.setPassword(pwd);
                                dismissProgDialog();

                                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
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
                                sessionManager.setPassword(pwd);

                                //AWSImage awsImage = new AWSImage(RegistrationActivity.this);
                                try {
                                    if (profileImageBitmap == null) {
                                        profileImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_default_profile);
                                    }

                                    if (profileImageBitmap != null) {
                                        // awsImage.initItem(profileImageBitmap);
                                        uploadNewBase64(SceneKey.sessionManager.getUserInfo().userFacebookId, profileImageBitmap);
                                        //dismissProgDialog();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dismissProgDialog();
                                }
                                // dismissProgDialog();
                            }

                        } else {
                            RegistrationActivityNewCreatePassword.click =0;
                            dismissProgDialog();
                            Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegistrationActivity.this, networkResponse + "", Toast.LENGTH_SHORT).show();
                    dismissProgDialog();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // New Code
                    params.put("fullname", firstName);
                    params.put("lastName", lastName);
                    params.put("userDeviceId", FirebaseInstanceId.getInstance().getToken());
                    params.put("deviceType", "2");
                    params.put("userEmail", email);
                    params.put("password", pwd);
                    params.put("userGender", maleFemale);
                    params.put("lat", String.valueOf(latitude));
                    params.put("longi", String.valueOf(longitude));
                    params.put("fullAddress", fullAddress);
                    params.put("address", city);
                    params.put("userFacebookId", userFbAndGmail);
                    params.put("socialType", loginstatus);

                    Utility.e("Registration send data", params.toString());
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(RegistrationActivity.this).addToRequestQueue(multipartRequest);
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
//            Toast.makeText(RegistrationActivity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
        }
    }

    public Pop_Up_Option initializePopup() {
        pop_up_option = new Pop_Up_Option(context) {
            @Override
            public void onGalleryClick(Pop_Up_Option dialog, Object object) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        callIntent(Constant.MY_PERMISSIONS_REQUEST_EXTERNAL);
                    } else {
                        callIntent(Constant.RESULT_LOAD);
                    }
                } else {
                    callIntent(Constant.RESULT_LOAD);
                }
                dialog.dismiss();
            }

            @Override
            public void onCameraClick(Pop_Up_Option dialog, Object object) {
                // New Code
                dispatchTakePictureIntent();
                dialog.dismiss();
            }
        };
        return pop_up_option;
    }

    public void callIntent(int caseId) {

        switch (caseId) {
            case Constant.INTENT_CAMERA:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Constant.RESULT_LOAD:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent, Constant.RESULT_LOAD);
                break;

            case Constant.REQUEST_CAMERA:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            Constant.MY_PERMISSIONS_REQUEST_CAMERA);
                }
                break;

            case Constant.MY_PERMISSIONS_REQUEST_EXTERNAL:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.MY_PERMISSIONS_REQUEST_EXTERNAL);
                }
                break;
        }
    }

    protected void showProgDialog(boolean b) {
        if (customProgressBar != null) {
            if (!customProgressBar.isShowing()) {
                customProgressBar.setCanceledOnTouchOutside(b);
                customProgressBar.setCancelable(b);
                customProgressBar.show();
            }
        }
    }

    private void dismissProgDialog() {
        if (customProgressBar != null) customProgressBar.dismiss();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // Logic to handle location object
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    // New Code
    private void getAddressFromLatLong(Double latitude, Double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

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

    @Override
    public void onProviderDisabled(String provider) {
        Utility.e("Latitude", "disable");
        if (provider.equals("network")) {
            try {
                utility.checkGpsStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    // New Code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   Bitmap bitmap;

        if (loginstatus.equals("facebook")) {

            objFbCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (loginstatus.equals("gmail")) {

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }

        if (resultCode != 0) {
            switch (requestCode) {
                case Constant.RESULT_LOAD:
                    Uri uri = data.getData();
                    long time = System.currentTimeMillis();
                    String str = String.valueOf(time);
                    String destinatiomPath = str + "dovies.jpg";

                    UCrop.Options options = new UCrop.Options();
                    options.setHideBottomControls(true);
                    UCrop.of(uri, Uri.fromFile(new File(destinatiomPath)))
                            .withAspectRatio(1f, 1f)
                            .withMaxResultSize(450, 450)
                            .withOptions(options)
                            .start(this);
                    break;

                case Constant.REQUEST_CAMERA:
                    // New Code
                    UCrop.Options options1 = new UCrop.Options();
                    options1.setHideBottomControls(true);
                    Uri uri1 = Uri.fromFile(new File(mCurrentPhotoPath));
                    UCrop.of(uri1, Uri.fromFile(new File(mCurrentPhotoPath)))
                            .withAspectRatio(1f, 1f)
                            .withMaxResultSize(450, 450)
                            .withOptions(options1)
                            .start(this);
                    break;

                // New Code
                case UCrop.REQUEST_CROP:// Image Cropper
                    final Uri result = UCrop.getOutput(data);
                    try {
                        if (result != null) {
                            profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                            profileImageUrl = Utility.getImageUri(this, profileImageBitmap).toString();
                            String uri_path = Utility.getRealPathFromURI(this, Utility.getImageUri(this, profileImageBitmap));
                            ImageSessionManager.getInstance().createImageSession(uri_path, false);

                            Log.e("UPLOAD PATH", uri_path);
                            Picasso.with(RegistrationActivity.this).load(profileImageUrl).into(imgUserImage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.alertImageException), Toast.LENGTH_SHORT).show();
                    } catch (OutOfMemoryError error) {
                        Toast.makeText(context, getResources().getString(R.string.alertOutOfMemory), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 2:
//                    maleFemale=data.getStringExtra("gender");
//
//                    if (!maleFemale.equalsIgnoreCase("")) {
//                        fbUserInfo.userGender = maleFemale;
//                        showProgDialog(false);
//                        //doRegistration(fbUserInfo.fullname, fbUserInfo.lastName, fbUserInfo.userEmail, fbUserInfo.password, fbUserInfo.userGender, fbUserInfo.userFacebookId,profileImageBitmap);
//                    } else {
//                        Toast.makeText(context, "Please select gender", Toast.LENGTH_SHORT).show();
//                    }

                    break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            final UserInfo userInfo = new UserInfo();

            // Signe in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            assert acct != null;
            String str = acct.getDisplayName();
            assert str != null;
            String[] fullName = str.split("\\s+");
            for (int i = 0; i < fullName.length; i++) {
                if (i == 0) {
                    userInfo.userName = "";
                    userInfo.userName = fullName[0];
                } else if (i == 1) {
                    userInfo.lastName = "";
                    userInfo.lastName = fullName[1];
                }
            }

            userInfo.fullname = userInfo.userName + " " + userInfo.lastName;
            userInfo.userFacebookId = acct.getId();
            userInfo.userImage = "";
            if (acct.getPhotoUrl() != null)
                userInfo.userImage = acct.getPhotoUrl().toString();
//                userInfo.userImage = acct.getPhotoUrl().getPath();
            userInfo.userEmail = acct.getEmail();
            userInfo.userAccessToken = FirebaseInstanceId.getInstance().getToken();
            userInfo.gender = "";
            userInfo.loginstatus = "gmail";

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (userInfo.userImage != "" && userInfo.userImage != null) {
                                getBitmapFromURL(userInfo.userImage, userInfo);
                            } else {
                                checkSocialDetail(userInfo, loginstatus, userInfo.userFacebookId, userInfo.userEmail);
                                getAddressFromLatLong(latitude, longitude);
                            }
                        }
                    });

        } else {
            signOut();
        }
    }

    private void checkSocialDetail(final UserInfo userInfo, final String loginstatus, final String userloginid, final String email) {
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
                        //int statusCode = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        // New Code
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            doRegistration(userInfo.fullname, userInfo.lastName, userInfo.userEmail, userInfo.password, userInfo.userGender, userInfo.userFacebookId, profileImageBitmap, loginstatus);
                            //doNewRegistration(userInfo.fullname, userInfo.lastName, userInfo.userEmail, userInfo.password, userInfo.userGender, userInfo.userFacebookId);
                        } else {
                            fbUserInfo = userInfo;
                            dismissProgDialog();
                            startActivity(new Intent(RegistrationActivity.this, RegistrationActivityNewEmail.class)
                                    .putExtra("userInfo", fbUserInfo));

//                            Intent intentForResult = new Intent(RegistrationActivity.this, RegistrationActivityNewGender.class);
//                            intentForResult.putExtra("from","social");
//                            startActivityForResult(intentForResult,2);

                            //openSelectGenderDialog(userInfo);
                        }

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
                    params.put("userFacebookId", userloginid);
                    params.put("socialType", loginstatus);
                    params.put("email", email);

                    Utility.e("Params", params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(this.getBaseContext()).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
//            utility.snackBar(registerView, getString(R.string.internetConnectivityError), 0);
            customProgressBar.cancel();
        }
    }

    private void openSelectGenderDialog(final UserInfo userInfo) {
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
        dialog_decline_button = genderDialog.findViewById(R.id.dialog_decline_button);
        TextView btn_select_gender = genderDialog.findViewById(R.id.btn_select_gender);

        imgRegiMale.setOnClickListener(this);
        imgRegiFemale.setOnClickListener(this);
        dialog_decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genderDialog.dismiss();
            }
        });

        ///genderDialog.findViewById(R.id.dialog_decline_button).setOnClickListener(this);
        //genderDialog.findViewById(R.id.btn_select_gender).setOnClickListener(this);

        btn_select_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!maleFemale.equalsIgnoreCase("")) {
                    fbUserInfo.userGender = maleFemale;
                    showProgDialog(false);
                    // doRegistration(userInfo.fullname, userInfo.lastName, userInfo.userEmail, userInfo.password, userInfo.userGender, userInfo.userFacebookId,profileImageBitmap);
                } else {
                    Toast.makeText(context, "Please select gender", Toast.LENGTH_SHORT).show();
                }

            }
        });

        genderDialog.getWindow().setGravity(Gravity.CENTER);
        genderDialog.show();
    }

    public void getBitmapFromURL(String image, final UserInfo userInfo) {
        if (image != null && !image.equals("")) {
            Picasso.with(RegistrationActivity.this).load(image).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    dismissProgDialog();
                    try {
                        if (bitmap != null) {
                            profileImageBitmap = bitmap;
                            //userInfo.profileImageBitmap = bitmap;
                        }

                        checkSocialDetail(userInfo, loginstatus, userInfo.userFacebookId, userInfo.userEmail);
                        getAddressFromLatLong(latitude, longitude);
                    } catch (Exception e) {
                        // some action
//                        checkSocialDetail(userInfo, loginstatus, userInfo.userFacebookId,userInfo.userEmail);
//                        getAddressFromLatLong(latitude, longitude);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    checkSocialDetail(userInfo, loginstatus, userInfo.userFacebookId, userInfo.userEmail);
                    getAddressFromLatLong(latitude, longitude);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                    checkSocialDetail(userInfo, loginstatus, userInfo.userFacebookId,userInfo.userEmail);
//                    getAddressFromLatLong(latitude, longitude);
                    // Toast.makeText(context, "onPrepareLoad", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_EXTERNAL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callIntent(Constant.RESULT_LOAD);
                } else {
                    Toast.makeText(this, "You denied permission , can't select image", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callIntent(Constant.INTENT_CAMERA);
                } else {
                    Toast.makeText(this, "permission denied by user ", Toast.LENGTH_LONG).show();
                }
                break;

            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                        //   getDeviceLocation();
                        initLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    Utility.e(TAG, getString(R.string.locationPermissionDeny));
                    // Toast.makeText(context, "deny Location Permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    // New Code
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constant.REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        ImageSessionManager.getInstance().createImageSession(mCurrentPhotoPath, false);
        return image;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //-----------------new code to upload image 16-05-19---------
    public void uploadNewBase64(final String userId, Bitmap bitmap) {
        if (utility.checkInternetConnection()) {
            //showProgDialog(false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            //sending image to server
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.IMAGEUPLOAD_BUCKET, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.has("success")) {
                            if (jo.getInt("success") == 1) {
                                String userImage = jo.getString("userImage");
                                setImage(userImage);
                            } else {
                                dismissProgDialog();
                                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                            }
                        }
                        // setImage();
                    } catch (Exception e) {
                        dismissProgDialog();
//                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    dismissProgDialog();
                    Toast.makeText(RegistrationActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                }
            }) {
                //adding parameters to send
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("profileImage", imageString);
                    parameters.put("user_id", userId);
                    return parameters;
                }
            };

            RequestQueue rQueue = Volley.newRequestQueue(RegistrationActivity.this);
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
            rQueue.add(request);
        }

    }

    //---------set default image----------------
    public void setImage(String s) {
      /*  if (!s.contains(Constant.DEV_TAG)) {
            s = Constant.DEV_TAG + s;
        }*/ //dev


        if (SceneKey.sessionManager.getUserInfo().getUserImage().equalsIgnoreCase(WebServices.USER_IMAGE + s)) {
//            Intent intent = new Intent(RegistrationActivity.this, IntroActivity.class);
            activeRewardApiData(SceneKey.sessionManager.getUserInfo().userid);
            // showDefaultDialog(getString(R.string.default_profile_title), getString(R.string.default_profile_msg));
        } else {
            activeRewardApiData(SceneKey.sessionManager.getUserInfo().userid);
            setDefaultImageOnServer(WebServices.USER_IMAGE + s, s);
            //setDefoultProfileImage(s,"Default Profile Image","Are you sure you want to make this your defoult Profile Photo?");
        }
    }

    public void showDefaultDialog(String title, String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_title_btn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //      deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id

        TextView tvPopupOk, tvTitle, tvMessages;

        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvMessages = dialog.findViewById(R.id.tvMessages);

        tvPopupOk = dialog.findViewById(R.id.tvPopupOk);
        tvPopupOk.setText(R.string.ok);

        tvTitle.setText(title);
        tvMessages.setText(msg);

        tvPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show location settings when the user acknowledges the alert dialog
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void setDefaultImageOnServer(final String key, String s) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("method", "PUT");
            jsonBody.put("action", "updateImage");
            jsonBody.put("userid", SceneKey.sessionManager.getUserInfo().userFacebookId);
            jsonBody.put("userImage", s);

            final String mRequestBody = jsonBody.toString();
            Utility.e("RequestBody", mRequestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, WebServices.DEFAULT_IMAGE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utility.e("server image set", response);
                    //img_profile_pic2.setVisibility(View.VISIBLE);
                    UserInfo userInfo = SceneKey.sessionManager.getUserInfo();
                    userInfo.userImage = key;
                    // Constant.DEF_PROFILE=key;
                    HomeActivity.userInfo = userInfo;
                    SceneKey.sessionManager.createSession(userInfo);

//                    Intent intent = new Intent(RegistrationActivity.this, IntroActivity.class);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utility.e("LOG_VOLLEY E", error.toString());
                    dismissProgDialog();
//                    Intent intent = new Intent(RegistrationActivity.this, IntroActivity.class);
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
            dismissProgDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void activeRewardApiData(String userid) {
        Call<ResponseBody> call = RetrofitClient.getInstance()
                .getAnotherApi().getActiveReward(userid);

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                try {
                    dismissProgDialog();
                    switch (response.code()) {
                        case 200: {
//                            {"status":"success","message":"Reward Sent successfully."}
                            String stresult = Objects.requireNonNull(response.body()).string();
                            Log.d("response", stresult);
                            JSONObject jsonObject = new JSONObject(stresult);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            if (status.equals("success")){
                                if (message.equals("Reward Sent successfully."))
                                {
                                    setBadgeCountToFireBase();
                                }
                                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                            }


                            break;
                        }
                        case 400: {
                            String result = Objects.requireNonNull(response.errorBody()).string();
                            Log.d("response400", result);
                            JSONObject jsonObject = new JSONObject(result);
                            String statusCode = jsonObject.optString("status");
                            String msg = jsonObject.optString("message");
                            if (statusCode.equals("true")) {
                                Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case 401:
                            try {
                                Log.d("ResponseInvalid", Objects.requireNonNull(response.errorBody()).string());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });

    }
    public void setBadgeCountToFireBase() {
        mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).child("count").setValue("1");
    }
}