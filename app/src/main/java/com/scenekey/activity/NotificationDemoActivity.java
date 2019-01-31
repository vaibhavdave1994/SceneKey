package com.scenekey.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.adapter.RoomPaggerSlider;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.model.UserInfo;
import com.scenekey.notificationViewPager.CustomViewPager;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import java.util.ArrayList;

public class NotificationDemoActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static UserInfo userInfo;
    private final String TAG = NotificationDemoActivity.class.toString();
    public com.scenekey.notificationViewPager.CustomViewPager viewPager;
    public int countupdate = 0;
    private ImageView iv_for_not_status, iv_forwordd_notify, iv_report_notify, img_back_notif;
    private TextView tv_user_notif_name, tvHomeTitle_notif, tv_send_btn_notify;
    private LinearLayout ly_msg_box;
    private RelativeLayout iv_dot_notify;
    private EditText et_prvt_message_notifiy;
    private CardView layout_message;
    private Utility utility;
    private String time;
    private String name = "Demo";
    private int pos;
    private int position1;
    private RoomPaggerSlider roomAdapter;
    private View bottom_sheet;
    private String send ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_demo);
        findById();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void findById() {
        utility = new Utility(this);
        userInfo = SceneKey.sessionManager.getUserInfo();
        iv_for_not_status = findViewById(R.id.iv_for_not_status);
        iv_forwordd_notify = findViewById(R.id.iv_forwordd_notify);
        tv_user_notif_name = findViewById(R.id.tv_user_notif_name);
        ly_msg_box = findViewById(R.id.ly_msg_box);
        layout_message = findViewById(R.id.layout_message);
        tvHomeTitle_notif = findViewById(R.id.tvHomeTitle_notif);
        iv_report_notify = findViewById(R.id.iv_report_notify);
        img_back_notif = findViewById(R.id.img_back_notif);

        bottom_sheet = findViewById(R.id.bottom_sheet);

        iv_report_notify.setOnClickListener(this);
        img_back_notif.setOnClickListener(this);
        iv_forwordd_notify.setOnClickListener(this);
        //viewpagger
        viewPager = findViewById(R.id.room_slider_pager);

        viewPager.setAllowedSwipeDirection(CustomViewPager.SwipeDirection.right);
        tv_send_btn_notify = findViewById(R.id.tv_send_btn_notify);
        et_prvt_message_notifiy = findViewById(R.id.et_prvt_message_notifiy);
        tv_send_btn_notify.setOnClickListener(this);

        time = utility.getTimestamp("hh:mm aa");

        if (getIntent().getSerializableExtra("fromNotificationList") != null) {
            ArrayList<Card> cardArrayList = (ArrayList<Card>) getIntent().getSerializableExtra("fromNotificationList");
            pos = getIntent().getIntExtra("fromNotification", 0);

            roomAdapter = new RoomPaggerSlider(this, cardArrayList);
            viewPager.setAdapter(new RoomPaggerSlider(this, cardArrayList));
            viewPager.setCurrentItem(pos);
            viewPager.setOnPageChangeListener(NotificationDemoActivity.this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_forwordd_notify:

                Animation animation = AnimationUtils.loadAnimation(this,R.anim.bottom_to_position);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottom_sheet.getLayoutParams();
                layoutParams.topMargin = 0;
                ly_msg_box.setVisibility(View.VISIBLE);
                iv_forwordd_notify.setVisibility(View.GONE);
                bottom_sheet.setLayoutParams (layoutParams);
                bottom_sheet.startAnimation(animation);

                break;

            case R.id.iv_report_notify:
                Intent reportIntent = new Intent(this, ReportActivity.class);
                reportIntent.putExtra("reportUser", name);
                startActivity(reportIntent);
                break;

            case R.id.img_back_notif:
                onBackPressed();
                break;

            case R.id.tv_send_btn_notify:
                if (et_prvt_message_notifiy.getText().toString().isEmpty()) {
                    utility.showCustomPopup(getResources().getString(R.string.enter_message), String.valueOf(R.font.montserrat_medium));
                } else {
                    Card card3 = new Card();
                    card3.name = "Demo";
                    card3.text = et_prvt_message_notifiy.getText().toString().trim();
                    card3.userImage = userInfo.getUserImage();
                    card3.uploadImage = R.drawable.demo_1;
                    card3.date = time;

                    TryAndDemoActivity.arrayList.add(card3);
                    TryAndDemoActivity.customListAdapter.notifyDataSetChanged();

                    send = "yes";
                    onBackPressed();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(send.equals("yes")){
            Intent intent = new Intent();
            intent.putExtra("Count", countupdate);
            intent.putExtra("yes", "yes");
            setResult(2, intent);
            finish();
        }else {
            Intent intent = new Intent();
            intent.putExtra("Count", countupdate);
            intent.putExtra("yes", "no");
            setResult(2, intent);
            finish();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (position == 4) {
            countupdate = 0;
        } else if (position == 3) {
            countupdate = 1;
        } else if (position == 2) {
            countupdate = 2;
        } else if (position == 1) {
            countupdate = 3;
        } else if (position == 0) {
            countupdate = 4;
        }

        position1 = position;
        if (position1 == 0) {
            tvHomeTitle_notif.setText("Alexander");
            tv_user_notif_name.setText("Alexander");
            iv_for_not_status.setImageResource(R.drawable.ic_active_red_circle);
            name = "Alexander";
        } else if (position1 == 1) {
            tvHomeTitle_notif.setText("Alizee");
            tv_user_notif_name.setText("Alizee");
            iv_for_not_status.setImageResource(R.drawable.ic_active_ylw_circle);
            name = "Alizee";
        } else if (position1 == 2) {
            tvHomeTitle_notif.setText("Amy");
            tv_user_notif_name.setText("Amy");
            iv_for_not_status.setImageResource(R.drawable.ic_active_red_circle);
            name = "Amy";
        } else if (position1 == 3) {
            tvHomeTitle_notif.setText("Dhan-mein");
            tv_user_notif_name.setText("Dhan-mein");
            iv_for_not_status.setImageResource(R.drawable.ic_active_ylw_circle);
            name = "Dhan-mein";
        } else if (position1 == 4) {
            tvHomeTitle_notif.setText("Darrin");
            tv_user_notif_name.setText("Darrin");
            name = "Darrin";
            iv_for_not_status.setImageResource(R.drawable.ic_active_red_circle);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.v("onPageSelected", "" + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.v("state", "" + state);
    }
}
