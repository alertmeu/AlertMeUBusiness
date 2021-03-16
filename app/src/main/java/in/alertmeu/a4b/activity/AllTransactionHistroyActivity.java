package in.alertmeu.a4b.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.alertmeu.a4b.JsonUtils.JsonHelper;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.adapter.AdvertisementListAdpter;
import in.alertmeu.a4b.adapter.TransactionHistoryListAdpter;
import in.alertmeu.a4b.models.AdvertisementDAO;
import in.alertmeu.a4b.models.TransactionHistoryDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;

public class AllTransactionHistroyActivity extends AppCompatActivity {
    RecyclerView transactiontList;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj;
    JSONArray jsonArray;
    String advertisementListResponse = "";
    List<TransactionHistoryDAO> data;
    TransactionHistoryListAdpter transactionHistoryListAdpter;
    TextView availBalTxt;
    Button addMoney, AdHistory;
    ImageView filter;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_all_transaction_histroy);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        transactiontList = (RecyclerView) findViewById(R.id.transactiontList);
        final Intent intent = getIntent();
        addMoney = (Button) findViewById(R.id.addMoney);
        AdHistory = (Button) findViewById(R.id.AdHistory);
        filter = (ImageView) findViewById(R.id.filter);
        data=new ArrayList<>();
        availBalTxt = (TextView) findViewById(R.id.availBalTxt);
        availBalTxt.setText(preferences.getString("currency_sign","")+intent.getStringExtra("balance"));
        if (AppStatus.getInstance(AllTransactionHistroyActivity.this).isOnline()) {
            new getAdvertisement().execute();
        } else {

            Toast.makeText(AllTransactionHistroyActivity.this, Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        AdHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllTransactionHistroyActivity.this, AllHistroyAdsActivity.class);
                startActivity(intent);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(AllTransactionHistroyActivity.this, filter);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_transc, popup.getMenu());
                //   popup.getMenu().add(preferences.getString("quantity_for_contacts", ""));
                popup.show();
                //registering popup with OnMenuItemClickListener

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        // Toast.makeText(DisplayStudentEditPreActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        if (item.getTitle().equals(res.getString(R.string.action_all))) {
                            transactionHistoryListAdpter = new TransactionHistoryListAdpter(AllTransactionHistroyActivity.this, data);
                            transactiontList.setAdapter(transactionHistoryListAdpter);
                            transactiontList.setLayoutManager(new LinearLayoutManager(AllTransactionHistroyActivity.this));
                            transactionHistoryListAdpter.notifyDataSetChanged();  // data set changed

                        } else if (item.getTitle().equals(res.getString(R.string.action_funds_added))) {

                            final List<TransactionHistoryDAO> filteredList = new ArrayList<TransactionHistoryDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getDescription().toLowerCase();
                                        if (subject.contains("funds added")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            transactionHistoryListAdpter = new TransactionHistoryListAdpter(AllTransactionHistroyActivity.this, filteredList);
                            transactiontList.setAdapter(transactionHistoryListAdpter);
                            transactiontList.setLayoutManager(new LinearLayoutManager(AllTransactionHistroyActivity.this));
                            transactionHistoryListAdpter.notifyDataSetChanged();  // data set changed


                        } else if (item.getTitle().equals(res.getString(R.string.action_advertisements))) {
                            final List<TransactionHistoryDAO> filteredList = new ArrayList<TransactionHistoryDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getDescription().toLowerCase();
                                        if (subject.contains("ad # ")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            transactionHistoryListAdpter = new TransactionHistoryListAdpter(AllTransactionHistroyActivity.this, filteredList);
                            transactiontList.setAdapter(transactionHistoryListAdpter);
                            transactiontList.setLayoutManager(new LinearLayoutManager(AllTransactionHistroyActivity.this));
                            transactionHistoryListAdpter.notifyDataSetChanged();  // data set changed

                        }
                        else if (item.getTitle().equals(res.getString(R.string.action_refund))) {
                            final List<TransactionHistoryDAO> filteredList = new ArrayList<TransactionHistoryDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getDescription().toLowerCase();
                                        if (subject.contains("refund # ")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            transactionHistoryListAdpter = new TransactionHistoryListAdpter(AllTransactionHistroyActivity.this, filteredList);
                            transactiontList.setAdapter(transactionHistoryListAdpter);
                            transactiontList.setLayoutManager(new LinearLayoutManager(AllTransactionHistroyActivity.this));
                            transactionHistoryListAdpter.notifyDataSetChanged();  // data set changed

                        }

                        popup.dismiss();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
    }

    private class getAdvertisement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AllTransactionHistroyActivity.this);
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
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLBUSINESSHISTORYTRANSACTION, jsonLeadObj);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);
            if (advertisementListResponse.compareTo("") != 0) {
                if (isJSONValid(advertisementListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {


                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parsetransactionhistoryList(advertisementListResponse);
                                jsonArray = new JSONArray(advertisementListResponse);

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
                            //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (data.size() > 0) {
                transactionHistoryListAdpter = new TransactionHistoryListAdpter(AllTransactionHistroyActivity.this, data);
                transactiontList.setAdapter(transactionHistoryListAdpter);
                transactiontList.setLayoutManager(new LinearLayoutManager(AllTransactionHistroyActivity.this));
                // Toast.makeText(getApplicationContext(), "" + data.size(), Toast.LENGTH_SHORT).show();

            } else {

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
