package com.scenekey.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.lib_sources.SwipeCard.Card;
import com.scenekey.model.EmoziesModal;

import java.util.List;

public class AllEmoziesAdapter  extends RecyclerView.Adapter<AllEmoziesAdapter.ViewHolder> {

    private Context context;
    private List<EmoziesModal> emoziesList;

    public AllEmoziesAdapter(Context context, List<EmoziesModal> emoziesList) {
        this.context = context;
        this.emoziesList = emoziesList;
    }

    @Override
    public AllEmoziesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.emozis_data_layout, parent, false);
        return new AllEmoziesAdapter.ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(AllEmoziesAdapter.ViewHolder holder, int position) {
        EmoziesModal emoziesModal =  emoziesList.get(position);
        holder.iv_smiley.setText(emoziesModal.getCharacter());
    }

    @Override
    public int getItemCount() {
        return emoziesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView iv_smiley;


        ViewHolder(View view) {
            super(view);

            iv_smiley = view.findViewById(R.id.iv_smiley);
            iv_smiley.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


            EmoziesModal emoziesModal  = emoziesList.get(getAdapterPosition());
            switch (view.getId()) {

                case R.id.iv_smiley:

                    Toast.makeText(context, emoziesModal.getName(), Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    }
}

