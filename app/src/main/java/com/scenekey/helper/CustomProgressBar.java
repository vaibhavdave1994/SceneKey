package com.scenekey.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.scenekey.R;


/**
 * Created by mindiii on 1/2/18.
 */

public class CustomProgressBar extends Dialog {

    public Context context;

    public CustomProgressBar(Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;

        // This is the layout XML file that describes your Dialog layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.custom_progress_dialog_layout);
        try {
            this.getWindow().setBackgroundDrawableResource(R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}