package com.eis.lnp.lnpsolutions;



import
        android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eis.lnp.lnpsolutions.AndroidMultiPartEntity.ProgressListener;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class UploadOffline extends Activity {
    private static final String TAG = UploadOffline.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView txtPercentage;
    long totalSize = 0;
    int curren = 1;
    String result = "LOGIN";
    DatabaseHandler db;
    private Button btnUpload;
    int total;
    List<Tag> tags;
    ProgressDialog dialog;
    String user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.button1);
//        getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor(getResources().getString(
//                        R.color.action_bar))));
        Intent myIntent = getIntent();
        user = myIntent.getStringExtra("user");
        db = new DatabaseHandler(this);
        total = db.getTagsCount();

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnUpload.setEnabled(false);
                btnUpload.setText("PREPARING... PLEASE WAIT");
                txtPercentage.setText("0% " + "\nUploading " + curren + " of " + total);
                progressBar.setVisibility(View.VISIBLE);


                tags = db.getAllTags();
                uploadTag(total - 1);
            }//onclick ends
        });
    }

    public void uploadTag(int index) {
//		try{
        new UploadFileToServer1(tags.get(index), tags.get(0), index).execute();//.get(30000,TimeUnit.MILLISECONDS);
//		}catch(TimeoutException e){

//		}catch(ExecutionException e){

//		}catch(InterruptedException s){

//		};
    }

    public void uploadOfflineTags(View v) {
        tags = db.getAllTags();
        uploadTag(total - 1);

    }

    private class UploadFileToServer1 extends AsyncTask<Void, Integer, String> {
        public Tag tag;
        public Tag last;
        public int index;

        public UploadFileToServer1(Tag tagi, Tag las, int ind) {
            tag = tagi;
            last = las;
            index = ind;
        }

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
            txtPercentage.setText(String.valueOf(progress[0]) + "% " + "\nUploading " + curren + " of " + total);
            //System.out.println(String.valueOf(progress[0]) + "% "+"\nUploading "+curren+" of "+total);
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
                int i = 0;

                if (tag.images.size() > 0) {
                    for (String im : tag.images) {
                        File sourceFil = new File(im);
                        entity.addPart("images[" + String.valueOf(i) + "]", new FileBody(sourceFil));
                        i++;
                    }
                }

                // Extra parameters if you want to pass to server
                //entity.addPart("driver_id", new StringBody(user));
//                entity.addPart("land_type", new StringBody(tag.land_type));
//                entity.addPart("land_size", new StringBody(tag.land_size));
//                entity.addPart("farmers_name", new StringBody(tag.farmer_name));
//                entity.addPart("phone_no", new StringBody(tag.farmer_phone));
//                entity.addPart("harvest_month", new StringBody(tag.harvest));
//                entity.addPart("latitude", new StringBody(tag.lat));
//                entity.addPart("longitude", new StringBody(tag.lon));


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
            curren += 1;
            if (result.equals("SUCCES")) {
                db.deleteTag(tag);
            }
            if (index > 0) {
                uploadTag(index - 1);
            }
            if (tag.id == last.id) {
                //if(dialog.isShowing()){
                //	   dialog.dismiss();
                //}
                // showing the server response in an alert dialog
                showAlert(result);
            }

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        String msg = "Successfully Uploaded";
        message = message.substring(0, 6);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (message.equals("SUCCES")) {
            builder.setMessage(msg).setTitle("Response from Servers")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(UploadOffline.this, MainPage.class);
                            i.putExtra("internet", "yes");
                            i.putExtra("user", user);
                            i.putExtra("res", result);
                            startActivity(i);
                        }
                    });
        } else {
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


    void goBack() {
        Intent intentMain = new Intent(UploadOffline.this, MainPage.class);
        intentMain.putExtra("res", "LOGIN");
        intentMain.putExtra("internet", "yes");
        intentMain.putExtra("user", user);
        //UploadOffline.this.startActivity(intentMain);
    }

}//end of upload class
