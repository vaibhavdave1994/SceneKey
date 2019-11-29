package com.scenekey.Retrofitprocess;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ravi Birla on 28,September,2019
 */
public interface RetroInterface {


    @POST("invite/contactList")
    @FormUrlEncoded
    Call<ResponseBody> contactList(
            @Field("contact") String contact,
            @Field("user_id") String user_id
    );


    @POST("invite/getContactnumber")
    @FormUrlEncoded
    Call<ResponseBody> getContactnumber(
            @Field("user_id") String user_id
    );

//    https://dev.scenekey.com/event/webservices/activeReward
    @POST("webservices/activeReward")
    @FormUrlEncoded
    Call<ResponseBody> getActiveReward(
            @Field("userId") String user_id
    );

    @POST("getBucketData")
    @FormUrlEncoded
    Call<ResponseBody> getBucketData(
            @Field("user_id") String user_id
    );

    @POST("eventSummary")
    @FormUrlEncoded
    Call<ResponseBody> eventSummary(
            @Field("event_id") String event_id,
            @Field("venue_id") String venue_id
    );

}
