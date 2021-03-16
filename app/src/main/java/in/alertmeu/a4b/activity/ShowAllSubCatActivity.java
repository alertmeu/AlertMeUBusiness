package in.alertmeu.a4b.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.a4b.JsonUtils.JsonHelper;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.adapter.AllSubCatListAdpter;
import in.alertmeu.a4b.adapter.SubCatListAdpter;
import in.alertmeu.a4b.models.AllSubCatModeDAO;
import in.alertmeu.a4b.models.SubCatModeDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.WebClient;
import in.alertmeu.a4b.view.SubCatDetailsView;

public class ShowAllSubCatActivity extends AppCompatActivity {
    Resources res;
    JSONObject jsonLeadObj;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String myPlaceListResponse = "";
    List<AllSubCatModeDAO> data;
    boolean status;
    AllSubCatListAdpter allSubCatListAdpter;
    RecyclerView allSubCatList;
    ImageView back_arrow1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        res = getResources();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_sub_cat);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        allSubCatList = (RecyclerView) findViewById(R.id.allSubCatList);
        back_arrow1 = (ImageView) findViewById(R.id.back_arrow1);
        data = new ArrayList<>();
        back_arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (AppStatus.getInstance(ShowAllSubCatActivity.this).isOnline()) {
            new getSubCatList().execute();
        } else {

            Toast.makeText(ShowAllSubCatActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
    }

    private class getSubCatList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLSUBCATBUSINESSID, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    JSONObject jsonObject = new JSONObject(myPlaceListResponse);
                                    status = jsonObject.getBoolean("status");
                                    if (status) {
                                        JsonHelper jsonHelper = new JsonHelper();
                                        data = jsonHelper.parseAllSubCatByIdList(myPlaceListResponse);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShowAllSubCatActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowAllSubCatActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (data.size() > 0) {
                allSubCatListAdpter = new AllSubCatListAdpter(ShowAllSubCatActivity.this, data);
                allSubCatList.setAdapter(allSubCatListAdpter);
                allSubCatList.setLayoutManager(new LinearLayoutManager(ShowAllSubCatActivity.this));
                allSubCatListAdpter.notifyDataSetChanged();

            }
        }
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