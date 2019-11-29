package com.scenekey.feedsViewPager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.scenekey.model.Feeds;

import java.util.ArrayList;

public class HZFeedViewPagerAdapter  extends FragmentStatePagerAdapter {

    public ArrayList<Feeds> feedsArrayList;

    public void setUserList(ArrayList<Feeds> feedsArrayList){
        this.feedsArrayList = feedsArrayList;
    }

    public HZFeedViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//        ChildFragment childFragment = new ChildFragment();
        FeedsChildFragment feedsChildFragment = new FeedsChildFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feedsArrayList",feedsArrayList);
        bundle.putInt("postion",position);
        feedsChildFragment.setArguments(bundle);
        return feedsChildFragment;
    }

    @Override
    public int getCount() {
        return feedsArrayList.size();
    }

   /* @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(object.);
    }*/

}

