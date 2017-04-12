package com.assignment.akashchandra.sharechatassignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Akash Chandra on 11-04-2017.
 */

public class SaveImgFunction {

    public Uri image_uri;
    public Context context;
    DataBaseHandler db;

    //Initialize "status" as failed for any case
    String status = "failed";
    public SaveImgFunction(Bitmap bm,String id,Context context){
        Bitmap bitmap;
        OutputStream output;
        this.context = context;
        db = new DataBaseHandler(context);

        //Scalling the image to fit into the area
        bitmap = Bitmap.createScaledBitmap(bm, 2500, 2000, false);
        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/SharechatDir/");
        dir.mkdirs();

        // Create a name for the saved image. Here the Name of the Image : _id.png
        File file = new File(dir, "_"+id+".png");
        this.image_uri = Uri.fromFile(file);
        try{
            db.UpdateURI(id,image_uri.toString());
            status = "success";
        }catch (Exception e){
            e.printStackTrace();
            status = e.toString();
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
    }

    public Uri GetUri()
    {
        return image_uri;
    }

    public String getStatus(){
        return status;
    }
}

