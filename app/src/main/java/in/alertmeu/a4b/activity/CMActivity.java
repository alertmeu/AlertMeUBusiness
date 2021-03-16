package in.alertmeu.a4b.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Locale;
import java.util.regex.Pattern;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;

public class CMActivity extends AppCompatActivity {
    EditText new_mobile;
    String old_number = "", new_number = "", verifyMobileResponse = "";
    JSONObject jsonObj;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TextView old_mobile, warning, tnp;
    boolean status;
    Button btnNext;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_c_m);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        old_mobile = (TextView) findViewById(R.id.old_mobile);
        new_mobile = (EditText) findViewById(R.id.new_mobile);
        warning = (TextView) findViewById(R.id.warning);
        btnNext = (Button) findViewById(R.id.btnNext);
        tnp = (TextView) findViewById(R.id.tnp);
        old_mobile.setText(preferences.getString("email_id", ""));
        tnp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(CMActivity.this, TremsServiceActivity.class);
                    startActivity(intent);

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    new_number = new_mobile.getText().toString().trim();
                    if (validate(new_number)) {
                        Intent intent = new Intent(CMActivity.this, OTPforChangeMActivity.class);
                        intent.putExtra("new_email", new_number);
                        startActivity(intent);
                    }

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void verifyMobileNumber() {


        jsonObj = new JSONObject() {
            {
                try {
                    put("email_id", old_number);
                    put("business_user_id", preferences.getString("business_user_id", ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread objectThread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                WebClient serviceAccess = new WebClient();
                verifyMobileResponse = serviceAccess.SendHttpPost(Config.URL_GETAVAILABLECHANGEBUSINESSEMAILID, jsonObj);
                Log.i("loginResponse", "verifyMobileResponse" + verifyMobileResponse);
                final Handler handler = new Handler(Looper.getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() { // This thread runs in the UI
                            @Override
                            public void run() {
                                if (verifyMobileResponse.compareTo("") == 0) {

                                } else {

                                    try {
                                        JSONObject jObject = new JSONObject(verifyMobileResponse);
                                        status = jObject.getBoolean("status");
                                        if (status) {
                                            // Toast.makeText(getApplicationContext(), "Already Exist", Toast.LENGTH_LONG).show();
                                            warning.setVisibility(View.GONE);
                                            btnNext.setVisibility(View.VISIBLE);

                                        } else {
                                            warning.setVisibility(View.VISIBLE);
                                            btnNext.setVisibility(View.GONE);

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
    }

    private boolean validate(String emailob) {
        boolean isValidate = false;
        if (emailob.trim().equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jcea), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (!validateEmail(emailob)) {
            if (!emailob.equals("")) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.jceav), Toast.LENGTH_LONG).show();
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