package com.scenekey.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.scenekey.R;
import com.scenekey.activity.OnBoardActivity;
import com.scenekey.activity.TrendinSearchActivity;
import com.scenekey.helper.SessionManager;
import com.scenekey.helper.WebServices;
import com.scenekey.model.VenueBoard;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.scenekey.volleymultipart.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class VenueBoardInnerAdapter extends RecyclerView.Adapter<VenueBoardInnerAdapter.ViewHolder> {

    private List<VenueBoard.EventTagBean.TagListBean> venue_boardList;
    private Context context;
    Activity activity;
    OnBoardActivity onBoardActivity;
    private boolean fromTrending;
    public boolean isGridview = false;
    private Utility utility;

    public VenueBoardInnerAdapter(Context context, List<VenueBoard.EventTagBean.TagListBean> venue_boardList,boolean fromTrending,
                                  boolean isGridview) {
        this.venue_boardList = venue_boardList;
        this.context = context;
        this.fromTrending = fromTrending;
        activity = (Activity) context;
        this.onBoardActivity = (OnBoardActivity) context;
        this.isGridview = isGridview;
    }

    @NonNull
    @Override
    public VenueBoardInnerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(isGridview){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venueboard_view, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_special_vennuboard_layout, parent, false);
        }

        return new VenueBoardInnerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VenueBoardInnerAdapter.ViewHolder holder, final int position) {
        final VenueBoard.EventTagBean.TagListBean  socialMediaBean = venue_boardList.get(position);

        if(isGridview){
            holder.tag__special_name.setText(socialMediaBean.getTag_name());
            holder.outerBouder.setBorderColor(Color.parseColor(socialMediaBean.getColor_code()));
            Glide.with(context).load(socialMediaBean.getTag_image()).centerCrop().placeholder(R.drawable.app_icon)
                    .into(holder.iv_tag__special_circulerImage);

            if(socialMediaBean.getIs_tag_follow().equalsIgnoreCase("0")){
                holder.iv_checked.setVisibility(View.GONE);
            }else {
                holder.iv_checked.setVisibility(View.VISIBLE);
            }

            holder.iv_tag__special_circulerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSearchConfirmDialog(isGridview,socialMediaBean,"Scenekey","Want to search for "+socialMediaBean.getTag_name()+"?");

                }
            });

            holder.iv_tag__special_circulerImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(socialMediaBean.getIs_tag_follow().equalsIgnoreCase("0")){
                        followUnfollowDialog(socialMediaBean,1);
                    }
                    else {
                        followUnfollowDialog(socialMediaBean,0);
                    }

                    return false;
                }
            });
        }
        else {
            holder.tag__special_name.setText(socialMediaBean.getTag_text());
            holder.tag__special_text.setText(socialMediaBean.getTag_name());
            holder.outerBouder.setBorderColor(Color.parseColor(socialMediaBean.getColor_code()));
            Glide.with(context).load(socialMediaBean.getTag_image()).centerCrop().placeholder(R.drawable.app_icon)
                    .into(holder.iv_tag__special_circulerImage);

//            if(socialMediaBean.getIs_tag_follow().equalsIgnoreCase("0")){
//                holder.view_followed.setVisibility(View.GONE);
//                holder.tv_unfollow.setVisibility(View.GONE);
//                holder.tv_follow.setVisibility(View.VISIBLE);
//            }else {
//                holder.view_followed.setVisibility(View.GONE);
//                holder.tv_unfollow.setVisibility(View.VISIBLE);
//                holder.tv_follow.setVisibility(View.GONE);
//            }

            holder.mainRoomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("SceneKey");
//                    builder.setMessage("Want to search for "+socialMediaBean.getTag_name()+" ?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent intent = new Intent(context, TrendinSearchActivity.class);
//                            intent.putExtra("tag_name", socialMediaBean.getTag_name());
//                            intent.putExtra("tag_image", socialMediaBean.getTag_image());
//                            context.startActivity(intent);
//
//                        }
//                    });
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                        }
//                    });
//                    final Dialog alertDialog = builder.create();
//                    alertDialog.setCancelable(false);
//                    alertDialog.show();

                    showSearchConfirmDialog(isGridview,socialMediaBean,"Scenekey","Want to search for "+socialMediaBean.getTag_text()+"?");
                }
            });

