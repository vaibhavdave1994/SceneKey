package com.scenekey.adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.DemoFeedUser;
import com.scenekey.activity.DemoProfileActivity;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {
    private ArrayList<Card> cardArrayList;
    private Context context;

    public CustomListAdapter(Context context, ArrayList<Card> cardArrayList) {
        this.cardArrayList = cardArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_comment_view, parent, false);
        return new CustomListAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final CustomListAdapter.ViewHolder holder, final int position) {
        Card card = cardArrayList.get(position);

        holder.demo_user_name.setText(card.name);


        Log.v("time", card.date);
      /*  if (card.date != null && !card.date.isEmpty()) {

            String startTime = card.date;
            StringTokenizer tk = new StringTokenizer(startTime);
            String time = tk.nextToken();
            String date = tk.nextToken();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat dateinPmAm = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
             Date dt = null;
            try {
                dt = sdf.parse(date);
                String sap_time = dateinPmAm.format(dt);
                holder.demo_date_tv.setText(sap_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }*/


     /*   String startTime = card.date;
        StringTokenizer tk = new StringTokenizer(startTime);
        String time = tk.nextToken();
        String date = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateinPmAm = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
         Date dt = null;
        try {
            dt = sdf.parse(date);

           String sap_time = dateinPmAm.format(dt);
            Log.v("time", card.date);
            holder.demo_date_tv.setText(sap_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/


        holder.demo_date_tv.setText(card.date);

        switch (card.user_status) {
            case "1":
                holder.demo_profile_user.setBorderColor(ContextCompat.getColor(context, R.color.go_green_color));
                break;

            case "2":
                holder.demo_profile_user.setBorderColor(ContextCompat.getColor(context, R.color.caution_yellow_color));
                break;

            default:
                holder.demo_profile_user.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
                break;
        }

        if (card.imageint != 0) {
            Picasso.with(context).load(card.imageint).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .into(holder.demo_profile_user);
        } else if (!card.userImage.isEmpty()) {
            Picasso.with(context).load(card.userImage).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .into(holder.demo_profile_user);
        }

        if (!card.text.isEmpty()) {
            holder.img_demo_event.setVisibility(View.GONE);
            holder.txt_demo_comment.setVisibility(View.VISIBLE);
            holder.txt_demo_comment.setText(card.text);
        }

        if (card.uploadImage != 0) {
            holder.img_demo_event.setVisibility(View.VISIBLE);
            holder.txt_demo_comment.setVisibility(View.GONE);
            Picasso.with(context).load(card.uploadImage).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .into(holder.img_demo_event);
        }


        if (card.uri != null) {
            holder.img_demo_event.setVisibility(View.VISIBLE);
            holder.txt_demo_comment.setVisibility(View.GONE);
            Picasso.with(context).load(card.uri).fit().centerCrop()
                    .placeholder(R.drawable.bg_event_card)
                    .error(R.drawable.bg_event_card)
                    .into(holder.img_demo_event);
        }

        try {
            if (!card.text.isEmpty()) {
                holder.img_demo_event.setVisibility(View.GONE);
                holder.txt_demo_comment.setVisibility(View.VISIBLE);
                holder.txt_demo_comment.setText(card.text);
            } else if (card.uploadImage == 0) {
                holder.img_demo_event.setVisibility(View.VISIBLE);
                holder.txt_demo_comment.setVisibility(View.GONE);
                Picasso.with(context).load(card.uploadImage).fit().centerCrop()
                        .placeholder(R.drawable.bg_event_card)
                        .error(R.drawable.bg_event_card)
                        .into(holder.img_demo_event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cardArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img_demo_event;
        private CircularImageView demo_profile_user;

        private TextView demo_user_name, txt_demo_comment, demo_date_tv;

        ViewHolder(View itemView) {
            super(itemView);
            demo_profile_user = itemView.findViewById(R.id.demo_profile_user);
            demo_user_name = itemView.findViewById(R.id.demo_user_name);
            txt_demo_comment = itemView.findViewById(R.id.txt_demo_comment);
            demo_date_tv = itemView.findViewById(R.id.demo_date_tv);
            img_demo_event = itemView.findViewById(R.id.img_demo_event);
            demo_profile_user.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Card userdetail = cardArrayList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.demo_profile_user:
                    Intent intent = new Intent(context, DemoFeedUser.class);
                    intent.putExtra("fromcustonList", cardArrayList);
                    intent.putExtra("fromcustonmodal", userdetail);
                    intent.putExtra("fromcustonPostion", getAdapterPosition());
                    context.startActivity(intent);
                    break;
            }
        }
    }
}
