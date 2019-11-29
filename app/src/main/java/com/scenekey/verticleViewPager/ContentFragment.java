package com.scenekey.verticleViewPager;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scenekey.R;
import com.scenekey.model.ImagesUpload;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ContentFragment extends Fragment {


    public String parentInd;
    ArrayList<ImagesUpload> userList;
    Context mcontext;
    private int position;
    private ImageView btn1, btn2, btn3, btn4, btn5;


    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(Bundle bundle) {
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null){
            userList = new ArrayList<>();
            Bundle bundle = getArguments();
            assert bundle != null;

            if (bundle.getSerializable("list") != null) {
                 userList = (ArrayList<ImagesUpload>) getArguments().getSerializable("list");
                 parentInd = getArguments().getString("postion");
                 position = Integer.parseInt(parentInd);

            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ImagesUpload images = userList.get(Integer.parseInt(parentInd));

        ImageView img_profile_pagger = view.findViewById(R.id.img_profile_pagger);
        // New Code
        //inItView(view);

        if (!images.getPath().equals("")) {
            //initButton(position);
            Picasso.with(getActivity()).load(images.getPath()).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .into(img_profile_pagger);
            //Glide.with(getActivity()).load(images.getPath()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(img_profile_pagger);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mcontext= context;
    }

    private void inItView(View view) {
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);

        initButton(0);
    }
        private void initButton(int position) {
            switch (position) {
                case 0:
                    btn1.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.active_profile_img_bullet));
                    btn2.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn3.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn4.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn5.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    break;

                case 1:
                    btn1.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn2.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.active_profile_img_bullet));
                    btn3.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn4.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn5.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    break;

                case 2:
                    btn1.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn2.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn3.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.active_profile_img_bullet));
                    btn4.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn5.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    break;

                case 3:
                    btn1.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn2.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn3.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn4.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.active_profile_img_bullet));
                    btn5.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    break;

                case 4:
                    btn1.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn2.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn3.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn4.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.inactive_profile_img_bullet));
                    btn5.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.active_profile_img_bullet));
                    break;
            }

            switch (userList.size()) {
                case 1:
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.GONE);
                    btn3.setVisibility(View.GONE);
                    btn4.setVisibility(View.GONE);
                    btn5.setVisibility(View.GONE);
                    break;

                case 2:
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                    btn3.setVisibility(View.GONE);
                    btn4.setVisibility(View.GONE);
                    btn5.setVisibility(View.GONE);
                    break;

                case 3:
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                    btn3.setVisibility(View.VISIBLE);
                    btn4.setVisibility(View.GONE);
                    btn5.setVisibility(View.GONE);
                    break;

                case 4:
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                    btn3.setVisibility(View.VISIBLE);
                    btn4.setVisibility(View.VISIBLE);
                    btn5.setVisibility(View.GONE);
                    break;

                case 5:
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                    btn3.setVisibility(View.VISIBLE);
                    btn4.setVisibility(View.VISIBLE);
                    btn5.setVisibility(View.VISIBLE);
                    break;
            }
    }
}

