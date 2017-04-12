package com.assignment.akashchandra.sharechatassignment.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

public class requestPostData implements Serializable {
    @SerializedName("request_id")
    private String request_id;

    @SerializedName("id_offset")
    private int id_offset;

    public requestPostData() {
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getId_offset() {
        return id_offset;
    }

    public void setId_offset(int id_offset) {
        this.id_offset = id_offset;
    }
}
