package com.scenekey.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.activity.SearchSubCategoryActivity;
import com.scenekey.activity.TagsActivity;
import com.scenekey.model.SearchTagModal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Search_tag_Adapter  extends RecyclerView.Adapter<Search_tag_Adapter.ViewHolder> {
    private ArrayList<SearchTagModal> search_tagList;
    private Context context;
    private HomeActivity activity;

    public Search_tag_Adapter(Context context, ArrayList<SearchTagModal> search_tagList, HomeActivity activity) {
        this.search_tagList = search_tagList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Search_tag_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchtaglist_view, parent, false);
        return new Search_tag_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Search_tag_Adapter.ViewHolder holder, final int position) {
        SearchTagModal  searchTagModal = search_tagList.get(position);

//        if(position == search_tagList.size() -1){
//            holder.view.setVisibility(View.VISIBLE);
//        }else{
//            holder.view.setVisibility(View.GONE);
//        }


        holder.txt_catgoryName.setText(searchTagModal.category_name);
        Picasso.with(context).load(searchTagModal.back_image)
                .error(R.drawable.sk_logo_black)
                .into(holder.iv_news);
    }

    @Override
    public int getItemCount() {
        return search_tagList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

         private  ImageView iv_news;
         private TextView txt_catgoryName;
         private View view;

        ViewHolder(View itemView) {
            super(itemView);
            iv_news = itemView.findViewById(R.id.iv_news);
            view = itemView.findViewById(R.id.view);
            txt_catgoryName = itemView.findViewById(R.id.txt_catgoryName);
            iv_news.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_news:
                    SearchTagModal  searchTagModal = search_tagList.get(getAdapterPosition());

                    Intent intent = new Intent(context, TagsActivity.class);
//                    Intent intent = new Intent(context, SearchSubCategoryActivity.class);
                    intent.putExtra("cat_id", searchTagModal.cat_id);
                    intent.putExtra("category_name", searchTagModal.category_name);
                    intent.putExtra("category_image", searchTagModal.category_image);
                    intent.putExtra("color_code", searchTagModal.color_code);
                    intent.putExtra("from_category", true);
                    context.startActivity(intent);

                    break;
            }
        }
    }
}



