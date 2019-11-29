package com.scenekey.helper;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.scenekey.R;

/**
 * Created by mindiii on 21/2/18.
 */

public abstract class Pop_Up_Option extends Dialog {

    private Object object;

    public Pop_Up_Option(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent);
        View pop_up_view = LayoutInflater.from(context).inflate(R.layout.custom_galleryimage_layout, null);  //popup_custom_menu layout

        TextView tv_cancel = pop_up_view.findViewById(R.id.tv_cancel);
        TextView tv_gallery = pop_up_view.findViewById(R.id.tv_gallery);
        TextView tv_camera = pop_up_view.findViewById(R.id.tv_camera);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(pop_up_view);
        this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop;
        this.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width    = (int) context.getResources().getDimension(R.dimen._260sdp);
        lp.height   = (int) context.getResources().getDimension(R.dimen._160sdp);
        lp.gravity  = Gravity.BOTTOM;

        this.getWindow().setAttributes(lp);

        tv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGalleryClick(Pop_Up_Option.this,object);
            }
        });
        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClick(Pop_Up_Option.this,object);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public abstract void onGalleryClick(Pop_Up_Option dialog , Object object);
    public abstract void onCameraClick(Pop_Up_Option dialog , Object object);

    public void setObject(Object object){
        this.object = object;
    }

}
