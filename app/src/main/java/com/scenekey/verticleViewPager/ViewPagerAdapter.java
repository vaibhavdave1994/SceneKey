package com.scenekey.verticleViewPager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.scenekey.listener.ImageIndicaterLIstener;
import com.scenekey.model.ImagesUpload;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ImagesUpload> imageUrl;
    private ImageIndicaterLIstener imageIndicaterLIstener;

    public ViewPagerAdapter(FragmentManager fm,ImageIndicaterLIstener imageIndicaterLIstener) {
        super(fm);
        this.imageIndicaterLIstener = imageIndicaterLIstener;
    }

    public void setimageList(ArrayList<ImagesUpload> imageUrl){
        this.imageUrl = imageUrl;
    }

    @Override
    public int getCount() {
        return imageUrl.size();
    }

    @Override
    public Fragment getItem(int position) {
        imageIndicaterLIstener.getPostion(position);
        Bundle bundle = new Bundle();
        bundle.putString("postion",String.valueOf(position));
        bundle.putSerializable("list",imageUrl);
        return ContentFragment.newInstance(bundle);
    }

}
