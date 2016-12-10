package com.eis.lnp.lnpsolutions;

import com.eis.lnp.lnpsolutions.MainPage;
import com.eis.lnp.lnpsolutions.AndroidMultiPartEntity.ProgressListener;
import com.eis.lnp.lnpsolutions.ModelRealm.UploadData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class UploadActivity extends Activity implements OnClickListener{
    // LogCat tag
    private static final String TAG = MainPage.class.getSimpleName();

    private ProgressBar progressBar;
    private Button btnCapturePicture;
    private Uri fileUri; // file url to store image/video

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private String filePath = null;
    private ArrayList<String> imags=new ArrayList<String>();
    private TextView txtPercentage;
    private ImageView imgPreview;
    private Button btnUpload;
    String user,internet;
    String result="LOGIN";
    String lat,lon;
    String pic;
    int currImg=0;
    ArrayList<Integer> bImags=new ArrayList<Integer>();
    Context cont;
    long totalSize = 0;
    EditText sLand;
    EditText tLand;
    EditText fName;
    EditText pName;
    EditText harvest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sLand=(EditText)findViewById(R.id.land_siz);
        tLand=(EditText)findViewById(R.id.land_type);
        fName=(EditText)findViewById(R.id.farmer_name);
        pName=(EditText)findViewById(R.id.farmer_ph);
        harvest=(EditText)findViewById(R.id.harvest_m);
        cont=this;
        // Changing action bar background color
//        getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor(getResources().getString(
//                        R.color.action_bar))));

        // Receiving the data from previous activity
        Intent i = getIntent();
        /*internet=i.getStringExtra("internet");
        if(internet.equals("no")){btnUpload.setText("Save to Database");}
*/
        // image or video path that is captured in previous activity

        user = i.getStringExtra("user");
        lat=i.getStringExtra("lat");
        lon=i.getStringExtra("lon");
        System.out.println(lat+"   "+lon);
        // boolean flag to identify the media type, image or video
        btnUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(UploadActivity.this, "Works", Toast.LENGTH_SHORT).show();
                Realm realm=Realm.getDefaultInstance();
                int maxId= 0;
                try {
                    maxId=realm.where(UploadData.class).max("id").intValue();
                    System.out.println("Max Value "+maxId);
                } catch (Exception e) {
                    e.printStackTrace();
                    maxId=1;
                }


                UploadData uploadData=new UploadData(maxId+1, sLand.getText().toString(),
                        tLand.getText().toString(),fName.getText().toString(),pName.getText().toString(),
                        harvest.getText().toString(),lat,lon,imageData);
                System.out.println("Max Value "+(maxId+1));
                realm.beginTransaction();
                uploadData=realm.copyToRealm(uploadData);
                realm.commitTransaction();


                AsyncHttpClient client = new AsyncHttpClient();
                try {
                    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(null, null);
                    MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                    sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    client.setSSLSocketFactory(sf);
                } catch (Exception e) {
                }
                client.addHeader("Content-Type","application/x-www-form-urlencoded");

                final RequestParams requestParams = new RequestParams();



                requestParams.put("driver_id", maxId+1);
                requestParams.put("farmers_name", fName.getText().toString());
                requestParams.put("phone_no", pName.getText().toString());
                requestParams.put("land_size", tLand.getText().toString());
                requestParams.put("harvest_month",harvest.getText().toString());
                requestParams.put("latitude", lat);
                requestParams.put("longitude", lon);
                requestParams.put("images", imageData);


                System.out.println(requestParams.toString());



                client.post(getApplicationContext(), "http://lnpsolution.acumenits.com/tags/add", requestParams, new JsonHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            System.out.println("1. " + response.toString());
                            if (response.getString("status").equals("true")) {
                                Toast.makeText(UploadActivity.this, "Successfully upload", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            System.out.println("2. " + response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            System.out.println("3. " + responseString.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        try {
                            System.out.println("4. " + responseString.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        try {
                            System.out.println("5. " + errorResponse.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        try {
                            System.out.println("6. " + errorResponse.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                /*RealmResults<UploadData> uploadDatas=realm.where(UploadData.class).findAll();
                for (UploadData ud:uploadDatas) {
                    System.out.println("Data--->>> "+ud.getId());
                }*/
            }
        });

        /*btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int j=0;
                // uploading the file to server
                CharSequence userText="";
                //check field values and then upload
                if(!sLand.getText().toString().equals("")){
                    if(!tLand.getText().toString().equals("")){
                        if(!fName.getText().toString().equals("")){
                            if(!pName.getText().toString().equals("")){
                                if(!harvest.getText().toString().equals("")){
                                    if(internet.equals("yes")){
                                        new UploadFileToServer().execute();
                                    }else{
                                        DatabaseHandler db = new DatabaseHandler(cont);
                                        //Removing Deleted images

                                        Tag tag= new Tag( imags,
                                                tLand.getText().toString(),
                                                sLand.getText().toString(),
                                                fName.getText().toString(),
                                                pName.getText().toString(),
                                                harvest.getText().toString(),
                                                lat,
                                                lon,
                                                bImags
                                        );
                                        db.addTag(tag);



                                        Intent i = new Intent(UploadActivity.this, MainPage.class);
                                        i.putExtra("internet","no");
                                        startActivity(i);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
                                        builder.setMessage("Saved").setTitle("Response from DB")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent i = new Intent(UploadActivity.this, MainPage.class);
                                                        i.putExtra("internet","no");
                                                        startActivity(i);
                                                    }
                                                });

                                    }//else internet finishes
                                }else{
                                    userText="Invalid Harvest Value";
                                }
                            }else{
                                userText="Invalid Phone Number";
                            }
                        }else{
                            userText="Invalid Farmer Name";
                        }
                    }else{
                        userText="Invalid Land type";
                    }
                }else{
                    userText="Invalid Land size";
                }

                if(!userText.equals("")){
                    Toast.makeText(getApplicationContext(), userText, Toast.LENGTH_SHORT).show();
                }
            }//onclick ends
        });*/

        //Camera Upload
        btnCapturePicture = (Button) findViewById(R.id.btnAddPicture);

        /**
         * Capture image button click event
         */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

    }

    /**
     * Displaying captured image on the screen
     * */
    String imageData="";
    private void previewMediaNew(boolean isImage, String fileP) {
        imageData="";
        LinearLayout lL = (LinearLayout) findViewById(R.id.upImag);

        ImageView imgView = new ImageView(this);
        imgView.setVisibility(View.VISIBLE);
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();
        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(fileP, options);

        imageData=Image.bitmapToBase64(bitmap);
        imgView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1000, 600);
        layoutParams.setMargins(0, 2, 0, 0);
        imgView.setLayoutParams(layoutParams);
        imgView.setId(currImg);
        imgView.setTag("im"+currImg);


        Button t;
        t= new Button(this);
        t.setText("Delete Image");
        t.setTag("bt"+currImg);
        t.setId(currImg);
        t.setOnClickListener(this);

        lL.addView(imgView);
        lL.addView(t);
        currImg=currImg+1;
    }

    @Override
    public void onClick(View v) {
        ImageView img = (ImageView) findViewById(R.id.upImag).findViewWithTag("im"+v.getId());
        Button bt = (Button) findViewById(R.id.upImag).findViewWithTag("bt"+v.getId());

        bt.setText("Deleted");
        img.setVisibility(View.GONE);
        bt.setVisibility(View.GONE);
        this.bImags.add(v.getId());
    }


    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                int i=0,j=0;

                if(imags.size()>0){
                    for(String im : imags){
                        if(!bImags.contains(j)){
                            File sourceFil = new File(im);
                            entity.addPart("images["+String.valueOf(i)+"]", new FileBody(sourceFil));
                            i++;
                        }
                        j++;
                    }
                }

                // Extra parameters if you want to pass to server
                entity.addPart("driver_id", new StringBody(user));
                entity.addPart("land_type", new StringBody(tLand.getText().toString()));
                entity.addPart("land_size", new StringBody(sLand.getText().toString()));
                entity.addPart("farmers_name", new StringBody(fName.getText().toString()));
                entity.addPart("phone_no", new StringBody(pName.getText().toString()));
                entity.addPart("harvest_month", new StringBody(harvest.getText().toString()));
                entity.addPart("latitude", new StringBody(lat));
                entity.addPart("longitude", new StringBody(lon));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        String msg="Successfully Uploaded";
        message=message.substring(0, 6);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(message.equals("SUCCES")){
            builder.setMessage(msg).setTitle("Response from Servers")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(UploadActivity.this, MainPage.class);
                            i.putExtra("internet","yes");
                            i.putExtra("user",user);
                            i.putExtra("res",result);
                            startActivity(i);
                        }
                    });
        }else{
            builder.setMessage(message).setTitle("Response from Servers")
                    .setCancelable(false)
                    .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Camera functions
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                addImag(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                addImag(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void addImag(boolean isImage){
        imags.add(fileUri.getPath());
        previewMediaNew(isImage,fileUri.getPath());
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}