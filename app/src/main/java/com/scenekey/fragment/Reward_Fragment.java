package com.scenekey.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scenekey.R;
import com.scenekey.activity.HomeActivity;


public class Reward_Fragment extends Fragment {

    private Context context;

    private HomeActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reward_, container, false);
        inItView();
        return view;
    }

    private void inItView() {
        activity.addFragment(new OfferSFragment(),0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity= (HomeActivity) getActivity();
    }
}
