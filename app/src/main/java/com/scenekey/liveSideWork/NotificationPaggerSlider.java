package com.scenekey.liveSideWork;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scenekey.R;
import com.scenekey.model.NotificationData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationPaggerSlider extends PagerAdapter {

    ArrayList<NotificationData> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public NotificationPaggerSlider(Context context, ArrayList<NotificationData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==  object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.demo_room_pager_slider, container, false);

        int count  = arrayList.size();

        NotificationData notificationData = arrayList.get(position);
        ImageView img_profile_pic2 = view.findViewById(R.id.img_profile_pagger);

        if(!notificationData.userimage.equals("")){
            Picasso.with(context).load(notificationData.userimage).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(img_profile_pic2);

        }else
            Picasso.with(context).load(notificationData.userimage).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(img_profile_pic2);


        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}