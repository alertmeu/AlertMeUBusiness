package in.alertmeu.a4b.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.GMailSender;
import in.alertmeu.a4b.utils.WebClient;

public class OTPforChangeMActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    EditText edtCode;
    Button btnNext;
    String id;
    TextView txtId;
    String code = "", email_id = "";
    private JSONObject jsonLeadObj;
    ProgressDialog mProgressDialog;
    boolean status;
    String msg = "";
    String updateStatusResponse = "";
    private static final String username = "email-verification@alertmeu.com";
    private static String password = "ZSAM@2020";
    GMailSender sender;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_o_t_pfor_change_m);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        edtCode = (EditText) findViewById(R.id.edtCode);
        btnNext = (Button) findViewById(R.id.btnNext);
        txtId = (TextView) findViewById(R.id.txtId);
        Intent intent = getIntent();
        email_id = intent.getStringExtra("new_email");
        Random random = new Random();
        id = String.format("%06d", random.nextInt(1000000));
        sender = new GMailSender(username, password);
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
      //  txtId.setText(id);
        new MyAsyncClass().execute();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    code = edtCode.getText().toString().trim();
                    if (code.equals(id)) {
                        new updateMobileNo().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jcodemis), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class updateMobileNo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(OTPforChangeMActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jues));
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
                        put("email_id", email_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEBEMAIL, jsonLeadObj);
            Log.i("resp", "updateStatusResponse" + updateStatusResponse);

            if (updateStatusResponse.compareTo("") != 0) {
                if (isJSONValid(updateStatusResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(updateStatusResponse);
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
            mProgressDialog.dismiss();
            if (status) {
                prefEditor.putString("userEmail", email_id);
                prefEditor.commit();
                Intent intent = new Intent(OTPforChangeMActivity.this, BusinessProfileSettingActivity.class);
                startActivity(intent);
                finish();


            } else {

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();


            }


        }
    }
    class MyAsyncClass extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(OTPforChangeMActivity.this);
            pDialog.setMessage(res.getString(R.string.jpw));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Toast.makeText(getActivity(), "mail id is"+preferences.getString("sendingmailid", ""), Toast.LENGTH_SHORT).show();
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Please verify your email address for AlertMeU", "To finish verifying your email with AlertMeU, please enter the following security code:\n\n" + id + "\n\nWe hope to see you again soon.\n" + Html.fromHtml("<b>AlertMeU.com Technical Support</b>") + "\nTechnical-Support@AlertMeU.com", username, email_id);


            } catch (Exception ex) {
                Log.d("Error", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast.makeText(OTPforChangeMActivity.this, res.getString(R.string.jes), Toast.LENGTH_LONG).show();
            btnNext.setVisibility(View.VISIBLE);
            // timer.setVisibility(View.VISIBLE);
           // time =60;
          //  startTimer();
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