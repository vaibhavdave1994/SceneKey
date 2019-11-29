package com.scenekey.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.scenekey.R;

/**
 * Created by mindiii on 21/2/18.
 */

public abstract class Pop_Up_Option_Follow_Unfollow extends Dialog {

    private Object object;
    private  int followUnfollow ;
    TextView unfollow_text,follow_text;

    public Pop_Up_Option_Follow_Unfollow(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent);
        View pop_up_view = LayoutInflater.from(context).inflate(R.layout.custom_follow_unfollow_dialog_layout, null);  //popup_custom_menu layout

        TextView tv_cancel = pop_up_view.findViewById(R.id.tv_cancel);
        unfollow_text = pop_up_view.findViewById(R.id.unfollow_text);
        follow_text = pop_up_view.findViewById(R.id.follow_text);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(pop_up_view);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop;
        this.setCanceledOnTouchOutside(true);
//        this.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black50p)));


//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(this.getWindow().getAttributes());
//        lp.width    = (int) context.getResources().getDimension(R.dimen._260sdp);
//        lp.height   = (int) context.getResources().getDimension(R.dimen._120sdp);
//        lp.gravity  = Gravity.BOTTOM;
//        this.getWindow().setAttributes(lp);

        follow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFollowSelect(Pop_Up_Option_Follow_Unfollow.this,object);
                dismiss();
            }
        });
        unfollow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUnFollowSelect(Pop_Up_Option_Follow_Unfollow.this,object);
                dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public abstract void onFollowSelect(Pop_Up_Option_Follow_Unfollow dialog , Object object);
    public abstract void onUnFollowSelect(Pop_Up_Option_Follow_Unfollow dialog , Object object);

    public void setObject(Object object,int followUnfollow){
        this.object = object;
        this.followUnfollow = followUnfollow;
    }

    @Override
    public void show() {
        if(followUnfollow == 0){
            unfollow_text.setVisibility(View.VISIBLE);
            follow_text.setVisibility(View.GONE);
        }
        else {
            unfollow_text.setVisibility(View.GONE);
            follow_text.setVisibility(View.VISIBLE);
        }
        super.show();
    }
}
