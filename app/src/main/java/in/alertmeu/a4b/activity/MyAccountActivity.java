package in.alertmeu.a4b.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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

public class MyAccountActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    JSONObject jsonLeadObj, jsonLeadObj1, jsonObj;
    String title = "", balanceAmountResponse = "";
    ProgressDialog mProgressDialog;
    String balance_amount = "", amountResponse = "", amt = "", message = "", up = "";
    boolean status;
    TextView balanceAmt;
    LinearLayout addMoney, hiStoryofall, checkTransc;
    String localTime;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    ToggleButton onoffTongleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_my_account);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        System.out.println(localTime + "  TimeZone   ");
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        balanceAmt = (TextView) findViewById(R.id.balanceAmt);
        addMoney = (LinearLayout) findViewById(R.id.addMoney);
        hiStoryofall = (LinearLayout) findViewById(R.id.hiStoryofall);
        checkTransc = (LinearLayout) findViewById(R.id.checkTransc);
        onoffTongleButton = (ToggleButton) findViewById(R.id.onoffTongleButton);

        if (preferences.getString("upgradetopr", "").equals("1")) {
            onoffTongleButton.setChecked(true);
            onoffTongleButton.setText(res.getString(R.string.jon));


        } else {
            onoffTongleButton.setChecked(false);
            onoffTongleButton.setText(res.getString(R.string.joff));

        }
        onoffTongleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onoffTongleButton.isChecked()) {

                    //  Toast.makeText(getApplicationContext(),""+onoffTongleButton.isChecked(),Toast.LENGTH_SHORT).show();
                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        prefEditor.putString("upgradetopr", "1");
                        prefEditor.commit();
                        onoffTongleButton.setTextOn(res.getString(R.string.jon));
                        up = "1";
                        new upgradeAccountRequest().execute();
                    } else {

                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                        prefEditor.putString("upgradetopr", "0");
                        prefEditor.commit();
                        onoffTongleButton.setTextOff(res.getString(R.string.joff));
                        up = "0";
                        new upgradeAccountRequest().execute();
                    } else {

                        Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new dueFeesAvailable().execute();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyAccountActivity.this);
                builder.setMessage(res.getString(R.string.jsarm))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.xsub), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                amt = "" + 1000;
                                //    new amountUpdate().execute();
                                new addMonyRequest().execute();

                            }
                        })
                        .setNegativeButton(res.getString(R.string.homeTakeCPic), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                //  alert.setTitle("Publish Advertisement?");
                alert.show();

            }
        });
        hiStoryofall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, AllHistroyAdsActivity.class);
                startActivity(intent);
            }
        });
        checkTransc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, AllTransactionHistroyActivity.class);
                intent.putExtra("balance", balance_amount);
                startActivity(intent);
            }
        });
    }

    private class dueFeesAvailable extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonObj = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            balanceAmountResponse = serviceAccess.SendHttpPost(Config.URL_GETBALANCEAMOUNT, jsonObj);
            Log.i("resp", "balanceAmountResponse" + balanceAmountResponse);


            if (balanceAmountResponse.compareTo("") != 0) {
                if (isJSONValid(balanceAmountResponse)) {
                    JSONArray leadJsonObj = null;
                    try {
                        JSONObject jObject = new JSONObject(balanceAmountResponse);
                        status = jObject.getBoolean("status");
                        if (status) {
                            /*JSONArray introJsonArray = jObject.getJSONArray("balanceamount");
                            for (int i = 0; i < introJsonArray.length(); i++) {
                                JSONObject introJsonObject = introJsonArray.getJSONObject(i);
                                balance_amount = introJsonObject.getString("balance_amount");
                            }*/
                            balance_amount = jObject.getString("balanceamount");

                        } else {
                            balance_amount = "0";
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            balanceAmt.setText(preferences.getString("currency_sign", "") + balance_amount);


        }
    }

    //
    private class amountUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MyAccountActivity.this);
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
                        put("balance_amount", amt);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            amountResponse = serviceAccess.SendHttpPost(Config.URL_ADDBAMT, jsonLeadObj);
            Log.i("resp", "leadListResponse" + amountResponse);


            if (amountResponse.compareTo("") != 0) {
                if (isJSONValid(amountResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(amountResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(MyAccountActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(MyAccountActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void ar) {
            if (status) {
                // Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                new dueFeesAvailable().execute();

            } else {
                //Toast.makeText(MyAccountActivity.this, message, Toast.LENGTH_LONG).show();
                // Close the progressdialog
                mProgressDialog.dismiss();

            }
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }

    //
    private class addMonyRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MyAccountActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jsarmr));
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
                        put("balance_amount", amt);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            amountResponse = serviceAccess.SendHttpPost(Config.URL_ADDREBAMT, jsonLeadObj);
            Log.i("resp", "leadListResponse" + amountResponse);


            if (amountResponse.compareTo("") != 0) {
                if (isJSONValid(amountResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(amountResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(MyAccountActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(MyAccountActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void ar) {
            if (status) {
                Toast.makeText(MyAccountActivity.this, res.getString(R.string.jadmons), Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                // new dueFeesAvailable().execute();

            } else {
                //Toast.makeText(MyAccountActivity.this, message, Toast.LENGTH_LONG).show();
                // Close the progressdialog
                mProgressDialog.dismiss();

            }
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }

    private class upgradeAccountRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MyAccountActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage(res.getString(R.string.jsarmr));
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
                        put("upgradetopr", up);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            amountResponse = serviceAccess.SendHttpPost(Config.URL_UGACCOUBUSIN, jsonLeadObj);
            Log.i("resp", "leadListResponse" + amountResponse);


            if (amountResponse.compareTo("") != 0) {
                if (isJSONValid(amountResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(amountResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(MyAccountActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(MyAccountActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void ar) {
            mProgressDialog.dismiss();
            if (status) {
              //  Toast.makeText(MyAccountActivity.this, res.getString(R.string.juptosc), Toast.LENGTH_LONG).show();
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

    protected boolean isJSONValid(String addImageLocationResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(addImageLocationResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(addImageLocationResponse);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
