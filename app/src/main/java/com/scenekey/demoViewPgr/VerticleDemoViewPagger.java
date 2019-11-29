package com.scenekey.demoViewPgr;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.ImageIndicaterLIstener;
import com.scenekey.model.ImagesUpload;

import java.util.ArrayList;

public class VerticleDemoViewPagger extends FragmentStatePagerAdapter {

    private ArrayList<ImagesUpload> userMultiplList;
    private ImageIndicaterLIstener imageIndicaterLIstener;

    public VerticleDemoViewPagger(FragmentManager fm, ImageIndicaterLIstener imageIndicaterLIstener) {
        super(fm);
        this.imageIndicaterLIstener = imageIndicaterLIstener;

    }

    public void setimageList(ArrayList<ImagesUpload> userMultiplList,ArrayList<Card> demoImageList){
        this.userMultiplList = userMultiplList;
    }

    @Override
    public int getCount() {
        return userMultiplList.size();
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        imageIndicaterLIstener.getPostion(position);
        bundle.putString("postion",String.valueOf(position));
        bundle.putSerializable("userMultiplList",userMultiplList);
        return DemoContentFragment.newInstance(bundle);
    }
}
