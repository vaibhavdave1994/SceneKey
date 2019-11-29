package com.scenekey.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.activity.TrendinSearchActivity;
import com.scenekey.model.TagModal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubCatergoryApdater  extends RecyclerView.Adapter<SubCatergoryApdater.ViewHolder> {
    private ArrayList<TagModal> tagList;
    private Context context;

    public SubCatergoryApdater(Context context, ArrayList<TagModal> tagList) {
        this.tagList = tagList;
        this.context = context;
    }

    @NonNull
    @Override
    public SubCatergoryApdater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tags_subcategory_view, parent, false);
        return new SubCatergoryApdater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCatergoryApdater.ViewHolder holder, final int position) {
        TagModal tagModal = tagList.get(position);

        holder.tag_name.setText(tagModal.tag_text);

        holder.iv_tag_circulerImage.setBorderColor(Color.parseColor(tagModal.color_code));
        Picasso.with(context).load(tagModal.tag_image).placeholder(R.drawable.app_icon)
                .error(R.drawable.app_icon).into(holder.iv_imageView);
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView iv_tag_circulerImage;
        private ImageView iv_imageView;
        private TextView tag_name;
        private RelativeLayout mainRoomView;

        ViewHolder(View itemView) {
            super(itemView);
            iv_tag_circulerImage = itemView.findViewById(R.id.iv_tag_circulerImage);
            iv_imageView = itemView.findViewById(R.id.iv_imageView);
            tag_name = itemView.findViewById(R.id.tag_name);
            mainRoomView = itemView.findViewById(R.id.mainRoomView);
            mainRoomView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.mainRoomView:

                    TagModal tagModal = tagList.get(getAdapterPosition());
                    switch (view.getId()) {
                        case R.id.mainRoomView:
                            Intent intent = new Intent(context, TrendinSearchActivity.class);
                            intent.putExtra("tag_name", tagModal.tag_name);
                            intent.putExtra("tag_text", tagModal.tag_text);
                            intent.putExtra("tag_image", tagModal.tag_image);
                            intent.putExtra("fromSpecial", true);
                            context.startActivity(intent);
                            break;
                    }
            }
        }
    }
}



