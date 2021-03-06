package com.scenekey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.OnBoardActivity;
import com.scenekey.activity.TheRoomActivity;
import com.scenekey.activity.TrendinSearchActivity;
import com.scenekey.activity.trending_summery.Summary_Activity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.SortByPoint;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.listener.FollowUnfollowLIstner;
import com.scenekey.model.Event;
import com.scenekey.model.Events;
import com.scenekey.model.ImageSlidModal;
import com.scenekey.model.KeyInUserModal;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TrendingSearchAdapter extends RecyclerView.Adapter<TrendingSearchAdapter.ViewHolder> {

    private String TAG = "Trending_Adapter";
    private TrendinSearchActivity activity;
    private ArrayList<Events> eventsArrayList;
    private String[] currentLatLng;
    private boolean clicked;
    private CheckEventStatusListener listener;
    FollowUnfollowLIstner followUnfollowLIstner;
    boolean isclick = false;
    public TrendingSearchAdapter(TrendinSearchActivity activity, ArrayList<Events> eventsArrayList, String[] currentLatLng, CheckEventStatusListener listener,
                                 FollowUnfollowLIstner followUnfollowLIstner) {
        this.activity = activity;
        this.eventsArrayList = eventsArrayList;
        this.currentLatLng = currentLatLng;
        this.listener = listener;
        this.followUnfollowLIstner =followUnfollowLIstner;
    }

    @Override
    public TrendingSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_trending, parent, false);

        return new TrendingSearchAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Events object = eventsArrayList.get(position);
        final Venue venue = object.getVenue();
        final Event event = object.getEvent();

        try {
            AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            blinkanimation.setDuration(1000); // duration - half a second
            blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            blinkanimation.setRepeatMode(Animation.REVERSE);
            holder.rl_anim.startAnimation(blinkanimation);
        }
        catch (Exception e){
            e.printStackTrace();
        }


      /*  if(venue.getIs_tag_follow().equalsIgnoreCase("0")){
            holder.tv_follow.setText("Follow");
            holder.tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_border));
            holder.tv_follow.setTextColor(activity.getResources().getColor(R.color.green));

        }
        else {
            holder.tv_follow.setText("Unfollow");
            holder.tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.unfollow_border));
            holder.tv_follow.setTextColor(activity.getResources().getColor(R.color.red_ring));
        }*/

        if (venue.getIs_tag_follow().equalsIgnoreCase("0")) {
            //holder.iv_add.setImageDrawable(activity.getResources().getDrawable(R.drawable.add_ico));
            holder.tv_follow.setText("Follow");
            holder.tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_border_gray));
            holder.tv_follow.setTextColor(activity.getResources().getColor(R.color.reward_day_color));
