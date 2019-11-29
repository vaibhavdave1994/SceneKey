package com.scenekey.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.scenekey.R;
import com.scenekey.Retrofitprocess.RetrofitClient;
import com.scenekey.adapter.ProfileImagePagerAdapter;
import com.scenekey.base.BaseActivity;
import com.scenekey.fragment.Event_Fragment;
import com.scenekey.fragment.Key_In_Event_Fragment;
import com.scenekey.helper.CustomProgressBar;
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
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileOtherUserNewActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = ProfileOtherUserNewActivity.class.toString();
    View v;
    String userImage = "", otheruserimage, otherusername, bio = "",useridother;
    ArrayList<BucketDataModel> alOfBucketData;
    //---new code------
    CircleImageView outerBouder, outerBouder1, outerBouder2, outerBouder3, outerBouder4;
    ImageView iv_tag__special_circulerImage, iv_tag__special_circulerImage1, iv_tag__special_circulerImage2, iv_tag__special_circulerImage3,
            iv_tag__special_circulerImage4;
    TextView tag__special_name, tag__special_name1, tag__special_name2, tag__special_name3, tag__special_name4;
    RelativeLayout rl, rl1, rl2, rl3, rl4, rl_error;
    TextView tv_viewall_interest, follow_tokens;
    LinearLayout ll_donothavebio;
    TagModal tagModal, tagModal1, tagModal2, tagModal3, tagModal4;
    RelativeLayout toolbar;
    AppCompatImageView img_f11_back;
    TextView txt_event_name;
    AppCompatImageView iv_report;
    private String otheruserid;
    private Context context;
    private Utility utility;
    private CognitoCredentialsProvider credentialsProvider;
    private EventAttendy attendy;
    private boolean myProfile;
    private Event_Fragment event_fragment;
    private Key_In_Event_Fragment key_in_event_fragment;
    private CustomProgressBar progressBar;
    private ArrayList<Feeds> feedsList;
    private ArrayList<ImagesUpload> imageList;
    private int currentImage, pageToshow = 1;
    private boolean clicked;
    private TextView tv_user_name;
    private SessionManager sessionManager;
    private TextView tv_bio;
    private ImageView btn1, btn2, btn3, btn4, btn5;
    private int profilePos;
    private RelativeLayout rl_view;
    //private RelativeLayout customizeView;
    private ProfileImagePagerAdapter pagerAdapter;
    private LinearLayout demo_View_dot;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_profile);
        context = this;
        utility = new Utility(context);
        sessionManager = new SessionManager(this);
        progressBar = new CustomProgressBar(this);
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        txt_event_name = findViewById(R.id.txt_event_name);
        toolbar = findViewById(R.id.toolbar);
        img_f11_back = findViewById(R.id.img_f11_back);

        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnTouchListener(new OnDragTouchListener(relativeLayout));

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
        rl_error = findViewById(R.id.rl_error);
        tv_viewall_interest = findViewById(R.id.tv_viewall_interest);
        tv_viewall_interest.setOnClickListener(this);

        rl = findViewById(R.id.rl);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);



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
        rl_view = findViewById(R.id.rl_view);


        getMyFollowTag();
        setProfileData();
        getOtherProfileApiData();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                onBackPressed();
            }
            break;
            case R.id.tv_viewall_interest:
                Intent intent = new Intent(context, TagsOtherActivity.class);
                intent.putExtra("otheruserid", useridother);
                intent.putExtra("fromProfile", true);
                intent.putExtra("otherusername", otherusername);
                startActivity(intent);
                break;

        }
    }

    private void setProfileData() {

        Intent intent = getIntent();
        final KeyInUserModal keyInUserModal = (KeyInUserModal) intent.getSerializableExtra("keyInUserModal");

        if (keyInUserModal != null) {
            tv_user_name.setText(keyInUserModal.userName);
            txt_event_name.setText(keyInUserModal.userName);
            otherusername = keyInUserModal.userName;

            otheruserimage = keyInUserModal.userImage;
            otheruserid = keyInUserModal.userFacebookId;
            useridother = keyInUserModal.userid;
            if (!keyInUserModal.bio.isEmpty()) {
                bio = keyInUserModal.bio;
            }


            if (keyInUserModal.bio.equals("")) {
                ll_donothavebio.setVisibility(View.VISIBLE);
                tv_bio.setVisibility(View.GONE);
            } else {
                tv_bio.setText(bio);
                ll_donothavebio.setVisibility(View.GONE);
                tv_bio.setVisibility(View.VISIBLE);


            }
        }

        iv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileOtherUserNewActivity.this, ReportActivity.class);
                intent.putExtra("reportUser", keyInUserModal.userName);
                startActivityForResult(intent, 2);
            }
        });
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

        Collections.reverse(imageList);
        pagerAdapter = new ProfileImagePagerAdapter(context, imageList, new ProfileImageListener() {
            @Override
            public void getProfilePostion(int pos) {
                profilePos = pos;
            }
        });

        viewPager.setAdapter(pagerAdapter);


        if (imageList.size() > 1 ) {
            userImage = SceneKey.sessionManager.getUserInfo().getUserImage();
            if (userImage != null) {

                for (int i = 0; i < imageList.size(); i++) {

                    if (otheruserimage.equals(imageList.get(i).getKey())) {
                        profilePos = i;
                        break;
                    }

                }

                viewPager.setCurrentItem(profilePos);
                initButton(profilePos);
            }
        }
        else {

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


    private void getOtherProfileApiData() {
        progressBar.show();
        Call<ResponseBody> call = RetrofitClient.getInstance()
                .getAnotherApi().getBucketData(otheruserid);

        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                try {
                    progressBar.dismiss();
                    switch (response.code()) {
                        case 200: {
                            String stresult = Objects.requireNonNull(response.body()).string();
                            Log.d("response", stresult);
                            alOfBucketData = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(stresult);
                            if (jsonObject.has("bucketInfo")) {
                                imageList = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONArray("bucketInfo");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                             if(jsonObject.has("Key"))
                                    String Key = jsonObject1.getString("Key");
                                    String LastModified = jsonObject1.getString("LastModified");
                                    String ETag = jsonObject1.getString("ETag");
                                    String Size = String.valueOf(jsonObject1.getInt("Size"));
                                    String StorageClass = jsonObject1.getString("StorageClass");
                                    JSONObject Owner = jsonObject1.getJSONObject("Owner");

                                    String DisplayName = Owner.getString("DisplayName");
                                    String ID = Owner.getString("ID");
                                    OwnerModel ownerModel = new OwnerModel(DisplayName, ID);
                                    alOfBucketData.add(new BucketDataModel(Key, LastModified, ETag, Size,
                                            StorageClass, ownerModel));

                                    imageList.add(new ImagesUpload(Key));
                                }
                                setUpView();
                            }

                            break;
                        }
                        case 400: {
                            String result = Objects.requireNonNull(response.errorBody()).string();
                            Log.d("response400", result);
                            JSONObject jsonObject = new JSONObject(result);
                            String statusCode = jsonObject.optString("status");
                            String msg = jsonObject.optString("message");
                            if (statusCode.equals("true")) {
                                Toast.makeText(ProfileOtherUserNewActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case 401:
                            try {
                                Log.d("ResponseInvalid", Objects.requireNonNull(response.errorBody()).string());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                progressBar.dismiss();
            }
        });

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
                    params.put("userId", useridother);
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
        } else {
            //activity.dismissProgDialog();
        }
    }
}

