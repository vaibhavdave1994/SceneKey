package com.scenekey.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.TheRoomActivity;
import com.scenekey.helper.WebServices;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ComeInUSerAdapter extends RecyclerView.Adapter<ComeInUSerAdapter.ViewHolder> {
    private ArrayList<Card> cardArrayList;
    private Context context;
    private String name;

    public ComeInUSerAdapter(Context context, ArrayList<Card> cardArrayList, String name) {
        this.cardArrayList = cardArrayList;
        this.context = context;
        this.name = name;
    }

    @NonNull
    @Override
    public ComeInUSerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userin_view, parent, false);
        return new ComeInUSerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ComeInUSerAdapter.ViewHolder holder, final int position) {
        Card card = cardArrayList.get(position);
        int count = cardArrayList.size();
        int restCount = count - 3;
        if (position == 3) {
            holder.no_count.setVisibility(View.VISIBLE);
            holder.no_count.setText("+" + "" + restCount);
            Picasso.with(context).load(card.imageint).fit().centerCrop()
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(holder.comeInProfile);
        }

        Log.v("statuscom", card.user_status);

        switch (card.user_status) {
            case "1":
                holder.comeInProfile.setBorderColor(ContextCompat.getColor(context, R.color.go_green_color));
                break;
            case "2":
                holder.comeInProfile.setBorderColor(ContextCompat.getColor(context, R.color.caution_yellow_color));
                break;
            case "3":
                holder.comeInProfile.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
                break;
            default:
                holder.comeInProfile.setBorderColor(ContextCompat.getColor(context, R.color.go_green_color));
                break;
        }

        Glide.with(context).load(card.imageint)
                .thumbnail(0.5f)
                .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)
                .into(holder.comeInProfile);

    }

    @Override
    public int getItemCount() {
        return 4;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircularImageView comeInProfile;
        private TextView no_count;

        ViewHolder(View itemView) {
            super(itemView);
            comeInProfile = itemView.findViewById(R.id.comeInProfile);
            no_count = itemView.findViewById(R.id.no_count);
            comeInProfile.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Card card = cardArrayList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.comeInProfile:
                    Intent intent = new Intent(context, TheRoomActivity.class);
                    intent.putExtra("demoRoomarrayList", cardArrayList);
                    intent.putExtra("userName", name);
                    context.startActivity(intent);
                    break;
            }
        }
    }
}