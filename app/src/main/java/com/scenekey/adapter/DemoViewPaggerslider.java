package com.scenekey.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scenekey.R;
import com.scenekey.activity.DemoFeedUser;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DemoViewPaggerslider   extends PagerAdapter {

    private ArrayList<Card> arrayList;
    private DemoFeedUser context;
    private LayoutInflater layoutInflater;

   public DemoViewPaggerslider(Context context, ArrayList<Card> arrayList) {
        this.context = (DemoFeedUser) context;
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

        Card card = arrayList.get(position);

        ImageView img_profile_pagger = view.findViewById(R.id.img_profile_pagger);

        if (card.imageint != 0) {
            Picasso.with(context).load(card.imageint).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .into(img_profile_pagger);
        } else if (!card.userImage.isEmpty()) {
            Picasso.with(context).load(card.userImage).fit().centerCrop()
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


