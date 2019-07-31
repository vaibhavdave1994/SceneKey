package com.scenekey.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.ImageUploadActivity;
import com.scenekey.activity.TagsActivity;
import com.scenekey.activity.TrendinSearchActivity;
import com.scenekey.adapter.ProfileImagePagerAdapter;
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

public class ProfileNew_fragment extends Fragment implements  View.OnClickListener{

    View v;
    private final String TAG = ProfileNew_fragment.class.toString();
    private Context context;
    private HomeActivity activity;
    private Utility utility;
    private CognitoCredentialsProvider credentialsProvider;
    private EventAttendy attendy;
    private boolean myProfile;
    private Event_Fragment event_fragment;
    private Key_In_Event_Fragment key_in_event_fragment;

    private ListView listViewFragProfile;
    private ImageView img_cross, img_left, img_right;
    private ImageView img_green, img_yellow, img_red;
    private TextView txt_event_count, txt_dimmer;

    private ArrayList<Feeds> feedsList;
    private ArrayList<ImagesUpload> imageList;
    private int currentImage, pageToshow = 1;
    private boolean clicked;
    private TextView tv_user_name;

    private EditText tv_bio;

    private ImageView btn1, btn2, btn3, btn4, btn5;
    //private RelativeLayout ly_match_profile;
   // private BottomSheetBehavior<View> mBottomSheetBehavior;
   // private View bottom_sheet;
    private int profilePos;
    //private RelativeLayout customizeView;
    private ProfileImagePagerAdapter pagerAdapter;
    String userImage ="";
    private LinearLayout demo_View_dot;
    ArrayList<BucketDataModel> alOfBucketData;

    //---new code------
    CircleImageView outerBouder,outerBouder1,outerBouder2,outerBouder3,outerBouder4;
    ImageView iv_tag__special_circulerImage,iv_tag__special_circulerImage1,iv_tag__special_circulerImage2,iv_tag__special_circulerImage3,
            iv_tag__special_circulerImage4;
    TextView tag__special_name,tag__special_name1,tag__special_name2,tag__special_name3,tag__special_name4;
    RelativeLayout rl,rl1,rl2,rl3,rl4,rl_error;
    TextView tv_viewall_interest,follow_tokens;
    LinearLayout ll_donothavebio;
    public static boolean shouldRefresh = false;
    TagModal tagModal,tagModal1,tagModal2,tagModal3,tagModal4;
    NestedScrollView bottom_sheet;
    LinearLayout ll_for_drag;
    RelativeLayout rl_;
    CardView cv1,cv2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View v =  inflater.inflate(R.layout.fragment_profile_new_fragment, container, false);
        v =  inflater.inflate(R.layout.new_fragment_profile_new_fragment, container, false);
//        v =  inflater.inflate(R.layout.user_detail_activity_layout, container, false);
       // ly_match_profile = v.findViewById(R.id.ly_match_profile);

        bottom_sheet = v.findViewById(R.id.bottom_sheet);

        bottom_sheet.smoothScrollTo(0,90);
        outerBouder = v.findViewById(R.id.outerBouder);
        outerBouder1 = v.findViewById(R.id.outerBouder1);
        outerBouder2 = v.findViewById(R.id.outerBouder2);
        outerBouder3 = v.findViewById(R.id.outerBouder3);
        outerBouder4 = v.findViewById(R.id.outerBouder4);

        iv_tag__special_circulerImage = v.findViewById(R.id.iv_tag__special_circulerImage);
        iv_tag__special_circulerImage1 = v.findViewById(R.id.iv_tag__special_circulerImage1);
        iv_tag__special_circulerImage2 = v.findViewById(R.id.iv_tag__special_circulerImage2);
        iv_tag__special_circulerImage3 = v.findViewById(R.id.iv_tag__special_circulerImage3);
        iv_tag__special_circulerImage4 = v.findViewById(R.id.iv_tag__special_circulerImage4);
        tag__special_name = v.findViewById(R.id.tag__special_name);
        tag__special_name1 = v.findViewById(R.id.tag__special_name1);
        tag__special_name2 = v.findViewById(R.id.tag__special_name2);
        tag__special_name3 = v.findViewById(R.id.tag__special_name3);
        tag__special_name4 = v.findViewById(R.id.tag__special_name4);

