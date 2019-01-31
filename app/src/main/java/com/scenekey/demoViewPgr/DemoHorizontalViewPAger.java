package com.scenekey.demoViewPgr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.verticleViewPager.ChildFragment;

import java.util.ArrayList;

public class DemoHorizontalViewPAger  extends FragmentStatePagerAdapter {

    public ArrayList<Card> demoUserList;

    public void setUserList(ArrayList<Card> demoUserList){
        this.demoUserList = demoUserList;
    }

    public DemoHorizontalViewPAger(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        DemoChidFragment demoChidFragment = new DemoChidFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("demoUserList",demoUserList);
        bundle.putInt("postion",position);
        demoChidFragment.setArguments(bundle);
        return demoChidFragment;

    }

    @Override
    public int getCount() {
        return demoUserList.size();
    }
   /* @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(object.);
    }*/

}