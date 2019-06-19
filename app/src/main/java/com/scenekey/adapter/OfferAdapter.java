package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.activity.EventDetailsActivity;
import com.scenekey.activity.ZoomImageActivity;
import com.scenekey.listener.MySelecteOfferListener;
import com.scenekey.model.Offers;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferAdapter  extends RecyclerView.Adapter {
    private ArrayList<Offers> offersArrayList;
    private Context context;
    private MySelecteOfferListener mySelecteOfferListener;
    Utility utility;
    public int TYPE_REWARD = 0;
    public int TYPE_TAG = 1;

    public OfferAdapter(Context context, ArrayList<Offers> offersArrayList, MySelecteOfferListener mySelecteOfferListener) {
        this.offersArrayList = offersArrayList;
        this.context = context;
        this.mySelecteOfferListener = mySelecteOfferListener;
        utility = new Utility(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_view_layout, parent, false);
        View view;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_adapter_alert, parent, false);
                return new ViewHolderReward(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_adapter_alert_tags, parent, false);
                return new ViewHolderTag(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Offers offers = offersArrayList.get(position);

        if(offers.alert_type.equalsIgnoreCase("reward")){
            ((ViewHolderReward) holder).offerUserName.setText(offers.business_name);
            ((ViewHolderReward) holder).tv_reward_language.setText(offers.reward_language);
            ((ViewHolderReward) holder).tv_point.setText(offers.point+" Keypoints");
            ((ViewHolderReward) holder).txt_exp_days.setText(offers.crd);
            if(!offers.exp.equals("1")){
                ((ViewHolderReward) holder).tv_exp_new.setText("(Expires in "+offers.exp+""+" days)");
            }else {
//            holder.txt_exp_days.setText("today");
                ((ViewHolderReward) holder).tv_exp_new.setText("(Expires in "+offers.exp+""+" day)");
            }

            if(!offers.reward_image.equals("")){
                Picasso.with(context).load(offers.reward_image).fit().centerCrop()
                        .into( ((ViewHolderReward) holder).venue_image);
            }
            else{
                ((ViewHolderReward) holder).venue_image.setVisibility(View.GONE);
            }

            Picasso.with(context).load(offers.venue_image).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into( ((ViewHolderReward) holder).civ);

            ((ViewHolderReward) holder).rv_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( ((ViewHolderReward) holder).venue_image.getVisibility() == View.GONE){
                        //utility.showCustomPopup(context.getString(R.string.plz_add_reward), String.valueOf(R.font.montserrat_medium));
                        diloagForAddtowallet(context,offers);
                    }
                    else {
                        diloagForImage(context,offers);
                    }
                }
            });
        }
        else {
            ((ViewHolderTag) holder).offerUserName.setText(offers.venue_name);
            if(offers.category_name.equalsIgnoreCase("Specials")){
                ((ViewHolderTag) holder).tv_reward.setText("Special : "+offers.tag_text);
            }
            else {
                ((ViewHolderTag) holder).tv_reward.setText(offers.category_name);
            }

            ((ViewHolderTag) holder).tv_description.setText(offers.message);
            Picasso.with(context).load(offers.venue_image).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into( ((ViewHolderTag) holder).civ);
//            if(!offers.exp.equals("1")){
//                ((ViewHolderReward) holder).txt_exp_days.setText(offers.exp+""+" days");
//            }else {
//                ((ViewHolderReward) holder).txt_exp_days.setText(offers.exp+""+" day");
//            }
        }
    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch (offersArrayList.get(position).alert_type) {
            case "tag":
                return TYPE_TAG;

            case "reward":
                return TYPE_REWARD;

            default:
                return -1;
        }
    }

    class ViewHolderReward extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView offerUserName,venue_address,tv_point,tv_reward_language,txt_exp_days,addIconForWallet;
        private ImageView venue_image;
        CircleImageView civ;
        RelativeLayout rv_main;
        TextView tv_exp_new;

        ViewHolderReward(View itemView) {
            super(itemView);
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
            switch (view.getId()){

                case R.id.addIconForWallet:
                    diloagForAddtowallet(context,offersforWallets);
                    break;

            }
        }
    }

    class ViewHolderTag extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView offerUserName,tv_description,txt_exp_days,tv_reward;
        CircleImageView civ;
        RelativeLayout rv_main;

        ViewHolderTag(View itemView) {
            super(itemView);
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
            switch (view.getId()){

            }
        }
    }


    /*........................doyoulike().............................*/
    public void diloagForAddtowallet(final Context context, final Offers offersforWallets) {

        final Dialog dialog = new Dialog(context);
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

        final Dialog dialog = new Dialog(context);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black20p))));
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
        if(!offers.exp.equals("1")){
            txt_exp_days.setText(offers.exp+""+" days");
        }else {
//            holder.txt_exp_days.setText("today");
            txt_exp_days.setText(offers.exp+""+" day");
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}


