package com.scenekey.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scenekey.R;

/**
 * same as event fragment but
 * */

public class Key_In_Event_Fragment extends Fragment {


    public boolean canCallWebservice=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_key_in_event, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
