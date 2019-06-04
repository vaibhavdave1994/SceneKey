package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scenekey.R;
import com.scenekey.helper.Constant;
import com.scenekey.listener.ForDeleteFeed;
import com.scenekey.listener.LikeFeedListener;
import com.scenekey.model.EmoziesModal;
import com.scenekey.model.FeedSmily;
import com.scenekey.model.Feeds;
import com.scenekey.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmoziesAdapter extends RecyclerView.Adapter<EmoziesAdapter.ViewHolder> {

    public Dialog emoziesDilog;
    private Context context;
    private List<EmoziesModal> emoziesList;
    private List<EmoziesModal> emozieListFilter;
    private ArrayList<FeedSmily> feedlikeList;
    private ArrayList<FilterModal> filterModalArrayList = new ArrayList<>();
    private String filterType;
    private ForDeleteFeed deleteFeed;
    private AllEmoziesAdapter adapter;
    private LikeFeedListener likeFeedListener;
    private RecyclerView gridView;
    private String feedId;
    private Feeds feeds;
    private String userExistOrNot;
    private Utility utility;
    private String fromsearch = "fromfilter";


    public EmoziesAdapter(Context context, String userExistOrNot, Feeds feeds, String feedId, List<EmoziesModal> emoziesList,
                          ArrayList<FeedSmily> feedlikeList, ForDeleteFeed deleteFeed, LikeFeedListener likeFeedListener) {
        this.context = context;
        this.emoziesList = emoziesList;
        this.emozieListFilter = emoziesList;
        this.feedlikeList = feedlikeList;
        this.deleteFeed = deleteFeed;
        this.likeFeedListener = likeFeedListener;
        this.feedId = feedId;
        this.feeds = feeds;
        this.userExistOrNot = userExistOrNot;
        feedlikeList.add(0, new FeedSmily());
        getfilterImage();

        utility = new Utility(context);

    }

    private void getfilterImage() {
        for (int i = 0; i <= 13; i++) {

            FilterModal filterModal = new FilterModal();
            if (i == 0) {
                filterModal.filterType = "recent";
                filterModal.imageint = R.drawable.timeline;
            }

            if (i == 1) {
                filterModal.filterType = "face";
                filterModal.imageint = R.drawable.smily;
            }

            if (i == 2) {
                filterModal.filterType = "other";
                filterModal.imageint = R.drawable.inactive_search_ico;
            }

            if (i == 3) {
                filterModal.filterType = "flag";
                filterModal.imageint = R.drawable.flag;
            }

            if (i == 4) {
                filterModal.filterType = "animal";
                filterModal.imageint = R.drawable.animal;
            }

            if (i == 5) {
                filterModal.filterType = "people";
                filterModal.imageint = R.drawable.inactive_user_ico;
            }

            if (i == 6) {
                filterModal.filterType = "food";
                filterModal.imageint = R.drawable.food;
            }

            if (i == 7) {
                filterModal.filterType = "sport";
                filterModal.imageint = R.drawable.footbal;
            }

            if (i == 8) {
                filterModal.filterType = "plant";
                filterModal.imageint = R.drawable.leaf;
            }

            if (i == 9) {
                filterModal.filterType = "instrument";
                filterModal.imageint = R.drawable.musics;
            }

            if (i == 10) {
                filterModal.filterType = "cloth";
                filterModal.imageint = R.drawable.cloth;
            }

            if (i == 11) {
                filterModal.filterType = "weather";
                filterModal.imageint = R.drawable.cloude;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_emozis_layout, parent, false);
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

        if (feedSmily.isReaction.equals("1")) {
            holder.clickOnSmily.setBackgroundResource(R.drawable.light_tranparent);
        } else {
            holder.clickOnSmily.setBackgroundResource(R.drawable.transparent_border);
        }

    }

    @Override
    public int getItemCount() {
        return feedlikeList.size();
    }

    public void cantJoinNotExixtUserDialog(String userStaus) {

        if (userStaus.equals("notStart")) {
            utility.showCustomPopup(context.getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else if (userStaus.equals("notStart")) {
            utility.showCustomPopup(context.getString(R.string.enotStarted), String.valueOf(R.font.montserrat_medium));
        } else if (userStaus.equals("notArrived")) {
            utility.showCustomPopup(context.getString(R.string.enotat), String.valueOf(R.font.montserrat_medium));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Filterable {
//        private LinearLayout clickOnSmily;
        private RelativeLayout clickOnSmily;
        private ImageView iv_plus;
        private ImageView iv_emozie;
        private TextView tv_emozie, tv_count;

        ViewHolder(View view) {
            super(view);

            clickOnSmily = view.findViewById(R.id.clickOnSmily);
            iv_emozie = view.findViewById(R.id.iv_emozie);
            tv_emozie = view.findViewById(R.id.tv_emozie);
            iv_plus = view.findViewById(R.id.iv_plus);
            tv_count = view.findViewById(R.id.tv_count);
            clickOnSmily.setOnClickListener(this);

            clickOnSmily.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    FeedSmily feedSmily = feedlikeList.get(getAdapterPosition());

                    if (!userExistOrNot.equals("")) {
                        cantJoinNotExixtUserDialog(userExistOrNot);
                    } else {
                        deleteFeed.getFeedIdForDelete("", feedSmily.id, feedSmily.reaction);
                    }

                    return true;
                }
            });
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.clickOnSmily:
                    FeedSmily feedSmily = feedlikeList.get(getAdapterPosition());

                    if (getAdapterPosition() == 0) {
                        if (!userExistOrNot.equals("")) {
                            cantJoinNotExixtUserDialog(userExistOrNot);
                        } else {
                            showSetFriendEmailDialog(feeds);
                        }
                    } else {
                        if (feedSmily.isReaction.equals("1")) {
                            likeFeedListener.likeFeedByReaction("", feedSmily.id, "");
                        }
                    }

                    break;
            }
        }


        public void showSetFriendEmailDialog(Feeds feeds) {
            emoziesDilog =
                    new Dialog(context, android.R.style.Theme_DeviceDefault);
            emoziesDilog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            emoziesDilog.setCancelable(true);
            emoziesDilog.setContentView(R.layout.dialog_for_emozies);
            emoziesDilog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            gridView = emoziesDilog.findViewById(R.id.gridview);
             RelativeLayout lowerView = emoziesDilog.findViewById(R.id.lowerView);
            TextView tv_cancle = emoziesDilog.findViewById(R.id.tv_cancle);
            final EditText et_search = emoziesDilog.findViewById(R.id.et_search);
            final RecyclerView filterSmilyReclerView = emoziesDilog.findViewById(R.id.filterSmilyReclerView);

            textwatcherMethod(et_search);

            if(fromsearch.equals("fromtextWatcher")){
                lowerView.setVisibility(View.GONE);
            }else lowerView.setVisibility(View.VISIBLE);

            FilterAdapter filterAdapter = new FilterAdapter(context, filterModalArrayList, new FilterAdapter.FilterListener() {
                @Override
                public void getFilterName(FilterModal filterModal) {

                    fromsearch = "fromfilter";
                    filterType = filterModal.filterType;
                    getFilter().filter(filterType);
                }
            });
            filterSmilyReclerView.setAdapter(filterAdapter);
            filterAdapter.notifyDataSetChanged();

            if (emozieListFilter.size() != 0) {
                List<EmoziesModal> emoziesListTemp = new ArrayList<>();
                emoziesListTemp.addAll(emozieListFilter);
                emoziesList.clear();
                emoziesList.addAll(emoziesListTemp);
                emoziesListTemp.clear();
            }

            adapter = new AllEmoziesAdapter(feeds, feedId, emoziesList, likeFeedListener, emoziesDilog);
            gridView.setLayoutManager(new GridLayoutManager(context, 8));
            gridView.setAdapter(adapter);

            tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emoziesDilog.dismiss();
                }
            });

            emoziesDilog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        emoziesDilog.dismiss();
                    }
                    return true;
                }
            });

            emoziesDilog.show();
        }

        /*................................textwatcherMethod().......................................*/
        private void textwatcherMethod(final EditText stext) {
            stext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    String searchText = editable.toString();

                    if (!searchText.isEmpty()) {
                        fromsearch = "fromtextWatcher";
                        getFilter().filter(searchText);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        emozieListFilter.clear();
                        emozieListFilter = emoziesList;
                    } else {

                        List<EmoziesModal> filteredList = new ArrayList<>();
                        if (fromsearch.equals("fromtextWatcher")) {
                            for (int i = 0; i < emoziesList.size(); i++) {
                                if ((emoziesList.get(i).getName().toLowerCase()).contains(charString.toLowerCase())) {
                                    filteredList.add(emoziesList.get(i));
                                }
                            }
                        }

                        if (fromsearch.equals("fromfilter")) {

                            if(charSequence.equals("recent")){
                                filteredList.addAll(Constant.recentEmoziesList);
                            }
                            //--------^------ deepaksharma's code
                            else {
                                for (int i = 0; i < emoziesList.size(); i++) {

                                    for (String group : emoziesList.get(i).getGroups()) {
                                        if (group.equals(charString)) {
                                            filteredList.add(emoziesList.get(i));
                                        }
                                    }
                                }
                            }
                        }

                        emozieListFilter = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = emozieListFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    emozieListFilter = (ArrayList<EmoziesModal>) filterResults.values;
                    adapter = new AllEmoziesAdapter(feeds, feedId, emozieListFilter, likeFeedListener, emoziesDilog);
                    gridView.setLayoutManager(new GridLayoutManager(context, 8));
                    gridView.setAdapter(adapter);
                }
            };
        }
    }
}

