package com.scenekey.verticleViewPager;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.facebook.AccessToken;
import com.scenekey.R;
import com.scenekey.helper.Constant;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.listener.ImageIndicaterLIstener;
import com.scenekey.liveSideWork.LiveProfileActivity;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.ImagesUpload;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChildFragment extends Fragment {

    private final String TAG = ChildFragment.class.toString();
    public ViewPagerAdapter viewPagerAdapter;
    public VerticalViewPager viewPager;
    private ArrayList<EventAttendy> userList;
    private Context context;
    private LiveProfileActivity activity;
    private ArrayList<ImagesUpload> imageList;
    private CognitoCredentialsProvider credentialsProvider;
    private String facebookId = "";
    private String userId = "";
    private CustomProgressBar customProgressBar;
    private ImageView btn1, btn2, btn3, btn4, btn5;
    private int pos = 0;

    private LinearLayout demo_View_dot;

    public ChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child2, container, false);
        inItView(view);
        return view;
    }

    private void inItView(View view) {

        //ImageView img_profile_pagger_child = view.findViewById(R.id.img_profile_pagger_child);
        imageList = new ArrayList<>();
        viewPager = (VerticalViewPager) view.findViewById(R.id.viewPager);
        demo_View_dot = view.findViewById(R.id.demo_View_dot);
        customProgressBar = new CustomProgressBar(context);
        Bundle bundle = getArguments();
        assert bundle != null;

        if (bundle.getSerializable("userList") != null) {
            int pos = bundle.getInt("postion");
            userList = (ArrayList<EventAttendy>) bundle.getSerializable("userList");
            EventAttendy eventAttendy = userList.get(pos);
            facebookId = eventAttendy.userFacebookId;
            userId = eventAttendy.userid;

            demo_View_dot.setVisibility(View.VISIBLE);

            if (!facebookId.equals("")) {
                downloadFileFromS3((credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (LiveProfileActivity) getActivity();
        //utility = new Utility(context);
    }


    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* get image from server start here*/
    private void downloadFileFromS3(CognitoCredentialsProvider credentialsProvider) {
        //, CognitoCachingCredentialsProvider credentialsProvider){
        //showProgDialog(false);
        try {
            final AmazonS3Client s3Client;
            s3Client = new AmazonS3Client(credentialsProvider);
            Log.e("step6", "Pass");
            // Set the region of your S3 bucket
            s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("step7", "Pass");
                    try {

                        ObjectListing listing;
                        if (!facebookId.equals("")) {
                            listing = s3Client.listObjects(Constant.BUCKET,  facebookId);
//                            listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + facebookId);
                        } else {
                            listing = s3Client.listObjects(Constant.BUCKET,  userId);
                            listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + userId);
                        }

                        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

                        while (listing.isTruncated()) {

                            listing = s3Client.listNextBatchOfObjects(listing);
                            summaries.addAll(listing.getObjectSummaries());

                        }
                        Log.e("step8", "Pass");
                        updateImages(summaries);

                        Utility.e(TAG, "listing " + summaries.get(0).getKey() + "no of image " + summaries.size());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.e(TAG, "Exception found while listing " + e);
                        Log.e("step9", "Fail");
                    }
                }
            });

            thread.start();
            activity.dismissProgDialog();
        } catch (Exception e) {
            Utility.e("AMAZON", e.toString());
            activity.dismissProgDialog();
            Log.e("step09", "fail");
        }
    }


    private void updateImages(final List<S3ObjectSummary> summaries) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (S3ObjectSummary obj : summaries) {
                    imageList.add(new ImagesUpload(obj.getKey()));
                }
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getView() != null) {
                    setUpView(getView());
                }

                Log.e("step10", "pass");
            }
        });
    }

    // New Code
    private void setUpView(View view) {
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);

        /*demo_View_dot.setVisibility(View.GONE);*/

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), new ImageIndicaterLIstener() {
            @Override
            public void getPostion(int position) {
                demo_View_dot.setVisibility(View.GONE);
                pos = position;
            }
        });

        viewPagerAdapter.setimageList(imageList);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(pos);
        initButton(pos);

        String userImage = SceneKey.sessionManager.getUserInfo().getUserImage();

        for (int i = 0; i < imageList.size(); i++) {
            if (userImage != null) {
                if (userImage.equals(imageList.get(i).getPath())) {
                    pos = i;
                    break;
                }
            }
        }
        viewPager.setCurrentItem(pos);
        initButton(pos);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                demo_View_dot.setVisibility(View.GONE);
                initButton(position);
            }

            @Override
            public void onPageSelected(int position) {
                //initButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.e("step11", "Pass");
    }

    /* get image from server end here*/

    private CognitoCredentialsProvider getCredentials() {
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider("us-west-2:86b58a3e-0dbd-4aad-a4eb-e82b1a4ebd91", Regions.US_WEST_2);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, context);

        Map<String, String> logins = new HashMap<String, String>();

        String token = "";
        try {
            token = AccessToken.getCurrentAccessToken().getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (token != null && !token.equals("")) {
            logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
        } else {
            logins.put("graph.facebook.com", Constant.Token);
        }
        credentialsProvider.setLogins(logins);
        return credentialsProvider;
    }


    private void initButton(int position) {
        switch (position) {
            case 0:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 1:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 2:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 3:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                break;

            case 4:
                btn1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn4.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.inactive_profile_img_bullet));
                btn5.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_profile_img_bullet));
                break;
        }

        switch (imageList.size()) {
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
