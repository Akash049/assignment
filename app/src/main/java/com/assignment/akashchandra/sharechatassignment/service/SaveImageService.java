package com.assignment.akashchandra.sharechatassignment.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;

import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;
import com.loopj.android.http.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by Akash Chandra on 12-04-2017.
 */

public class SaveImageService extends IntentService {

    public static final int STATUS_RUNNING = 3;
    public static final int STATUS_FINISHED = 4;
    public static final int STATUS_ERROR = 5;

    ResultReceiver receiver;
    Bundle bundle = new Bundle();
    private static final String TAG = "SaveImageService";

    public SaveImageService() {
        super(SaveImageService.class.getName());
    }
    DataBaseHandler db;
    Uri image_uri;
    Context context;
    Bitmap bm;
    String id,url;
    String status;
    byte[] byteArray;
    Bitmap bitmap;
    OutputStream output;
    @Override
    protected void onHandleIntent(Intent intent) {

        db = new DataBaseHandler(this);
        Bitmap bitmap;
        OutputStream output;

        try{
            //Receive the id for the data, to upadte the uri in database
            id = intent.getStringExtra("id");
            //Receive the byte array of the compressed image
            url = intent.getStringExtra("url");
            downloadBitmap(url);
            receiver = intent.getParcelableExtra("receiver");
        }catch (Exception e){

        }

        Log.d(TAG, "Service Stopping!");
        this.stopSelf();

    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                try {

                    //Scalling the image to fit into the area
                    bitmap = Bitmap.createScaledBitmap(bitmap,3000, 2500, false);
                    // Find the SD Card path
                    File filepath = Environment.getExternalStorageDirectory();

                    // Create a new folder in SD Card
                    File dir = new File(filepath.getAbsolutePath()
                            + "/SharechatDir/");
                    dir.mkdirs();

                    // Create a name for the saved image. Here the Name of the Image : _id.png
                    File file = new File(dir, "_"+id+".png");
                    image_uri = Uri.fromFile(file);
                    try{
                        db.UpdateURI(id,image_uri.toString());
                        status = "success";
                        bundle.putString("result", "updated");
                        receiver.send(STATUS_FINISHED, bundle);
                    }catch (Exception e){
                        e.printStackTrace();
                        status = e.toString();
                        bundle.putString("result", e.toString());
                        receiver.send(STATUS_FINISHED, bundle);
                    }

                    try {

                        output = new FileOutputStream(file);

                        // Compress into png format image from 0% - 100%
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                        output.flush();
                        output.close();
                    }

                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    bundle.putString("result", e.toString());
                    receiver.send(STATUS_FINISHED, bundle);
                }
                return bitmap;
            }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        return null;
    }
}

