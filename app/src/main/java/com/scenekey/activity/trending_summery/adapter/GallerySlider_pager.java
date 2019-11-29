package com.scenekey.activity.trending_summery.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.scenekey.R;
import com.scenekey.activity.OnBoardActivity;
import com.scenekey.activity.trending_summery.Model.SummeryModel;
import com.scenekey.cus_view.RoundRectCornerIv;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.listener.CheckEventStatusListener;
import com.scenekey.model.Event;
import com.scenekey.model.Events;
import com.scenekey.model.ImageSlidModal;
import com.scenekey.model.UserInfo;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GallerySlider_pager extends PagerAdapter {

   private Context context;
   private List<SummeryModel.EventBean.FeedPostBean> feedPostlist;


    public GallerySlider_pager(Context context, List<SummeryModel.EventBean.FeedPostBean> feedPostlist) {
       this.context = context;
       this.feedPostlist = feedPostlist;
    }

    @Override
    public int getCount() {
        return feedPostlist.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.gallery_image_slider, container, false);
        final SummeryModel.EventBean.FeedPostBean feedPostBean = feedPostlist.get(position);

        final ImageView imageView = view.findViewById(R.id.imagee);



        if (!feedPostBean.getFeed_image().isEmpty()) {
            Picasso.with(context).load(feedPostBean.getFeed_image())
                    .placeholder(R.drawable.sk_logo_image)
                    .error(R.drawable.sk_logo_image)
                    .into(imageView);
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }


}
