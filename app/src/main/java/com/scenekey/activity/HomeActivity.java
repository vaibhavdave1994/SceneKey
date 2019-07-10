package com.scenekey.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.scenekey.R;
import com.scenekey.aws_service.Aws_Web_Service;
import com.scenekey.fragment.Event_Fragment;
import com.scenekey.fragment.Home_No_Event_Fragment;
import com.scenekey.fragment.Map_Fragment;
import com.scenekey.fragment.NearEvent_Fragment;
import com.scenekey.fragment.NewSearchkFragment;
import com.scenekey.fragment.OfferSFragment;
import com.scenekey.fragment.ProfileNew_fragment;
import com.scenekey.fragment.Profile_Fragment;
import com.scenekey.fragment.Search_Fragment;
import com.scenekey.fragment.Trending_Fragment;
import com.scenekey.fragment.WalletsFragment;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.Permission;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.BackPressListner;
import com.scenekey.listener.StatusBarHide;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.Events;
import com.scenekey.model.NotificationModal;
import com.scenekey.model.Offers;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.StatusBarUtil;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    public static int ActivityWidth;
    public static int ActivityHeight;
    public static UserInfo userInfo;
    private final String TAG = "HomeActivity";
    public Context context = this;
    public RelativeLayout rtlv_profile;
    public FrameLayout frm_bottmbar;
    public boolean isApiM, isKitKat, statusKey;
    int lastClick = 0;
    boolean isFirstTimeTrending = false;
    //Shubham
    TabLayout tabLayout, tablayout_home, tablayout_alert;
    private FrameLayout frame_fragments;
    private TextView tvHomeTitle, tv_key_points;
    private RelativeLayout rl_title_view;
    private RelativeLayout rtlv_alert, rtlv_home, rtlv_reward, lastclicked;
    private ImageView img_home_logo, img_three_one;
    private View view, bottom_margin_view, top_status;
    private boolean doubleBackPress;
    private ArrayList<Events> eventsArrayList, eventsNearbyList;
    private boolean isPermissionAvail;
    private Map_Fragment map_fragment;
    private double latitude = 0.0, longitude = 0.0;
    private double latitudeAdmin = 0.0, longitudeAdmin = 0.0;
    private boolean checkGPS;
    private CustomProgressBar customProgressBar;
    private LocationManager locationManager;
    private Utility utility;
    private int position = 0;
    private String notificationType1 = "";
    public String frequency = "";
    public String venue_id = "";
    private String eventId = "";
    private String currentScreen = "";
    private String isBroadCast;
    private boolean isBgNoti = false;
    //private TextView txt_five;
    private TextView tv_title;
    private boolean myProfile;
    public static boolean fromSearch = false;
    public static String name = "";
    private DatabaseReference mDatabase;
     public TextView tv_alert_badge_count;
/*
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;*/

