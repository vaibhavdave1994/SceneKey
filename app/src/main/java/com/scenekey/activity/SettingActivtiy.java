package com.scenekey.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.scenekey.R;
import com.scenekey.fragment.Bio_Fragment;
import com.scenekey.fragment.ChangePassword_Fragment;
import com.scenekey.fragment.Edit_NameFragment;
import com.scenekey.fragment.Profile_Fragment;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.CustomeClick;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.StatusBarHide;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.StatusBarUtil;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SettingActivtiy extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = SettingActivtiy.class.toString();
    public LinearLayout linLayBio, linLayChangePassword;
    public PlaceAutocompleteFragment autocompleteFragment;
    private Context context;
    private Utility utility;
    private Profile_Fragment fragment;
    private ImageView img_default_location;
    private TextView txt_location, txt_first_name, txt_last_name, txt_email, txt_feedback, txt_logout, txt_admin;
    private LatLng latLng;
    private RelativeLayout lnr_location;
    private LinearLayout lnr_deatils, lnr_lastName;
    private LoginActivity loginActivity;
    private CustomProgressBar customProgressBar;
    public static UserInfo userInfo;
    public boolean isApiM;
    private HomeActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        inItView();
    }

    private void inItView() {

        loginActivity = new LoginActivity();
        utility = new Utility(this);
        activity = new HomeActivity();
        customProgressBar = new CustomProgressBar(this);

        LinearLayout linLayTnC = findViewById(R.id.linLayTnC);
        LinearLayout linLayPriPolicy = findViewById(R.id.linLayPriPolicy);
        ImageView img_f1_back = findViewById(R.id.img_f1_back);
        LinearLayout mainLayout = findViewById(R.id.mainlayout);
        lnr_location = findViewById(R.id.lnr_location);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_email = findViewById(R.id.txt_email);
        linLayBio = findViewById(R.id.linLayBio);
        txt_location = findViewById(R.id.txt_location);
        txt_logout = findViewById(R.id.txt_logout);
        txt_feedback = findViewById(R.id.txt_feedback);
        img_default_location = findViewById(R.id.img_default_location);
        txt_admin = findViewById(R.id.txt_admin);

        //Shubham
        linLayChangePassword = findViewById(R.id.linLayChangePassword);
        lnr_deatils = findViewById(R.id.lnr_deatils);
        lnr_lastName = findViewById(R.id.lnr_lastName);
        linLayChangePassword.setOnClickListener(this);


        String facebookId = userInfo().userFacebookId;
        final String userID = userInfo().userid;

        try {
            if (userInfo().makeAdmin.contains(Constant.ADMIN_NO)) {
                txt_admin.setVisibility(View.GONE);
                lnr_location.setVisibility(View.GONE);
            } else {
                txt_admin.setVisibility(View.VISIBLE);
                lnr_location.setVisibility(View.VISIBLE);
                if (userInfo().address == null || userInfo().address.length() < 2) {
                    txt_location.setText(getAddress(Double.parseDouble(userInfo().lat), Double.parseDouble(userInfo().longi)));
                } else if (userInfo().currentLocation) {
                    txt_location.setText(getAddress(Double.parseDouble(userInfo().lat), Double.parseDouble(userInfo().longi)));
                } else if (userInfo().address.length() > 60) {
                    txt_location.setText(userInfo().address.substring(0, 60) + "...");
                } else {
                    //txt_location.setText(getAddress(Double.parseDouble(activity.userInfo.latitude), Double.parseDouble(activity.userInfo.longitude)));
                    // New Code
                    txt_location.setText(userInfo().address);
                }
            }

            txt_first_name.setText(userInfo().fullname);
            if (!userInfo().lastName.isEmpty())
                txt_last_name.setText(userInfo().lastName);
            else
                txt_last_name.setText(R.string.na);

            if (!userInfo().userEmail.contains(userInfo().userFacebookId))
                txt_email.setText(userInfo().userEmail);
            else if (userInfo().userFacebookId.isEmpty()) {
                txt_email.setText(userInfo().userEmail);
            } else txt_email.setText(getString(R.string.noemail));

        } catch (Exception e) {
            e.printStackTrace();
        }

        setClick(txt_email, lnr_lastName, lnr_deatils, txt_last_name, linLayBio, linLayTnC, linLayPriPolicy, mainLayout, txt_logout, txt_feedback, img_f1_back, img_default_location);

        try {
            autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    //address = place.getAddress().toString();
                    txt_location.setText(place.getAddress().toString());
                    latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    if (userInfo().makeAdmin.contains(Constant.ADMIN_YES)) {
                        UserInfo userInfo = userInfo();
                        userInfo.lat = (latLng.latitude + "");
                        userInfo.longi = (latLng.longitude + "");
                        userInfo.adminLat = userInfo.lat;
                        userInfo.adminLong = userInfo.longi;
                        userInfo.address = (place.getAddress().toString());
                        userInfo.currentLocation = (false);
                        updateSession(userInfo);
                        updateLocation(userInfo);
                    }
                }

                @Override
                public void onError(Status status) {
                    Log.d("ERROR:::", status.getStatusMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(SettingActivtiy.this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }


    private void setClick(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    private String getAddress(double latitude, double longitude) {
        String result = null;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String addressLine = addresses.get(0).getAddressLine(1);
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            //result = knownName + " ," + addressLine + " , " + city + "," + state + "," + country + " counter" + counter;// Here 1 represent max location result to returned, by documents it recommended 1 to 5
            result = address + "," + city + "," + state + "," + country;// Here 1 represent max location result to returned, by documents it recommended 1 to 5
            txt_location.setText(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_logout:

              /*  if(sessionManager.getLoginType().equals("gmail")){
                    loginActivity.signOut();
                }else if(sessionManager.getLoginType().equals("facebook")){

                }*/
                SceneKey.sessionManager.logout(this);
                break;

            case R.id.txt_last_name:
               /* try {
                    addFragment(new Edit_NameFragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/


                Intent seetingIntet = new Intent(this,Edit_NAmeActivity.class);
                seetingIntet.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(seetingIntet);
                break;

            case R.id.lnr_deatils:
               /* try {
                    addFragment(new Edit_NameFragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                Intent intent = new Intent(this,Edit_NAmeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.linLayChangePassword:
              /*  try {
                    addFragment(new ChangePassword_Fragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/


                Intent changePassword = new Intent(this,ChangePasswordActivity.class);
                changePassword.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(changePassword);
                break;

            case R.id.txt_feedback:
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto: info@scenekey.com"));
                    startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                    //startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_f1_back:
                onBackPressed();
                break;

            case R.id.linLayTnC:
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(WebServices.TERMS_));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Utility.showToast(context, getString(R.string.enoactivity), 0);
                } catch (Exception e) {
                    Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                }
                break;
            case R.id.linLayBio:
              /*  try {
                    addFragment(new Bio_Fragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                Intent bioIntent = new Intent(this,BioActivity.class);
                bioIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(bioIntent);
                break;
            case R.id.img_default_location:
                UserInfo userInfo = userInfo();
                userInfo.lat = (activity.getLatitude() + "");
                userInfo.longi = (activity.getLongitude() + "");
                userInfo.adminLat = userInfo.lat;
                userInfo.adminLong = userInfo.longi;
                userInfo.address = (getAddress(activity.getLatitude(), activity.getLongitude()));
                userInfo.currentLocation = (true);
                updateSession(userInfo);
                updateLocation(userInfo);
                break;
            case R.id.linLayPriPolicy:
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(WebServices.PRIVACY_));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Utility.showToast(context, getString(R.string.enoactivity), 0);
                } catch (Exception e) {
                    Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                }
                break;
        }

    }

    private void updateLocation(final UserInfo userInfo) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_BY_LOCAL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utility.printBigLogcat(TAG, response);
                    dismissProgDialog();

                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.has("userInfo")) {
                            UserInfo userInfo = userInfo();
                            Object intervention = jo.get("userInfo");
                            if (intervention instanceof JSONArray) {
                                SceneKey.sessionManager.logout(SettingActivtiy.this);
                            }
                            JSONObject user = jo.getJSONObject("userInfo");
                            if (user.has("makeAdmin")) {
                                userInfo.makeAdmin = (user.getString("makeAdmin"));

                            }
                            if (user.has("lat"))
                                userInfo.lat = (user.getString("lat"));

                            if (user.has("longi"))
                                userInfo.longi = (user.getString("longi"));

                            /*if (user.has("adminLat"))
                                userInfo.adminLat = (user.getString("adminLat"));

                            if (user.has("adminLong"))
                                userInfo.adminLong = (user.getString("adminLong"));*/

                            if (user.has("adminLat")) {
                                if (user.getString("adminLat").isEmpty()) {
                                    userInfo.adminLat = user.getString("lat");
                                    userInfo.adminLong = user.getString("longi");
                                    userInfo.currentLocation = true;
                                } else {
                                    userInfo.adminLat = user.getString("adminLat");
                                    userInfo.adminLong = user.getString("adminLong");
                                    userInfo.currentLocation = false;
                                }
                            }

                            if (user.has("address"))
                                userInfo.address = (user.getString("address"));

                            if (user.has("key_points"))
                                userInfo.key_points = (user.getString("key_points"));

                            if (userInfo.makeAdmin.equals(Constant.ADMIN_NO)) {
                                lnr_location.setVisibility(View.GONE);
                            }
                            updateSession(userInfo);

                        } else {
                            Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
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
                    params.put("lat", userInfo.lat + "");
                    params.put("long", userInfo.longi + "");
                    params.put("user_id", userInfo().userid);
                    params.put("updateLocation", Constant.ADMIN_YES);
                    params.put("fullAddress", txt_location.getText().toString());

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(txt_logout, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        CustomeClick.getmInctance().setListner(new CustomeClick.ExploreSearchListener() {
            @Override
            public void onTextChange(UserInfo user) {
                //userInfo = user;

                if(user!= null){
                    txt_first_name.setText(user.fullname == null? "":user.fullname);
                    if (!user.lastName.isEmpty())
                        txt_last_name.setText(user.lastName);
                    else
                        txt_last_name.setText(R.string.na);
                }  else
                    txt_last_name.setText(R.string.na);
            }
        });

        Log.e("Test"," Setting-OnResume");
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


    @Override
    public void onStart() {
        Log.e("Test"," Setting-OnStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.e("Test"," Setting-OnStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.e("Test"," Setting-OnPause");
        super.onPause();
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

    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showStatusBar() {
        getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // top_status.setBackgroundResource(R.color.white);
            isApiM = true;
        } else {
            StatusBarUtil.setStatusBarColor(this, R.color.new_white_bg);
        }
    }


    public void hideKeyBoard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Fragment addFragment(Fragment fragmentHolder, int animationValue) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentName = fragmentHolder.getClass().getName();
            if (!(fragmentHolder instanceof StatusBarHide)) showStatusBar();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (animationValue == 0) {

                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_out_down, R.anim.slide_in_down);
            }
            if (animationValue == 1)
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setEnterTransition(null);
            }
            fragmentTransaction.add(R.id.frame_fragments, fragmentHolder, fragmentName).addToBackStack(fragmentName);
            fragmentTransaction.commit();

            hideKeyBoard();
            return fragmentHolder;
        } catch (Exception e) {
            return null;
        }
    }

}
