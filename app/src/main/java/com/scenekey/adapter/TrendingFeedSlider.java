package com.scenekey.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.scenekey.R;
import com.scenekey.activity.OnBoardActivity;
import com.scenekey.cus_view.RoundRectCornerIv;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.model.Event;
import com.scenekey.model.Events;
import com.scenekey.model.ImageSlidModal;
import com.scenekey.model.Venue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrendingFeedSlider  extends PagerAdapter {

    private ArrayList<ImageSlidModal> detailsModals;
    private Context context;
    Event event;
    Venue venue;
    private CheckEventStatusListener listener;
    Events object;
    String[] currentLatLng;
    private CustomProgressBar customProgressBar;
    public TrendingFeedSlider(Context context, ArrayList<ImageSlidModal> detailsModals, Event event,Venue venue,
                              CheckEventStatusListener listener,Events object,String[] currentLatLng) {
        this.context = context;
        this.detailsModals = detailsModals;
        this.event = event;
        this.venue = venue;
        this.listener = listener;
        this.object = object;
        this.currentLatLng = currentLatLng;
    }

    @Override
    public int getCount() {
        return detailsModals.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.image_slider, container, false);
        final ImageSlidModal imageSlidModal = detailsModals.get(position);

        final RoundRectCornerIv imageView = view.findViewById(R.id.imagee);
        LinearLayout whole_view = view.findViewById(R.id.whole_view);


        if (!imageSlidModal.feed_image.isEmpty()){
            Picasso.with(context).load(imageSlidModal.feed_image).fit().centerCrop()
                    .placeholder(R.drawable.sk_logo_image)
                    .error(R.drawable.sk_logo_image)
                    .into(imageView);
        }

        container.addView(view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setEnabled(false);
                if (event.isFeed != 0) {
                    try {
                        showProgDialog(false, "");
                        listener.getCheckEventStatusListener(event.event_name, event.event_id, venue, object, currentLatLng, new String[]{venue.getLatitude(), venue.getLongitude()});
                    } catch (NullPointerException e) {
                        dismissProgDialog();
                        e.printStackTrace();
                    }

                } else {

                    Intent intent = new Intent(context, OnBoardActivity.class);
                    intent.putExtra("eventid", event);
                    intent.putExtra("venuid", venue);
                    intent.putExtra("object", object);
                    intent.putExtra("currentLatLng", currentLatLng);
                    intent.putExtra("fromTrending", true);
                    context.startActivity(intent);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            imageView.setEnabled(true);
                        // Add the line which you want to run after 5 sec.

                    }
                },3000);

//                Intent intent = new Intent(context, OnBoardActivity.class);
//                intent.putExtra("eventid", event);
//                intent.putExtra("venuid", venue);
//                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
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
