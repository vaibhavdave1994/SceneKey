package com.scenekey.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.scenekey.R;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.SessionManager;
import com.scenekey.listener.StatusBarHide;
import com.scenekey.model.UserInfo;
import com.scenekey.util.CommonUtils;
import com.scenekey.util.SceneKey;
import com.scenekey.util.StatusBarUtil;

public class BaseActivity extends AppCompatActivity {

    private Activity activity = this;
    private Dialog mProgressBar;
    public boolean isApiM;
    CustomProgressBar customProgressBar ;

    public static SessionManager getSessionData() {
        return SceneKey.sessionManager;
    }

    public void replaceFragment(Fragment fragmentHolder) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            String fragmentName = fragmentHolder.getClass().getName();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame_fragments, fragmentHolder, fragmentName).addToBackStack(fragmentName);
            fragmentTransaction.commit();
            hideKeyboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fragment addFragment(Fragment fragmentHolder, int animationValue) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentName = fragmentHolder.getClass().getName();
            if (!(fragmentHolder instanceof StatusBarHide)) showStatusBar();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (animationValue == 0) {

                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_out_down, R.anim.slide_in_down);
            }
            if (animationValue == 1)
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setEnterTransition(null);
            }
            fragmentTransaction.add(R.id.frame_fragments, fragmentHolder, fragmentName).addToBackStack(fragmentName);
            fragmentTransaction.commit();

            hideKeyboard();
            return fragmentHolder;
        } catch (Exception e) {
            return null;
        }
    }

    public void showStatusBar() {
        getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // top_status.setBackgroundResource(R.color.white);
        } else {
            StatusBarUtil.setStatusBarColor(this, R.color.new_white_bg);
        }
    }

    protected void setLoading(Boolean isLoading) {

        //Log.v("test", ""+isLoading);
        //first cancel progress bar
        if (mProgressBar != null && mProgressBar.isShowing()) {
            mProgressBar.cancel();
        }
        if (isLoading) {
            mProgressBar = CommonUtils.showLoadingDialog(this);
        }
    }

    /*private void showProgDialog(boolean b) {
        customProgressBar.setCanceledOnTouchOutside(b);
        customProgressBar.setCancelable(b);
        customProgressBar.show();
    }

    private void dismissProgDialog() {
        if (customProgressBar != null) customProgressBar.dismiss();
    }*/


   /* public void showProgDialog(boolean b, String TAG) {
        try {
            customProgressBar.setCanceledOnTouchOutside(b);
            customProgressBar.setCancelable(b);
            customProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

*/

    protected void hideKeyboard() {
        if (getCurrentFocus() == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    protected void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void custom_dialog(String message) {
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.custom_dilog);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvMessages = dialog.findViewById(R.id.tvMessages);
        TextView tvPopupOk = dialog.findViewById(R.id.tvPopupOk);

        tvMessages.setText(message);

        tvPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }*/

    public Fragment getCurrentFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void showProgDialog(boolean b, String TAG) {
        try {
            if(customProgressBar == null) {
                customProgressBar = new CustomProgressBar(activity);
            }
            customProgressBar.setCanceledOnTouchOutside(b);
            customProgressBar.setCancelable(b);
            customProgressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgDialog() {
        try {
            if (customProgressBar != null) customProgressBar.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserInfo userInfo() {

        if (!SceneKey.sessionManager.isLoggedIn()) {
            SceneKey.sessionManager.logout(activity);
        }
        return SceneKey.sessionManager.getUserInfo();
    }
}
