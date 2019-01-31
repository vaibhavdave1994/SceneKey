package com.scenekey.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scenekey.R;

import java.util.ArrayList;

public class DemoTagAdapter extends RecyclerView.Adapter<DemoTagAdapter.ViewHolder> {

    private ArrayList<String> tagList;
    private Context context;

    public DemoTagAdapter(ArrayList<String> tagList, Context context) {
        this.tagList = tagList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chip_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        String tag = tagList.get(i);
        holder.tv_tag2.setText(tag);
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_tag2;

        ViewHolder(View view) {
            super(view);
            tv_tag2 = view.findViewById(R.id.label);
        }
    }
}
