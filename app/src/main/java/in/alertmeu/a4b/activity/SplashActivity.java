package in.alertmeu.a4b.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.GPSTracker;
import in.alertmeu.a4b.utils.WebClient;

public class SplashActivity extends AppCompatActivity {
    String msg = "Android : ";
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    // GPSTracker class
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    static final Integer ACCESS_FINE_LOCATION = 0x14;
    static final Integer ACCESS_COARSE_LOCATION = 0x15;
    private JSONObject jsonSchedule;
    boolean status;

    String imagePathResponse = "";
    String app_status = "";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_splash);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        for (int i = 0; i <= 30; i++) {
            gps = new GPSTracker(SplashActivity.this);
        }
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.i("TrackResponse", "" + latitude);
            Log.i("TrackResponse", "" + longitude);
            if (latitude != 0.0 && longitude != 0.0) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    getImagePath();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), res.getString(R.string.jptag), Toast.LENGTH_LONG).show();
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
                askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION);
            }

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // Do something for lollipop and above versions
                displayLocationSettingsRequest(SplashActivity.this);
            } else {
                // do something for phones running an SDK before lollipop
                gps.showSettingsAlert();

            }
        }

    }

    /**
     * Called when the activity is about to become visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(msg, "The onStart() event");
        init();


    }

    /**
     * Called when the activity has become visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(msg, "The onResume() event");
        init();


    }

    /**
     * Called when another activity is taking focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(msg, "The onPause() event");

    }

    /**
     * Called when the activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(msg, "The onStop() event");

    }

    /**
     * Called just before the activity is destroyed.
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(msg, "The onDestroy() event");

    }

    public void init() {
        if (!preferences.getString("business_user_id", "").equals("")) {
            Intent intent = new Intent(SplashActivity.this, HomePageActivity.class);
            startActivity(intent);
            //Remove activity
            finish();
        } else {

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            //Remove activity
            finish();
        }

    }

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("status", 2);
                    put("latitude", latitude);
                    put("longitude", longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("json exception", "json exception" + e);
                }
            }
        };


        Thread objectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                Log.i("json", "json" + jsonSchedule);
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_GETALLSLIDEIMAGES, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(imagePathResponse);
                            status = jsonObject.getBoolean("status");
                            app_status = jsonObject.getString("app_status");
                            if (status) {
                                if (Integer.parseInt(app_status) > 0) {

                                } else {
                                    prefEditor.remove("business_user_id");
                                    prefEditor.remove("business_referral_code");
                                    prefEditor.commit();

                                }
                                // init();
                            } else {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });


            }
        });

        objectThread.start();

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    public void displayLocationSettingsRequest(final Context context) {
// final int REQUEST_CHECK_SETTINGS = 0x1;
        final String TAG = "MainActivity";
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
// Show the dialog by calling startResolutionForResult(), and check the result
// in onActivityResult().
                            status.startResolutionForResult((SplashActivity) context, REQUEST_CHECK_SETTINGS);

// getActivity().startActivityForResult((MainActivity)getActivity(),REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }
}



