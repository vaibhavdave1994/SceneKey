package com.scenekey.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.listener.KeyinUserListener;
import com.scenekey.model.KeyInUserModal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TheKeyInUserAdapter extends RecyclerView.Adapter<TheKeyInUserAdapter.ViewHolder> {

    private ArrayList<KeyInUserModal> keyInUserModalsList;
    private Context context;
    private KeyinUserListener keyinUserListener;


    public TheKeyInUserAdapter(ArrayList<KeyInUserModal> keyInUserModalsList, Context context,KeyinUserListener keyinUserListener) {
        this.keyInUserModalsList = keyInUserModalsList;
        this.context = context;
        this.keyinUserListener = keyinUserListener;
    }

    @Override
    public TheKeyInUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comin_user_profile_view, parent, false);
        return new TheKeyInUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TheKeyInUserAdapter.ViewHolder holder, int i) {
        KeyInUserModal keyInUserModal = keyInUserModalsList.get(i);

       /* switch (keyInUserModal.user_status) {
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
        }*/

        holder.et_comeInName.setText(keyInUserModal.userName);
        Log.v("image",  keyInUserModal.getUserimage());

        if(!keyInUserModalsList.get(i).getUserimage().contains("dev-")){
            String image = "dev-"+keyInUserModal.getUserimage();

            Picasso.with(context).load(image).placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img).into(holder.comeInUserProfile);
        }else{

            Picasso.with(context).load(keyInUserModalsList.get(i).getUserimage()).placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img).into(holder.comeInUserProfile);
        }

    }

    @Override
    public int getItemCount() {

        return  keyInUserModalsList.size();
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
                    KeyInUserModal keyInUserModal = keyInUserModalsList.get(getAdapterPosition());
                    Log.e("keyInUserModal", ""+keyInUserModal);

                    if (getAdapterPosition() != -1) {
                        keyinUserListener.getRoomData(getAdapterPosition(),keyInUserModalsList);
                    }
                    break;
            }
        }
    }
}

