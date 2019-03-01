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
import com.scenekey.model.EmoziesModal;

import java.util.ArrayList;

public class FilterAdapter  extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FilterModal> emoziesList;
    private FilterListener filterListener;

    public FilterAdapter(Context context, ArrayList<FilterModal> emoziesList,FilterListener filterListener) {
        this.context = context;
        this.emoziesList = emoziesList;
        this.filterListener = filterListener;
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item_view, parent, false);
        return new FilterAdapter.ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(FilterAdapter.ViewHolder holder, int position) {
        FilterModal filterModal =  emoziesList.get(position);
        holder.iv_filterIcon.setImageResource(filterModal.imageint);

        if (filterModal.isSelected.equals("yes")) {
            holder.ln_view.setVisibility(View.VISIBLE);
            //holder.ln_view.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark_new));
        } else {
            holder.ln_view.setVisibility(View.INVISIBLE);
            //holder.ln_view.setTextColor(context.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return emoziesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_filterIcon;
        private TextView ln_view;

        ViewHolder(View view) {
            super(view);

            iv_filterIcon = view.findViewById(R.id.iv_filterIcon);
            ln_view = view.findViewById(R.id.ln_view);
            iv_filterIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            FilterModal filterModal  = emoziesList.get(getAdapterPosition());

            switch (view.getId()) {

                case R.id.iv_filterIcon:
                    for (int i = 0; i < emoziesList.size(); i++) {
                        emoziesList.get(i).isSelected = "no";
                    }
                    filterModal.isSelected = "yes";
                    filterListener.getFilterName(filterModal);
                    notifyDataSetChanged();
                    break;
            }
        }
    }

    interface FilterListener{
        void getFilterName(FilterModal filterModal);
    }
}