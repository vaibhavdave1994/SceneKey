package com.scenekey.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
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
import com.scenekey.model.KeyInUserModal;
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

public class ProfileOtherUserNewActivity extends BaseActivity{

    View v;
    private final String TAG = ProfileOtherUserNewActivity.class.toString();
    private Context context;
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
    private TextView tv_user_name;

    private TextView tv_bio;

    private ImageView btn1, btn2, btn3, btn4, btn5;
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
    TagModal tagModal,tagModal1,tagModal2,tagModal3,tagModal4;
    RelativeLayout toolbar;
    AppCompatImageView img_f11_back;
    TextView txt_event_name;
    AppCompatImageView iv_report;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        context = this;
        utility = new Utility(context);

        txt_event_name = findViewById(R.id.txt_event_name);
        toolbar = findViewById(R.id.toolbar);
        img_f11_back = findViewById(R.id.img_f11_back);

        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnTouchListener(new OnDragTouchListener(relativeLayout));

        ll_donothavebio = findViewById(R.id.ll_donothavebio);
        btn1 = findViewById(R.id.d_btn1);
        btn2 = findViewById(R.id.d_btn2);
        btn3 = findViewById(R.id.d_btn3);
        btn4 = findViewById(R.id.d_btn4);
        btn5 = findViewById(R.id.d_btn5);

        imageList = new ArrayList<>();

        demo_View_dot = findViewById(R.id.demo_View_dot);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        tv_user_name = findViewById(R.id.tv_user_name);
        tv_bio = findViewById(R.id.tv_bio);
        iv_report = findViewById(R.id.iv_report);


        setProfileData();
    }


    private void setProfileData() {

        Intent intent = getIntent();
        final KeyInUserModal keyInUserModal = (KeyInUserModal) intent.getSerializableExtra("keyInUserModal");

        if(keyInUserModal != null) {
            tv_user_name.setText(keyInUserModal.userName);
            txt_event_name.setText(keyInUserModal.userName);
            tv_bio.setText(keyInUserModal.bio);


            imageList.add(new ImagesUpload(keyInUserModal.userImage));
            setUpView();

            iv_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileOtherUserNewActivity.this, ReportActivity.class);
                    intent.putExtra("reportUser", keyInUserModal.userName);
                    startActivityForResult(intent, 2);
                }
            });
        }
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
         }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 2 && resultCode == RESULT_OK) {

                String value = data.getStringExtra("dialog");
                if (value.equals("1")) {
                    utility.showCustomPopup(getString(R.string.report_submitted_successfully), String.valueOf(R.font.montserrat_medium));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

