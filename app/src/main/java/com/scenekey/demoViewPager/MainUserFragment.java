package com.scenekey.demoViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.activity.DemoProfileActivity;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.ProfileImagePagerAdapter;
import com.scenekey.helper.VerticalViewPager;
import com.scenekey.listener.OnSwipeTouchListener;
import com.scenekey.model.ImagesUpload;

import java.util.ArrayList;

public class MainUserFragment extends Fragment {

    public DemoProfileImagePagerFragment pagerAdapter;
    private Context context;
    private ArrayList<ImagesUpload> imageList;
    private DemoProfileActivity activity;
    private RelativeLayout customizeView_demo;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_user, container, false);
        inItView(view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (pagerAdapter!=null){
            imageList.clear();
            imageList.addAll(((DemoProfileActivity) context).imageList);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    public void inItView(View view) {
        imageList = new ArrayList<>();

        customizeView_demo = view.findViewById(R.id.customizeView_demo);

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int dpHeight = outMetrics.heightPixels;
        int dpWidth = outMetrics.widthPixels;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) customizeView_demo.getLayoutParams();
        params.height = (dpWidth - 20);
        customizeView_demo.setLayoutParams(params);


        imageList.addAll(((DemoProfileActivity) context).imageList);
        final VerticalViewPager sliderViewPager = view.findViewById(R.id.viewpager);
        pagerAdapter = new DemoProfileImagePagerFragment(context, imageList);
        sliderViewPager.setAdapter(pagerAdapter);
        sliderViewPager.setCurrentItem(0);

        sliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                sliderViewPager.setOnTouchListener(new OnSwipeTouchListener(context) {

                    public void onSwipeRight() {
                       // ((DemoProfileActivity) context).viewPager.setCurrentItem(4);
                    }

                    public void onSwipeLeft() {
                        //((DemoProfileActivity) context).viewPager.setCurrentItem(0);
                    }
                });
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (DemoProfileActivity) getActivity();
    }
}
