package com.assignment.akashchandra.sharechatassignment.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.assignment.akashchandra.sharechatassignment.model.DataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "SQL";

    // Specifying the DATABASE VERSION
    private static final int DATABASE_VERSION = 1;

    // Specifying the database name
    private static final String DATABASE_NAME = "POSTS";

    // Specifying the TABLE NAME
    public static final String TABLE_NAME = "posts";

    //Specifying the fields
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID = "id";
    private static final String KEY_AUTHOR_NAME = "author_name";
    private static final String KEY_AUTHOR_DOB = "author_dob";
    private static final String KEY_AUTHOR_STATUS = "author_status";
    private static final String KEY_AUTHOR_CONTACT = "author_contact";
    private static final String KEY_GENDER = "author_gender";
    private static final String KEY_PROFILE_URL = "profile_url";
    private static final String KEY_URL = "url";
    private static final String KEY_POSTEDON = "postedOn";
    private static final String KEY_URI = "uri";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( " + KEY_TYPE + " TEXT , " + KEY_ID + " TEXT , " +
                KEY_AUTHOR_NAME + " TEXT , " + KEY_AUTHOR_DOB + " TEXT , " + KEY_AUTHOR_STATUS + " TEXT , " +  KEY_AUTHOR_CONTACT  + " TEXT , " + KEY_GENDER + " TEXT , " + KEY_PROFILE_URL +
                " TEXT ," + KEY_URL +" TEXT, "+ KEY_POSTEDON +" TIMESTAMP  ," + KEY_URI + "  TEXT  ) ";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPostDataList(List<DataModel> data1){

        final SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(DataModel data : data1){
            values.put(KEY_TYPE,data.getType());
            values.put(KEY_ID,data.getId());
            values.put(KEY_AUTHOR_NAME,data.getAuthor_name());
            values.put(KEY_AUTHOR_DOB,data.getAuthor_dob());
            values.put(KEY_AUTHOR_STATUS,data.getAuthor_status());
            values.put(KEY_AUTHOR_CONTACT,data.getAuthor_contact());
            values.put(KEY_GENDER,data.getAuthor_gender());
            values.put(KEY_PROFILE_URL,data.getProfile_url());
            values.put(KEY_URL,data.getUrl());
            values.put(KEY_POSTEDON,data.getPostedOn());
            values.put(KEY_URI,"");
            db.insert(TABLE_NAME ,null,values);
        }
        db.close();
    }
    public void addPostData(DataModel data){

        final SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TYPE,data.getType());
        values.put(KEY_ID,data.getId());
        values.put(KEY_AUTHOR_NAME,data.getAuthor_name());
        values.put(KEY_AUTHOR_DOB,data.getAuthor_dob());
        values.put(KEY_AUTHOR_STATUS,data.getAuthor_status());
        values.put(KEY_AUTHOR_CONTACT,data.getAuthor_contact());
        values.put(KEY_GENDER,data.getAuthor_gender());
        values.put(KEY_PROFILE_URL,data.getProfile_url());
        values.put(KEY_POSTEDON,data.getPostedOn());
        values.put(KEY_URL,data.getUrl());
        db.insert(TABLE_NAME ,null,values);
        db.close();
    }

    public void UpdateURI( String id, String uri)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE "+TABLE_NAME+ " SET " + KEY_URI + " ='"+ uri+"' WHERE "+KEY_ID+"='"+id+"'";
        db.execSQL(query);
    }

    //To update the profile data once the data has been updated to the server
    public void UpdateProfile( String id, String dob, String contact, String gender, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE "+TABLE_NAME+ " SET " + KEY_AUTHOR_DOB + " ='"+ dob +"' , " + KEY_AUTHOR_CONTACT + " ='"+ contact +"' , "
                + KEY_GENDER + " ='"+ gender +"' , "+ KEY_AUTHOR_STATUS + " ='"+ status  +"' WHERE "+KEY_ID+"='"+id+"'";
        db.execSQL(query);
    }


    public DataModel getProfile(String id)
    {
        DataModel data = new DataModel();
        String query = "SELECT  * FROM " + TABLE_NAME + " WHERE (" +KEY_ID+"='"+id+"')" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do{
                data.setType(cursor.getString(0));
                data.setId(cursor.getString(1));
                data.setAuthor_name(cursor.getString(2));
                data.setAuthor_dob(cursor.getString(3));
                data.setAuthor_status(cursor.getString(4));
                data.setAuthor_contact(cursor.getString(5));
                data.setAuthor_gender(cursor.getString(6));
                data.setProfile_url(cursor.getString(7));
                data.setURI(cursor.getString(10));
            }while (cursor.moveToNext());
        }
        return data;
    }

    public void Erase()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME;
        db.execSQL(query);
    }

    public boolean isEmpty()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor mcursor = db.rawQuery(count,null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0)
            return false;
        else
            return true;
    }

    public int count()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_NAME;
        Cursor mcursor = db.rawQuery(count,null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return  icount;
    }

    public ArrayList<DataModel> getData()
    {
        ArrayList<DataModel> dataModels = new ArrayList<DataModel>();
        String query = "SELECT  * FROM " + TABLE_NAME ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst())
        {
            do{
                DataModel data = new DataModel();
                data.setType(cursor.getString(0));
                data.setId(cursor.getString(1));
                data.setAuthor_name(cursor.getString(2));
                data.setAuthor_dob(cursor.getString(3));
                data.setAuthor_status(cursor.getString(4));
                data.setAuthor_contact(cursor.getString(5));
                data.setAuthor_gender(cursor.getString(6));
                data.setProfile_url(cursor.getString(7));
                data.setUrl(cursor.getString(8));
                data.setPostedOn(Long.valueOf(cursor.getString(9)));
                data.setURI(cursor.getString(10));
                dataModels.add(data);
            }while (cursor.moveToNext());
        }
        return dataModels;
    }
}
