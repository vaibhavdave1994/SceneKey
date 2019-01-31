package com.scenekey.activity;

import android.Manifest;
import android.app.Dialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scenekey.BuildConfig;
import com.scenekey.R;
import com.scenekey.adapter.ComeInUSerAdapter;
import com.scenekey.adapter.CustomListAdapter;
import com.scenekey.adapter.DemoTagAdapter;
import com.scenekey.aws_service.Aws_Web_Service;
import com.scenekey.cropper.CropImage;
import com.scenekey.cropper.CropImageView;
import com.scenekey.fragment.SingleMap_Fragment;
import com.scenekey.helper.Constant;
import com.scenekey.helper.Permission;
import com.scenekey.lib_sources.Floting_menuAction.FloatingActionButton;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.OnSwipeTouchListener;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TryAndDemoActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    //,
    public static CustomListAdapter customListAdapter;
    public static ArrayList<Card> arrayList;
    private static UserInfo userInfo;
    public final String TAG = TryAndDemoActivity.class.toString();
    public boolean isInfoVisible;
    public Double latitude, longitude;
    private TextView txt_f2_badge,
            txt_hide_all;
    private LinearLayout info_view;
    private ImageView img_f10_back, img_demo_notif;
    private ImageView img_infoget_f2;
    private RecyclerView listViewFragProfile;
    private boolean isFABOpen;
    private GoogleMap gmap;
    private ArrayList<Card> notificationUserList, notificationUserList5;
    private int notificationValue = 5;
    private boolean isTrue, islike = false;
    private RecyclerView comeInProfileRView;
    private ComeInUSerAdapter comeInUSerAdapter;
    private FloatingActionButton fab_like, fab_picture, fab_comment;
    private com.scenekey.lib_sources.Floting_menuAction.FloatingActionMenu_TryDemo floatBtn_demo;
    private Uri imageUri;
    private DemoTagAdapter demoTagAdapter;
    private ArrayList<String> taglist;
    private RecyclerView tagRecyclerView;
    private ImageView iv_add_ico;
    private Utility utility;
    private String time;
    private String date;
    private TextView txt_discipI_f2;
    private int count;
    private int message = 0;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_and_demo_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        inItView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void inItView() {

        userInfo = SceneKey.sessionManager.getUserInfo();
        utility = new Utility(this);

        isInfoVisible = false;
        fab_like = findViewById(R.id.fab_like);
        fab_picture = findViewById(R.id.fab_picture);
        fab_comment = findViewById(R.id.fab_comment);
        txt_discipI_f2 = findViewById(R.id.txt_discipI_f2);


        time = utility.getTimestamp("hh:mm aa");
        date = new SimpleDateFormat("MMM dd, yyyy hh:mm", Locale.getDefault()).format(new Date());
        Log.i("date", date);


        notificationUserList = new ArrayList<>();
        notificationUserList5 = new ArrayList<>();
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            getDemoData2();
        }
        getDemoNotificaionData();
        getDemoNotificaionData5();

        info_view = findViewById(R.id.info_view);
        txt_f2_badge = findViewById(R.id.txt_f2_badge);
        iv_add_ico = findViewById(R.id.iv_add_ico);

        // New Code
        img_f10_back = findViewById(R.id.img_f10_back);
        img_infoget_f2 = findViewById(R.id.img_infoget_f2);
        img_demo_notif = findViewById(R.id.img_demo_notif);
        txt_hide_all = findViewById(R.id.txt_hide_all);

        fab_like.setTextView(new TextView[]{txt_hide_all});
        listViewFragProfile = findViewById(R.id.listViewFragProfile);
        floatBtn_demo = findViewById(R.id.floatBtn_demo);

        //tagrecyclerView
        tagRecyclerView = findViewById(R.id.tagRecyclerView);

        customListAdapter = new CustomListAdapter(this, arrayList);
        listViewFragProfile.setAdapter(customListAdapter);

        //Tags Adapter
        taglist = new ArrayList<>();
        taglist.add("Vegas");
        taglist.add("Musuic");
        taglist.add("Festival");
        taglist.add("Dancing");
        taglist.add("Mega");
        taglist.add("Live");

        demoTagAdapter = new DemoTagAdapter(taglist, this);
        tagRecyclerView.setLayoutManager(new GridLayoutManager(TryAndDemoActivity.this, 3));
        tagRecyclerView.setAdapter(demoTagAdapter);

        comeInProfileRView = findViewById(R.id.comeInProfileRView);
        comeInUSerAdapter = new ComeInUSerAdapter(this, notificationUserList, userInfo.userName);
        comeInProfileRView.setAdapter(comeInUSerAdapter);

        setOnClick(img_f10_back, img_demo_notif, img_infoget_f2, txt_hide_all);

        txt_discipI_f2.setText(date);
        fab_like.setImageResource(R.drawable.heart_ico);
        fab_picture.setImageResource(R.drawable.picture_ico);
        fab_comment.setImageResource(R.drawable.comment_ico);

        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (islike) {
                    fab_like.setImageResource(R.drawable.active_like);
                    showKeyPoints("+1");
                    islike = false;
                } else {
                    fab_like.setImageResource(R.drawable.heart_ico);
                    showKeyPoints("-1");
                    islike = true;
                }

                floatBtn_demo.close(true);
            }
        });

        fab_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                customListAdapter.notifyDataSetChanged();
                floatBtn_demo.close(true);
            }
        });

        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TryAndDemoActivity.this, DemoCommentActivity.class);
                startActivityForResult(intent, 3);
                floatBtn_demo.close(true);
            }
        });


        if (notificationValue != 0) {
            txt_f2_badge.setVisibility(View.VISIBLE);
            txt_f2_badge.setText("" + notificationValue);
        } else {
            txt_f2_badge.setVisibility(View.GONE);
        }

    }

    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    /*private void showKeyPoints(String s) {
        final Dialog dialog = new Dialog(this);
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
        }, 2000);

        dialog.show();
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_f10_back:

              /*  if (!isTrue) {
                    info_view.setVisibility(View.GONE);
                    isTrue = true;
                } else {
                    onBackPressed();
                }*/

                if (isInfoVisible) {

                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.viewslidedown);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) info_view.getLayoutParams();
                    layoutParams.height = 0;
                    info_view.setLayoutParams(layoutParams);
                    info_view.startAnimation(animation);
                    info_view.setVisibility(View.GONE);
                    isInfoVisible = false;


                } else {
                    onBackPressed();
                }

                break;

            case R.id.txt_hide_all:
                floatBtn_demo.close(true);
                txt_hide_all.setVisibility(View.GONE);
                break;

            case R.id.img_demo_notif:

                if (notificationValue > 0) {
                    txt_f2_badge.setText("" + notificationValue);

                    if (message == 4) {
                        count = 1;
                    } else if (message == 3) {
                        count = 2;
                    } else if (message == 2) {
                        count = 3;
                    } else if (message == 1) {
                        count = 4;
                    }

                    Intent intent1 = new Intent(TryAndDemoActivity.this, NotificationDemoActivity.class);
                    intent1.putExtra("fromNotification", count);
                    intent1.putExtra("fromNotificationList", notificationUserList5);
                    startActivityForResult(intent1, 2);
                } else {
                    utility.showCustomPopup(getString(R.string.no_notification_at_this_time), String.valueOf(R.font.montserrat_medium));
                }


                break;

            case R.id.img_infoget_f2:
               /* if (isTrue) {
                    info_view.setVisibility(View.VISIBLE);
                    isTrue = false;
                } else {
                    info_view.setVisibility(View.GONE);
                    isTrue = true;
                }
*/

                try {
                    animateInfo(isInfoVisible);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private void animateInfo(boolean currentVisible) {
        if (!currentVisible) {

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.viewslideup);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) info_view.getLayoutParams();
            layoutParams.topMargin = 0;
            info_view.setLayoutParams(layoutParams);

            info_view.startAnimation(animation);
            info_view.setVisibility(View.VISIBLE);
            isInfoVisible = true;
        } else {

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.viewslidedown);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) info_view.getLayoutParams();
            layoutParams.height = 0;
            info_view.setLayoutParams(layoutParams);
            info_view.startAnimation(animation);
            info_view.setVisibility(View.GONE);
            isInfoVisible = false;
        }
    }


    private void captureImage() {

        final Dialog dialog = new Dialog(this);
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
                Permission permission = new Permission(TryAndDemoActivity.this);
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
                        imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                String requiredValue = data.getStringExtra("incresePoint");
                if (requiredValue.equals("1")) {
                    showKeyPoints("+1");
                }
            }
        }


        if (resultCode == -1) {

            if (requestCode == Constant.INTENT_CAMERA) {

                if (imageUri != null) {
                    CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE).setMinCropResultSize(160, 160).setMaxCropResultSize(4000, 3500).setAspectRatio(400, 300).start(this);

                } else {
                    Utility.showToast(TryAndDemoActivity.this, getString(R.string.somethingwentwrong), 0);
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    if (result != null) {
                        Bitmap eventImg = MediaStore.Images.Media.getBitmap(TryAndDemoActivity.this.getContentResolver(), result.getUri());
                        Card cardIv = new Card();
                        cardIv.name = userInfo.userName;
                        //cardIv.imageint = R.drawable.placeholder_img;
                        cardIv.uri = result.getUri();
                        cardIv.date = time;
                        cardIv.facebookId = userInfo.userFacebookId;
                        cardIv.userId = userInfo.userid;
                        arrayList.add(0, cardIv);
                        customListAdapter.notifyItemInserted(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 2) {
            message = data.getIntExtra("Count", 0);
            if (data.getStringExtra("yes") != null) {
                String showPoint = data.getStringExtra("yes");
                if (showPoint.equals("yes")) {
                    showKeyPoints("+1");
                }
            }

            notificationValue = message;

            if (message != 0) {
                txt_f2_badge.setVisibility(View.VISIBLE);
                txt_f2_badge.setText("" + (message));
            } else {
                txt_f2_badge.setVisibility(View.GONE);
            }
            notificationValue = message;
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
        final Dialog dialog = new Dialog(this);
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

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();
        try {
            //Picasso.with(this).load(userInfo.getUserImage()).placeholder(R.drawable.image_default_profile).into(img_profile);
            //userInfo.key_points
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        customListAdapter.notifyDataSetChanged();
    }

    private void getDemoData2() {

        Card card1 = new Card();
        card1.name = "Amy";
        card1.imageint = R.drawable.room_3;
        card1.uploadImage = R.drawable.demo_7;
        card1.user_status = "3";
        card1.date = "12:00 AM";
        card1.type = "room";
        arrayList.add(card1);

        Card card2 = new Card();
        card2.name = "Alexander";
        card2.uploadImage = R.drawable.demo_1;
        card2.imageint = R.drawable.room_1;
        card2.user_status = "1";
        card2.date = "12:00 AM";
        card2.type = "room";
        arrayList.add(card2);

//        yyyy-MM-dd HH:mm:ss


        Card card3 = new Card();
        card3.name = "Alizee";
        card3.imageint = R.drawable.room_2;
        card3.text = getResources().getString(R.string.musicisamezing);
        card3.user_status = "2";
        card3.date = "11:59 PM";
        card3.type = "room";
        arrayList.add(card3);


        Card card4 = new Card();
        card4.name = "Dhan-mein";
        card4.text = getResources().getString(R.string.lineheriswaylong);
        card4.imageint = R.drawable.room_4;
        card4.user_status = "2";
        card4.date = "11:59 PM";
        card4.type = "room";
        arrayList.add(card4);

        Card card5 = new Card();
        card5.name = "Darrin";
        card5.uploadImage = R.drawable.demo_3;
        card5.imageint = R.drawable.room_5;
        card5.date = "11:58 PM";
        card5.user_status = "3";
        card5.type = "room";
        arrayList.add(card5);

        Card card6 = new Card();
        card6.name = "Erin";
        card6.imageint = R.drawable.room_6;
        card6.uploadImage = R.drawable.demo_4;
        card6.user_status = "1";
        card6.date = "11:57 PM";
        card6.type = "room";
        arrayList.add(card6);

        Card card7 = new Card();
        card7.name = "James";
        card7.text = getResources().getString(R.string.musicisamezing);
        card7.imageint = R.drawable.room_7;
        card7.date = "11:56 PM";
        card7.user_status = "2";
        card7.type = "room";
        arrayList.add(card7);

        Card card8 = new Card();
        card8.name = "Morgan";
        card8.imageint = R.drawable.room_8;
        card8.uploadImage = R.drawable.demo_5;
        card8.date = "11:55 PM";
        card8.user_status = "3";
        card8.type = "room";
        arrayList.add(card8);

        Card card9 = new Card();
        card9.name = "Alexander";
        card9.text = getResources().getString(R.string.lineheriswaylong);
        card9.imageint = R.drawable.room_1;
        card9.date = "11:55 AM";
        card8.user_status = "1";
        card9.type = "room";
        arrayList.add(card9);

        Card card10 = new Card();
        card10.name = "Alizee";
        card10.imageint = R.drawable.room_2;
        card10.uploadImage = R.drawable.demo_6;
        card10.date = "11:55 PM";
        card10.user_status = "3";
        card10.type = "room";
        arrayList.add(card10);

        Card card11 = new Card();
        card11.name = "Amy";
        card11.imageint = R.drawable.room_3;
        card11.uploadImage = R.drawable.demo_2;
        card11.date = "11:54 AM";
        card11.user_status = "3";
        card11.type = "room";
        arrayList.add(card11);

        Card card12 = new Card();
        card12.name = "Dhan-mein";
        card12.imageint = R.drawable.room_4;
        card12.text = getResources().getString(R.string.twoforonemozi);
        card12.date = "11:53 PM";
        card12.user_status = "1";
        card12.type = "room";
        arrayList.add(card12);

        Card card13 = new Card();
        card13.name = "Darrin";
        card13.text = getResources().getString(R.string.musicisamezing);
        card13.imageint = R.drawable.room_5;
        card13.date = "11:52 PM";
        card13.user_status = "1";
        card13.type = "room";
        arrayList.add(card13);

        Card card14 = new Card();
        card14.name = "Erin";
        card14.imageint = R.drawable.room_6;
        card14.uploadImage = R.drawable.demo_8;
        card14.date = "11:51 PM";
        card14.user_status = "3";
        card14.type = "room";
        arrayList.add(card14);

    }

    private void getDemoNotificaionData() {

        Card card1 = new Card();
        card1.name = "Alexander";
        card1.text = getResources().getString(R.string.i_have_best);
        card1.imageint = R.drawable.room_1;
        card1.date = "12:00 AM";
        card1.user_status = "1";
        card1.type = "notify";
        notificationUserList.add(card1);

        Card card5 = new Card();
        card5.name = "Alizee";
        card5.imageint = R.drawable.room_2;
        card5.uploadImage = R.drawable.demo_3;
        card5.user_status = "2";
        card5.date = "11:59 PM";
        card5.type = "notify";
        notificationUserList.add(card5);

        Card card3 = new Card();
        card3.name = "Amy";
        card3.imageint = R.drawable.room_3;
        card3.uploadImage = R.drawable.demo_1;
        card3.date = "12:00 AM";
        card3.user_status = "3";
        card3.type = "notify";
        notificationUserList.add(card3);

        Card card6 = new Card();
        card6.name = "Dhan-mein";
        card6.imageint = R.drawable.room_4;
        card6.uploadImage = R.drawable.demo_5;
        card6.date = "11:59 PM";
        card6.user_status = "2";
        card6.type = "notify";
        notificationUserList.add(card6);

        Card card7 = new Card();
        card7.name = "Darrin";
        card7.text = getResources().getString(R.string.omg__smooth);
        card7.imageint = R.drawable.room_5;
        card7.date = "11:58 PM";
        card7.user_status = "3";
        card7.type = "notify";
        notificationUserList.add(card7);

        Card card13 = new Card();
        card13.name = "Erin";
        card13.imageint = R.drawable.room_6;
        card13.uploadImage = R.drawable.demo_3;
        card13.date = "11:51 PM";
        card13.user_status = "1";
        card13.type = "notify";
        notificationUserList.add(card13);

        Card card9 = new Card();
        card9.name = "James";
        card9.text = getResources().getString(R.string.i_have_best);
        card9.imageint = R.drawable.room_7;
        card9.date = "11:56 PM";
        card9.user_status = "1";
        card9.type = "notify";
        notificationUserList.add(card9);

        Card card10 = new Card();
        card10.name = "Morgan";
        card10.imageint = R.drawable.room_8;
        card10.uploadImage = R.drawable.demo_3;
        card10.date = "11:56 PM";
        card10.user_status = "2";
        card10.type = "notify";
        notificationUserList.add(card10);

        Card card11 = new Card();
        card11.name = userInfo.userName;
        card11.text = getResources().getString(R.string.i_have_best);
        card11.userImage = userInfo.getUserImage();
        card11.date = time;
        card11.user_status = userInfo.userStatus;
        card11.type = "notify";
        card11.facebookId = userInfo.userFacebookId;
        card11.userId = userInfo.userid;
        notificationUserList.add(card11);
    }

    private void getDemoNotificaionData5() {

        Card card1 = new Card();
        card1.name = "Alexander";
        card1.text = getResources().getString(R.string.i_have_best);
        card1.imageint = R.drawable.room_1;
        card1.date = "12:00 AM";
        card1.user_status = "1";
        card1.type = "notify";
        notificationUserList5.add(card1);

        Card card5 = new Card();
        card5.name = "Alizee";
        card5.imageint = R.drawable.room_2;
        card5.uploadImage = R.drawable.demo_3;
        card5.user_status = "2";
        card5.date = "11:59 PM";
        card5.type = "notify";
        notificationUserList5.add(card5);

        Card card3 = new Card();
        card3.name = "Amy";
        card3.imageint = R.drawable.room_3;
        card3.uploadImage = R.drawable.demo_1;
        card3.date = "12:00 AM";
        card3.user_status = "3";
        card3.type = "notify";
        notificationUserList5.add(card3);

        Card card6 = new Card();
        card6.name = "Dhan-mein";
        card6.imageint = R.drawable.room_4;
        card6.uploadImage = R.drawable.demo_5;
        card6.date = "11:59 PM";
        card6.user_status = "2";
        card6.type = "notify";
        notificationUserList5.add(card6);

        Card card7 = new Card();
        card7.name = "Darrin";
        card7.text = getResources().getString(R.string.omg__smooth);
        card7.imageint = R.drawable.room_5;
        card7.date = "11:58 PM";
        card7.user_status = "3";
        card7.type = "notify";
        notificationUserList5.add(card7);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(36.114647, -115.172813);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);


        gmap.setMinZoomPreference(12);
        gmap.setIndoorEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        gmap.addMarker(new MarkerOptions()
                .position(ny)
                .title("Ace Hotel Vegas")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)))
                .showInfoWindow();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(TryAndDemoActivity.this, DemoMapActivity.class);
                startActivity(intent);
            }
        });


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent = new Intent(TryAndDemoActivity.this, DemoMapActivity.class);
                startActivity(intent);
                marker.showInfoWindow();

                return true;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                Intent intent = new Intent(TryAndDemoActivity.this, DemoMapActivity.class);
                startActivity(intent);
            }
        });
    }
}
