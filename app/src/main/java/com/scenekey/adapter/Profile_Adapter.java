package com.scenekey.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.helper.Constant;
import com.scenekey.helper.DoubleClickListener;
import com.scenekey.helper.WebServices;
import com.scenekey.listener.ForDeleteFeed;
import com.scenekey.listener.GetZoomImageListener;
import com.scenekey.listener.LikeFeedListener;
import com.scenekey.liveSideWork.LiveProfileActivity;
import com.scenekey.model.EmoziesModal;
import com.scenekey.model.EventAttendy;
import com.scenekey.model.EventDetailsForActivity;
import com.scenekey.model.Feeds;
import com.scenekey.util.Utility;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by mindiii on 15/2/18.
 */

public class Profile_Adapter extends RecyclerView.Adapter<Profile_Adapter.ViewHolder> {

    public String userExistOrNot = "";
    private boolean doubleClick = false;
    private Context context;
    private ArrayList<Feeds> feedList;
    private List<EmoziesModal> emoziesModalArrayList;
    private ArrayList<EventAttendy> userList;
    private String sap_time;
    private String userId;
    private String eventid;
    private ForDeleteFeed deleteFeed;
    private LikeFeedListener likeFeedListener;
    private Handler handler = new Handler();
    private Utility utility;
    private GetZoomImageListener getZoomImageListener;

