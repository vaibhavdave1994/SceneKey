package com.scenekey.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.adapter.SubCatergoryApdater;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.WebServices;
import com.scenekey.model.TagModal;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchSubCategoryActivity extends BaseActivity implements View.OnClickListener {

    private CustomProgressBar customProgressBar;
    private ArrayList<TagModal> tag_list;
    RecyclerView tag_sub_recycler_view;
    private UserInfo userInfo;
    SubCatergoryApdater subCatergoryApdater;
    TagModal intentTagModal;
    String catId = "";
    boolean fromSpecial = false;
    ArrayList<TagModal> object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sub_category);

        inItView();
    }

    private void inItView() {
        customProgressBar = new CustomProgressBar(this);
        tag_list = new ArrayList<>();
        ImageView img_back = findViewById(R.id.img_back);
        ImageView tag_image = findViewById(R.id.tag_image);
        tag_sub_recycler_view = findViewById(R.id.tag_sub_recycler_view);
        TextView txt_f1_title = findViewById(R.id.txt_f1_title);
        userInfo = SceneKey.sessionManager.getUserInfo();

        if (getIntent().getSerializableExtra("tagModal") != null) {
            intentTagModal = (TagModal) getIntent().getSerializableExtra("tagModal");}
        if (getIntent().getBundleExtra("BUNDLE") != null){
            Bundle args = getIntent().getBundleExtra("BUNDLE");
            object = (ArrayList<TagModal>) args.getSerializable("ARRAYLIST");}
            catId =  getIntent().getStringExtra("catId");
            fromSpecial =  getIntent().getBooleanExtra("fromSpecial",false);
            txt_f1_title.setText("" + intentTagModal.tag_name);

            Picasso.with(this).load(intentTagModal.tag_image).fit().centerCrop()
                    .placeholder(R.drawable.app_icon)
                    .error(R.drawable.app_icon)
                    .into(tag_image);


        setOnClick(img_back);

        getSearchTagList(catId);

        tag_sub_recycler_view.setLayoutManager(new LinearLayoutManager(SearchSubCategoryActivity.this));
        if (object != null)
        {
            subCatergoryApdater = new SubCatergoryApdater(SearchSubCategoryActivity.this, object);
            tag_sub_recycler_view.setAdapter(subCatergoryApdater);

        }
        else {
            subCatergoryApdater = new SubCatergoryApdater(SearchSubCategoryActivity.this, tag_list);
            tag_sub_recycler_view.setAdapter(subCatergoryApdater);

        }

       /* if(catId != null)
        getSearchTagList(catId);*/
    }

    private void setOnClick(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                    }

                    if (status.equals("success")) {
                        dismissProgDialog();
                        JSONArray tagList = jo.getJSONArray("tagList");

                        for (int i = 0; i < tagList.length(); i++) {

                            TagModal tagModal = new TagModal();
                            JSONObject jsonObject = tagList.getJSONObject(i);
                            tagModal.biz_tag_id = jsonObject.getString("biz_tag_id");
                            tagModal.tag_name = jsonObject.getString("tag_name");
                            tagModal.color_code = jsonObject.getString("color_code");
                            tagModal.tag_text = jsonObject.getString("tag_text").trim();
                            tagModal.tag_image = jsonObject.getString("tag_image");

//                            if(!tagModal.tag_name.equalsIgnoreCase("Happy Hour")){
//                               tagModal.tag_text = tagModal.tag_name;
////                            }
                            if (intentTagModal.tag_text.equalsIgnoreCase("Happy Hour") ){
                                String text_array[]= tagModal.tag_text.split("_");
                                for(int j =0; j<text_array.length; j++){
                                    TagModal tagModalNew = new TagModal();
                                    tagModalNew.tag_text = text_array[j];
                                    tagModalNew.biz_tag_id = jsonObject.getString("biz_tag_id");
                                    tagModalNew.tag_name = jsonObject.getString("tag_name");
                                    tagModalNew.color_code = jsonObject.getString("color_code");
                                    tagModalNew.tag_image = jsonObject.getString("tag_image");
                                    tag_list.add(tagModalNew);
                                }
                            }
                            else {
                                System.out.println("tagtext : "+intentTagModal.tag_text);
                                if (tagModal.biz_tag_id.equalsIgnoreCase(intentTagModal.biz_tag_id)){
                                    String text_array[]= tagModal.tag_text.split("_");
                                    for(int j =0; j<text_array.length; j++){
                                        TagModal tagModalNew = new TagModal();
                                        tagModalNew.tag_text = text_array[j];
                                        tagModalNew.biz_tag_id = jsonObject.getString("biz_tag_id");
                                        tagModalNew.tag_name = jsonObject.getString("tag_name");
                                        tagModalNew.color_code = jsonObject.getString("color_code");
                                        tagModalNew.tag_image = jsonObject.getString("tag_image");
                                       // tagModal.tag_text = text_array[j];
                                        tag_list.add(tagModalNew);
                                    }
                                }
                            }
                        }
                        subCatergoryApdater.notifyDataSetChanged();

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
                params.put("category_id", cat_id);
                params.put("lat", userInfo.lat);
                params.put("long", userInfo.longi);
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
}
