package com.scenekey.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.facebook.AccessToken;
import com.scenekey.R;

import com.scenekey.adapter.TheDemoRoomAdapter;
import com.scenekey.adapter.TheRoomAdapter;
import com.scenekey.aws_service.Aws_Web_Service;
import com.scenekey.helper.Constant;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.RoomDemoListener;
import com.scenekey.listener.RoomListener;
import com.scenekey.liveSideWork.LiveProfileActivity;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.ImagesUpload;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheRoomActivity extends AppCompatActivity implements View.OnClickListener {

    public static UserInfo userInfo;
    private final String TAG = TheRoomActivity.class.toString();
    public boolean isApiM;
    public ArrayList<ImagesUpload> imageList;
    private ArrayList<EventAttendy> eventAttendyArrayList;
    private ArrayList<Card> rooDemoList;
    private ImageView img_f11_back;
    private TheDemoRoomAdapter theDemoRoomAdapter;
    private String username;
    private ScrollView no_member_yet;
    private String eventId;
    private CognitoCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_room);
        inItView();
    }

    private void inItView() {
        userInfo = SceneKey.sessionManager.getUserInfo();
        imageList = new ArrayList<>();
        no_member_yet = findViewById(R.id.no_member_yet);


        //downloadFileFromS3(eventAttendy, (credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));

        if (getIntent().getStringExtra("noMemberYet") != null) {
            String getValue = getIntent().getStringExtra("noMemberYet");
            if (getValue.equals("No")) {
                no_member_yet.setVisibility(View.VISIBLE);
            } else {
                no_member_yet.setVisibility(View.GONE);
            }
        }

        if (getIntent().getSerializableExtra("commentPesionList") != null) {
            eventAttendyArrayList = (ArrayList<EventAttendy>) getIntent().getSerializableExtra("commentPesionList");
            eventId = getIntent().getStringExtra("eventId");
        }

        if (getIntent().getSerializableExtra("demoRoomarrayList") != null) {
            rooDemoList = (ArrayList<Card>) getIntent().getSerializableExtra("demoRoomarrayList");
        }

        if (getIntent().getStringExtra("userName") != null) {
            username = getIntent().getStringExtra("userName");
        }

        RecyclerView theRoomRecyclerView = findViewById(R.id.theRoomRecyclerView);
        img_f11_back = findViewById(R.id.img_f11_back);
        img_f11_back.setOnClickListener(this);

        if (eventAttendyArrayList != null) {

            TheRoomAdapter theRoomAdapter = new TheRoomAdapter(eventAttendyArrayList, TheRoomActivity.this, eventId, new RoomListener() {
                @Override
                public void getRoomData(int pos, ArrayList<EventAttendy> list, String eventId) {
                    Intent intent = new Intent(TheRoomActivity.this, LiveProfileActivity.class);
                    intent.putExtra("from", "fromTheRoomActivity");
                    intent.putExtra("fromliveRoomadptPostion", pos);
                    intent.putExtra("fromLiveRoomList", list);
                    intent.putExtra("eventId", eventId);
                    startActivityForResult(intent, 2);
                }
            });
            theRoomRecyclerView.setLayoutManager(new GridLayoutManager(TheRoomActivity.this, 3));
            theRoomRecyclerView.setAdapter(theRoomAdapter);
        }

        if (rooDemoList != null) {
            theDemoRoomAdapter = new TheDemoRoomAdapter(rooDemoList, this, new RoomDemoListener() {
                @Override
                public void getRoomDemoData(int pos, ArrayList<Card> cardArrayList) {

                    Intent intent = new Intent(TheRoomActivity.this, DemoProfileActivity.class);
                    intent.putExtra("fromRoomAdapterPostion", pos);
                    intent.putExtra("fromRoomAdapter", cardArrayList);
                    startActivityForResult(intent, 3);
                }
            });
            theRoomRecyclerView.setLayoutManager(new GridLayoutManager(TheRoomActivity.this, 3));
            theRoomRecyclerView.setAdapter(theDemoRoomAdapter);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_f11_back:
                onBackPressed();
                break;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 2) {
                    String requiredValue = data.getStringExtra("incresePoint");
                    if (requiredValue.equals("1")) {
                        incrementKeyPoints("");
                    }
                } else if (requestCode == 3) {
                    String requiredValue = data.getStringExtra("demoRoomIncres");
                    if (requiredValue.equals("1")) {
                        showKeyPoints("+1");
                    }
                }
               /* else if(requestCode == 2){
                    String status = data.getStringExtra("status");
                    if(status.equals("1")){
                       String  mstatus = "1";
                    }else if(status.equals("2")){
                        String  mstatus = "2";
                    }else if(status.equals("3")){
                        String  mstatus = "3";
                    }
                }*/
            }

        } catch (Exception ex) {

        }
    }
}