    public Profile_Adapter(Context context, String userId,
                           ArrayList<Feeds> feedsList,
                           ArrayList<EventAttendy> userList, String eventid,
                           List<EmoziesModal> emoziesModalArrayList, ForDeleteFeed deleteFeed,
                           LikeFeedListener likeFeedListener, GetZoomImageListener getZoomImageListener) {
        this.context = context;
        this.feedList = feedsList;
        this.userList = userList;
        this.eventid = eventid;
        this.userId = userId;
        this.deleteFeed = deleteFeed;
        this.likeFeedListener = likeFeedListener;
        this.emoziesModalArrayList = emoziesModalArrayList;
        this.getZoomImageListener = getZoomImageListener;

        utility = new Utility(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_comment_view, parent, false);
        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feeds feeds = feedList.get(position);


        ViewGroup.LayoutParams layoutParams = holder.img_demo_event.getLayoutParams();
        layoutParams.height = (int) ((HomeActivity.ActivityWidth - context.getResources().getDimension(R.dimen._50sdp)) * 0.75);
        Log.e("HEIGHT", layoutParams.height + "=====" + HomeActivity.ActivityWidth);
        holder.img_demo_event.setLayoutParams(layoutParams);

        if (feeds.date != null && !feeds.date.isEmpty()) {

            String startTime = feeds.date;
            StringTokenizer tk = new StringTokenizer(startTime);
            String date = tk.nextToken();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat dateinPmAm = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            Date dt = null;
            try {
                dt = sdf.parse(date);
                sap_time = dateinPmAm.format(dt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.emaojisRecyclerView.setVisibility(View.VISIBLE);
            holder.demo_date_tv.setVisibility(View.VISIBLE);
            holder.demo_date_tv.setText(sap_time);

            switch (feeds.user_status) {
                case "1":
                    holder.demo_profile_user.setBorderColor(ContextCompat.getColor(context, R.color.go_green_color));
                    break;

                case "2":
                    holder.demo_profile_user.setBorderColor(ContextCompat.getColor(context, R.color.caution_yellow_color));
                    break;

                case "3":
                    holder.demo_profile_user.setBorderColor(ContextCompat.getColor(context, R.color.stop_red_color));
                    break;
            }

            Picasso.with(context).load(WebServices.USER_IMAGE + feeds.userimage).placeholder(R.drawable.bg_event_card).into(holder.demo_profile_user);
        } else {
            holder.demo_date_tv.setVisibility(View.GONE);
            holder.emaojisRecyclerView.setVisibility(View.GONE);
            Picasso.with(context).load(feeds.userimage).placeholder(R.drawable.bg_event_card).into(holder.demo_profile_user);
        }

        holder.demo_user_name.setText(feeds.username);

        if (feeds.type.equals(Constant.FEED_TYPE_COMMENT)) {
            holder.img_demo_event.setVisibility(View.GONE);
            holder.txt_demo_comment.setVisibility(View.VISIBLE);
            holder.txt_demo_comment.setText(feeds.feed);

        } else if (feeds.type.equals(Constant.FEED_TYPE_PICTURE)) {
            holder.txt_demo_comment.setVisibility(View.GONE);
            holder.img_demo_event.setVisibility(View.VISIBLE);
            Picasso.with(context).load(feeds.isUri ? feeds.feed : WebServices.FEED_IMAGE + feeds.feed).placeholder(R.drawable.bg_event_card).into(holder.img_demo_event);
        }

        EmoziesAdapter adapter = new EmoziesAdapter(holder.demo_date_tv.getContext(),
                userExistOrNot, feeds, feeds.feedId, emoziesModalArrayList, feeds.feedSmilies, deleteFeed, likeFeedListener);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);

        holder.emaojisRecyclerView.setLayoutManager(gridLayoutManager);
        holder.emaojisRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);
        holder.emaojisRecyclerView.setNestedScrollingEnabled(false);
        holder.emaojisRecyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return feedList.size();
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


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img_demo_event;
        private TextView demo_user_name, txt_demo_comment, demo_date_tv;
        private CircularImageView demo_profile_user;
        private RecyclerView emaojisRecyclerView;


        ViewHolder(View view) {
            super(view);

            img_demo_event = view.findViewById(R.id.img_demo_event);
            demo_profile_user = view.findViewById(R.id.demo_profile_user);
            demo_user_name = view.findViewById(R.id.demo_user_name);
            txt_demo_comment = view.findViewById(R.id.txt_demo_comment);
            demo_date_tv = view.findViewById(R.id.demo_date_tv);
            emaojisRecyclerView = view.findViewById(R.id.emaojisRecyclerView);


            txt_demo_comment.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Feeds feeds = feedList.get(getAdapterPosition());
                    if (userId.equals(feeds.userid)) {

                        if (!userExistOrNot.equals("")) {
                            cantJoinNotExixtUserDialog(userExistOrNot);
                        } else {
                            deleteFeed.getFeedIdForDelete(feeds.feedId, "", "");
                        }
                    }

                    return true;
                }
            });

            /*..................................................................................*/
            txt_demo_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {

                            doubleClick = false;
                        }
                    };

                    if (doubleClick) {

                        Feeds feeds = feedList.get(getAdapterPosition());

                        int size = feeds.feedSmilies.size();
                        if (!userExistOrNot.equals("")) {
                            cantJoinNotExixtUserDialog(userExistOrNot);
                        } else {

                            if (size != 0) {

                                if (size > 1) {
                                    for (int j = 1; j < size; j++) {

                                        if (feeds.feedSmilies.get(j).reaction.equals(("❤"))) {
                                            if (feeds.feedSmilies.get(j).isReaction.equals("1")) {
                                                likeFeedListener.likeFeedByReaction("", feeds.feedSmilies.get(j).id, "");
                                                break;
                                            } else {
                                                likeFeedListener.likeFeedByReaction(feeds.feedId, "", "");
                                                break;
                                            }
                                        } else if (j == size - 1) {
                                            likeFeedListener.likeFeedByReaction(feeds.feedId, "", "");
                                            break;
                                        }
                                    }
                                } else {
                                    likeFeedListener.likeFeedByReaction(feeds.feedId, "", "");
                                }
                            }
                        }

                        //your logic for double click action
                        doubleClick = false;

                    } else {
                        doubleClick = true;
                        handler.postDelayed(r, 500);
                    }
                }
            });


            img_demo_event.setOnClickListener(new DoubleClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onSingleClick(View v) {
                    Feeds feeds = feedList.get(getAdapterPosition());
                    if (!userExistOrNot.equals("")) {
                        cantJoinNotExixtUserDialog(userExistOrNot);
                    } else {
                        String image_url = feeds.isUri ? feeds.feed : WebServices.FEED_IMAGE + feeds.feed;
                        getZoomImageListener.getImageUrl(image_url);
                    }
                }

                @Override
                public void onDoubleClick(View v) {

                    Feeds feeds = feedList.get(getAdapterPosition());

                    int size = feeds.feedSmilies.size();
                    if (!userExistOrNot.equals("")) {
                        cantJoinNotExixtUserDialog(userExistOrNot);
                    } else {

                        if (size != 0) {

                            if (size > 1) {
                                for (int j = 1; j < size; j++) {

                                    if (feeds.feedSmilies.get(j).reaction.equals(("❤"))) {
                                        if (feeds.feedSmilies.get(j).isReaction.equals("1")) {
                                            likeFeedListener.likeFeedByReaction("", feeds.feedSmilies.get(j).id, "");
                                            break;
                                        } else {
                                            likeFeedListener.likeFeedByReaction(feeds.feedId, "", "");
                                            break;
                                        }
                                    } else if (j == size - 1) {
                                        likeFeedListener.likeFeedByReaction(feeds.feedId, "", "");
                                        break;
                                    }
                                }
                            } else {
                                likeFeedListener.likeFeedByReaction(feeds.feedId, "", "");
                            }
                        }
                    }
                }
            });

            img_demo_event.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Feeds feeds = feedList.get(getAdapterPosition());
                    if (userId.equals(feeds.userid)) {

                        if (!userExistOrNot.equals("")) {
                            cantJoinNotExixtUserDialog(userExistOrNot);
                        } else {
                            deleteFeed.getFeedIdForDelete(feeds.feedId, "", "");
                        }
                    }

                    return true;
                }
            });
        }

        /*..................................................................................*/
        @Override
        public void onClick(View view) {

            Feeds feeds = feedList.get(getAdapterPosition());
            switch (view.getId()) {

                case R.id.demo_profile_user:

                    if (!userExistOrNot.equals("")) {
                        cantJoinNotExixtUserDialog(userExistOrNot);
                    } else {

                        if (feedList.size() != 0) {

                            if (feeds.userid != null) {

                                for (int i = 0; i < userList.size(); i++) {

                                    if (feeds.userid.equals(userList.get(i).userid)) {
                                        Intent intent = new Intent(context, LiveProfileActivity.class);
                                        intent.putExtra("from", "fromProfileAdapter");
                                        intent.putExtra("fromLiveRoomList", userList);
                                        intent.putExtra("eventId", eventid);
                                        intent.putExtra("feedsid", feeds.userid);
                                        context.startActivity(intent);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }
}

