package com.scenekey.cus_view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scenekey.R;
import com.scenekey.activity.HomeActivity;
import com.scenekey.adapter.EmojiAdapter_Notification;
import com.scenekey.aws_service.AWSImage;
import com.scenekey.helper.Constant;
import com.scenekey.model.NotificationData;
import com.scenekey.util.CircleTransform;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Arrays;

//import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by mindiii on 11/2/17.
 */

public abstract class ProfilePopUp_Notification extends Dialog implements View.OnClickListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {

    private static final int maxsize = 28; //Maximum size of recent grid view
    public ArrayList<String> list;
    private HomeActivity activity;
    private Context context;
    private RecyclerView rclv_emoji;
    private int maxNudes;
    private ArrayList<String> getlist;
    private LinearLayout lr_send_nudge ,lr_get_ndge,lr_indicator,linLayEmoji;
    private ImageView iv_delete ,img_cross,iv_indicator,lastSelected , profileImg;
    private TextView tv_nudge,txt_send ,tv_userName;
    private SharedPreferences preferences ;
    private String[] recent ;
    private NotificationData data;
    private int lastFillPosition;
    private boolean isLastFilled,isClicked;
    private int currentImage;
    private AWSImage awsImage;

    protected ProfilePopUp_Notification(@NonNull final Activity activity, final AWSImage awsImage, int maxNudes, final NotificationData nudge) {
        super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.context=activity;
        this.activity= (HomeActivity) activity;
        this.awsImage = awsImage;

        View pop_up_view = LayoutInflater.from(context).inflate(R.layout.popup_nudge_n_notificaiton, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(pop_up_view);
        this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        this.setCanceledOnTouchOutside(false);

        LinearLayout llMain =  pop_up_view.findViewById(R.id.llMain);
        rclv_emoji =  pop_up_view.findViewById(R.id.rclv_emoji);
        lr_send_nudge =  pop_up_view.findViewById(R.id.lr_send_nudge);
        lr_get_ndge =  pop_up_view.findViewById(R.id.lr_get_ndge);
        linLayEmoji =  pop_up_view.findViewById(R.id.linLayEmoji);
        lr_indicator =  pop_up_view.findViewById(R.id.lr_indicator);
        ImageView iv_delete = pop_up_view.findViewById(R.id.iv_delete);
        iv_indicator =  pop_up_view.findViewById(R.id.iv_indicator);
        ImageView img_cross = pop_up_view.findViewById(R.id.img_cross);
        final ImageView img_left = pop_up_view.findViewById(R.id.img_left);
        final ImageView img_right = pop_up_view.findViewById(R.id.img_right);
        ImageView zero = pop_up_view.findViewById(R.id.zero);
        ImageView one = pop_up_view.findViewById(R.id.one);
        ImageView two = pop_up_view.findViewById(R.id.two);
        ImageView three = pop_up_view.findViewById(R.id.three);
        ImageView four = pop_up_view.findViewById(R.id.four);
        ImageView five = pop_up_view.findViewById(R.id.five);
        txt_send=  pop_up_view.findViewById(R.id.txt_send);
        tv_userName=  pop_up_view.findViewById(R.id.tv_userName);
        profileImg =  pop_up_view.findViewById(R.id.img_profile_pic2);
        tv_nudge = pop_up_view.findViewById(R.id.tv_nudge);
        TextView tv_bio = pop_up_view.findViewById(R.id.tv_my_bio);

        String name=nudge.username.split("\\s+")[0];

        tv_bio.setText(nudge.bio);
        Picasso.with(context).load(nudge.getUserImage()).transform(new CircleTransform()).placeholder(R.drawable.image_default_profile).into(profileImg);


        txt_send.setText("Nudge "+name);
        tv_nudge.setText("Nudge "+name+" Back");

        tv_userName.setText(name+" nudged you!");
        linLayEmoji.setVisibility(View.GONE);
        lr_get_ndge.setVisibility(View.VISIBLE);
        txt_send.setVisibility(View.GONE);
        tv_nudge.setVisibility(View.VISIBLE);

        GridLayoutManager layoutManager = new GridLayoutManager(context,4, LinearLayoutManager.HORIZONTAL,false);

        rclv_emoji.setLayoutManager(layoutManager);
        rclv_emoji.setAdapter(new EmojiAdapter_Notification(context,this));
        this.maxNudes = maxNudes;

        list = new ArrayList<>();
        getlist = new ArrayList<>();

        setClicks(llMain,txt_send, iv_delete, img_cross,tv_nudge, one, two, three, four, five, zero,
                img_left,
                img_right);

        this.setOnShowListener(this);
        String[] ar = nudge.nudges.split(",");
        for(String s: ar){
            getlist.add(StringEscapeUtils.unescapeJava(s));
        }

        setText();
        getRecentTask();

        this.setOnDismissListener(this);
        this.data = nudge;
        updateImageView(one);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((HomeActivity) activity).dismissProgDialog();
                if (awsImage.imageList.size() > 1) {
                    img_left.setVisibility(View.VISIBLE);
                    img_right.setVisibility(View.VISIBLE);
                }
            }
        }, 4000);
    }

    private void setClicks(View... views){
        for(View view :views) view.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        rclv_emoji.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstFull = ((GridLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                updateIndicator(firstFull>28?firstFull/28:0);
            }
        });
        updateIndicator();
    }


    public void setText(String text){
        if(list.size()< maxNudes){
            list.add(text);
            int i=0;
            for(String str : list){
                ((EmojiTextView)lr_send_nudge.getChildAt(i)).setText(str.trim());
                i++;
            }
        }
    }

    public void setText(){

        int i=0;
        for(String str : getlist){
            ((EmojiTextView)lr_get_ndge.getChildAt(i)).setText(StringEscapeUtils.unescapeJava(str.trim()));
            Log.e("value" ,str);
            i++;
            if(i==4)return;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.llMain:
                activity.hideStatusBar();
                this.dismiss();
                break;

            case R.id.tv_nudge:
                linLayEmoji.setVisibility(View.VISIBLE);
                lr_get_ndge.setVisibility(View.GONE);
                txt_send.setVisibility(View.VISIBLE);
                tv_nudge.setVisibility(View.GONE);
                break;

            case R.id.iv_delete:
                delete();
                break;
            case R.id.img_cross:
                this.dismiss();
                break;
            case R.id.zero:
                ((EmojiAdapter_Notification)rclv_emoji.getAdapter()).setList(-1);
                rclv_emoji.smoothScrollToPosition(0);
                updateImageView((ImageView) v);
                break;
            case R.id.one:
                ((EmojiAdapter_Notification)rclv_emoji.getAdapter()).setList(0);
                rclv_emoji.smoothScrollToPosition(0);
                updateImageView((ImageView) v);
                break;
            case R.id.two:
                ((EmojiAdapter_Notification)rclv_emoji.getAdapter()).setList(1);
                rclv_emoji.smoothScrollToPosition(0);
                updateImageView((ImageView) v);
                break;
            case R.id.three:
                ((EmojiAdapter_Notification)rclv_emoji.getAdapter()).setList(2);
                rclv_emoji.smoothScrollToPosition(0);
                updateImageView((ImageView) v);
                break;
            case R.id.four:
                ((EmojiAdapter_Notification)rclv_emoji.getAdapter()).setList(3);
                rclv_emoji.smoothScrollToPosition(0);
                updateImageView((ImageView) v);
                break;
            case R.id.five:
                ((EmojiAdapter_Notification)rclv_emoji.getAdapter()).setList(4);
                rclv_emoji.smoothScrollToPosition(0);
                updateImageView((ImageView) v);
                break;
            case R.id.txt_send:
                txt_send.setClickable(false);
                if (!isClicked) {
                    try {
                        activity.hideStatusBar();
                        onSendCLick((TextView) v,this,data);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    isClicked = true;
                }
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txt_send.setClickable(true);
                            isClicked = false;
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_right:
                setUserImage(true);
                // onNextClick((ImageView) v,this);
                break;
            case R.id.img_left:
                setUserImage(false);
                // onPrevClick((ImageView) v,this);
                break;
        }
    }

    private void delete(){
        if(list.size()>0){
            int position = list.size()-1;
            list.remove(position);
            ((EmojiTextView)lr_send_nudge.getChildAt(position)).setText("");
        }
    }

    public void updateIndicator(){
        updateIndicator(0);
    }

    private void updateIndicator(int position){
        int i =((EmojiAdapter_Notification)rclv_emoji.getAdapter()).getIndicatorCount((int) context.getResources().getDimension(R.dimen._240sdp), (int) context.getResources().getDimension(R.dimen._40sdp));
        lr_indicator.setWeightSum(i);
        int padding = (int) context.getResources().getDimension(R.dimen._3sdp);
        for (int j=0;j<i;j++){
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(iv_indicator.getLayoutParams());
            imageView.setImageResource(R.drawable.iv_indicator);
            imageView.setSelected(false);
            imageView.setPadding(padding,padding,padding,padding);
            if(position==j)imageView.setSelected(true);
            if(j==0)lr_indicator.removeAllViews();
            lr_indicator.addView(imageView);
        }
    }


    private void setUserImage(boolean isRight) {
        if (awsImage.imageList.size() != 0) {
            currentImage = (isRight ? (currentImage == awsImage.imageList.size() - 1 ? 0 : currentImage + 1) : (currentImage == 0 ? awsImage.imageList.size() - 1 : currentImage - 1));
            Picasso.with(activity).load(awsImage.imageList.get(currentImage).path).transform(new CircleTransform()).placeholder(R.drawable.image_default_profile).into(profileImg);
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        updateIndicator();
    }

    public String[]  getRecent(){
        preferences = context.getSharedPreferences(Constant.PREF_EMOJI, Context.MODE_PRIVATE);
        String s = preferences.getString(Constant.PREF_string,"[]");
        String ar [] = new String[maxsize];
        int i=0;
        for(String txt :s.substring(1,s.length()-1).split(",")){
            ar[i] = txt.replace("null","").trim();
            if(txt.trim().isEmpty() && !isLastFilled) {
                lastFillPosition = i;
                isLastFilled = true;
            }
            i++;
        }
        recent = ar;
        return ar;
    }

    public synchronized void setRecent(final String text){
        new AsyncTask<Void,Void ,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                preferences = context.getSharedPreferences(Constant.PREF_EMOJI, Context.MODE_PRIVATE);
                if(lastFillPosition <maxsize){
                    if(canAddToList(text))
                    {
                        recent[lastFillPosition] = text.trim();
                        lastFillPosition++;
                    }
                }
                else {
                    if(canAddToList(text))
                    {
                        recent[0]= text.trim();
                        lastFillPosition = 1;
                    }
                }
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.apply();
                    editor.putString(Constant.PREF_string, Arrays.toString(recent));
                    editor.commit();
                }

                return null;
            }
        }.execute();
    }

    private void getRecentTask(){
        new AsyncTask<Void ,Void ,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                recent = new String[maxsize];
                for(String s: recent)s="";
                getRecent();
                return null;
            }
        }.execute();
    }

    private boolean canAddToList(String text){
        for(String s :recent){
            if(s!= null && s.equals(text)) return false;
        }
        return true;
    }

    private void updateImageView(ImageView selected){
        updateIndicator();
        setImage(selected,true);
        if(lastSelected != null)setImage(lastSelected,false);
        lastSelected = selected;
    }

    private void setImage(ImageView view , boolean isActive){
        view.setBackgroundResource(isActive?  R.color.old_primary     : R.drawable.bg_table     );
        switch (view.getId()){
            case R.id.zero: view.setImageResource(isActive?  R.drawable.ic_zero_active     : R.drawable.ic_zero     );    break;
            case R.id.one:  view.setImageResource(isActive?  R.drawable.ic_one_active      : R.drawable.ic_one      );    break;
            case R.id.two:  view.setImageResource(isActive?  R.drawable.ic_two_active      : R.drawable.ic_two      );    break;
            case R.id.three:view.setImageResource(isActive?  R.drawable.ic_three_active    : R.drawable.ic_three    );    break;
            case R.id.four: view.setImageResource(isActive?  R.drawable.ic_four_car_active : R.drawable.ic_four_car );    break;
            case R.id.five: view.setImageResource(isActive?  R.drawable.ic_five_active     : R.drawable.ic_five     );    break;
        }
    }

    public abstract void onClickView(TextView textView , ProfilePopUp_Notification profilePopUp);
    public abstract void onSendCLick(TextView textView , ProfilePopUp_Notification profilePopUp , final NotificationData obj );
    public abstract void onPrevClick(ImageView textView , ProfilePopUp_Notification profilePopUp);
    public abstract void onNextClick(ImageView textView , ProfilePopUp_Notification profilePopUp);
    public abstract void onDismiss(ProfilePopUp_Notification profilePopUp);

    @Override
    public void onDismiss(DialogInterface dialog) {
        onDismiss(this);
    }

    public void updateData(NotificationData nudge){
        getlist.clear();
        this.data = nudge;
        String[] ar = nudge.nudges.split(",");
        Log.e("Data" , nudge.nudges);
        for(String s: ar){
            getlist.add(StringEscapeUtils.unescapeJava(s));
        }
        setText();
        tv_userName.setText(nudge.username);
        try {

            Picasso.with(context).load(nudge.getUserImage()).transform(new CircleTransform()).into(profileImg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
