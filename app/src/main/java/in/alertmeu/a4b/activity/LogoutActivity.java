package in.alertmeu.a4b.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import in.alertmeu.a4b.R;

public class LogoutActivity extends AppCompatActivity {
    static SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TextView logout, cancel;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_logout);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        logout = (TextView) findViewById(R.id.logout);
        cancel = (TextView) findViewById(R.id.cancel);
        logout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.jlogout), Toast.LENGTH_SHORT).show();
                prefEditor.remove("business_user_id");
                prefEditor.remove("business_referral_code");
                prefEditor.remove("primary_login");
                prefEditor.remove("r_with_ep");
                prefEditor.commit();
                Intent intent = new Intent(LogoutActivity.this, LSplashScreenActivity.class);
                startActivity(intent);
                finish();

            //    finishAffinity();
              //  System.exit(0);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(LogoutActivity.this, HelpCenterActivity.class);
        startActivity(setIntent);
        finish();
    }
}
