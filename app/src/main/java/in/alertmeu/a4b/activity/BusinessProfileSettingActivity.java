package in.alertmeu.a4b.activity;

import android.Manifest;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.alertmeu.a4b.R;

import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.CompressFile;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.Utility;
import in.alertmeu.a4b.utils.WebClient;

import static android.graphics.BitmapFactory.decodeFile;

public class BusinessProfileSettingActivity extends AppCompatActivity {
    static SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    LinearLayout inviteMsg, btnHelp, btnshopPrec, myPlaces, myAccount, myProfile, langu, inviteShareApp, hideshowr, btnPriceList;
    TextView userName, userMobile, usermailid, referralCode;
    private JSONObject jsonLeadObj, jsonSchedule;
    String businessUserResponse = "";
    ProgressDialog mProgressDialog;
    String businessName = "", businessMobile = "", businessEmail = "", businessImage = "";
    ImageView profilePic, ugtop;
//upload

    ProgressDialog dialog;
    MultipartEntity entity;
    JSONObject jsonLeadObjReq;
    //photo
    String userChoosenTask = "";
    int PICK_IMAGE_MULTIPLE = 1;
    private int REQUEST_CAMERA = 0;
    private Uri fileUri; // file url to store image/video
    //new camera images
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = BusinessProfileSettingActivity.class.getSimpleName();
    static String fileName = "";
    static File destination;
    String imageEncoded;
    static List<String> imagesEncodedList;
    // Declare variables
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    String message = "", id = "", addRequestAttachResponse = "", deleteLogoResponse = "", tempimageName = "", imagePathResponse = "";
    int count = 0;
    public ArrayList<String> map = new ArrayList<String>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    int cf = 0;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    String torcount = "", avgRateResponse = "", avgrating = "";
    TextView torcountr;
    RatingBar avgratingStar;
    boolean status1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_business_profile_setting);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        btnHelp = (LinearLayout) findViewById(R.id.btnHelp);
        btnshopPrec = (LinearLayout) findViewById(R.id.btnshopPrec);
        userName = (TextView) findViewById(R.id.userName);
        userMobile = (TextView) findViewById(R.id.userMobile);
        usermailid = (TextView) findViewById(R.id.usermailid);
        referralCode = (TextView) findViewById(R.id.referralCode);
        profilePic = (ImageView) findViewById(R.id.profilePic);
        ugtop = (ImageView) findViewById(R.id.ugtop);
        myPlaces = (LinearLayout) findViewById(R.id.myPlaces);
        myProfile = (LinearLayout) findViewById(R.id.myProfile);
        inviteMsg = (LinearLayout) findViewById(R.id.inviteMsg);
        hideshowr = (LinearLayout) findViewById(R.id.hideshowr);
        langu = (LinearLayout) findViewById(R.id.langu);
        btnPriceList = (LinearLayout) findViewById(R.id.btnPriceList);
        myAccount = (LinearLayout) findViewById(R.id.myAccount);
        inviteShareApp = (LinearLayout) findViewById(R.id.inviteShareApp);
        avgratingStar = (RatingBar) findViewById(R.id.avgratingStar);
        torcountr = (TextView) findViewById(R.id.torcountr);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessProfileSettingActivity.this, HelpCenterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnshopPrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessProfileSettingActivity.this, BusinessMainCategoryActivity.class);
                startActivity(intent);
            }
        });
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

            //new getData().execute();
            getImagePath();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        langu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BusinessProfileSettingActivity.this, LSelectActivity.class);
                startActivity(i);

            }
        });
        myPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(BusinessProfileSettingActivity.this, EditAccountSetupLocationActivity.class);
                    startActivity(intent);
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(BusinessProfileSettingActivity.this, EditAccountSetupActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    if (checkAndRequestPermissions()) {
                        selectImage();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessProfileSettingActivity.this, MyAccountActivity.class);
                startActivity(intent);
            }
        });
        inviteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BusinessProfileSettingActivity.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.custom_inviteapp_alertdialog, null);
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setCancelable(true);
                final AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                Button UserApps = (Button) dialog.findViewById(R.id.btnUserApp);
                UserApps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.u_share_logo);
                        String path = getExternalCacheDir() + "/shareimage.jpg";
                        java.io.OutputStream out = null;
                        java.io.File file = new java.io.File(path);
                        try {
                            out = new java.io.FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        path = file.getPath();
                        Uri bmpUri = Uri.parse("file://" + path);

                        Intent shareIntent = new Intent();
                        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, res.getString(R.string.jshareto) + "\nhttps://play.google.com/store/apps/details?id=in.alertmeu" + " \nReferral Code:" + preferences.getString("business_referral_code", ""));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/jpg");
                        startActivity(Intent.createChooser(shareIntent, res.getString(R.string.jswith)));
                        insertUserClick();
                    }
                });

                Button btnBusinessApp = (Button) dialog.findViewById(R.id.btnBusinessApp);
                btnBusinessApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b_share_logo);
                        String path = getExternalCacheDir() + "/shareimage.jpg";
                        java.io.OutputStream out = null;
                        java.io.File file = new java.io.File(path);
                        try {
                            out = new java.io.FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        path = file.getPath();
                        Uri bmpUri = Uri.parse("file://" + path);

                        Intent shareIntent = new Intent();
                        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, res.getString(R.string.jsharetob) + "\nhttps://play.google.com/store/apps/details?id=in.alertmeu.a4b" + " \nReferral Code:" + preferences.getString("business_referral_code", ""));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/jpg");
                        startActivity(Intent.createChooser(shareIntent, res.getString(R.string.jsbwith)));
                        insertBusinessClick();
                    }
                });
                dialog.show();

            }
        });
        torcountr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessProfileSettingActivity.this, ShowAllReviewsActivity.class);
                startActivity(intent);
            }
        });
        inviteShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BusinessProfileSettingActivity.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.custom_inviteapp_alertdialog, null);
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setCancelable(true);
                final AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                Button UserApps = (Button) dialog.findViewById(R.id.btnUserApp);
                UserApps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String t = res.getString(R.string.jshareto) + "\nhttps://play.google.com/store/apps/details?id=in.alertmeu" + " \nReferral Code:" + preferences.getString("business_referral_code", "");
                        Intent intent = new Intent(BusinessProfileSettingActivity.this, ShareAppBNUActivity.class);
                        intent.putExtra("shareappm", t);
                        intent.putExtra("aptysc", "AlertMeU");
                        startActivity(intent);
                    }
                });
                Button btnBusinessApp = (Button) dialog.findViewById(R.id.btnBusinessApp);
                btnBusinessApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String t = res.getString(R.string.jsharetob) + "\nhttps://play.google.com/store/apps/details?id=in.alertmeu.a4b" + " \nReferral Code:" + preferences.getString("business_referral_code", "");
                        Intent intent = new Intent(BusinessProfileSettingActivity.this, ShareAppBNUActivity.class);
                        intent.putExtra("shareappm", t);
                        intent.putExtra("aptysc", "AlertMeU Business");
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });
        btnPriceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessProfileSettingActivity.this, ShowAllSubCatActivity.class);
                startActivity(intent);
            }
        });
        //
        try {
            String qr = preferences.getString("business_referral_code", "").substring(0, 1) + "-" + preferences.getString("business_referral_code", "").substring(1, 5) + "-" + preferences.getString("business_referral_code", "").substring(5, 9);
            referralCode.setText(qr);
            businessMobile = preferences.getString("mobile_no", "");
            businessEmail = preferences.getString("email_id", "");
            businessImage = preferences.getString("company_logo", "");
            businessName = preferences.getString("business_name", "");
            fileName = businessName;
            tempimageName = businessImage;
            if (!businessMobile.toLowerCase().contains("null")) {
                userMobile.setVisibility(View.VISIBLE);
                userMobile.setText(businessMobile);
            }
            if (!businessEmail.toLowerCase().contains("null")) {
                usermailid.setVisibility(View.VISIBLE);
                usermailid.setText(businessEmail);
            }
            userName.setText(businessName);
            // Picasso.with(BusinessProfileSettingActivity.this).load(businessImage).noPlaceholder().into((ImageView) profilePic);

            if (businessImage.contains("http")) {
                try {
                    Picasso.get().load(businessImage).into(profilePic);
                    if (preferences.getString("upgradetopr", "").contains("1")) {
                        ugtop.setVisibility(View.VISIBLE);
                    } else {
                        ugtop.setVisibility(View.GONE);
                    }

                } catch (Exception e) {

                }
            } else {
                profilePic.setImageResource(R.drawable.profile_pic);
                if (preferences.getString("upgradetopr", "").contains("1")) {
                    ugtop.setVisibility(View.VISIBLE);
                } else {
                    ugtop.setVisibility(View.GONE);
                }
            }
        } catch (Exception E) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.getString("upgradetopr", "").contains("1")) {
            ugtop.setVisibility(View.VISIBLE);
        } else {
            ugtop.setVisibility(View.GONE);
        }
    }

    private class getData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(BusinessProfileSettingActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //   mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            businessUserResponse = serviceAccess.SendHttpPost(Config.URL_GETBUSINESSUSERDEATILS, jsonLeadObj);
            Log.i("resp", "businessUserResponse" + businessUserResponse);


            if (businessUserResponse.compareTo("") != 0) {
                if (isJSONValid(businessUserResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                JSONArray leadJsonObj = new JSONArray(businessUserResponse);
                                for (int i = 0; i < leadJsonObj.length(); i++) {
                                    JSONObject object = leadJsonObj.getJSONObject(i);
                                    businessName = object.getString("business_name");
                                    businessMobile = object.getString("mobile_no");
                                    businessEmail = object.getString("email_id");
                                    businessImage = object.getString("company_logo");
                                    prefEditor.putString("mobile_no", object.getString("mobile_no"));
                                    prefEditor.putString("email_id", object.getString("email_id"));
                                    prefEditor.putString("blat", object.getString("latitude"));
                                    prefEditor.putString("blong", object.getString("longitude"));
                                    prefEditor.commit();
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            userName.setText(businessName);
            fileName = businessName;
            tempimageName = businessImage;
            // referralCode.setText(preferences.getString("business_referral_code", ""));
            String qr = preferences.getString("business_referral_code", "").substring(0, 1) + "-" + preferences.getString("business_referral_code", "").substring(1, 5) + "-" + preferences.getString("business_referral_code", "").substring(5, 9);
            referralCode.setText(qr);
            if (!businessMobile.equals("null")) {
                userMobile.setVisibility(View.VISIBLE);
                userMobile.setText(businessMobile);
            }
            if (!businessEmail.equals("null")) {
                usermailid.setVisibility(View.VISIBLE);
                usermailid.setText(businessEmail);
            }
            //  ImageLoader imageLoader = new ImageLoader(BusinessProfileSettingActivity.this);
            //  imageLoader.DisplayImage(businessImage, profilePic);

            //Picasso.with(BusinessProfileSettingActivity.this).load(businessImage).noPlaceholder().into((ImageView) profilePic);
            Picasso.get().load(businessImage).into(profilePic);
            // Close the progressdialog
            //  mProgressDialog.dismiss();
        }
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

    //profile photo
    private void selectImage() {
        final CharSequence[] items = {res.getString(R.string.jtp), res.getString(R.string.jtcfl)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BusinessProfileSettingActivity.this);
        builder.setTitle(res.getString(R.string.japh));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(BusinessProfileSettingActivity.this);

                if (items[item].equals(res.getString(R.string.jtp))) {
                    userChoosenTask = res.getString(R.string.jtp);
                    if (result)
                        cf = 1;
                    cameraIntent();


                } else if (items[item].equals(res.getString(R.string.jtcfl))) {
                    userChoosenTask = res.getString(R.string.jtcfl);
                    if (result)
                        cf = 2;
                    galleryIntent();


                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);*/

        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // pickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, PICK_IMAGE_MULTIPLE);

        /*Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_MULTIPLE);*/

    }

    private void cameraIntent() {

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    mArrayUri.add(mImageUri);
                    if (!mImageUri.toString().contains("mediakey")) {
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);

                        cursor.close();
                        Log.v("LOG_TAG", "imageEncoded" + imageEncoded);
                        Log.v("LOG_TAG", "Selected Images" + mImageUri);
                        // Create a String array for FilePathStrings
                        FilePathStrings = new String[1];
                        // Create a String array for FileNameStrings
                        FileNameStrings = new String[1];

                        //   File myFile = new File(mImageUri.getPath());

                        //  Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(BusinessProfileSettingActivity.this, mImageUri));

                        imagesEncodedList.add(getPathFromUri(BusinessProfileSettingActivity.this, mImageUri));
                        // imagesEncodedList.add(getRealPathFromUri(mImageUri));

                        // Get the path of the image file
                        // FilePathStrings[i] = myFile.getAbsolutePath();
                        // FilePathStrings[0] = getRealPathFromURI(mImageUri);
                        //   FilePathStrings[0] = getPathFromUri(BusinessProfileSettingActivity.this, mImageUri);
                        // Get the name image file
                        FileNameStrings[0] = getFileName(mImageUri);
                        // Toast.makeText(getApplicationContext(), "Uploading image", Toast.LENGTH_SHORT).show();
                        if (mArrayUri.size() > 0) {
                            for (int i = 0; i < imagesEncodedList.size(); i++) {
                                map.add(imagesEncodedList.get(i).toString());
                            }
                            new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                            if (!tempimageName.equals("")) {
                                new deleteDisContinue().execute();
                            }
                        }
                    } else {
                        Toast.makeText(BusinessProfileSettingActivity.this, res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            } else if (requestCode == REQUEST_CAMERA) {
                //  onCaptureImageResult(data);

                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity(true);


                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            res.getString(R.string.junc), Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            res.getString(R.string.jsunc), Toast.LENGTH_SHORT)
                            .show();
                }


            } else {
                Toast.makeText(this, res.getString(R.string.jeunp),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Exception", "" + e);
            Toast.makeText(this, res.getString(R.string.jsw), Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //new camera images


    private void launchUploadActivity(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            mArrayUri.add(Uri.fromFile(destination));
            imagesEncodedList = new ArrayList<String>();
            FilePathStrings = new String[1];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[1];

            imagesEncodedList.add("" + destination);// imagesEncodedList.add(destination);


            // Get the path of the image file
            // FilePathStrings[i] = myFile.getAbsolutePath();
            // FilePathStrings[0] = getRealPathFromURI(mImageUri);
            FilePathStrings[0] = "" + destination;
            // Get the name image file
            FileNameStrings[0] = fileName;
            //  Toast.makeText(getApplicationContext(), "Uploading image", Toast.LENGTH_SHORT).show();
            if (mArrayUri.size() > 0) {
                for (int i = 0; i < imagesEncodedList.size(); i++) {
                    map.add(imagesEncodedList.get(i).toString());
                }
                new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
            }

        } else {

        }
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

        // Internal sdcard location
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "AlertMeUBusiness");
        // Create the storage directory if it does not exist
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.d(TAG, "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {

            fileName = "B_" + preferences.getString("business_user_id", "") + "_pic_" + System.currentTimeMillis() + ".png";
            mediaFile = new File(folder.getPath() + File.separator + fileName);
            destination = new File(folder.getPath(), fileName);

        } else {
            return null;
        }

        return mediaFile;
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


    //
    class ImageUploadTask extends AsyncTask<String, Void, String> {

        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(BusinessProfileSettingActivity.this, "",
                    res.getString(R.string.jpw), true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                File file = new File(map.get(i));
                File f = CompressFile.getCompressedImageFile(file, BusinessProfileSettingActivity.this);
                //  Bitmap bitmap = decodeFile(map.get(i));
                Bitmap bitmap = decodeFile(f.getAbsolutePath());
              /*  Bitmap bitmap = decodeFile(map.get(i));
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                File file = new File(map.get(i));
                int imageRotation = getImageRotation(file);

                if (imageRotation != 0)
                    scaled = getBitmapRotatedByDegree(scaled, imageRotation);*/
                //   bitmap = Bitmap.createScaledBitmap(bitmap, 800, 1000, true);
                //  File file = new File(map.get(i));
                // HttpClient httpClient = new DefaultHttpClient();
                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                // scaled.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();
                if (cf == 1) {
                    fileName = "B_" + preferences.getString("business_user_id", "") + "_cam_" + System.currentTimeMillis() + ".png";
                }
                if (cf == 2) {
                    fileName = "B_" + preferences.getString("business_user_id", "") + "_lib_" + System.currentTimeMillis() + ".png";
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

            jsonLeadObjReq = new JSONObject() {
                {
                    try {

                        put("path", Config.URL_AlertMeUImage + "uploads/" + fileName);
                        put("business_user_id", preferences.getString("business_user_id", ""));
                        businessImage = Config.URL_AlertMeUImage + "uploads/" + fileName;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObjReq);
            addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEPROFILEPATH, jsonLeadObjReq);
            Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
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
                        //Toast.makeText(getApplicationContext(), "Refresh...", Toast.LENGTH_SHORT).show();
                       /* FileCache fileCache;
                        fileCache = new FileCache(getApplicationContext());
                        fileCache.clear();
                        ImageLoader imageLoader = new ImageLoader(BusinessProfileSettingActivity.this);
                        imageLoader.DisplayImage(businessImage, profilePic);*/
                        // Picasso.with(BusinessProfileSettingActivity.this).load(businessImage).noPlaceholder().into((ImageView) profilePic);
                        try {
                            Picasso.get().load(businessImage).into(profilePic);
                        } catch (Exception e) {

                        }
                        prefEditor.putString("company_logo", businessImage);
                        prefEditor.commit();
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    private class deleteDisContinue extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("path", tempimageName.replace("https://www.alertmeu.com/", ""));
                        // put("path", imageName.replace(Config.URL_AlertMeUImage+"uploads/", ""));
                        put("business_user_id", preferences.getString("business_user_id", ""));


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            deleteLogoResponse = serviceAccess.SendHttpPost(Config.URL_DELETEBUSINESSIMAGE, jsonLeadObj);
            // Log.i("resp", "deleteLogoResponse" + deleteLogoResponse);

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {


        }
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
    //
    private boolean checkAndRequestPermissions() {

        int writepermission = ContextCompat.checkSelfPermission(BusinessProfileSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(BusinessProfileSettingActivity.this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(BusinessProfileSettingActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //finish();
                        //else any one or both the permissions are not granted
                        selectImage();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(BusinessProfileSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(BusinessProfileSettingActivity.this, Manifest.permission.CAMERA)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BusinessProfileSettingActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(BusinessProfileSettingActivity.this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:in.alertmeu.a4b")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Intent setIntent = new Intent(BusinessProfileSettingActivity.this, HomePageActivity.class);
        startActivity(setIntent);
        finish();

    }

    public void insertUserClick() {

        JSONObject jsonSchedule1 = new JSONObject() {
            {
                try {

                    put("business_user_id", preferences.getString("business_user_id", ""));

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

                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_INSERTBUSINESSSHARECOUNTUAPP, jsonSchedule1);
            }
        });

        objectThread.start();

    }

    public void insertBusinessClick() {

        JSONObject jsonSchedule1 = new JSONObject() {
            {
                try {
                    put("business_user_id", preferences.getString("business_user_id", ""));
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
                imagePathResponse = serviceAccess.SendHttpPost(Config.URL_INSERTBUSINESSSHARECOUNTBAPP, jsonSchedule1);
            }
        });

        objectThread.start();

    }

    public void getImagePath() {

        jsonSchedule = new JSONObject() {
            {
                try {
                    put("business_user_id", preferences.getString("business_user_id", ""));
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
                avgRateResponse = serviceAccess.SendHttpPost(Config.URL_GETREVIEWBUSINESSAVGBYBID, jsonSchedule);
                Log.i("resp", "imagePathResponse" + imagePathResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObject1 = new JSONObject(avgRateResponse);
                            status1 = jsonObject1.getBoolean("status");
                            if (status1) {
                                hideshowr.setVisibility(View.VISIBLE);
                                if (!jsonObject1.isNull("dataList")) {
                                    JSONArray jsonArray = jsonObject1.getJSONArray("dataList");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        avgrating = object.getString("pBidavgrating");
                                        torcount = object.getString("torcount");
                                        avgratingStar.setVisibility(View.VISIBLE);
                                        avgratingStar.setRating(Float.parseFloat(avgrating));
                                        torcountr.setVisibility(View.VISIBLE);
                                        torcountr.setText(torcount);
                                    }

                                }
                            } else {
                                hideshowr.setVisibility(View.GONE);
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
}
