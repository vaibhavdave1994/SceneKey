package com.scenekey.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.helper.SortByPoint;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.model.Event;
import com.scenekey.model.Events;
import com.scenekey.model.ImageSlidModal;
import com.scenekey.model.Venue;
import com.scenekey.model.keyInUserModal;
import com.scenekey.util.Utility;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mindiii on 12/2/18.
 */

public class Trending_Adapter extends RecyclerView.Adapter<Trending_Adapter.ViewHolder> {

    private String TAG = "Trending_Adapter";
    private HomeActivity activity;
    private ArrayList<Events> eventsArrayList;
    private String[] currentLatLng;
    private boolean clicked;
    private CheckEventStatusListener listener;

    public Trending_Adapter(HomeActivity activity, ArrayList<Events> eventsArrayList, String[] currentLatLng, CheckEventStatusListener listener) {
        this.activity = activity;
        this.eventsArrayList = eventsArrayList;
        this.currentLatLng = currentLatLng;
        this.listener = listener;
    }

    @Override
    public Trending_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_custom_trending, parent, false);

        return new Trending_Adapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Trending_Adapter.ViewHolder holder, int position) {

        final Events object = eventsArrayList.get(position);
        final Venue venue = object.getVenue();
        final Event event = object.getEvent();

        Collections.sort(eventsArrayList, new SortByPoint());
        try {
            if (position == eventsArrayList.size() - 1) {
                holder.txt_gap.setVisibility(View.VISIBLE);

            } else {
                holder.txt_gap.setVisibility(View.GONE);
                holder.txt_gap2.setVisibility(View.GONE);
            }

            Log.v("keyInUser", "" + event.keyInUserModalList);

           /* if (event.keyInUserModalList.size() != 0) {
                setRecyclerView(holder, event.keyInUserModalList);
                holder.iv_group.setVisibility(View.GONE);
            } else {
                holder.iv_group.setVisibility(View.VISIBLE);
            }*/
            //for IMage
            //Picasso.with(activity).load(event.getImage().contains("defaultevent.jpg") ? venue.getImage() : event.getImage()).placeholder(R.drawable.transparent).into(holder.img_event);//TODO chnage in all if work fine
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
            //holder.green_dot.setBackgroundColor(activity.getResources().getColor(R.color.green));


        } else {
            holder.txt_time.setVisibility(View.VISIBLE);
            holder.txt_live.setVisibility(View.GONE);
            holder.green_dot.setImageResource(R.drawable.gray_dot);
            //holder.green_dot.setBackgroundColor(activity.getResources().getColor(R.color.gray));
            holder.txt_time.setText(mTime);
        }


        if (!object.getEvent().isEventLike.equals("0")) {
            holder.heart.setImageResource(R.drawable.active_heart_ico);
        } else {
            holder.heart.setImageResource(R.drawable.inactive_heart_ico);
        }

        if (!object.getEvent().likeCount.isEmpty()) {
            holder.like_count_txt.setText(object.getEvent().likeCount);
        }

        holder.txt_eventName.setText(event.event_name);
        holder.txt_eventAdress.setText((venue.getVenue_name().trim().length() > 29 ? venue.getVenue_name().trim().substring(0, 29) : venue.getVenue_name().trim()));

        //distance bw event and user
        String miles = String.valueOf(activity.getDistanceMile(new Double[]{Double.valueOf(venue.getLatitude()), Double.valueOf(venue.getLongitude()), Double.valueOf(currentLatLng[0]), Double.valueOf(currentLatLng[1])}));
        Utility.e("Miles---", miles);
        holder.txt_eventmile.setText(miles + " M");
        //holder.txt_eventDate.setText(object.timeFormat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setClickable(false);
                if (!clicked) {

                    try {
                        activity.showProgDialog(false, TAG);
                        listener.getCheckEventStatusListener(event.event_name, event.event_id, venue.getVenue_name(), object, currentLatLng, new String[]{venue.getLatitude(), venue.getLongitude()});

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

        if (event.imageslideList.isEmpty()) {
            if (event.getImage().contains("defaultevent.jpg")) {
                ImageSlidModal imageSlidModal = new ImageSlidModal();
                imageSlidModal.id = "1";
                imageSlidModal.feed_image = venue.getImage();
                event.imageslideList.add(imageSlidModal);
                //holder.indicaterView.setVisibility(View.GONE);
            }
        }

        TrendingFeedSlider trendingFeedSlider = new TrendingFeedSlider(activity, event.imageslideList);
        holder.viewPager.setAdapter(trendingFeedSlider);

        int listSize = event.imageslideList.size();
        if (listSize == 0 || listSize == 1) {
            holder.indicator_linear_layout.setVisibility(View.GONE);
            //holder.indicaterView.setVisibility(View.GONE);
        } else {
            holder.indicator_linear_layout.setVisibility(View.VISIBLE);
            //holder.indicaterView.setVisibility(View.VISIBLE);
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

    private void setRecyclerView(ViewHolder holder, ArrayList<keyInUserModal> keyInUserModalList) {

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder.parent.removeAllViews();
        for (int i = 0; i < keyInUserModalList.size(); i++) {

            assert inflater != null;
            View v = inflater.inflate(R.layout.trend_user_view, null);
            CircularImageView comeInUserProfile = v.findViewById(R.id.comeInProfile_t);
            TextView no_count = v.findViewById(R.id.no_count_t);
            RelativeLayout marginlayout = v.findViewById(R.id.mainProfileView_t);

            if (i == 0) {

                holder.parent.addView(v, i);
                String image = "";

                if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                    image = "dev-" + keyInUserModalList.get(i).userImage;
                } else {
                    image = keyInUserModalList.get(i).userImage;
                }

                Glide.with(activity).load(image)
                        .thumbnail(0.5f)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_img)
                        .error(R.drawable.placeholder_img)
                        .into(comeInUserProfile);

            } else {
                if (i < 4) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(10 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    holder.parent.addView(v, i);
                    String image = "";

                    if (!keyInUserModalList.get(i).userImage.contains("dev-")) {
                        image = "dev-" + keyInUserModalList.get(i).userImage;
                    } else {
                        image = keyInUserModalList.get(i).userImage;
                    }

                    Glide.with(activity).load(image)
                            .thumbnail(0.5f)
                            .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder_img)
                            .error(R.drawable.placeholder_img)
                            .into(comeInUserProfile);
                }

                if (i == 5) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(30 * i, 0, 0, 0);
                    marginlayout.setLayoutParams(params);
                    holder.parent.addView(v, i);
                    no_count.setText("" + (keyInUserModalList.size() - 1) + "+");
                    no_count.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventsArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_event;
        private ImageView heart;
        private TextView txt_eventName, txt_eventAdress, txt_eventDate, txt_time, txt_like, txt_gap, txt_gap2, txt_eventmile;
        private RelativeLayout rl_main;
        private TextView like;
        private ViewPager viewPager;
        private LinearLayout indicator_linear_layout;
        private ImageView iv_group;
        private ViewGroup parent;
        private TextView like_count_txt;
        private TextView txt_live;
        private RelativeLayout indicaterView;
        private ImageView green_dot;

        ViewHolder(View view) {
            super(view);

            rl_main = view.findViewById(R.id.rl_main);
            img_event = view.findViewById(R.id.img_event);
            txt_eventName = view.findViewById(R.id.txt_eventName);
            txt_eventAdress = view.findViewById(R.id.txt_eventAdress);
            txt_gap = view.findViewById(R.id.txt_gap);
            txt_gap2 = view.findViewById(R.id.txt_gap2);
            txt_eventmile = view.findViewById(R.id.txt_eventmile);
            txt_time = view.findViewById(R.id.txt_time);
            txt_like = view.findViewById(R.id.txt_like);
            heart = view.findViewById(R.id.heart);
            like = view.findViewById(R.id.like);
            viewPager = view.findViewById(R.id.image_slider_pager);
            indicator_linear_layout = view.findViewById(R.id.indicator_linear_layout);
            iv_group = view.findViewById(R.id.iv_group);
            parent = view.findViewById(R.id.comeInUser_lnr);
            like_count_txt = view.findViewById(R.id.like_count_txt);
            txt_live = view.findViewById(R.id.txt_live);
            indicaterView = view.findViewById(R.id.indicaterView);
            green_dot = view.findViewById(R.id.green_dot);
        }
    }
}