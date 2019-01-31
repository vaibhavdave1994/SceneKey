package com.scenekey.helper;

import com.scenekey.model.UserInfo;

/**
 * Created by mindiii on 22/11/18.
 */

public class CustomeClick {
    private static CustomeClick mInctance;
    private ExploreSearchListener mListner;


    public static CustomeClick getmInctance() {
        if (mInctance==null){
            mInctance = new CustomeClick();
        }
        return mInctance;
    }


    public void setListner(ExploreSearchListener mListner){
        this.mListner = mListner;
    }

    public void onTextChange(UserInfo txt){

        if(mListner!=null){
            mListner.onTextChange(txt);
        }
    }


    public interface ExploreSearchListener {
        void onTextChange(UserInfo Text);
    }
}
