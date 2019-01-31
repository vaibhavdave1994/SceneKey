package com.scenekey.helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.scenekey.R;

/**
 * Created by mindiii on 5/3/18.
 */

public abstract class CustomAlertDialog extends Dialog {

    private String message, buttonLeft, buttonRight;
    private View pop_up_view;
    private TextView txt_pop10_One, txt_pop10_Two, txt_pop10_message, txt_pop_title;


    public CustomAlertDialog(Activity activity) {
        super(activity, android.R.style.Theme_Translucent);
        pop_up_view = LayoutInflater.from(activity).inflate(R.layout.custom_alert_dialog, null);
        txt_pop10_One = pop_up_view.findViewById(R.id.txt_pop10_One);
        txt_pop10_Two = pop_up_view.findViewById(R.id.txt_pop10_Two);
        txt_pop10_message = pop_up_view.findViewById(R.id.txt_pop10_message);
        txt_pop_title = pop_up_view.findViewById(R.id.txt_pop_title);

        //this.context = context;
        // This is the layout XML file that describes your Dialog layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(pop_up_view);

        txt_pop10_One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftButtonClick();
            }
        });

        txt_pop10_Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightButtonClick();
            }
        });


        /******************** This is to set Layout Parmanetes ********************************/

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = (int) activity.getResources().getDimension(R.dimen._250sdp);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND; // This is for Demmed effect
        lp.dimAmount = 0.4f;                                    // set Dimmed level from here.
        this.getWindow().setAttributes(lp);
    }


    public abstract void leftButtonClick();

    public abstract void rightButtonClick();

    public void setLeftButtonText(String s) {
        txt_pop10_One.setText(s);
    }

    public void setRightButtonText(String s) {
        txt_pop10_Two.setText(s);

    }

    public void setMessage(String s) {
        txt_pop10_message.setText(s);
    }

    public void setTitle(String s) {
        txt_pop_title.setText(s);
    }


}
