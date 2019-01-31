package com.scenekey.demoViewPager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scenekey.R;
import com.scenekey.model.ImagesUpload;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DemoProfileImagePagerFragment extends PagerAdapter {
    private ArrayList<ImagesUpload> imageUrl;
    private Context context;

    public DemoProfileImagePagerFragment(Context context, ArrayList<ImagesUpload> imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getCount() {
        return imageUrl.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        //View view = layoutInflater.inflate(R.layout.view_profile_image_adapter, container, false);
        View view = layoutInflater.inflate(R.layout.demo_view_profile_pager, container, false);

        ImageView imageView = view.findViewById(R.id.iv_img_profile_demo);

        Log.i("imageUrl", imageUrl.get(position).getPath());

        try {
            Picasso.with(context).load(imageUrl.get(position).getPath()).placeholder(R.drawable.image_default_profile).into(imageView);
        }catch (Exception e){
            e.printStackTrace();
        }

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
