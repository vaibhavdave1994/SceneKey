package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.activity.ImageUploadActivity;
import com.scenekey.helper.Pop_Up_Option;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.ProfileImageListener;
import com.scenekey.model.ImagesUpload;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mindiii on 21/2/18.
 */

public class ImageUpload_Adapter extends RecyclerView.Adapter<ImageUpload_Adapter.Holder> {

    private ArrayList<ImagesUpload> list;
    private ImageUploadActivity activity;
    private Pop_Up_Option pop_up_option;
    private Context context;
    private ProfileImageListener profileImageListener;

    public ImageUpload_Adapter(ImageUploadActivity activity) {
        list = new ArrayList<>();
        this.activity = activity;
        context = activity;
        initializePopup();
    }

    private static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;

        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.custom_image_upload, null));
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {

        if (position >= 6) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }
        holder.itemView.setVisibility(View.VISIBLE);
        if (position == list.size()) {

            holder.img_cross.setVisibility(View.GONE);
            //holder.img_pic.setImageBitmap(null);
            holder.img_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.size() >= 5) {
                        Toast.makeText(activity, R.string.w_photo_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pop_up_option.setObject(null);
                    pop_up_option.show();
                }
            });
        } else {
            holder.img_cross.setVisibility(View.VISIBLE);
            if (list.get(position).getBitmap() != null)
                holder.img_pic.setImageBitmap(list.get(position).getBitmap());
            else {
                Picasso.with(activity).load(list.get(position).getPath()).into(holder.img_pic);
            }

            final String s = list.get(position).getKey() + "";
            holder.img_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (s.length() > 5) {
                        activity.setImage(s);
                        //setDefoultProfileImage(s,"Default Profile Image","Are you sure you want to make this your defoult Profile Photo?");
                    }
                }
            });

            holder.img_cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.e("adapter", SceneKey.sessionManager.getUserInfo().getUserImage() + " d " + WebServices.USER_IMAGE + s);
                    if (SceneKey.sessionManager.getUserInfo().getUserImage().equalsIgnoreCase(WebServices.USER_IMAGE + s)) {
                        showDefaultDialog(context.getString(R.string.default_deleted_title), context.getString(R.string.default_deleted_msg));
                    } else {
                        showDeletePopup(position);
                    }
                }
            });
        }
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
                activity.removeImage(position);
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

    public void addImage(Bitmap bitmap) {
        list.add(new ImagesUpload(cropToSquare(bitmap)));
        //notifyDataSetChanged();
        notifyItemInserted(list.size() - 1);
    }

    public void addImage(String string) {

        list.add(new ImagesUpload(string));
        //notifyDataSetChanged();
        notifyItemInserted(list.size() - 1);
    }

    public void clearList() {
        list.clear();
    }

    public void addImage(String string, Bitmap bitmap) {
        try {
            list.add(new ImagesUpload(string, cropToSquare(bitmap)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyItemInserted(list.size() - 1);

    }

    private void initializePopup() {
        pop_up_option = activity.initializePopup();
    }

    public ImagesUpload getListObject(int position) {
        return list.get(position);
    }

    public int getlistSize() {
        return list.size();
    }

    public ArrayList<ImagesUpload> getList() {
        return list;
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_cross, img_pic;

        Holder(View itemView) {
            super(itemView);
            img_pic = itemView.findViewById(R.id.img_pic);
            img_cross = itemView.findViewById(R.id.img_cross);
            img_cross.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {

            }
        }
    }
}
