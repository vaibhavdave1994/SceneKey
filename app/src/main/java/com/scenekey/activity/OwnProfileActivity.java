package com.scenekey.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.scenekey.R;
import com.scenekey.adapter.ProfileImagePagerAdapter;
import com.scenekey.base.BaseActivity;
import com.scenekey.fragment.Event_Fragment;
import com.scenekey.fragment.Key_In_Event_Fragment;
import com.scenekey.helper.Constant;
import com.scenekey.helper.OnDragTouchListener;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.VerticalViewPager;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.ProfileImageListener;
import com.scenekey.model.BucketDataModel;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.Feeds;
import com.scenekey.model.ImagesUpload;
import com.scenekey.model.OwnerModel;
import com.scenekey.model.TagModal;
import com.scenekey.model.UserInfo;
import com.scenekey.util.CircleTransform;
import com.scenekey.util.SceneKey;
import com.scenekey.util.StatusBarUtil;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OwnProfileActivity extends BaseActivity implements View.OnClickListener {

    public static boolean shouldRefresh = false;
    private final String TAG = OwnProfileActivity.class.toString();
    String userImage = "";
    ArrayList<BucketDataModel> alOfBucketData;
    UserInfo userInfo;
    //---new code------
    CircleImageView outerBouder, outerBouder1, outerBouder2, outerBouder3, outerBouder4;
    ImageView iv_tag__special_circulerImage, iv_tag__special_circulerImage1, iv_tag__special_circulerImage2, iv_tag__special_circulerImage3,
            iv_tag__special_circulerImage4;
    TextView tag__special_name, tag__special_name1, tag__special_name2, tag__special_name3, tag__special_name4;
    RelativeLayout rl, rl1, rl2, rl3, rl4, rl_error, rl_view;
    TextView tv_viewall_interest, follow_tokens;
    LinearLayout ll_donothavebio;
    TagModal tagModal, tagModal1, tagModal2, tagModal3, tagModal4;
    NestedScrollView bottom_sheet;
    private Context context;
    private OwnProfileActivity activity;
    private Utility utility;
    private CognitoCredentialsProvider credentialsProvider;
    private EventAttendy attendy;
    private boolean myProfile;
    private Event_Fragment event_fragment;
    private Key_In_Event_Fragment key_in_event_fragment;
    private ArrayList<Feeds> feedsList;
    private ArrayList<ImagesUpload> imageList;
    private int currentImage, pageToshow = 1;
    private boolean clicked;
    private TextView tv_user_name, tvHomeTitle, tv_key_points;
    private EditText tv_bio;
    private ImageView btn1;
    private ImageView btn2;
    private ImageView btn3;
    private ImageView btn4;
    private ImageView btn5;
    //private RelativeLayout ly_match_profile;
    // private BottomSheetBehavior<View> mBottomSheetBehavior;
    // private View bottom_sheet;
    private int profilePos;
    //private RelativeLayout customizeView;
    private ProfileImagePagerAdapter pagerAdapter;
    private LinearLayout demo_View_dot;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owne_profile);


        this.context = this;
        activity = OwnProfileActivity.this;
        utility = new Utility(context);
        userInfo = new UserInfo();
        /*bottom_sheet = findViewById(R.id.bottom_sheet);


        bottom_sheet.smoothScrollTo(0,90);*/
        outerBouder = findViewById(R.id.outerBouder);
        outerBouder1 = findViewById(R.id.outerBouder1);
        outerBouder2 = findViewById(R.id.outerBouder2);
        outerBouder3 = findViewById(R.id.outerBouder3);
        outerBouder4 = findViewById(R.id.outerBouder4);
        iv_tag__special_circulerImage = findViewById(R.id.iv_tag__special_circulerImage);
        iv_tag__special_circulerImage1 = findViewById(R.id.iv_tag__special_circulerImage1);
        iv_tag__special_circulerImage2 = findViewById(R.id.iv_tag__special_circulerImage2);
        iv_tag__special_circulerImage3 = findViewById(R.id.iv_tag__special_circulerImage3);
        iv_tag__special_circulerImage4 = findViewById(R.id.iv_tag__special_circulerImage4);
        tag__special_name = findViewById(R.id.tag__special_name);
        tag__special_name1 = findViewById(R.id.tag__special_name1);
        tag__special_name2 = findViewById(R.id.tag__special_name2);
        tag__special_name3 = findViewById(R.id.tag__special_name3);
        tag__special_name4 = findViewById(R.id.tag__special_name4);

        rl = findViewById(R.id.rl);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl_view = findViewById(R.id.rl_view);
        ImageView img_setting = findViewById(R.id.img_setting);
        ImageView iv_back = findViewById(R.id.iv_back);
        tvHomeTitle = findViewById(R.id.tvHomeTitle);
        tv_key_points = findViewById(R.id.tv_key_points);
        rl.setOnClickListener(this);
        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);
        rl4.setOnClickListener(this);
        rl_view.setOnClickListener(this);
        img_setting.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        rl_error = findViewById(R.id.rl_error);
        ll_donothavebio = findViewById(R.id.ll_donothavebio);

        tv_viewall_interest = findViewById(R.id.tv_viewall_interest);
        tv_viewall_interest.setOnClickListener(this);
        follow_tokens = findViewById(R.id.follow_tokens);
        follow_tokens.setOnClickListener(this);

        btn1 = findViewById(R.id.d_btn1);
        btn2 = findViewById(R.id.d_btn2);
        btn3 = findViewById(R.id.d_btn3);
        btn4 = findViewById(R.id.d_btn4);
        btn5 = findViewById(R.id.d_btn5);

        imageList = new ArrayList<>();
        demo_View_dot = findViewById(R.id.demo_View_dot);

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        getBucketDetatils();
        getMyFollowTag();
        ImageView iv_image_upload = findViewById(R.id.iv_image_upload);
        iv_image_upload.setOnClickListener(this);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_bio = findViewById(R.id.tv_bio);
        TextView tv_update_bio = findViewById(R.id.tv_update_bio);
        tv_update_bio.setOnClickListener(this);
        setProfileData();

        int dpHeight = outMetrics.heightPixels;
        int dpWidth = outMetrics.widthPixels;
        tv_bio.setCursorVisible(false);
        tv_bio.setOnTouchListener((view, motionEvent) -> {
            tv_bio.setCursorVisible(true);
            return false;
        });


        final RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnTouchListener(new OnDragTouchListener(relativeLayout));
    }


    private void setProfileData() {

        UserInfo userInfo = SessionManager.getInstance().getUserInfo();

        tvHomeTitle.setText(userInfo.fullname);

        if (Integer.parseInt(SceneKey.sessionManager.getUserInfo().key_points) > 1000) {
            tv_key_points.setText(Utility.coolFormat(Double.parseDouble(SceneKey.sessionManager.getUserInfo().key_points), 0));
        } else {
            tv_key_points.setText(SceneKey.sessionManager.getUserInfo().key_points);

        }
        if (!userInfo.lastName.equals("0"))
            tv_user_name.setText(userInfo.fullname + " " + userInfo.lastName);
        else
            tv_user_name.setText(userInfo.fullname);


        if (userInfo.bio.equals("")) {
            ll_donothavebio.setVisibility(View.VISIBLE);
            tv_bio.setVisibility(View.GONE);

        } else {
            tv_bio.setText(userInfo.bio);
            ll_donothavebio.setVisibility(View.GONE);
            tv_bio.setVisibility(View.VISIBLE);

        }

    }

    private void setClick(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.iv_image_upload: {
                Intent i = new Intent(context, ImageUploadActivity.class);
                i.putExtra("from", "profile");
                i.putExtra("alOfBucketData", alOfBucketData);
                startActivityForResult(i, Constant.IMAGE_UPLOAD_CALLBACK);
                Constant.DONE_BUTTON_CHECK = 1;
            }
            break;

            case R.id.img_setting:

                Intent seetingIntet = new Intent(this, SettingActivtiy.class);
                startActivity(seetingIntet);
                // }

                break;


            case R.id.tv_update_bio:
                if (tv_bio.getVisibility() == View.VISIBLE) {
                    if (!tv_bio.getText().toString().trim().equalsIgnoreCase("")) {
                        updateBio(tv_bio.getText().toString().trim());
                        tv_bio.setCursorVisible(false);
                    } else
                        utility.showCustomPopup("Please enter bio", String.valueOf(R.font.montserrat_medium));
                } else {
                    tv_bio.setCursorVisible(false);
                    Intent bioIntent = new Intent(OwnProfileActivity.this, BioActivity.class);
                    bioIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bioIntent.putExtra("from", "setting");
                    startActivity(bioIntent);
                }


                break;

            case R.id.img_cross:
                crossImgClicked();
                break;

            case R.id.img_right:
                setImage(true);
                break;
            case R.id.img_left:
                setImage(false);
                break;
            case R.id.txt_dimmer:
                crossImgClicked();
                break;

            case R.id.tv_viewall_interest:
                Intent intent = new Intent(context, TagsActivity.class);
                intent.putExtra("fromProfile", true);
                startActivity(intent);
                break;

            case R.id.follow_tokens:
                Intent intent1 = new Intent(context, HomeActivity.class);
                intent1.putExtra("fromSearch", true);
                intent1.putExtra("name", "");
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
                break;

            case R.id.rl:
                goToTagSearchInEvent(tagModal);
                break;

            case R.id.rl1:
                goToTagSearchInEvent(tagModal1);
                break;

            case R.id.rl2:
                goToTagSearchInEvent(tagModal2);
                break;

            case R.id.rl3:
                goToTagSearchInEvent(tagModal3);
                break;

            case R.id.rl4:
                goToTagSearchInEvent(tagModal4);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (event_fragment != null) event_fragment.canCallWebservice = false;
        if (key_in_event_fragment != null) key_in_event_fragment.canCallWebservice = false;

        Log.e("Test", " Profile-OnStart");
    }

    @Override
    public void onResume() {
        //   setProfileData();
        super.onResume();

    }


    private void crossImgClicked() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.profile_pic_scale_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Animation dimmer = AnimationUtils.loadAnimation(context, R.anim.alpha_to_o);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                StatusBarUtil.setColorNoTranslucent(activity, getResources().getColor(R.color.white));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Utility.e(TAG, "Animation Repeat");
            }
        });
    }

    private void setImage(boolean isRight) {
        if (imageList.size() != 0) {
            currentImage = (isRight ? (currentImage == imageList.size() - 1 ? 0 : currentImage + 1) : (currentImage == 0 ? imageList.size() - 1 : currentImage - 1));
        }
    }

    public OwnProfileActivity setData(EventAttendy attendy, boolean myProfile, Event_Fragment fragment, int mutulFriendCount) {
        this.attendy = attendy;
        this.myProfile = myProfile;
        this.event_fragment = fragment;
        return this;
    }

    /* Profile setData start here */
    private void getProfileDataApi() {

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.LISTATTENDEDEVENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.v("responce", response);

                    Utility.printBigLogcat(TAG, response);
                    try {
                        getResponse(response);
                        Log.e("step2", "Pass");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("step02", "fail");
                    }
                    if (feedsList == null) {
                        feedsList = new ArrayList<>();
                    }
                    activity.dismissProgDialog();
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
                    params.put("user_id", attendy.userid);
                    params.put("type", "app");
                    params.put("myId", activity.userInfo().userid);
                    Log.e("step1", "Pass");
                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            //utility.snackBar(mainLayout, getString(R.string.internetConnectivityError), 0);
            //utility.snackBar(ly_match_profile, getString(R.string.internetConnectivityError), 0);
            Toast.makeText(context, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            activity.dismissProgDialog();
        }
    }

    private synchronized void getResponse(String response) throws JSONException {
        if (feedsList == null) {
            feedsList = new ArrayList<>();
            feedsList.clear();
        }
        JSONObject object = new JSONObject(response);

        try {
            if (object.has("myInfo")) {
                UserInfo userInfo = activity.userInfo();
                JSONObject user = object.getJSONObject("myInfo");
                if (user.has("fullname")) userInfo.fullname = (user.getString("fullname"));
                if (user.has("address")) userInfo.address = (user.getString("address"));
                if (user.has("lat")) userInfo.lat = (user.getString("lat"));
                if (user.has("longi")) userInfo.longi = (user.getString("longi"));
                if (user.has("makeAdmin")) userInfo.makeAdmin = (user.getString("makeAdmin"));
                if (user.has("key_points")) userInfo.key_points = (user.getString("key_points"));

                Utility.e("Profile session update.", userInfo.getUserImage());

                updateSession(userInfo);
                Log.e("step3", "Pass");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("step03", "Pass");
        }

        if (object.has("allfeeds")) {
            JSONArray array = object.getJSONArray("allfeeds");
            for (int i = 0; i < array.length(); i++) {
                Feeds feeds = new Feeds();
                JSONObject feedJson = array.getJSONObject(i);

                if (feedJson.has("username")) feeds.username = (feedJson.getString("username"));
                if (feedJson.has("userid")) feeds.userid = (feedJson.getString("userid"));
                if (feedJson.has("userFacebookId"))
                    feeds.userFacebookId = (feedJson.getString("userFacebookId"));
                if (feedJson.has("event_id")) feeds.event_id = (feedJson.getString("event_id"));
                if (feedJson.has("ratetype")) feeds.ratetype = (feedJson.getString("ratetype"));
                if (feedJson.has("event_name"))
                    feeds.event_name = (feedJson.getString("event_name"));
                if (feedJson.has("userimage")) feeds.userimage = (feedJson.getString("userimage"));
                if (feedJson.has("type")) feeds.type = (feedJson.getString("type"));
                if (feedJson.has("location")) feeds.location = (feedJson.getString("location"));
                if (feedJson.has("date")) feeds.date = (feedJson.getString("date"));
                if (feedJson.has("feed")) feeds.feed = (feedJson.getString("feed"));

                feedsList.add(feeds);
                if (i == 1) activity.dismissProgDialog();
                Log.e("step4", "Pass");
            }
            //adapter.notifyDataSetChanged();
        }

     /*   if (feedsList.size() == 0) {
            //  Utility.showToast(context, " No event found !", 0);
          //  adapter.notifyDataSetChanged();
        }*/

        try {
            if (object.has("keyin_count")) {
                String keyin_count = "";
                keyin_count = String.valueOf(object.getInt("keyin_count"));
                Log.e("keyin_count", keyin_count);
//                txt_event_count.setText(keyin_count + " events");
                Log.e("step5", "Pass");
            }
        } catch (Exception e) {

            e.printStackTrace();
            Log.e("step05", "Pass");
        }
        //setRecyclerView();
        //rclv_f3_trending.setHasFixedSize(true);
    }

    /* get image from server start here*/

    private void downloadFileFromS3(CognitoCredentialsProvider credentialsProvider) {//, CognitoCachingCredentialsProvider credentialsProvider){
        try {
            final AmazonS3Client s3Client;
            s3Client = new AmazonS3Client(credentialsProvider);
            Log.e("step6", "Pass");
            // Set the region of your S3 bucket
            s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("step7", "Pass");
                    try {
                        Log.v("getFacebookId", SceneKey.sessionManager.getFacebookId());

                        Log.v("url", Constant.BUCKET + "," + Constant.DEV_TAG + SceneKey.sessionManager.getFacebookId());
                        ObjectListing listing = s3Client.listObjects(Constant.BUCKET,  SceneKey.sessionManager.getFacebookId());
//                        ObjectListing listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + SceneKey.sessionManager.getFacebookId());
                        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

                        while (listing.isTruncated()) {

                            listing = s3Client.listNextBatchOfObjects(listing);
                            summaries.addAll(listing.getObjectSummaries());

                        }
                        Log.e("step8", "Pass");
                        updateImages(summaries);

                        Utility.e(TAG, "listing " + summaries.get(0).getKey() + "no of image " + summaries.size());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.e(TAG, "Exception found while listing " + e);
                        Log.e("step9", "Fail");
                    }
                }
            });

            thread.start();
            activity.dismissProgDialog();
        } catch (Exception e) {
            Utility.e("AMAZON", e.toString());
            activity.dismissProgDialog();
            Log.e("step09", "fail");
        }
    }

    private void updateImages(final List<S3ObjectSummary> summaries) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                demo_View_dot.setVisibility(View.GONE);
                for (S3ObjectSummary obj : summaries) {
                    imageList.add(new ImagesUpload(obj.getKey()));
                }
                try {
                    //Picasso.with(activity).load(imageList.get(currentImage).path).transform(new CircleTransform()).placeholder(R.drawable.image_default_profile).into(img_profile_pic2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                setUpView();

                Log.e("step10", "pass");
            }
        });
    }

    private CognitoCredentialsProvider getCredentials() {
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider("us-west-2:86b58a3e-0dbd-4aad-a4eb-e82b1a4ebd91", Regions.US_WEST_2);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, context);

        Map<String, String> logins = new HashMap<String, String>();

        String token = "";
        try {
            token = AccessToken.getCurrentAccessToken().getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (token != null && !token.equals("")) {
            logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
        } else {
            logins.put("graph.facebook.com", Constant.Token);
        }
        credentialsProvider.setLogins(logins);
        return credentialsProvider;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.IMAGE_UPLOAD_CALLBACK) {
            if (resultCode == Activity.RESULT_OK) {
                boolean isValue = data.getBooleanExtra("isResult", false);
                if (isValue) {
                    //reload image
                    imageList.clear();
                    UserInfo userInfo = SceneKey.sessionManager.getUserInfo();
                    userInfo.userImage = SceneKey.sessionManager.getUserInfo().getUserImage();
                    updateSession(userInfo);
                    Picasso.with(activity).load(SceneKey.sessionManager.getUserInfo().getUserImage()).transform(new CircleTransform()).placeholder(R.drawable.image_default_profile);
                    activity.showProgDialog(false, TAG);
                    downloadFileFromS3((credentialsProvider == null ? credentialsProvider = this.getCredentials() : credentialsProvider));
                }
            }
        }
    }//onActivityResult

    // New Code
    private void setUpView() {
        VerticalViewPager viewPager = findViewById(R.id.viewpager);

        pagerAdapter = new ProfileImagePagerAdapter(context, imageList, new ProfileImageListener() {
            @Override
            public void getProfilePostion(int pos) {
                profilePos = pos;
            }
        });

        viewPager.setAdapter(pagerAdapter);

        userImage = SceneKey.sessionManager.getUserInfo().getUserImage();
        if (imageList.size() > 1) {
            if (userImage != null) {

                for (int i = 0; i < imageList.size(); i++) {

                    if (userImage.equals(imageList.get(i).getPath())) {
                        profilePos = i;
                        break;
                    }

                }

                viewPager.setCurrentItem(profilePos);
                initButton(profilePos);

            }
        } else {
            demo_View_dot.setVisibility(View.GONE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                initButton(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.e("step11", "Pass");
    }

    private void initButton(int position) {
        if (imageList.size() == 0) {

        }
        switch (position) {
            case 0:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 1:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 2:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 3:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 4:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                break;
        }

        switch (imageList.size()) {
            case 1:
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
                btn3.setVisibility(View.GONE);
                btn4.setVisibility(View.GONE);
                btn5.setVisibility(View.GONE);
                break;

            case 2:
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.GONE);
                btn4.setVisibility(View.GONE);
                btn5.setVisibility(View.GONE);
                break;

            case 3:
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                btn4.setVisibility(View.GONE);
                btn5.setVisibility(View.GONE);
                break;

            case 4:
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                btn4.setVisibility(View.VISIBLE);
                btn5.setVisibility(View.GONE);
                break;

            case 5:
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                btn4.setVisibility(View.VISIBLE);
                btn5.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setUserStatus(int i, ImageView imageView) {

        switch (i) {
            case 1:
                imageView.setImageResource(R.drawable.ic_active_grn_circle);
                setUserStatus(i);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_active_ylw_circle);
                setUserStatus(i);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_active_red_circle);
                setUserStatus(i);
                break;

        }
    }

    private void setUserStatus(final int value) {
        final Utility utility = new Utility(context);

        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.SET_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utility.e(TAG, response);
                    UserInfo userInfo = activity.userInfo();
                    userInfo.user_status = String.valueOf(value);
                    updateSession(userInfo);
                    activity.dismissProgDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
//                    Utility.showToast(context, context.getResources().getString(R.string.somethingwentwrong), 0);
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("status", value + "");
                    params.put("user_id", activity.userInfo().userid);

                    Utility.e(TAG, " params " + params.toString());
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
//            Utility.showToast(context, context.getResources().getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    private void updateBio(final String bio) {
        activity.showProgDialog(false, TAG);
        final Utility utility = new Utility(context);

        Log.e("step12", "Pass");
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.BIO, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {

                    Log.e("step12", "Pass");
                    // get response
                    JSONObject jsonObject;
                    try {
                        activity.dismissProgDialog();
                        // System.out.println(" login response" + response);
                        jsonObject = new JSONObject(Response);
                        int statusCode = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (statusCode == 1) {
                            UserInfo userInfo = activity.userInfo();
                            userInfo.bio = bio;
                            updateSession(userInfo);
                            utility.showCustomPopup("Bio updated successfully", String.valueOf(R.font.montserrat_medium));
                            //  callIntent();
                            Log.e("step13", "Pass");
                        } else {
                            Utility.showToast(context, message, 0);
                            Log.e("step14", "Pass");
                        }

                    } catch (Exception ex) {
                        activity.dismissProgDialog();
                        ex.printStackTrace();
                        Log.e("step15", "Pass");
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
                    params.put("bio", bio);
                    params.put("user_id", activity.userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
//            Utility.showToast(context, context.getResources().getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }

    public void getBucketDetatils() {
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.GET_BUCKET_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        alOfBucketData = new ArrayList<>();
                        if (jo.has("success")) {
                            int success = jo.getInt("success");
                            if (success == 1) {
                                try {

                                    JSONArray jsonArray = new JSONArray();
                                    if (jo.has("bucketInfo")) {
                                        imageList = new ArrayList<>();
                                        jsonArray = jo.getJSONArray("bucketInfo");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                             if(jsonObject.has("Key"))
                                            String Key = jsonObject.getString("Key");
                                            String LastModified = jsonObject.getString("LastModified");
                                            String ETag = jsonObject.getString("ETag");
                                            String Size = jsonObject.getString("Size");
                                            String StorageClass = jsonObject.getString("StorageClass");
                                            JSONObject Owner = jsonObject.getJSONObject("Owner");

                                            String DisplayName = Owner.getString("DisplayName");
                                            String ID = Owner.getString("ID");
                                            OwnerModel ownerModel = new OwnerModel(DisplayName, ID);
                                            alOfBucketData.add(new BucketDataModel(Key, LastModified, ETag, Size,
                                                    StorageClass, ownerModel));

                                            imageList.add(new ImagesUpload(Key));
                                        }
                                        setUpView();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        //activity.dismissProgDialog();
//                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    //  activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userFacebookId);
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
        } else {
            //activity.dismissProgDialog();
        }
    }

    public void getMyFollowTag() {
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.GET_MY_FOLLOW_TAGS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        if (jo.has("status")) {
                            if (jo.getString("status").equalsIgnoreCase("success")) {
                                try {

                                    JSONArray jsonArray = new JSONArray();
                                    if (jo.has("followTag")) {
                                        jsonArray = jo.getJSONArray("followTag");
                                        if (jsonArray.length() > 0) {
                                            rl_error.setVisibility(View.GONE);
                                        }
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            rl_view.setVisibility(View.VISIBLE);
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String biz_tag_id = jsonObject.getString("biz_tag_id");
                                            String tag_name = jsonObject.getString("tag_name");
                                            String color_code = jsonObject.getString("color_code");
                                            String tag_image = jsonObject.getString("tag_image");
                                            String isVenue = jsonObject.getString("isVenue");

                                            switch (i) {
                                                case 0:
                                                    outerBouder.setBorderColor(Color.parseColor(color_code));
                                                    if (isVenue.equalsIgnoreCase("1")) {
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder);
                                                    } else {
                                                        Glide.with(context).load(tag_image).centerCrop().placeholder(R.drawable.app_icon)
                                                                .into(iv_tag__special_circulerImage);
                                                    }

                                                    tag__special_name.setText(tag_name);
                                                    rl.setVisibility(View.VISIBLE);
                                                    tagModal = new TagModal();
                                                    tagModal.biz_tag_id = biz_tag_id;
                                                    tagModal.tag_name = tag_name;
                                                    tagModal.tag_image = tag_image;
                                                    tagModal.color_code = color_code;
                                                    break;
                                                case 1:
                                                    outerBouder1.setBorderColor(Color.parseColor(color_code));
                                                    if (isVenue.equalsIgnoreCase("1")) {
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder1);
                                                    } else {
                                                        Glide.with(context).load(tag_image).centerCrop().placeholder(R.drawable.app_icon)
                                                                .into(iv_tag__special_circulerImage1);
                                                    }


                                                    tag__special_name1.setText(tag_name);
                                                    rl1.setVisibility(View.VISIBLE);
                                                    tagModal1 = new TagModal();
                                                    tagModal1.biz_tag_id = biz_tag_id;
                                                    tagModal1.tag_name = tag_name;
                                                    tagModal1.tag_image = tag_image;
                                                    tagModal1.color_code = color_code;
                                                    break;
                                                case 2:
                                                    outerBouder2.setBorderColor(Color.parseColor(color_code));

                                                    if (isVenue.equalsIgnoreCase("1")) {
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder2);
                                                    } else {
                                                        Glide.with(context).load(tag_image).centerCrop().placeholder(R.drawable.app_icon)
                                                                .into(iv_tag__special_circulerImage2);
                                                    }

                                                    tag__special_name2.setText(tag_name);
                                                    rl2.setVisibility(View.VISIBLE);
                                                    tagModal2 = new TagModal();
                                                    tagModal2.biz_tag_id = biz_tag_id;
                                                    tagModal2.tag_name = tag_name;
                                                    tagModal2.tag_image = tag_image;
                                                    tagModal2.color_code = color_code;
                                                    break;
                                                case 3:
                                                    outerBouder3.setBorderColor(Color.parseColor(color_code));

                                                    if (isVenue.equalsIgnoreCase("1")) {
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder3);
                                                    } else {
                                                        Glide.with(context).load(tag_image).centerCrop().placeholder(R.drawable.app_icon)
                                                                .into(iv_tag__special_circulerImage3);
                                                    }

                                                    tag__special_name3.setText(tag_name);
                                                    rl3.setVisibility(View.VISIBLE);
                                                    tagModal3 = new TagModal();
                                                    tagModal3.biz_tag_id = biz_tag_id;
                                                    tagModal3.tag_name = tag_name;
                                                    tagModal3.tag_image = tag_image;
                                                    tagModal3.color_code = color_code;

                                                    break;
                                                case 4:
                                                    outerBouder4.setBorderColor(Color.parseColor(color_code));

                                                    if (isVenue.equalsIgnoreCase("1")) {
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder4);
                                                    } else {
                                                        Glide.with(context).load(tag_image).centerCrop().placeholder(R.drawable.app_icon)
                                                                .into(iv_tag__special_circulerImage4);
                                                    }

                                                    tag__special_name4.setText(tag_name);
                                                    rl4.setVisibility(View.VISIBLE);
                                                    tagModal4 = new TagModal();
                                                    tagModal4.biz_tag_id = biz_tag_id;
                                                    tagModal4.tag_name = tag_name;
                                                    tagModal4.tag_image = tag_image;
                                                    tagModal4.color_code = color_code;

                                                    break;

                                                default:
                                                    break;
                                            }
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        //activity.dismissProgDialog();
//                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    //  activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("userId", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
        } else {
            //activity.dismissProgDialog();
        }
    }

    public void goToTagSearchInEvent(TagModal tagModal) {
        Intent intent;
        intent = new Intent(context, TrendinSearchActivity.class);
        intent.putExtra("tag_name", tagModal.tag_name);
        intent.putExtra("tag_image", tagModal.tag_image);
        intent.putExtra("tagmodel", tagModal);
        intent.putExtra("from_tagadapter", true);
        context.startActivity(intent);
    }

    public void updateSession(UserInfo user) {
        SceneKey.sessionManager.createSession(user);
        userInfo = SceneKey.sessionManager.getUserInfo();


    }
}

