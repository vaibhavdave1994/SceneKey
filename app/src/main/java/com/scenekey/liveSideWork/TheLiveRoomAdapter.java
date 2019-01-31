package com.scenekey.liveSideWork;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.DemoProfileActivity;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TheLiveRoomAdapter  extends RecyclerView.Adapter<TheLiveRoomAdapter.ViewHolder> {

    private ArrayList<Card> liveRoomList;
    private Context context;

    public TheLiveRoomAdapter(ArrayList<Card> liveRoomList, Context context) {
        this.liveRoomList = liveRoomList;
        this.context = context;
    }


    @Override
    public TheLiveRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_demo_room, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comin_user_profile_view, parent, false);
        //cominuserrofile_view
        return new TheLiveRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TheLiveRoomAdapter.ViewHolder holder, int i) {

        Card card = liveRoomList.get(i);

        if (card.user_status.equals("1")) {
            holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.go_green_color));
            holder.et_comeInName.setTextColor(context.getResources().getColor(R.color.go_green_color));

        } else if (card.user_status.equals("2")) {
            holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.caution_yellow_color));
            holder.et_comeInName.setTextColor(context.getResources().getColor(R.color.caution_yellow_color));
        } else {
            holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
            holder.et_comeInName.setTextColor(context.getResources().getColor(R.color.stop_red_color));
        }

        holder.et_comeInName.setText(liveRoomList.get(i).name);
        if(!liveRoomList.get(i).userImage.equals("")){
            Picasso.with(context).load(liveRoomList.get(i).userImage).into(holder.comeInUserProfile);
        }else
            Picasso.with(context).load(liveRoomList.get(i).imageint).into(holder.comeInUserProfile);

    }

    @Override
    public int getItemCount() {
        return liveRoomList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircularImageView comeInUserProfile;
        private TextView et_comeInName;
        private LinearLayout mainRoomView;

        ViewHolder(View view) {
            super(view);
            et_comeInName = view.findViewById(R.id.et_comeInName);
            comeInUserProfile = view.findViewById(R.id.comeInUserProfile);
            mainRoomView = view.findViewById(R.id.mainRoomView);
            mainRoomView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.mainRoomView:
                  /*  if (getAdapterPosition() != -1) {
                        //Intent intent = new Intent(context, DemoProfileActivity.class);
                        Intent intent = new Intent(context, LiveProfileActivity.class);
                        intent.putExtra("fromLiveRoomAdptrPostion", getAdapterPosition());
                        intent.putExtra("fromLiveRoomAdptrList", liveRoomList);
                        context.startActivity(intent);
                    }*/
                    break;
            }
        }
    }
}

