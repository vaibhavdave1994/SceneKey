package com.scenekey.activity.trending_summery;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.scenekey.R;
import com.scenekey.Retrofitprocess.RetrofitClient;
import com.scenekey.activity.Bottomsheet.SummeryBottomSheetDialog;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.OnBoardActivity;
import com.scenekey.activity.TheRoomActivity;
import com.scenekey.activity.trending_summery.Model.SummeryModel;
import com.scenekey.activity.trending_summery.Model.VenueHourModel;
import com.scenekey.activity.trending_summery.adapter.Gallery_Adapter;
import com.scenekey.activity.trending_summery.adapter.Venuehour_Adapter;
import com.scenekey.base.BaseActivity;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.WebServices;
import com.scenekey.model.Events;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Summary_Activity extends BaseActivity implements View.OnClickListener, SummeryBottomSheetDialog.BottomSheetListener {

    private RecyclerView recyclerView;
    private Gallery_Adapter adapter;
    private CustomProgressBar progressBar;
    private ImageView iv_eventimg;
    private ImageView iv_map;
    private ImageView iv_menuarrow;
    private TextView txt_notyet, txt_eventname, txt_content, txt_tag, txt_opentime, txt_eventAdress, txt_event_name;
    private RelativeLayout rl_tag, rl_addedgallery;
    private LinearLayout ll_getdirection;
    private SummeryModel summeryModel;
    private String event_id = "";
    private String venue_id = "";
    private Events object;
    private String[] currentLatLng;
    private boolean fromTrending = false;
    private List<VenueHourModel> venueHourModelList;
    private View view1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_);
        progressBar = new CustomProgressBar(this);
        inItView();
        getIntentData();
        getSummeryDataApiData();

    }

    private void getIntentData() {

        venue_id = getIntent().getStringExtra("venue_id");

        if (venue_id != null &&!venue_id.equals("")) {
            SceneKey.sessionManager.putMapFragment("");
        }

        event_id = getIntent().getStringExtra("event_id");
        object = (Events) getIntent().getSerializableExtra("object");
        currentLatLng = (String[]) getIntent().getSerializableExtra("currentLatLng");
        if (currentLatLng == null) {
            currentLatLng = new String[]{userInfo().lat, userInfo().longi};
        }
        fromTrending = getIntent().getBooleanExtra("fromTrending", false);

    }

    private void inItView() {
        recyclerView = findViewById(R.id.recycler_view);
        iv_eventimg = findViewById(R.id.iv_eventimg);
        txt_eventname = findViewById(R.id.txt_eventname);
        txt_content = findViewById(R.id.txt_content);
        rl_tag = findViewById(R.id.rl_tag);
        txt_tag = findViewById(R.id.txt_tag);
        txt_opentime = findViewById(R.id.txt_opentime);
        ll_getdirection = findViewById(R.id.ll_getdirection);
        txt_eventAdress = findViewById(R.id.txt_eventAdress);
        iv_map = findViewById(R.id.iv_map);
        rl_addedgallery = findViewById(R.id.rl_addedgallery);
        ImageView img_back = findViewById(R.id.img_back);
        txt_event_name = findViewById(R.id.txt_event_name);
        ImageView img_dot = findViewById(R.id.img_dot);
        txt_notyet = findViewById(R.id.txt_notyet);
        iv_menuarrow = findViewById(R.id.iv_menuarrow);
        iv_menuarrow = findViewById(R.id.iv_menuarrow);
        LinearLayout ll_menu = findViewById(R.id.ll_menu);
        LinearLayout ll_today = findViewById(R.id.ll_today);
        RelativeLayout rl_map = findViewById(R.id.rl_map);
        view1 = findViewById(R.id.view);
        setClicks(img_back, img_dot, ll_menu, rl_map, ll_today);
    }

    private void setClicks(View... views) {
        for (View view : views) view.setOnClickListener(this);
    }

    private void gallaryAdapter(SummeryModel summeryModel) {

        if (summeryModel.getEvent().getFeedPost() != null) {
            if (summeryModel.getEvent().getFeedPost().size() == 0) {
                rl_addedgallery.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                rl_addedgallery.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
        adapter = new Gallery_Adapter(Summary_Activity.this, summeryModel.getEvent().getFeedPost());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Summary_Activity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }


    public void getSummeryDataApiData() {
        progressBar.show();
        Call<ResponseBody> call = RetrofitClient.getInstance()
                .getAnotherApi().eventSummary(event_id, venue_id);

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
                            JSONObject jsonObject = new JSONObject(stresult);
                            String status = jsonObject.optString("status");
                            if (status.equals("success")) {

                                Gson gson = new Gson();
                                summeryModel = gson.fromJson(stresult, SummeryModel.class);
                                putSummeryData(summeryModel);
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
                                Toast.makeText(Summary_Activity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void putSummeryData(SummeryModel summeryModel) {


        gallaryAdapter(summeryModel);
        Glide.with(this).load(summeryModel.getEvent().getImage())
                .thumbnail(0.5f)
                .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.sk_logo_image)
                .error(R.drawable.sk_logo_image)
                .into(iv_eventimg);

        if (venue_id != null && !venue_id.equals("")) {
            txt_eventname.setText(summeryModel.getEvent().getVenue_name());

        } else {
            txt_eventname.setText(summeryModel.getEvent().getEvent_name());

        }
        txt_eventAdress.setText(summeryModel.getEvent().getVenue_address());
//        txt_event_name.setText(summeryModel.getEvent().getEvent_name());


        venueHourModelList = new ArrayList<>();
        venueHourModelList.add(new VenueHourModel("Monday", summeryModel.getEvent().getVenue_hour().get(0).getMonday()));
        venueHourModelList.add(new VenueHourModel("Tuesday", summeryModel.getEvent().getVenue_hour().get(0).getTuesday()));
        venueHourModelList.add(new VenueHourModel("Wednesday", summeryModel.getEvent().getVenue_hour().get(0).getWednesday()));
        venueHourModelList.add(new VenueHourModel("Thursday", summeryModel.getEvent().getVenue_hour().get(0).getThursday()));
        venueHourModelList.add(new VenueHourModel("Friday", summeryModel.getEvent().getVenue_hour().get(0).getFriday()));
        venueHourModelList.add(new VenueHourModel("Saturday", summeryModel.getEvent().getVenue_hour().get(0).getSaturday()));
        venueHourModelList.add(new VenueHourModel("Sunday", summeryModel.getEvent().getVenue_hour().get(0).getSunday()));


        if (summeryModel.getEvent().getMenu().isEmpty()) {
            txt_notyet.setVisibility(View.VISIBLE);
            iv_menuarrow.setVisibility(View.GONE);
        } else {
            txt_notyet.setVisibility(View.GONE);
            iv_menuarrow.setVisibility(View.VISIBLE);

        }

        if (summeryModel.getEvent().getDescription().isEmpty()) {

            txt_content.setVisibility(View.GONE);
            rl_tag.setVisibility(View.VISIBLE);
            txt_tag.setText(summeryModel.getEvent().getVenue_type());


        } else {
            rl_tag.setVisibility(View.GONE);
            txt_content.setVisibility(View.VISIBLE);
            txt_content.setText(summeryModel.getEvent().getDescription());
        }


        if (summeryModel.getEvent().getOpen_today().contains("-")) {
            String[] separated = summeryModel.getEvent().getOpen_today().split("-");
            String open = separated[0];
            String close = separated[1];

            if (open.length() == 2 && close.length() == 2) {
                char open1 = open.charAt(0);
                char close1 = close.charAt(0);
                char openap = open.charAt(1);
                char closeap = close.charAt(1);

                txt_opentime.setText(String.format("%s-%s", String.format("0%s:00" + openap, open1), String.format("0%s:00" + closeap, close1)));
            } else if (open.length() == 2 && close.length() == 3) {
                char open1 = open.charAt(0);
                char openap = open.charAt(1);
                String close2 = close.substring(0, 2);
                char closeap = close.charAt(2);
                txt_opentime.setText(String.format("%s-%s", String.format("0%s:00" + openap, open1), String.format("0%s:00" + closeap, close2)));

            } else if (open.length() == 3 && close.length() == 2) {
                String open2 = open.substring(0, 2);
                char close1 = close.charAt(0);
                char closeap = close.charAt(1);
                char openap = open.charAt(2);
                txt_opentime.setText(String.format("%s-%s", String.format("0%s:00" + openap, open2), String.format("0%s:00" + closeap, close1)));

            } else if (open.length() == 3 && close.length() == 3) {
                String open2 = open.substring(0, 2);
                String close2 = close.substring(0, 2);
                char openap = open.charAt(2);
                char closeap = close.charAt(2);
                txt_opentime.setText(String.format("%s-%s", String.format("%s:00" + openap, open2), String.format("%s:00" + closeap, close2)));

            } else if (open.contains(":") && close.contains(":")) {
                String open3, close3;
                String[] colen1 = open.split(":");
                String value1 = colen1[0];

                String[] colen2 = close.split(":");
                String value2 = colen2[0];


                if (value1.length() == 1) {
                    open3 = String.format("0%s", open);
                } else {
                    open3 = open;
                }
                if (value2.length() == 1) {
                    close3 = String.format("0%s", close);
                } else {
                    close3 = close;

                }


                txt_opentime.setText(String.format("%s-%s", open3, close3));


            } else if (open.contains(":") && !close.contains(":")) {
                String open3, close3 = "";
                String[] colen1 = open.split(":");
                String value1 = colen1[0];

                if (value1.length() == 1) {
                    open3 = String.format("0%s", open);
                } else {
                    open3 = open;
                }


                if (close.length() == 2) {
                    char close1 = close.charAt(0);
                    char closeap = close.charAt(1);

                    close3 = String.format("0%s:00" + closeap, close1);
                } else if (close.length() == 3) {
                    String close2 = close.substring(0, 2);
                    char closeap = close.charAt(2);

                    close3 = String.format("%s:00" + closeap, close2);
                }

                txt_opentime.setText(String.format("%s-%s", open3, close3));


            } else if (!open.contains(":") && close.contains(":")) {
                String open3="", close3;
                String[] colen1 = close.split(":");
                String value1 = colen1[0];

                if (value1.length() == 1) {
                    close3 = String.format("0%s", close);
                } else {
                    close3 = close;
                }


                if (open.length() == 2) {
                    char open1 = open.charAt(0);
                    char openap = open.charAt(1);

                    open3 = String.format("0%s:00" + openap, open1);
                } else if (open.length() == 3) {
                    String open2 = open.substring(0, 2);
                    char openap = open.charAt(2);
                    open3 = String.format("%s:00" + openap, open2);
                }

                txt_opentime.setText(String.format("%s-%s", open3, close3));


            } else {
                txt_opentime.setText(String.format("%s-%s", open, close));
            }

        } else {
            txt_opentime.setText(String.format("%s-%s", summeryModel.getEvent().getOpen_today(), summeryModel.getEvent().getOpen_today()));
        }


    }

    @Override
    public void onBackPressed() {

        if (SceneKey.sessionManager.getBackOrIntent() && SceneKey.sessionManager.getMapFragment().equalsIgnoreCase("trending")) {
            Intent intent = new Intent(Summary_Activity.this, HomeActivity.class);
            intent.putExtra("fromSearch1", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (SceneKey.sessionManager.getMapFragment().equalsIgnoreCase("map")) {
            Intent intent = new Intent(Summary_Activity.this, HomeActivity.class);
            intent.putExtra("fromSearch2", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back: {
                onBackPressed();
            }
            break;
            case R.id.img_dot: {
                SummeryBottomSheetDialog bottomSheet = new SummeryBottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
            break;

            case R.id.ll_menu: {
                if (!summeryModel.getEvent().getMenu().isEmpty()) {
                    Intent intent = new Intent(Summary_Activity.this, MenuActivity.class);
                    intent.putExtra("pdf", summeryModel.getEvent().getMenu());
                    startActivity(intent);

                }
            }
            break;
            case R.id.ll_today: {
                showvenueHourList();
            }
            break;

            case R.id.rl_map: {
                Intent intent = new Intent(Summary_Activity.this, MapActivity.class);
                intent.putExtra("lat", summeryModel.getEvent().getVenue_lat());
                intent.putExtra("long", summeryModel.getEvent().getVenue_long());
                intent.putExtra("img", summeryModel.getEvent().getImage());
                intent.putExtra("vname", summeryModel.getEvent().getVenue_name());
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void onButtonClicked(String text) {

        switch (text) {
            case "board": {
                Intent intent = new Intent(Summary_Activity.this, OnBoardActivity.class);
                intent.putExtra("eventid", object.getEvent());
                intent.putExtra("venuid", object.getVenue());
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                if (fromTrending) {
                    intent.putExtra("fromTrending", true);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    finish();
                } else {
                    startActivity(intent);
                }

            }
            break;
            case "people": {

                Intent intent1 = new Intent(this, TheRoomActivity.class);
                intent1.putExtra("noMemberYet", "No");
                intent1.putExtra("fromTrendingHome", object.getEvent().keyInUserModalList);
                intent1.putExtra("object", object);
                intent1.putExtra("currentLatLng", currentLatLng);
                if (fromTrending) {
                    intent1.putExtra("fromTrending", true);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
//                    finish();
                } else {
                    startActivity(intent1);
                }
                //}

            }
            break;

            case "post": {
                callCheckEventStatusApi(object.getEvent().event_name, object.getEvent().event_id, object.getVenue(), object, currentLatLng
                        , new String[]{object.getVenue().getLatitude(), object.getVenue().getLongitude()});

            }
            break;

        }

    }

    private void callCheckEventStatusApi(final String event_name, final String event_id, final Venue venue_name, final Events object, final String[] currentLatLng, final String[] strings) {
        final Utility utility = new Utility(this);
        showProgDialog(false, "");
        if (utility.checkInternetConnection()) {
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.CHECK_EVENT_STATUS, new Response.Listener<String>() {
                @Override
                public void onResponse(String Response) {
                    // get response
                    JSONObject jsonObject;
                    try {
                        dismissProgDialog();
                        jsonObject = new JSONObject(Response);

                        String status = jsonObject.getString("status");
                        boolean isKeyInAble;

                        // If Not exist then isKeyInAble is false
                        // If exist then isKeyInAble is true
                        isKeyInAble = !status.equals("not exist");

                        if (!isKeyInAble && userInfo().key_points.equals("0")) {
                            Toast.makeText(Summary_Activity.this, "Sorry! you have run out of key points! Earn more by connecting on the scene!", Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(Summary_Activity.this, EventDetailsActivity.class);
                            intent.putExtra("event_id", event_id);
                            intent.putExtra("fromTab", "trending");
                            intent.putExtra("venueName", venue_name.getVenue_name());
                            intent.putExtra("currentLatLng", currentLatLng);
                            intent.putExtra("event_name", event_name);
                            intent.putExtra("object", object);
                            intent.putExtra("venueId", venue_name.getVenue_id());
                            if (fromTrending) {
                                intent.putExtra("fromTrending", true);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
//                                finish();
                            } else {
                                startActivity(intent);
                            }

                         /*   Event_Fragment fragment = Event_Fragment.newInstance("trending");
                            fragment.setData(event_id, venue_name, object, currentLatLng, strings, isKeyInAble);
                            activity.addFragment(fragment, 0);*/
                        }

                    } catch (Exception ex) {
                        dismissProgDialog();
                        ex.printStackTrace();
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
                    params.put("eventid", event_id);
                    params.put("userid", userInfo().userid);

                    return params;
                }
            };
            VolleySingleton.getInstance(Summary_Activity.this).addToRequestQueue(request);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            Utility.showCheckConnPopup(this, "No network connection", "", "");
            Toast.makeText(Summary_Activity.this, getString(R.string.internetConnectivityError), Toast.LENGTH_SHORT).show();
            dismissProgDialog();
        }
    }

  /*  public void showvenueHourList() {

        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.venuhourlayout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);


        RecyclerView recycler_view = dialog.findViewById(R.id.recycler_view);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);


        Venuehour_Adapter adapter = new Venuehour_Adapter(Summary_Activity.this, venueHourModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Summary_Activity.this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show location settings when the user acknowledges the alert dialog
                dialog.cancel();
            }
        });

        dialog.show();
    }*/


    private void showvenueHourList() {
        final Dialog dialog = new Dialog(Summary_Activity.this);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();
        dialog.setContentView(R.layout.venuhourlayout);
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        view1.setVisibility(View.VISIBLE);

        RecyclerView recycler_view = dialog.findViewById(R.id.recycler_view);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);


        Venuehour_Adapter adapter = new Venuehour_Adapter(Summary_Activity.this, venueHourModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Summary_Activity.this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show location settings when the user acknowledges the alert dialog
                view1.setVisibility(View.GONE);
                dialog.cancel();
            }
        });

        dialog.show();

    }

}
