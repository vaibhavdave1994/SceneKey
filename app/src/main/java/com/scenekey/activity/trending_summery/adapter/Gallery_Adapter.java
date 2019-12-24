package com.scenekey.activity.trending_summery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.scenekey.activity.trending_summery.GalleryMultiViewActivity;
import com.scenekey.activity.trending_summery.Model.SummeryModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ravi Birla on 09,September,2019
 */
public class Gallery_Adapter extends RecyclerView.Adapter<Gallery_Adapter.MyViewHolder> {

    private Context context;
    private List<SummeryModel.EventBean.FeedPostBean> feedPostlist;

    public Gallery_Adapter(Context context, List<SummeryModel.EventBean.FeedPostBean> feedPostlist) {
        this.context = context;
        this.feedPostlist = feedPostlist;
    }

    @NonNull
    @Override
    public Gallery_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.gallery_layout, parent, false);
        return new Gallery_Adapter.MyViewHolder(v);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final Gallery_Adapter.MyViewHolder holder, final int position) {

        int count = feedPostlist.size();
        int restCount = count - 3;
        SummeryModel.EventBean.FeedPostBean summeryModel = feedPostlist.get(position);
        if (position == 3 && restCount > 0) {
            holder.card_count.setVisibility(View.VISIBLE);
            holder.count.setText(String.format("+%d", restCount));
        } else if (position < 3) {
            holder.card_count.setVisibility(View.GONE);
            holder.iv_proimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(summeryModel.getFeed_image())
                    .into(holder.iv_proimg);
        } else {
            holder.card_count.setVisibility(View.GONE);
            holder.iv_proimg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return feedPostlist.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView iv_proimg;
        private TextView count;
        private CardView card_count;

        MyViewHolder(final View itemView) {
            super(itemView);
            iv_proimg = itemView.findViewById(R.id.iv_proimg);
            card_count = itemView.findViewById(R.id.card_count);
            count = itemView.findViewById(R.id.count);

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, GalleryMultiViewActivity.class);
                intent.putExtra("feedPostlist", (Serializable) feedPostlist);
                context.startActivity(intent);
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    }
}
