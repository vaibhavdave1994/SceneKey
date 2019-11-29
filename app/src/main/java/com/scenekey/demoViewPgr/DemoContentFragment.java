package com.scenekey.demoViewPgr;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scenekey.R;
import com.scenekey.model.ImagesUpload;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DemoContentFragment extends Fragment {

    private String postion;
    private  ArrayList<ImagesUpload>userMultiplList;

    public static DemoContentFragment newInstance(Bundle bundle) {
        DemoContentFragment fragment = new DemoContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null){
            userMultiplList = new ArrayList<>();
            Bundle bundle = getArguments();
            assert bundle != null;

            if (bundle.getSerializable("userMultiplList") != null) {
                userMultiplList = (ArrayList<ImagesUpload>) getArguments().getSerializable("userMultiplList");
                postion = getArguments().getString("postion");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_demo_content, container, false);

        ImagesUpload imagesUpload = userMultiplList.get(Integer.parseInt(postion));
        ImageView img_profile_pagger = view.findViewById(R.id.demoProfilePAgger);

            if (!imagesUpload.getPath().equals("")) {
            Picasso.with(getActivity()).load(imagesUpload.getPath()).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .fit()
                    .centerCrop()
                    .into(img_profile_pagger);
        }

        return view;
    }
}
