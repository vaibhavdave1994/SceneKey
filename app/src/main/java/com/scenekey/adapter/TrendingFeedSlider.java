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
import com.scenekey.model.ImageSlidModal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrendingFeedSlider  extends PagerAdapter {

    ArrayList<ImageSlidModal> detailsModals;
    private Context context;
    private LayoutInflater layoutInflater;


    public TrendingFeedSlider(Context context, ArrayList<ImageSlidModal> detailsModals) {
        this.context = context;
        this.detailsModals = detailsModals;
    }

    @Override
    public int getCount() {
        return detailsModals.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.image_slider, container, false);
        final ImageSlidModal imageSlidModal = detailsModals.get(position);

        ImageView imageView = view.findViewById(R.id.imagee);
        LinearLayout whole_view = view.findViewById(R.id.whole_view);

        if (!imageSlidModal.feed_image.isEmpty()){
            Picasso.with(context).load(imageSlidModal.feed_image).fit().centerCrop()
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
