package com.scenekey.verticleViewPager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.scenekey.model.EventAttendy;

import java.util.ArrayList;

public class HorizontalViewPagerAdapter  extends FragmentStatePagerAdapter {

    public ArrayList<EventAttendy> userList;

    public void setUserList(ArrayList<EventAttendy> userList){
        this.userList = userList;
    }

    public HorizontalViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ChildFragment childFragment = new ChildFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userList",userList);
        bundle.putInt("postion",position);
        childFragment.setArguments(bundle);
        return childFragment;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

   /* @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(object.);
    }*/

}
