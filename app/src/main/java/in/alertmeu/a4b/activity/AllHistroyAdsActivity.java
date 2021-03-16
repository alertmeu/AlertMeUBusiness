package in.alertmeu.a4b.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.alertmeu.a4b.Fragments.InActiveAdsFragment;
import in.alertmeu.a4b.Fragments.TabsFragmentActivity;
import in.alertmeu.a4b.JsonUtils.JsonHelper;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.adapter.AdvertisementListAdpter;
import in.alertmeu.a4b.models.AdvertisementDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;

public class AllHistroyAdsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj, jsonLeadObj1;
    JSONArray jsonArray;
    String advertisementListResponse = "";
    List<AdvertisementDAO> data;
    AdvertisementListAdpter advertisementListAdpter;
    RecyclerView recyclerView;
    String alertResponse = "", alertSubResponse = "", bc_id = "", sub_id = "";
    ImageView filter;
    LinearLayout linkClick;
    Button postAds;
    TextView catHideShow;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.floating_allhistory_layout);

        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        data = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.advertisementList);
        filter = (ImageView) findViewById(R.id.filter);
        linkClick = (LinearLayout) findViewById(R.id.linkClick);
        postAds = (Button) findViewById(R.id.postAds);
        catHideShow = (TextView) findViewById(R.id.catHideShow);
        if (AppStatus.getInstance(AllHistroyAdsActivity.this).isOnline()) {
            new getAdvertisement().execute();
            //  catHideShow.setVisibility(View.VISIBLE);
            // linkClick.setVisibility(View.VISIBLE);
        } else {

            Toast.makeText(AllHistroyAdsActivity.this, Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        postAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllHistroyAdsActivity.this, TabsFragmentActivity.class);
                intent.putExtra("active", 2);
                startActivity(intent);
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(AllHistroyAdsActivity.this, filter);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_home_all, popup.getMenu());
                //   popup.getMenu().add(preferences.getString("quantity_for_contacts", ""));
                popup.show();
                //registering popup with OnMenuItemClickListener

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        // Toast.makeText(DisplayStudentEditPreActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        if (item.getTitle().equals(res.getString(R.string.action_all))) {
                            catHideShow.setVisibility(View.GONE);
                            advertisementListAdpter = new AdvertisementListAdpter(AllHistroyAdsActivity.this, data);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllHistroyAdsActivity.this));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed

                        } else if (item.getTitle().equals(res.getString(R.string.action_inactive))) {

                            final List<AdvertisementDAO> filteredList = new ArrayList<AdvertisementDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getActive_status().toLowerCase();
                                        if (subject.contains("2")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            advertisementListAdpter = new AdvertisementListAdpter(AllHistroyAdsActivity.this, filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllHistroyAdsActivity.this));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnostarads));
                            } else {
                                catHideShow.setVisibility(View.GONE);
                            }

                        } else if (item.getTitle().equals(res.getString(R.string.action_saved))) {
                            final List<AdvertisementDAO> filteredList = new ArrayList<AdvertisementDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getActive_status().toLowerCase();
                                        if (subject.contains("3")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            advertisementListAdpter = new AdvertisementListAdpter(AllHistroyAdsActivity.this, filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllHistroyAdsActivity.this));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnosavads));
                            } else {
                                catHideShow.setVisibility(View.GONE);
                            }

                        } else if (item.getTitle().equals(res.getString(R.string.action_expired))) {
                            final List<AdvertisementDAO> filteredList = new ArrayList<AdvertisementDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getActive_status().toLowerCase();
                                        if (subject.contains("0")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            advertisementListAdpter = new AdvertisementListAdpter(AllHistroyAdsActivity.this, filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllHistroyAdsActivity.this));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnoinactads));
                            } else {
                                catHideShow.setVisibility(View.GONE);
                            }
                        } else if (item.getTitle().equals(res.getString(R.string.action_active))) {
                            final List<AdvertisementDAO> filteredList = new ArrayList<AdvertisementDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getActive_status().toLowerCase();
                                        if (subject.contains("1")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            advertisementListAdpter = new AdvertisementListAdpter(AllHistroyAdsActivity.this, filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllHistroyAdsActivity.this));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnoactads));
                            } else {
                                catHideShow.setVisibility(View.GONE);
                            }
                        } else if (item.getTitle().equals(res.getString(R.string.action_deactivated))) {
                            final List<AdvertisementDAO> filteredList = new ArrayList<AdvertisementDAO>();
                            if (data != null) {
                                if (data.size() > 0) {


                                    for (int i = 0; i < data.size(); i++) {
                                        String subject = data.get(i).getActive_status().toLowerCase();
                                        if (subject.contains("5")) {
                                            filteredList.add(data.get(i));
                                        }
                                    }
                                }
                            }


                            advertisementListAdpter = new AdvertisementListAdpter(AllHistroyAdsActivity.this, filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllHistroyAdsActivity.this));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnodeacti));
                            } else {
                                catHideShow.setVisibility(View.GONE);
                            }
                        }
                        popup.dismiss();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission1(AllHistroyAdsActivity.this)) {
        }
    }

    public static boolean checkPermission1(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    // Toast.makeText(context, "1", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    //Toast.makeText(context, "2", Toast.LENGTH_LONG).show();
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    private class getAdvertisement extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AllHistroyAdsActivity.this);
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
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLBUSINESSHISTORYADVERTISEMENT, jsonLeadObj);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);
            if (advertisementListResponse.compareTo("") != 0) {
                if (isJSONValid(advertisementListResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {


                                JsonHelper jsonHelper = new JsonHelper();
                                data = jsonHelper.parseAdvertisementList(advertisementListResponse);
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
                linkClick.setVisibility(View.GONE);
                catHideShow.setVisibility(View.GONE);
                advertisementListAdpter = new AdvertisementListAdpter(AllHistroyAdsActivity.this, data);
                recyclerView.setAdapter(advertisementListAdpter);
                recyclerView.setLayoutManager(new LinearLayoutManager(AllHistroyAdsActivity.this));
                // Toast.makeText(getApplicationContext(), "" + data.size(), Toast.LENGTH_SHORT).show();

            } else {
                catHideShow.setVisibility(View.VISIBLE);
                linkClick.setVisibility(View.VISIBLE);
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
