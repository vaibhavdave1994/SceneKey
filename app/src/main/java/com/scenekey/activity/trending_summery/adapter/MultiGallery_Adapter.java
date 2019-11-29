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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.scenekey.R;
import com.scenekey.activity.trending_summery.GalleryMultiViewActivity;
import com.scenekey.activity.trending_summery.GallerySlideActivity;
import com.scenekey.activity.trending_summery.Model.SummeryModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ravi Birla on 09,September,2019
 */
public class MultiGallery_Adapter extends RecyclerView.Adapter<MultiGallery_Adapter.MyViewHolder> {

    private Context context;
    private List<SummeryModel.EventBean.FeedPostBean> feedPostlist;


    public MultiGallery_Adapter(Context context, List<SummeryModel.EventBean.FeedPostBean> feedPostlist) {
        this.context = context;
        this.feedPostlist=feedPostlist;
    }

    @NonNull
    @Override
    public MultiGallery_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.galleryviewlayout, parent, false);
        return new MultiGallery_Adapter.MyViewHolder(v);

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final MultiGallery_Adapter.MyViewHolder holder, final int position) {


        SummeryModel.EventBean.FeedPostBean summeryModel = feedPostlist.get(position);
        Glide.with(context).load(summeryModel.getFeed_image())
                .thumbnail(0.5f)
                .crossFade().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.sk_logo_image)
                .error(R.drawable.sk_logo_image)
                .into(holder.iv_proimg);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return feedPostlist.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder  {

        private ImageView iv_proimg;
        MyViewHolder(final View itemView) {
            super(itemView);
            iv_proimg = itemView.findViewById(R.id.iv_proimg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, GallerySlideActivity.class);
                    intent.putExtra("feedPostlist", (Serializable) feedPostlist);
                    intent.putExtra("feedListPostion", getAdapterPosition());
                    context.startActivity(intent);
                }
            });

            }


    }
}
