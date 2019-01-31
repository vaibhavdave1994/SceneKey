package com.scenekey.demoViewPgr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.scenekey.activity.DemoFeedUser;
import com.scenekey.activity.DemoProfileActivity;
import com.scenekey.helper.Constant;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.listener.ImageIndicaterLIstener;
import com.scenekey.model.ImagesUpload;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.verticleViewPager.VerticalViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DemoChidFragment extends Fragment {

    private final String TAG = DemoChidFragment.class.toString();
    private Context context;
    private ArrayList<Card> demoUserList;
    private VerticalViewPager demoviewPager;
    private VerticleDemoViewPagger verticleDemoViewPagger;
    private CognitoCredentialsProvider credentialsProvider;
    private String facebookid = "";
    private DemoProfileActivity activity;
    private ArrayList<ImagesUpload> imageList;
    private ImageView btn1, btn2, btn3, btn4, btn5;
    private int pos = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_demo_chid, container, false);
        inItView(view);
        return view;
    }

    private void inItView(View view) {

        demoUserList = new ArrayList<>();
        imageList = new ArrayList<>();
        demoviewPager = view.findViewById(R.id.demoviewPager);
        Bundle bundle = getArguments();
        assert bundle != null;

        if (bundle.getSerializable("demoUserList") != null) {
            int pos = bundle.getInt("postion");
            demoUserList = (ArrayList<Card>) bundle.getSerializable("demoUserList");
            Card card = demoUserList.get(pos);
            ImageView img_profile_pagger = view.findViewById(R.id.demo_image_View);
            facebookid = card.facebookId;
            if (!card.facebookId.equals("")) {
                img_profile_pagger.setVisibility(View.GONE);
                demoviewPager.setVisibility(View.VISIBLE);
                downloadFileFromS3((credentialsProvider == null ? credentialsProvider = getCredentials() : credentialsProvider));

            } else {

                img_profile_pagger.setVisibility(View.VISIBLE);
                demoviewPager.setVisibility(View.GONE);

                if (card.imageint != 0) {
                    Picasso.with(getActivity()).load(card.imageint).fit().centerCrop()
                            .placeholder(R.drawable.bg_event_card)
                            .error(R.drawable.bg_event_card)
                            .into(img_profile_pagger);
                }
            }
        }
    }

        /* get image from server start here*/
        private void downloadFileFromS3 (CognitoCredentialsProvider credentialsProvider){

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
                            if (!facebookid.equals("")) {
                                listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + facebookid);
                            } else {
                                listing = s3Client.listObjects(Constant.BUCKET, Constant.DEV_TAG + facebookid);
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
                //dismissProgDialog();
            } catch (Exception e) {
                Utility.e("AMAZON", e.toString());
                Log.e("step09", "fail");
            }
        }


        private void updateImages ( final List<S3ObjectSummary> summaries){
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
        private void setUpView (View view){
            btn1 = view.findViewById(R.id.btn1);
            btn2 = view.findViewById(R.id.btn2);
            btn3 = view.findViewById(R.id.btn3);
            btn4 = view.findViewById(R.id.btn4);
            btn5 = view.findViewById(R.id.btn5);

            verticleDemoViewPagger = new VerticleDemoViewPagger(getChildFragmentManager(), new ImageIndicaterLIstener() {
                @Override
                public void getPostion(int position) {
                    pos = position;
                }
            });
            verticleDemoViewPagger.setimageList(imageList, demoUserList);
            demoviewPager.setAdapter(verticleDemoViewPagger);
            demoviewPager.setCurrentItem(pos);
            initButton(0);

            String userImage = SceneKey.sessionManager.getUserInfo().getUserImage();

            for (int i = 0; i < imageList.size(); i++) {
                if (userImage != null) {
                    if (userImage.equals(imageList.get(i).getPath())) {
                        pos = i;
                        break;
                    }
                }
            }

            demoviewPager.setCurrentItem(pos);
            initButton(pos);

            demoviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    initButton(position);
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


        /* get image from server end here*/

        private CognitoCredentialsProvider getCredentials () {
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

        @Override
        public void onAttach (Context context){
            super.onAttach(context);
            this.context = context;
            activity = (DemoProfileActivity) getActivity();

        }


        private void initButton ( int position){
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
