package com.scenekey.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class DemoCommentActivity extends AppCompatActivity implements View.OnClickListener {

    private static UserInfo userInfo;
    private final String TAG = DemoCommentActivity.class.toString();
    private ImageView img_f1_backdemo, imgPostdemo;
    private EditText edt_comment_demo;
    private TextView txt_char_demo, txt_char1_demo;
    private int maxNumber = 120;
    private ImageView img_profile_demo;
    private HomeActivity activity;
    private Utility utility;
    private String time = "";
    private String sap_time ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_comment);
        inItView();
    }

    private void inItView() {
        userInfo = SceneKey.sessionManager.getUserInfo();
        activity = new HomeActivity();
        utility = new Utility(this);
        img_f1_backdemo = findViewById(R.id.img_f1_backdemo);
        imgPostdemo = findViewById(R.id.imgPostdemo);
        edt_comment_demo = findViewById(R.id.edt_comment_demo);
        txt_char_demo = findViewById(R.id.txt_char_demo);
        txt_char1_demo = findViewById(R.id.txt_char1_demo);
        img_profile_demo = findViewById(R.id.img_profile_demo);

        img_f1_backdemo.setOnClickListener(this);
        imgPostdemo.setOnClickListener(this);


        time = utility.getTimestamp("hh:mm:ss");

        try {
            Utility.e("Profile pic Home", userInfo.getUserImage());

            Picasso.with(this).load(userInfo.getUserImage()).placeholder(R.drawable.image_default_profile).into(img_profile_demo);

        } catch (Exception e) {
            Utility.e("Picasso e", e.toString());
            Utility.e("Picasso e", userInfo.getUserImage());
        }
        workingCode();


        String timeInPMAM = getCurrentTimeInFormat();

        String startTime = timeInPMAM;
        StringTokenizer tk = new StringTokenizer(startTime);
        String time = tk.nextToken();
        String date = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateinPmAm = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date dt = null;
        try {
            dt = sdf.parse(date);
            sap_time = dateinPmAm.format(dt);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public String getCurrentTimeInFormat() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())).format(new Date(System.currentTimeMillis()));
    }


    private void workingCode() {
        edt_comment_demo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txt_char_demo.setText((maxNumber - s.length()) + " ");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_f1_backdemo:
                onBackPressed();
                break;

            case R.id.imgPostdemo:
                if (edt_comment_demo.getText().toString().isEmpty()) {
                    utility.showCustomPopup(getString(R.string.write_something), String.valueOf(R.font.montserrat_medium));

                } else {
                    Card card4 = new Card();
                    card4.name = userInfo.userName;
                    card4.text = edt_comment_demo.getText().toString().trim();
                    //card4.imageint = R.drawable.placeholder_img;
                    card4.userImage = userInfo.getUserImage();
                    card4.date = sap_time;
                    card4.user_status = userInfo.user_status;
                    card4.facebookId = userInfo.userFacebookId;
                    card4.userId = userInfo.userid;
                    TryAndDemoActivity.arrayList.add(0, card4);
                    //onBackPressed();

                    Intent intent = getIntent();
                    intent.putExtra("incresePoint", "1");
                    setResult(RESULT_OK, intent);
                    finish();

                }
                break;
        }
    }
}
