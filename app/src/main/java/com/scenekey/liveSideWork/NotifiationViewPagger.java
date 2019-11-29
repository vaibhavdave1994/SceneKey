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
import com.scenekey.model.NotificationData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotifiationViewPagger extends PagerAdapter {

    ArrayList<NotificationData> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public NotifiationViewPagger(Context context, ArrayList<NotificationData> arrayList) {
        this.context = context;
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

        NotificationData notificationData = arrayList.get(position);
        ImageView img_profile_pic2 = view.findViewById(R.id.img_profile_pagger);

        if (!notificationData.userimage.equals("")) {
            Picasso.with(context).load(notificationData.userimage).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(img_profile_pic2);
        } else
            Picasso.with(context).load(notificationData.userimage).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(img_profile_pic2);


        LinearLayout ly_match_profile = view.findViewById(R.id.ly_pagerprofile);
        ly_match_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewPagerListener.getPostion(position);
            }
        });

        container.addView(view);
        return view;
    }

    // Delete a page at a `position`
    public void deletePage(int position) {
        // Remove the corresponding item in the data set
        arrayList.remove(position);
        // Notify the adapter that the data set is changed
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
