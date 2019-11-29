package com.scenekey.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.fragment.Search_Fragment;
import com.scenekey.model.Tags;

import java.util.ArrayList;

/**
 * Created by mindiii on 15/2/18.
 */


public class Tag_Adapter extends RecyclerView.Adapter<Tag_Adapter.ChipTags>  {
    private Context mContext;
    private ArrayList<Tags> list;
    private int count;
    private Fragment fragment;

    public Tag_Adapter(Context c , ArrayList<Tags> list ) {
        mContext = c;
        this.list = list;
    }

    public Tag_Adapter(Context c , ArrayList<Tags> list , Fragment fragment) {
        mContext = c;
        this.list = list;
        this.fragment = fragment;
        this.count = 0;
    }

    @Override
    public ChipTags onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChipTags(LayoutInflater.from(mContext).inflate(R.layout.adapter_custom_tag_search,parent,false));
    }

    @Override
    public void onBindViewHolder(ChipTags holder, int position) {

        Tags obj = list.get(position);
        holder.tv_tag.setText(obj.tag);
        holder.tv_tag2.setText(obj.tag);
        holder.tv_tag2.setVisibility(obj.selected?View.VISIBLE:View.INVISIBLE);
        holder.tv_tag.setVisibility(!obj.selected?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ChipTags extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_tag ,tv_tag2 ;


        ChipTags(View itemView) {
            super(itemView);
            tv_tag =  itemView.findViewById(R.id.tv_tag);
            tv_tag2 =  itemView.findViewById(R.id.tv_tag2);
            if(fragment!=null && fragment instanceof Search_Fragment)
            {   tv_tag2.setOnClickListener(this);
                tv_tag.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();

            switch (v.getId()){
                case R.id.tv_tag2:
                    list.get(i).selected=!list.get(i).selected;
                    count = count-1;
                    notifyDataSetChanged();
                    break;
                case R.id.tv_tag:
                    if(count>=9){
                        Toast.makeText(mContext,"You can't select more then 9 tags" , Toast.LENGTH_LONG).show();
                        return;
                    }
                    list.get(i).selected=!list.get(i).selected;
                    count = count+1;
                    notifyDataSetChanged();
                    break;
            }
            updateText();
        }
    }

    private void updateText(){
        if(fragment!=null && fragment instanceof Search_Fragment){
            ((Search_Fragment)fragment).txt_search.setBackgroundResource(count>0?R.drawable.bg_search_tag_active:R.drawable.bg_search_tag_inactive);
        }
    }
}