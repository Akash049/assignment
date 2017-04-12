package com.assignment.akashchandra.sharechatassignment.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.R;
import com.assignment.akashchandra.sharechatassignment.SaveImgFunction;
import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;
import com.assignment.akashchandra.sharechatassignment.service.DataFetchService;
import com.assignment.akashchandra.sharechatassignment.service.DownloadResultReceiver;
import com.assignment.akashchandra.sharechatassignment.service.SaveImageService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.HttpStatus;


public class HomeActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver{
    //Permission request for read-write
    protected boolean shouldAskPermissions(){ return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);}
    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    int iterator,size;

    public DownloadResultReceiver mReceiver;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<DataModel> download;
    public PostScreen adapter;
    DataBaseHandler db;
    int oldDBSize = 0 , updatedDBSize = 0;
    int partialLoad = 0;
    TextView header;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = new DataBaseHandler(this);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        header = (TextView)findViewById(R.id.header);
        if (shouldAskPermissions()) {
            askPermissions();
        }

        //Receiver to listen from Data
        mReceiver = new DownloadResultReceiver(new Handler());
        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        mReceiver.setReceiver(this);

        download = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(isNetworkConnected()){
                    Toast.makeText(getApplicationContext(),"Service Started",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, HomeActivity.this, DataFetchService.class);
                    intent.putExtra("id","-1");
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("requestId", 101);
                    startService(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"No Internet available",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }


            }
        });


        if(!db.isEmpty()){
            oldDBSize = 0;
            updatedDBSize = db.count();
            download = db.getData();
            //adapter.notifyDataSetChanged();
            adapter = new PostScreen(download,HomeActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HomeActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }else{
            Toast.makeText(getApplicationContext(),"DataBase empty",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    This function is only for the service that are used , namely : DataFetchService and SaveImageService
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DataFetchService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);
                String[] data = resultData.getStringArray("result");
                swipeRefreshLayout.setRefreshing(false);
                /*
                   If the data loading is complete then set the data to the recycler view
                 */
                if(data[1].equals("-1")){
                    Toast.makeText(getApplicationContext(),"Partial Data Downloaded",Toast.LENGTH_SHORT).show();
                    try{
                        //In case of partial data downlload
                        updatedDBSize = db.count();
                        oldDBSize = 0;
                        download.clear();
                        download = db.getData();
                        //adapter.notifyDataSetChanged();
                        adapter = new PostScreen(download,HomeActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HomeActivity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Complete Data Downloaded",Toast.LENGTH_SHORT).show();
                    try{
                        List<DataModel> mData;
                        mData = db.getData();
                        updatedDBSize = db.count();
                        for(DataModel d : mData)
                        {
                            download.add(d);
                        }
                        //adapter.notifyDataSetChanged();
                        adapter = new PostScreen(download,HomeActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HomeActivity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            case DataFetchService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                //data.setText("Result received"+error);
                break;

            case SaveImageService.STATUS_FINISHED:
                String message = resultData.getString("result");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                Log.d("Home:ImageUpdate",message);
        }
    }


    /*
    Adapter for the recycler view
     */
    public class PostScreen extends RecyclerView.Adapter<PostScreen.MyViewHolder>{

        private List<DataModel> list;
        private Context context;

        public PostScreen(List<DataModel> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_home_activity, parent, false);
            return new PostScreen.MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final DataModel data = list.get(position);
            String uri = "";

            holder.loadMore.setVisibility(View.GONE);
            if(data.getType().equals("profile"))
            {
                /*
                Implementing the image  downloading and saving for the Posts.
                The "LoadImage" Asynctask is used to download image if the URI is unavailable
                The "SaveImageService" is used to download and save image to SD card, which is done in the background
                  to prevent app slow down or app crash
                 */
                try{
                    uri = data.getURI();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
                if(uri.equals("")){
                    try{
                        new LoadImage(holder.profileImage,data.getId()).execute(data.getProfile_url());
                        //Toast.makeText(getApplicationContext(),"Image Service Started",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_SYNC, null, HomeActivity.this, SaveImageService.class);
                        intent.putExtra("id",data.getId());
                        intent.putExtra("receiver", mReceiver);
                        intent.putExtra("url", data.getProfile_url());
                        startService(intent);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                     /*
                    TO check from the URI, if the File at that location exits, just in case
                    someone mistakenly deleted the file from the location and the database is still updated with location
                     */
                    File file = new File(Uri.parse(uri).getPath());
                    if(file.exists()){
                        holder.profileImage.setImageURI(Uri.parse(uri));
                    }else{
                        /*
                        If location does not have the required file, then start the initial process of recovering the file
                         */
                        new LoadImage(holder.profileImage,data.getId()).execute(data.getProfile_url());
                        //Toast.makeText(getApplicationContext(),"Image Service Started",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_SYNC, null, HomeActivity.this, SaveImageService.class);
                        intent.putExtra("id",data.getId());
                        intent.putExtra("receiver", mReceiver);
                        intent.putExtra("url", data.getProfile_url());
                        startService(intent);
                    }

                }

                holder.profileView.setVisibility(View.VISIBLE);
                holder.userName.setText(data.getAuthor_name());
                holder.userDob.setText(data.getAuthor_dob());
                holder.userGender.setText(data.getAuthor_gender());
                holder.userStatus.setText(data.getAuthor_status());
            }
            else {
                holder.postData.setVisibility(View.VISIBLE);
                try{
                    uri = data.getURI();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
                if(uri.equals("")){
                    try{
                        new LoadImage(holder.postImage,data.getId()).execute(data.getUrl());
                        Intent intent = new Intent(Intent.ACTION_SYNC, null, HomeActivity.this, SaveImageService.class);
                        intent.putExtra("id",data.getId());
                        intent.putExtra("receiver", mReceiver);
                        intent.putExtra("url", data.getUrl());
                        startService(intent);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //Toast.makeText(getApplicationContext(),"Loaded image from URI",Toast.LENGTH_SHORT).show();
                    /*
                    TO check from the URI, if the File at that location exits, just in case
                    someone mistakenly deleted the file from the location and the database is still updated with location
                     */
                    File file = new File(Uri.parse(uri).getPath());
                    if(file.exists()){
                        holder.postImage.setImageURI(Uri.parse(uri));
                    }else{
                        /*
                        If location does not have the required file, then start the initial process of recovering the file
                        again
                         */
                        new LoadImage(holder.postImage,data.getId()).execute(data.getUrl());
                        //Toast.makeText(getApplicationContext(),"Image Service Started",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_SYNC, null, HomeActivity.this, SaveImageService.class);
                        intent.putExtra("id",data.getId());
                        intent.putExtra("receiver", mReceiver);
                        intent.putExtra("url", data.getUrl());
                        startService(intent);
                    }
                }

                holder.name.setText(data.getAuthor_name());
                holder.postDate.setText(toDate(data.getPostedOn()));
            }

            if(position == (list.size()-1)){
                /*
                TO check if the complete data has been downaloaded or not
                 */
                if(updatedDBSize != oldDBSize)
                {
                    holder.loadMore.setVisibility(View.VISIBLE);
                }

            }
            holder.loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isNetworkConnected()){
                        //holder.loadMore.setVisibility(View.GONE);
                        /*
                        On every click
                         */
                        oldDBSize = updatedDBSize;
                        holder.pg.setVisibility(View.VISIBLE);
                        holder.loadMore.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Service Started",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_SYNC, null, HomeActivity.this, DataFetchService.class);
                         /* Send optional extras to Download IntentService */
                        intent.putExtra("id",data.getId());
                        intent.putExtra("receiver", mReceiver);
                        intent.putExtra("requestId", 101);
                        startService(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet available",Toast.LENGTH_SHORT).show();
                        holder.pg.setVisibility(View.GONE);
                        holder.loadMore.setVisibility(View.VISIBLE);
                    }

                }
            });
            holder.profileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"ID: "+holder.saveID.getText().toString(),Toast.LENGTH_SHORT).show();
                    try {
                        //startActivity(new Intent(HomeActivity.this,ProfileDetailsActivity.class).putExtra("ID",data.getId().toString()));
                        startActivity(new Intent(HomeActivity.this,ProfileDetailsActivity.class).putExtra("ID",holder.saveID.getText().toString()));
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.saveID.setText(data.getId());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout profileView,postData;
            public TextView userName, userAge, userDob,userStatus,userGender,name,postDate,saveID;
            public ImageView profileImage,postImage;
            public Button loadMore;
            public ProgressBar pg;

            public MyViewHolder(View view) {
                super(view);
                profileView = (LinearLayout)view.findViewById(R.id.profileView);
                profileImage = (ImageView)view.findViewById(R.id.profileImage);
                userName = (TextView)view.findViewById(R.id.userName);
                userDob = (TextView)view.findViewById(R.id.userDob);
                userStatus = (TextView)view.findViewById(R.id.userStatus);
                userGender = (TextView)view.findViewById(R.id.userGender);
                name = (TextView)view.findViewById(R.id.name);
                postDate = (TextView)view.findViewById(R.id.postDate);
                postData = (LinearLayout)view.findViewById(R.id.postData);
                postImage = (ImageView)view.findViewById(R.id.postImage);
                loadMore = (Button)view.findViewById(R.id.loadMore);
                saveID = (TextView)view.findViewById(R.id.saveID);
                pg = (ProgressBar)view.findViewById(R.id.pg);

            }
        }

        private String toDate(long timestamp) {
            Date date = new Date (timestamp * 1000);
            return DateFormat.getInstance().format(date).toString();
        }

    }


    class LoadImage extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        String id,imgURI;
        public LoadImage(ImageView imageView, String id) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.id = id;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                return downloadBitmap(params[0]);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        /*byte[] byteArray;
                        try{
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byteArray = stream.toByteArray();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                        }*/

                    }
                }
            }
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
                    /*
                    Scaling the image to a particular size, every time it loads
                     */
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 850, false);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
