package in.alertmeu.a4b.activity;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.provider.Settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import in.alertmeu.a4b.FirebaseNotification.SharedPrefManager;
import in.alertmeu.a4b.R;

import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.ExifUtil;
import in.alertmeu.a4b.utils.GMailSender;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.WebClient;
import in.alertmeu.a4b.view.AddEEntryView;
import in.alertmeu.a4b.view.AddPEntryView;

public class EditAccountSetupActivity extends AppCompatActivity {
    EditText edtNameOb, edtAddressOb;
    TextView edtMobileOb, edtEmailOb;
    ImageView showimage, takeimage, deleteimage;
    LinearLayout btnNext, rmobile, rmail;
    Bitmap myBitmap;
    Uri picUri = null;

    int PICK_IMAGE_MULTIPLE = 1;
    private int REQUEST_CAMERA = 0;
    private Uri fileUri; // file url to store image/video
    //new camera images
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = AccountSetupActivity.class.getSimpleName();
    static String fileName = "";
    static File destination;
    String imageEncoded;
    static List<String> imagesEncodedList;
    // Declare variables
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

    //for edit
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    private JSONObject jsonLeadObj;
    String businessUserResponse = "";
    String businessName = "", businessEmail = "", businessAddress = "", imageName = "", businessMobile = "", locationName, latitude, longitude;
    boolean status;
    String message = "";
    JSONArray jsonArray;
    JSONObject jsonObj1;
    String checkEmailResponse = "", msg1 = "", addBusinessAcountResponse = "";
    TextView warning, rmobilen, rmailid;
    boolean status1;
    JSONObject jsonObject;
    String imgFlag = "1";