//            holder.tv_follow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    tagFollowUnfollow(1,socialMediaBean,position);
//                }
//            });
//
//            holder.tv_unfollow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    tagFollowUnfollow(0,socialMediaBean,position);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return venue_boardList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_tag__special_circulerImage,iv_checked;
        private CircleImageView outerBouder;
        private TextView tag__special_name,tag__special_text;
        private RelativeLayout rl_main;
        private View view_followed,view;

        private LinearLayout mainRoomView;
        TextView tv_follow,tv_unfollow;

        ViewHolder(View itemView) {
            super(itemView);

            if(isGridview){
                iv_tag__special_circulerImage = itemView.findViewById(R.id.iv_tag__special_circulerImage);
                outerBouder = itemView.findViewById(R.id.outerBouder);
                tag__special_name = itemView.findViewById(R.id.tag__special_name);
                rl_main = itemView.findViewById(R.id.rl_main);
                view_followed = itemView.findViewById(R.id.view_followed);
                iv_checked = itemView.findViewById(R.id.iv_checked);
            }
            else {
                tv_follow = itemView.findViewById(R.id.tv_follow);
                tv_unfollow = itemView.findViewById(R.id.tv_unfollow);
                iv_tag__special_circulerImage = itemView.findViewById(R.id.iv_tag__special_circulerImage);
                tag__special_name = itemView.findViewById(R.id.tag__special_name);
                outerBouder = itemView.findViewById(R.id.outerBouder);
                mainRoomView = itemView.findViewById(R.id.mainRoomView);
                tag__special_text = itemView.findViewById(R.id.tag__special_text);
                view = itemView.findViewById(R.id.view);
                view_followed = itemView.findViewById(R.id.view_followed);
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
                VenueBoard.EventTagBean.TagListBean  socialMediaBean = (VenueBoard.EventTagBean.TagListBean) object;
                onBoardActivity.tagFollowUnfollow(1,socialMediaBean.getBiz_tag_id(),0);
                dialog.dismiss();
            }
        });
        unfollow_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VenueBoard.EventTagBean.TagListBean  socialMediaBean = (VenueBoard.EventTagBean.TagListBean) object;
                onBoardActivity.tagFollowUnfollow(0,socialMediaBean.getBiz_tag_id(),0);
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

    public void tagFollowUnfollow(final int followUnfollow, final VenueBoard.EventTagBean.TagListBean  socialMediaBean, final int pos) { // 0 from search, 1 for tags long press
        utility = new Utility(context);
        if (utility.checkInternetConnection()) {
            onBoardActivity.showProgDialog(true, "TAG");
            StringRequest request = new StringRequest(Request.Method.POST, WebServices.TAG_FOLLOW_UNFOLLOW, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    onBoardActivity.dismissProgDialog();
                    // get response
                    try {
                        JSONObject jo = new JSONObject(response);
                        onBoardActivity.dismissProgDialog();
                        if(jo.has("status")){
                            if(jo.getString("status").equalsIgnoreCase("success")){
                                if(followUnfollow == 0){
                                    socialMediaBean.setIs_tag_follow("0");
                                }
                                else {
                                    socialMediaBean.setIs_tag_follow("1");
                                }
                                venue_boardList.set(pos,socialMediaBean);
                                notifyItemChanged(pos);
                            }
                        }

                    } catch (Exception e) {
                        onBoardActivity.dismissProgDialog();
//                        Utility.showToast(context, context.getString(R.string.somethingwentwrong), 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    utility.volleyErrorListner(e);
                    onBoardActivity.dismissProgDialog();
                }
            }) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("biz_tag_id",socialMediaBean.getBiz_tag_id());
                    params.put("follow_status", String.valueOf(followUnfollow));
                    params.put("user_id", SceneKey.sessionManager.getUserInfo().userid);
                    return params;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request, "HomeApi");
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1));
        } else {
            onBoardActivity.dismissProgDialog();
        }
    }

    public void showSearchConfirmDialog(final boolean isGridview, final VenueBoard.EventTagBean.TagListBean  socialMediaBean, String title, String msg) {
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
        tvPopupCancel.setText(R.string.cancel);
        tvPopupOk.setText(R.string.yes);

        tvTitle.setText(title);
        tvMessages.setText(msg);

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
                SessionManager sessionManager = new SessionManager(context);
                sessionManager.backOrIntent(false);
                Intent intent = new Intent(context, TrendinSearchActivity.class);
                intent.putExtra("tag_name", socialMediaBean.getTag_name());
                intent.putExtra("tag_image", socialMediaBean.getTag_image());

                if (!isGridview) {
                    intent.putExtra("tag_text", socialMediaBean.getTag_text());
                    intent.putExtra("fromSearchSpecial", true);
                }

                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}