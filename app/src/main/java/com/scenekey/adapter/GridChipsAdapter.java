package com.scenekey.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.model.Tags;

import java.util.ArrayList;

public class GridChipsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Tags> list;

    public GridChipsAdapter(Context c , ArrayList<Tags> list) {
        mContext = c;
        this.list = list;
    }

    public int getCount() {

        return (list.size()>6?6:list.size());
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            itemView = inflater.inflate(R.layout.custom_chip_view, parent, false);
            ChipTags chip = new ChipTags();

            chip.text =  itemView.findViewById(R.id.label);
            chip.text.setText(list.get(position).tag);
            itemView.setTag(chip);

            // if it's not recycled, initialize some attributes

        } else {
            ChipTags chip = (ChipTags) itemView.getTag();
            chip.text.setText(list.get(position).tag);
        }
        return itemView;
    }

    class ChipTags{
       public TextView text ;
    }
}