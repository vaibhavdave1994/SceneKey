package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.activity.ImageUploadActivity;
import com.scenekey.helper.Pop_Up_Option;
import com.scenekey.helper.WebServices;
import com.scenekey.model.BucketDataModel;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mindiii on 21/2/18.
 */

public class NewImageUpload_Adapter extends RecyclerView.Adapter<NewImageUpload_Adapter.Holder> {

    public ArrayList<BucketDataModel> list;
    private ImageUploadActivity activity;
    private Pop_Up_Option pop_up_option;
    private Context context;
    Utility utility;

    public NewImageUpload_Adapter(ImageUploadActivity activity,ArrayList<BucketDataModel> list) {
        this.list = list;
        this.activity = activity;
        context = activity;
        utility = new Utility(context);
        initializePopup();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.custom_image_upload, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        if(position > list.size() - 1){
            holder.img_cross.setVisibility(View.GONE);
         // holder.img_pic.setImageBitmap(null);
            holder.img_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.size() >= 5) {
                        utility.showCustomPopup(context.getString(R.string.photo_limit),"Photo Limit Reached", String.valueOf(R.font.montserrat_medium));
                        return;
                    }
                    pop_up_option.setObject(null);
                    pop_up_option.show();
                }
            });
        }
        else {
            Picasso.with(context).load(WebServices.USER_IMAGE+list.get(position).getKey()).placeholder(R.drawable.app_icon)
                    .error(R.drawable.app_icon).into(holder.img_pic);
            holder.img_cross.setVisibility(View.VISIBLE);

            holder.img_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String s = list.get(position).getKey() + "";
                    if (s.length() > 5) {
                        activity.setImage(s);
                        //setDefoultProfileImage(s,"Default Profile Image","Are you sure you want to make this your defoult Profile Photo?");
                    }
                }
            });
        }

        holder.img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SceneKey.sessionManager.getUserInfo().getUserImage().equalsIgnoreCase(WebServices.USER_IMAGE +list.get(position).getKey())){
                    showDefaultDialog(context.getString(R.string.default_deleted_title), context.getString(R.string.default_deleted_msg));
                }
                else {
                    showDeletePopup(position);
                }
            }
        });
    }

    public void showDefaultDialog(String title, String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_title_btn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //      deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id

        TextView tvPopupOk, tvTitle, tvMessages;

        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvMessages = dialog.findViewById(R.id.tvMessages);

        tvPopupOk = dialog.findViewById(R.id.tvPopupOk);
        tvPopupOk.setText(R.string.ok);

        tvTitle.setText(title);
        tvMessages.setText(msg);

        tvPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show location settings when the user acknowledges the alert dialog
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void showDeletePopup(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_popup_with_btn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //      deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id

        TextView tvTitle, tvPopupOk, tvPopupCancel, tvMessages;

        tvTitle = dialog.findViewById(R.id.tvTitle);

        tvMessages = dialog.findViewById(R.id.tvMessages);


        tvPopupOk = dialog.findViewById(R.id.tvPopupOk);
        tvPopupCancel = dialog.findViewById(R.id.tvPopupCancel);

//for layout position
        tvPopupCancel.setText(R.string.yes);
        tvPopupOk.setText(R.string.cancel);

        tvTitle.setText(context.getString(R.string.delete));
        tvMessages.setText(context.getString(R.string.ques_deleted));

        tvPopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show location settings when the user acknowledges the alert dialog
                dialog.dismiss();
//                activity.removeImage(position);
                activity.removeImageFromServer(position);
            }
        });

        tvPopupOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() + 1 : 1);
    }

    private void initializePopup() {
        pop_up_option = activity.initializePopup();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_cross, img_pic;

        Holder(View itemView) {
            super(itemView);
            img_pic = itemView.findViewById(R.id.img_pic);
            img_cross = itemView.findViewById(R.id.img_cross);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {

            }
        }
    }

    public void clearList() {
        list.clear();
    }
}
