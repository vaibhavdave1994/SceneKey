package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scenekey.R;
import com.scenekey.activity.TagsActivity;
import com.scenekey.activity.TrendinSearchActivity;
import com.scenekey.fragment.NewSearchkFragment;
import com.scenekey.helper.CustomProgressBar;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.FollowUnfollowLIstner;
import com.scenekey.model.TagModal;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tags_SpecialAdapter extends RecyclerView.Adapter<Tags_SpecialAdapter.ViewHolder> {
    private ArrayList<TagModal> tagList;
    private Context context;
    private Utility utility;
    private CustomProgressBar customProgressBar;
    NewSearchkFragment newSearchkFragment;
    FollowUnfollowLIstner followUnfollowLIstner;

    public Tags_SpecialAdapter(Context context, ArrayList<TagModal> tagList, FollowUnfollowLIstner followUnfollowLIstner) {
        this.tagList = tagList;
        this.context = context;
        utility = new Utility(context);
        customProgressBar = new CustomProgressBar(context);
        this.followUnfollowLIstner = followUnfollowLIstner;
    }

    @NonNull
    @Override
    public Tags_SpecialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_special_layout, parent, false);
        return new Tags_SpecialAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Tags_SpecialAdapter.ViewHolder holder, final int position) {

        if (position == tagList.size() - 1) {
            holder.view.setVisibility(View.VISIBLE);
        } else {
            holder.view.setVisibility(View.GONE);
        }

        TagModal tagModal = tagList.get(position);

        if (tagModal.tag_text == null || tagModal.tag_text.equals("")) {
            holder.tag__special_text.setVisibility(View.GONE);
            holder.tag__special_name.setText(tagModal.tag_name);
        } else {
            holder.tag__special_name.setText(tagModal.tag_text);
            holder.tag__special_text.setText(tagModal.tag_name);
        }

       // if(context instanceof TagsActivity){
            holder.tag__special_text.setText(tagModal.category_name);
            holder.tag__special_text.setVisibility(View.VISIBLE);
       // }

        holder.outerBouder.setBorderColor(Color.parseColor(tagModal.color_code));
        Picasso.with(context).load(tagModal.tag_image).placeholder(R.drawable.app_icon)
                .error(R.drawable.app_icon).into(holder.iv_tag__special_circulerImage);

        if(tagModal.status.equalsIgnoreCase("active")){
            holder.blurView.setVisibility(View.GONE);
        }
        else {
            holder.blurView.setVisibility(View.VISIBLE);
        }

        if(tagModal.is_tag_follow.equalsIgnoreCase("0")){
            holder.tv_follow.setVisibility(View.VISIBLE);
            holder.tv_unfollow.setVisibility(View.GONE);
            holder.view_followed.setVisibility(View.GONE);
        }
        else {
            holder.tv_follow.setVisibility(View.GONE);
            holder.tv_unfollow.setVisibility(View.VISIBLE);
            holder.view_followed.setVisibility(View.GONE);
        }

        holder.mainRoomView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_tag__special_circulerImage;
        private CircleImageView outerBouder;
        private TextView tag__special_name, tag__special_text;
        private View view;
        private View blurView;
        private LinearLayout mainRoomView;
        TextView tv_follow,tv_unfollow;
        View view_followed;

        ViewHolder(View itemView) {
            super(itemView);
            tv_follow = itemView.findViewById(R.id.tv_follow);
            tv_follow.setOnClickListener(this);
            tv_unfollow = itemView.findViewById(R.id.tv_unfollow);
            tv_unfollow.setOnClickListener(this);
            iv_tag__special_circulerImage = itemView.findViewById(R.id.iv_tag__special_circulerImage);
            tag__special_name = itemView.findViewById(R.id.tag__special_name);
            outerBouder = itemView.findViewById(R.id.outerBouder);
            mainRoomView = itemView.findViewById(R.id.mainRoomView);
            tag__special_text = itemView.findViewById(R.id.tag__special_text);
            view = itemView.findViewById(R.id.view);
            blurView = itemView.findViewById(R.id.blurView);
            view_followed = itemView.findViewById(R.id.view_followed);
            mainRoomView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            TagModal tagModal = tagList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.mainRoomView:
                    //if(tagModal.status.equalsIgnoreCase("active")){
                        Intent intent = new Intent(context, TrendinSearchActivity.class);
                        intent.putExtra("tag_name", tagModal.tag_name);
                        intent.putExtra("tag_image", tagModal.tag_image);
                        intent.putExtra("tagmodel", tagModal);
                        intent.putExtra("from_tagadapter", true);
                        context.startActivity(intent);
                    //}
                    break;

                case R.id.tv_follow:
                    followUnfollowLIstner.getFollowUnfollow(1,tagModal.biz_tag_id,getAdapterPosition());
                    break;

                case R.id.tv_unfollow:
                    followUnfollowLIstner.getFollowUnfollow(0,tagModal.biz_tag_id,getAdapterPosition());
                    break;
            }
        }
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

        tvTitle.setText(context.getString(R.string.follow));
        tvMessages.setText(context.getString(R.string.ques_tag_follow));

        tvPopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show location settings when the user acknowledges the alert dialog
                dialog.dismiss();

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

    public void showProgDialog(boolean b, String TAG) {
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

}