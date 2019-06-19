package com.scenekey.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.scenekey.R;
import com.scenekey.adapter.Tags_Adapter;
import com.scenekey.adapter.Tags_SpecialAdapter;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.Pop_Up_Option;
import com.scenekey.helper.Pop_Up_Option_Follow_Unfollow;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.FollowUnfollowLIstner;
import com.scenekey.model.TagModal;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TagsActivity extends AppCompatActivity implements View.OnClickListener {

    private String category_name = "",category_image = "";
    private ArrayList<TagModal> tag_list;
    private ArrayList<TagModal> tag_listDeactive;
    private Tags_Adapter tags_adapter;
    private Tags_SpecialAdapter tags_specialAdapter;
    private CustomProgressBar customProgressBar;
    private RelativeLayout no_data_found;
    private RecyclerView tag_recycler_view;
    private UserInfo userInfo;
    private String searchText = "";
    private String cat_id = "";
    private Utility utility;
    private String checkTextOrNot = "";
    private RelativeLayout mainView;
    private ArrayList<TagModal> specialTag_list;
    RelativeLayout linear;
    boolean fromProfile = false;
    boolean from_category = false;
    RelativeLayout toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        inItView();
        hideKeyBoard();
    }

    private void inItView() {
        utility = new Utility(this);

        userInfo = SceneKey.sessionManager.getUserInfo();
        customProgressBar = new CustomProgressBar(this);

        tag_list = new ArrayList<>();
        tag_listDeactive = new ArrayList<>();
        specialTag_list = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        linear = findViewById(R.id.linear);
        tag_recycler_view = findViewById(R.id.tag_recycler_view);
        no_data_found = findViewById(R.id.no_data_found);
        mainView = findViewById(R.id.mainView);
        ImageView img_tags_back = findViewById(R.id.img_tags_back);
        ImageView tag_image = findViewById(R.id.tag_image);
        TextView txt_f1_title = findViewById(R.id.txt_f1_title);
        EditText et_serch_post = findViewById(R.id.et_serch_post);

        textWatcher(et_serch_post);

        fromProfile = getIntent().getBooleanExtra("fromProfile",false);
        from_category = getIntent().getBooleanExtra("from_category",false);

        if(fromProfile){
            txt_f1_title.setText("My Interests");
            getMyFollowTag();
        }
        else {
            if (getIntent().getStringExtra("cat_id") != null) {
                cat_id = getIntent().getStringExtra("cat_id");
                category_name = getIntent().getStringExtra("category_name");
                category_image = getIntent().getStringExtra("category_image");

//                if(category_name.equalsIgnoreCase("Specials")){
//                    linear.setVisibility(View.GONE);
//                }

                if (!category_name.isEmpty()) {
                    txt_f1_title.setText(category_name);

                    Picasso.with(this).load(category_image).fit().centerCrop()
                            .placeholder(R.drawable.app_icon)
                            .error(R.drawable.app_icon)
                            .into(tag_image);

                } else txt_f1_title.setText("Category");

                if (!cat_id.isEmpty()) {
//                    if(category_name.equalsIgnoreCase("Happy Hour")){
//                        tag_list.clear();
//                        TagModal tagModal = new TagModal();
//                        tagModal.biz_tag_id = "0";
//                        tagModal.tag_name = category_name;
//                        tagModal.color_code  = getIntent().getStringExtra("color_code");
//                        tagModal.tag_text = category_name;
//                        tagModal.tag_image = category_image;
//                        tagModal.is_tag_follow = "0";
//                        tagModal.isVenue = "0";
//                        tag_list.add(tagModal);
//
//                        tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
//                        tags_adapter = new Tags_Adapter(fromProfile,TagsActivity.this, tag_list,cat_id,category_name, new FollowUnfollowLIstner() {
//                            @Override
//                            public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
//                                tagFollowUnfollow(followUnfollow,biz_tag_id,2);
//                            }
//                        });
//                        tag_recycler_view.setAdapter(tags_adapter);
//                    }
//                    else {
                        getSearchTagList(cat_id);
                   // }

                }
            }
        }
        if(from_category){
            et_serch_post.setHint("search "+category_name+" here");
        }

        setOnClick(img_tags_back);
    }

    private void textWatcher(EditText et_serch_post) {

        et_serch_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                searchText = editable.toString();

                if (!searchText.isEmpty()) {

                    if(from_category){
                        tag_list.clear();
                        getSearchListByCategory(searchText);
                        //beginSearch(searchText);
                    }
                    else {
                        if(fromProfile){
                            beginSearch(searchText);
                        }
                        else {
                            toolbar.setVisibility(View.GONE);
                            tag_list.clear();
                            getTag_searchList(searchText);
                        }

                    }

                } else {

                    if(fromProfile){
                        getMyFollowTag();
                    }
                    else {
                        if (!cat_id.isEmpty()) {
//                            if(category_name.equalsIgnoreCase("Happy Hour")){
//                                tag_list.clear();
//                                TagModal tagModal = new TagModal();
//                                tagModal.biz_tag_id = "0";
//                                tagModal.tag_name = category_name;
//                                tagModal.color_code  = getIntent().getStringExtra("color_code");
//                                tagModal.tag_text = category_name;
//                                tagModal.tag_image = category_image;
//                                tagModal.is_tag_follow = "0";
//                                tagModal.isVenue = "0";
//                                tag_list.add(tagModal);
//
//                                tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
//                                tags_adapter = new Tags_Adapter(fromProfile,TagsActivity.this, tag_list,cat_id,category_name, new FollowUnfollowLIstner() {
//                                    @Override
//                                    public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
//                                        tagFollowUnfollow(followUnfollow,biz_tag_id,2);
//                                    }
//                                });
//                                tag_recycler_view.setAdapter(tags_adapter);
//                            }
//                            else {
                                getSearchTagList(cat_id);
                            //}
                        }
                    }
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getTag_searchList(final String searchText) {

        if (utility.checkInternetConnection()) {

            if (!searchText.isEmpty()) {
                checkTextOrNot = "1";
            } else {
                checkTextOrNot = "0";
            }

            if (checkTextOrNot.equals("0")) {
                showProgDialog(true, "");
            }

            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_SEARCH, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (checkTextOrNot.equals("0")) {
                        dismissProgDialog();
                    }
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);

                        String status = jo.getString("status");
                        if (status.equals("success")) {
                            tag_list.clear();
                            dismissProgDialog();
                            JSONArray tagList = jo.getJSONArray("tagList");

                            for (int i = 0; i < tagList.length(); i++) {

                                TagModal tagModal = new TagModal();
                                JSONObject jsonObject = tagList.getJSONObject(i);
                                tagModal.biz_tag_id = jsonObject.getString("biz_tag_id");
                                tagModal.tag_name = jsonObject.getString("tag_name");
                                tagModal.category_name = jsonObject.getString("category_name");
                                tagModal.color_code = jsonObject.getString("color_code");
                                tagModal.tag_text = jsonObject.getString("tag_text");
                                tagModal.tag_image = jsonObject.getString("tag_image");
                                tagModal.status = jsonObject.getString("status");
                                tagModal.is_tag_follow = jsonObject.getString("is_tag_follow");

                                if(jsonObject.getString("status").equalsIgnoreCase("active")){
                                    tag_list.add(tagModal);
                                }
                                else {
                                    tag_listDeactive.add(tagModal);
                                }

                                Log.v("tag_list", "" + tag_list.size());
                            }

                            JSONArray specialTagList = jo.getJSONArray("specialTagList");

                            for (int i = 0; i < specialTagList.length(); i++) {

                                TagModal tagModal = new TagModal();
                                JSONObject jsonObjectSTL = specialTagList.getJSONObject(i);
                                tagModal.biz_tag_id = jsonObjectSTL.getString("biz_tag_id");
                                tagModal.tag_text = jsonObjectSTL.getString("tag_name");
                                tagModal.category_name = jsonObjectSTL.getString("category_name");
                                tagModal.color_code = jsonObjectSTL.getString("color_code");
                                tagModal.tag_name = jsonObjectSTL.getString("tag_text");
                                tagModal.tag_image = jsonObjectSTL.getString("tag_image");
                                tagModal.status = jsonObjectSTL.getString("status");
                                tagModal.is_tag_follow = jsonObjectSTL.getString("is_tag_follow");
                                if(jsonObjectSTL.getString("status").equalsIgnoreCase("active")){
                                    specialTag_list.add(tagModal);
                                }
                                else {
                                    tag_listDeactive.add(tagModal);
                                }

                                Log.v("tag_list2", "" + specialTag_list.size());
                            }

                            if(specialTag_list.size() >0){
                                TagModal tagModalNew = new TagModal();
                                JSONObject jsonObjectSTL = specialTagList.getJSONObject(0);
                                tagModalNew.biz_tag_id = jsonObjectSTL.getString("biz_tag_id");
                                tagModalNew.tag_text = jsonObjectSTL.getString("category_name");
                                tagModalNew.category_name = jsonObjectSTL.getString("category_name");
                                tagModalNew.color_code = jsonObjectSTL.getString("color_code");
                                tagModalNew.tag_name = jsonObjectSTL.getString("tag_name");
                                tagModalNew.tag_image = jsonObjectSTL.getString("tag_image");
                                tagModalNew.status = jsonObjectSTL.getString("status");
                                tagModalNew.is_tag_follow = jsonObjectSTL.getString("is_tag_follow");
                                tagModalNew.makeOwnItem =true;
                                specialTag_list.add(0,tagModalNew);
                            }

                            tag_list.addAll(specialTag_list);
                            tag_list.addAll(tag_listDeactive);
                            tag_listDeactive.clear();
                            Log.v("tag_list3", "" + tag_list.size());

                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tag_recycler_view.setLayoutManager(mLayoutManager);
                            tags_specialAdapter = new Tags_SpecialAdapter(TagsActivity.this, tag_list, new FollowUnfollowLIstner() {
                                @Override
                                public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
                                    tagFollowUnfollow(followUnfollow,biz_tag_id,0);
                                }
                            });
                            tag_recycler_view.setAdapter(tags_specialAdapter);
                            tags_specialAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("tag", searchText);
                    params.put("lat", userInfo.lat);
                    params.put("long", userInfo.longi);
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(mainView, getString(R.string.internetConnectivityError), 0);
        }
    }

    private void getSearchListByCategory(final String searchText) {

        if (utility.checkInternetConnection()) {

            if (!searchText.isEmpty()) {
                checkTextOrNot = "1";
            } else {
                checkTextOrNot = "0";
            }

            if (checkTextOrNot.equals("0")) {
                showProgDialog(true, "");
            }

            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_SEARCH_CATEGORY, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (checkTextOrNot.equals("0")) {
                        dismissProgDialog();
                    }
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);

                        String status = jo.getString("status");
                        if (status.equals("success")) {
                            tag_list.clear();
                            dismissProgDialog();
                            JSONArray tagList = jo.getJSONArray("tagList");

                            for (int i = 0; i < tagList.length(); i++) {

                                TagModal tagModal = new TagModal();
                                JSONObject jsonObject = tagList.getJSONObject(i);
                                tagModal.biz_tag_id = jsonObject.getString("biz_tag_id");
                                tagModal.tag_text = jsonObject.getString("tag_name");
                                tagModal.category_name = jsonObject.getString("category_name");
                                tagModal.color_code = jsonObject.getString("color_code");
                                tagModal.tag_name = jsonObject.getString("tag_text");
                                tagModal.tag_image = jsonObject.getString("tag_image");
                                tagModal.status = jsonObject.getString("status");
                                tagModal.is_tag_follow = jsonObject.getString("is_tag_follow");

                                if(jsonObject.getString("status").equalsIgnoreCase("active")){
                                    tag_list.add(tagModal);
                                }
                                else {
                                    tag_listDeactive.add(tagModal);
                                }

                                Log.v("tag_list", "" + tag_list.size());
                            }

                            if(jo.has("specialTagList")) {
                                JSONArray specialTagList = jo.getJSONArray("specialTagList");

                                for (int i = 0; i < specialTagList.length(); i++) {

                                    TagModal tagModal = new TagModal();
                                    JSONObject jsonObjectSTL = specialTagList.getJSONObject(i);
                                    tagModal.biz_tag_id = jsonObjectSTL.getString("biz_tag_id");
                                    tagModal.tag_text = jsonObjectSTL.getString("tag_name");
                                    tagModal.category_name = jsonObjectSTL.getString("category_name");
                                    tagModal.color_code = jsonObjectSTL.getString("color_code");
                                    tagModal.tag_name = jsonObjectSTL.getString("tag_text");
                                    tagModal.tag_image = jsonObjectSTL.getString("tag_image");
                                    tagModal.status = jsonObjectSTL.getString("status");
                                    tagModal.is_tag_follow = jsonObjectSTL.getString("is_tag_follow");
                                    if (jsonObjectSTL.getString("status").equalsIgnoreCase("active")) {
                                        specialTag_list.add(tagModal);
                                    } else {
                                        tag_listDeactive.add(tagModal);
                                    }

                                    Log.v("tag_list2", "" + specialTag_list.size());
                                }

                            }

                            if(category_name.equalsIgnoreCase("Specials") ||
                                    category_name.equalsIgnoreCase("Happy Hour")){

                                TagModal tagModalNew = new TagModal();
                                JSONObject jsonObjectSTL = tagList.getJSONObject(0);
                                tagModalNew.biz_tag_id = jsonObjectSTL.getString("biz_tag_id");
                                tagModalNew.tag_text = jsonObjectSTL.getString("category_name");
                                tagModalNew.category_name = jsonObjectSTL.getString("category_name");
                                tagModalNew.color_code = jsonObjectSTL.getString("color_code");
                                tagModalNew.tag_name = jsonObjectSTL.getString("tag_name");
                                tagModalNew.tag_image = jsonObjectSTL.getString("tag_image");
                                tagModalNew.status = jsonObjectSTL.getString("status");
                                tagModalNew.is_tag_follow = jsonObjectSTL.getString("is_tag_follow");
                                tagModalNew.makeOwnItem =true;
                                tag_list.add(0,tagModalNew);

                            }
                            tag_list.addAll(specialTag_list);
                            tag_list.addAll(tag_listDeactive);
                            tag_listDeactive.clear();
                            Log.v("tag_list3", "" + tag_list.size());

                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tag_recycler_view.setLayoutManager(mLayoutManager);
                            tags_specialAdapter = new Tags_SpecialAdapter(TagsActivity.this, tag_list, new FollowUnfollowLIstner() {
                                @Override
                                public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
                                    tagFollowUnfollow(followUnfollow,biz_tag_id,0);
                                }
                            });
                            tag_recycler_view.setAdapter(tags_specialAdapter);
                            tags_specialAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("tag", searchText);
                    params.put("lat", userInfo.lat);
                    params.put("long", userInfo.longi);
                    params.put("category_id", cat_id);
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            utility.snackBar(mainView, getString(R.string.internetConnectivityError), 0);
        }
    }

    private void setOnClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void hideKeyBoard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_tags_back:
                onBackPressed();
                break;
        }
    }

    private void getSearchTagList(final String cat_id) {

        showProgDialog(true, "");
        StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAGLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // get response
                try {
                    JSONObject jo = new JSONObject(response);

                    String status = jo.getString("status");

                    if (status.equals("fail")) {
                        dismissProgDialog();
                        no_data_found.setVisibility(View.VISIBLE);
                    }

                    if (status.equals("success")) {
                        no_data_found.setVisibility(View.GONE);
                        dismissProgDialog();
                        JSONArray tagList = jo.getJSONArray("tagList");
                        tag_list = new ArrayList<>();
                        for (int i = 0; i < tagList.length(); i++) {

                            TagModal tagModal = new TagModal();
                            JSONObject jsonObject = tagList.getJSONObject(i);
                            tagModal.biz_tag_id = jsonObject.getString("biz_tag_id");
                            tagModal.tag_name = jsonObject.getString("tag_name");
                            tagModal.color_code = jsonObject.getString("color_code");
                            tagModal.tag_text = jsonObject.getString("tag_text").trim();
                            tagModal.tag_image = jsonObject.getString("tag_image");
                            tagModal.is_tag_follow = jsonObject.getString("is_tag_follow");
                            tagModal.category_name = jsonObject.getString("category_name");
                            tagModal.isVenue = "0";
                            tag_list.add(tagModal);
                        }

                       /* if (!category_name.equals("Specials")) {

                            tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
                            tags_adapter = new Tags_Adapter(TagsActivity.this, tag_list);
                            tag_recycler_view.setAdapter(tags_adapter);

                        } else {

                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tag_recycler_view.setLayoutManager(mLayoutManager);
                            tags_specialAdapter = new Tags_SpecialAdapter(TagsActivity.this, tag_list);
                            tag_recycler_view.setAdapter(tags_specialAdapter);
                        }*/

                        tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
                        tags_adapter = new Tags_Adapter(fromProfile,TagsActivity.this, tag_list,cat_id,category_name, new FollowUnfollowLIstner() {
                            @Override
                            public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
                                tagFollowUnfollow(followUnfollow,biz_tag_id,2);
                            }
                        });
                        tag_recycler_view.setAdapter(tags_adapter);

                    }

                } catch (Exception e) {
                    dismissProgDialog();
                    no_data_found.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                dismissProgDialog();
                no_data_found.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category_id", cat_id);
                params.put("lat", userInfo.lat);
                params.put("long", userInfo.longi);
                params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request, "HomeApi");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
    }

    public void showProgDialog(boolean b, String TAG) {
        try {
            customProgressBar.setCanceledOnTouchOutside(b);
            customProgressBar.setCancelable(b);
            customProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyBoard();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fromProfile){
            getMyFollowTag();
        }
        else {
            if (!cat_id.isEmpty()) {
//                if(category_name.equalsIgnoreCase("Happy Hour")){
//                    tag_list.clear();
//                    TagModal tagModal = new TagModal();
//                    tagModal.biz_tag_id = "0";
//                    tagModal.tag_name = category_name;
//                    tagModal.color_code = getIntent().getStringExtra("color_code");
//                    tagModal.tag_text = category_name;
//                    tagModal.tag_image = category_image;
//                    tagModal.is_tag_follow = "0";
//                    tagModal.isVenue = "0";
//                    tag_list.add(tagModal);
//
//                    tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
//                    tags_adapter = new Tags_Adapter(fromProfile,TagsActivity.this, tag_list,cat_id,category_name, new FollowUnfollowLIstner() {
//                        @Override
//                        public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
//                            tagFollowUnfollow(followUnfollow,biz_tag_id,2);
//                        }
//                    });
//                    tag_recycler_view.setAdapter(tags_adapter);
//                }
//                else {
                    getSearchTagList(cat_id);
                //}
            }
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
                                        tag_list = new ArrayList<>();
                                        for(int i =0; i<jsonArray.length(); i++){
                                            TagModal tagModal = new TagModal();
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                                            tagModal.biz_tag_id = jsonObject.getString("biz_tag_id");
                                            tagModal.tag_name = jsonObject.getString("tag_name");
                                            tagModal.color_code = jsonObject.getString("color_code");
                                            tagModal.category_name = "";
                                            tagModal.tag_text = "";
                                            tagModal.tag_image = jsonObject.getString("tag_image");
                                            tagModal.is_tag_follow = "1";
                                            tagModal.isVenue = jsonObject.getString("isVenue");
                                            tag_list.add(tagModal);
                                        }

                                        tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
                                        tags_adapter = new Tags_Adapter(fromProfile, TagsActivity.this, tag_list,cat_id,category_name, new FollowUnfollowLIstner() {
                                            @Override
                                            public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
                                                tagFollowUnfollow(followUnfollow,biz_tag_id,2);
                                            }
                                        });
                                        tag_recycler_view.setAdapter(tags_adapter);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        //activity.dismissProgDialog();
                        Utility.showToast(TagsActivity.this, getString(R.string.somethingwentwrong), 0);
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
            VolleySingleton.getInstance(TagsActivity.this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
        } else {
            //activity.dismissProgDialog();
        }
    }

    public Pop_Up_Option_Follow_Unfollow initializePopup() {
        return new Pop_Up_Option_Follow_Unfollow(TagsActivity.this) {

            @Override
            public void onFollowSelect(Pop_Up_Option_Follow_Unfollow dialog, Object object) {
                TagModal tagModal = (TagModal) object;
                tagFollowUnfollow(1,tagModal.biz_tag_id,1);
            }

            @Override
            public void onUnFollowSelect(Pop_Up_Option_Follow_Unfollow dialog, Object object) {
                TagModal tagModal = (TagModal) object;
                tagFollowUnfollow(0,tagModal.biz_tag_id,1);
            }
        };
    }

    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final int callFrom) { // 0 from search, 1 for tags long press
        utility = new Utility(TagsActivity.this);
        if (utility.checkInternetConnection()) {
            showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        dismissProgDialog();
                        if(jo.has("status")){
                            if(jo.getString("status").equalsIgnoreCase("success")){

                                if(callFrom == 0) {
                                    if(from_category){
                                        getSearchListByCategory(searchText);
                                    }
                                    else {
                                        getTag_searchList(searchText);
                                    }
                                }
                                else if(callFrom == 1){
                                    if(fromProfile){
                                        getMyFollowTag();
                                    }
                                    else {
                                        if(category_name.equalsIgnoreCase("Happy Hour")){
                                            tag_list.clear();
                                            TagModal tagModal = new TagModal();
                                            tagModal.biz_tag_id = "0";
                                            tagModal.tag_name = category_name;
                                            tagModal.color_code = getIntent().getStringExtra("color_code");
                                            tagModal.tag_text = category_name;
                                            tagModal.tag_image = category_image;
                                            tagModal.is_tag_follow = "0";
                                            tagModal.isVenue = "0";
                                            tag_list.add(tagModal);

                                            tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
                                            tags_adapter = new Tags_Adapter(fromProfile,TagsActivity.this, tag_list,cat_id,category_name, new FollowUnfollowLIstner() {
                                                @Override
                                                public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
                                                    tagFollowUnfollow(followUnfollow,biz_tag_id,2);
                                                }
                                            });
                                            tag_recycler_view.setAdapter(tags_adapter);
                                        }
                                        else {
                                            getSearchTagList(cat_id);
                                        }
                                    }

                                }
                                else {
                                    getMyFollowTag();
                                }
                            }
                        }

                    } catch (Exception e) {
                        dismissProgDialog();
                        Utility.showToast(TagsActivity.this, getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("biz_tag_id",biz_tag_id);
                    params.put("follow_status", String.valueOf(followUnfollow));
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(TagsActivity.this).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            dismissProgDialog();
        }
    }

    public void beginSearch(String newText){
        final ArrayList<TagModal> filteredList = new ArrayList<>();
        for (int i = 0; i < tag_list.size(); i++) {
            final String text = tag_list.get(i).tag_name.toLowerCase();
            if (text.contains(newText)) {
                filteredList.add(tag_list.get(i));
            }
        }

        if(filteredList.size()>0) {
//            switch (searchFrom){
//                case 0:
//                    break;
//                case 1:
//                    tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
//                    tags_adapter = new Tags_Adapter(fromProfile,TagsActivity.this, tag_list,cat_id,category_name, new FollowUnfollowLIstner() {
//                        @Override
//                        public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
//                            tagFollowUnfollow(followUnfollow,biz_tag_id,2);
//                        }
//                    });
//                    tag_recycler_view.setAdapter(tags_adapter);
//                    break;
//            }

            tag_recycler_view.setLayoutManager(new GridLayoutManager(TagsActivity.this, 3));
            tags_adapter = new Tags_Adapter(fromProfile, TagsActivity.this, filteredList,cat_id,category_name, new FollowUnfollowLIstner() {
                @Override
                public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
                    tagFollowUnfollow(followUnfollow,biz_tag_id,2);
                }
            });
            tag_recycler_view.setAdapter(tags_adapter);
        }

    }
}
