package com.scenekey.liveSideWork;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.scenekey.R;
import com.scenekey.helper.Constant;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.MyListener;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.ImagesUpload;
import com.scenekey.util.CircularViewPagerHandler;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveRoomPaggerSlider extends PagerAdapter {

    private ArrayList<EventAttendy> arrayList;
    private LiveProfileActivity context;
    private LayoutInflater layoutInflater;

    LiveRoomPaggerSlider(Context context, ArrayList<EventAttendy> arrayList) {
        this.context = (LiveProfileActivity) context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.room_pager_slider_layout, container, false);

         EventAttendy eventAttendy = arrayList.get(position);

         ImageView img_profile_pagger = view.findViewById(R.id.img_profile_pagger);

        if (!eventAttendy.getUserimage().equals("")) {
            Picasso.with(context).load(eventAttendy.getUserimage()).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .into(img_profile_pagger);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}


