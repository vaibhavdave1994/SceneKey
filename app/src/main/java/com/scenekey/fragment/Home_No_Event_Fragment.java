package com.scenekey.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scenekey.R;
import com.scenekey.activity.HomeActivity;

public class Home_No_Event_Fragment extends Fragment implements View.OnClickListener {

    private final String TAG = Home_No_Event_Fragment.class.toString();
    private Context context;
    private HomeActivity activity;

    public Bitmap imageArray[];
    private boolean clicked;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home_no_event, container, false);
        v.findViewById(R.id.tvTryDemo).setOnClickListener(this);
        v.findViewById(R.id.tvSearch).setOnClickListener(this);
        activity.setTitleVisibility(View.VISIBLE);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle(context.getResources().getString(R.string.enter));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        activity= (HomeActivity) getActivity();
    }

    @Override
    public void onStart() {
        activity.setBBVisibility(View.VISIBLE,TAG);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setBBVisibility(View.VISIBLE,TAG);
        activity.setTitleVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvTryDemo:
             /*   Intent intent = new Intent(context, TryAndDemoActivity.class);
                startActivity(intent);
*/
                break;
        }
    }
}
