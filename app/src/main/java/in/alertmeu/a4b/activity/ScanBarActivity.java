package in.alertmeu.a4b.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.a4b.Fragments.TabsFragmentActivity;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;

public class ScanBarActivity extends AppCompatActivity {
    ImageView btnHome, btnBusinessList, btnscanbar;
    private ImageView naviBtn;
    TextView txtScan;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String values = "";
    String user_id = "", business_id = "", barcode = "";
    private JSONObject jsonLeadObj, jsonLeadObj1;
    ProgressDialog mProgressDialog;
    boolean status;
    String msg = "", redeemedResponse = "";
    String localTime;
    LinearLayout btnscanbarC;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_scan_bar);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        btnHome = (ImageView) findViewById(R.id.btnHome);
        naviBtn = (ImageView) findViewById(R.id.naviBtn);
        btnscanbar = (ImageView) findViewById(R.id.btnscanbar);
        txtScan = (TextView) findViewById(R.id.txtScan);
        btnscanbarC = (LinearLayout) findViewById(R.id.btnscanbarC);
        btnscanbarC.setBackgroundColor(Color.parseColor("#809E9E9E"));
        values = preferences.getString("value", "");
        try {
            String a[] = values.split("#");
            user_id = a[0];
            business_id = a[1];
            barcode = a[2];
        } catch (Exception e) {

        }
        btnBusinessList = (ImageView) findViewById(R.id.btnBusinessList);
        naviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(ScanBarActivity.this, BusinessProfileSettingActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBusinessList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanBarActivity.this, TabsFragmentActivity.class);
                intent.putExtra("active", "1");
                startActivity(intent);
                finish();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanBarActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnscanbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanBarActivity.this, ScanActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new initChkRedeemed().execute();
        } else {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
    }

    private class initChkRedeemed extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(ScanBarActivity.this);
            // Set progressdialog title
            //   mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            // mProgressDialog.setMessage("Updating Redeemed...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                        put("advertisment_id", business_id);
                        put("bar_code", barcode);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            redeemedResponse = serviceAccess.SendHttpPost(Config.URL_CHECLUSERREDEEMEDCODE, jsonLeadObj1);
            Log.i("resp", "redeemedResponse" + redeemedResponse);

            if (redeemedResponse.compareTo("") != 0) {
                if (isJSONValid(redeemedResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(redeemedResponse);
                                msg = jsonObject.getString("message");
                                status = jsonObject.getBoolean("status");

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
                            // Toast.makeText(ScanBarActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(ScanBarActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // mProgressDialog.dismiss();
            if (status) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    new initRedeemed().execute();
                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            } else {
                txtScan.setText(res.getString(R.string.jvqb));
                txtScan.setTextColor(Color.parseColor("#FF0000"));
            }


        }
    }

    private class initRedeemed extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ScanBarActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jurb));
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
                        put("user_id", user_id);
                        put("advertisment_id", business_id);
                        put("bar_code", barcode);
                        put("t_zone", localTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            redeemedResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERREDEEMED, jsonLeadObj);
            Log.i("resp", "redeemedResponse" + redeemedResponse);

            if (redeemedResponse.compareTo("") != 0) {
                if (isJSONValid(redeemedResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(redeemedResponse);
                                msg = jsonObject.getString("message");
                                status = jsonObject.getBoolean("status");

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
                            Toast.makeText(ScanBarActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanBarActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (status) {
                txtScan.setText(res.getString(R.string.jurwb));
            } else {
                txtScan.setText(res.getString(R.string.jurhwb));
                txtScan.setTextColor(Color.parseColor("#FF0000"));

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
}
