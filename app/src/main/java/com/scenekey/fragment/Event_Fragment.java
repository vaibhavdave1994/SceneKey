package com.scenekey.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scenekey.BuildConfig;
import com.scenekey.R;

import com.scenekey.activity.ForNotificactionActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.TheRoomActivity;
import com.scenekey.adapter.GridChipsAdapter;
import com.scenekey.adapter.Profile_Adapter;
import com.scenekey.adapter.UserInAdapter;
import com.scenekey.aws_service.AWSImage;
import com.scenekey.cropper.CropImage;
import com.scenekey.cropper.CropImageView;
import com.scenekey.cus_view.Grid_multiRow;
import com.scenekey.cus_view.ProfilePopUp_Notification;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomeClick;
import com.scenekey.helper.Permission;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.Floting_menuAction.FloatingActionButton;
import com.scenekey.lib_sources.Floting_menuAction.FloatingActionMenu;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.MyListenerForProfile;
import com.scenekey.listener.StatusBarHide;
import com.scenekey.liveSideWork.LiveProfileActivity;
import com.scenekey.liveSideWork.NotificationLiveActivity;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.EventDetails;
import com.scenekey.model.Events;
import com.scenekey.model.Feeds;
import com.scenekey.model.NotificationData;
import com.scenekey.model.Tags;
import com.scenekey.model.UserInfo;
import com.scenekey.util.ImageUtil;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class Event_Fragment extends Fragment implements View.OnClickListener, StatusBarHide {

    private static final String FROM_TAB = "from_tab";
    private static Timer timerHttp;
    private static UserInfo userInfo;
    public final String TAG = Event_Fragment.class.toString();
    public boolean canCallWebservice, isInfoVisible, isPopUpShowing, canGetNotification;
    public Double latitude, longitude;
    public TextView txt_event_name, txt_discrp, txt_f2_badge, txt_hide_all_two, btn_got_it, txt_discipI_f2, txt_hide_all;
    public ArrayList<Card> cardsList;
    public FloatingActionButton fabMenu1_like, fabMenu2_picture, fabMenu3_comment;
    public String evenName;
    private LinearLayout info_view;
    private RelativeLayout rtlv_top, demoView; //Demo Screen
    private ImageView img_infoget_f2, img_f10_back, image_map, img_notifnew;
    private RecyclerView usercomeInRecyclerView;
    private ScrollView scrl_all;
    private FloatingActionMenu floatBtn;
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private String eventId;
    private String venueName;

    private String[] currentLatLng;
    private int currentNudge, noNotify, timer;
    private Handler handler;
    private View popupview;
    private Dialog dialog;
    private Timer timerNudge;
    private Uri imageUri;
    private EventDetails eventDetails;
    private Events event;
    private ArrayList<NotificationData> nudgeList;
    private ArrayList<EventAttendy> userList;
    //map data
    private MapView map_view;
    private GoogleMap googleMap;


    private ProfilePopUp_Notification popup;
    private ForNotificactionActivity forNotificactionActivity;

    private AWSImage awsImage;
    private CognitoCredentialsProvider credentialsProvider;
    //New Code Shubham
    private RecyclerView listViewFragProfile;
    private Profile_Adapter adapter;
    private ArrayList<Feeds> feedsList;
    private boolean myProfile;
    private EventAttendy attendy;
    private boolean isKeyInAble = false;
    private String from_tab;
    private TextView tv_no_members;
    private boolean isTrue = false;
    private TextView txt_discipI_date;
    private String time;
    private String date, cutrrentDate;
    private ArrayList<NotificationData> newNudgeList, tempList;
    private int count = 0;


    public static Event_Fragment newInstance(String from_tab) {
        Bundle args = new Bundle();
        Event_Fragment fragment = new Event_Fragment();
        fragment.setArguments(args);
        args.putString(FROM_TAB, from_tab);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getString(FROM_TAB) != null) {
                from_tab = getArguments().getString(FROM_TAB);
            }
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_demo_event, container, false);
        //for status bar manage
        userInfo = SceneKey.sessionManager.getUserInfo();
        activity.setTopStatus();
        activity.showProgDialog(false, TAG);
        //TODO handling on grid adapter click if user is not key in
        map_view = view.findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);
        map_view.onResume();

        initView(view);

        floatBtn.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    if (eventDetails != null && eventDetails.getProfile_rating() != null) {
                        if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
                            try {
                                if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
                                    addUserIntoEvent(-1, null);
                                } else if (activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                                    addUserIntoEvent(-1, null);
                                } else cantJoinDialog();
                            } catch (ParseException d) {
                                d.getMessage();
                            }
                        }
                    }
                }
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView(View view) {
        txt_discipI_f2 = view.findViewById(R.id.txt_discipI_f2);
        info_view = view.findViewById(R.id.info_view);

        usercomeInRecyclerView = view.findViewById(R.id.usercomeInRecyclerView);

        txt_f2_badge = view.findViewById(R.id.txt_f2_badge);
        txt_discrp = view.findViewById(R.id.txt_discrp);

        scrl_all = view.findViewById(R.id.scrl_all);
        image_map = view.findViewById(R.id.image_map);
        rtlv_top = view.findViewById(R.id.rtlv_top);

        txt_hide_all_two = view.findViewById(R.id.txt_hide_all_two);
        demoView = view.findViewById(R.id.demoView);
        btn_got_it = view.findViewById(R.id.btn_got_it);

        // New Code
        img_f10_back = view.findViewById(R.id.img_f10_back);
        txt_event_name = view.findViewById(R.id.txt_event_name);
        img_infoget_f2 = view.findViewById(R.id.img_infoget_f2);
        img_notifnew = view.findViewById(R.id.img_notifnew);
        floatBtn = view.findViewById(R.id.floatBtn);
        fabMenu1_like = view.findViewById(R.id.fabMenu1_like);
        fabMenu2_picture = view.findViewById(R.id.fabMenu2_picture);
        fabMenu3_comment = view.findViewById(R.id.fabMenu3_comment);
        txt_hide_all = view.findViewById(R.id.txt_hide_all);
        fabMenu1_like.setTextView(new TextView[]{txt_hide_all});
        tv_no_members = view.findViewById(R.id.tv_no_members);
        txt_discipI_date = view.findViewById(R.id.txt_discipI_date);
        time = utility.getTimestamp("hh:mm aa");
        date = new SimpleDateFormat("dd-MMM-yyyy hh:mm", Locale.getDefault()).format(new Date());
        cutrrentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Log.i("date", date);

        activity.setBBVisibility(View.GONE, TAG);
        txt_discipI_date.setText(date + " " + time);


        //New Code Shubham
        try {
            attendy = new EventAttendy();
            attendy.userid = (userInfo.userid);
            attendy.userFacebookId = (userInfo.userFacebookId);
            attendy.setUserimage(userInfo.getUserImage());
            attendy.username = (userInfo.userName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        listViewFragProfile = view.findViewById(R.id.listViewFragProfile);

        feedsList = new ArrayList<>();
        tempList = new ArrayList<>();
        userList = new ArrayList<>();


       /* adapter = new Profile_Adapter(context, feedsList, myProfile,userList,eventId);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        listViewFragProfile.setLayoutManager(layoutManager);
        listViewFragProfile.setAdapter(adapter);*/

        updateUI();
        if (from_tab.equals("homeTab")) {
            getNotifyAllData(eventId);
        }
    }

    private void updateUI() {

        if(event.getVenue().getAddress()!= null){
            txt_discipI_f2.setText(event.getVenue().getAddress());
        }

        int distance = activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])});
        if (distance > Constant.MAXIMUM_DISTANCE) {
            activity.keyPointsUpdate();
        }
        mapAsyncer(latitude, longitude);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                activity.showProgDialog(false, TAG);
                getAllData();
            }
        });

        if (userInfo().firstTimeDemo) {
            demoView.setVisibility(View.VISIBLE);
            userInfo().firstTimeDemo = (false);
            activity.updateSession(userInfo());
        }

        if (timerHttp == null) setDataTimer();

        isInfoVisible = false;
        // rclv_grid.hasFixedSize();

        // New Code
        setOnClick(tv_no_members, img_f10_back, img_infoget_f2, img_notifnew, txt_hide_all, fabMenu1_like, fabMenu2_picture, fabMenu3_comment); //mainlayout

        cardsList = new ArrayList<>();
        info_view.setVisibility(View.GONE);

        awsImage = new AWSImage(activity);

        activity.dismissProgDialog();
        activity.hideStatusBar();

        //callAddEventApi() Api for trending tanb and map tab
        CustomeClick.getmInctance().setListner(new CustomeClick.ExploreSearchListener() {
            @Override
            public void onTextChange(UserInfo user) {
                if (from_tab.equals("trending") && !isKeyInAble) {
                    callAddEventApi(eventId, venueName, event, currentLatLng, new String[]{latitude.toString(), longitude.toString()});
                } else if (from_tab.equals("map_tab") && !isKeyInAble) {
                    callAddEventApi(eventId, venueName, event, currentLatLng, new String[]{latitude.toString(), longitude.toString()});
                }
            }
        });
    }

    private UserInfo userInfo() {
        return activity.userInfo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
        utility = new Utility(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setBBVisibility(View.GONE, TAG);
        // canCallWebservice = true;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getAllData();
        activity.setBBVisibility(View.GONE, TAG);
        activity.hideStatusBar();
        activity.dismissProgDialog();
        canCallWebservice = true;
        if (timerHttp == null) setDataTimer();
    }

    @Override
    public void onPause() {
        if (timerHttp != null) timerHttp.cancel();
        timerHttp = null;
        super.onPause();
    }


    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    private void animateInfo(boolean currentVisible) {
        if (!currentVisible) {

            Animation animation = AnimationUtils.loadAnimation(context,R.anim.viewslideup);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) info_view.getLayoutParams ();
            layoutParams.topMargin = 0;
            info_view.setLayoutParams ( layoutParams );

            info_view.startAnimation(animation);
            info_view.setVisibility(View.VISIBLE);
            isInfoVisible = true;
        } else {

            Animation animation = AnimationUtils.loadAnimation(context,R.anim.viewslidedown);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) info_view.getLayoutParams();
            layoutParams.height = 0;
            info_view.setLayoutParams(layoutParams);
            info_view.startAnimation(animation);
            info_view.setVisibility(View.GONE);
            isInfoVisible = false;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_f10_back:
                if (isInfoVisible) {

                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.viewslidedown);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) info_view.getLayoutParams();
                    layoutParams.height = 0;
                    info_view.setLayoutParams(layoutParams);
                    info_view.startAnimation(animation);
                    info_view.setVisibility(View.GONE);
                    isInfoVisible = false;


                } else {
                    activity.onBackPressed();
                }
                break;

            case R.id.img_infoget_f2:
                try {
                    animateInfo(isInfoVisible);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.fabMenu1_like:
                floatBtn.close(true);
                try {
                    if (userInfo().makeAdmin.equals(Constant.ADMIN_YES) && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        addUserIntoEvent(0, null);
                    } else if (activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
                            addUserIntoEvent(0, null);
                        } else likeEvent();
                    } else {
                        cantJoinDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.showToast(context, getResources().getString(R.string.somethingwentwrong), 0);
                }

                break;
            case R.id.fabMenu2_picture:
                floatBtn.close(true);
                try {
                    if (userInfo().makeAdmin.equals(Constant.ADMIN_YES) && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        captureImage();
                    } else if (activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        captureImage();
                    } else {
                        cantJoinDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.showToast(context, getResources().getString(R.string.somethingwentwrong), 0);
                }

                break;
            case R.id.fabMenu3_comment:
                floatBtn.close(true);
                try {
                    if (userInfo().makeAdmin.equals(Constant.ADMIN_YES) && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        canCallWebservice = false;
                        activity.addFragment(new Comment_Fragment().setData(currentLatLng, eventDetails.getProfile_rating().getKey_in(), eventId, eventDetails.getProfile_rating().getEvent_date(), eventDetails.getProfile_rating().getEvent_name(), this), 1);
                    } else if (activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
                        canCallWebservice = false;
                        activity.addFragment(new Comment_Fragment().setData(currentLatLng, eventDetails.getProfile_rating().getKey_in(), eventId, eventDetails.getProfile_rating().getEvent_date(), eventDetails.getProfile_rating().getEvent_name(), this), 1);
                    } else {
                        cantJoinDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.showToast(context, getResources().getString(R.string.somethingwentwrong), 0);
                }
                break;
            case R.id.txt_hide_all:
                floatBtn.close(true);
                break;

            case R.id.tv_no_members:
                Intent intent = new Intent(context, TheRoomActivity.class);
                intent.putExtra("noMemberYet", "No");
                startActivity(intent);
                break;

            case R.id.img_notifnew:
                if (noNotify > 0) {
                    notificationView(newNudgeList, noNotify, eventId);
                } else noNotification();
                break;

            case R.id.image_map:
                try {
                    if (eventDetails.getProfile_rating().getVenue_long() != null)
                        activity.addFragment(new SingleMap_Fragment().setData(eventDetails.getProfile_rating().getVenue_lat(), eventDetails.getProfile_rating().getVenue_long()), 1);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rtlv_top:
                break;

            case R.id.btn_got_it:
                demoView.setVisibility(View.GONE);
                break;
            case R.id.img_edit_i1:
                break;
        }
    }

    public void cantJoinDialog() {

        if (activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else if (activity.userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
            utility.showCustomPopup(getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else {
            utility.showCustomPopup(getString(R.string.enotat), String.valueOf(R.font.montserrat_medium));
        }
    }

    public boolean keyInEventCheck() {
        boolean a = false;
        Utility.e("Event fragment", currentLatLng[0] + " " + currentLatLng[1]);
        try {
            a = (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) | (activity.getDistance(new Double[]{latitude, longitude, Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }


    /***
     * For getting the nudge at notification popUp and show on it
     */
    private void getNudges() {
        canGetNotification = false;

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.GET_NUDGE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject nudgeJson = new JSONObject(response);
                        if (nudgeJson.has("success") && nudgeJson.getInt("success") == 0) {
                            Toast.makeText(getContext(), "No nudge available", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        NotificationData nudge = new NotificationData();
                        if (nudgeJson.has("nudges"))
                            nudge.nudges = ((nudgeJson.getString("nudges")));
                        if (nudgeJson.has("user_id"))
                            nudge.user_id = (nudgeJson.getString("user_id"));
                        if (nudgeJson.has("facebook_id"))
                            nudge.facebook_id = (nudgeJson.getString("facebook_id"));
                        if (nudgeJson.has("username"))
                            nudge.username = (nudgeJson.getString("username"));
                        if (nudgeJson.has("bio"))
                            nudge.bio = (nudgeJson.getString("bio"));
                        if (nudgeJson.has("userimage"))
                            nudge.userimage = (nudgeJson.getString("userimage"));
                        if (nudge.nudges.equals(Constant.NUDGE_YOUR)) nudge.message = false;
                        {
                            if (nudgeList == null) nudgeList = new ArrayList<>();
                            nudgeList.add(nudge);
                            noNotify -= 1;
                            setTextBadge();
                        }
                        if (!isPopUpShowing) popupNotification_New();
                        else {
                            currentNudge = nudgeList.size() - 1;
                            popup.updateData(nudgeList.get(currentNudge));
                        }
                        canGetNotification = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        canGetNotification = true;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    canGetNotification = true;
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", userInfo().userid);
                    params.put("event_id", eventId);
                    params.put("nudges_no", noNotify + "");
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }


    private void captureImage() {

        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_takephoto_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        TextView tv_camera, tv_cancel;

        tv_camera = dialog.findViewById(R.id.tv_camera);
        tv_cancel = dialog.findViewById(R.id.tv_cancel);

        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Permission permission = new Permission(activity);
                if (permission.checkCameraPermission()) callIntent(Constant.INTENT_CAMERA);
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void callIntent(int caseId) {

        switch (caseId) {
            case Constant.INTENT_CAMERA:
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    } else {
                        imageUri = Uri.fromFile(file);
                    }

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //  intent.putExtra("android.intent.extras.CAMERA_FACING", 1); //for front camera
                    startActivityForResult(intent, Constant.INTENT_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /****
     * This method is used when The user is not exist in the event to first time key in the user
     *
     * @param type must be 0 or 1
     */
    private void addUserIntoEvent(final int type, @Nullable final Bitmap bitmap) {
        if (type != -1) activity.showProgDialog(false, TAG);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.getInt("success") == 0) {
                            activity.incrementKeyPoints(getString(R.string.kp_keyin));
                        }
                        if (type == 0) likeEvent();
                        else if (type == 1) sendPicture(bitmap);
                        getAllData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
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

                    params.put("userid", userInfo().userid);
                    params.put("eventid", eventId);
                    params.put("eventname", eventDetails.getProfile_rating().getEvent_name());
                    Log.e("eventdate", event.getEvent().event_time);
//                    params.put("eventdate", eventDetails.getProfile_rating().getEvent_date());
                    params.put("eventdate", event.getEvent().event_time);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    /***
     * Event like volley
     */
    private void likeEvent() {
        activity.showProgDialog(false, TAG);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_LIKE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("success")) if (object.getInt("success") == 1) {
                            if (object.getString("msg").contains(" liked the event.")) {
                                fabMenu1_like.setImageDrawable(getResources().getDrawable(R.drawable.active_like));
                                //activity.showCustomPopup("You liked this event.", 1);
                                activity.incrementKeyPoints("");

                            } else if (object.getString("msg").contains("unliked the event.")) {
                                fabMenu1_like.setImageDrawable(getResources().getDrawable(R.drawable.heart));
                                //activity.showCustomPopup(getString(R.string.kp_unlike), 0);
                                activity.decrementKeyPoints(getString(R.string.kp_like));
                            }
                            getAllData();
                            //{"success":1,"msg":"your have liked the event."}
                            //{"success":1,"msg":"your have unliked the event."}
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utility.showToast(context, getResources().getString(R.string.somethingwentwrong), 0);
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

                    params.put("user_id", userInfo().userid);
                    params.put("event_id", eventId);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    /**
     * @param bitmap the bitmap return by the activity result
     */
    private void sendPicture(final Bitmap bitmap) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.EVENT_POST_PIC, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    // get response
                    Utility.e("Send picture response", response);
                    getAllData();
                    try {
                        JSONObject respo = new JSONObject(response);
                        if (respo.getInt("success") == 0) {
                            Utility.showToast(context, respo.getString("msg"), 0);
                            //activity.showCustomPopup("Photo has been posted successfully.", 1);
                            activity.decrementKeyPoints("");
                        } else {
//                            activity.showCustomPopup("Photo has been posted successfully.", 1);
                            activity.incrementKeyPoints("");
                        }
                    } catch (Exception e) {
                        //activity.incrementKeyPoints(getString(R.string.kp_img_post));
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    getAllData();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("user_id", userInfo().userid);
                    params.put("event_id", eventId);
                    params.put("location", getLocation()); //TODO location
                    params.put("image", ImageUtil.encodeTobase64(bitmap));
                    params.put("ratingtime", activity.getCurrentTimeInFormat());

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    private String getLocation() {
        String result;
        if (userInfo().address.length() > 1) {
            result = userInfo().address;
        } else {
            result = activity.getAddress(Double.parseDouble(currentLatLng[0]), Double.parseDouble(currentLatLng[1]));
        }
        return result;
    }

    /**
     * The dialogue use to show if user is not in the range of the event and evneet is not started yet
     */

    /**
     * @param response the responce provided by getAlldata()
     * @throws JSONException
     */
    private void getResponse(String response) throws Exception {
        JSONObject obj1 = new JSONObject(response);
        activity.dismissProgDialog();
        if (eventDetails == null) eventDetails = new EventDetails();
        try {
            if (obj1.has("eventattendy")) {
                // eventDetails.setAttendyJson(obj1.getJSONArray("eventattendy"), this);
                Object objectType = obj1.get("eventattendy");

                if (objectType instanceof String) {
                    if (eventDetails.getAttendyList() == null) {
                        tv_no_members.setVisibility(View.VISIBLE);
                        usercomeInRecyclerView.setVisibility(View.GONE);
                    }

                } else if (objectType instanceof JSONArray) {
                    eventDetails.setAttendyJson(obj1.getJSONArray("eventattendy"), this);
                    tv_no_members.setVisibility(View.GONE);
                    usercomeInRecyclerView.setVisibility(View.VISIBLE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("nudges_count"))
                eventDetails.setNudges_count(obj1.getString("nudges_count"));
            noNotify = Integer.parseInt(eventDetails.getNudges_count());
            setTextBadge();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("allTags")) {
                eventDetails.setTagList(obj1.getJSONArray("allTags"), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("allfeeds")) {
                //  eventDetails.setFeedsJson(obj1.getJSONArray("allfeeds"), this);
                Object objectType = obj1.get("allfeeds");

                if (objectType instanceof String) {

                    if (eventDetails.getFeedList() == null) {
                        if (feedsList.size() == 0) {
                            if (obj1.has("event_profile_rating")) {
                                JSONObject obj = obj1.getJSONObject("event_profile_rating");

                                Feeds feeds = new Feeds();
                                String event_name = obj.getString("event_name");
                                String[] event_name_array = event_name.split("@");
                                feeds.username = event_name_array[1];

                                feeds.userimage = event.getVenue().getImage();
                                feeds.type = Constant.FEED_TYPE_COMMENT;

                                if (from_tab.equals("event_tab")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + activity.userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                        floatBtn.open(true);
                                        activity.incrementKeyPoints("");
                                        getAllData();
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    }

                                } else if (from_tab.equals("trending")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + activity.userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                    }
                                } else if (from_tab.equals("map_tab")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + activity.userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                    }
                                } else if (from_tab.equals("homeTab")) {
                                    if (isKeyInAble) {
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    } else {
                                        feeds.feed = "Hi " + activity.userInfo().fullname + "! Come join the fun here at " + venueName + ". You must be here to connect!";
                                        getAllData();
                                        feeds.feed = "Welcome to " + venueName + "! Join the fun! Share your pics & comments right here!";
                                    }
                                }
                                feedsList.add(feeds);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else if (objectType instanceof JSONArray) {
                    feedsList.clear();
                    eventDetails.setFeedsJson(obj1.getJSONArray("allfeeds"), this);
                    feedsList.addAll(eventDetails.getFeedList());
                    adapter.notifyDataSetChanged();
                    Log.e("FEEDS LIST SIZE", eventDetails.getFeedList().size() + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (obj1.has("event_profile_rating")) {
                eventDetails.setProfile_ratingJSon(obj1.getJSONObject("event_profile_rating"), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("userInfo")) {

                Object intervention = obj1.get("userInfo");
                if (intervention instanceof JSONArray) {
                    SceneKey.sessionManager.logout(activity);
                }
                JSONObject user = obj1.getJSONObject("userInfo");
                if (user.has("makeAdmin")) {
                    userInfo().makeAdmin = (user.getString("makeAdmin"));

                }
                if (user.has("lat")) userInfo().lat = (user.getString("lat"));
                if (user.has("longi")) userInfo().longi = (user.getString("longi"));

                /*if (user.has("adminLat")) userInfo().latitude = (user.getString("adminLat"));
                if (user.has("adminLong")) userInfo().longitude = (user.getString("adminLong"));*/

                if (user.getString("adminLat").isEmpty()) {
                    userInfo.adminLat = user.getString("lat");
                    userInfo.adminLong = user.getString("longi");
                    userInfo.currentLocation = true;
                } else {
                    userInfo.adminLat = user.getString("adminLat");
                    userInfo.adminLong = user.getString("adminLong");
                    userInfo.currentLocation = false;
                }
                if (user.has("address")) userInfo().address = (user.getString("address"));
                activity.updateSession(userInfo());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (obj1.has("nudgeList")) {
                Object objectType = obj1.get("nudgeList");

                if (objectType instanceof String) {
                    if (eventDetails.getNudgeList() == null) {
                       /* tv_no_members.setVisibility(View.GONE);
                        usercomeInRecyclerView.setVisibility(View.VISIBLE);*/
                    }
                } else if (objectType instanceof JSONArray) {
                    eventDetails.setNudgeJson(obj1.getJSONArray("nudgeList"), this);
                   /* tv_no_members.setVisibility(View.GONE);
                    usercomeInRecyclerView.setVisibility(View.VISIBLE);*/
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        int height = (int) getResources().getDimension(R.dimen._125sdp);//activity().ActivityWidth;
        int width = activity.ActivityWidth;
        String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=12&size=" + width + "x" + height + "&sensor=false";
        Utility.e(TAG, "URL" + url + "Lat lin" + latitude + " : " + longitude);

        try {
            Picasso.with(activity).load(url).into(image_map);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (cardsList.size() <= 0) {
            Card card = new Card();
            card.imageUrl = null;
            card.text = "Welcome to the " + venueName + "! Join the fun! Share your pics & comments right here!";
            cardsList.add(card);
        }
    }

    private void mapAsyncer(final double lat, final double lng) {
        map_view.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                Marker m = null;
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //callPermission(Constants.TYPE_PERMISSION_FINE_LOCATION);
                        } else if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //callPermission(Constants.TYPE_PERMISSION_CORAS_LOCATION);
                        }
                    }
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(lat, lng);


                final Marker mr = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .anchor(0.5f, 0.5f)
                        .title(event.getVenue()!=null?event.getVenue().getVenue_name():"")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                mr.showInfoWindow();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (eventDetails.getProfile_rating().getVenue_long() != null)
                            activity.addFragment(new SingleMap_Fragment().setData(eventDetails.getProfile_rating().getVenue_lat(), eventDetails.getProfile_rating().getVenue_long()), 1);
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if (eventDetails.getProfile_rating().getVenue_long() != null)
                            activity.addFragment(new SingleMap_Fragment().setData(eventDetails.getProfile_rating().getVenue_lat(), eventDetails.getProfile_rating().getVenue_long()), 1);
                        marker.showInfoWindow();

                        return true;
                    }
                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (eventDetails.getProfile_rating().getVenue_long() != null)
                            activity.addFragment(new SingleMap_Fragment().setData(eventDetails.getProfile_rating().getVenue_lat(), eventDetails.getProfile_rating().getVenue_long()), 1);
                        mr.showInfoWindow();

                    }
                });//TODO check with iphone

                Handler handler = new Handler();
                final Marker finalM = m;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalM != null) finalM.showInfoWindow();
                    }
                }, 2000);


            }
        });

    }

    private boolean checkWithTime_No_Attendy(final String date, Double interval) throws ParseException {

        return true; //TODO change time check
       /* String[] dateSplit = (date.replace("TO", "T")).replace(" ", "T").split("T");
        Date startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(dateSplit[0] + " " + dateSplit[1]);
        Date endTime = new Date(startTime.getTime()+(int)(interval* 60 * 60 * 1000));
        Util.printLog(TAG, " Date "+startTime+" : "+endTime);
        long currentTime = java.util.Calendar.getInstance().getTime().getTime();
        if (currentTime > startTime.getTime()) {
            return true;
        }
        return false;*/
    }

    /**
     * text badge count from 15 to 0 sec.
     */
    private void setTextBadge() {

        if (noNotify != 0) {
            txt_f2_badge.setVisibility(View.VISIBLE);
            txt_f2_badge.setText(noNotify + "");
        }else{
            txt_f2_badge.setVisibility(View.GONE);
        }

        if (noNotify > 0) {
            txt_f2_badge.setBackground(getResources().getDrawable(R.drawable.bg_circle_red_badge));
            if (noNotify > 99) txt_f2_badge.setText("99+");
            txt_f2_badge.setVisibility(View.VISIBLE);
        } else {
            img_notifnew.setImageResource(R.drawable.notification_icon);
            txt_f2_badge.setVisibility(View.GONE);
        }
    }

    private void setDataTimer() {
        if (timerHttp == null) timerHttp = new Timer();

        //Set the schedule function and rate
        //TODO timer changed as required
        timerHttp.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {
                                              activity.runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      Utility.e(TAG, "TimerVolley Event fragment");
                                                      try {
                                                          if (canCallWebservice) getAllData();
                                                      } catch (Exception e) {
                                                          e.printStackTrace();
                                                      }
                                                  }
                                              });
                                          }
                                      },
                //Set how long before to start calling the TimerTask (in milliseconds)
                60000,
                //Set the amount of time between each execution (in milliseconds)
                60000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == Constant.INTENT_CAMERA) {

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160, 160).setMaxCropResultSize(4000, 3500).setAspectRatio(400, 300).start(context, this);

                } else {
                    Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null) {

                        Feeds feeds = new Feeds();
                        feeds.type = Constant.FEED_TYPE_PICTURE;
                        feeds.date = cutrrentDate;
                        feeds.user_status = userInfo.user_status;
                        feeds.userimage = userInfo.getUserImage();
                        feeds.username = userInfo.userName;
                        feeds.feed = result.getUri().toString();
                        feeds.isUri = true;
                        feedsList.add(0, feeds);
                        adapter.notifyItemInserted(0);
                        listViewFragProfile.scrollToPosition(0);

                        Bitmap eventImg = MediaStore.Images.Media.getBitmap(context.getContentResolver(), result.getUri());
                        if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST))
                            addUserIntoEvent(1, eventImg);
                        else sendPicture(eventImg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2 && data.hasExtra("incresePoint")) {

                if (data.hasExtra("incresePoint")) {

                    String requiredValue = data.getStringExtra("incresePoint");
                    if (requiredValue.equals("1")) {
                        activity.incrementKeyPoints("");
                    }

                } else {
                    tempList = (ArrayList<NotificationData>) data.getSerializableExtra("nudgeDataList");
                    newNudgeList.clear();
                    newNudgeList.addAll(tempList);//list
                    count = newNudgeList.size();
                    noNotify = count;
                    if(count!= 0){
                        txt_f2_badge.setText("" + count);
                    }else{
                        txt_f2_badge.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Utility.showToast(context, "permission denied by user ", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (timerHttp != null) timerHttp.cancel();
        if (timerNudge != null) timerNudge.cancel();
        timerHttp = null;
        timerNudge = null;

        for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
            try {
                ((Trending_Fragment) fragment).getTrendingData();
                activity.setTitle(getString(R.string.trending));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ((Map_Fragment) fragment).checkEventAvailability();
                activity.setTitle(getString(R.string.map));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((Event_Search_Tag_Fragment) fragment).setVisibility();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }

          /*  try {
                ((NearEvent_Fragment) fragment).eventApiRefresh();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        VolleySingleton.getInstance(context).cancelPendingRequests(TAG);
        activity.showStatusBar();
        //  handler.removeCallbacksAndMessages(null);
        activity.setBBVisibility(View.VISIBLE, 300, TAG);
        super.onDestroyView();
    }

    @Override
    public boolean onStatusBarHide() {
        return false;
    }

    /* common methods used somewhere else  */

    public void addChips(ArrayList<Tags> tag) {
        try {
            Grid_multiRow layout = this.getView().findViewById(R.id.chip_linear);
            layout.setAdapter(new GridChipsAdapter(context, tag));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Event_Fragment setData(String eventId, String venueName, Events event, String[] currentLatLng, String[] venuLatLng, boolean isKeyInAble) {
        this.eventId = eventId;
        this.venueName = venueName;
        this.event = event;
        this.currentLatLng = currentLatLng;
        latitude = Double.valueOf(venuLatLng[0]);
        longitude = Double.valueOf(venuLatLng[1]);
        this.isKeyInAble = isKeyInAble;
        return this;
    }

    /**
     * GetALl the data for that event
     */
    public void getAllData() {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LISTEVENTFEED, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();
                    Log.e("Responce129", response);
                    // get response
                    try {
                        if (response != null) getResponse(response);
                        else Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
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

                    params.put("event_id", eventId);
                    params.put("user_id", userInfo().userid);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };

            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    public void getNotifyAllData(final String eventId) {
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LISTEVENTFEED, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("Responce129", response);
                    // get response
                    try {
                        if (response != null) getResponse(response);
                        else Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.dismissProgDialog();
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

                    params.put("event_id", eventId);
                    params.put("user_id", userInfo().userid);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };

            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    /***
     * For setting the Grid Layout of room Persons showing at bottom of the Room
     *
     * @param list
     */
    public void setRecyclerView(ArrayList<EventAttendy> list) {
        userList.clear();
        userList.addAll(list);
        adapter.notifyDataSetChanged();
        //this.userList  = list;
        UserInAdapter adapter = new UserInAdapter(list, context, eventId);
        usercomeInRecyclerView.setAdapter(adapter);
    }

//  fragment.addNudge(obj.userid, obj.userFacebookId , StringEscapeUtils.escapeJava(s).replace(" +",""),profilePopUp);

    public void addNudge(final String attendyId, final String attendyFBID, final String nudge, final Dialog profilePop) {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_NUDGE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.dismissProgDialog();

                    activity.showCustomPopup(getResources().getString(R.string.goodNudge), 1);

                    if (dialog != null) dialog.dismiss();
                    if (profilePop != null) profilePop.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    if (dialog != null) dialog.dismiss();
                    Utility.showToast(getContext(), getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("event_id", eventId);
                    params.put("nudges_to", attendyId);
                    params.put("nudges_by", userInfo().userid);
                    params.put("facebook_id", attendyFBID);
                    params.put("nudges", nudge);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(usercomeInRecyclerView, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    /**
     * Tost shown at popup of user.
     */
    public void cantInteract() {
        utility.showCustomPopup(getString(R.string.sorryEvent), String.valueOf(R.font.montserrat_medium));
    }

    public boolean check() throws ParseException {
        boolean result;
        if (userInfo().makeAdmin.equals(Constant.ADMIN_YES)) {
            result = true;
        } else if (activity.getDistance(new Double[]{latitude, longitude, Double.parseDouble(currentLatLng[0]), Double.parseDouble(currentLatLng[1])}) <= Constant.MAXIMUM_DISTANCE && activity.checkWithTime(eventDetails.getProfile_rating().getEvent_date(), Double.parseDouble(eventDetails.getProfile_rating().getInterval()))) {
            if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
                result = false;//addUserIntoEvent(0, null);
            } else result = true;
        } else {
            result = false;
        }
        if (eventDetails.getProfile_rating().getKey_in().equals(Constant.KEY_NOTEXIST)) {
            result = false;//addUserIntoEvent(0, null);
        }

        return result;
    }

    public void noNotification() {
        utility.showCustomPopup(getString(R.string.noNotification), String.valueOf(R.font.montserrat_medium));
    }


    public void popupNotification_New() {
        activity.showProgDialog(false, TAG);
        NotificationData nudge = nudgeList.get(nudgeList.size() - 1);
        awsImage.downloadFileFromS3(awsImage.getFacebookId(nudge.facebook_id, nudge.user_id), (credentialsProvider == null ? credentialsProvider = awsImage.getCredentials() : credentialsProvider));
        currentNudge = 0;
        isPopUpShowing = true;
        popup = new ProfilePopUp_Notification(activity, awsImage, 4, nudge) {
            @Override
            public void onClickView(TextView textView, ProfilePopUp_Notification profilePopUp) {
                profilePopUp.setText(textView.getText().toString());
            }

            @Override
            public void onSendCLick(TextView textView, ProfilePopUp_Notification profilePopUp, NotificationData obj) {
                Log.e("Value ", profilePopUp.list.toString());
                String s = profilePopUp.list.toString();
                byte[] ptext = (s = s.substring(1, s.length() - 1).replace("", "")).getBytes();

                addNudge(obj.user_id, obj.facebook_id, StringEscapeUtils.escapeJava(s).replace(" +", ""), profilePopUp);
            }

            @Override
            public void onPrevClick(ImageView textView, ProfilePopUp_Notification profilePopUp) {
                if (currentNudge > 0) {
                    currentNudge -= 1;
                    updateData(nudgeList.get(currentNudge));
                } else {
                    Toast.makeText(getContext(), "No nudge available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNextClick(ImageView textView, ProfilePopUp_Notification profilePopUp) {
                if (currentNudge == (nudgeList.size() - 1) && canGetNotification) {
                    getNudges();
                } else {
                    currentNudge += 1;
                    updateData(nudgeList.get(currentNudge));
                }
            }

            @Override
            public void onDismiss(ProfilePopUp_Notification profilePopUp) {
                nudgeList.clear();
                isPopUpShowing = false;
            }
        };
        popup.show();
    }

    private void notificationView(ArrayList<NotificationData> nudgeList, int noNotify, String eventId) {
        activity.showProgDialog(false, TAG);
        Intent intent = new Intent(getContext(), ForNotificactionActivity.class);
        intent.putExtra("nudgeList", nudgeList);
        intent.putExtra("noNotify", noNotify);
        intent.putExtra("eventId", eventId);
//        startActivity(intent);
        startActivityForResult(intent, 2);
    }

    private void callAddEventApi(final String event_id, final String venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(context);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.ADD_EVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        activity.dismissProgDialog();
                        jsonObject = new JSONObject(Response);
                        String status = jsonObject.getString("status");

                        if (status.equals("event_Added")) {
                            isKeyInAble = true;
                            feedsList.clear();
                            getAllData();
                            adapter.notifyDataSetChanged();
                        } else if (status.equals("exist")) {
                            isKeyInAble = true;
                            feedsList.clear();
                            getAllData();
                            adapter.notifyDataSetChanged();
                        }

                    } catch (Exception ex) {
                        activity.dismissProgDialog();
                        ex.printStackTrace();
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
                    params.put("userid", activity.userInfo().userid);
                    params.put("eventname", object.getEvent().event_name);
                    params.put("eventid", object.getEvent().event_id);
//                    params.put("Eventdate", object.getEvent().event_date);
                    params.put("eventdate", object.getEvent().event_time);
                    return params;
                }
            };

            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }

    public void setNudgeListinMethod(ArrayList<NotificationData> nudgeModalArrayList) {

        newNudgeList = nudgeModalArrayList;
    }
}
