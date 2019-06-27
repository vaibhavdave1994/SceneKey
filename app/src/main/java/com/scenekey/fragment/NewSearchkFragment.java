package com.scenekey.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.Search_tag_Adapter;
import com.scenekey.adapter.Tags_Adapter;
import com.scenekey.adapter.Tags_SpecialAdapter;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.arc_menu.util.Util;
import com.scenekey.listener.FollowUnfollowLIstner;
import com.scenekey.model.SearchTagModal;
import com.scenekey.model.TagModal;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NewSearchkFragment extends Fragment {

    private RecyclerView search_recycler_view;
    private Context context;
    private ArrayList<SearchTagModal> search_tag_list;
    private Search_tag_Adapter search_tag_adapter;
    private HomeActivity activity;
    private String searchText = "";
    private Utility utility;
    private RelativeLayout searchContianer;
    private String checkTextOrNot = "";
    private ArrayList<TagModal> tag_list;
    private ArrayList<TagModal> tag_listDeactive;
    private ArrayList<TagModal> specialTag_list;
    private Tags_SpecialAdapter tags_specialAdapter;
    private UserInfo userInfo;
    EditText et_serch_post;
    private boolean isSearchable = false;
    View view;
    boolean onBack = false;
    RelativeLayout no_data_found;
    ImageView iv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_searchk, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        inItView(view);
        return view;
    }

    private void inItView(View view) {
        utility = new Utility(context);

        activity.hideKeyBoard();
        userInfo = SceneKey.sessionManager.getUserInfo();
        no_data_found = view.findViewById(R.id.no_data_found);
        search_recycler_view = view.findViewById(R.id.search_recycler_view);
        et_serch_post = view.findViewById(R.id.et_serch_post);
        searchContianer = view.findViewById(R.id.searchContianer);
        iv = view.findViewById(R.id.iv);
        iv.setVisibility(View.GONE);
        search_tag_list = new ArrayList<>();
        tag_list = new ArrayList<>();
        tag_listDeactive = new ArrayList<>();
        specialTag_list = new ArrayList<>();

        textWatcher(et_serch_post);

        search_recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 2));
        search_tag_adapter = new Search_tag_Adapter(context, search_tag_list, activity);
        search_recycler_view.setAdapter(search_tag_adapter);

        getSearchTagList();


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
                tag_list.clear();
                tag_listDeactive.clear();
                searchText = editable.toString();

                if (!searchText.equalsIgnoreCase("")) {
                    isSearchable = true;
                    getTag_searchList(searchText);
                } else {
                    isSearchable = false;
                    getSearchTagList();
                }
            }
        });

    }

    private void getSearchTagList() {

        if (utility.checkInternetConnection()) {

            activity.showProgDialog(true, "TAG");

            StringRequest request = new StringRequest(Request.Method.POST, WebServices.SEARCH_TAG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    activity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);

                        String status = jo.getString("status");
                        if (status.equals("success")) {
                            search_tag_list.clear();
                            activity.dismissProgDialog();
                            JSONArray categoryList = jo.getJSONArray("categoryList");

                            for (int i = 0; i < categoryList.length(); i++) {

                                SearchTagModal searchTagModal = new SearchTagModal();
                                JSONObject jsonObject = categoryList.getJSONObject(i);
                                searchTagModal.cat_id = jsonObject.getString("cat_id");
                                searchTagModal.category_name = jsonObject.getString("category_name");
                                searchTagModal.color_code = jsonObject.getString("color_code");
                                searchTagModal.back_image = jsonObject.getString("back_image");
                                searchTagModal.category_image = jsonObject.getString("category_image");

                                search_tag_list.add(searchTagModal);
                            }
                            // search_tag_adapter.notifyDataSetChanged();

                            search_recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 2));
                            search_tag_adapter = new Search_tag_Adapter(context, search_tag_list, activity);
                            search_recycler_view.setAdapter(search_tag_adapter);
                            System.out.println("special search");
                        }
                        if(HomeActivity.fromSearch) {
                            et_serch_post.setText(HomeActivity.name);
                            HomeActivity.fromSearch = false;

                            et_serch_post.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    et_serch_post.requestFocus();
                                    activity.showSoftKeyboard(et_serch_post);
                                }
                            }, 100);
                        }
                    } catch (Exception e) {
                        activity.dismissProgDialog();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    activity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("lat", userInfo.lat);
                    params.put("long", userInfo.longi);
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));

        } else {
            utility.snackBar(searchContianer, getString(R.string.internetConnectivityError), 0);
        }
    }

    private void getTag_searchList(final String searchText) {

        if (utility.checkInternetConnection()) {

            if (!searchText.isEmpty()) {
                checkTextOrNot = "1";
            } else {
                checkTextOrNot = "0";
            }

            if (checkTextOrNot.equals("0")) {
                activity.showProgDialog(true, "");
            }

            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_SEARCH, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (checkTextOrNot.equals("0")) {
                        activity.dismissProgDialog();
                    }
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        String status = jo.getString("status");
                        if (status.equals("success")) {
                            tag_list.clear();
                            tag_listDeactive.clear();
                            specialTag_list.clear();
                            activity.dismissProgDialog();
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
                                tagModal.tag_name = jsonObjectSTL.getString("tag_name");
                                tagModal.category_name = jsonObjectSTL.getString("category_name");
                                tagModal.color_code = jsonObjectSTL.getString("color_code");
                                tagModal.tag_text = jsonObjectSTL.getString("tag_text");
                                tagModal.tag_image = jsonObjectSTL.getString("tag_image");
                                tagModal.status = jsonObjectSTL.getString("status");
                                tagModal.is_tag_follow = jsonObjectSTL.getString("is_tag_follow");

                                if(jsonObjectSTL.getString("status").equalsIgnoreCase("active")){
                                    System.out.println("specialTag_list : "+specialTag_list.size());
                                    specialTag_list.add(tagModal);
                                    System.out.println("specialTag_list : "+specialTag_list.size());
                                }
//                                else {
//                                    tag_listDeactive.add(tagModal);
//                                }

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
                                tagModalNew.cat_id = jsonObjectSTL.getString("cat_id");
                                tagModalNew.makeOwnItem =true;
                                specialTag_list.add(0,tagModalNew);
                            }
                            tag_list.addAll(specialTag_list);
                            tag_list.addAll(tag_listDeactive);

                            if(tag_list.size()<=0){
                                no_data_found.setVisibility(View.VISIBLE);
                                return;
                            }
                            else {
                                no_data_found.setVisibility(View.GONE);
                            }
                            if(isSearchable) {
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            search_recycler_view.setLayoutManager(mLayoutManager);
                            tags_specialAdapter = new Tags_SpecialAdapter(context, tag_list, new FollowUnfollowLIstner() {
                                @Override
                                public void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int postion) {
                                    tagFollowUnfollow(followUnfollow,biz_tag_id,postion);
                                }
                            });
                                search_recycler_view.setAdapter(tags_specialAdapter);
                                tags_specialAdapter.notifyDataSetChanged();
                                activity.dismissProgDialog();
                                System.out.println("special");
                            }
                        }

                    } catch (Exception e) {
                        activity.dismissProgDialog();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    activity.dismissProgDialog();
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
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
        } else {
            utility.snackBar(searchContianer, getString(R.string.internetConnectivityError), 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (HomeActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if(et_serch_post != null){
            if(!et_serch_post.getText().toString().equalsIgnoreCase("")){
               // et_serch_post.setText(et_serch_post.getText().toString());
            }
            else {
                if(onBack) {
                    getSearchTagList();
                    onBack = false;
                }
            }
        }
        else {
            if(onBack) {
                getSearchTagList();
                onBack = false;
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        onBack = true;
    }

    public void tagFollowUnfollow(final int followUnfollow, final String biz_tag_id, final int pos) {
        utility = new Utility(context);
        if (utility.checkInternetConnection()) {
            activity.showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        activity.dismissProgDialog();
                        if(jo.has("status")){
                            if(jo.getString("status").equalsIgnoreCase("success")){
//                            TagModal tagModal = tag_list.get(pos);
//                            if(followUnfollow == 0){
//                                tagModal.is_tag_follow = "0";
//                            }
//                            else {
//                                tagModal.is_tag_follow = "1";
//                            }
//                            tag_list.set(pos,tagModal);
//                            tags_specialAdapter.notifyItemChanged(pos);
                                getTag_searchList(searchText);
                            }
                        }

                    } catch (Exception e) {
                        activity.dismissProgDialog();
                        Utility.showToast(context, context.getString(R.string.somethingwentwrong), 0);
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
                    params.put("biz_tag_id",biz_tag_id);
                    params.put("follow_status", String.valueOf(followUnfollow));
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            // utility.snackBar(continer, getString(R.string.internetConnectivityError), 0);
            activity.dismissProgDialog();
        }
    }
}
