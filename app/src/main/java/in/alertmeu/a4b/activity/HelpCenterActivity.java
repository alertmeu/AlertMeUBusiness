package in.alertmeu.a4b.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import in.alertmeu.a4b.R;

public class HelpCenterActivity extends AppCompatActivity {
    LinearLayout logout;
    static SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    LinearLayout changenumber, faq, contactus, termsprivacy, changepassword,changeMailId,conprivacy;
    TextView appversion;

    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_help_center);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        changenumber = (LinearLayout) findViewById(R.id.changenumber);
        changepassword = (LinearLayout) findViewById(R.id.changepassword);
        changeMailId= (LinearLayout) findViewById(R.id.changeMailId);
        faq = (LinearLayout) findViewById(R.id.faq);
        contactus = (LinearLayout) findViewById(R.id.contactus);
        termsprivacy = (LinearLayout) findViewById(R.id.termsprivacy);
        conprivacy= (LinearLayout) findViewById(R.id.conprivacy);
        appversion = (TextView) findViewById(R.id.appversion);
        if(preferences.getString("primary_login","").equals("1"))
        {
            changeMailId.setVisibility(View.GONE);
            changenumber.setVisibility(View.VISIBLE);
        }
        else
        {
            changenumber.setVisibility(View.GONE);
            changeMailId.setVisibility(View.VISIBLE);
        }
        logout = (LinearLayout) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HelpCenterActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish();

            }
        });
        changenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, InstructionChangeNumberActivity.class);
                startActivity(intent);
            }
        });
        changeMailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, CMActivity.class);
                startActivity(intent);
            }
        });
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, ChangeMyPasswordActivity.class);
                startActivity(intent);
                finish();

            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, FQAActivity.class);
                startActivity(intent);
            }
        });
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpCenterActivity.this, ContactUsActivity.class);
                startActivity(intent);
            }
        });
        termsprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(HelpCenterActivity.this, TremsServiceActivity.class);
                startActivity(intent);*/
                Uri uri = Uri.parse("http://www.alertmeu.com/policy.html"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        conprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(HelpCenterActivity.this, TremsServiceActivity.class);
                startActivity(intent);*/
                Uri uri = Uri.parse("http://www.alertmeu.com/terms.html"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        getVersionInfo();
    }


    //get the current version number and name
    private void getVersionInfo() {
        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appversion.setText(getString(R.string.helpv) + ": " + versionName + " " + getString(R.string.helpb) + " " + versionCode);
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
        Intent setIntent = new Intent(HelpCenterActivity.this, BusinessProfileSettingActivity.class);
        startActivity(setIntent);
        finish();
    }
}
