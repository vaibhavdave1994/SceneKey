package com.scenekey.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.activity.SearchSubCategoryActivity;
import com.scenekey.activity.TagsActivity;
import com.scenekey.activity.TrendinSearchActivity;
import com.scenekey.helper.Pop_Up_Option_Follow_Unfollow;
import com.scenekey.listener.FollowUnfollowLIstner;
import com.scenekey.model.TagModal;
import com.scenekey.model.VenueBoard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tags_Adapter extends RecyclerView.Adapter<Tags_Adapter.ViewHolder> {
    private ArrayList<TagModal> tagList;
    private Context context;
    String catId = "";
    String category_name = "";
    TagsActivity activity;
    FollowUnfollowLIstner followUnfollowLIstner;
    boolean fromProfile = false;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public Tags_Adapter(boolean fromProfile, Context context, ArrayList<TagModal> tagList,String catId,String category_name,
                        FollowUnfollowLIstner followUnfollowLIstner) {
        this.tagList = tagList;
        this.context = context;
        activity = (TagsActivity) context;
        this.catId = catId;
        this.category_name = category_name;
        this.followUnfollowLIstner = followUnfollowLIstner;
        this.fromProfile = fromProfile;
    }

    @NonNull
    @Override
    public Tags_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_views_layout, parent, false);
        return new Tags_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Tags_Adapter.ViewHolder holder, final int position) {
        final TagModal tagModal = tagList.get(position);

        holder.tag_name.setText(tagModal.tag_name);

        holder.iv_tag_circulerImage.setBorderColor(Color.parseColor(tagModal.color_code));
        if(tagModal.isVenue == null){
            tagModal.isVenue = "0";
        }
//        if(tagModal.isVenue.equalsIgnoreCase("1")){
//            Picasso.with(context).load(tagModal.tag_image).placeholder(R.drawable.app_icon)
//                    .error(R.drawable.app_icon).into(holder.iv_tag_circulerImage);
//        }
//        else {
            Picasso.with(context).load(tagModal.tag_image).placeholder(R.drawable.app_icon)
                    .error(R.drawable.app_icon).into(holder.iv_imageView);
       // }

        if(!fromProfile){
            if(tagModal.is_tag_follow.equalsIgnoreCase("0")){
                holder.iv_checked.setVisibility(View.GONE);
            }else {
                holder.iv_checked.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.iv_checked.setVisibility(View.GONE);
        }
        holder.mainRoomView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                if(tagModal != null) {
                    if(tagModal.category_name != null){
                    if (!tagModal.category_name.equalsIgnoreCase("Specials") &&
                            !tagModal.category_name.equalsIgnoreCase("Happy Hour")) {
                        if (tagModal.is_tag_follow.equalsIgnoreCase("0")) {
                            followUnfollowDialog(tagModal, 1);

                        } else {
                            followUnfollowDialog(tagModal, 0);
                        }

                    }
                    }
                    else {

                    }
                }
                return false;

            }
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView iv_tag_circulerImage;
        private ImageView iv_imageView,iv_checked;
        private TextView tag_name;
        private RelativeLayout mainRoomView;
        View view_followed;

        ViewHolder(View itemView) {
            super(itemView);
            iv_tag_circulerImage = itemView.findViewById(R.id.iv_tag_circulerImage);
            iv_imageView = itemView.findViewById(R.id.iv_imageView);
            iv_checked = itemView.findViewById(R.id.iv_checked);
            view_followed = itemView.findViewById(R.id.view_followed);
            tag_name = itemView.findViewById(R.id.tag_name);
            mainRoomView = itemView.findViewById(R.id.mainRoomView);
            mainRoomView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.mainRoomView:

                    TagModal tagModal = tagList.get(getAdapterPosition());
                    switch (view.getId()) {
                        case R.id.mainRoomView:
                            Intent intent;
                            if(category_name.equalsIgnoreCase("Specials") || category_name.equalsIgnoreCase("Happy Hour")){
                                intent = new Intent(context, SearchSubCategoryActivity.class);
                                intent.putExtra("tagModal", tagModal);
                                intent.putExtra("catId", catId);
                                intent.putExtra("fromSpecial", true);
                            }
                            else {
                                intent = new Intent(context, TrendinSearchActivity.class);
                                intent.putExtra("tag_name", tagModal.tag_name);
                                intent.putExtra("tag_image", tagModal.tag_image);
                                intent.putExtra("from_tagadapter", true);
                            }

                            context.startActivity(intent);
                            break;
                    }
            }
        }
    }

    private void followUnfollowDialog(final Object object, final int followUnfollow) {

        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_follow_unfollow_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottTop; //style id

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);
        TextView unfollow_text = dialog.findViewById(R.id.unfollow_text);
        TextView follow_text = dialog.findViewById(R.id.follow_text);

        follow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagModal tagModal = (TagModal) object;
//                followUnfollowLIstner.getFollowUnfollow(1,tagModal.biz_tag_id);
                activity.tagFollowUnfollow(1,tagModal.biz_tag_id,1);
                dialog.dismiss();
            }
        });
        unfollow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagModal tagModal = (TagModal) object;
//                followUnfollowLIstner.getFollowUnfollow(0,tagModal.biz_tag_id);
                activity.tagFollowUnfollow(0,tagModal.biz_tag_id,1);
                dialog.dismiss();
                dialog.dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if(followUnfollow == 0){
            unfollow_text.setVisibility(View.VISIBLE);
            follow_text.setVisibility(View.GONE);
        }
        else {
            unfollow_text.setVisibility(View.GONE);
            follow_text.setVisibility(View.VISIBLE);
        }
        dialog.show();
    }
}



