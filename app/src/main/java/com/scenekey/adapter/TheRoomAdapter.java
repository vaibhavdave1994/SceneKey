package com.scenekey.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.listener.RoomListener;
import com.scenekey.model.EventAttendy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TheRoomAdapter  extends RecyclerView.Adapter<TheRoomAdapter.ViewHolder> {

    private ArrayList<EventAttendy> eventAttendyArrayList;
    private Context context;
    private String eventId;

    private RoomListener roomListener;

    public TheRoomAdapter(ArrayList<EventAttendy> eventAttendyArrayList, Context context, String eventId,RoomListener roomListener) {
        this.eventAttendyArrayList = eventAttendyArrayList;
        this.context = context;
        this.eventId = eventId;
        this.roomListener = roomListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comin_user_profile_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        EventAttendy eventAttendy = eventAttendyArrayList.get(i);

        switch (eventAttendy.user_status) {
            case "1":
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.go_green_color));
                holder.et_comeInName.setTextColor(context.getResources().getColor(R.color.go_green_color));

                break;
            case "2":
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.caution_yellow_color));
                holder.et_comeInName.setTextColor(context.getResources().getColor(R.color.caution_yellow_color));
                break;

            case "3":
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
                holder.et_comeInName.setTextColor(context.getResources().getColor(R.color.stop_red_color));
                break;

            default:
                holder.comeInUserProfile.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
                holder.et_comeInName.setTextColor(context.getResources().getColor(R.color.stop_red_color));
                break;
        }

        holder.et_comeInName.setText(eventAttendyArrayList.get(i).username);
        Log.v("image",  eventAttendyArrayList.get(i).getUserimage());

        if(!eventAttendyArrayList.get(i).getUserimage().contains("dev-")){
            String image = "dev-"+eventAttendyArrayList.get(i).getUserimage();

            Picasso.with(context).load(image).placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img).into(holder.comeInUserProfile);
        }else{

            Picasso.with(context).load(eventAttendyArrayList.get(i).getUserimage()).placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img).into(holder.comeInUserProfile);
        }

    }

    @Override
    public int getItemCount() {

        return  eventAttendyArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
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
                    EventAttendy eventAttendy = eventAttendyArrayList.get(getAdapterPosition());
                    Log.e("eventAttendy", ""+eventAttendy);

                    if (getAdapterPosition() != -1) {
                        roomListener.getRoomData(getAdapterPosition(),eventAttendyArrayList,eventId);
                    }
                    break;
            }
        }
    }
}
