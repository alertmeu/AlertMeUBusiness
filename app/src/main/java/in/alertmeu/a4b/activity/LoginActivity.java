package in.alertmeu.a4b.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Pattern;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;

public class LoginActivity extends AppCompatActivity {
    public static LoginActivity fa;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private EditText u_emailid, u_pass, u_mobile;
    CountryCodePicker ccp;
    String userId = "", userMobile = "", userPassword = "", loginResponse = "", msg = "", registrationResponse = "";
    boolean status;
    ProgressDialog mProgressDialog;
    JSONObject jsonObj, jsonObject;
    private TextView title, register, loginEmailButton, loginMobileButton, hide1;
    private Button emailNext, mobileNext;
    LinearLayout layoutHide, hide2;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_login);
        fa = this;
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        title = (TextView) findViewById(R.id.title);
        loginMobileButton = (TextView) findViewById(R.id.loginMobileButton);
        loginEmailButton = (TextView) findViewById(R.id.loginEmailButton);
        register = (TextView) findViewById(R.id.register);
        hide1 = (TextView) findViewById(R.id.hide1);
        hide2 = (LinearLayout) findViewById(R.id.hide2);
        emailNext = (Button) findViewById(R.id.emailNext);
        mobileNext = (Button) findViewById(R.id.mobileNext);
        layoutHide = (LinearLayout) findViewById(R.id.layoutHide);

        u_emailid = (EditText) findViewById(R.id.u_emailid);
        u_mobile = (EditText) findViewById(R.id.u_mobile);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(u_mobile);
        // ccp.setNumberAutoFormattingEnabled(true);
        ccp.isValidFullNumber();
        ccp.setCountryPreference(ccp.getDefaultCountryNameCode());
        prefEditor.putString("country_code", ccp.getSelectedCountryCodeWithPlus());
        prefEditor.commit();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //  String CountryCode = Locale.getDefault().getCountry();
                //  Toast.makeText(getApplicationContext(), "Updated " + CountryCode, Toast.LENGTH_SHORT).show();
                prefEditor.putString("country_code", ccp.getSelectedCountryCodeWithPlus());
                prefEditor.commit();
            }
        });
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code

                if (isValidNumber) {
                    userMobile = ccp.getFullNumberWithPlus();

                 //   Toast.makeText(getApplicationContext(), "Your mobile number is valid.", Toast.LENGTH_SHORT).show();
                } else {
                    userMobile = "";
                    //Toast.makeText(getApplicationContext(), "Please Enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginMobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText(res.getString(R.string.xemn));
                u_emailid.setVisibility(View.GONE);
                emailNext.setVisibility(View.GONE);
                layoutHide.setVisibility(View.VISIBLE);
                mobileNext.setVisibility(View.VISIBLE);
                hide1.setVisibility(View.VISIBLE);
                hide2.setVisibility(View.VISIBLE);
                loginMobileButton.setVisibility(View.GONE);
                loginEmailButton.setVisibility(View.VISIBLE);

            }
        });
        loginEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText(res.getString(R.string.l_email));
                layoutHide.setVisibility(View.GONE);
                mobileNext.setVisibility(View.GONE);
                hide1.setVisibility(View.GONE);
                hide2.setVisibility(View.GONE);
                u_emailid.setVisibility(View.VISIBLE);
                emailNext.setVisibility(View.VISIBLE);
                loginMobileButton.setVisibility(View.VISIBLE);
                loginEmailButton.setVisibility(View.GONE);
            }
        });

        mobileNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Mobile" + u_mobile.getText().toString(), Toast.LENGTH_SHORT).show();
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    //userMobile = u_mobile.getText().toString().trim();
                    if (validateMobile(userMobile)) {
                        new userCheckMobile().execute();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        emailNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Email" + u_emailid.getText().toString(), Toast.LENGTH_SHORT).show();
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    userId = u_emailid.getText().toString().trim();
                    if (validate(userId)) {
                        new userCheckEmail().execute();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    Intent intent = new Intent(LoginActivity.this, RegisterNGetStartActivity.class);
                    startActivity(intent);


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean validateMobile(String userMobile) {
        boolean isValidate = false;
        if (userMobile.trim().equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpenm), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else {
            isValidate = true;
        }
        return isValidate;
    }

    public boolean validate(String email) {
        boolean isValidate = false;
        if (email.trim().equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jemi), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (!validateEmail(email)) {
            if (!email.equals("")) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.jvemi), Toast.LENGTH_LONG).show();
                isValidate = false;
            } else {
                isValidate = true;
            }
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


    private class userCheckEmail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jlogin));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonObj = new JSONObject() {
                {
                    try {
                        put("email_users", userId);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            loginResponse = serviceAccess.SendHttpPost(Config.URL_CHECKBUSEREMAILLOGIN, jsonObj);
            Log.i("resp", "loginResponse" + loginResponse);


            if (loginResponse.compareTo("") != 0) {
                if (isJSONValid(loginResponse)) {


                    try {

                        jsonObject = new JSONObject(loginResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        if (status) {
                            if (!jsonObject.isNull("user_id")) {
                                JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                                for (int i = 0; i < ujsonArray.length(); i++) {
                                    JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                                    prefEditor.putString("business_name", UJsonObject.getString("business_name"));
                                    prefEditor.putString("save_latitude", "" + UJsonObject.getString("latitude"));
                                    prefEditor.putString("save_longitude", "" + UJsonObject.getString("longitude"));
                                    prefEditor.putString("mobile_no", UJsonObject.getString("mobile_no"));
                                    prefEditor.putString("email_id",  UJsonObject.getString("email_id"));
                                    prefEditor.putString("company_logo",  UJsonObject.getString("company_logo"));
                                    prefEditor.putString("userEmail", userId);
                                    prefEditor.putString("flag", "email");
                                    prefEditor.putInt("units_for_area", 20);
                                    prefEditor.commit();
                                }
                            }

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    // Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, PostLoginActivity.class);
                startActivity(intent);

                // Close the progressdialog
                mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

            }
        }
    }

    private class userCheckMobile extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jlogin));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonObj = new JSONObject() {
                {
                    try {
                        put("mobile_users", userMobile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            loginResponse = serviceAccess.SendHttpPost(Config.URL_CHECKBUSINESSMOBILELOGIN, jsonObj);
            Log.i("resp", "loginResponse" + loginResponse);


            if (loginResponse.compareTo("") != 0) {
                if (isJSONValid(loginResponse)) {


                    try {

                        jsonObject = new JSONObject(loginResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        if (status) {
                            if (!jsonObject.isNull("user_id")) {
                                JSONArray ujsonArray = jsonObject.getJSONArray("user_id");
                                for (int i = 0; i < ujsonArray.length(); i++) {
                                    JSONObject UJsonObject = ujsonArray.getJSONObject(i);
                                    prefEditor.putString("business_name", UJsonObject.getString("business_name"));
                                    prefEditor.putString("save_latitude", "" + UJsonObject.getString("latitude"));
                                    prefEditor.putString("save_longitude", "" + UJsonObject.getString("longitude"));
                                    prefEditor.putString("mobile_no", UJsonObject.getString("mobile_no"));
                                    prefEditor.putString("email_id",  UJsonObject.getString("email_id"));
                                    prefEditor.putString("company_logo",  UJsonObject.getString("company_logo"));
                                    prefEditor.putString("userMobile", userMobile);
                                    prefEditor.putString("flag", "mobile");
                                    prefEditor.putInt("units_for_area", 20);
                                    prefEditor.commit();
                                }
                            }

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    // Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
               // Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, PostLoginActivity.class);
                startActivity(intent);

                // Close the progressdialog
                mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

            }
        }
    }

    protected boolean isJSONValid(String registrationResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(registrationResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(registrationResponse);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
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
        Intent setIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(setIntent);
    }
}
