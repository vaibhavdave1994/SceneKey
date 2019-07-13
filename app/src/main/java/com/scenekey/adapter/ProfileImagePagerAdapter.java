package com.scenekey.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.scenekey.R;
import com.scenekey.listener.ProfileImageListener;
import com.scenekey.model.ImagesUpload;

import java.util.ArrayList;

/**
 * Created by mindiii on 22/11/18.
 */

public class ProfileImagePagerAdapter extends PagerAdapter {
    private ArrayList<ImagesUpload> imageUrl;
    private Context context;
    private ProfileImageListener profileImageListener;

    public ProfileImagePagerAdapter(Context context, ArrayList<ImagesUpload> imageUrl,ProfileImageListener profileImageListener) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.profileImageListener = profileImageListener;
    }

    @Override
    public int getCount() {
        return imageUrl.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.view_profile_image_adapter, container, false);

        ImageView imageView = view.findViewById(R.id.iv_img_profile);

        Log.i("imageUrl", imageUrl.get(position).getPath());
        String imgUrl = imageUrl.get(position).getPath();

        try {
            Glide.with(context).load(imgUrl).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//            Picasso.with(context).load(imgUrl).placeholder(R.drawable.image_default_profile).into(imageView);
        }catch (Exception e){
            e.printStackTrace();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileImageListener.getProfilePostion(position);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}

