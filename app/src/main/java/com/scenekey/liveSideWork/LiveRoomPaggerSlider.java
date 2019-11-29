package com.scenekey.liveSideWork;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scenekey.R;
import com.scenekey.model.EventAttendy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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


