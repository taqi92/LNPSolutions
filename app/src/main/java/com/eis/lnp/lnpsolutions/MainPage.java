package com.eis.lnp.lnpsolutions;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eis.lnp.lnpsolutions.ModelRealm.UploadData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainPage extends Activity implements LocationListener {
    //Camera Upload Code
    private static final String TAG = MainPage.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnTag;
    //Camera upload code

    private LocationManager locationManager;
    ProgressDialog dialog;
    String res, user, internet, resu;
    String lat1, lon1;
    int once = 0;
    int off = 0;
    Location loc = null;
    WebView myWebView;
    TextView gpsC;
    String result;
    Context cont;
    DatabaseHandler db;
    List<Tag> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cont = this;
        Intent myIntent = getIntent(); // gets the previously created intent
        this.internet = myIntent.getStringExtra("internet");
        if (this.internet.equals("yes")) {
            if (!isOnline()) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            //Datbase connection and checking for existing offline tags
            db = new DatabaseHandler(this);

            setContentView(R.layout.activity_no_internet_main_page);

            // Changing action bar background color
            /*getActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor(getResources().getString(
                            R.color.action_bar))));*/


            this.dialog = new ProgressDialog(this);
            this.dialog.setMessage("Finding GPS Signal");
            this.dialog.setCancelable(false);
            this.dialog.setInverseBackgroundForced(false);

            String response = myIntent.getStringExtra("res");
            this.res = response;
            this.user = myIntent.getStringExtra("user");

				/*myWebView= (WebView) findViewById(R.id.webview);
                WebSettings webSettings = myWebView.getSettings();
				webSettings.setJavaScriptEnabled(true);
				myWebView.setWebViewClient(new WebViewClient());*/
            gpsC = (TextView) findViewById(R.id.gpsC);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    500,   // half sec
                    1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    550,   // half sec
                    1, this);
            //beginGPS();
        } else {
            if (isOnline()) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            setContentView(R.layout.activity_no_internet_main_page);
//            getActionBar().setBackgroundDrawable(
//                    new ColorDrawable(Color.parseColor(getResources().getString(
//                            R.color.action_bar))));

            this.dialog = new ProgressDialog(this);
            this.dialog.setMessage("Finding GPS Signal");
            this.dialog.setCancelable(false);
            this.dialog.setInverseBackgroundForced(false);

            gpsC = (TextView) findViewById(R.id.gpsC);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    500,   // half sec
                    1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    550,   // half sec
                    1, this);
        }


        this.dialog.show();

        //Tag Location
        btnTag = (Button) findViewById(R.id.btnTag);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Upload Activity Directly
                tagLocUpload();
            }
        });

    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        loc = location;
        lat1 = String.valueOf(location.getLatitude());
        lon1 = String.valueOf(location.getLongitude());
        if (this.internet.equals("yes")) {
            if (!isOnline()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(this);
                locationManager = null;
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            System.out.println("getting here");
            //updateUserLoc(location.getLatitude(), location.getLongitude());
			/*this.dialog.setMessage("Downloading Map");
			if(once==0){
			myWebView.loadUrl("http://lnp.solutions/mobilemap.php?lat="+location.getLatitude()+"&long="+location.getLongitude());
			myWebView.setWebViewClient(new WebViewClient() {

				   public void onPageFinished(WebView view, String url) {
				        // dismiss dialog
					   if(dialog.isShowing()){
						   dialog.dismiss();
						}
					   if(db.getTagsCount()>0){
				        	Intent intentMain = new Intent(MainPage.this ,  UploadOffline.class);
				        	intentMain.putExtra("user",user);
							MainPage.this.startActivity(intentMain);
							finish();
				        }
				    }
				});
			once=1;
			}else{
				myWebView.loadUrl("javascript:upPos("+location.getLatitude()+","+location.getLongitude()+")");
			}*/
            this.gpsC.setText("CURRENT COORDINATES \nLatitude: " + location.getLatitude() + " \nLongitude: " + location.getLongitude());
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (db.getTagsCount() > 0 && off == 0) {
                off = 1;
                locationManager.removeUpdates(this);
//                Intent intentMain = new Intent(MainPage.this, UploadOffline.class);
//                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intentMain.putExtra("user", user);
//                MainPage.this.startActivity(intentMain);
//                finish();
            }
        } else {
            if (isOnline()) {
                locationManager.removeUpdates(this);
                locationManager = null;
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            this.gpsC.setText("CURRENT COORDINATES \nLatitude: " + location.getLatitude() + " \nLongitude: " + location.getLongitude());
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void updateUserLoc(double lat, double lon) {
        Context context = getApplicationContext();
        CharSequence userLogin = "You have been Locket out, please log in again";
        //request code
        lat1 = String.valueOf(lat);
        lon1 = String.valueOf(lon);

        if (this.res.equals("LOGIN")) {
            try {
                this.result = new DownloadWebpageTask().execute("http://lnpsolution.acumenits.com/login/driver"//http://lnp.solutions/respond.php?case=track&user="
                        + this.user
                        + "&lat=" + lat
                        + "&long=" + lon
                        + "&per=pass"
                ).get(10000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {

            } catch (ExecutionException e) {

            } catch (InterruptedException s) {

            }
            //request code ends
            this.res = "noLog";
        } else {
            try {
                this.result = new DownloadWebpageTask().execute("http://lnpsolution.acumenits.com/login/driver" //http://lnp.solutions/respond.php?case=track&user="
                        + this.user
                        + "&lat=" + lat
                        + "&long=" + lon
                ).get(10000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {

            } catch (ExecutionException e) {

            } catch (InterruptedException s) {

            }
            //request code ends
        }
        if (this.result.equals("LOCKO")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(this);
            locationManager = null;
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.login, menu);
//        return true;
//    }

    protected void beginGPS() {
        if (isOnline()) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    500,   // half sec
                    1, this);
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //if (id == R.id.action_settings) {
//            return true;
//        }
//        //return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    private void tagLocUpload(){
        Intent i = new Intent(MainPage.this, UploadActivity.class);
        i.putExtra("user",this.user);
        i.putExtra("lat",lat1);
        i.putExtra("lon",lon1);

        if(this.internet.equals("yes")){
            i.putExtra("internet","yes");
        }else{
            i.putExtra("internet","no");
        }
        startActivity(i);
    }

}