        rl = v.findViewById(R.id.rl);
        rl1 = v.findViewById(R.id.rl1);
        rl2 = v.findViewById(R.id.rl2);
        rl3 = v.findViewById(R.id.rl3);
        rl4 = v.findViewById(R.id.rl4);
        rl.setOnClickListener(this);
        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);
        rl4.setOnClickListener(this);

        rl_error = v.findViewById(R.id.rl_error);
        ll_donothavebio = v.findViewById(R.id.ll_donothavebio);

        tv_viewall_interest = v.findViewById(R.id.tv_viewall_interest);
        tv_viewall_interest.setOnClickListener(this);
        follow_tokens = v.findViewById(R.id.follow_tokens);
        follow_tokens.setOnClickListener(this);

        btn1 = v.findViewById(R.id.d_btn1);
        btn2 = v.findViewById(R.id.d_btn2);
        btn3 = v.findViewById(R.id.d_btn3);
        btn4 = v.findViewById(R.id.d_btn4);
        btn5 = v.findViewById(R.id.d_btn5);

        //listViewFragProfile = v.findViewById(R.id.listViewFragProfile);
        //bottom_sheet = v.findViewById(R.id.bottom_sheet);
       // mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        imageList = new ArrayList<>();

        //customizeView = v.findViewById(R.id.customizeView);
        demo_View_dot = v.findViewById(R.id.demo_View_dot);

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        getBucketDetatils();
        getMyFollowTag();
        ImageView iv_image_upload = v.findViewById(R.id.iv_image_upload);
        iv_image_upload.setOnClickListener(this);
        tv_user_name = v.findViewById(R.id.tv_user_name);
        tv_bio = v.findViewById(R.id.tv_bio);
        TextView tv_update_bio = v.findViewById(R.id.tv_update_bio);
        tv_update_bio.setOnClickListener(this);
        setProfileData();

        int dpHeight = outMetrics.heightPixels;
        int dpWidth = outMetrics.widthPixels;

//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) customizeView.getLayoutParams();
//        params.height = (dpWidth - 20);
//        customizeView.setLayoutParams(params);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



//        Handler handler = new Handler();
//        demo_View_dot.setVisibility(View.VISIBLE);
//        downloadFileFromS3((credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                activity.showProgDialog(false, TAG);
//                getProfileDataApi();
//            }
//        }, 200);
//
//        feedsList = new ArrayList<>();
//
//        // New Code

