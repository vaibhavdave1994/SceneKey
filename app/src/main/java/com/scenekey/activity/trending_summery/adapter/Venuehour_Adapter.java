package com.scenekey.activity.trending_summery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.scenekey.R;
import com.scenekey.activity.trending_summery.Model.SummeryModel;
import com.scenekey.activity.trending_summery.Model.VenueHourModel;

import java.util.List;

/**
 * Created by Ravi Birla on 09,September,2019
 */
public class Venuehour_Adapter extends RecyclerView.Adapter<Venuehour_Adapter.MyViewHolder> {

    private Context context;
    private List<VenueHourModel> venueHourModelList;


    public Venuehour_Adapter(Context context, List<VenueHourModel> venueHourModelList) {
        this.context = context;
        this.venueHourModelList=venueHourModelList;
    }

    @NonNull
    @Override
    public Venuehour_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.adapter_venuehour_layout, parent, false);
        return new Venuehour_Adapter.MyViewHolder(v);

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final Venuehour_Adapter.MyViewHolder holder, final int position) {
        VenueHourModel venueHourModel = venueHourModelList.get(position);




        holder.day.setText(venueHourModel.getDay());


        if (venueHourModel.getValue().contains("-")) {
            String[] separated = venueHourModel.getValue().split("-");
            String open = separated[0];
            String close = separated[1];
            if (open.length() == 2 ) {
                char open1 = open.charAt(0);
                char openap = open.charAt(1);
                holder.open.setText(String.format("0%s:00"+openap, open1));
            }
            else if (open.length() == 3){
                String open2 = open.substring(0, 2);
                char openap = open.charAt(2);
                holder.open.setText(String.format("%s:00"+openap, open2));

            }
            else if (open.contains(":")) {
                String[] colen = open.split(":");
                String value1 = colen[0];
                String value2 = colen[1];

                if (value1.length() == 1) {
                    holder.open.setText(String.format("0%s", open));
                } else {
                    holder.open.setText(open);

                }

            } else {
                holder.open.setText(open);
            }

            if (close.length() == 2 ) {
                char close1 = close.charAt(0);
                char closeap = close.charAt(1);
                holder.close.setText(String.format("0%s:00"+closeap, close1));
            }
            else if (close.length() == 3){
                String close2 = close.substring(0, 2);
                char closeap = close.charAt(2);
                holder.close.setText(String.format("%s:00"+closeap, close2));

            }
            else if (close.contains(":")) {
                String[] colen = close.split(":");
                String value1 = colen[0];
                String value2 = colen[1];

                if (value1.length() == 1) {
                    holder.close.setText(String.format("0%s", close));
                } else {
                    holder.close.setText(close);

                }

            } else {
                holder.close.setText(close);
            }
        }
        else {
            holder.open.setText(venueHourModel.getValue());
            holder.close.setText(venueHourModel.getValue());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return venueHourModelList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView day,open,close;
        MyViewHolder(final View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            open = itemView.findViewById(R.id.open);
            close = itemView.findViewById(R.id.close);
            }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }
}
