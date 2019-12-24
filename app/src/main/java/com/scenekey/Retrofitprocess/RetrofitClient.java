package com.scenekey.Retrofitprocess;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
//    private final String BASE_URL = "https://dev.scenekey.com/event/";//dev
    private final String BASE_URL = "https://scenekey.com/event/"; ;//live
    private static RetrofitClient minstance;
    private Retrofit retrofit;

    private RetrofitClient() {



        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();


    }

    public static RetrofitClient getInstance() {
        if (minstance == null) {
            minstance = new RetrofitClient();
        }
        return minstance;
    }


    public RetroInterface getAnotherApi() {
        return retrofit.create(RetroInterface.class);
    }

}
