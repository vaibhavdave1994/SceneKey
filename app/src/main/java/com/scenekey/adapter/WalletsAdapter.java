package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.listener.MyRedeemListener;
import com.scenekey.listener.MySelecteOfferListener;
import com.scenekey.model.Wallets;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WalletsAdapter  extends RecyclerView.Adapter<WalletsAdapter.ViewHolder> {
    private ArrayList<Wallets> walletsArrayList;
    private Context context;
    private MyRedeemListener myRedeemListener;

    public WalletsAdapter(Context context, ArrayList<Wallets> walletsArrayList,MyRedeemListener myRedeemListener) {
        this.walletsArrayList = walletsArrayList;
        this.context = context;
        this.myRedeemListener = myRedeemListener;
    }

    @NonNull
    @Override
    public WalletsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallets_view_layout, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_adapter_reward, parent, false);
        return new WalletsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WalletsAdapter.ViewHolder holder, final int position) {
        Wallets wallets = walletsArrayList.get(position);
        holder.wallets_offerUserName.setText(wallets.business_name);
        holder.venue_wallets_address.setText(wallets.venue_address);
        holder.tv_reward_wallets_language.setText(wallets.reward_language);



        if(!wallets.exp.equals("1")){
            holder.txt_wallets_exp_days.setText(wallets.exp+""+" days");
        }else {
            holder.txt_wallets_exp_days.setText("today");
        }

        Picasso.with(context).load(wallets.venue_image).fit().centerCrop()
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)
                .into(holder.venue_wallets_image);

    }

    @Override
    public int getItemCount() {
        return walletsArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView venue_wallets_address,wallets_offerUserName,btn_redeem_wallets,tv_reward_wallets_language,txt_wallets_exp_days;
//        private ImageView venue_wallets_image;
        private CircleImageView venue_wallets_image;

        ViewHolder(View itemView) {
            super(itemView);
            venue_wallets_address = itemView.findViewById(R.id.venue_wallets_address);
            wallets_offerUserName = itemView.findViewById(R.id.wallets_offerUserName);
            tv_reward_wallets_language = itemView.findViewById(R.id.tv_reward_wallets_language);
            venue_wallets_image = itemView.findViewById(R.id.venue_wallets_image);
            btn_redeem_wallets = itemView.findViewById(R.id.btn_redeem_wallets);
            txt_wallets_exp_days = itemView.findViewById(R.id.txt_wallets_exp_days);
            btn_redeem_wallets.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Wallets wallets = walletsArrayList.get(getAdapterPosition());
            switch (view.getId()){
                case R.id.btn_redeem_wallets:
                    redeemForReward(context,wallets);
                    break;
            }
        }
    }

    /*........................diloagForAddtowallet().............................*/
    public void redeemForReward(final Context context, final Wallets wallets) {

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.reddemforreward_layout);

        TextView redeem_No = dialog.findViewById(R.id.redeem_No);
        TextView redeem_Yes = dialog.findViewById(R.id.redeem_Yes);
        redeem_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRedeemListener.MyReddemSelcteListenr(wallets);
                dialog.dismiss();
            }
        });

        redeem_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}


