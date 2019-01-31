package com.scenekey.liveSideWork;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.ProfileImagePagerAdapter;
import com.scenekey.helper.Constant;
import com.scenekey.helper.VerticalViewPager;
import com.scenekey.listener.ProfileImageListener;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.ImagesUpload;
import com.scenekey.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoFragment extends Fragment {

    public ArrayList<ImagesUpload> imageList;
    private VerticalViewPager fragmentViewPagger;
    private EventAttendy eventAttendy;
    private LiveProfileActivity liveProfileActivity;
    private LiveProfileActivity activity;
    private CognitoCredentialsProvider credentialsProvider;
    private ProfileImagePagerAdapter pagerAdapter;
    private int profilePos;


    public static DemoFragment newInstance(EventAttendy eventAttendy) {
        DemoFragment frag = new DemoFragment();
        Bundle args = new Bundle();
        args.putSerializable("eventAttendy", eventAttendy);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
        //inItView(view);
        return view;
    }

   /* private void inItView(View view) {
        fragmentViewPagger = view.findViewById(R.id.fragmentViewPagger);
        eventAttendy = (EventAttendy) getArguments().getSerializable("eventAttendy");
        imageList = new ArrayList<>();

        String facebookid = eventAttendy.userFacebookId;
        String userid = eventAttendy.userid;

        downloadFileFromS3((credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));

        pagerAdapter = new ProfileImagePagerAdapter(activity, imageList, new ProfileImageListener() {
            @Override
            public void getProfilePostion(int pos) {
                profilePos = pos;
            }
        });

        fragmentViewPagger.setAdapter(pagerAdapter);

        fragmentViewPagger.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.e("step11", "Pass");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (LiveProfileActivity) getActivity();
    }

    //    private void downloadFileFromS3(final int position, final EventAttendy eventAttendy, CognitoCredentialsProvider credentialsProvider) {//, CognitoCachingCredentialsProvider credentialsProvider){
    private void downloadFileFromS3(CognitoCredentialsProvider credentialsProvider) {//, CognitoCachingCredentialsProvider credentialsProvider){
        try {
            final AmazonS3Client s3Client;
            s3Client = new AmazonS3Client(credentialsProvider);

            // Set the region of your S3 bucket
            s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ObjectListing listing;

                        if (!eventAttendy.userFacebookId.isEmpty()) {
                            listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + eventAttendy.userFacebookId);
                        } else {
                            listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + eventAttendy.userid);
                        }

                        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

                        while (listing.isTruncated()) {

                            listing = s3Client.listNextBatchOfObjects(listing);
                            summaries.addAll(listing.getObjectSummaries());
                        }
//                        updateImages(position, summaries);
                        updateImages(summaries);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            Utility.e("AMAZON", e.toString());
        }
    }


    //    private void updateImages(final int position, final List<S3ObjectSummary> summaries) {
    private void updateImages(final List<S3ObjectSummary> summaries) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageList.clear();
                for (S3ObjectSummary obj : summaries) {
                    imageList.add(new ImagesUpload(obj.getKey()));
                }
                //liveRoomPaggerSlider.setVerticalPager(position, imageList);
            }
        });
    }

    private CognitoCredentialsProvider getCredentials() {
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider("us-west-2:86b58a3e-0dbd-4aad-a4eb-e82b1a4ebd91", Regions.US_WEST_2);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, activity);

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
    }*/
}
