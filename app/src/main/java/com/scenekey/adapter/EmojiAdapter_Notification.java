package com.scenekey.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.cus_view.ProfilePopUp_Notification;
import com.vanniktech.emoji.EmojiTextView;

/**
 * Created by mindiii on 11/2/17.
 */

public class EmojiAdapter_Notification extends RecyclerView.Adapter<EmojiAdapter_Notification.Holder> {

    private Context context;
    private String list [];
    private ProfilePopUp_Notification profilePopUp;
   private boolean addToRecent;

    public EmojiAdapter_Notification(Context context , ProfilePopUp_Notification profilePopUp ) {
        this.context = context;
        this.list = context.getResources().getStringArray(R.array.emojiArray);
        this.profilePopUp = profilePopUp;
        addToRecent =  true;
    }

    @Override
    public EmojiAdapter_Notification.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.custom_emoji,null));
    }


    @Override
    public void onBindViewHolder(EmojiAdapter_Notification.Holder holder, int position) {
        holder.tv_emoji.setText(list [position]);
        holder.tv_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePopUp.onClickView((TextView) v ,profilePopUp);
                if(addToRecent && profilePopUp.list.size()<=4){
                    profilePopUp.setRecent(profilePopUp.list.size()==4? profilePopUp.list.get(3) :((TextView) v).getText().toString());
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    class Holder extends RecyclerView.ViewHolder{
        EmojiTextView tv_emoji;
        Holder(View itemView) {
            super(itemView);
            tv_emoji = (EmojiTextView) itemView.findViewById(R.id.tv_emoji);

        }
    }

    public int getIndicatorCount(double recyclerWidth,int tv_width){
       // return (int) ((tv_width*list.length)/(recyclerWidth*4));
        return  (list.length/28);

    }

   public void setList(int i){
       addToRecent =  true;
        switch (i){
            case 0: this.list = context.getResources().getStringArray(R.array.emojiArray);  break;
            case 1: this.list = context.getResources().getStringArray(R.array.nature);      break;
            case 2: this.list = context.getResources().getStringArray(R.array.things);      break;
            case 3: this.list = context.getResources().getStringArray(R.array.buildings);   break;
            case 4: this.list = context.getResources().getStringArray(R.array.symbool);     break;
            default:this.list = profilePopUp.getRecent(); addToRecent = false ;             break;
        }
        notifyDataSetChanged();
        profilePopUp.updateIndicator();

    }

}
