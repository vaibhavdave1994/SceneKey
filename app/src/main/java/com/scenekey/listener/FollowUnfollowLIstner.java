package com.scenekey.listener;


import com.scenekey.model.Events;
import com.scenekey.model.Venue;

/**
 * Created by mindiii on 29/11/18.
 */

public interface FollowUnfollowLIstner {
    void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,int position);
}
