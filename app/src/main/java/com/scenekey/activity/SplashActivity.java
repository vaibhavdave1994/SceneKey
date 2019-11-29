package com.scenekey.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.scenekey.R;
import com.scenekey.activity.new_reg_flow.NewIntroActivity;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SceneKey.sessionManager.putbadgecount(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*TextView tvSplashLogoName=findViewById(R.id.tvSplashLogoName);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        tvSplashLogoName.setAnimation(animation);*/
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
}
