package com.scenekey.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.model.ReactionUserModal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SmilyUserAdapter  extends RecyclerView.Adapter<SmilyUserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ReactionUserModal> emoziesList;

    public SmilyUserAdapter(Context context, ArrayList<ReactionUserModal> emoziesList) {
        this.context = context;
        this.emoziesList = emoziesList;
    }

    @Override
    public SmilyUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.same_user_smyly_view, parent, false);
        return new SmilyUserAdapter.ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(SmilyUserAdapter.ViewHolder holder, int position) {
        ReactionUserModal  reactionUserModal = emoziesList.get(position);
        holder.tv_userName.setText(reactionUserModal.fullname);

        if (position == emoziesList.size() - 1) {
            holder.line_view.setVisibility(View.GONE);
        } else {
            holder.line_view.setVisibility(View.VISIBLE);
        }

        Picasso.with(context).load(reactionUserModal.user_image).placeholder(R.drawable.bg_event_card).into(holder.iv_userImage);
    }

    @Override
    public int getItemCount() {
        return emoziesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView iv_userImage;
        private TextView tv_userName;
        private View line_view;


        ViewHolder(View view) {
            super(view);

            iv_userImage = view.findViewById(R.id.iv_userImage);
            tv_userName = view.findViewById(R.id.tv_userName);
            line_view = view.findViewById(R.id.line_view);
        }
    }
}