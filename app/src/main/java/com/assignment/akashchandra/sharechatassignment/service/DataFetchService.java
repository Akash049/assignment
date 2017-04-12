package com.assignment.akashchandra.sharechatassignment.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;
import com.assignment.akashchandra.sharechatassignment.model.requestPostData;
import com.assignment.akashchandra.sharechatassignment.model.responsePostData;
import com.assignment.akashchandra.sharechatassignment.restAPI.APIClient;
import com.assignment.akashchandra.sharechatassignment.restAPI.apiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Akash Chandra on 11-04-2017.
 */

public class DataFetchService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DataFetchService";

    public DataFetchService() {
        super(DataFetchService.class.getName());
    }
    DataBaseHandler db;
    List<DataModel> list = new ArrayList<>();
    @Override
    protected void onHandleIntent(Intent intent) {

        db = new DataBaseHandler(this);
        //list = db.getData();
        Log.d(TAG, "Service Started!");
        final Bundle bundle = new Bundle();
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        final int id = Integer.valueOf(intent.getStringExtra("id"));

        /* Update UI: Download Service is Running */
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        requestPostData reqData = new requestPostData();
        reqData.setRequest_id(APIClient.REQUEST_ID);
        if(id!=-1)
        {
            reqData.setId_offset(id);
        }
        apiInterface apiService = APIClient.getClient().create(apiInterface.class);
        Call<responsePostData> call = apiService.requestData(reqData);
        call.enqueue(new Callback<responsePostData>() {
            @Override
            public void onResponse(Call<responsePostData> call, Response<responsePostData> response) {
                String[] result = {"Success",String.valueOf(id)};
                if(response.body().getSuccess().equals("true"))
                {   try{
                        list = response.body().getData();
                        if(id==-1){
                            if(db.isEmpty())
                            {
                                db.addPostDataList(list);
                                bundle.putStringArray("result", result);
                                receiver.send(STATUS_FINISHED, bundle);
                            }else{
                                db.Erase();
                                db.addPostDataList(list);
                                bundle.putStringArray("result", result);
                                receiver.send(STATUS_FINISHED, bundle);
                            }
                        }else{
                            db.addPostDataList(list);
                            bundle.putStringArray("result", result);
                            receiver.send(STATUS_FINISHED, bundle);
                        }
                    }catch (Exception e){
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                    }
                }else{
                    bundle.putString(Intent.EXTRA_TEXT, response.body().getError());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }

            @Override
            public void onFailure(Call<responsePostData> call, Throwable t) {
                bundle.putString(Intent.EXTRA_TEXT, t.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        });
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();

    }
}
