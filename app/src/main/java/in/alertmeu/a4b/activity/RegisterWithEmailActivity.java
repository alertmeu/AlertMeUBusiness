package in.alertmeu.a4b.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;

public class RegisterWithEmailActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    EditText u_mail, uReferral;
    Button btnNext, btnApply;
    String emailId = "";
    CountryCodePicker ccp;
    JSONObject jsonObj, jsonObj1;
    String verifyMobileResponse = "";
    TextView warning;
    boolean status, status1;
    LinearLayout phone;
    String deviceId = "0";
    TelephonyManager telephonyManager;
    TextView failCode, successCode, usedCode,tnc;
    String localTime, refer_user_id = "", flag = "0", referral_code = "", registrationResponse = "", referral_status = "0", msg = "", checkEmailResponse = "", msg1 = "",r_country_code="";
    ProgressDialog mProgressDialog;
    JSONObject jsonObject;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    private EditText editText1, editText2, editText3, editText4, editText5, editText6, editText7, editText8, editText9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_register_with_email);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        btnNext = (Button) findViewById(R.id.btnNext);
        warning = (TextView) findViewById(R.id.warning);
        phone = (LinearLayout) findViewById(R.id.phone);
        u_mail = (EditText) findViewById(R.id.u_mail);
        btnApply = (Button) findViewById(R.id.btnApply);
        uReferral = (EditText) findViewById(R.id.uReferral);
        failCode = (TextView) findViewById(R.id.failCode);
        successCode = (TextView) findViewById(R.id.successCode);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        editText7 = (EditText) findViewById(R.id.editText7);
        editText8 = (EditText) findViewById(R.id.editText8);
        editText9 = (EditText) findViewById(R.id.editText9);
        tnc= (TextView) findViewById(R.id.tnc);
        usedCode = (TextView) findViewById(R.id.usedCode);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    emailId = u_mail.getText().toString().trim();
                    if (validate(emailId)) {
                        new userCheckEmailsCode().execute();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterWithEmailActivity.this, TremsServiceActivity.class);
                startActivity(intent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterWithEmailActivity.this, RegisterNGetStartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText1.getText().toString().length() == 1) {
                    editText2.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText2.getText().toString().length() == 0) {
                    editText1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText2.getText().toString().length() == 1) {
                    editText3.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText3.getText().toString().length() == 0) {
                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText3.getText().toString().length() == 1) {
                    editText4.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
            }
        });

        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText4.getText().toString().length() == 0) {
                    editText3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText4.getText().toString().length() == 1) {
                    editText5.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                // Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText5.getText().toString().length() == 0) {
                    editText4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText5.getText().toString().length() == 1) {
                    editText6.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                // Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText6.getText().toString().length() == 0) {
                    editText5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText6.getText().toString().length() == 1) {
                    editText7.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        editText7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText7.getText().toString().length() == 0) {
                    editText6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText7.getText().toString().length() == 1) {
                    editText8.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
        editText8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText8.getText().toString().length() == 0) {
                    editText7.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText8.getText().toString().length() == 1) {
                    editText9.requestFocus();
                }
                referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                if (referral_code.length()==9) {
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                // We can call api to verify the OTP here or on an explicit button click
                //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        editText9.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText9.getText().toString().length() == 0) {
                    editText8.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // We can call api to verify the OTP here or on an explicit button click
                if (!editText9.getText().toString().equals("")) {
                    referral_code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString() + editText7.getText().toString() + editText8.getText().toString() + editText9.getText().toString();
                    new userReferralCode().execute();
                    //Toast.makeText(getApplicationContext(), referral_code, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    successCode.setVisibility(View.GONE);
                    usedCode.setVisibility(View.GONE);
                    failCode.setVisibility(View.GONE);
                }
            }
        });
        uReferral.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referral_code = uReferral.getText().toString().trim();
                if (!referral_code.equals("")) {
                    try {

                    } catch (Exception e) {
                        deviceId = "0";
                    }
                    if (!deviceId.equals("0")) {
                        new userReferralCode().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Your device not compatible for Referral Code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter referral code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uReferral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(uReferral.getText())) {
                        referral_code = uReferral.getText().toString();
                        try {

                            if (!deviceId.equals("0")) {
                                new userReferralCode().execute();
                            } else {
                                Toast.makeText(getApplicationContext(), "Your device not compatible for Referral Code", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            deviceId = "0";
                        }


                    } else {
                        successCode.setVisibility(View.GONE);
                        usedCode.setVisibility(View.GONE);
                        failCode.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
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

    private class userReferralCode extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
         //   mProgressDialog = new ProgressDialog(RegisterWithEmailActivity.this);
            // Set progressdialog title
          //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
         //   mProgressDialog.setMessage("Apply Referral...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
         //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonObj = new JSONObject() {
                {
                    try {

                        put("referral_code", referral_code);
                        put("referral_status", referral_code.substring(0, 1));
                        put("deviceId", deviceId);
                        put("app_type", 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj);
            registrationResponse = serviceAccess.SendHttpPost(Config.URL_CHECKINGREFERRALCODE, jsonObj);
            Log.i("resp", "registrationResponse" + registrationResponse);


            if (registrationResponse.compareTo("") != 0) {
                if (isJSONValid(registrationResponse)) {


                    try {

                        jsonObject = new JSONObject(registrationResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                        if (status) {
                            refer_user_id = jsonObject.getString("referral_user_id");
                            r_country_code = jsonObject.getString("r_country_code");
                        } else {
                            refer_user_id = "";
                            r_country_code="";
                            flag = jsonObject.getString("referral_user_id");

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
          //  mProgressDialog.dismiss();
            if (status) {

                referral_status = referral_code.substring(0, 1);
                failCode.setVisibility(View.GONE);
                usedCode.setVisibility(View.GONE);
                successCode.setVisibility(View.VISIBLE);
                prefEditor.putString("apply_u_referral_code", referral_code);
                prefEditor.putString("apply_U_referral_status", referral_status);
                prefEditor.putString("referral_user_id", refer_user_id);
                prefEditor.putString("r_country_code", r_country_code);
                prefEditor.commit();
            } else {
                if (flag.equals("0")) {
                    successCode.setVisibility(View.GONE);
                    usedCode.setVisibility(View.GONE);
                    failCode.setVisibility(View.VISIBLE);
                } else {
                    failCode.setVisibility(View.GONE);
                    successCode.setVisibility(View.GONE);
                    usedCode.setVisibility(View.VISIBLE);

                }
                referral_status = "0";
                prefEditor.putString("apply_u_referral_code", referral_code);
                prefEditor.putString("apply_U_referral_status", referral_status);
                prefEditor.commit();
            }
        }
    }

    private class userCheckEmailsCode extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(RegisterWithEmailActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jceid));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonObj1 = new JSONObject() {
                {
                    try {

                        put("email_id", emailId);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj1);
            checkEmailResponse = serviceAccess.SendHttpPost(Config.URL_CHECKINGEMAILADDRESS, jsonObj1);
            Log.i("resp", "registrationResponse" + checkEmailResponse);


            if (checkEmailResponse.compareTo("") != 0) {
                if (isJSONValid(checkEmailResponse)) {


                    try {

                        jsonObject = new JSONObject(checkEmailResponse);
                        status1 = jsonObject.getBoolean("status");
                        msg1 = jsonObject.getString("message");

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
            mProgressDialog.dismiss();
            if (status1) {
                warning.setVisibility(View.GONE);
                prefEditor.putString("r_with_ep", "2");
                prefEditor.commit();
                Intent intent = new Intent(RegisterWithEmailActivity.this, OTPForRegisterActivity.class);
                intent.putExtra("emailId", emailId);
                intent.putExtra("mobile", "");
                startActivity(intent);

            } else {
                //Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();
                warning.setVisibility(View.VISIBLE);
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
}
