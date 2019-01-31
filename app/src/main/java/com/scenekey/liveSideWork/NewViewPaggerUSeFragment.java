package com.scenekey.liveSideWork;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.scenekey.model.EventAttendy;


public class NewViewPaggerUSeFragment extends FragmentPagerAdapter {

    private EventAttendy eventAttendy;


    public NewViewPaggerUSeFragment(FragmentManager fm, EventAttendy eventAttendy) {
        super(fm);
        this.eventAttendy = eventAttendy;
    }

    @Override
    public Fragment getItem(int postion) {
        Fragment frag = DemoFragment.newInstance(eventAttendy);
        return frag;

    }

    @Override
    public int getCount() {

        return 1;
    }
}