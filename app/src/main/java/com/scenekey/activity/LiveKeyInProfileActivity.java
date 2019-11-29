package com.scenekey.activity;

import android.content.Intent;
import android.os.Build;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.scenekey.R;
import com.scenekey.adapter.ProfileImagePagerAdapter;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.VerticalViewPager;
import com.scenekey.listener.ProfileImageListener;
import com.scenekey.liveSideWork.LiveProfileActivity;
import com.scenekey.model.ImagesUpload;
import com.scenekey.model.KeyInUserModal;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.verticleViewPager.HorizontalKeyInViewPagerAdapter;
import com.scenekey.verticleViewPager.HorizontalViewPager;

import java.util.ArrayList;

public class LiveKeyInProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btn1, btn2, btn3, btn4, btn5;
    public static UserInfo userInfo;
    private final String TAG = LiveProfileActivity.class.toString();
    public ArrayList<ImagesUpload> imageList;
    public HorizontalViewPager viewPager;
    public HorizontalKeyInViewPagerAdapter horizontalKeyInViewPagerAdapter;
    private Utility utility;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private ImageView img_green, img_yellow, img_red;
    private TextView tvHomeTitle, tv_user_name, tv_bio;
    private ImageView img_back, iv_report;
    private String name = "";
    private CustomProgressBar customProgressBar;
    private RelativeLayout ly_match_profile;
    private String eventId;
    private RelativeLayout customizeView;
    private CardView tv_demoMassage;
    private String facebookId = "";
    private String userid = "";
    private CognitoCredentialsProvider credentialsProvider;
    private int profilePos;
    private TextView tv_update_btn;
    private EditText tv_bio_own;
    private ProfileImagePagerAdapter pagerAdapter;
    private  ArrayList<KeyInUserModal>keyInUserModalArrayList;
    private  KeyInUserModal keyInUserModal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        //credentialsProvider = getCredentials();
        inItView();
    }

    private void inItView() {

        utility = new Utility(this);
        customProgressBar = new CustomProgressBar(this);
        userInfo = SceneKey.sessionManager.getUserInfo();
        tvHomeTitle = findViewById(R.id.tvHomeTitle);
        tv_bio = findViewById(R.id.tv_bio);
        tv_user_name = findViewById(R.id.tv_user_name);
        img_back = findViewById(R.id.img_back);
        iv_report = findViewById(R.id.iv_report);
        iv_report.setOnClickListener(this);

        imageList = new ArrayList<>();
        keyInUserModalArrayList = new ArrayList<>();

        tv_demoMassage = findViewById(R.id.tv_demoMassage);

        img_back.setOnClickListener(this);
        iv_report.setOnClickListener(this);

        if (getIntent().getStringExtra("from")!=null){
            String from = getIntent().getStringExtra("from");

            switch (from){

                case "fromTrendingHomeActivity":
                    if (getIntent().getSerializableExtra("keyInUserModalArrayList") != null) {
                        keyInUserModalArrayList = (ArrayList<KeyInUserModal>) getIntent().getSerializableExtra("keyInUserModalArrayList");
                        int pos = getIntent().getIntExtra("fromTrendingHomePostion", 0);

                        if (keyInUserModalArrayList.get(0).userid.equalsIgnoreCase(userInfo().userid)) {
//
                        } else {

                            imageList.add(new ImagesUpload(keyInUserModalArrayList.get(0).userImage));
                            showKeyInUI(pos);
                            setUpView();
                        }
                    }
                    break;
            }
        }
    }

    private void showKeyInUI(int pos) {
        keyInUserModal = keyInUserModalArrayList.get(pos);


        if (keyInUserModal.userName.equals(userInfo.fullname)) {
            iv_report.setVisibility(View.GONE);
            tv_bio_own.setText(keyInUserModal.bio);

        } else {
            iv_report.setVisibility(View.VISIBLE);
            name = keyInUserModal.userName;
            if(!tv_bio.equals("")){
                tv_bio.setText(keyInUserModal.bio);
            }else{
                tv_bio.setText("Demo bio");
            }
        }

        tvHomeTitle.setText(keyInUserModal.userName);
        tv_user_name.setText(keyInUserModal.userName);


        name = keyInUserModal.userName;

        facebookId = keyInUserModal.userFacebookId;
        userid = keyInUserModal.userid;
    }

    private void setUpView() {
        VerticalViewPager viewPager = findViewById(R.id.viewpager);

        btn1 = findViewById(R.id.d_btn1);
        btn2 = findViewById(R.id.d_btn2);
        btn3 = findViewById(R.id.d_btn3);
        btn4 = findViewById(R.id.d_btn4);
        btn5 = findViewById(R.id.d_btn5);

        pagerAdapter = new ProfileImagePagerAdapter(this, imageList, new ProfileImageListener() {
            @Override
            public void getProfilePostion(int pos) {
                profilePos = pos;
            }
        });

        viewPager.setAdapter(pagerAdapter);


            for (int i = 0; i < imageList.size(); i++) {



            }

            viewPager.setCurrentItem(profilePos);
            initButton(0);


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
                btn1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                break;

            case 1:
                btn1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                break;

            case 2:
                btn1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                break;

            case 3:
                btn1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                break;

            case 4:
                btn1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_profile_img_bullet));
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
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_back:
                onBackPressed();
                break;


            case R.id.iv_report:
                Intent intent = new Intent(this, ReportActivity.class);
                intent.putExtra("reportUser", name);
                startActivityForResult(intent, 2);
                break;
        }
    }


    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public UserInfo userInfo() {
        if (userInfo == null) {
            if (!SceneKey.sessionManager.isLoggedIn()) {
                SceneKey.sessionManager.logout(LiveKeyInProfileActivity.this);
            }
            userInfo = SceneKey.sessionManager.getUserInfo();
        }
        return userInfo;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public Fragment addFragment(Fragment fragmentHolder, int animationValue) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentName = fragmentHolder.getClass().getName();

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

            return fragmentHolder;
        } catch (Exception e) {
            return null;
        }
    }
}