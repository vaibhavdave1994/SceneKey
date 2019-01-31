package com.scenekey.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.scenekey.util.SceneKey;

/**
 * Created by mindiii on 15/11/18.
 */

public class ImageSessionManager {
    private Context context;
    private SharedPreferences mypref;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "Scenekey_Image";

    private static ImageSessionManager instance = null;

    private final String IMAGE_URL = "image_url";
    private final String IS_IMAGE_UPLOADED = "is_image_uploaded";
    private final String IS_USER_REGISTERED = "is_user_registered";
    private final String SET_TO_BIO = "set_to_bio";
    private final String SCREEN_FLAG = "screen_flag";


    public ImageSessionManager(Context context) {
        this.context = context;
        mypref = this.context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
        editor = mypref.edit();
        editor.apply();
    }

    public static ImageSessionManager getInstance() {
        if ((instance != null)) {
            return instance;
        }
        instance = new ImageSessionManager(SceneKey.getInstance().getApplicationContext());
        return instance;
    }

    public void createImageSession(String imageUrl, boolean isImageUploaded) {
        editor.putString(IMAGE_URL, imageUrl);
        editor.putBoolean(IS_IMAGE_UPLOADED, isImageUploaded);
        editor.commit();
    }

    public String getImageUrl() {
        return mypref.getString(IMAGE_URL, "");
    }

    public void clearImageSession() {
        editor.clear();
    }

    public void setScreenFlag(int screen) {
        editor.putInt(SCREEN_FLAG, screen);
        editor.commit();
    }

    public int getScreenFlag() {
        return mypref.getInt(SCREEN_FLAG, 0);
    }
}
