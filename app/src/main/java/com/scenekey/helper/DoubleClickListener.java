package com.scenekey.helper;

import android.os.Handler;
import android.view.View;


public abstract class DoubleClickListener implements View.OnClickListener {
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
    View mView = null;

    long lastClickTime = 0;
    Handler handler = new Handler();
    Runnable sc = new Runnable() {
        @Override
        public void run() {
            onSingleClick(mView);
            mView = null;
        }
    };

    Runnable dc = new Runnable() {
        @Override
        public void run() {
            onDoubleClick(mView);
            mView = null;
        }
    };

    @Override
    public void onClick(View v) {
        mView = v;
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            handler.removeCallbacks(sc);
            handler.post(dc);
        } else {
            handler.postDelayed(sc, DOUBLE_CLICK_TIME_DELTA);
        }
        lastClickTime = clickTime;
    }

    public abstract void onSingleClick(View v);
    public abstract void onDoubleClick(View v);
}
