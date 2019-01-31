package com.scenekey.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.LoginActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomeClick;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.WebServices;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Setting_Fragment extends Fragment implements View.OnClickListener {

    private final String TAG = Setting_Fragment.class.toString();
    public LinearLayout linLayBio, linLayChangePassword;
    public PlaceAutocompleteFragment autocompleteFragment;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private Profile_Fragment fragment;
    private ImageView img_default_location;
    private TextView txt_location, txt_first_name, txt_last_name, txt_email, txt_feedback, txt_logout, txt_admin;
    private LatLng latLng;
    private RelativeLayout lnr_location;
    private LinearLayout lnr_deatils, lnr_lastName;
    private SessionManager sessionManager;
    private LoginActivity loginActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        activity.setBBVisibility(View.GONE, TAG);

        Log.e("Test"," Setting-OnCreateView");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        loginActivity = new LoginActivity();
        sessionManager = new SessionManager(context);

        LinearLayout linLayTnC = view.findViewById(R.id.linLayTnC);
        LinearLayout linLayPriPolicy = view.findViewById(R.id.linLayPriPolicy);
        ImageView img_f1_back = view.findViewById(R.id.img_f1_back);
        LinearLayout mainLayout = view.findViewById(R.id.mainlayout);
        lnr_location = view.findViewById(R.id.lnr_location);
        txt_first_name = view.findViewById(R.id.txt_first_name);
        txt_last_name = view.findViewById(R.id.txt_last_name);
        txt_email = view.findViewById(R.id.txt_email);
        linLayBio = view.findViewById(R.id.linLayBio);
        txt_location = view.findViewById(R.id.txt_location);
        txt_logout = view.findViewById(R.id.txt_logout);
        txt_feedback = view.findViewById(R.id.txt_feedback);
        img_default_location = view.findViewById(R.id.img_default_location);
        txt_admin = view.findViewById(R.id.txt_admin);

        //Shubham
        linLayChangePassword = view.findViewById(R.id.linLayChangePassword);
        lnr_deatils = view.findViewById(R.id.lnr_deatils);
        lnr_lastName = view.findViewById(R.id.lnr_lastName);
        linLayChangePassword.setOnClickListener(this);


        String facebookId = activity.userInfo().userFacebookId;
        final String userID = activity.userInfo().userid;

        try {
            if (activity.userInfo().makeAdmin.contains(Constant.ADMIN_NO)) {
                txt_admin.setVisibility(View.GONE);
                lnr_location.setVisibility(View.GONE);
            } else {
                txt_admin.setVisibility(View.VISIBLE);
                lnr_location.setVisibility(View.VISIBLE);
                if (activity.userInfo().address == null || activity.userInfo().address.length() < 2) {
                    txt_location.setText(getAddress(Double.parseDouble(activity.userInfo().lat), Double.parseDouble(activity.userInfo().longi)));
                } else if (activity.userInfo().currentLocation) {
                    txt_location.setText(getAddress(Double.parseDouble(activity.userInfo().lat), Double.parseDouble(activity.userInfo().longi)));
                } else if (activity.userInfo().address.length() > 60) {
                    txt_location.setText(activity.userInfo().address.substring(0, 60) + "...");
                } else {
                    //txt_location.setText(getAddress(Double.parseDouble(activity.userInfo.latitude), Double.parseDouble(activity.userInfo.longitude)));
                    // New Code
                    txt_location.setText(activity.userInfo().address);
                }
            }

            txt_first_name.setText(activity.userInfo().fullname);
            if (!activity.userInfo().lastName.isEmpty())
                txt_last_name.setText(activity.userInfo().lastName);
            else
                txt_last_name.setText(R.string.na);

            if (!activity.userInfo().userEmail.contains(activity.userInfo().userFacebookId))
                txt_email.setText(activity.userInfo().userEmail);
            else if (activity.userInfo().userFacebookId.isEmpty()) {
                txt_email.setText(activity.userInfo().userEmail);
            } else txt_email.setText(getString(R.string.noemail));

        } catch (Exception e) {
            e.printStackTrace();
        }

        setClick(txt_email, lnr_lastName, lnr_deatils, txt_last_name, linLayBio, linLayTnC, linLayPriPolicy, mainLayout, txt_logout, txt_feedback, img_f1_back, img_default_location);

        try {
            autocompleteFragment = (PlaceAutocompleteFragment) activity.getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    //address = place.getAddress().toString();
                    txt_location.setText(place.getAddress().toString());
                    latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    if (activity.userInfo().makeAdmin.contains(Constant.ADMIN_YES)) {
                        UserInfo userInfo = activity.userInfo();
                        userInfo.lat = (latLng.latitude + "");
                        userInfo.longi = (latLng.longitude + "");
                        userInfo.adminLat = userInfo.lat;
                        userInfo.adminLong = userInfo.longi;
                        userInfo.address = (place.getAddress().toString());
                        userInfo.currentLocation = (false);
                        activity.updateSession(userInfo);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
        utility = new Utility(context);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txt_logout:

              /*  if(sessionManager.getLoginType().equals("gmail")){
                    loginActivity.signOut();
                }else if(sessionManager.getLoginType().equals("facebook")){

                }*/
                SceneKey.sessionManager.logout(activity);
                break;

            case R.id.txt_last_name:
                try {
                    activity.addFragment(new Edit_NameFragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.lnr_deatils:
                try {
                    activity.addFragment(new Edit_NameFragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.linLayChangePassword:
                try {
                    activity.addFragment(new ChangePassword_Fragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                activity.onBackPressed();
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
                try {
                    activity.addFragment(new Bio_Fragment(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_default_location:
                UserInfo userInfo = activity.userInfo();
                userInfo.lat = (activity.getLatitude() + "");
                userInfo.longi = (activity.getLongitude() + "");
                userInfo.adminLat = userInfo.lat;
                userInfo.adminLong = userInfo.longi;
                userInfo.address = (getAddress(activity.getLatitude(), activity.getLongitude()));
                userInfo.currentLocation = (true);
                activity.updateSession(userInfo);
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

    @Override
    public void onDestroyView() {
        try {
            if (fragment != null) {
                //check
                //fragment.reloadeData(activity.userInfo());
            }
            if (activity != null)
                activity.getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Log.e("Test"," Setting-OnDestroyView");

        super.onDestroyView();
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
        geocoder = new Geocoder(activity, Locale.getDefault());

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

    public Setting_Fragment setData(Profile_Fragment fragment) {
        this.fragment = fragment;
        return this;
    }

    private void updateLocation(final UserInfo userInfo) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_BY_LOCAL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utility.printBigLogcat(TAG, response);
                    activity.dismissProgDialog();

                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.has("userInfo")) {
                            UserInfo userInfo = activity.userInfo();
                            Object intervention = jo.get("userInfo");
                            if (intervention instanceof JSONArray) {
                                SceneKey.sessionManager.logout(activity);
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
                            activity.updateSession(userInfo);

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
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("lat", userInfo.lat + "");
                    params.put("long", userInfo.longi + "");
                    params.put("user_id", activity.userInfo().userid);
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
            activity.dismissProgDialog();
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
}