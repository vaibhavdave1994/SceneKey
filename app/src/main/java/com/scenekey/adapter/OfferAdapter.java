package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.OnBoardActivity;
import com.scenekey.activity.invite_friend.InviteFriendsActivity;
import com.scenekey.activity.trending_summery.Summary_Activity;
import com.scenekey.listener.MySelecteOfferListener;
import com.scenekey.model.Event;
import com.scenekey.model.Events;
import com.scenekey.model.Offers;
import com.scenekey.model.Venue;
import com.scenekey.util.SceneKey;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferAdapter extends RecyclerView.Adapter {
    public int TYPE_REWARD = 1;
    public int TYPE_TAG = 2;
    public int TYPE_FRIEND = 0;
    public String inviteDate;
    public String alert_type;
    Utility utility;
    HomeActivity activity;
    private ArrayList<Offers> offersArrayList;
    private Context context;
    private MySelecteOfferListener mySelecteOfferListener;
    private ArrayList<Events> eventsArrayList;

    public OfferAdapter(HomeActivity activity, Context context, ArrayList<Offers> offersArrayList, ArrayList<Events> eventsArrayList, MySelecteOfferListener mySelecteOfferListener, String alert_type) {
        this.offersArrayList = offersArrayList;
        this.context = context;
        this.mySelecteOfferListener = mySelecteOfferListener;
        this.alert_type = alert_type;
        this.activity = activity;
        this.eventsArrayList = eventsArrayList;
        utility = new Utility(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_view_layout, parent, false);
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_adapter_alert, parent, false);
                return new ViewHolderFriend(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_adapter_alert, parent, false);
                return new ViewHolderReward(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_adapter_alert_tags, parent, false);
                return new ViewHolderTag(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Offers offers = offersArrayList.get(position);

        if (offers.alert_type.equalsIgnoreCase("reward")) {
            ((ViewHolderReward) holder).offerUserName.setText(offers.business_name);
            ((ViewHolderReward) holder).tv_reward_language.setText(offers.reward_language);
            ((ViewHolderReward) holder).tv_point.setText(offers.point + " Keypoints");
            ((ViewHolderReward) holder).txt_exp_days.setText(offers.crd);
            if (!offers.exp.equals("1")) {
                ((ViewHolderReward) holder).tv_exp_new.setText("(Expires in " + offers.exp + "" + " days)");
            } else {
                ((ViewHolderReward) holder).tv_exp_new.setText("(Expire today)");
                //((ViewHolderReward) holder).tv_exp_new.setText("(Expires in "+offers.exp+""+" day)");
            }

            if (!offers.reward_image.equals("")) {
                Picasso.with(context).load(offers.reward_image).fit().centerCrop()
                        .into(((ViewHolderReward) holder).venue_image);
                ((ViewHolderReward) holder).venue_image.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolderReward) holder).venue_image.setVisibility(View.GONE);
            }

            Picasso.with(context).load(offers.venue_image).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(((ViewHolderReward) holder).civ);

            ((ViewHolderReward) holder).rv_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ViewHolderReward) holder).venue_image.getVisibility() == View.GONE) {
                        if (Integer.parseInt(SceneKey.sessionManager.getUserInfo().key_points)> 15) {
                            diloagForAddtowallet(context, offers);
                        }
                        else {
                            Utility.showCheckConnPopup(activity,context.getResources().getString(R.string.not_enough_keypoint),"", String.valueOf(R.font.montserrat_medium));

                        }
                    } else {
                        diloagForImage(context, offers);
                    }
                }
            });

            ((ViewHolderReward) holder).rl_summery.setOnClickListener(v -> {
                Intent intent = new Intent(context, Summary_Activity.class);
                intent.putExtra("venue_id", offers.venue_id);
                context.startActivity(intent);
            });
        } else if (offers.alert_type.equalsIgnoreCase("tag")) {
            ((ViewHolderTag) holder).offerUserName.setText(offers.venue_name);
            if (offers.category_name.equalsIgnoreCase("Specials")) {
                ((ViewHolderTag) holder).tv_reward.setText("Special  " + offers.tag_text);
            } else {
                ((ViewHolderTag) holder).tv_reward.setText(offers.category_name + "  " + offers.tag_text);
            }

            ((ViewHolderTag) holder).txt_exp_days.setText(offers.crd);

            ((ViewHolderTag) holder).tv_description.setText(offers.message);
            Picasso.with(context).load(offers.venue_image).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(((ViewHolderTag) holder).civ);

            ((ViewHolderTag) holder).rv_main.setOnClickListener(v -> {

                Intent intent = new Intent(context, OnBoardActivity.class);
                intent.putExtra("frequency", offers.frequency_all);
                intent.putExtra("venuid", offers.venue_id);
                intent.putExtra("event_name", offers.event_name);
                intent.putExtra("fromAlert", true);

                context.startActivity(intent);
            });

            ((ViewHolderTag) holder).rl_summery.setOnClickListener(v -> {

                Intent intent = new Intent(context, Summary_Activity.class);
                intent.putExtra("venue_id", offers.venue_id);
                context.startActivity(intent);
            });
//            if(!offers.exp.equals("1")){
//                ((ViewHolderReward) holder).txt_exp_days.setText(offers.exp+""+" days");
//            }else {
//                ((ViewHolderReward) holder).txt_exp_days.setText(offers.exp+""+" day");
//            }
        }
        else {

            ((ViewHolderFriend) holder).txt_daysago.setText(offers.inviteDate);
        }
    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch (offersArrayList.get(position).alert_type) {

            case "friend":
                return TYPE_FRIEND;

            case "tag":
                return TYPE_TAG;

            case "reward":
                return TYPE_REWARD;

            default:
                return -1;
        }
    }

    /*........................doyoulike().............................*/
    public void diloagForAddtowallet(final Context context, final Offers offersforWallets) {

        final Dialog dialog = new Dialog(context,R.style.DialogTheme);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.addtowallet_dialog_view);

        TextView custom_popup_No = dialog.findViewById(R.id.custom_popup_No);
        TextView custom_popup_Yes = dialog.findViewById(R.id.custom_popup_Yes);
        custom_popup_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySelecteOfferListener.getSelectedOffer(offersforWallets);
                dialog.dismiss();
            }
        });

        custom_popup_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void diloagForImage(final Context context, final Offers offers) {

        final Dialog dialog = new Dialog(context,R.style.DialogTheme);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_alert_img_show);
        TextView offerUserName = dialog.findViewById(R.id.offerUserName);
        TextView txt_exp_days = dialog.findViewById(R.id.txt_exp_days);
        TextView tv_reward_language = dialog.findViewById(R.id.tv_reward_language);
        ImageView imageView = dialog.findViewById(R.id.iv);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        Picasso.with(context).load(offers.reward_image).fit().centerCrop()
                .into(imageView);
        offerUserName.setText(offers.business_name);
        tv_reward_language.setText(offers.reward_language);
        if (!offers.exp.equals("1")) {
            txt_exp_days.setText(offers.crd);
        } else {
//            holder.txt_exp_days.setText("today");
            txt_exp_days.setText(offers.crd);
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class ViewHolderFriend extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_daysago,invite_btn;

        ViewHolderFriend(View itemView) {
            super(itemView);
            txt_daysago = itemView.findViewById(R.id.txt_daysago);
            invite_btn = itemView.findViewById(R.id.invite_btn);
            invite_btn.setOnClickListener(this);
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, InviteFriendsActivity.class);
                context.startActivity(intent);
            });
        }

        @Override
        public void onClick(View view) {
//            Offers offersforWallets = offersArrayList.get(getAdapterPosition());
            if (view.getId() == R.id.invite_btn) {
                Intent intent = new Intent(context, InviteFriendsActivity.class);
                context.startActivity(intent);
            }
        }

    }

    class ViewHolderReward extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView civ;
        RelativeLayout rv_main,rl_summery;
        TextView tv_exp_new;
        private TextView offerUserName, venue_address, tv_point, tv_reward_language, txt_exp_days, addIconForWallet;
        private ImageView venue_image;

        ViewHolderReward(View itemView) {
            super(itemView);
            rl_summery = itemView.findViewById(R.id.rl_summery);
            tv_exp_new = itemView.findViewById(R.id.tv_exp_new);
            offerUserName = itemView.findViewById(R.id.offerUserName);
            civ = itemView.findViewById(R.id.civ);
            //venue_address = itemView.findViewById(R.id.venue_address);
            venue_image = itemView.findViewById(R.id.venue_image);
            tv_reward_language = itemView.findViewById(R.id.tv_reward_language);
            tv_point = itemView.findViewById(R.id.tv_point);
            addIconForWallet = itemView.findViewById(R.id.addIconForWallet);
            txt_exp_days = itemView.findViewById(R.id.txt_exp_days);
            rv_main = itemView.findViewById(R.id.rv_main);
            addIconForWallet.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Offers offersforWallets = offersArrayList.get(getAdapterPosition());
            switch (view.getId()) {

                case R.id.addIconForWallet:
                    if (Integer.parseInt(SceneKey.sessionManager.getUserInfo().key_points) > 15) {
                        diloagForAddtowallet(context, offersforWallets);
                    }
                    else {
                        Utility.showCheckConnPopup(activity,context.getResources().getString(R.string.not_enough_keypoint),"", String.valueOf(R.font.montserrat_medium));

                    }
                    break;

            }
        }
    }

    class ViewHolderTag extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView civ;
        RelativeLayout rv_main,rl_summery;
        private TextView offerUserName, tv_description, txt_exp_days, tv_reward;

        ViewHolderTag(View itemView) {
            super(itemView);
            rl_summery = itemView.findViewById(R.id.rl_summery);
            offerUserName = itemView.findViewById(R.id.offerUserName);
            civ = itemView.findViewById(R.id.civ);
            tv_reward = itemView.findViewById(R.id.tv_reward);
            tv_description = itemView.findViewById(R.id.tv_description);
            txt_exp_days = itemView.findViewById(R.id.txt_exp_days);
            rv_main = itemView.findViewById(R.id.rv_main);

            // addIconForWallet.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Offers offersforWallets = offersArrayList.get(getAdapterPosition());
            switch (view.getId()) {

            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}


