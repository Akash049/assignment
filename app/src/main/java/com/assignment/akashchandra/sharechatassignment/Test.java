package com.assignment.akashchandra.sharechatassignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by Akash Chandra on 12-04-2017.
 */

public class Test extends AppCompatActivity {
    Button get,set;
    ImageView img;
    TextView data;
    DataBaseHandler db;
    Uri image_uri;
    String id;
    Bundle bundle = new Bundle();
    OutputStream output;
    String url;
    DataModel mdata;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        get = (Button)findViewById(R.id.get);
        set = (Button)findViewById(R.id.set);
        img = (ImageView)findViewById(R.id.img);
        data = (TextView)findViewById(R.id.data);
        id = "2";
        db = new DataBaseHandler(this);
        mdata = db.getProfile("2");
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = null;
                try{
                    mdata = db.getProfile("2");
                    uri = mdata.getURI();
                    if(uri.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"URI not enabled",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),uri,Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Set : "+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public byte[] convertBitmapToByteArray(Context context, Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }


}
