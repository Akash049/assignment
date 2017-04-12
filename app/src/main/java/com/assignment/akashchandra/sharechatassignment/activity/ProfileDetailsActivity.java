package com.assignment.akashchandra.sharechatassignment.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.R;
import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;
import com.assignment.akashchandra.sharechatassignment.restAPI.APIClient;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;


public class ProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView userProfileImage;
    Toolbar toolbar;
    String user_id;
    DataModel mdata;
    DataBaseHandler db;
    TextView userName,dob;
    EditText status,contact;
    Button save;
    RadioGroup genderSelect;
    RadioButton male,female;
    int dataChange = 0;
    static final int DATE_DIALOG_ID = 999;
    int year,month,day;
    String uName,uContact,uStatus,uDob,uGender,uIMG,uImgUri;
    String nContact,nStatus,nDob,nGender;
    ProgressBar pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        toolbar = (Toolbar)findViewById(R.id.MyToolbar);
        userProfileImage = (ImageView)findViewById(R.id.userProfileImage);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitle("Username");
        setSupportActionBar(toolbar);
        initialize();
        db = new DataBaseHandler(this);

        Bundle extra = getIntent().getExtras();
        if(extra !=null){
            try{
                Log.d("Profile:IntentData","Data received");
                Intent in = getIntent();
                user_id = in.getStringExtra("ID");
                if(db.getProfile(user_id).getType().equals("profile")){
                    mdata = db.getProfile(user_id);
                    uName = mdata.getAuthor_name();
                    uIMG = mdata.getProfile_url();
                    uContact = mdata.getAuthor_contact();
                    uStatus = mdata.getAuthor_status();
                    uDob = mdata.getAuthor_dob();
                    uGender = mdata.getAuthor_gender();
                    nGender = uGender;
                    uImgUri = mdata.getURI();
                    try{
                        uImgUri = mdata.getURI();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                    if(uImgUri.equals("")){
                        try{
                            new LoadImage(userProfileImage,mdata.getId()).execute(mdata.getProfile_url());
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        File file = new File(Uri.parse(uImgUri).getPath());
                        if(file.exists()){
                            userProfileImage.setImageURI(Uri.parse(uImgUri));
                        }

                    }
                    collapsingToolbar.setTitle(uName);
                    collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.bluecolor));
                    status.setText(uStatus);
                    dob.setText(uDob);
                    contact.setText(uContact);
                    try{
                        if(uGender.equals("male")){
                            male.setChecked(true);
                        }else{
                            female.setChecked(true);
                        }
                    }catch (Exception e){
                    }

                }


            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                Log.d("Profile:IntentData",e.toString());
            }
        }
        genderSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.maleSelect : nGender = "male";
                        //Toast.makeText(getApplicationContext(),"Male Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.femaleSelect : nGender = "female";
                        //Toast.makeText(getApplicationContext(),"female Selected",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void initialize(){
        status = (EditText)findViewById(R.id.statusField);
        contact = (EditText) findViewById(R.id.contactField);
        dob = (TextView)findViewById(R.id.dobText);
        male = (RadioButton)findViewById(R.id.maleSelect);
        female = (RadioButton)findViewById(R.id.femaleSelect);
        genderSelect = (RadioGroup)findViewById(R.id.genderSelect);
        dob.setOnClickListener(this);
        save = (Button)findViewById(R.id.save);
        pg = (ProgressBar)findViewById(R.id.pg);
        save.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.dobText:
            showDialog(DATE_DIALOG_ID);break;

            case R.id.save :
                if(isNetworkConnected()){
                    if(dataChange == 0){
                        pg.setVisibility(View.VISIBLE);
                        save.setVisibility(View.GONE);
                        save.setText("Discard Changes");
                        nStatus = status.getText().toString();
                        nContact = contact.getText().toString();
                        nDob = dob.getText().toString();
                        new UpdateProfileObject().execute();
                        dataChange = 1;

                    }else{

                        pg.setVisibility(View.VISIBLE);
                        save.setVisibility(View.GONE);
                        save.setText("Save Changes");
                        contact.setText(uContact);
                        status.setText(uStatus);
                        dob.setText(uDob);
                        nStatus = uStatus;
                        nContact = uContact;
                        nDob = uDob;
                        new UpdateProfileObject().execute();
                        dataChange = 0;}
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet available",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date _date = new Date(selectedYear, selectedMonth, selectedDay-1);
            String dayOfWeek = simpledateformat.format(_date);
            year = selectedYear;
            month = selectedMonth+1;
            day = selectedDay;
            nDob = selectedDay+"/"+selectedMonth+"/"+selectedYear;
            // set selected date into textview
            dob.setText(nDob);
        }
    };


        private class UpdateProfileObject extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                HttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httpPost = new HttpPost("http://35.154.143.154:8000/update");
                //Post Data
                JSONObject mdata = new JSONObject();
                try {
                    Log.d("Profile:JSONCreating","Success");
                    mdata.put("id",user_id);
                    mdata.put("author_name",uName);
                    mdata.put("author_contact",nContact);
                    mdata.put("author_status",nStatus);
                    mdata.put("author_dob",nDob);
                    mdata.put("author_gender",nGender);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Profile:IntentData",e.toString());
                }

                JSONObject data = new JSONObject();
                try {
                    data.put("request_id",APIClient.REQUEST_ID);
                    data.put("data",mdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                httpPost.addHeader("Content-Type","application/json");
                StringEntity se = null;
                try {
                    se = new StringEntity(data.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    httpPost.setEntity(se);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String jsonResult = "";
                try {HttpResponse response = httpClient.execute(httpPost);
                    jsonResult = inputStreamToString(response.getEntity().getContent()).toString();}
                catch (ClientProtocolException e) {e.printStackTrace();}
                catch (IOException e){e.printStackTrace();}return jsonResult;}
                @Override
                protected void onPreExecute() {super.onPreExecute();}
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    pg.setVisibility(View.GONE);
                    save.setVisibility(View.VISIBLE);
                    try {
                        JSONObject obj = new JSONObject(result);
                        String status = obj.optString("success");
                        if(status.equals("true"))
                        {
                            try{
                                //TO update the profile to android database once success request from server is received
                                db.UpdateProfile(user_id,nDob,nContact,nGender,nStatus);
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                                Log.d("Profile:ProfileUpdate",e.toString());
                            }
                            Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),obj.optString("error"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                private StringBuilder inputStreamToString(InputStream is) {
                    String rLine = "";
                    StringBuilder answer = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    try {while ((rLine = br.readLine()) != null){answer.append(rLine);}}
                    catch (IOException e) {e.printStackTrace();}return answer;}
            }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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

                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.img_placeholder);
                        imageView.setImageDrawable(placeholder);
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
}
