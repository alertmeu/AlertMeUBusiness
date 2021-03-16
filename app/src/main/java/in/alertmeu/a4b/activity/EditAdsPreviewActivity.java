package in.alertmeu.a4b.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.a4b.Fragments.TabsFragmentActivity;
import in.alertmeu.a4b.R;

import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.AppUtils;
import in.alertmeu.a4b.utils.CompressFile;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.WebClient;

import static android.graphics.BitmapFactory.decodeFile;

public class EditAdsPreviewActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    GoogleMap googleMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    private GoogleApiClient mGoogleApiClient;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    ArrayList<String> subCatArrayList;
    ArrayList<String> mainCatArrayList;
    String title = "", description = "", response = "", balanceAmountResponse = "";
    String tunit = "", tunit1 = "", stime = "", sdate = "", tsign = "", edate = "", etime = "", rq_code = "", main_cat = "", main_cat1 = "", subcatname = "", bar_code = "", n_flag = "0", s_flag = "";
    String addImageLocationResponse = "", message = "", id = "", addRequestAttachResponse = "", describe_limitations = "", fileName = "", bid = "";
    Button edit, pay, save, addMoney;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    ProgressDialog mProgressDialog1;
    JSONObject jsonLeadObj, jsonLeadObj1, syncJsonObject, jsonObjectSync, jsonNotifyObj, jsonLeadObjReq, jsonObj, jsonLeadObjReq1;
    JSONArray jsonArray, jsonArraySync;
    String balance_amount = "", alertSubResponse = "", bc_id = "", sub_id = "", syncDataesponse = "", tamount = "", activeStatus = "";
    boolean status, status2;
    int count = 0;
    int i, flag, notification_status;
    public ArrayList<String> map = new ArrayList<String>();
    ArrayList<String> imagesEncodedList = new ArrayList<String>();
    ArrayList<String> subCatNameArrayList;
    private ProgressDialog dialog;
    MultipartEntity entity;
    ImageView imageView;
    TextView txtTitle, txtDescpritipon, txtValidity, txtCost, mainCat, subCat, txtBalanceAmount, txtPayingAmount, limitation;
    double paidamount;
    String filePath;
    String localTime;
    int thours = 0, clc = 0;
    String h;
    LinearLayout limithideshow, deshideshow;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_edit_ads_preview);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        System.out.println(localTime + "  TimeZone   ");
        imageView = (ImageView) findViewById(R.id.imageView);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescpritipon = (TextView) findViewById(R.id.txtDescpritipon);
        txtValidity = (TextView) findViewById(R.id.txtValidity);
        txtCost = (TextView) findViewById(R.id.txtCost);
        mainCat = (TextView) findViewById(R.id.mainCat);
        subCat = (TextView) findViewById(R.id.subCat);
        txtBalanceAmount = (TextView) findViewById(R.id.txtBalanceAmount);
        txtPayingAmount = (TextView) findViewById(R.id.txtPayingAmount);
        limithideshow = (LinearLayout) findViewById(R.id.limithideshow);
        deshideshow = (LinearLayout) findViewById(R.id.deshideshow);
        limitation = (TextView) findViewById(R.id.limitation);
        subCatArrayList = new ArrayList<>();
        mainCatArrayList = new ArrayList<>();
        subCatNameArrayList = new ArrayList<>();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        subCatArrayList = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        mainCatArrayList = (ArrayList<String>) args.getSerializable("ARRAYLIST1");
        mArrayUri = (ArrayList<Uri>) args.getSerializable("ARRAYLIST2");
        imagesEncodedList = (ArrayList<String>) args.getSerializable("ARRAYLIST3");
        subCatNameArrayList = (ArrayList<String>) args.getSerializable("ARRAYLIST4");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        describe_limitations = intent.getStringExtra("describe_limitations");
        rq_code = intent.getStringExtra("rq_code");
        sdate = intent.getStringExtra("sdate");
        stime = intent.getStringExtra("stime");
        edate = intent.getStringExtra("edate");
        etime = intent.getStringExtra("etime");
        fileName = intent.getStringExtra("fileName");
        tamount = intent.getStringExtra("tamount");
        tunit = intent.getStringExtra("tunit");
        tunit1 = intent.getStringExtra("tunit1");
        tsign = intent.getStringExtra("tsign");
        n_flag = intent.getStringExtra("nflag");
        s_flag = intent.getStringExtra("s_flag");
        bc_id = intent.getStringExtra("bc_id");
        sub_id = intent.getStringExtra("subcatid");
        thours = Integer.parseInt(intent.getStringExtra("thours"));
        balance_amount = intent.getStringExtra("balance_amount");
        bid = preferences.getString("bid", "");
        txtTitle.setText(title);
        if (!description.equals("")) {
            deshideshow.setVisibility(View.VISIBLE);
            txtDescpritipon.setText(description);
        }
        if (!describe_limitations.equals("")) {
            limithideshow.setVisibility(View.VISIBLE);
            limitation.setText(describe_limitations);
        }
        if (tunit.equals("1")) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
            stime = dateformat.format(c.getTime());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            sdate = format.format(cal.getTime());

            Date myDateTime = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //Parse your string to SimpleDateFormat
            try {
                myDateTime = simpleDateFormat.parse(sdate + " " + stime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cal.setTime(myDateTime); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, thours); // adds one hour
            cal.getTime(); // returns new date object, one hour in the future
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
            edate = sdf.format(cal.getTime());
            etime = tdf.format(cal.getTime());

        }
        txtValidity.setText(parseTime(stime, "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", sdate) + " to " + parseTime(etime, "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", edate));
        txtCost.setText(preferences.getString("currency_sign", "") + tamount);
        mainCat.setText(res.getString(R.string.jadcat) + intent.getStringExtra("main_cat"));
        subCat.setText(res.getString(R.string.jadscat) + intent.getStringExtra("subcatname"));
        main_cat = res.getString(R.string.jadcat) + intent.getStringExtra("main_cat");
        main_cat1 = intent.getStringExtra("main_cat");
        subcatname = res.getString(R.string.jadscat) + intent.getStringExtra("subcatname");
        edit = (Button) findViewById(R.id.edit);
        pay = (Button) findViewById(R.id.pay);
        save = (Button) findViewById(R.id.save);
        addMoney = (Button) findViewById(R.id.addMoney);
   /*     try {
            if (mArrayUri != null) {
                filePath = getPathFromUri(EditAdsPreviewActivity.this, mArrayUri.get(0));
                // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + fileName;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap b = BitmapFactory.decodeFile(filePath, options);
                imageView.setImageBitmap(b);
            }
        } catch (Exception e) {
        }
        if (fileName.contains(Config.URL_AlertMeUImage)) {
            //  Picasso.with(EditAdsPreviewActivity.this).load(preferences.getString("image_path", "")).noPlaceholder().into((ImageView) imageView);
            try {
                Picasso.get().load(preferences.getString("image_path", "")).into(imageView);
            } catch (Exception e) {

            }
        }*/

        try {

            filePath = "" + mArrayUri.get(0);
            if (filePath.contains("http")) {
                filePath = "" + mArrayUri.get(0);
                //  Picasso.with(getActivity()).load(preferences.getString("image_path", "")).noPlaceholder().into((ImageView) imageView);
                try {
                    Picasso.get().load(filePath).into(imageView);
                } catch (Exception e) {

                }
            } else {
                filePath = getPathFromUri(EditAdsPreviewActivity.this, mArrayUri.get(0));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap b = BitmapFactory.decodeFile(filePath, options);
                File myFile = new File(filePath);
                int imageRotation = getImageRotation(myFile);
                if (imageRotation != 0)
                    b = getBitmapRotatedByDegree(b, imageRotation);
                imageView.setImageBitmap(b);
            }
        } catch (Exception e) {
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            if (!preferences.getString("previous_amount", "").equals("0")) {
                Double temp = Double.parseDouble(preferences.getString("previous_amount", ""));
                balance_amount = "" + (Double.parseDouble(balance_amount) + temp);
                String a = balance_amount + "(Previous paid amount is=" + preferences.getString("previous_amount", "") + ")";
                // balance_amount+=Integer.parseInt(preferences.getString("previous_amount",""));
                txtBalanceAmount.setText(preferences.getString("currency_sign", "") + a);
            } else {
                txtBalanceAmount.setText(preferences.getString("currency_sign", "") + balance_amount);
            }
            if (Double.parseDouble(balance_amount) > 0.0) {
                paidamount = (Double.parseDouble(balance_amount) - Double.parseDouble(tamount));
                if (paidamount >= 0.0) {

                    pay.setVisibility(View.VISIBLE);
                    addMoney.setVisibility(View.GONE);
                    txtPayingAmount.setText(preferences.getString("currency_sign", "") + "" + balance_amount + "-" + preferences.getString("currency_sign", "") + tamount + "=" + preferences.getString("currency_sign", "") + (Double.parseDouble(balance_amount) - Double.parseDouble(tamount)));
                } else {

                    txtPayingAmount.setText(preferences.getString("currency_sign", "") + "" + balance_amount + "-" + preferences.getString("currency_sign", "") + tamount + "=" + preferences.getString("currency_sign", "") + (Double.parseDouble(balance_amount) - Double.parseDouble(tamount)));
                    pay.setVisibility(View.GONE);
                    addMoney.setVisibility(View.VISIBLE);
                }

            } else {
                pay.setVisibility(View.GONE);
                addMoney.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    if (tunit.equals("1")) {
                        activeStatus = "1";
                        flag = 0;
                        notification_status = 1;
                    } else {
                        activeStatus = "2";
                        notification_status = 0;
                        flag = 1;
                    }
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm am");
                    final String dateToStr = format.format(today);

                    DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
                    DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = null;
                    try {
                        date = readFormat.parse(dateToStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String formattedDate = "";
                    if (date != null) {
                        formattedDate = writeFormat.format(date);
                    }

                    System.out.println(formattedDate);


                    Date d1 = null;
                    Date d2 = null;

                    try {
                        d1 = writeFormat.parse(formattedDate);
                        d2 = writeFormat.parse(sdate + " " + stime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (d1.compareTo(d2) > 0 && !tunit.equals("1")) {
                        Toast.makeText(EditAdsPreviewActivity.this, res.getString(R.string.jfads), Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAdsPreviewActivity.this);
                        builder.setMessage(res.getString(R.string.jpads))
                                .setCancelable(false)
                                .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Date today = new Date();
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                        final String dateToStr = format.format(today);
                                        Date d1 = null;
                                        Date d2 = null;

                                        try {
                                            d1 = format.parse(dateToStr);
                                            d2 = format.parse(sdate + " " + stime);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (d1.compareTo(d2) == 0 && !tunit.equals("1")) {
                                            activeStatus = "1";
                                           /* Calendar c = Calendar.getInstance();
                                            SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
                                            stime = dateformat.format(c.getTime());

                                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                            Calendar cal = Calendar.getInstance();
                                            sdate = format1.format(cal.getTime());*/
                                        }
                                        if (clc == 0) {
                                            new addLocationDataDetails().execute();
                                            clc++;
                                        }

                                    }
                                })
                                .setNegativeButton(res.getString(R.string.helpCan), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        //    alert.setTitle("");
                        alert.show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    activeStatus = "3";
                    notification_status = 0;
                    flag = 1;
                    tamount = "0";
                    if (clc == 0) {
                        new addLocationDataDetails().execute();
                        clc++;
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(EditAdsPreviewActivity.this)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditAdsPreviewActivity.this);
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
            Toast.makeText(EditAdsPreviewActivity.this, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }


    /*    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new dueFeesAvailable().execute();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }*/

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditAdsPreviewActivity.this, MyAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Add a marker in loc and move the camera
        LatLng loc = new LatLng(Double.parseDouble(preferences.getString("save_latitude", "")), Double.parseDouble(preferences.getString("save_longitude", "")));
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
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        googleMap.addMarker(new MarkerOptions().position(loc).title(preferences.getString("business_name", "")));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(EditAdsPreviewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditAdsPreviewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mGoogleApiClient = new GoogleApiClient.Builder(EditAdsPreviewActivity.this)
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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(EditAdsPreviewActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, EditAdsPreviewActivity.this,
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
    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + googleMap);


        if (ActivityCompat.checkSelfPermission(EditAdsPreviewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditAdsPreviewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            LatLng latLong = new LatLng(Double.parseDouble(preferences.getString("save_latitude", "")), Double.parseDouble(preferences.getString("save_longitude", "")));
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            // mLocationMarkerText.setText("Lat : " + String.format("%.06f", latitude) + "," + "Long : " + String.format("%.06f", latitude));


        } else {
            Toast.makeText(EditAdsPreviewActivity.this,
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    private class addLocationDataDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(EditAdsPreviewActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.juload));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            final String dateToStr = format.format(today);

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                        put("bid", bid);
                        put("description", description);
                        put("title", title);
                        put("flag", "advertisement");
                        put("describe_limitations", describe_limitations);
                        put("rq_code", rq_code);
                        put("sdate", sdate);
                        put("stime", stime);
                        put("edate", edate);
                        put("etime", etime);
                        put("status", "2");
                        put("active_status", activeStatus);
                        put("paid_amount", tamount);
                        put("t_zone", localTime);
                        put("tunit", tunit1);
                        put("tsign", tsign);
                        put("create_at", dateToStr);
                        put("notifiy_flag", n_flag);
                        put("sponsor_flag", s_flag);
                        put("business_main_category_id", bc_id);
                        put("business_subcategory_id", sub_id);
                        put("distance_for_users", preferences.getInt("units_for_area", 0));
                        put("notification_status", notification_status);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };


            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            if (preferences.getString("modifyflag", "").equals("0")) {
                addImageLocationResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEBUSINESSDATA, jsonLeadObj);
            } else {
                addImageLocationResponse = serviceAccess.SendHttpPost(Config.URL_ADDBUSINESSDATA, jsonLeadObj);
            }
            Log.i("resp", "addRequestChangeResponse" + addImageLocationResponse);


            if (addImageLocationResponse.compareTo("") != 0) {
                if (isJSONValid(addImageLocationResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addImageLocationResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        id = jObject.getString("id");
                        bar_code = jObject.getString("bar_code");
                        if (preferences.getString("modifyflag", "").equals("1")) {
                            bid = id;
                            if (mArrayUri == null) {
                                jsonLeadObjReq1 = new JSONObject() {
                                    {
                                        try {
                                            put("image_path", fileName);
                                            put("business_id", bid);
                                            put("image_status", "0");


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("json exception", "json exception" + e);
                                        }
                                    }
                                };


                                addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_ADDREQUESTATTACHMENT, jsonLeadObjReq1);
                                Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
                            }
                        }

                        if (status) {
                            prefEditor.putString("editflag", "");
                            prefEditor.commit();
                            jsonArraySync = new JSONArray();
                            for (i = 0; i < subCatArrayList.size(); i++) {
                                jsonObjectSync = new JSONObject();
                                try {
                                    jsonObjectSync.put("business_user_id", preferences.getString("business_user_id", ""));
                                    jsonObjectSync.put("business_main_category_id", bc_id);
                                    jsonObjectSync.put("business_subcategory_id", sub_id);
                                    jsonObjectSync.put("business_subcategory_name", subCatNameArrayList.get(i));
                                    jsonObjectSync.put("business_main_category_name", main_cat1);
                                    jsonObjectSync.put("business_id", bid);
                                    jsonArraySync.put(jsonObjectSync);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            jsonNotifyObj = new JSONObject() {
                                {
                                    try {

                                        put("description", description);
                                        put("title", title);
                                        put("business_user_id", preferences.getString("business_user_id", ""));
                                        put("business_main_category_id", bc_id);
                                        put("business_subcategory_id", sub_id);
                                        put("business_id", id);
                                        put("units_for_area", preferences.getInt("units_for_area", 0));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("json exception", "json exception" + e);
                                    }
                                }
                            };
                            if (activeStatus.equals("1") && n_flag.equals("1")) {
                                response = serviceAccess.SendHttpPost(Config.URL_SENDBUSINESSTOUSERNOTIFICATION, jsonNotifyObj);
                                Log.i("json", "response" + response);
                            }
                            try {
                                syncJsonObject = new JSONObject();
                                syncJsonObject.put("shopperData", jsonArraySync);
                                Log.d("shopperData", "" + syncJsonObject);


                                Thread objectThread = new Thread(new Runnable() {
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        final WebClient serviceAccess = new WebClient();

                                        //   syncDataesponse = serviceAccess.SendHttpPost(Config.URL_SHOPPERCATSYNCDATA, syncJsonObject);
                                        Log.i("response", "response" + response);
                                        Log.i("resp", "syncDataesponse" + syncDataesponse);
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                handler.post(new Runnable() { // This thread runs in the UI
                                                    @Override
                                                    public void run() {
                                                        if (syncDataesponse.compareTo("") == 0) {

                                                        } else {

                                                            try {
                                                                JSONObject jObject = new JSONObject(syncDataesponse);
                                                                status = jObject.getBoolean("status");
                                                                if (status) {

                                                                    Toast.makeText(EditAdsPreviewActivity.this, res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                // TODO Auto-generated catch block
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        };

                                        new Thread(runnable).start();
                                    }
                                });
                                objectThread.start();


                            } catch (JSONException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                        jsonArray = new JSONArray(addImageLocationResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            //   mProgressDialog.dismiss();
            if (status) {
                //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                if (!filePath.contains("http")) {
                    if (mArrayUri != null) {
                        for (int i = 0; i < imagesEncodedList.size(); i++) {
                            map.add(imagesEncodedList.get(i).toString());
                        }
                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();
                        if (!activeStatus.equals("1")) {
                            Intent intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                            intent.putExtra("active", flag);
                            startActivity(intent);
                        } else {

                            Intent intent = new Intent(getApplicationContext(), AdvertisementPostedActivity.class);
                            intent.putExtra("filePath", fileName);
                            intent.putExtra("title", title);
                            intent.putExtra("description", description);
                            intent.putExtra("describe_limitations", describe_limitations);
                            intent.putExtra("rq_code", bar_code);
                            intent.putExtra("sdate", sdate);
                            intent.putExtra("stime", stime);
                            intent.putExtra("edate", edate);
                            intent.putExtra("etime", etime);
                            intent.putExtra("main_cat", main_cat);
                            intent.putExtra("subcatname", subcatname);
                            startActivity(intent);

                        }
                        finish();
                    }
                } else {
                    uploadDataOnly();
                }

            } else {
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();


            }

        }
    }

    private void uploadDataOnly() {
        jsonLeadObjReq = new JSONObject() {
            {
                try {

                    put("image_path", fileName);
                    put("business_id", bid);
                    put("image_status", 0);


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
                Log.i("json", "json" + jsonLeadObjReq);
                addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_ADDREQUESTATTACHMENT, jsonLeadObjReq);
                Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isJSONValid(addRequestAttachResponse)) {
                            try {
                                JSONObject jObject = new JSONObject(addRequestAttachResponse);
                                status = jObject.getBoolean("status");
                                if (status) {
                                    if (!activeStatus.equals("1")) {
                                        Intent intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                                        intent.putExtra("active", flag);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), AdvertisementPostedActivity.class);
                                        intent.putExtra("filePath", filePath);
                                        intent.putExtra("title", title);
                                        intent.putExtra("description", description);
                                        intent.putExtra("describe_limitations", describe_limitations);
                                        intent.putExtra("rq_code", bar_code);
                                        intent.putExtra("sdate", sdate);
                                        intent.putExtra("stime", stime);
                                        intent.putExtra("edate", edate);
                                        intent.putExtra("etime", etime);
                                        intent.putExtra("main_cat", main_cat);
                                        intent.putExtra("subcatname", subcatname);
                                        startActivity(intent);

                                    }
                                    finish();
                                }
                                jsonArray = new JSONArray(addRequestAttachResponse);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        } else {

                            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                        }

                    }
                });


            }
        });

        objectThread.start();
    }


    class ImageUploadTask extends AsyncTask<String, Void, String> {
        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(EditAdsPreviewActivity.this, "", res.getString(R.string.jpw), true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                File file = new File(map.get(i));
                File f = CompressFile.getCompressedImageFile(file, EditAdsPreviewActivity.this);
                //  Bitmap bitmap = decodeFile(map.get(i));
                Bitmap bitmap = decodeFile(f.getAbsolutePath());
               /* Bitmap bitmap = decodeFile(map.get(i));
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                //  bitmap = Bitmap.createScaledBitmap(bitmap, 500, 300, true);
                File file = new File(map.get(i));
                int imageRotation = getImageRotation(file);

                if (imageRotation != 0)
                    scaled = getBitmapRotatedByDegree(scaled, imageRotation);*/
                // HttpClient httpClient = new DefaultHttpClient();
                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                //  scaled.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();
                if (preferences.getInt("ifg", 0) == 1) {
                    fileName = "A_" + preferences.getString("business_user_id", "") + "_cam_" + bar_code + "_" + System.currentTimeMillis() + ".png";
                }
                if (preferences.getInt("ifg", 0) == 2) {
                    fileName = "A_" + preferences.getString("business_user_id", "") + "_lib_" + bar_code + "_" + System.currentTimeMillis() + ".png";
                }
                entity.addPart("user_id", new StringBody("199"));
                entity.addPart("fileName", new StringBody(fileName));
                entity.addPart("club_image", new ByteArrayBody(data, "image/*", params[1]));
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
            String temp = getFileName(mArrayUri.get(count));
            final String image_status;
            if (temp.contains("Image-")) {
                image_status = "1";
            } else {
                image_status = "0";
            }
            jsonLeadObjReq = new JSONObject() {
                {
                    try {

                        put("image_path", Config.URL_AlertMeUImage + "uploads/" + fileName);
                        put("business_id", bid);
                        put("image_status", image_status);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObjReq);
            if (preferences.getString("modifyflag", "").equals("1")) {
                addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_ADDREQUESTATTACHMENT, jsonLeadObjReq);
                Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
            } else {
                addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEREQUESTATTACHMENT, jsonLeadObjReq);
                Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
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
                    if (count < map.size()) {
                        // new ImageUploadTask().execute(count + "", "hm" + count + ".jpg");

                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));

                    } else {


                        Toast.makeText(getApplicationContext(), res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();

                        if (!activeStatus.equals("1")) {
                            Intent intent = new Intent(getApplicationContext(), TabsFragmentActivity.class);
                            intent.putExtra("active", flag);
                            startActivity(intent);
                        } else {

                            Intent intent = new Intent(getApplicationContext(), AdvertisementPostedActivity.class);
                            intent.putExtra("filePath", filePath);
                            intent.putExtra("description", description);
                            intent.putExtra("rq_code", bar_code);
                            intent.putExtra("sdate", sdate);
                            intent.putExtra("stime", stime);
                            intent.putExtra("edate", edate);
                            intent.putExtra("etime", etime);
                            intent.putExtra("main_cat", main_cat);
                            intent.putExtra("subcatname", subcatname);
                            startActivity(intent);

                        }
                        finish();
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

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

    protected boolean isJSONValid(String addImageLocationResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(addImageLocationResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(addImageLocationResponse);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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

    private class dueFeesAvailable extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog1 = new ProgressDialog(EditAdsPreviewActivity.this);
            // Set progressdialog title
            mProgressDialog1.setTitle(res.getString(R.string.jpw));
            mProgressDialog1.setIndeterminate(true);
            mProgressDialog1.setCancelable(false);
            mProgressDialog1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonObj = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            balanceAmountResponse = serviceAccess.SendHttpPost(Config.URL_GETBALANCEAMOUNT, jsonObj);
            Log.i("resp", "balanceAmountResponse" + balanceAmountResponse);


            if (balanceAmountResponse.compareTo("") != 0) {
                if (isJSONValid(balanceAmountResponse)) {
                    // JSONArray leadJsonObj = null;
                    try {
                        JSONObject jObject = new JSONObject(balanceAmountResponse);
                        status = jObject.getBoolean("status");
                        if (status) {
                            /*JSONArray introJsonArray = jObject.getJSONArray("balanceamount");
                            for (int i = 0; i < introJsonArray.length(); i++) {
                                JSONObject introJsonObject = introJsonArray.getJSONObject(i);
                                balance_amount = introJsonObject.getString("balance_amount");
                            }*/
                            balance_amount = jObject.getString("balanceamount");

                        } else {
                            balance_amount = "0";
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog1.dismiss();
            try {
                if (!preferences.getString("previous_amount", "").equals("0")) {
                    Double temp = Double.parseDouble(preferences.getString("previous_amount", ""));
                    balance_amount = "" + (Double.parseDouble(balance_amount) + temp);
                    String a = balance_amount + "(Previous paid amount is=" + preferences.getString("previous_amount", "") + ")";
                    // balance_amount+=Integer.parseInt(preferences.getString("previous_amount",""));
                    txtBalanceAmount.setText(preferences.getString("currency_sign", "") + a);
                } else {
                    txtBalanceAmount.setText(preferences.getString("currency_sign", "") + balance_amount);
                }
                if (Double.parseDouble(balance_amount) > 0.0) {
                    paidamount = (Double.parseDouble(balance_amount) - Double.parseDouble(tamount));
                    if (paidamount >= 0.0) {

                        pay.setVisibility(View.VISIBLE);
                        addMoney.setVisibility(View.GONE);
                        txtPayingAmount.setText(preferences.getString("currency_sign", "") + "" + balance_amount + "-" + preferences.getString("currency_sign", "") + tamount + "=" + preferences.getString("currency_sign", "") + (Double.parseDouble(balance_amount) - Double.parseDouble(tamount)));
                    } else {

                        txtPayingAmount.setText(preferences.getString("currency_sign", "") + "" + balance_amount + "-" + preferences.getString("currency_sign", "") + tamount + "=" + preferences.getString("currency_sign", "") + (Double.parseDouble(balance_amount) - Double.parseDouble(tamount)));
                        pay.setVisibility(View.GONE);
                        addMoney.setVisibility(View.VISIBLE);
                    }

                } else {
                    pay.setVisibility(View.GONE);
                    addMoney.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
            }
        }
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

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

    public static String parseTime(String time, String inFormat, String outFormat) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat(outFormat).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
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