//        img_green = view.findViewById(R.id.img_green);
//        img_yellow = view.findViewById(R.id.img_yellow);
//        img_red = view.findViewById(R.id.img_red);



        //setClick(iv_image_upload, img_green, img_yellow, img_red, tv_bio, tv_update_bio);
    }

    private void setProfileData() {

        UserInfo userInfo = SessionManager.getInstance().getUserInfo();

        if (!userInfo.lastName.equals("0"))
            tv_user_name.setText(userInfo.fullname + " " + userInfo.lastName);
        else
            tv_user_name.setText(userInfo.fullname);

        if (activity.userInfo().bio.equals("")) {
            //tv_bio.setText("N/A");
            ll_donothavebio.setVisibility(View.VISIBLE);
        } else {
            tv_bio.setText(activity.userInfo().bio);
            ll_donothavebio.setVisibility(View.GONE);
        }

//        if (userInfo.user_status != null) {
//            switch (userInfo.user_status) {
//                case "1":
//                    img_green.setImageResource(R.drawable.ic_active_grn_circle);
//                    img_red.setImageResource(R.drawable.bg_red_ring);
//                    img_yellow.setImageResource(R.drawable.bg_yellow_ring);
//                    break;
//
//                case "2":
//                    img_green.setImageResource(R.drawable.bg_green_ring);
//                    img_red.setImageResource(R.drawable.bg_red_ring);
//                    img_yellow.setImageResource(R.drawable.ic_active_ylw_circle);
//                    break;
//
//                case "3":
//                    img_green.setImageResource(R.drawable.bg_green_ring);
//                    img_red.setImageResource(R.drawable.ic_active_red_circle);
//                    img_yellow.setImageResource(R.drawable.bg_yellow_ring);
//                    break;
//
//                default:
//                    img_green.setImageResource(R.drawable.bg_green_ring);
//                    img_red.setImageResource(R.drawable.ic_active_red_circle);
//                    img_yellow.setImageResource(R.drawable.bg_yellow_ring);
//
//            }
//        }
    }

    private void setClick(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
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

            case R.id.iv_image_upload:
                if (myProfile) {
                    Intent i = new Intent(context, ImageUploadActivity.class);
                    i.putExtra("from", "profile");
                    i.putExtra("alOfBucketData", alOfBucketData);
                    startActivityForResult(i, Constant.IMAGE_UPLOAD_CALLBACK);
                    Constant.DONE_BUTTON_CHECK = 1;
                }
                break;

            case R.id.img_green:
                img_red.setImageResource(R.drawable.bg_red_ring);
                img_yellow.setImageResource(R.drawable.bg_yellow_ring);
                setUserStatus(1, (ImageView) v);
                break;

            case R.id.img_yellow:
                img_green.setImageResource(R.drawable.bg_green_ring);
                img_red.setImageResource(R.drawable.bg_red_ring);
                setUserStatus(2, (ImageView) v);
                break;

            case R.id.img_red:
                img_yellow.setImageResource(R.drawable.bg_yellow_ring);
                img_green.setImageResource(R.drawable.bg_green_ring);
                setUserStatus(3, (ImageView) v);
                break;

            case R.id.tv_update_bio:
                if(!tv_bio.getText().toString().trim().equalsIgnoreCase(""))
                updateBio(tv_bio.getText().toString().trim());
                else
                    utility.showCustomPopup("Please enter bio", String.valueOf(R.font.montserrat_medium));
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
                 getActivity().finish();
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
        if(shouldRefresh){
            // Reload current fragment
//            Fragment frg = null;
//            frg = activity.getSupportFragmentManager().findFragmentByTag(getFragmentManager().getFragments().getClass().getName());
//            final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
//            ft.detach(frg);
//            ft.attach(frg);
//            ft.commit();
//            getBucketDetatils();
//            getMyFollowTag();
        }
    }

    private void profileImgClick() {
        listViewFragProfile.smoothScrollToPosition(0);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.profile_pic_scale_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                StatusBarUtil.setColorNoTranslucent(activity, getResources().getColor(R.color.black70p));
                txt_dimmer.setVisibility(View.VISIBLE);
                img_cross.setVisibility(View.VISIBLE);
                img_right.setVisibility(View.VISIBLE);
                img_left.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Utility.e(TAG, "Animation Repeat");
            }
        });

    }

    private void crossImgClicked() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.profile_pic_scale_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //img_profile_pic2.setAlpha(1.0f);
                //img_profile_pic2.setBorderColor(getResources().getColor(R.color.colorPrimary));
                Animation dimmer = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_to_o);
                img_right.startAnimation(dimmer);
                img_left.startAnimation(dimmer);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txt_dimmer.setVisibility(View.GONE);
                img_cross.setVisibility(View.GONE);
                //img_profile_pic2.setVisibility(View.GONE);

                img_right.setVisibility(View.GONE);
                img_left.setVisibility(View.GONE);
                StatusBarUtil.setColorNoTranslucent(activity, getResources().getColor(R.color.white));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Utility.e(TAG, "Animation Repeat");
            }
        });
        //img_profile_pic2.startAnimation(animation);
    }

    private void setImage(boolean isRight) {
        if (imageList.size() != 0) {
            currentImage = (isRight ? (currentImage == imageList.size() - 1 ? 0 : currentImage + 1) : (currentImage == 0 ? imageList.size() - 1 : currentImage - 1));
            //Picasso.with(activity).load(imageList.get(currentImage).path).transform(new CircleTransform()).placeholder(R.drawable.image_default_profile).into(img_profile_pic2);
        }
    }

    public ProfileNew_fragment setData(EventAttendy attendy, boolean myProfile, Event_Fragment fragment, int mutulFriendCount) {
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

                activity.updateSession(userInfo);
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
                txt_event_count.setText(keyin_count + " events");
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
                        ObjectListing listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + SceneKey.sessionManager.getFacebookId());
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

                if (getView() != null) {
                    setUpView(getView());
                }
                Log.e("step10", "pass");
            }
        });
    }

    /* get image from server end here*/

   /* public CognitoCredentialsProvider getCredentials() {
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider("us-west-2:86b58a3e-0dbd-4aad-a4eb-e82b1a4ebd91", Regions.US_WEST_2);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, context);

        Map<String, String> logins = new HashMap<String, String>();

        try {
            logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }


        credentialsProvider.setLogins(logins);
        return credentialsProvider;
    }*/

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
                    activity.updateSession(userInfo);
                    Picasso.with(activity).load(SceneKey.sessionManager.getUserInfo().getUserImage()).transform(new CircleTransform()).placeholder(R.drawable.image_default_profile);
                    activity.showProgDialog(false, TAG);
                    downloadFileFromS3((credentialsProvider == null ? credentialsProvider = this.getCredentials() : credentialsProvider));
                }
            }
        }
    }//onActivityResult

    // New Code
    private void setUpView(View view) {
        VerticalViewPager viewPager = view.findViewById(R.id.viewpager);

        pagerAdapter = new ProfileImagePagerAdapter(context, imageList, new ProfileImageListener() {
            @Override
            public void getProfilePostion(int pos) {
                profilePos = pos;
            }
        });

        viewPager.setAdapter(pagerAdapter);


        userImage = SceneKey.sessionManager.getUserInfo().getUserImage();
        if (userImage != null) {

            for (int i = 0; i < imageList.size(); i++) {

                if (userImage.equals(imageList.get(i).getPath())) {
                    profilePos = i;
                    break;
                }

            }

            viewPager.setCurrentItem(profilePos);
            initButton(0);
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
        if(imageList.size() == 0){

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
                    activity.updateSession(userInfo);
                    activity.dismissProgDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    activity.dismissProgDialog();
                    Utility.showToast(context, context.getResources().getString(R.string.somethingwentwrong), 0);
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
            Utility.showToast(context, context.getResources().getString(R.string.internetConnectivityError), 0);
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
                            activity.updateSession(userInfo);
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
            Utility.showToast(context, context.getResources().getString(R.string.internetConnectivityError), 0);
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
                                    if(jo.has("bucketInfo")){
                                        imageList = new ArrayList<>();
                                        jsonArray = jo.getJSONArray("bucketInfo");
                                        for(int i =0; i<jsonArray.length(); i++){
                                             JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                             if(jsonObject.has("Key"))
                                            String Key = jsonObject.getString("Key");
                                            String LastModified = jsonObject.getString("LastModified");
                                            String ETag = jsonObject.getString("ETag");
                                            String Size = jsonObject.getString("Size");
                                            String StorageClass = jsonObject.getString("StorageClass");
                                            JSONObject Owner  = jsonObject.getJSONObject("Owner");

                                            String DisplayName = Owner.getString("DisplayName");
                                            String ID = Owner.getString("ID");
                                            OwnerModel ownerModel = new OwnerModel(DisplayName,ID);
                                            alOfBucketData.add(new BucketDataModel(Key,LastModified,ETag,Size,
                                                    StorageClass,ownerModel));

                                            imageList.add(new ImagesUpload(Key));
                                        }
                                        setUpView(v);
                                    }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                      }
                    } catch (Exception e) {
                        //activity.dismissProgDialog();
                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
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
                                    if(jo.has("followTag")){
                                        jsonArray = jo.getJSONArray("followTag");
                                        if(jsonArray.length()>0){
                                            rl_error.setVisibility(View.GONE);
                                        }
                                        else {
                                            rl_error.setVisibility(View.VISIBLE);
                                        }
                                        for(int i =0; i<jsonArray.length(); i++){
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String biz_tag_id = jsonObject.getString("biz_tag_id");
                                            String tag_name = jsonObject.getString("tag_name");
                                            String color_code = jsonObject.getString("color_code");
                                            String tag_image = jsonObject.getString("tag_image");
                                            String isVenue = jsonObject.getString("isVenue");

                                            switch (i){
                                                case 0:
                                                    outerBouder.setBorderColor(Color.parseColor(color_code));
                                                    if(isVenue.equalsIgnoreCase("1")){
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder);
                                                    }
                                                    else {
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
                                                    if(isVenue.equalsIgnoreCase("1")){
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder1);
                                                    }
                                                    else {
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

                                                    if(isVenue.equalsIgnoreCase("1")){
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder2);
                                                    }
                                                    else {
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

                                                    if(isVenue.equalsIgnoreCase("1")){
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder3);
                                                    }
                                                    else {
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

                                                    if(isVenue.equalsIgnoreCase("1")){
                                                        Picasso.with(context).load(tag_image).placeholder(R.drawable.app_icon)
                                                                .error(R.drawable.app_icon).into(outerBouder4);
                                                    }
                                                    else {
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
                        Utility.showToast(context, getString(R.string.somethingwentwrong), 0);
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

    public void goToTagSearchInEvent(TagModal tagModal){
        Intent intent;
//        if(category_name.equalsIgnoreCase("Specials")){
//            intent = new Intent(context, SearchSubCategoryActivity.class);
//            intent.putExtra("tagModal", tagModal);
//            intent.putExtra("catId", catId);
//        }
//        else {
            intent = new Intent(context, TrendinSearchActivity.class);
            intent.putExtra("tag_name", tagModal.tag_name);
            intent.putExtra("tag_image", tagModal.tag_image);
            intent.putExtra("tagmodel", tagModal);
            intent.putExtra("from_tagadapter", true);
       // }
        context.startActivity(intent);
    }

}

