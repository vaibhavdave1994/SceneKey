package com.scenekey.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.scenekey.R;
import com.scenekey.model.VenueBoard;

import java.util.ArrayList;
import java.util.List;

public class VenueBoardAdapter extends RecyclerView.Adapter<VenueBoardAdapter.ViewHolder> {

    private ArrayList<VenueBoard.EventTagBean> venue_boardList;
    private Context context;
    boolean fromTrending;
    private VenueBoardInnerAdapter venueBoardInnerAdapter;

    public VenueBoardAdapter(Context context, ArrayList<VenueBoard.EventTagBean> venue_boardList,boolean fromTrending) {

        this.venue_boardList = venue_boardList;
        this.context = context;
        this.fromTrending = fromTrending;

    }

    @NonNull
    @Override
    public VenueBoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_on_board, parent, false);
        return new VenueBoardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VenueBoardAdapter.ViewHolder holder, final int position) {
       // VenueBoard.EventTagBean.TagListBean  socialMediaBean = venue_boardList.get(position);

        //------------------venueboard special tags ---------------------
        List<VenueBoard.EventTagBean.TagListBean> venue_boardList_inner = new ArrayList<>();
        List<VenueBoard.EventTagBean.TagListBean> venue_boardList_inner_vertical = new ArrayList<>();
        if(venue_boardList.get(position).getCategory_name().equalsIgnoreCase("Specials") || venue_boardList.get(position).getCategory_name().equalsIgnoreCase("Happy Hour")){
            holder.tag__vanue_name.setText(venue_boardList.get(position).getCategory_name());
            Glide.with(context).load(venue_boardList.get(position).getCategory_image()).centerCrop().placeholder(R.drawable.app_icon)
                    .into(holder.iv_tag__special_image);

            String colorCode =venue_boardList.get(position).getColor_code().substring(1);
            //  holder.iv_tag__special_image.setBackgroundDrawable(new Border(Color.parseColor(venue_boardList.get(position).getColor_code()),5));
            GradientDrawable border = new GradientDrawable();
            //border.setColor(0xFFFFFFFF); //white background
            border.setStroke(2, Color.parseColor(venue_boardList.get(position).getColor_code())); //black border with full opacity
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                holder.iv_tag__special_image.setBackgroundDrawable(border);
            } else {
                holder.iv_tag__special_image.setBackground(border);
            }
            venue_boardList_inner_vertical = venue_boardList.get(position).getTagList();
            if (venue_boardList_inner_vertical == null) {
                venue_boardList_inner_vertical = new ArrayList<>();
            }

            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams)  holder.venuRecyclerView.getLayoutParams();
            marginLayoutParams.setMargins(0, 0, 0, 0);
            holder.venuRecyclerView.setLayoutParams(marginLayoutParams);

            holder.venuRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            venueBoardInnerAdapter = new VenueBoardInnerAdapter(context, venue_boardList_inner_vertical, fromTrending,false);
            holder.venuRecyclerView.setAdapter(venueBoardInnerAdapter);
        }
        else {
            holder.tag__vanue_name.setText(venue_boardList.get(position).getCategory_name());
            Glide.with(context).load(venue_boardList.get(position).getCategory_image()).centerCrop().placeholder(R.drawable.app_icon)
                    .into(holder.iv_tag__special_image);

            String colorCode =venue_boardList.get(position).getColor_code().substring(1);
            //  holder.iv_tag__special_image.setBackgroundDrawable(new Border(Color.parseColor(venue_boardList.get(position).getColor_code()),5));
            GradientDrawable border = new GradientDrawable();
            //border.setColor(0xFFFFFFFF); //white background
            border.setStroke(2, Color.parseColor(venue_boardList.get(position).getColor_code())); //black border with full opacity
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                holder.iv_tag__special_image.setBackgroundDrawable(border);
            } else {
                holder.iv_tag__special_image.setBackground(border);
            }
            venue_boardList_inner = venue_boardList.get(position).getTagList();
            if (venue_boardList_inner == null) {
                venue_boardList_inner = new ArrayList<>();
            }

            venueBoardInnerAdapter = new VenueBoardInnerAdapter(context, venue_boardList_inner, fromTrending,true);
            holder.venuRecyclerView.setAdapter(venueBoardInnerAdapter);

        }

        venueBoardInnerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return venue_boardList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_tag__special_image;
        private TextView tag__vanue_name;
        RecyclerView venuRecyclerView;
        RelativeLayout toolbar;
        ViewHolder(View itemView) {
            super(itemView);

            iv_tag__special_image = itemView.findViewById(R.id.iv_tag__special_image);
            tag__vanue_name = itemView.findViewById(R.id.tag__vanue_name);
            venuRecyclerView = itemView.findViewById(R.id.venuRecyclerView);
            venuRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            toolbar = itemView.findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}