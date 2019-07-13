package com.scenekey.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.facebook.login.LoginManager;
import com.scenekey.activity.new_reg_flow.NewIntroActivity;
import com.scenekey.model.UserInfo;
import com.scenekey.util.SceneKey;


/*
 * Created by mindiii on 8/12/17.
 */

public class SessionManager {
    private Context context;

    private static SessionManager instance = null;

    private final String PREF_NAME2 = "Scenekey2";
    private final String PREF_TUTO = "Scenekey_Tuto";
    private final String IS_LOGGEDIN2 = "isLoggedIn2";

    // New Code
    private final String USER_ID = "userid";
    private final String USER_FB_ID = "userFacebookId";
    private final String SOCIAL_TYPE = "socialType";
    private final String USER_NAME = "userName";
    private final String USER_EMAIL = "userEmail";
    private final String MAUTIC_CONTACT_ID = "mauticContactId";
    private final String FULL_NAME = "fullname";
    private final String LAST_NAME = "lastName";
    private final String PASSWORD = "password";
    private final String USER_IMAGE = "userImage";
    private final String AGE = "age";
    private final String DOB = "dob";
    private final String GENDER = "gender";
    private final String USER_DEVICE_ID = "userDeviceId";
    private final String DEVICE_TYPE = "deviceType";
    private final String USER_GENDER = "userGender";
    private final String USERSTATUS = "userStatus";
    private final String USER_LAST_LOGIN = "userLastLogin";
    private final String REGISTERED_DATE = "registered_date";
    private final String USER_TYPE = "usertype";
    private final String ARTIST_TYPE = "artisttype";
    private final String STAGE_NAME = "stagename";
    private final String VENUE_NAME = "venuename";
    private final String ADDRESS = "address";
    private final String FULL_ADDRESS = "fullAddress";
    private final String LATITUDE = "lat";
    private final String LONGITUDE = "longi";
    private final String ADMIN_LAT = "adminLat";
    private final String ADMIN_LONG = "adminLong";
    private final String USER_STATUS = "user_status";
    private final String MAKE_ADMIN = "makeAdmin";
    private final String CUR_DATE = "currentDate";
    private final String KEY_POINTS = "key_points";
    private final String BIO = "bio";
    private final String APP_BADGE_COUNT = "appBadgeCount";

    private final String ENVIRONMENT = "environment";
    private final String USER_ACCESS_TOKEN = "userAccessToken";
    private final String FIRST_TIME_DEMO = "firstTimeDemo";
    private final String CURRENT_LOCATION = "currentLocation";

    private final String TUTORIAL = "tutorial";
    private final String ADMIN_NO = "no";

    private final String SOFT_KEY = "SOFT_KEY";
    private final String USER_PASSWORD = "PASSWORD";
    private final String LOGINTYPE = "LOGINTYPE";

    private SharedPreferences mypref;
    private SharedPreferences tuto_pref;

    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor2;


