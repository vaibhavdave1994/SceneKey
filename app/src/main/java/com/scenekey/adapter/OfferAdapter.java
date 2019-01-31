package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.listener.MySelecteOfferListener;
import com.scenekey.model.Offers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OfferAdapter  extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    private ArrayList<Offers> offersArrayList;
    private Context context;
    private MySelecteOfferListener mySelecteOfferListener;

    public OfferAdapter(Context context, ArrayList<Offers> offersArrayList, MySelecteOfferListener mySelecteOfferListener) {
        this.offersArrayList = offersArrayList;
        this.context = context;
        this.mySelecteOfferListener = mySelecteOfferListener;
    }

    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_view_layout, parent, false);
        return new OfferAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfferAdapter.ViewHolder holder, final int position) {
        Offers offers = offersArrayList.get(position);
        holder.offerUserName.setText(offers.business_name);
        holder.venue_address.setText(offers.venue_address);
        holder.tv_reward_language.setText(offers.reward_language);
        holder.tv_point.setText(offers.point);

        if(!offers.exp.equals("1")){
            holder.txt_exp_days.setText(offers.exp+""+" days");
        }else {
            holder.txt_exp_days.setText("today");
        }
        Picasso.with(context).load(offers.venue_image).fit().centerCrop()
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)
                .into(holder.venue_image);

    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView offerUserName,venue_address,tv_point,tv_reward_language,txt_exp_days;
        private ImageView venue_image,addIconForWallet;

        ViewHolder(View itemView) {
            super(itemView);
            offerUserName = itemView.findViewById(R.id.offerUserName);
            venue_address = itemView.findViewById(R.id.venue_address);
            venue_image = itemView.findViewById(R.id.venue_image);
            tv_reward_language = itemView.findViewById(R.id.tv_reward_language);
            tv_point = itemView.findViewById(R.id.tv_point);
            addIconForWallet = itemView.findViewById(R.id.addIconForWallet);
            txt_exp_days = itemView.findViewById(R.id.txt_exp_days);
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

}


