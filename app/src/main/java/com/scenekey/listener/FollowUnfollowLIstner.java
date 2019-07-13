package com.scenekey.listener;

/**
 * Created by mindiii on 29/11/18.
 */

public interface FollowUnfollowLIstner {
    void getFollowUnfollow(final int followUnfollow, final String biz_tag_id,Object object, int position);
    //void getVenueFollowUnfollow(final String venueId, final int followUnfollow, final String biz_tag_id,int position);
}
