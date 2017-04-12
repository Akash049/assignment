package com.assignment.akashchandra.sharechatassignment.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

public class DataModel implements Serializable {
    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private String id;

    @SerializedName("author_name")
    private String author_name;

    @SerializedName("author_contact")
    private String author_contact;

    @SerializedName("author_dob")
    private String author_dob;

    @SerializedName("author_status")
    private String author_status;

    @SerializedName("author_gender")
    private String author_gender;

    @SerializedName("profile_url")
    private String profile_url;

    @SerializedName("url")
    private String url;

    @SerializedName("postedOn")
    private long postedOn;

    private String URI;

    public DataModel() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_contact() {
        return author_contact;
    }

    public void setAuthor_contact(String author_contact) {
        this.author_contact = author_contact;
    }

    public String getAuthor_dob() {
        return author_dob;
    }

    public void setAuthor_dob(String author_dob) {
        this.author_dob = author_dob;
    }

    public String getAuthor_status() {
        return author_status;
    }

    public void setAuthor_status(String author_status) {
        this.author_status = author_status;
    }

    public String getAuthor_gender() {
        return author_gender;
    }

    public void setAuthor_gender(String author_gender) {
        this.author_gender = author_gender;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(long postedOn) {
        this.postedOn = postedOn;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }
}
