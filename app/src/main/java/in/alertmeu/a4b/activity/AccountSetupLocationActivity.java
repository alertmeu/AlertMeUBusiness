package in.alertmeu.a4b.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Handler;
import android.os.ResultReceiver;

import android.provider.OpenableColumns;
import android.provider.Settings;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.a4b.FirebaseNotification.SharedPrefManager;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.AppUtils;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.FetchAddressIntentService;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.WebClient;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import static android.graphics.BitmapFactory.decodeFile;

public class AccountSetupLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    String nob = "", mob = "", emailob = "", addressob = "", password = "", mobile_no = "", imageName = "", addBusinessAcountResponse = "", message = "";
    Uri picUri;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private static String TAG = "MAP LOCATION";
    private AccountSetupLocationActivity.AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    TextView latlong;
    EditText edtNameLoca;
    GoogleMap googleMap;
    double latitude = 0.0;
    double longitude = 0.0;
    double save_latitude = 0.0;
    double save_longitude = 0.0;
    private LatLng mCenterLatLong;
    String placeName = "";
    LinearLayout saveData;
    JSONObject jsonLeadObj;
    JSONArray jsonArray;
    ProgressDialog mProgressDialog;
    String deviceId = "0", primary_login = "";
    TelephonyManager telephonyManager;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    public ArrayList<String> map = new ArrayList<String>();
    boolean status;
    int count = 0;
    private ProgressDialog dialog;
    MultipartEntity entity;
    String localTime;
    String provider;
    private LocationManager locationManager;
    String token = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_account_setup_location);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        Intent intent = getIntent();
        password = intent.getStringExtra("password");
        mobile_no = intent.getStringExtra("mobile_no");
        emailob = intent.getStringExtra("emailId");

        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        edtNameLoca = (EditText) findViewById(R.id.edtNameLoca);
        latlong = (TextView) findViewById(R.id.latlong);
        saveData = (LinearLayout) findViewById(R.id.saveData);
        save_latitude = Double.parseDouble(preferences.getString("save_latitude", ""));
        save_longitude = Double.parseDouble(preferences.getString("save_longitude", ""));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(AccountSetupLocationActivity.this);

        mResultReceiver = new AddressResultReceiver(new Handler());
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(AccountSetupLocationActivity.this)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(AccountSetupLocationActivity.this);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(AccountSetupLocationActivity.this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeName = edtNameLoca.getText().toString().trim();
                if (!placeName.equals("")) {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSetupLocationActivity.this);
                        builder.setMessage(res.getString(R.string.jconfloc))
                                .setCancelable(false)
                                .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                                            new addBusinessAccountDetails().execute();

                                        }


                                    }
                                })
                                .setNegativeButton(res.getString(R.string.jno), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();

                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        // alert.setTitle("Publish Advertisement?");
                        alert.show();


                    }
                } else {
                    Toast.makeText(AccountSetupLocationActivity.this, res.getString(R.string.jpsbn), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap Map) {
        Log.d(TAG, "OnMapReady");
        googleMap = Map;

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                // googleMap.clear();

                try {

                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setCostAllowed(true);
                    criteria.setPowerRequirement(Criteria.POWER_LOW);
                    criteria.setAltitudeRequired(false);
                    criteria.setBearingRequired(false);
                    //API level 9 and up
                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                    locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                    provider = locationManager.getBestProvider(criteria, true);
                    Location mLocation = new Location(provider);
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);
                    save_latitude = mCenterLatLong.latitude;
                    save_longitude = mCenterLatLong.longitude;
                    // mLocationMarkerText.setText("Lat : " + String.format("%.06f", mCenterLatLong.latitude) + "," + "Long : " + String.format("%.06f", mCenterLatLong.longitude));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        LatLng latLng;
        latitude = Double.parseDouble(preferences.getString("save_latitude", ""));
        longitude = Double.parseDouble(preferences.getString("save_longitude", ""));
        latLng = new LatLng(latitude, longitude);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        if (ActivityCompat.checkSelfPermission(AccountSetupLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AccountSetupLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                //Toast.makeText(getActivity(), "zoom out", Toast.LENGTH_SHORT).show();

            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Toast.makeText(getApplicationContext(), "zoom in", Toast.LENGTH_SHORT).show();
            }
        });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Clears the previously touched position
                //  googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                save_latitude = latLng.latitude;
                save_longitude = latLng.longitude;
                // Placing a marker on the touched position
                // googleMap.addMarker(markerOptions);
            }
        });


    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(AccountSetupLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AccountSetupLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            @SuppressLint("RestrictedApi")
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {

            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(AccountSetupLocationActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(AccountSetupLocationActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, AccountSetupLocationActivity.this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(AccountSetupLocationActivity.this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        //  Context ctx = (Context) MyPlacesActivity.this;
        startService(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    private void displayAddressOutput() {


        if (mAreaOutput != null) {
            // edtNameLoca.setText(mAreaOutput);

        } else {
            // edtNameLoca.setText("");

        }
        latlong.setText(save_latitude + " " + save_longitude);

    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + googleMap);


        if (ActivityCompat.checkSelfPermission(AccountSetupLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AccountSetupLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (googleMap != null) {

            googleMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            //  latitude = location.getLatitude();
            //  longitude = location.getLongitude();
            latitude = Double.parseDouble(preferences.getString("save_latitude", ""));
            longitude = Double.parseDouble(preferences.getString("save_longitude", ""));
            latLong = new LatLng(latitude, longitude);

            save_latitude = latitude;
            save_longitude = longitude;
            // latLong = new LatLng(location.getLatitude(), location.getLongitude());

            // save_latitude = location.getLatitude();
            // save_longitude = location.getLongitude();
            /*  CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(10f).tilt(70).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            // mLocationMarkerText.setText("Lat : " + String.format("%.06f", latitude) + "," + "Long : " + String.format("%.06f", latitude));
            startIntentService(location);


        } else {
            Toast.makeText(AccountSetupLocationActivity.this,
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private class addBusinessAccountDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AccountSetupLocationActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
            } catch (Exception e) {
                token = "null";
            }

            if (preferences.getString("r_with_ep", "").equals("2")) {
                mobile_no = "NULL";
                primary_login = "2";
            } else {
                emailob = "NULL";
                primary_login = "1";
            }
            jsonLeadObj = new JSONObject() {
                {
                    try {

                        put("email_id", emailob);
                        put("mobile_no", mobile_no);
                        put("business_name", placeName);
                        put("password", password);
                        put("device_id", deviceId);
                        put("fcm_id", token);
                        put("primary_login", primary_login);
                       /* if (!imageName.equals("")) {
                            put("company_logo", Config.URL_AlertMeUImage + "uploads/" + imageName);
                        } else {
                            put("company_logo", "-");
                            put("address", addressob);
                        }*/
                        put("location_name", placeName);
                        put("latitude", save_latitude);
                        put("longitude", save_longitude);
                        put("t_zone", localTime);
                        put("referral_status", preferences.getString("apply_U_referral_status", ""));
                        put("referral_code", preferences.getString("apply_u_referral_code", ""));
                        put("referral_user_id", preferences.getString("referral_user_id", ""));
                        put("r_country_code", preferences.getString("r_country_code", ""));
                        put("country_code", preferences.getString("country_code", ""));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            addBusinessAcountResponse = serviceAccess.SendHttpPost(Config.URL_ADDBUSINESSACCOUNTDEATILS, jsonLeadObj);
            Log.i("resp", "addBusinessAcountResponse" + addBusinessAcountResponse);


            if (addBusinessAcountResponse.compareTo("") != 0) {
                if (isJSONValid(addBusinessAcountResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addBusinessAcountResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        if (status) {

                            if (!jObject.isNull("business_user_id")) {
                                JSONArray ujsonArray = jObject.getJSONArray("business_user_id");
                                for (int i = 0; i < ujsonArray.length(); i++) {
                                    JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                                    prefEditor.putString("business_user_id", UJsonObject.getString("id"));
                                    prefEditor.putString("business_referral_code", UJsonObject.getString("referral_code"));
                                    prefEditor.putString("create_at", UJsonObject.getString("create_at"));
                                    prefEditor.putString("company_logo", UJsonObject.getString("company_logo"));
                                    prefEditor.putString("primary_login", primary_login);
                                    prefEditor.putInt("units_for_area", 20);
                                    prefEditor.putString("save_latitude", "" + save_latitude);
                                    prefEditor.putString("save_longitude", "" + save_longitude);
                                    prefEditor.putString("business_name", placeName);
                                    prefEditor.putString("mobile_no", mobile_no);
                                    prefEditor.putString("email_id", emailob);
                                    prefEditor.putString("blat", "" + save_latitude);
                                    prefEditor.putString("blong", "" + save_longitude);
                                    prefEditor.remove("apply_u_referral_code");
                                    prefEditor.remove("apply_U_referral_status");
                                    prefEditor.remove("referral_user_id");
                                    prefEditor.remove("r_country_code");
                                    prefEditor.commit();
                                }
                            }

                        }
                        jsonArray = new JSONArray(addBusinessAcountResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    // Toast.makeText(AccountSetupLocationActivity.this, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                // Toast.makeText(AccountSetupLocationActivity.this, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            //Toast.makeText(getApplicationContext(), addBusinessAcountResponse, Toast.LENGTH_SHORT).show();

            if (status) {
                prefEditor.putString("duappstatus", "1");
                prefEditor.commit();
                Intent intent = new Intent(AccountSetupLocationActivity.this, BusinessMainCategoryActivity.class);
                startActivity(intent);
                /*if (mArrayUri.size() > 0) {
                    for (int i = 0; i < imagesEncodedList.size(); i++) {
                        map.add(imagesEncodedList.get(i).toString());
                    }
                    new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                } else {


                }*/

            }

        }
    }

    class ImageUploadTask extends AsyncTask<String, Void, String> {

        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(AccountSetupLocationActivity.this, "Uploading",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                Bitmap bitmap = decodeFile(map.get(i));
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                File file = new File(map.get(i));
                int imageRotation = getImageRotation(file);

                if (imageRotation != 0)
                    scaled = getBitmapRotatedByDegree(scaled, imageRotation);
                // HttpClient httpClient = new DefaultHttpClient();
                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();

                entity.addPart("user_id", new StringBody("199"));
                entity.addPart("club_id", new StringBody("10"));
                entity.addPart("club_image", new ByteArrayBody(data, "image/jpeg", params[1]));
                // entity.addPart("club_image", new FileBody(file, "image/jpeg", params[1]));
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                sResponse = EntityUtils.getContentCharSet(response.getEntity());

                System.out.println("sResponse : " + sResponse);
            } catch (Exception e) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Log.e(e.getClass().getName(), e.getMessage(), e);

            }

            return sResponse;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    //  Toast.makeText(getApplicationContext(), sResponse + " Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                    count++;
                   /* if (count < map.size()) {
                        // new ImageUploadTask().execute(count + "", "hm" + count + ".jpg");

                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));

                    } else {
                        Intent intent = new Intent(AccountSetupLocationActivity.this, BusinessExpandableListViewActivity.class);
                        startActivity(intent);

                    }*/
                    Intent intent = new Intent(AccountSetupLocationActivity.this, BusinessMainCategoryActivity.class);
                    startActivity(intent);

                }

            } catch (Exception e) {
                //  Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private static int getImageRotation(final File imageFile) {

        ExifInterface exif = null;
        int exifRotation = 0;

        try {
            exif = new ExifInterface(imageFile.getPath());
            exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;
        else
            return exifToDegrees(exifRotation);
    }

    private static int exifToDegrees(int rotation) {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;

        return 0;
    }

    private static Bitmap getBitmapRotatedByDegree(Bitmap bitmap, int rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();

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