    Button Edit, add, addM, EditM;
    String id;
    private static final String username = "email-verification@alertmeu.com";
    private static String password = "ZSAM@2020";
    GMailSender sender;
    EditText edtCode;
    String orgMail = "";
    private JSONObject jsonLeadObj1, jsonLeadObjReq;
    private ProgressDialog dialog;
    String token = "null";
    String deviceId = "";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_edit_account_setup);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sender = new GMailSender(username, password);
        edtNameOb = (EditText) findViewById(R.id.edtNameOb);
        edtMobileOb = (TextView) findViewById(R.id.edtMobileOb);
        edtEmailOb = (TextView) findViewById(R.id.edtEmailOb);
        rmobilen = (TextView) findViewById(R.id.rmobilen);
        rmailid = (TextView) findViewById(R.id.rmailid);
        edtAddressOb = (EditText) findViewById(R.id.edtAddressOb);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);
        rmobile = (LinearLayout) findViewById(R.id.rmobile);
        rmail = (LinearLayout) findViewById(R.id.rmail);
        warning = (TextView) findViewById(R.id.warning);
        add = (Button) findViewById(R.id.add);
        Edit = (Button) findViewById(R.id.Edit);
        addM = (Button) findViewById(R.id.addM);
        EditM = (Button) findViewById(R.id.EditM);
        edtCode = (EditText) findViewById(R.id.edtCode);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (preferences.getString("primary_login", "").equals("1")) {
            rmail.setVisibility(View.GONE);
            rmobile.setVisibility(View.VISIBLE);
            rmobilen.setText(preferences.getString("mobile_no", ""));
        } else {
            rmobile.setVisibility(View.GONE);
            rmail.setVisibility(View.VISIBLE);
            rmailid.setText(preferences.getString("email_id", ""));
        }
        Random random = new Random();
        id = String.format("%06d", random.nextInt(1000000));

        AddEEntryView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (!messageText.trim().equals("")) {
                    add.setVisibility(View.GONE);
                    Edit.setVisibility(View.VISIBLE);
                    edtEmailOb.setText(messageText);

                }
            }
        });

        AddPEntryView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (!messageText.trim().equals("")) {
                    addM.setVisibility(View.GONE);
                    EditM.setVisibility(View.VISIBLE);
                    edtMobileOb.setText(messageText);

                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                businessName = edtNameOb.getText().toString();
                businessAddress = edtAddressOb.getText().toString();
                if (businessAddress.equals("")) {
                    businessAddress = " ";
                }
                if (validatewe(businessName)) {
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        new addBusinessAccountDetails().execute();

                    } else {

                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEEntryView addEEntryView = new AddEEntryView();
                addEEntryView.show(getSupportFragmentManager(), "addEEntryView");
            }
        });
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEEntryView addEEntryView = new AddEEntryView();
                addEEntryView.show(getSupportFragmentManager(), "addEEntryView");
            }
        });
        addM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPEntryView addPEntryView = new AddPEntryView();
                addPEntryView.show(getSupportFragmentManager(), "addPEntryView");
            }
        });
        EditM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPEntryView addPEntryView = new AddPEntryView();
                addPEntryView.show(getSupportFragmentManager(), "addPEntryView");
            }
        });
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

            new getData().execute();

        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }


    }


    private boolean validatewe(String nob) {
        boolean isValidate = false;
        if (nob.equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpsbn), Toast.LENGTH_LONG).show();
        } else {
            isValidate = true;
        }
        return isValidate;
    }

    /**
     * email validation
     */
    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

    public boolean validateEmail(String email) {
        if (!email.contains("@")) {
            return false;
        }
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }


    private class getData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(EditAccountSetupActivity.this);
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
            businessUserResponse = serviceAccess.SendHttpPost(Config.URL_GETBUSINESSUSERDETAILS, jsonLeadObj);
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
                                    businessMobile = object.getString("business_number");
                                    businessEmail = object.getString("business_email");
                                    businessAddress = object.getString("address");
                                    imageName = object.getString("company_logo");
                                    locationName = object.getString("location_name");


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
            // Close the progressdialog
            mProgressDialog.dismiss();
            edtNameOb.setText(businessName);
            if (!businessMobile.trim().equals("")) {
                addM.setVisibility(View.GONE);
                EditM.setVisibility(View.VISIBLE);
                edtMobileOb.setText(businessMobile);
            }
            if (!businessEmail.trim().equals("")) {
                add.setVisibility(View.GONE);
                Edit.setVisibility(View.VISIBLE);
                edtEmailOb.setText(businessEmail);

            }
            edtAddressOb.setText(businessAddress);
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

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(EditAccountSetupActivity.this);
            pDialog.setMessage(res.getString(R.string.jpw));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Toast.makeText(getActivity(), "mail id is"+preferences.getString("sendingmailid", ""), Toast.LENGTH_SHORT).show();
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Please verify your email address for AlertMeU", "To finish verifying your email with AlertMeU, please enter the following security code:\n\n" + id + "\n\nWe hope to see you again soon.\n" + Html.fromHtml("<b>AlertMeU.com Technical Support</b>") + "\nTechnical-Support@AlertMeU.com", username, businessEmail);

            } catch (Exception ex) {
                Log.d("Error", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(EditAccountSetupActivity.this, res.getString(R.string.jes), Toast.LENGTH_LONG).show();
            edtCode.setVisibility(View.VISIBLE);
            // timer.setVisibility(View.VISIBLE);
            // time =60;
            //  startTimer();
        }


    }

    private class addBusinessAccountDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(EditAccountSetupActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jud));
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
            prefEditor.putString("business_name", businessName);
            prefEditor.commit();

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                        put("business_name", businessName);
                        put("device_id", deviceId);
                        put("fcm_id", token);
                        put("address", businessAddress);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            addBusinessAcountResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEBUSINESSUSERDETAILS, jsonLeadObj);
            Log.i("resp", "addBusinessAcountResponse" + addBusinessAcountResponse);
            if (addBusinessAcountResponse.compareTo("") != 0) {
                if (isJSONValid(addBusinessAcountResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addBusinessAcountResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
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
            if (status) {
                prefEditor.putString("business_name", businessName);
                prefEditor.commit();
                Intent setIntent = new Intent(EditAccountSetupActivity.this, BusinessProfileSettingActivity.class);
                startActivity(setIntent);
                finish();
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

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(EditAccountSetupActivity.this, BusinessProfileSettingActivity.class);
        startActivity(setIntent);
        finish();
    }
}
