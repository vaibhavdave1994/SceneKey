package com.scenekey.adapter;

import android.app.Dialog;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.helper.Constant;
import com.scenekey.listener.LikeFeedListener;
import com.scenekey.model.EmoziesModal;
import com.scenekey.model.Feeds;

import java.util.List;

public class AllEmoziesAdapter extends RecyclerView.Adapter<AllEmoziesAdapter.ViewHolder> {

    public Feeds feeds;
    private Dialog emoziesDilog;
    private List<EmoziesModal> emoziesList;
    private LikeFeedListener likeFeedListener;
    private String feedId;

    AllEmoziesAdapter(Feeds feeds, String feedId, List<EmoziesModal> emoziesList,
                      LikeFeedListener likeFeedListener, Dialog emoziesDilog) {
        this.emoziesList = emoziesList;
        this.likeFeedListener = likeFeedListener;
        this.feedId = feedId;
        this.emoziesDilog = emoziesDilog;
        this.feeds = feeds;
    }

    @Override
    public AllEmoziesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.emozis_data_layout, parent, false);
        return new AllEmoziesAdapter.ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(AllEmoziesAdapter.ViewHolder holder, int position) {
        EmoziesModal emoziesModal = emoziesList.get(position);
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


            EmoziesModal emoziesModal = emoziesList.get(getAdapterPosition());
            switch (view.getId()) {

                case R.id.iv_smiley:
                    int size = feeds.feedSmilies.size();
                    if (size != 0) {

                        if (size > 1) {
                            for (int j = 1; j < size; j++) {

                                if (feeds.feedSmilies.get(j).reaction.equals((emoziesModal.getCharacter()))) {
                                    if (feeds.feedSmilies.get(j).isReaction.equals("1")) {
                                        likeFeedListener.likeFeedByReaction("", feeds.feedSmilies.get(j).id, "");
                                        break;
                                    } else {
                                        likeFeedListener.likeFeedByReaction(feeds.feedId, "", emoziesModal.getCharacter());
                                        break;
                                    }
                                } else if (j == size - 1) {
                                    likeFeedListener.likeFeedByReaction(feeds.feedId, "", emoziesModal.getCharacter());
                                    if(Constant.recentEmoziesList.size() > 0){
                                        if(!Constant.recentEmoziesList.contains(emoziesModal)){
                                            Constant.recentEmoziesList.add(emoziesModal);
                                        }
                                    }
                                    else {
                                        Constant.recentEmoziesList.add(emoziesModal);
                                    }
                                    System.out.println(Constant.recentEmoziesList.size());
//                                    List<String> mGroups = emoziesModal.getGroups();
//                                    boolean mFlag = false;
//                                    if (mGroups != null) {
//                                        for (String group : mGroups) {
//                                            if (group.equals("recent")) {
//                                                mFlag = true;
//                                                break;
//                                            }
//                                        }
//
//                                        if (!mFlag) {
//                                            mGroups.add("recent");
//                                        }
//                                    }

                                    break;
                                }
                            }
                        } else {
                            likeFeedListener.likeFeedByReaction(feedId, "", emoziesModal.getCharacter());
                        }

                        emoziesDilog.dismiss();
                        break;
                    }
            }
        }
    }
}

