package in.alertmeu.a4b.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Constant;

public class ReEnterPassctivity extends AppCompatActivity {
    EditText uPassword;
    Button btnNext;
    String password = "", repassword = "", mobile_no = "",emailId="";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_re_enter_passctivity);
        uPassword = (EditText) findViewById(R.id.uPassword);
        btnNext = (Button) findViewById(R.id.btnNext);
        Intent intent = getIntent();
        password = intent.getStringExtra("password");
        mobile_no = intent.getStringExtra("mobile");
        emailId= intent.getStringExtra("emailId");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repassword = uPassword.getText().toString().trim();
                if (repassword.equals(password)) {
                    Intent intent = new Intent(ReEnterPassctivity.this, AccountSetupLocationActivity.class);
                    intent.putExtra("mobile_no", mobile_no);
                    intent.putExtra("password", repassword);
                    intent.putExtra("emailId", emailId);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpmm), Toast.LENGTH_SHORT).show();
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
