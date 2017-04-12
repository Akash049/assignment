package com.assignment.akashchandra.sharechatassignment.restAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

public class APIClient {

    public static final String BASE_URL = "http://35.154.143.154:8000/";
    public static final String REQUEST_ID = "c0a469b5f39d24e27b6310a70dd4a60987fd8646";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