/*
    https://stackoverflow.com/questions/47351172/i-want-to-show-tab-bar-icon-left-side-of-text?rq=1
*/

    private int[] tabIcons = {R.drawable.ic_flame, R.drawable.ic_location, R.drawable.ic_search};

    private int[] navLabels = {
            R.string.trending,
            R.string.map,
            R.string.search
    };

    private int[] tabIconsActive = {
            R.drawable.ic_flame_active, R.drawable.ic_location_active, R.drawable.ic_search_active
    };

    private RelativeLayout rl_title_view_home, rl_title_main_view, rl_toolbar_alert, rl_profileView;

    /* when notification receive fragment end here */
    private String notificationMessage = "";
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            handleNotification(intent);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //StatusBarUtil.setTranslucent(this);
        overridePendingTransition(R.anim.slide_left, R.anim.fab_slide_out_to_left);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getDataFromFB();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed: " + refreshedToken);
        //automatically hide status bar
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            Utility.e(TAG, "Status bar visible");

                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                            Utility.e(TAG, "Status bar Invisible");
                        }
                    }
                });
        fromSearch = getIntent().getBooleanExtra("fromSearch",false);
        name = getIntent().getStringExtra("name");
        if(name == null){
            name = "";
        }
        //Reward Screen Toolbar...............................................
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Offers"));
        tabLayout.addTab(tabLayout.newTab().setText("Wallet"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        replaceFragment(new OfferSFragment());
                        break;

                    case 1:
                        replaceFragment(new WalletsFragment());
                        break;
                    default:
                        replaceFragment(new OfferSFragment());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        .....................................................................


        //Alert Screen Toolbar...............................................

        tablayout_alert = findViewById(R.id.tablayout_alert);
        tablayout_alert.addTab(tablayout_alert.newTab().setText("Alert"));
        tablayout_alert.addTab(tablayout_alert.newTab().setText("Chat"));
        tablayout_alert.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:

                        break;

                    case 1:

                        break;
                    default:

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        tablayout_home
        tablayout_home = findViewById(R.id.tablayout_home);

        TabLayout.Tab fTab = tablayout_home.newTab();
//        fTab.setText("Trending");
//        fTab.setIcon(R.drawable.ic_flame);
        tablayout_home.addTab(fTab);

        TabLayout.Tab secTab = tablayout_home.newTab();
//        secTab.setText("Map");
//        secTab.setIcon(R.drawable.ic_location);
        tablayout_home.addTab(secTab);

        TabLayout.Tab thTab = tablayout_home.newTab();
//        thTab.setText("Search");
//        thTab.setIcon(R.drawable.ic_search);
        tablayout_home.addTab(thTab);

        //----------new code-----------------
        for (int i = 0; i <3; i++) {
            // inflate the Parent LinearLayout Container for the tab
            // from the layout nav_tab.xml file that we created 'R.layout.nav_tab
            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.new_home_tab_items, null);

            // get child TextView and ImageView from this layout for the icon and label
            TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tab.findViewById(R.id.nav_icon);

            // set the label text by getting the actual string value by its id
            // by getting the actual resource value `getResources().getString(string_id)`
            tab_label.setText(getResources().getString(navLabels[i]));

            // set the home to be active at first
            if(i == 0) {
                tab_label.setTextColor(getResources().getColor(R.color.colorPrimaryDark_new));
                tab_icon.setImageResource(tabIconsActive[i]);
            } else {
                tab_icon.setImageResource(tabIcons[i]);
            }

            // finally publish this custom view to navigation tab
            tablayout_home.getTabAt(i).setCustomView(tab);
        }

        //Objects.requireNonNull(Objects.requireNonNull(tablayout_home.getTabAt(0)).getIcon()).setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);

        tablayout_home.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        if (isPermissionAvail) {
                            assert tab.getIcon() != null;
                            //tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);
                            View tabView = tab.getCustomView();
                            // get inflated children Views the icon and the label by their id
                            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                            tab_label.setTextColor(getResources().getColor(R.color.colorPrimaryDark_new));
                            tab_icon.setImageResource(tabIconsActive[0]);
                            replaceFragment(new Trending_Fragment());
                            //setBottomBar((RelativeLayout) v, lastclicked);
                        } else {
                            utility.snackBar(rtlv_home, "Location permission not available", 0);
                        }

                        break;

                    case 1:
                        if (isPermissionAvail) {
                            assert tab.getIcon() != null;
                           // tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);
                            //setBottomBar((RelativeLayout) v, lastclicked);
                            View tabView = tab.getCustomView();
                            // get inflated children Views the icon and the label by their id
                            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                            tab_label.setTextColor(getResources().getColor(R.color.colorPrimaryDark_new));
                            tab_icon.setImageResource(tabIconsActive[1]);
                            if (map_fragment == null) map_fragment = new Map_Fragment();
                            replaceFragment(map_fragment);
                        } else {
                            utility.snackBar(rtlv_home, "Location permission not available", 0);
                        }

                        break;

                    case 2:
                        assert tab.getIcon() != null;
                       // fromSearch = false;
                      //  tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);
                        //replaceFragment(new Search_Fragment());
                        View tabView = tab.getCustomView();
                        // get inflated children Views the icon and the label by their id
                        TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                        ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                        tab_label.setTextColor(getResources().getColor(R.color.colorPrimaryDark_new));
                        tab_icon.setImageResource(tabIconsActive[2]);
                        replaceFragment(new NewSearchkFragment());
                        break;

                    default:
                        if (isPermissionAvail) {
                            assert tab.getIcon() != null;
                            tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);
                            replaceFragment(new Trending_Fragment());
                        } else {
                            utility.snackBar(rtlv_home, "Location permission not available", 0);
                        }
                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.getIcon().setColorFilter(Color.parseColor("#4B4B4B"), PorterDuff.Mode.SRC_IN);
                View tabView = tab.getCustomView();
                // get inflated children Views the icon and the label by their id
                TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                tab_label.setTextColor(getResources().getColor(R.color.deselect_tabtext));
                tab_icon.setImageResource(tabIcons[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

       /* toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();*/
    }

    private void handleNotification(Intent intent) {

          /* "notificationType":20 = (Like, comment, Update stats)
              notificationType":2 = Keyin
               notificationType":9 = Reward
                notificationType":22 = nudge
              notificationType":1 = make admin*/

        if (intent.getSerializableExtra("notificationModalEvent") != null) {
            NotificationModal notificationModal = (NotificationModal) intent.getSerializableExtra("notificationModalEvent");
            eventId = notificationModal.eventId;
            String fullAddress = notificationModal.fullAddress;
            String bag_admin = notificationModal.bag_admin;
            notificationType1 = notificationModal.notificationType1;
            notificationMessage = notificationModal.message;
            currentScreen = notificationModal.notificationCurrentScreen;
            isBroadCast = notificationModal.isBroadCast;

        } else if (intent.getSerializableExtra("notificationModalHome") != null) {
            NotificationModal notificationModal = (NotificationModal) intent.getSerializableExtra("notificationModalHome");
            eventId = notificationModal.eventId;
            String fullAddress = notificationModal.fullAddress;
            String bag_admin = notificationModal.bag_admin;
            notificationType1 = notificationModal.notificationType1;
            notificationMessage = notificationModal.message;
            currentScreen = notificationModal.notificationCurrentScreen;
            isBroadCast = notificationModal.isBroadCast;

        } else if (intent.getSerializableExtra("notificationModalReward") != null) {
            NotificationModal notificationModal = (NotificationModal) intent.getSerializableExtra("notificationModalReward");
            eventId = notificationModal.eventId;
            String fullAddress = notificationModal.fullAddress;
            String bag_admin = notificationModal.bag_admin;
            notificationType1 = notificationModal.notificationType1;
            frequency = notificationModal.frequency;
            venue_id = notificationModal.venue_id;
            notificationMessage = notificationModal.message;
            currentScreen = notificationModal.notificationCurrentScreen;
            isBroadCast = notificationModal.isBroadCast;

           /* txt_notification.setVisibility(View.VISIBLE);
            txt_notification.setText("1");*/
        }

        switch (notificationType1) {

//          "notificationType":20 = (Like, comment, Update stats)
            case "2":
            case "20":
                //prfect
                if (!eventId.isEmpty() && notificationType1.equals("2") && currentScreen.equals("HomeScreen")) {

                    if (isBroadCast.equals("isBroadCast")) {
                        showCustomNotification(notificationMessage);
                    } else {
                        isBgNoti = true;
                    }

                } else if (notificationType1.equals("2") || currentScreen.equals("EventScreen")) {
                    Fragment fragment = getCurrentFragment();
                    if (fragment instanceof Event_Fragment) {
                        Event_Fragment event_fragment = (Event_Fragment) fragment;
                        event_fragment.getAllData();
                    }
                }
                break;

//            notificationType":22 = nudge
            case "22":
                //prfect
                if (!eventId.isEmpty() && currentScreen.equals("HomeScreen")) {
                    if (isBroadCast.equals("isBroadCast")) {
                        showCustomNotification(notificationMessage);
                    } else {
                        isBgNoti = true;
                    }
                } else if (currentScreen.equals("EventScreen")) {
                    Fragment fragment = getCurrentFragment();
                    if (fragment instanceof Event_Fragment) {
                        Event_Fragment event_fragment = (Event_Fragment) fragment;
                        event_fragment.getAllData();
                    }
                }
                break;

//            notificationType":3 = Reward
            case "3":
//                if (currentScreen.equals("HomeScreen")) {
//                    if (isBroadCast.equals("isBroadCast")) {
//                        rewardNotification(notificationMessage);
//                    } else {
//                        isBgNoti = true;
//                    }
//                } else if (currentScreen.equals("RewardScreen")) {
//                    if (isBroadCast.equals("isBroadCast")) {
//                Fragment fragment = getCurrentFragment();
//                if (fragment instanceof OfferSFragment) {
//                    OfferSFragment reward_fragment = (OfferSFragment) fragment;
//                    reward_fragment.getOffersList();
//                }
//            }
                //}


                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {

                        HomeActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                lastClick = 0;
                                isPermissionAvail = true;
                                rtlv_alert.callOnClick();
                            }
                        });
                    }
                }, 3 * 1000);

                break;

            case "tag":

                Intent intentVenueBoard = new Intent(context, OnBoardActivity.class);
                intentVenueBoard.putExtra("frequency", frequency);
                intentVenueBoard.putExtra("venuid", venue_id);
                intentVenueBoard.putExtra("fromAlert", true);
                startActivity(intentVenueBoard);

                break;

                case "8":

                    new Handler().postDelayed(new Runnable() {
                        // Using handler with postDelayed called runnable run method
                        @Override
                        public void run() {

                            HomeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    lastClick = 0;
                                    isPermissionAvail = true;
                                    rtlv_alert.callOnClick();
                                }
                            });
                        }
                    }, 3 * 1000);




                    break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void forMakeAdmin(String s) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_push);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);

        TextView title_name, text_name;
        RelativeLayout notification_view;

        notification_view = dialog.findViewById(R.id.notification_view);
        title_name = dialog.findViewById(R.id.title_name);
        text_name = dialog.findViewById(R.id.text_name);
        title_name.setText("ScenceKey");
        text_name.setText(s);
        notification_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 5000);
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initLocation();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Permission permission = new Permission(context);
        userInfo = SceneKey.sessionManager.getUserInfo();
        initView();
        dimmedEffect();
        isPermissionAvail = permission.checkLocationPermission();

        replaceFragment(new Home_No_Event_Fragment());
        try {
            if (userInfo.makeAdmin.equals(Constant.ADMIN_YES)) {
                latitude = Double.parseDouble(userInfo.adminLat);
                longitude = Double.parseDouble(userInfo.adminLong);
                latitudeAdmin = latitude;
                longitudeAdmin = longitude;

            } else {
                latitude = Double.parseDouble(userInfo.lat);
                longitude = Double.parseDouble(userInfo.longi);
                latitudeAdmin = latitude;
                longitudeAdmin = longitude;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rtlv_home.callOnClick();

        if(fromSearch){
            if(tablayout_home != null){
                tablayout_home.getTabAt(2).select();
            }
        }
    }

    private void initView() {
        frame_fragments = findViewById(R.id.frame_fragments);
        frm_bottmbar = findViewById(R.id.frm_bottmbar);
        rl_title_view = findViewById(R.id.rl_title_view);
        tvHomeTitle = findViewById(R.id.tvHomeTitle);
        tv_key_points = findViewById(R.id.tv_key_points_new);
        //txt_five = findViewById(R.id.txt_five);
        tv_title = findViewById(R.id.tv_title);
        tv_alert_badge_count = findViewById(R.id.tv_alert_badge_count);
        ImageView img_setting = findViewById(R.id.img_setting);

        rtlv_alert = findViewById(R.id.rtlv_alert);
        rtlv_home = findViewById(R.id.rtlv_home);
        rtlv_profile = findViewById(R.id.rtlv_profile);
        rtlv_reward = findViewById(R.id.rtlv_reward);


        rl_title_view_home = findViewById(R.id.rl_title_view_home);
        rl_title_main_view = findViewById(R.id.rl_title_main_view);
        rl_toolbar_alert = findViewById(R.id.rl_toolbar_alert);
        rl_profileView = findViewById(R.id.rl_profileView);


        img_home_logo = findViewById(R.id.img_home_logo);
        //img_three_one = findViewById(R.id.img_three_one);
        bottom_margin_view = findViewById(R.id.bottom_margin_view);
        //txt_notification = findViewById(R.id.txt_notification);

        setOnClick(rtlv_alert, rtlv_home, rtlv_profile, rtlv_reward, img_setting);

        tv_key_points.setText(userInfo.key_points);


        //tvHomeTitle.setText(userInfo().fullname + " " + userInfo().lastName);
        tvHomeTitle.setText(userInfo().fullname);

        //initArc();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        ActivityWidth = width;
        ActivityHeight = height;

        customProgressBar = new CustomProgressBar(context);
        utility = new Utility(context);

        handleNotification(getIntent());
    }

    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    private void initLocation() {
        try {
            // get GPS status
            checkGPS = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // get network provider status
            boolean checkNetwork = locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               /* CustomPopup customPopup = new CustomPopup(HomeActivity.this);
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

    void dimmedEffect() {
        Animation animat1 = AnimationUtils.loadAnimation(this, R.anim.one_fade);
        Animation animat2 = AnimationUtils.loadAnimation(this, R.anim.two_fade);
        Animation animat3 = AnimationUtils.loadAnimation(this, R.anim.three_fade);
        Animation animat4 = AnimationUtils.loadAnimation(this, R.anim.four_fade);

        getImgV(rtlv_home).setAnimation(animat1);
        getImgV(rtlv_alert).setAnimation(animat2);
        getImgV(rtlv_reward).setAnimation(animat3);
        getImgV(rtlv_profile).setAnimation(animat4);
    }

    public UserInfo userInfo() {

        if (!SceneKey.sessionManager.isLoggedIn()) {
            SceneKey.sessionManager.logout(HomeActivity.this);
        }
        return SceneKey.sessionManager.getUserInfo();
    }

    //Shubham.................................

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rtlv_home:

                if (lastClick != R.id.rtlv_home) {
                    lastClick = R.id.rtlv_home;
                    rl_title_view_home.setVisibility(View.VISIBLE);
                    rl_title_main_view.setVisibility(View.GONE);
                    rl_toolbar_alert.setVisibility(View.GONE);
                    rl_profileView.setVisibility(View.GONE);

                    if (isPermissionAvail) {
                        if(!isFirstTimeTrending){
                            isFirstTimeTrending = true;
                            replaceFragment(new Trending_Fragment());

                            View tabView = tablayout_home.getTabAt(0).getCustomView();
                            // get inflated children Views the icon and the label by their id
                            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                            tab_label.setTextColor(getResources().getColor(R.color.colorPrimaryDark_new));
                            tab_icon.setImageResource(tabIconsActive[0]);

                            //tablayout_home.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);
                        }
                      else {
                            replaceFragment(new Trending_Fragment());

                            View tabView = tablayout_home.getTabAt(0).getCustomView();
                            // get inflated children Views the icon and the label by their id
                            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
                            tab_label.setTextColor(getResources().getColor(R.color.colorPrimaryDark_new));
                            tab_icon.setImageResource(tabIconsActive[0]);

                           // tablayout_home.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);
                            tablayout_home.getTabAt(0).select();
                        }
                        setBottomBar((RelativeLayout) v, lastclicked);

                    } else {
                        utility.snackBar(rtlv_home, "Location permission not available", 0);
                    }
                }

                break;

            case R.id.rtlv_alert:

                if (lastClick != R.id.rtlv_alert) {
                    lastClick = R.id.rtlv_alert;
                    rl_toolbar_alert.setVisibility(View.VISIBLE);
                    rl_title_view_home.setVisibility(View.GONE);
                    rl_title_main_view.setVisibility(View.GONE);
                    rl_profileView.setVisibility(View.GONE);
//                    tv_title.setText("My Scene");
                    tv_title.setText("Alerts");

                    if (isPermissionAvail) {
                       // replaceFragment(new Alert_fragment());
                        replaceFragment(new OfferSFragment());
                        setBottomBar((RelativeLayout) v, lastclicked);
                    } else {
                        utility.snackBar(rtlv_home, "Location permission not available", 0);
                    }
                }

                break;

            case R.id.img_setting:

               // if (lastClick != R.id.img_setting) {
                    lastClick = R.id.img_setting;
                    Intent seetingIntet = new Intent(this, SettingActivtiy.class);
                    startActivity(seetingIntet);
               // }

                break;

            case R.id.rtlv_profile:

                if (lastClick != R.id.rtlv_profile) {
                    lastClick = R.id.rtlv_profile;
                    rl_profileView.setVisibility(View.VISIBLE);
                    rl_toolbar_alert.setVisibility(View.GONE);
                    rl_title_view_home.setVisibility(View.GONE);
                    rl_title_main_view.setVisibility(View.GONE);

                    if (isPermissionAvail) {

                        EventAttendy attendy = new EventAttendy();
                        attendy.userid = (userInfo.userid);
                        attendy.userFacebookId = (userInfo.userFacebookId);
                        attendy.setUserimage(userInfo.getUserImage());
                        attendy.username = (userInfo.userName);
                        VolleySingleton.getInstance(this.getBaseContext()).cancelPendingRequests("HomeApi");
                        replaceFragment(new ProfileNew_fragment().setData(attendy, true, null, 0));
                        //replaceFragment(new Profile_Fragment());
                        setBottomBar((RelativeLayout) v, lastclicked);
                    } else {
                        utility.snackBar(rtlv_home, "Location permission not available", 0);
                    }
                }
                break;

            case R.id.rtlv_reward:

                if (lastClick != R.id.rtlv_reward) {
                    lastClick = R.id.rtlv_reward;
                    //                    rl_toolbar_alert.setVisibility(View.GONE);
//                    rl_title_view_home.setVisibility(View.GONE);
//                    rl_title_main_view.setVisibility(View.VISIBLE);
//                    rl_profileView.setVisibility(View.GONE);
                    //checkFregment();

                    rl_toolbar_alert.setVisibility(View.VISIBLE);
                    rl_title_view_home.setVisibility(View.GONE);
                    rl_title_main_view.setVisibility(View.GONE);
                    rl_profileView.setVisibility(View.GONE);
                    tv_title.setText("Reward");
                    hideKeyBoard();
                    //replaceFragment(new OfferSFragment());
                    replaceFragment(new WalletsFragment());
                    setBottomBar((RelativeLayout) v, lastclicked);
                   // txt_five.setTextColor(getResources().getColor(R.color.selected_bb_text));
                }

                break;

            case R.id.img_profile:

                if (lastClick != R.id.img_profile) {
                    lastClick = R.id.img_profile;
                    try {
                        EventAttendy attendy = new EventAttendy();
                        attendy.userid = (userInfo.userid);
                        attendy.userFacebookId = (userInfo.userFacebookId);
                        attendy.setUserimage(userInfo.getUserImage());
                        attendy.username = (userInfo.userName);
                        VolleySingleton.getInstance(this.getBaseContext()).cancelPendingRequests("HomeApi");
                        addFragment(new Profile_Fragment().setData(attendy, true, null, 0), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;

        }
    }

    protected void onResume() {
        registerReceiver(myReceiver, new IntentFilter("BroadcastNotification"));

        Fragment fragment = getCurrentFragment();
        if (fragment instanceof Event_Fragment) {
            Event_Fragment event_fragment = (Event_Fragment) fragment;
            event_fragment.getAllData();
            event_fragment.onResume();
        } else if (fragment instanceof ProfileNew_fragment) {
//            ProfileNew_fragment profileNew_fragment = (ProfileNew_fragment) fragment;
//            tvHomeTitle.setText(userInfo().fullname);
//            profileNew_fragment.onResume();
            lastClick = 0;
            rtlv_profile.performClick();
        }
        else if (fragment instanceof Trending_Fragment) {
            replaceFragment(new Trending_Fragment());

            View tabView = tablayout_home.getTabAt(0).getCustomView();
            // get inflated children Views the icon and the label by their id
            TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);
            tab_label.setTextColor(getResources().getColor(R.color.colorPrimaryDark_new));
            tab_icon.setImageResource(tabIconsActive[0]);

            // tablayout_home.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorPrimaryDark_new), PorterDuff.Mode.SRC_IN);
            tablayout_home.getTabAt(0).select();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.myReceiver);
    }

    private void setBottomBar(RelativeLayout v, final RelativeLayout lastClicked) {
        //setRtlvText(v, true);

        if (lastClicked != null) {

           // setRtlvText(lastClicked, false);

            switch (lastClicked.getId()) {

                case R.id.rtlv_home:
                        getImgV(lastClicked).setImageResource(R.drawable.home);
                    break;


                case R.id.rtlv_alert:
                    getImgV(lastClicked).setImageResource(R.drawable.alert);
                    break;

                case R.id.rtlv_reward:
                    getImgV(lastClicked).setImageResource(R.drawable.reward);
                    break;


                case R.id.rtlv_profile:
                    getImgV(lastClicked).setImageResource(R.drawable.profile_active);
                    break;

            }

        }
        switch (v.getId()) {

            case R.id.rtlv_home:
                getImgV(v).setImageResource(R.drawable.home_active);
                break;

            case R.id.rtlv_alert:
                 getImgV(v).setImageResource(R.drawable.alert_active);

                break;

            case R.id.rtlv_reward:
                getImgV(v).setImageResource(R.drawable.reward_active);
                break;

            case R.id.rtlv_profile:
                getImgV(v).setImageResource(R.drawable.profile_active);
                break;
        }

        this.lastclicked = v;
    }

    void chaeckReward(boolean chaeckValue) {
        if (chaeckValue) {
            tvHomeTitle.setVisibility(View.GONE);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tvHomeTitle.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
        }
    }
    //..............................................

    void setRtlvText(RelativeLayout rtlv, boolean isClicked) {
        if (isClicked) {
            ((TextView) rtlv.getChildAt(1)).setTextColor(getResources().getColor(R.color.selected_bb_text));
        } else {
            ((TextView) rtlv.getChildAt(1)).setTextColor(getResources().getColor(R.color.gray2));
        }
    }

    ImageView getImgV(RelativeLayout rtlv) {
        return ((ImageView) rtlv.getChildAt(0));
    }

    public void replaceFragment(Fragment fragmentHolder) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            String fragmentName = fragmentHolder.getClass().getName();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame_fragments, fragmentHolder, fragmentName).addToBackStack(fragmentName);
            fragmentTransaction.commit();
            hideKeyBoard();
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

    @Override
    public void onBackPressed() {
        hideKeyBoard();
        Handler handler = new Handler();
        Runnable runnable;
        //  Util.printLog(TAG, "" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            if (getCurrentFragment() instanceof BackPressListner) {
                if (((BackPressListner) getCurrentFragment()).onKeyBackPress()) return;
            }
            super.onBackPressed();

            onPopUpBackstage();
        } else {

            if (getCurrentFragment() instanceof BackPressListner) {
                if (((BackPressListner) getCurrentFragment()).onKeyBackPress()) return;
            }

            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackPress = false;
                }
            }, 1000);
            if (doubleBackPress) {
                handler.removeCallbacks(runnable);
                finish();
            } else {
                doubleBackPress = true;
            }
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

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            try {
                InputMethodManager imm = (InputMethodManager)
                       getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkEventAvailablity(final boolean showProgress) {
        if (latitudeAdmin != 0.0d && longitudeAdmin != 0.0d) {
            showProgDialog(false, TAG);
            getEventsByLocationApi();
        } else if (!checkGPS) {
            utility.checkGpsStatus();
        } else {
            showErrorPopup();
        }
    }

    private void getEventsByLocationApi() {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_BY_LOCAL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    try {
                        //{"status":0,"message":"No data were found!","userInfo":{"fullname":"Thomas Lewis","address":"Los Angeles County,California","lat":"22.7051015","longi":"75.9090669","makeAdmin":"yes","key_points":"31"}}
                        JSONObject jo = new JSONObject(response);
                        Log.e(TAG, "HOME RESPONSE" + response);

                        if (jo.has("status")) {
                            int status = jo.getInt("status");
                            if (status == 0) {
                                replaceFragment(new Home_No_Event_Fragment());
                                //Utility.showToast(context, jo.getString("message"), 0);
                            }
                            if (jo.has("userInfo")) {
                                if (userInfo == null)
                                    userInfo = SceneKey.sessionManager.getUserInfo();
                                Object intervention = jo.get("userInfo");
                                if (intervention instanceof JSONArray) {
                                    SceneKey.sessionManager.logout(HomeActivity.this);
                                }
                                JSONObject user = jo.getJSONObject("userInfo");

                                if (user.has("makeAdmin"))
                                    userInfo.makeAdmin = (user.getString("makeAdmin"));

                                if (user.has("lat"))
                                    userInfo.lat = (user.getString("lat"));

                                if (user.has("longi"))
                                    userInfo.longi = (user.getString("longi"));

                                if (user.has("address"))
                                    userInfo.address = (user.getString("address"));

                                if (user.has("fullname"))
                                    userInfo.fullname = (user.getString("fullname"));

                                if (user.has("key_points"))
                                    userInfo.key_points = (user.getString("key_points"));

                                if (user.has("bio"))
                                    userInfo.bio = (user.getString("bio"));
                                updateSession(userInfo);

                                // New Code
                                if (user.getString("fullname").equals("")) {
                                    SceneKey.sessionManager.logout(HomeActivity.this);
                                }
                            }
                            //else if()
                        } else {
                            if (jo.has("userInfo")) {
                                if (userInfo == null)
                                    userInfo = SceneKey.sessionManager.getUserInfo();
                                Object intervention = jo.get("userInfo");
                                if (intervention instanceof JSONArray) {
                                    SceneKey.sessionManager.logout(HomeActivity.this);
                                    return;
                                }
                                JSONObject user = jo.getJSONObject("userInfo");
                                if (user.has("makeAdmin"))
                                    userInfo.makeAdmin = (user.getString("makeAdmin"));

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

                                if (user.has("bio"))
                                    userInfo.bio = (user.getString("bio"));
                                updateSession(userInfo);

                                // New Code
                                if (user.getString("fullname").equals("")) {
                                    SceneKey.sessionManager.logout(HomeActivity.this);
                                }
                            }
                            if (jo.has("events")) {
                                if (eventsArrayList == null) eventsArrayList = new ArrayList<>();
                                eventsArrayList.clear();
                                JSONArray eventAr = jo.getJSONArray("events");
                                for (int i = 0; i < eventAr.length(); i++) {
                                    JSONObject object = eventAr.getJSONObject(i);
                                    Events events = new Events();
                                    if (object.has("venue"))
                                        events.setVenueJSON(object.getJSONObject("venue"));
                                    if (object.has("artists"))
                                        events.setArtistsArray(object.getJSONArray("artists"));
                                    if (object.has("events"))
                                        events.setEventJson(object.getJSONObject("events"));
                                    try {

                                        int time_format = 0;
                                        try {
                                            time_format = Settings.System.getInt(getContentResolver(), Settings.System.TIME_12_24);
                                        } catch (Settings.SettingNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                        events.settimeFormat(time_format);
                                        events.setRemainingTime();
                                    } catch (Exception e) {
                                        Utility.e("Exception time", e.toString());
                                    }

                                    eventsArrayList.add(events);
                                    // Util.printLog("Size",eventsArrayList.size()+"");
                                }

                                try {
                                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                                        if (fragment instanceof Map_Fragment) {
                                            if (map_fragment != null)
                                                map_fragment.notifyAdapter(eventsArrayList);
                                        }
                                    }
                                    checkedEventToJoin();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    Utility.e("Near Event Size", String.valueOf(eventsNearbyList.size()));
                                    if (eventsNearbyList.size() != 0)
                                        onNearByEventFound();
                                    else {
                                        replaceFragment(new Home_No_Event_Fragment());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                           /* else{
                                try {
                                    Utility.e("Near Event Size", String.valueOf(eventsNearbyList.size()));
                                    if (eventsNearbyList.size() != 0)
                                        onNearByEventFound();
                                    else {
                                        replaceFragment(new Home_No_Event_Fragment());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }*/
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    }

                    if (isBgNoti) {
                        isBgNoti = false;
                        forBackGroundNotification();
                    }

                    try {
                        //Picasso.with(HomeActivity.this).load(userInfo.getUserImage()).placeholder(R.drawable.image_default_profile).into(img_profile);
                    } catch (Exception e) {
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
                    params.put("lat", getLatLng()[0]);
                    params.put("long", getLatLng()[1]);
                    params.put("user_id", userInfo.userid);
                    params.put("updateLocation", Constant.ADMIN_NO);
                    params.put("fullAddress", userInfo.address);

                    Utility.e(TAG, "HOME PARAMS" + params.toString());
                    return params;
                }
            };

            VolleySingleton.getInstance(this.getBaseContext()).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(frame_fragments, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
        try {
            //Picasso.with(this).load(userInfo.getUserImage()).placeholder(R.drawable.image_default_profile).into(img_profile);
            tv_key_points.setText(userInfo.key_points);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isLocation() {
        try {
            /* if(Double.parseDouble(userInfo.longitude)==0.0D){
                return false;
            }
            else return !(Double.parseDouble(userInfo.latitude) == 0.0D);*/
            return !(Double.parseDouble(userInfo.longi) == 0.0D) && !(Double.parseDouble(userInfo.lat) == 0.0D);
        } catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showErrorPopup() {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_with_btn);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
                showProgDialog(false, TAG);


                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {
                        dismissProgDialog();
                        switch ("checkEventAvailablity") {
                            case "checkEventAvailablity":
                                checkEventAvailablity(true);
                                break;

                            case "rtlv_home":
                                position = 0;
                                rtlv_home.callOnClick();
                                break;

                           /* case "getCurrentLatLng":
                                getCurrentLatLng();
                                break;*/

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

    /***
     * This method is to check nearby events accouding to given conditions i.e, user should be within 200 meter radius from the event
     * && the event should be started i.e, user current time should be less then end time and should be greater then Start time.
     * if both the conditions are fullfiled then te event will show insted of the try demo screen.
     */
    public void checkedEventToJoin() {
        if (eventsNearbyList == null) eventsNearbyList = new ArrayList<>();
        else eventsNearbyList.clear();
        for (Events events : eventsArrayList) {

            Location startLocation = new Location("Location My");

            startLocation.setLatitude(Double.parseDouble(getLatLng()[0]));
            startLocation.setLongitude(Double.parseDouble(getLatLng()[1]));

            Location endLocation = new Location("Location end");

            endLocation.setLatitude(Double.parseDouble(events.getVenue().getLatitude()));
            endLocation.setLongitude(Double.parseDouble(events.getVenue().getLongitude()));

            //   Log.e("START LOC", startLocation.getLatitude() + "---" + startLocation.getLongitude());
            //  Log.e("END LOC", endLocation.getLatitude() + "---" + endLocation.getLongitude());
            double distance = endLocation.distanceTo(startLocation);

            try {
                boolean b = checkWithTime(events.getEvent().event_date, events.getEvent().interval);
                if (distance <= Constant.MAXIMUM_DISTANCE && b) {
                    events.setOngoing(true);
                    eventsNearbyList.add(events);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_LONG).show();
            }
        }
    }

    private float getDistancBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {

        float[] distance = new float[2];

        Log.e("STEP 0", lat1 + "===" + lon1 + "===" + lat2 + "===" + lon2);
        Location.distanceBetween(lat1, lon1,
                lat2, lon2, distance);
        return distance[0];
    }

    private void kjgfhkg(Events events) {
        /*Location startLocation = new Location("Location My");

        startLocation.setLatitude(Double.parseDouble(getLatLng()[0]));
        startLocation.setLongitude(Double.parseDouble(getLatLng()[1]));

        Location endLocation = new Location("Location end");

        endLocation.setLatitude(Double.parseDouble(events.getVenue().getLatitude()));
        endLocation.setLongitude(Double.parseDouble(events.getVenue().getLongitude()));
        */
        // double distance = endLocation.distanceTo(startLocation);

        float distance = getDistancBetweenTwoPoints(Double.parseDouble(getLatLng()[0]), Double.parseDouble(getLatLng()[1]), Double.parseDouble(events.getVenue().getLatitude()), Double.parseDouble(events.getVenue().getLongitude()));

        Log.e("STEP 1", distance + "");
        try {
            boolean b = checkWithTime(events.getEvent().event_date, events.getEvent().interval);
            Log.e("STEP 3", b + "");

            if (distance <= Constant.MAXIMUM_DISTANCE && b) {
                events.setOngoing(true);
                eventsNearbyList.add(events);
            }


        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_LONG).show();
        }
    }

    /*********************************
     * Chekcing the Events
     ******************************************************/

    /*public boolean checkWithTime(final String date, double interval) throws ParseException {
        String[] dateSplit = (date.replace("TO", "T")).replace(" ", "T").split("T");
        Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).parse(dateSplit[0] + " " + dateSplit[1]);
        //   Date endTime = new Date(startTime.getTime() + (long) (interval * 60 * 60 * 1000));

        long currentTime = Calendar.getInstance().getTime().getTime();

        Log.e("STEP 2", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).format(Calendar.getInstance().getTime()) + "");
        Log.e("STEP 2 - START DATE", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).format(startTime) + "");
        *//*Utility.e(TAG, "STEP 2 - Event Date " + dateSplit[0] + " " + dateSplit[1]);
        Utility.e(TAG, " Date " + currentTime + " " + startTime.getTime());*//*
        return currentTime > startTime.getTime();  //old ios logic

        //  return currentTime < endTime.getTime() && currentTime > startTime.getTime();

    }*/
    public boolean checkWithTime(final String date, double interval) throws ParseException {
        String[] dateSplit = (date.replace("TO", "T")).replace(" ", "T").split("T");
        Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)).parse(dateSplit[0] + " " + dateSplit[1]);

        long currentTime = Calendar.getInstance().getTime().getTime();


        return currentTime > startTime.getTime();  //old ios logic

        //  return currentTime < endTime.getTime() && currentTime > startTime.getTime();

    }

    private boolean onNearByEventFound() {
        Utility.e(TAG, eventsNearbyList.size() + " : ");
        if (eventsNearbyList.size() >= 1) {
            NearEvent_Fragment nearEvent_fragment = new NearEvent_Fragment();
            nearEvent_fragment.setEventsList(eventsNearbyList);
            nearEvent_fragment.setNearLatLng(new String[]{getLatLng()[0], getLatLng()[1]});
            try {
                replaceFragment(nearEvent_fragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else return true;
    }

    /*public common methods start */

    public void setTitle(String title) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitleVisibility(int visibility) {
        try {
            rl_title_view.setVisibility(visibility);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String[] getLatLng() {
        String latLng[] = new String[2];

        if (userInfo.makeAdmin.contains(Constant.ADMIN_YES) && userInfo().currentLocation) {
            latLng[0] = userInfo.adminLat = (latitudeAdmin + "");
            latLng[1] = userInfo.adminLong = (longitudeAdmin + "");
            return latLng;
        } else if (userInfo.makeAdmin.contains(Constant.ADMIN_YES) && isLocation()) {
            //userInfo.setAddress("");
            latLng[0] = userInfo.adminLat;
            latLng[1] = userInfo.adminLong;
            return latLng;
        } else {
            latLng[0] = latitude + "";
            latLng[1] = longitude + "";
            return latLng;
        }

    }

    public double getDistanceMile(Double[] LL) {
        Utility.e("LAT LONG ", LL[0] + " " + LL[1] + " " + LL[2] + " " + LL[3]);

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(LL[0]);
        startPoint.setLongitude(LL[1]);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(LL[2]);
        endPoint.setLongitude(LL[3]);

        double distance = (startPoint.distanceTo(endPoint)) * 0.00062137;
        return new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public int getDistance(Double[] LL) {
        Utility.e("LAT LONG ", LL[0] + " " + LL[1] + " " + LL[2] + " " + LL[3]);
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(LL[0]);
        startPoint.setLongitude(LL[1]);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(LL[2]);
        endPoint.setLongitude(LL[3]);

        double distance = startPoint.distanceTo(endPoint);

        return (int) distance;
    }

    public double phpDistance(Double[] LL) {
        Utility.e(TAG, " Distance " + 6371000 * (Math.acos(Math.cos(Math.toRadians(LL[0])) * Math.cos(Math.toRadians(LL[2])) * Math.cos(Math.toRadians(LL[3]) - Math.toRadians(LL[1])) + Math.sin(Math.toRadians(LL[0])) * Math.sin(Math.toRadians(LL[2])))));
        return 6371000 * (Math.acos(Math.cos(Math.toRadians(LL[0])) * Math.cos(Math.toRadians(LL[2])) * Math.cos(Math.toRadians(LL[3]) - Math.toRadians(LL[1])) + Math.sin(Math.toRadians(LL[0])) * Math.sin(Math.toRadians(LL[2]))));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Fragment getCurrentFragment() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                return fragmentManager.findFragmentByTag(fragmentTag);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * @param ViewDot One of {  #VISIBLE}, { #INVISIBLE}, or { #GONE}.
     */
    public void setBBVisibility(int ViewDot, String TAG) {
        Utility.e(TAG, " B B visiballity " + ViewDot + " TAG " + TAG);
        try {
            frm_bottmbar.setVisibility(ViewDot);
            if (ViewDot == View.GONE) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frame_fragments.getLayoutParams();
              /*  if (SceneKey.sessionManager.isSoftKey())
                    layoutParams.bottomMargin = ActivitybottomMarginOne;
                else layoutParams.bottomMargin = 0;*/
                frame_fragments.setLayoutParams(layoutParams);
                bottom_margin_view.setVisibility(View.GONE);
                rl_title_view.setVisibility(View.GONE);
                setTopStatus();
                //  top_status.setVisibility(View.GONE);
            } else {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frame_fragments.getLayoutParams();
                //  layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.bottomBar_margin);
                frame_fragments.setLayoutParams(layoutParams);
                bottom_margin_view.setVisibility(View.VISIBLE);
                rl_title_view.setVisibility(View.VISIBLE);
                setTopStatus();
                // top_status.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBBVisibility(final int ViewDot, final int delay, String TAG) {
        Utility.e(TAG, " B B visiballity " + ViewDot + " TAG " + TAG);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (ViewDot == View.GONE) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frame_fragments.getLayoutParams();
                   /* if (SceneKey.sessionManager.isSoftKey())
                        layoutParams.bottomMargin = ActivitybottomMarginOne;
                    else layoutParams.bottomMargin = 0;*/
                    frame_fragments.setLayoutParams(layoutParams);
                    rl_title_view.setVisibility(View.GONE);
                    frm_bottmbar.setVisibility(View.GONE);
                    bottom_margin_view.setVisibility(View.GONE);
                    //  top_status.setVisibility(View.GONE);
                    setTopStatus();

                } else {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up1);
                    Animation animation1 = AnimationUtils.loadAnimation(context, R.anim.slide_in_up);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frame_fragments.getLayoutParams();
                    //  layoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.bottomBar_margin);
                    frame_fragments.setLayoutParams(layoutParams);
                    rl_title_view.setVisibility(View.VISIBLE);
                    rl_title_view.startAnimation(animation1);
                    bottom_margin_view.setVisibility(View.VISIBLE);
                    //   top_status.setVisibility(View.VISIBLE);
                    setTopStatus();
                    frm_bottmbar.setVisibility(View.VISIBLE);
                    frm_bottmbar.startAnimation(animation);
                }
            }
        }, delay);

    }

    public void backPressToPosition() {
        try {
            switch (position) {

                case 1:
                    rtlv_home.callOnClick();
                    break;
                case 2:
                    rtlv_alert.callOnClick();
                    break;
                case 3:
                    rtlv_reward.callOnClick();
                    break;
                case 4:
                    rtlv_profile.callOnClick();
                    break;

                default:
                    rtlv_home.callOnClick();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideStatusBar() {
        View decorView = getWindow().getDecorView();
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

    public void setTopStatus() {

        if (isKitKat) {
            //  top_status.setBackgroundResource(R.color.black);
        }
        if (isApiM) {
            //  top_status.setBackgroundResource(R.color.white);
        }
    }

    public void onPopUpBackstage() {
        Fragment fragment = getCurrentFragment();

        if (fragment instanceof StatusBarHide) {
            hideStatusBar();
        } else showStatusBar();

        if (fragment != null && fragment instanceof Profile_Fragment) {
            Profile_Fragment profileFragment = (Profile_Fragment) fragment;
            profileFragment.onResume();
        } else if (fragment != null && fragment instanceof Home_No_Event_Fragment || fragment instanceof NearEvent_Fragment) {
            if (fragment instanceof NearEvent_Fragment) {
                NearEvent_Fragment nearEvent_fragment = (NearEvent_Fragment) fragment;
                eventsNearbyList.clear();
                eventsArrayList.clear();
                nearEvent_fragment.setRecyclerView();
                showProgDialog(false, TAG);
                getEventsByLocationApi();
//                getEventsByLocationApi();
            } else {
                showProgDialog(false, TAG);
                getEventsByLocationApi();
            }

        } else if (fragment != null && fragment instanceof Search_Fragment) {
            Search_Fragment search_fragment = (Search_Fragment) fragment;
            search_fragment.searchData();
        } else if (fragment != null && fragment instanceof Event_Fragment) {
            Event_Fragment event_fragment = (Event_Fragment) fragment;
            event_fragment.onResume();
        }
    }

    //TODO message on increment or decrement
    public void incrementKeyPoints(String msg) {
        final int points = Integer.parseInt(userInfo.key_points);
        new Aws_Web_Service() {
            @Override
            public okhttp3.Response onResponseUpdate(okhttp3.Response response) {
                Utility.e(TAG, "Increment response " + response);
                if (response == null) return null;
                try {
                    String s = response.body().string();
                    if (new JSONObject(s).getInt("serverStatus") == 2) {
                        Utility.e("Response", s);
                        userInfo.key_points = ((points + 1) + "");
                        showKeyPoints("+1");
                        updateSession(userInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }.updateKeyPoint(points + 1, userInfo.userid);
    }

    private void showKeyPoints(String s) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_keypoint_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationLeftRight; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);

        TextView tvKeyPoint;

        tvKeyPoint = dialog.findViewById(R.id.tvKeyPoint);
        tvKeyPoint.setText(s);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);

        dialog.show();
    }

  /*  Runnable notiRunnable = new Runnable() {
        @Override
        public void run() {
            *//*................................................>*//*
            if (!eventId.isEmpty() && notificationType1.equals("2")) {
                Events nEvent = new Events();
                String venue_name = "";
                String[] venuLatLng = new String[2];
                venuLatLng[0] = String.valueOf(latitude);
                venuLatLng[1] = String.valueOf(longitude);
                if (eventsArrayList == null) eventsArrayList = new ArrayList<>();
                for (Events event : eventsArrayList) {
                    if (eventId.equals(event.getEvent().event_id)) {
                        nEvent = event;
                        venue_name = event.getVenue().getVenue_name();
                        venuLatLng[0] = event.getVenue().getLatitude();
                        venuLatLng[1] = event.getVenue().getLongitude();
                        break;
                    }
                }
                Event_Fragment fragment = Event_Fragment.newInstance("home");
                fragment.setData(eventId, venue_name, nEvent, new String[]{getLatLng()[0], getLatLng()[1]}, venuLatLng, false);
                addFragment(fragment, 1);

            } else {
                Fragment fragment = getCurrentFragment();
                if (fragment != null && fragment instanceof Event_Fragment) {
                    Event_Fragment event_fragment = (Event_Fragment) fragment;
                    event_fragment.getAllData();
                }
            }
        }
    };*/

    public void forBackGroundNotification() {
        if (!eventId.isEmpty() && notificationType1.equals("2") || notificationType1.equals("1") || notificationType1.equals("22")) {
            Events nEvent = new Events();
            String venue_name = "";
            String[] venuLatLng = new String[2];
            venuLatLng[0] = String.valueOf(latitude);
            venuLatLng[1] = String.valueOf(longitude);
            if (eventsArrayList == null) eventsArrayList = new ArrayList<>();
            for (Events event : eventsArrayList) {
                if (eventId.equals(event.getEvent().event_id)) {
                    nEvent = event;
                    venue_name = event.getVenue().getVenue_name();
                    venuLatLng[0] = event.getVenue().getLatitude();
                    venuLatLng[1] = event.getVenue().getLongitude();
                    break;
                }
            }

            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);

           /* Event_Fragment fragment = Event_Fragment.newInstance("homeTab");
            fragment.setData(eventId, venue_name, nEvent, new String[]{getLatLng()[0], getLatLng()[1]}, venuLatLng, false);
            addFragment(fragment, 1);*/

        } else {
            Fragment fragment = getCurrentFragment();
            if (fragment != null && fragment instanceof Event_Fragment) {
                Event_Fragment event_fragment = (Event_Fragment) fragment;
                event_fragment.getAllData();
            }
        }
    }

    /*................................................>*/

    private void showCustomNotification(String s) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_push);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationLeftRight; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);

        TextView title_name, text_name;
        RelativeLayout notification_view;

        notification_view = dialog.findViewById(R.id.notification_view);
        title_name = dialog.findViewById(R.id.title_name);
        text_name = dialog.findViewById(R.id.text_name);
        title_name.setText("ScenceKey");
        text_name.setText(s);
        notification_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*................................................>*/
                if (!eventId.isEmpty() && notificationType1.equals("2") || notificationType1.equals("1") || notificationType1.equals("22")) {
                    Events nEvent = new Events();
                    String venue_name = "";
                    String[] venuLatLng = new String[2];
                    venuLatLng[0] = String.valueOf(latitude);
                    venuLatLng[1] = String.valueOf(longitude);
                    if (eventsArrayList == null) eventsArrayList = new ArrayList<>();
                    for (Events event : eventsArrayList) {
                        if (eventId.equals(event.getEvent().event_id)) {
                            nEvent = event;
                            venue_name = event.getVenue().getVenue_name();
                            venuLatLng[0] = event.getVenue().getLatitude();
                            venuLatLng[1] = event.getVenue().getLongitude();
                            break;
                        }
                    }
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("event_id", eventId);
                    intent.putExtra("homeTab", "homeTab");
                    startActivity(intent);

                  /*  Event_Fragment fragment = Event_Fragment.newInstance("homeTab");
                    fragment.setData(eventId, venue_name, nEvent, new String[]{getLatLng()[0], getLatLng()[1]}, venuLatLng, false);
                    addFragment(fragment, 1);*/

                } else {
                    Fragment fragment = getCurrentFragment();
                    if (fragment != null && fragment instanceof Event_Fragment) {
                        Event_Fragment event_fragment = (Event_Fragment) fragment;
                        event_fragment.getAllData();
                    }
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
        dialog.show();
    }

    private void rewardNotification(String s) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_push);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationLeftRight; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);

        TextView title_name, text_name;
        RelativeLayout notification_view;

        notification_view = dialog.findViewById(R.id.notification_view);
        title_name = dialog.findViewById(R.id.title_name);
        text_name = dialog.findViewById(R.id.text_name);
        title_name.setText("ScenceKey");
        text_name.setText(s);
        notification_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rtlv_reward.callOnClick();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);

        dialog.show();
    }

    public void decrementKeyPoints(final String msg) {
        final int points = Integer.parseInt(userInfo.key_points);
        new Aws_Web_Service() {
            @Override
            public okhttp3.Response onResponseUpdate(okhttp3.Response response) {
                if (response == null) return null;
                try {
                    String s = response.body().string();
                    if (new JSONObject(s).getInt("serverStatus") == 2) {
                        //   utility.showCustomPopup(msg, String.valueOf(R.font.arial_regular));

                        showKeyPoints("-1");
                        userInfo.key_points = (points <= 0 ? 0 + "" : (points - 1) + "");
                        updateSession(userInfo);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }.updateKeyPoint((points <= 0 ? 25 : points - 1), userInfo.userid);
    }

    public void keyPointsUpdate() {

        final int points = Integer.parseInt(userInfo.key_points);
        new Aws_Web_Service() {
            @Override
            public okhttp3.Response onResponseUpdate(okhttp3.Response response) {
                if (response == null) return null;
                try {
                    String s = response.body().string();
                    if (new JSONObject(s).getInt("serverStatus") == 2) {

                        userInfo.key_points = Integer.parseInt(userInfo.key_points) - 1 + "";
                        updateSession(userInfo);

                        if (userInfo.key_points.equals("0")) {
                            userInfo.key_points = 25 + "";
                            updateSession(userInfo);
                        }
                        dismissProgDialog();
                        //   utility.showCustomPopup(msg, String.valueOf(R.font.arial_regular));
                        // TSnackbar.make(rl_title_view, "-1 Key Points!", TSnackbar.LENGTH_LONG).show();
                        showKeyPoints("-1");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }.updateKeyPoint((points <= 0 ? 25 : points - 1), userInfo.userid);
    }

    public void showCustomPopup(String message, final int call) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvPopupOk, tvMessages;

        tvMessages = dialog.findViewById(R.id.custom_popup_tvMessage);
        tvPopupOk = dialog.findViewById(R.id.custom_popup_ok);
        tvPopupOk.setText(R.string.ok);
        tvMessages.setText(message);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (call == 1) {
                    //incrementKeyPoints(getString(R.string.kp_like));
                } else {
                    //decrementKeyPoints(getString(R.string.kp_unlike));
                }
            }
        });

        tvPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show location settings when the user acknowledges the alert dialog
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public String getCurrentTimeInFormat() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())).format(new Date(System.currentTimeMillis()));
    }

    public String getAddress(double latitude, double longitude) {
        String result = null;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            //  String addressLine = addresses.get(0).getAddressLine(1);
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            // String postalCode = addresses.get(0).getPostalCode();
            // String knownName = addresses.get(0).getFeatureName();
            //result = knownName + " ," + addressLine + " , " + city + "," + state + "," + country + " counter" + counter;// Here 1 represent max location result to returned, by documents it recommended 1 to 5
            result = address + "," + city + "," + state + "," + country;// Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /*public common methods end*/

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // Logic to handle location object
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latitudeAdmin = location.getLatitude();
            longitudeAdmin = location.getLongitude();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.IMAGE_UPLOAD_CALLBACK) {
            if (resultCode == Activity.RESULT_OK) {
                if (getCurrentFragment() != null) {
                    try {
                        getCurrentFragment().onActivityResult(requestCode, resultCode, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void getDataFromFB(){
        //String userId = mDatabase.push().getKey();
        showProgDialog(true,"");
        mDatabase.child("dev").child("reward").child(SceneKey.sessionManager.getUserInfo().userid).
                child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = String.valueOf(dataSnapshot.getValue());
                if(!val.equalsIgnoreCase("") && val != null && !val.equalsIgnoreCase("null")) {
                    if (Integer.parseInt(val) > 0) {
                        tv_alert_badge_count.setText(val);
                        tv_alert_badge_count.setVisibility(View.VISIBLE);
                    }
                }
                dismissProgDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
               dismissProgDialog();
            }
        });
    }

//    public void getOffersList() {
//        if (utility.checkInternetConnection()) {
//            StringRequest request = new StringRequest(Request.Method.POST, WebServices.REWARD_OFFER, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.v("response", response);
//                    // get response
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        String status = jsonObject.getString("status");
//
//                        if (status.equals("success")) {
//                            JSONArray data = jsonObject.getJSONArray("allReward");
//                            if(data.length()>0){
//                                tv_alert_badge_count.setText(data.length());
//                                tv_alert_badge_count.setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                tv_alert_badge_count.setVisibility(View.GONE);
//                            }
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError e) {
//                    utility.volleyErrorListner(e);
//                }
//            }) {
//                @Override
//                public Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("lat", SceneKey.sessionManager.getUserInfo().lat);
//                    params.put("long", SceneKey.sessionManager.getUserInfo().longi);
//                    params.put("userId", SceneKey.sessionManager.getUserInfo().userid + "");
////                    params.put("userId", "1384");
//
//                    Utility.e(TAG, " params " + params.toString());
//                    return params;
//                }
//            };
//            VolleySingleton.getInstance(context).addToRequestQueue(request);
//            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
//        } else {
//            //utility.snackBar(rcViewTrending,getString(R.string.internetConnectivityError),0);
//            Toast.makeText(context, R.string.internetConnectivityError, Toast.LENGTH_SHORT).show();
//        }
//    }
}
