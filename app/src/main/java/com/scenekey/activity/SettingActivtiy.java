package com.scenekey.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.CustomeClick;
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
import java.util.Objects;

public class SettingActivtiy extends AppCompatActivity implements View.OnClickListener, LocationListener {

    public static UserInfo userInfo;
    private final String TAG = SettingActivtiy.class.toString();
    public LinearLayout linLayBio, linLayChangePassword;
    public PlaceAutocompleteFragment autocompleteFragment;
    public boolean isApiM;
    private Utility utility;
    private TextView txt_location;
    private TextView txt_first_name;
    private TextView txt_last_name;
    private TextView txt_logout;
    private LatLng latLng;
    private RelativeLayout lnr_location;
    private CustomProgressBar customProgressBar;
    private double latitude = 0.0, longitude = 0.0;
    private LocationManager locationManager;
    View view_bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        inItView();
    }

    private void inItView() {

        utility = new Utility(this);
        customProgressBar = new CustomProgressBar(this);

        UserInfo updateUserInfo = SceneKey.sessionManager.getUserInfo();

        LinearLayout linLayTnC = findViewById(R.id.linLayTnC);
        LinearLayout linLayPriPolicy = findViewById(R.id.linLayPriPolicy);
        LinearLayout mainLayout = findViewById(R.id.mainlayout);
        LinearLayout lnr_deatils = findViewById(R.id.lnr_deatils);
        LinearLayout lnr_lastName = findViewById(R.id.lnr_lastName);

        lnr_location = findViewById(R.id.lnr_location);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        linLayBio = findViewById(R.id.linLayBio);
        txt_location = findViewById(R.id.txt_location);
        txt_logout = findViewById(R.id.txt_logout);

        view_bio = findViewById(R.id.view_bio);
        TextView txt_email = findViewById(R.id.txt_email);
        TextView txt_feedback = findViewById(R.id.txt_feedback);
        TextView txt_admin = findViewById(R.id.txt_admin);
        ImageView img_f1_back = findViewById(R.id.img_f1_back);
        ImageView img_default_location = findViewById(R.id.img_default_location);

        linLayChangePassword = findViewById(R.id.linLayChangePassword);


        setClick(txt_email, lnr_lastName, lnr_deatils, txt_last_name, linLayBio, linLayTnC, linLayPriPolicy, mainLayout, txt_logout, txt_feedback, img_f1_back, img_default_location);
        linLayChangePassword.setOnClickListener(this);

        if (!updateUserInfo.socialType.equals("facebook") && !updateUserInfo.socialType.equals("gmail")) {
            linLayChangePassword.setVisibility(View.VISIBLE);
            view_bio.setVisibility(View.VISIBLE);
        } else {
            linLayChangePassword.setVisibility(View.GONE);
            view_bio.setVisibility(View.GONE);
        }

        try {
            if (updateUserInfo.makeAdmin.contains(Constant.ADMIN_NO)) {
                txt_admin.setVisibility(View.GONE);
                lnr_location.setVisibility(View.GONE);
            } else {
                txt_admin.setVisibility(View.VISIBLE);
                lnr_location.setVisibility(View.VISIBLE);
                if (updateUserInfo.address == null || updateUserInfo.address.length() < 2) {
                    txt_location.setText(getAddress(Double.parseDouble(updateUserInfo.lat), Double.parseDouble(updateUserInfo.longi)));
                } else if (updateUserInfo.currentLocation) {
                    txt_location.setText(getAddress(Double.parseDouble(updateUserInfo.lat), Double.parseDouble(updateUserInfo.longi)));
                } else if (updateUserInfo.address.length() > 60) {
                    txt_location.setText(updateUserInfo.address.substring(0, 60) + "...");
                } else {
                    txt_location.setText(updateUserInfo.address);
                }
            }

            txt_first_name.setText(updateUserInfo.fullname);
            if (!updateUserInfo.lastName.equals("0"))
                txt_last_name.setText(updateUserInfo.lastName);
            else
                txt_last_name.setText(R.string.na);

            if (!updateUserInfo.userEmail.contains(updateUserInfo.userFacebookId))
                txt_email.setText(updateUserInfo.userEmail);
            else if (updateUserInfo.userFacebookId.isEmpty()) {
                txt_email.setText(updateUserInfo.userEmail);
            } else txt_email.setText(getString(R.string.noemail));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }


    /*.........................initLocation....................................*/
    private void initLocation() {
        try {
            // get GPS status
            boolean checkGPS = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // get network provider status
            boolean checkNetwork = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (!checkGPS && !checkNetwork) {
                Utility.e(TAG, "GPS & Provider not available");
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

    /*.........................userInfo Session update....................................*/
    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(SettingActivtiy.this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }

    /*.........................setClick()....................................*/
    private void setClick(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    /*.........................getAddress()....................................*/
    private String getAddress(double latitude, double longitude) {
        String result = null;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            result = address + "," + city + "," + state + "," + country;
            txt_location.setText(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*.........................onClick()....................................*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_logout:
                SceneKey.sessionManager.logout(this);
                break;

            case R.id.txt_last_name:

                Intent seetingIntet = new Intent(this, Edit_NAmeActivity.class);
                seetingIntet.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(seetingIntet);
                break;

            case R.id.lnr_deatils:

                Intent intent = new Intent(this, Edit_NAmeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.linLayChangePassword:

                Intent changePassword = new Intent(this, ChangePasswordActivity.class);
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
                    Utility.showToast(this, getString(R.string.enoactivity), 0);
                } catch (Exception e) {
                    Utility.showToast(this, getString(R.string.somethingwentwrong), 0);
                }
                break;

            case R.id.linLayBio:
                Intent bioIntent = new Intent(this, BioActivity.class);
                bioIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bioIntent.putExtra("from", "setting");
                startActivity(bioIntent);
                break;

            case R.id.img_default_location:
                UserInfo userInfo = userInfo();
                userInfo.lat = (latitude + "");
                userInfo.longi = (longitude + "");
                userInfo.adminLat = userInfo.lat;
                userInfo.adminLong = userInfo.longi;
                userInfo.address = (getAddress(latitude, longitude));
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
                    Utility.showToast(this, getString(R.string.enoactivity), 0);
                } catch (Exception e) {
                    Utility.showToast(this, getString(R.string.somethingwentwrong), 0);
                }
                break;
        }

    }

    /*.........................updateLocation()....................................*/
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
                            Utility.showToast(SettingActivtiy.this, getString(R.string.somethingwentwrong), 0);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utility.showToast(SettingActivtiy.this, getString(R.string.somethingwentwrong), 0);
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
            VolleySingleton.getInstance(this).addToRequestQueue(request);
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
                if (user != null) {
                    txt_first_name.setText(user.fullname == null ? "" : user.fullname);
                    if (!user.lastName.isEmpty())
                        txt_last_name.setText(user.lastName);
                    else
                        txt_last_name.setText(R.string.na);
                } else
                    txt_last_name.setText(R.string.na);
            }
        });

        Log.e("Test", " Setting-OnResume");
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
    }

    @Override
    public void onStart() {
        Log.e("Test", " Setting-OnStart");
        super.onStart();
        initLocation();
    }

    @Override
    public void onStop() {
        Log.e("Test", " Setting-OnStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.e("Test", " Setting-OnPause");
        super.onPause();
    }

    /*............................showProgDialog..............................*/
    public void showProgDialog(boolean b) {
        try {
            customProgressBar.setCanceledOnTouchOutside(b);
            customProgressBar.setCancelable(b);
            customProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*............................dismissProgDialog..............................*/
    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*............................showStatusBar..............................*/
    public void showStatusBar() {
        getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            isApiM = true;
        } else {
            StatusBarUtil.setStatusBarColor(this, R.color.new_white_bg);
        }
    }

    /*............................hideKeyBoard..............................*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void hideKeyBoard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*............................addFragment..............................*/
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                hideKeyBoard();
            }
            return fragmentHolder;
        } catch (Exception e) {
            return null;
        }
    }

    /*............................Location..............................*/
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // Logic to handle location object
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
