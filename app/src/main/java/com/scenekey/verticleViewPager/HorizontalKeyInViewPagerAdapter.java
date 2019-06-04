package com.scenekey.verticleViewPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.scenekey.fragment.keyInChildFragment;
import com.scenekey.model.KeyInUserModal;

import java.util.ArrayList;

public class HorizontalKeyInViewPagerAdapter extends FragmentStatePagerAdapter {

    public ArrayList<KeyInUserModal> userList;

    public void setUserList(ArrayList<KeyInUserModal> userList){
        this.userList = userList;
    }

    public HorizontalKeyInViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        keyInChildFragment childFragment = new keyInChildFragment();
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
}
