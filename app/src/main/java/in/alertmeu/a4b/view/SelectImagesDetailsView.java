package in.alertmeu.a4b.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.alertmeu.a4b.JsonUtils.JsonHelper;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.adapter.AdsImagesListAdpter;
import in.alertmeu.a4b.adapter.SubCatListAdpter;
import in.alertmeu.a4b.models.AdsImagesModeDAO;
import in.alertmeu.a4b.models.SubCatModeDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.WebClient;


public class SelectImagesDetailsView extends DialogFragment {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    RecyclerView mainCatList;
    JSONObject jsonLeadObj, jsonObjectSync, syncJsonObject;
    JSONArray jsonArray, jsonArraySync;
    String myPlaceListResponse = "", syncDataesponse = "", maincat_id = "";
    List<AdsImagesModeDAO> data;
    AdsImagesListAdpter subCatListAdpter;
    LinearLayout showhide;
    ProgressDialog mProgressDialog;
    LinearLayout btnNext;
    Resources res;
    boolean status;
    ArrayList<String> nameArrayList;
    private static Listener mListener;
    ImageView back_arrow1;
    TextView title;
    CheckBox chkAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View registerView = inflater.inflate(R.layout.dialog_ads_images_category, null);
        context = getActivity();
        res = getResources();
        Window window = getDialog().getWindow();
        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = 50;
        window.setAttributes(params);
        preferences = getActivity().getSharedPreferences("Prefrence", getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        mainCatList = (RecyclerView) registerView.findViewById(R.id.mainCatList);
        btnNext = (LinearLayout) registerView.findViewById(R.id.btnNext);
        back_arrow1 = (ImageView) registerView.findViewById(R.id.back_arrow1);
        title = (TextView) registerView.findViewById(R.id.title);
        showhide = (LinearLayout) registerView.findViewById(R.id.showhide);
        title.setText(preferences.getString("main_id_img_ti", ""));
        data = new ArrayList<>();
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            new getSubCatList().execute();
        } else {

            Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        back_arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        AdsImagesListAdpter.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
               // Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();
                mListener.messageReceived(messageText);
                dismiss();
            }
        });
        return registerView;
    }


    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_DOWN) {
                        update();
                        return true;
                    } else {
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }

    private void update() {
        dismiss();
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
                        put("main_id", preferences.getString("main_id_img", ""));
                        put("sub_id", preferences.getString("sub_id_img", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLADSIMAGES, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            if (myPlaceListResponse.compareTo("") != 0) {
                if (isJSONValid(myPlaceListResponse)) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    JSONObject jsonObject = new JSONObject(myPlaceListResponse);
                                    status = jsonObject.getBoolean("status");
                                    JsonHelper jsonHelper = new JsonHelper();
                                    data = jsonHelper.parseAllAdsImgIdList(myPlaceListResponse);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (data.size() > 0) {
                subCatListAdpter = new AdsImagesListAdpter(getActivity(), data);
                mainCatList.setAdapter(subCatListAdpter);
                mainCatList.setLayoutManager(new LinearLayoutManager(getActivity()));
                subCatListAdpter.notifyDataSetChanged();
            } else {

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

    public static void bindListener(Listener listener) {
        mListener = listener;
    }

}