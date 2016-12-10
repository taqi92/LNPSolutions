package com.eis.lnp.lnpsolutions;

import java.security.KeyStore;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.eis.lnp.lnpsolutions.DownloadWebpageTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity {
    String result = "empty";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isOnline()) {/*Check if Internet connection is established*/
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.activity_no_internet);
        }


        this.dialog = new ProgressDialog(this);
        this.dialog.setMessage("Logging in...");
        this.dialog.setCancelable(false);
        this.dialog.setInverseBackgroundForced(false);
    }

//    public void login(View v){
//        EditText userName=(EditText)findViewById(R.id.user_user);
//        EditText userPass=(EditText)findViewById(R.id.user_pass);
//
//        Context context = getApplicationContext();
//        CharSequence userText = "Please Enter User name";
//        CharSequence userPassword = "Please Enter Password";
//        int duration = Toast.LENGTH_SHORT;
//
//        if(isOnline()){
//            if(!userName.getText().toString().equals("")){//if user name is empty
//                if(!userPass.getText().toString().equals("")){//if password is empty
//                    //send login request
//                    View view = this.getCurrentFocus();
//                    if (view != null) {
//                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }
//                    this.dialog=new ProgressDialog(this);
//                    this.dialog.setMessage("Logging in...");
//                    this.dialog.setCancelable(false);
//                    this.dialog.setInverseBackgroundForced(false);
//                    this.dialog.show();
//                    //urlReq();
//                    this.dialog.dismiss();
//                    handleReq();
//
//                }else{Toast.makeText(context, userPassword, duration).show();}
//            }else{Toast.makeText(context, userText, duration).show();}
//        }else{//reload same activity if no internet connection
//            Intent intentMain = new Intent(LoginActivity.this ,LoginActivity.class);
//            LoginActivity.this.startActivity(intentMain);
//            Log.i("Content "," No Internet layout ");
//        }
//
//        AsyncHttpClient client=new AsyncHttpClient();
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            client.setSSLSocketFactory(sf);
//        }
//        catch (Exception e) {}
//        final RequestParams requestParams=new RequestParams();
//        requestParams.put("email",userName.getText().toString());
//        requestParams.put("password",userPass.getText().toString());
//
//        client.post(getApplicationContext(), "http://lnpsolution.acumenits.com/login/driver",requestParams,new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                try {
//                    System.out.println("2. "+response.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        });
//
//    }
//
//    public void handleReq(){
//        Context context = getApplicationContext();
//        EditText userName=(EditText)findViewById(R.id.user_user);
//        CharSequence userLogin = "Logged in";
//        CharSequence userInvalid = "Invalid user name/ password";
//        CharSequence userError = "Error";
//
//        if(this.result.equals("INVAL")){
//            Toast.makeText(context, userInvalid, Toast.LENGTH_LONG).show();
//        }else if(this.result.equals("LOGIN")){
//            Toast.makeText(context, userLogin, Toast.LENGTH_LONG).show();
//            Intent intentMain = new Intent(LoginActivity.this ,  MainPage.class);
//            intentMain.putExtra("res",this.result);
//            intentMain.putExtra("internet","yes");
//            intentMain.putExtra("user",userName.getText().toString());
//            LoginActivity.this.startActivity(intentMain);
//            finish();
//        }else{
//            Toast.makeText(context, userError, Toast.LENGTH_LONG).show();
//        }
//
//    }




    public void login(View v) {


        EditText userName = (EditText) findViewById(R.id.user_user);
        EditText userPass = (EditText) findViewById(R.id.user_pass);

        Context context = getApplicationContext();
        CharSequence userText = "Please Enter User name";
        CharSequence userPassword = "Please Enter Password";
        int duration = Toast.LENGTH_SHORT;

        /*if(this.result.equals("LOGIN")){
            Intent intentMain = new Intent(LoginActivity.this ,MainPage.class);
            intentMain.putExtra("res",this.result);
            intentMain.putExtra("internet","yes");
            intentMain.putExtra("user",userName.getText().toString());
            LoginActivity.this.startActivity(intentMain);
            finish();
        }*/




        AsyncHttpClient client = new AsyncHttpClient();
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        } catch (Exception e) {
        }
        final RequestParams requestParams = new RequestParams();
        requestParams.put("email", userName.getText().toString());
        requestParams.put("password", userPass.getText().toString());






        client.post(getApplicationContext(), "http://lnpsolution.acumenits.com/login/driver", requestParams, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.println("1. " + response.toString());
                    if (response.getString("status").equals("true")) {
                        Toast.makeText(LoginActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                        Intent intentMain = new Intent(LoginActivity.this ,MainPage.class);
                        //intentMain.putExtra("res",this.result);
                        intentMain.putExtra("internet","yes");
                        //intentMain.putExtra("user",userName.getText().toString());
                        LoginActivity.this.startActivity(intentMain);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User Name Or Password Invalid", Toast.LENGTH_SHORT).show();
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

        if(isOnline()){
            if(!userName.getText().toString().equals("")){//if user name is empty
                if(!userPass.getText().toString().equals("")){//if password is empty
                    //send login request
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    this.dialog=new ProgressDialog(this);
                    this.dialog.setMessage("Logging in...");
                    this.dialog.setCancelable(false);
                    this.dialog.setInverseBackgroundForced(false);
                    this.dialog.show();
                    //urlReq();
                    this.dialog.dismiss();
                    //handleReq();

                }else{Toast.makeText(context, userPassword, duration).show();}
            }else{Toast.makeText(context, userText, duration).show();}
        }else{//reload same activity if no internet connection
            Intent intentMain = new Intent(LoginActivity.this ,LoginActivity.class);
            LoginActivity.this.startActivity(intentMain);
            Log.i("Content "," No Internet layout ");
        }
    }


    public void noInt(View v) {
        Intent intentMain = new Intent(LoginActivity.this,
                MainPage.class);
        intentMain.putExtra("internet", "no");
        LoginActivity.this.startActivity(intentMain);
    }

    /*Function to check if device is connected to internet*/
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void serverRes(String res) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
