package com.scenekey.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.TheRoomActivity;
import com.scenekey.model.EventAttendy;

import java.util.ArrayList;

public class UserInAdapter extends RecyclerView.Adapter<UserInAdapter.ViewHolder> {

    private ArrayList<EventAttendy> comeInUserList;
    private Context context;
    private String eventId;

    public UserInAdapter(ArrayList<EventAttendy> comeInUserList, Context context, String eventId) {
        this.comeInUserList = comeInUserList;
        this.context = context;
        this.eventId = eventId;
    }

    @Override
    public UserInAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userin_view, parent, false);
        return new UserInAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserInAdapter.ViewHolder holder, int i) {

        int count = comeInUserList.size();
        int restCount = count - 3;
        if (i == 3) {
            holder.no_count.setVisibility(View.VISIBLE);
            holder.no_count.setText("+" + "" + restCount);

            if(!comeInUserList.get(i).getUserimage().contains("dev-")){
                String image = "dev-"+comeInUserList.get(i).getUserimage();
                Glide.with(context).load(image)
                        .thumbnail(0.5f)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_img)
                        .error(R.drawable.placeholder_img)
                        .into(holder.comeInUserProfile);
            }else{
                Glide.with(context).load(comeInUserList.get(i).getUserimage())
                        .thumbnail(0.5f)
                        .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_img)
                        .error(R.drawable.placeholder_img)
                        .into(holder.comeInUserProfile);
            }
        }

        switch (comeInUserList.get(i).user_status) {
            case "1":
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.go_green_color));
                break;

            case "2":
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.caution_yellow_color));
                break;

            case "3":
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
                break;
            default:
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
        }

       if(!comeInUserList.get(i).getUserimage().contains("dev-")){
            String image = "dev-"+comeInUserList.get(i).getUserimage();
            //Picasso.with(context).load(image).error(R.drawable.placeholder_img).into(holder.comeInUserProfile);
            Glide.with(context).load(image)
                    .thumbnail(0.5f)
                    .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(holder.comeInUserProfile);
        }else{
            Glide.with(context).load(comeInUserList.get(i).getUserimage())
                    .thumbnail(0.5f)
                    .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(holder.comeInUserProfile);
            //Picasso.with(context).load(comeInUserList.get(i).getUserimage()).error(R.drawable.placeholder_img).into(holder.comeInUserProfile);
        }
    }

    @Override
    public int getItemCount() {
        int count = comeInUserList.size();
        if(count<=3){
            return comeInUserList.size();
        }else{
            return  4;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircularImageView comeInUserProfile;
        private RelativeLayout mainProfileView;
        private TextView no_count;

        ViewHolder(View view) {
            super(view);
            comeInUserProfile = view.findViewById(R.id.comeInProfile);
            mainProfileView = view.findViewById(R.id.mainProfileView);
            no_count = view.findViewById(R.id.no_count);
            mainProfileView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.mainProfileView:
//                    Intent intent = new Intent(context, LiveRoomActivity.class);
                    Intent intent = new Intent(context, TheRoomActivity.class);
                    intent.putExtra("commentPesionList", comeInUserList);
                    intent.putExtra("eventId",eventId);
                    context.startActivity(intent);
                    break;
            }
        }
    }
}