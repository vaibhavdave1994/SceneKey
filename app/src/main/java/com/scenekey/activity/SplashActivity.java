package com.scenekey.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.scenekey.R;
import com.scenekey.activity.new_reg_flow.NewIntroActivity;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class SplashActivity extends AppCompatActivity {

    private Context context = this;
    private Branch.BranchUniversalReferralInitListener branchReferralInitListener =
            new Branch.BranchUniversalReferralInitListener() {
                @Override
                public void onInitFinished(BranchUniversalObject branchUniversalObject,
                                           LinkProperties linkProperties, BranchError branchError) {
                    // do something with branchUniversalObject/linkProperties..
                }
            };

   /* @Override
    protected void onStart() {
        super.onStart();
        *//*TextView tvSplashLogoName=findViewById(R.id.tvSplashLogoName);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        tvSplashLogoName.setAnimation(animation);*//*
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SceneKey.sessionManager.putbadgecount(0);
//        printHash();

//        getFacebookKeyHash();

      /*  // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();*/
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //    SceneKey.sessionManager.setSoftKey(hasSoftKeys(getWindowManager()));
        showSplash();
    }

    private void showSplash() {
        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {
                if (SceneKey.sessionManager.isLoggedIn()) {
                    switch (ImageSessionManager.getInstance().getScreenFlag()) {
                        case 0:
                            Intent objIntent = new Intent(context, HomeActivity.class);
                            objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(objIntent);
                            finish();
                            break;

                        case 1:
//                            Intent intent = new Intent(context, ImageUploadActivity.class);
//                            intent.putExtra("from", "splash");
//                            // Closing all the Activities
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            // Add new Flag to start new Activity
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();

                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            break;

                        case 2:
                            Intent intent1 = new Intent(context, BioActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);
                            finish();
                            break;

                    }
                } else {
//                    Intent i = new Intent(context, LoginActivity.class);
                    Intent i = new Intent(context, NewIntroActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }
        }, 3 * 1000); // wait for 3 seconds
    }

    public void getFacebookKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.scenekey", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Utility.e("hash key", something);

//                GNsxVbIvnGf6o/M0ClW+TlnutTA=

            }
        } catch (PackageManager.NameNotFoundException e1) {
            Utility.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Utility.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Utility.e("exception", e.toString());
        }
    }

   /* @Override
    public void onStart() {
        super.onStart();
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {

                    // option 1: log data
                    Log.i("BRANCH SDK", referringParams.toString());

                    if (SceneKey.sessionManager.isLoggedIn()) {

                        Intent objIntent = new Intent(context, HomeActivity.class);
                        startActivity(objIntent);
                        finish();

                    } else {
//                    Intent i = new Intent(context, LoginActivity.class);
                        Intent i = new Intent(context, NewIntroActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();
                    }


                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }
*/
    private boolean hasSoftKeys(WindowManager windowManager) {
        boolean hasSoftwareKeys = true;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = windowManager.getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    private void printHash() {
// A2:15:16:DD:EA:05:62:E9:81:5A:C0:4F:17:D0:B7:3B
// 32:7A:4C:77:E4:8A:22:C2:02
//        50:CC:F8:85:DF:13:3B:27:19:6F:EB:C2:76:62:56:0F:5E:C2:4F:35
        byte[] bytes = {(byte) 0x89, (byte) 0xCC, (byte) 0xF8, (byte) 0x85, (byte) 0xDF,
                (byte) 0x13, 0x3B, (byte) 0x27, (byte) 0x19, 0x6F, (byte) 0xEB,
                (byte) 0xC2, (byte) 0x76, 0x62, (byte) 0x56, (byte) 0x0F,
                (byte) 0x5E, (byte) 0xC2, (byte) 0x4F, 0x35};
        Log.d("HASH", Base64.encodeToString(bytes, Base64.NO_WRAP));
    }
}
