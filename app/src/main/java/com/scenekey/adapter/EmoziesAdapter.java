package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.model.EmoziesModal;
import com.scenekey.model.FeedSmily;
import com.scenekey.model.Feeds;

import java.util.ArrayList;
import java.util.List;

public class EmoziesAdapter extends RecyclerView.Adapter<EmoziesAdapter.ViewHolder> {

    private Context context;
    private List<EmoziesModal> emoziesList;
    private List<EmoziesModal> emozieListFilter;
    private ArrayList<FeedSmily> feedlikeList;
    private ArrayList<FilterModal> filterModalArrayList = new ArrayList<>();
    private String filterType;

    public EmoziesAdapter(Context context, List<EmoziesModal> emoziesList, ArrayList<FeedSmily> feedlikeList) {
        this.context = context;
        this.emoziesList = emoziesList;
        this.emozieListFilter = emoziesList;
        this.feedlikeList = feedlikeList;

        feedlikeList.add(0, null);
        getfilterImage();
    }

    private void getfilterImage() {


        for (int i = 0; i <= 13; i++) {

//             ["face", "flag", "animal", "people", "food", "plant", "instrument", "cloth", "sport", "lens", "weather", "building", "ball"]

            FilterModal filterModal = new FilterModal();
            if (i == 0) {
                filterModal.filterType = "face";
                filterModal.imageint = R.drawable.timeline;

            }

            if (i == 1) {
                filterModal.filterType = "face";
                filterModal.imageint = R.drawable.smily;
            }

            if (i == 2) {
                filterModal.filterType = "plant";
                filterModal.imageint = R.drawable.leaf;
            }

            if (i == 3) {
                filterModal.filterType = "food";
                filterModal.imageint = R.drawable.food;
            }

            if (i == 4) {
                filterModal.filterType = "ball";
                filterModal.imageint = R.drawable.footbal;
            }
            if (i == 5) {
                filterModal.filterType = "other";
                filterModal.imageint = R.drawable.inactive_happiness_ico;
            }
            if (i == 6) {
                filterModal.filterType = "smily";
                filterModal.imageint = R.drawable.light;
            }
            if (i == 7) {
                filterModal.filterType = "smily";
                filterModal.imageint = R.drawable.inactive_happiness_ico;
            }
            if (i == 8) {
                filterModal.filterType = "flag";
                filterModal.imageint = R.drawable.flag;
            }
            if (i == 9) {
                filterModal.filterType = "weather";
                filterModal.imageint = R.drawable.cloude;
            }
            if (i == 10) {
                filterModal.filterType = "instrument";
                filterModal.imageint = R.drawable.musics;
            }
            if (i == 11) {
                filterModal.filterType = "cloth";
                filterModal.imageint = R.drawable.cloth;
            }
            if (i == 12) {
                filterModal.filterType = "building";
                filterModal.imageint = R.drawable.skyline;
            }

            filterModalArrayList.add(filterModal);
        }
    }

    @Override
    public EmoziesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.emozis_layout, parent, false);
        return new EmoziesAdapter.ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(EmoziesAdapter.ViewHolder holder, int position) {
        FeedSmily feedSmily = feedlikeList.get(position);

        if (position == 0) {

            holder.iv_emozie.setVisibility(View.VISIBLE);
            holder.iv_plus.setVisibility(View.VISIBLE);

            holder.tv_emozie.setVisibility(View.GONE);
            holder.tv_count.setVisibility(View.GONE);

        } else {
            holder.iv_emozie.setVisibility(View.GONE);
            holder.iv_plus.setVisibility(View.GONE);

            holder.tv_emozie.setVisibility(View.VISIBLE);
            holder.tv_count.setVisibility(View.VISIBLE);

            holder.tv_emozie.setText(feedSmily.reaction);
            holder.tv_count.setText(feedSmily.reaction_count);
        }
    }

    @Override
    public int getItemCount() {
        return feedlikeList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,Filterable{
        private LinearLayout clickOnSmily;
        private ImageView iv_plus;
        private ImageView iv_emozie;
        private TextView tv_emozie,tv_count;


        ViewHolder(View view) {
            super(view);

            clickOnSmily = view.findViewById(R.id.clickOnSmily);
            iv_emozie = view.findViewById(R.id.iv_emozie);
            tv_emozie = view.findViewById(R.id.tv_emozie);
            iv_plus = view.findViewById(R.id.iv_plus);
            tv_count = view.findViewById(R.id.tv_count);
            clickOnSmily.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            FeedSmily feedSmily = feedlikeList.get(getAdapterPosition());
            switch (view.getId()) {

                case R.id.clickOnSmily:
                    if (getAdapterPosition() == 0) {
                        showSetFriendEmailDialog();
                    } else {
//                        feedlikeList.remove(getAdapterPosition());
                        Toast.makeText(context, "Selected Imaozies", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }


        protected void showSetFriendEmailDialog() {
            // Create the dialog.
            final Dialog emoziesDilog =
                    new Dialog(context, android.R.style.Theme_DeviceDefault);
            emoziesDilog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            emoziesDilog.setCancelable(true);
            emoziesDilog.setContentView(R.layout.dialog_for_emozies);
            RecyclerView gridView = emoziesDilog.findViewById(R.id.gridview);
            TextView tv_cancle = emoziesDilog.findViewById(R.id.tv_cancle);
            RecyclerView filterSmilyReclerView = emoziesDilog.findViewById(R.id.filterSmilyReclerView);

            FilterAdapter filterAdapter = new FilterAdapter(context, filterModalArrayList, new FilterAdapter.FilterListener() {
                @Override
                public void getFilterName(FilterModal filterModal) {

                    filterType = filterModal.filterType;

                    getFilter().filter(filterType);
                }
            });
            filterSmilyReclerView.setAdapter(filterAdapter);
            filterAdapter.notifyDataSetChanged();

            AllEmoziesAdapter adapter = new AllEmoziesAdapter(context, emoziesList);
            gridView.setLayoutManager(new GridLayoutManager(context, 8));
            gridView.setAdapter(adapter);


            tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    emoziesDilog.dismiss();
                }
            });

            emoziesDilog.show();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        emozieListFilter = emoziesList;
                    } else {
                        List<EmoziesModal> filteredList = new ArrayList<>();
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                            /*if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }*/

                        for(int i=0; i<emoziesList.size();i++){

                            for (String group : emoziesList.get(i).getGroups()){
                                if(group.equals(charString)){

                                    EmoziesModal emoziesModal = new EmoziesModal();

                                    filteredList.add(emoziesModal);
                                }
                            }
                        }

                          /*  if (row.getGroups().get()) {
                                filteredList.add(row);
                            }*/

                        emozieListFilter = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = emozieListFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    emozieListFilter = (ArrayList<EmoziesModal>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }


}

