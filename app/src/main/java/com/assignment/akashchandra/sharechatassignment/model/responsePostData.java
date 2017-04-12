package com.assignment.akashchandra.sharechatassignment.model;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class responsePostData implements  Serializable {

    @SerializedName("success")
    private String success;

    @SerializedName("error")
    private String error;

    @SerializedName("data")
    private List<DataModel> data;

    public responsePostData() {
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<DataModel> getData() {
        return data;
    }

    public void setData(List<DataModel> data) {
        this.data = data;
    }
}
