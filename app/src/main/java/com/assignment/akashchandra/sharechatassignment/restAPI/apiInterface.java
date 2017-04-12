package com.assignment.akashchandra.sharechatassignment.restAPI;

import com.assignment.akashchandra.sharechatassignment.model.requestPostData;
import com.assignment.akashchandra.sharechatassignment.model.responsePostData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

public interface apiInterface {

    @POST("/data")
    Call<responsePostData> requestData(@Body requestPostData data);

}
