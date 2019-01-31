package com.scenekey.demoViewPgr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.scenekey.lib_sources.SwipeCard.Card;

import java.util.ArrayList;

public class DemofeedHorizontalViewPAger    extends FragmentStatePagerAdapter {

    public ArrayList<Card> demoUserList;

    public void setUserList(ArrayList<Card> demoUserList){
        this.demoUserList = demoUserList;
    }

    public DemofeedHorizontalViewPAger(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        DemoFeedChildFragment demoFeedChildFragment = new DemoFeedChildFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("demoUserList",demoUserList);
        bundle.putInt("postion",position);
        demoFeedChildFragment.setArguments(bundle);
        return demoFeedChildFragment;

    }

    @Override
    public int getCount() {
        return demoUserList.size();
    }
}