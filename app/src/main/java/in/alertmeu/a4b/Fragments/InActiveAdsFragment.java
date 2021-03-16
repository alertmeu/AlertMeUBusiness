package in.alertmeu.a4b.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.a4b.JsonUtils.JsonHelper;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.HomePageActivity;
import in.alertmeu.a4b.adapter.AdvertisementListAdpter;
import in.alertmeu.a4b.models.AdvertisementDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;


public class InActiveAdsFragment extends Fragment {
    View v;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.floating_inactive_layout, container, false);
        res = getResources();
        preferences = getActivity().getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        data = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.advertisementList);
        filter = (ImageView) v.findViewById(R.id.filter);
        linkClick = (LinearLayout) v.findViewById(R.id.linkClick);
        postAds = (Button) v.findViewById(R.id.postAds);
        catHideShow = (TextView) v.findViewById(R.id.catHideShow);
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            new getAdvertisement().execute();
        } else {

            Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        postAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TabsFragmentActivity.class);
                intent.putExtra("active", 2);
                startActivity(intent);
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(getActivity(), filter);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_home, popup.getMenu());
                //   popup.getMenu().add(preferences.getString("quantity_for_contacts", ""));
                popup.show();
                //registering popup with OnMenuItemClickListener

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        // Toast.makeText(DisplayStudentEditPreActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        if (item.getTitle().equals(res.getString(R.string.action_all))) {
                            catHideShow.setVisibility(View.GONE);
                            advertisementListAdpter = new AdvertisementListAdpter(getActivity(), data);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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


                            advertisementListAdpter = new AdvertisementListAdpter(getActivity(), filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnostarads));
                            }
                            else
                            {
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


                            advertisementListAdpter = new AdvertisementListAdpter(getActivity(), filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnosavads));
                            }
                            else
                            {
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


                            advertisementListAdpter = new AdvertisementListAdpter(getActivity(), filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnoinactads));
                            }
                            else
                            {
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


                            advertisementListAdpter = new AdvertisementListAdpter(getActivity(), filteredList);
                            recyclerView.setAdapter(advertisementListAdpter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            advertisementListAdpter.notifyDataSetChanged();  // data set changed
                            if (filteredList.size() == 0) {
                                catHideShow.setVisibility(View.VISIBLE);
                                catHideShow.setText(res.getString(R.string.jnodeacti));
                            }
                            else
                            {
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
        return v;
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
                        put("active_status", 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            advertisementListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLBUSINESSNOTACTIVEUSERADVERTISEMENT, jsonLeadObj);
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
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (data.size() > 0) {
                linkClick.setVisibility(View.GONE);
                catHideShow.setVisibility(View.GONE);
                filter.setVisibility(View.VISIBLE);
                advertisementListAdpter = new AdvertisementListAdpter(getActivity(), data);
                recyclerView.setAdapter(advertisementListAdpter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                // Toast.makeText(getApplicationContext(), "" + data.size(), Toast.LENGTH_SHORT).show();

            } else {
                filter.setVisibility(View.GONE);
                catHideShow.setVisibility(View.VISIBLE);
                catHideShow.setText(res.getString(R.string.jnoinactads));
                //  linkClick.setVisibility(View.VISIBLE);
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
