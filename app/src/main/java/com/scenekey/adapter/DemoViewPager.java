package com.scenekey.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.scenekey.demoViewPager.FragmentEight;
import com.scenekey.demoViewPager.FragmentFive;
import com.scenekey.demoViewPager.FragmentFour;
import com.scenekey.demoViewPager.FragmentOne;
import com.scenekey.demoViewPager.FragmentSeven;
import com.scenekey.demoViewPager.FragmentSix;
import com.scenekey.demoViewPager.FragmentThree;
import com.scenekey.demoViewPager.FragmentTwo;
import com.scenekey.fragment.MainUserFeedFragment;

public class DemoViewPager  extends FragmentPagerAdapter {

    public DemoViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int postion) {

        switch (postion) {
            case 0:
                return new FragmentOne();
            case 1:
                return new FragmentTwo();
            case 2:
                return new FragmentThree();
            case 3:
                return new FragmentFour();
            case 4:
                return new FragmentFive();
            case 5:
                return new FragmentSix();
            case 6:
                return new FragmentSeven();
            case 7:
                return new FragmentEight();
            case 8:
                return new MainUserFeedFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 9;
    }
}