//            holder.tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_border));
//            holder.tv_follow.setTextColor(activity.getResources().getColor(R.color.green));
        } else {
            // holder.iv_add.setImageDrawable(activity.getResources().getDrawable(R.drawable.right_tick_ico));
            holder.tv_follow.setText("Unfollow");
            holder.tv_follow.setBackground(activity.getResources().getDrawable(R.drawable.follow_active_gray));
            holder.tv_follow.setTextColor(activity.getResources().getColor(R.color.white));
        }

        if(venue.getIs_tag_follow().equalsIgnoreCase("0")){
//            holder.iv_add.setImageDrawable(activity.getResources().getDrawable(R.drawable.add_ico));
        }
        else {
            holder.iv_add.setImageDrawable(activity.getResources().getDrawable(R.drawable.right_tick_ico));
        }

        Collections.sort(eventsArrayList, new SortByPoint());
        try {
            if (position == eventsArrayList.size() - 1) {
                holder.txt_gap.setVisibility(View.VISIBLE);

            } else {
                holder.txt_gap.setVisibility(View.GONE);
            }

            Log.v("keyInUser", "" + event.keyInUserModalList);

            if (event.keyInUserModalList.size() != 0) {
                holder.iv_group.setVisibility(View.GONE);
                holder.parent.setVisibility(View.VISIBLE);
                setRecyclerView(holder, event.keyInUserModalList,object);
            } else {
                holder.parent.setVisibility(View.GONE);
                holder.iv_group.setVisibility(View.VISIBLE);
            }
            //for IMage
        } catch (Exception e) {
            //Picasso.with(activity).load(event.getImage().contains("defaultevent.jpg") ? venue.getImage() : event.getImage()).placeholder(R.drawable.transparent).into(holder.img_event);
            e.printStackTrace();
        }

        String[] hhmmss = object.getEvent().event_time.split(":");
        String a = null;
        if (Integer.valueOf(hhmmss[0]) > 12) {
            a = " PM";
            hhmmss[0] = String.valueOf(Integer.valueOf(hhmmss[0]) - 12);
        } else {
            a = " AM";
        }
        String mTime = hhmmss[0] + ":" + hhmmss[1] + a;


        // Old Code
        if (object.getEvent().strStatus == 0) {
            holder.txt_time.setVisibility(View.GONE);
            holder.txt_live.setVisibility(View.VISIBLE);
            holder.green_dot.setImageResource(R.drawable.abc_dot);


        } else {
            holder.txt_time.setVisibility(View.VISIBLE);
            holder.txt_live.setVisibility(View.GONE);
//            holder.green_dot.setImageResource(R.drawable.gray_dot);
            holder.green_dot.setVisibility(View.GONE);
            holder.txt_time.setText(mTime);
        }

        if (object.getEvent().isEventLike.equals("1")) {
            holder.iv_heart.setImageResource(R.drawable.active_like_ico);
        } else {
            holder.iv_heart.setImageResource(R.drawable.inactive_like_ico);
        }

        if (!object.getEvent().likeCount.isEmpty()) {
            holder.like_count_txt.setText(object.getEvent().likeCount);
        }

        holder.txt_eventName.setText(event.event_name);
        holder.txt_eventAdress.setText((venue.getVenue_name().trim().length() > 29 ? venue.getVenue_name().trim().substring(0, 29) : venue.getVenue_name().trim()));

        //distance bw event and user
        String miles1 = String.valueOf(getDistanceMile(new Double[]{Double.valueOf(venue.getLatitude()), Double.valueOf(venue.getLongitude()), Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}));
        final int distance = getDistance(new Double[]{Double.valueOf(object.getVenue().getLatitude()), Double.valueOf(object.getVenue().getLongitude()), Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])});
        Utility.e("Miles---", miles1);
        if (Double.parseDouble(miles1) == 0.0)
        {
            holder.txt_eventmile.setText(miles1 +"0" + " M");

        }else if (miles1.length() == 3){
            holder.txt_eventmile.setText(String.format("%.2f", Double.parseDouble(miles1))+ " M");


        }
        else {
            holder.txt_eventmile.setText(miles1 + " M");

        }

        if(distance < Constant.MAXIMUM_DISTANCE && object.getEvent().strStatus != 2){
            holder.frame_keyinbutton.setVisibility(View.VISIBLE);
            event.ableToKeyIn = true;
        }
        else {
            holder.frame_keyinbutton.setVisibility(View.GONE);
            event.ableToKeyIn = false;
        }


        holder.iv_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_Api(object.getEvent().event_id, holder, object,position);
            }
        });

        holder.iv_note_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, OnBoardActivity.class);
                intent.putExtra("eventid", event);
                intent.putExtra("venuid", venue);
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                activity.startActivity(intent);
            }
        });

        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setClickable(false);
                if (!clicked) {
                    try {
                        activity.showProgDialog(false, TAG);
                        listener.getCheckEventStatusListener(event.event_name, event.event_id, venue, object, currentLatLng, new String[]{venue.getLatitude(), venue.getLongitude()});

                    } catch (NullPointerException e) {
                        activity.dismissProgDialog();
                        e.printStackTrace();
                    }
                    clicked = true;
                }
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setClickable(true);
                            clicked = false;
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.iv_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TheRoomActivity.class);
                intent.putExtra("fromTrendingHome", event.keyInUserModalList);
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                intent.putExtra("fromTrending", true);
                activity.startActivity(intent);
            }
        });

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(object.getVenue().getIs_tag_follow().equalsIgnoreCase("0")){
                    followUnfollowLIstner.getFollowUnfollow(1,object.getVenue().getBiz_tag_id(),object,position);
//                    tagFollowUnfollow(1,object.getVenue().getBiz_tag_id(),position);
                }
                else {
                    followUnfollowLIstner.getFollowUnfollow(0,object.getVenue().getBiz_tag_id(),object,position);
//                    tagFollowUnfollow(0,object.getVenue().getBiz_tag_id(),position);
                }
            }
        });

        holder.tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(object.getVenue().getIs_tag_follow().equalsIgnoreCase("0")){
                    followUnfollowLIstner.getFollowUnfollow(1,object.getVenue().getBiz_tag_id(),object,position);
                }
                else {
                    followUnfollowLIstner.getFollowUnfollow(0,object.getVenue().getBiz_tag_id(),object,position);
                }
            }
        });


        if (event.imageslideList.isEmpty()) {
            if (event.getImage().contains("defaultevent.jpg")) {
                ImageSlidModal imageSlidModal = new ImageSlidModal();
                imageSlidModal.id = "1";
                imageSlidModal.feed_image = venue.getImage();
                event.imageslideList.add(imageSlidModal);
            }
        }

        TrendingFeedSlider trendingFeedSlider = new TrendingFeedSlider(activity, event.imageslideList, event, venue,
                listener, object, currentLatLng, new TrendingFeedSlider.MapListener() {
            @Override
            public void mapFeed(String event_name, String event_id, Venue venue_name, Events object, String[] currentLatLng, String[] strings) {

            }
        });
        holder.viewPager.setAdapter(trendingFeedSlider);

        int listSize = event.imageslideList.size();
        if (listSize == 0 || listSize == 1) {
            holder.indicator_linear_layout.setVisibility(View.GONE);
        } else {
            holder.indicator_linear_layout.setVisibility(View.VISIBLE);
            addBottomDots(holder.indicator_linear_layout, listSize, 0);
            holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    addBottomDots(holder.indicator_linear_layout, event.imageslideList.size(), position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    public int getDistance(Double[] LL) {
        Utility.e("LAT LONG ", LL[0] + " " + LL[1] + " " + LL[2] + " " + LL[3]);
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(LL[0]);
        startPoint.setLongitude(LL[1]);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(LL[2]);
        endPoint.setLongitude(LL[3]);

        double distance = startPoint.distanceTo(endPoint);

        return (int) distance;
    }

    private double getDistanceMile(Double[] LL) {
        Utility.e("LAT LONG ", LL[0] + " " + LL[1] + " " + LL[2] + " " + LL[3]);

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(LL[0]);
        startPoint.setLongitude(LL[1]);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(LL[2]);
        endPoint.setLongitude(LL[3]);

        double distance = (startPoint.distanceTo(endPoint)) * 0.00062137;
        return new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void like_Api(final String event_id, final ViewHolder holder, final Events object,int myPos) {

        activity.showProgDialog(true, "TAG");

        StringRequest request = new StringRequest(Request.Method.POST, WebServices.LIKE_EVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                activity.dismissProgDialog();
                // get response
                try {
                    JSONObject jo = new JSONObject(response);

                    //{"success":1,"msg":"your have liked the event."}

                    if (jo.has("success")) {
                        int status = jo.getInt("success");
                        String msg = jo.getString("msg");
                        int likeCount = Integer.parseInt(object.getEvent().likeCount);
                        if (msg.equals("your have liked the event.")) {
                            int newLikeCount = likeCount + 1;
                            holder.like_count_txt.setText("" + newLikeCount);
                            object.getEvent().likeCount = String.valueOf(newLikeCount);
                            holder.iv_heart.setImageResource(R.drawable.active_like_ico);
                            eventsArrayList.get(myPos).getEvent().isEventLike = "1";
                        } else {
                            int newLikeCount = likeCount - 1;
                            holder.like_count_txt.setText(likeCount >= 0 ? newLikeCount + "" : "0");
                            object.getEvent().likeCount = String.valueOf(newLikeCount);
                            holder.iv_heart.setImageResource(R.drawable.inactive_like_ico);
                            eventsArrayList.get(myPos).getEvent().isEventLike = "0";
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
                params.put("event_id", event_id);
                params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                Utility.e(TAG, " params " + params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(activity).addToRequestQueue(request, "HomeApi");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
    }

    private void addBottomDots(LinearLayout indicator_linear_layout, int size, int i) {
        ImageView[] dots = new ImageView[size];
        indicator_linear_layout.removeAllViews();
        for (int j = 0; j < dots.length; j++) {
            dots[j] = new ImageView(activity);
            dots[j].setImageResource(R.drawable.inactive_dot_img);
            indicator_linear_layout.addView(dots[j]);
        }
        if (dots.length > 0)
            dots[i].setImageResource(R.drawable.dot_ico);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setRecyclerView(ViewHolder holder, final ArrayList<KeyInUserModal> keyInUserModalList, final Events object) {


        CircularImageView comeInUserProfile = null;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder.parent.removeAllViews();
        int loopCount  = keyInUserModalList.size();
        if(loopCount >5){
            loopCount = 5;
        }
        for (int i = 0; i < loopCount ; i++) {

            assert inflater != null;
            View v = inflater.inflate(R.layout.trend_user_view, null);
            comeInUserProfile = v.findViewById(R.id.comeInProfile_t);
            TextView no_count = v.findViewById(R.id.no_count_t);
            RelativeLayout marginlayout = v.findViewById(R.id.mainProfileView_t);

            if (i == 0) {
                holder.parent.addView(v, i);
                String image = "";
                image = keyInUserModalList.get(i).getUserimage();

                /*if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                    image = "dev-" + keyInUserModalList.get(i).getUserimage();
                } else {
                    //image = keyInUserModalList.get(i).userImage;
                    image = keyInUserModalList.get(i).getUserimage();
                }*/

                Glide.with(activity).load(image)
                        .thumbnail(0.5f)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_img)
                        .error(R.drawable.placeholder_img)
                        .into(comeInUserProfile);


            } else {
                if (i ==1) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(20 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    holder.parent.addView(v, i);
                    String image = "";

                    image = keyInUserModalList.get(i).getUserimage();

                   /* if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                        image = "dev-" + keyInUserModalList.get(i).getUserimage();
                    } else {
                        image = keyInUserModalList.get(i).getUserimage();
                    }*/

                    Glide.with(activity).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                }
                else
                if (i == 2) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(20 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    holder.parent.addView(v, i);
                    String image = "";

                    image = keyInUserModalList.get(i).getUserimage();

                  /*  if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                        image = "dev-" + keyInUserModalList.get(i).getUserimage();
                    } else {
                        image = keyInUserModalList.get(i).getUserimage();
                    }*/

                    Glide.with(activity).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                }

                if (i == 3) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(20 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    holder.parent.addView(v, i);
                    no_count.setText(" +" + (keyInUserModalList.size() - i));
                    no_count.setVisibility(View.VISIBLE);
                }
            }
        }

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, TheRoomActivity.class);
                intent.putExtra("fromTrendingHome", keyInUserModalList);
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                activity.startActivity(intent);
            }
        });

    holder.iv_summery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, Summary_Activity.class);
                intent.putExtra("event_id", object.getEvent().event_id);
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                activity.startActivity(intent);
            }
        });


       /* holder.iv_summery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isclick){
                    isclick =true;
                Intent intent = new Intent(activity, Summary_Activity.class);
                intent.putExtra("event_id", object.getEvent().event_id);
                intent.putExtra("object", object);
                intent.putExtra("currentLatLng", currentLatLng);
                activity.startActivity(intent);}
                return false;
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return eventsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_event;
        private ImageView iv_heart;
        private TextView txt_eventName, txt_eventAdress, txt_eventDate, txt_time, txt_like, txt_gap, txt_gap2, txt_eventmile;
        private RelativeLayout rl_main;
        private TextView like;
        private ViewPager viewPager;
        private LinearLayout indicator_linear_layout,rl_anim;
        private ImageView iv_group;
        private ImageView iv_comment;
        private ViewGroup parent;
        private TextView like_count_txt;
        private TextView txt_live;
        private ImageView green_dot;
        private ImageView iv_note_book;
        private ImageView iv_summery;
        AppCompatImageView iv_add;
        FrameLayout frame_keyinbutton;
        TextView tv_follow;
        private LinearLayout ll_summery;

        ViewHolder(View view) {
            super(view);

            rl_main = view.findViewById(R.id.rl_main);
            iv_summery = view.findViewById(R.id.iv_summery);
            iv_summery = view.findViewById(R.id.iv_summery);
            rl_anim = view.findViewById(R.id.rl_anim);
            img_event = view.findViewById(R.id.img_event);
            txt_eventName = view.findViewById(R.id.txt_eventName);
            txt_eventAdress = view.findViewById(R.id.txt_eventAdress);
            txt_gap = view.findViewById(R.id.txt_gap);
            txt_gap2 = view.findViewById(R.id.txt_gap2);
            txt_eventmile = view.findViewById(R.id.txt_eventmile);
            txt_time = view.findViewById(R.id.txt_time);
            txt_like = view.findViewById(R.id.txt_like);
            iv_heart = view.findViewById(R.id.iv_heart);
            like = view.findViewById(R.id.like);
            viewPager = view.findViewById(R.id.image_slider_pager);
            indicator_linear_layout = view.findViewById(R.id.indicator_linear_layout);
            iv_group = view.findViewById(R.id.iv_group);
            parent = view.findViewById(R.id.comeInUser_lnr);
            like_count_txt = view.findViewById(R.id.like_count_txt);
            txt_live = view.findViewById(R.id.txt_live);
            green_dot = view.findViewById(R.id.green_dot);
            iv_note_book = view.findViewById(R.id.iv_note_book);
            iv_add = view.findViewById(R.id.iv_add);
            iv_comment = view.findViewById(R.id.iv_comment);
            tv_follow = view.findViewById(R.id.tv_follow);
            frame_keyinbutton = view.findViewById(R.id.frame_keyinbutton);

        }
    }
}
