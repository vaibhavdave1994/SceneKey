package com.scenekey.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.multidex.MultiDex;

import com.scenekey.activity.invite_friend.roomdatabase.AppDataManager;
import com.scenekey.helper.ImageSessionManager;
import com.scenekey.helper.SessionManager;
import com.crashlytics.android.Crashlytics;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;

/**
 * Created by mindiii on 2/2/18.
 */

public class SceneKey extends Application {

    public static SceneKey instance = null;
    public static SessionManager sessionManager;
    public static ImageSessionManager imageSessionManager;
    private static AppDataManager appInstance;
    private Activity activeActivity;
    // public static final String TAG = Impress.class.getSimpleName();

    public static synchronized SceneKey getInstance() {
        if (instance != null) {
            return instance;
        }
        return new SceneKey();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
            appInstance = AppDataManager.getInstance(this);
        setupActivityListener();
        instance = this;
        sessionManager = new SessionManager(instance.getApplicationContext());
        imageSessionManager = new ImageSessionManager(instance.getApplicationContext());
        EmojiManager.install(new IosEmojiProvider());

        /*// Branch logging for debugging
        Branch.enableDebugMode();*/

        // Branch object initialization
        Branch.getAutoInstance(this);

    }

    public static AppDataManager getDataManager() {
        return appInstance;
    }



    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                activeActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                activeActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public Activity getActiveActivity() {
        return activeActivity;
    }

}