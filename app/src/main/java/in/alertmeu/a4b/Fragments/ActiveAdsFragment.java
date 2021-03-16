package in.alertmeu.a4b.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.a4b.JsonUtils.JsonHelper;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.adapter.ActiveAdvertisementListAdpter;
import in.alertmeu.a4b.adapter.AdvertisementListAdpter;
import in.alertmeu.a4b.models.AdvertisementDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.WebClient;
import in.alertmeu.a4b.view.UploadImageView;


public class ActiveAdsFragment extends Fragment {
    View v;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj, jsonLeadObj1;
    JSONArray jsonArray;
    String advertisementListResponse = "";
    List<AdvertisementDAO> data;
    ActiveAdvertisementListAdpter advertisementListAdpter;
    RecyclerView recyclerView;
    LinearLayout linkClick;
    Button postAds;
    TextView catHideShow;
    Resources res;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_active_ads, container, false);
        res = getResources();
        preferences = getActivity().getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        linkClick = (LinearLayout) v.findViewById(R.id.linkClick);
        postAds=  (Button) v.findViewById(R.id.postAds);
        catHideShow=(TextView)v.findViewById(R.id.catHideShow);
        data = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.advertisementList);

        if (AppStatus.getInstance(getActivity()).isOnline()) {
            new getAdvertisement().execute();
        } else {

            Toast.makeText(getActivity(),res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        ActiveAdvertisementListAdpter.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    new getAdvertisement().execute();
                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        UploadImageView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    new getAdvertisement().execute();
                } else {
                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        postAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TabsFragmentActivity.class);
                intent.putExtra("active", 2);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission(getActivity())) {
        }
    }
    public static boolean checkPermission(final Context context) {
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
            mProgressDialog = new ProgressDialog(getActivity());
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
                        put("active_status", 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLBUSINESSUSERADVERTISEMENT, jsonLeadObj);
            Log.i("resp", "advertisementListResponse" + advertisementListResponse);
            try {
                if (advertisementListResponse.compareTo("") != 0) {
                    if (isJSONValid(advertisementListResponse)) {

                        getActivity().runOnUiThread(new Runnable() {

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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                            }
                        });

                        return null;
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            }catch (Exception e)
            {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (data.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                linkClick.setVisibility(View.GONE);
                catHideShow.setVisibility(View.GONE);
                advertisementListAdpter = new ActiveAdvertisementListAdpter(getActivity(), data);
                recyclerView.setAdapter(advertisementListAdpter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                // Toast.makeText(getApplicationContext(), "" + data.size(), Toast.LENGTH_SHORT).show();

            } else {
                recyclerView.setVisibility(View.GONE);
                catHideShow.setVisibility(View.VISIBLE);
                linkClick.setVisibility(View.VISIBLE);

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
