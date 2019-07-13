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
import com.scenekey.lib_sources.SwipeCard.Card;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RoomPaggerSlider extends PagerAdapter {

    ArrayList<Card> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public RoomPaggerSlider(Context context, ArrayList<Card> arrayList) {
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

        Card card = arrayList.get(position);
        ImageView img_profile_pic2 = view.findViewById(R.id.img_profile_pagger);

        if(!card.userImage.equals("")){
            Picasso.with(context).load(card.userImage).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(img_profile_pic2);
        }else
            Picasso.with(context).load(card.imageint).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(img_profile_pic2);




        container.addView(view);
        return view;
    }

    // Delete a page at a `position`
    public void deletePage(int position)
    {
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