    public SessionManager(Context context) {
        this.context = context;
        mypref = this.context.getSharedPreferences(PREF_NAME2, Context.MODE_PRIVATE);
        tuto_pref = this.context.getSharedPreferences(PREF_TUTO, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor2 = tuto_pref.edit();
        editor.apply();
        editor2.apply();
    }

    public static SessionManager getInstance() {
        if ((instance != null)) {
            return instance;
        }
        instance = new SessionManager(SceneKey.getInstance().getApplicationContext());
        return instance;
    }

    public void canTutorial(boolean tutorial) {
        editor2.putBoolean(TUTORIAL, tutorial);
        editor2.commit();
    }

    public boolean showTutorial() {
        return tuto_pref.getBoolean(TUTORIAL, true);
    }

    public void createSession(UserInfo userInfo) {
        // New Code
        editor.putString(USER_ID, userInfo.userid);
        editor.putString(USER_FB_ID, userInfo.userFacebookId);
        editor.putString(SOCIAL_TYPE, userInfo.socialType);
        editor.putString(USER_NAME, userInfo.userName);
        editor.putString(USER_EMAIL, userInfo.userEmail);
        editor.putString(MAUTIC_CONTACT_ID, userInfo.mauticContactId);
        editor.putString(FULL_NAME, userInfo.fullname);
        editor.putString(LAST_NAME, userInfo.lastName);
        editor.putString(PASSWORD, userInfo.password);
        editor.putString(USER_IMAGE, userInfo.userImage);
        editor.putString(AGE, userInfo.age);
        editor.putString(DOB, userInfo.dob);
        editor.putString(GENDER, userInfo.gender);
        editor.putString(USER_DEVICE_ID, userInfo.userDeviceId);
        editor.putString(DEVICE_TYPE, userInfo.deviceType);
        editor.putString(USER_GENDER, userInfo.userGender);
        editor.putString(USERSTATUS, userInfo.userStatus);
        editor.putString(USER_LAST_LOGIN, userInfo.userLastLogin);
        editor.putString(REGISTERED_DATE, userInfo.registered_date);
        editor.putString(USER_TYPE, userInfo.usertype);
        editor.putString(ARTIST_TYPE, userInfo.artisttype);
        editor.putString(STAGE_NAME, userInfo.stagename);
        editor.putString(VENUE_NAME, userInfo.venuename);
        editor.putString(ADDRESS, userInfo.address);
        editor.putString(FULL_ADDRESS, userInfo.fullAddress);
        editor.putString(LATITUDE, userInfo.lat);
        editor.putString(LONGITUDE, userInfo.longi);
        editor.putString(ADMIN_LAT, userInfo.adminLat);
        editor.putString(ADMIN_LONG, userInfo.adminLong);
        editor.putString(USER_STATUS, userInfo.user_status);
        editor.putString(MAKE_ADMIN, userInfo.makeAdmin);
        editor.putString(KEY_POINTS, userInfo.key_points);
        editor.putString(BIO, userInfo.bio);
        editor.putString(APP_BADGE_COUNT, userInfo.appBadgeCount);
        editor.putString(CUR_DATE, userInfo.currentDate);

        editor.putString(ENVIRONMENT, userInfo.environment);
        editor.putString(USER_ACCESS_TOKEN, userInfo.userAccessToken);
        editor.putBoolean(FIRST_TIME_DEMO, userInfo.firstTimeDemo);
        editor.putBoolean(CURRENT_LOCATION, userInfo.currentLocation);

        editor.putBoolean(IS_LOGGEDIN2, true);
        editor.commit();
    }

    public UserInfo getUserInfo() {

        // New Code
        UserInfo userInfo = new UserInfo();
        userInfo.userid = (mypref.getString(USER_ID, null));
        userInfo.userFacebookId = (mypref.getString(USER_FB_ID, null));
        userInfo.socialType = (mypref.getString(SOCIAL_TYPE, null));
        userInfo.userName = (mypref.getString(USER_NAME, null));
        userInfo.userEmail = (mypref.getString(USER_EMAIL, null));
        userInfo.mauticContactId = (mypref.getString(MAUTIC_CONTACT_ID, null));
        userInfo.fullname = (mypref.getString(FULL_NAME, null));
        userInfo.lastName = (mypref.getString(LAST_NAME, null));
        userInfo.password = (mypref.getString(PASSWORD, null));
        userInfo.userImage = (mypref.getString(USER_IMAGE, null));
        userInfo.age = (mypref.getString(AGE, null));
        userInfo.dob = (mypref.getString(DOB, null));
        userInfo.gender = (mypref.getString(GENDER, null));
        userInfo.userDeviceId = (mypref.getString(USER_DEVICE_ID, null));
        userInfo.deviceType = (mypref.getString(DEVICE_TYPE, null));
        userInfo.userGender = (mypref.getString(USER_GENDER, null));
        userInfo.userStatus = (mypref.getString(USERSTATUS, null));
        userInfo.userLastLogin = (mypref.getString(USER_LAST_LOGIN, null));
        userInfo.registered_date = (mypref.getString(REGISTERED_DATE, null));
        userInfo.usertype = (mypref.getString(USER_TYPE, null));
        userInfo.artisttype = (mypref.getString(ARTIST_TYPE, null));
        userInfo.stagename = (mypref.getString(STAGE_NAME, null));
        userInfo.venuename = (mypref.getString(VENUE_NAME, null));
        userInfo.address = (mypref.getString(ADDRESS, null));
        userInfo.fullAddress = (mypref.getString(FULL_ADDRESS, null));
        userInfo.lat = (mypref.getString(LATITUDE, null));
        userInfo.longi = (mypref.getString(LONGITUDE, null));
        userInfo.adminLat = (mypref.getString(ADMIN_LAT, null));
        userInfo.adminLong = (mypref.getString(ADMIN_LONG, null));
        userInfo.user_status = (mypref.getString(USER_STATUS, null));
        userInfo.makeAdmin = (mypref.getString(MAKE_ADMIN, null));
        userInfo.key_points = (mypref.getString(KEY_POINTS, null));
        userInfo.bio = (mypref.getString(BIO, null));
        userInfo.appBadgeCount = (mypref.getString(APP_BADGE_COUNT, null));

        userInfo.environment = (mypref.getString(ENVIRONMENT, null));
        userInfo.userAccessToken = (mypref.getString(USER_ACCESS_TOKEN, null));
        userInfo.firstTimeDemo = (mypref.getBoolean(FIRST_TIME_DEMO, false));
        userInfo.currentLocation = (mypref.getBoolean(CURRENT_LOCATION, false));
        return userInfo;
    }

    public boolean isLoggedIn() {
        return mypref.getBoolean(IS_LOGGEDIN2, false);
    }

    public void logout(Activity activity) {
        editor.putBoolean(IS_LOGGEDIN2, false);
        editor.clear();
        editor.commit();

        editor2.clear();
        editor2.commit();

//        Intent intent = new Intent(activity, RegistrationActivity.class);
        Intent intent = new Intent(activity, NewIntroActivity.class);
        // LoginActivity.CALLBACK =0;
        LoginManager.getInstance().logOut();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public void setPassword(String password) {
        editor.putString(USER_PASSWORD, password);
        editor.commit();
    }

    /*public void setLoginType(String fbOrGmail) {
        editor.putString(LOGINTYPE, fbOrGmail);
        editor.commit();
    }

    public String getLoginType() {
        return mypref.getString(LOGINTYPE, null);
    }*/

    public String getPassword() {
        return mypref.getString(USER_PASSWORD, null);
    }

    public boolean isSoftKey() {
        return mypref.getBoolean(SOFT_KEY, false);
    }

    public void setSoftKey(Boolean value) {
        editor.putBoolean(SOFT_KEY, value);
        editor.commit();
    }

    public void setLogin(boolean value) {
        editor.putBoolean(IS_LOGGEDIN2, value);
        editor.commit();
    }

    public String getFacebookId() {
        String s = mypref.getString(USER_FB_ID, "");
        if (s.equals("")) {
            s = mypref.getString(USER_ID, "");
        }
        return s;
    }

}
