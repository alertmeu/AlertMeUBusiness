package in.alertmeu.a4b.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.alertmeu.a4b.JsonUtils.JsonHelper;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.adapter.AllSubCatListAdpter;
import in.alertmeu.a4b.adapter.AllSubPricListAdpter;
import in.alertmeu.a4b.adapter.SubCatListAdpter;
import in.alertmeu.a4b.models.AllPriceListSubCatModeDAO;
import in.alertmeu.a4b.models.AllSubCatModeDAO;
import in.alertmeu.a4b.models.MainCatModeDAO;
import in.alertmeu.a4b.models.MainPriListModeDAO;
import in.alertmeu.a4b.models.SubCatModeDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.CompressFile;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.ExifUtil;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.Utility;
import in.alertmeu.a4b.utils.WebClient;
import in.alertmeu.a4b.view.MainCatEntryView;
import in.alertmeu.a4b.view.UDMainCatEntryView;

import static android.graphics.BitmapFactory.decodeFile;

public class AddModifyPriceListCategoryActivity extends AppCompatActivity implements AllSubPricListAdpter.OnClickDetails {
    ImageView addCatName, modifyCatName, editCatName;
    Resources res;
    String mainCatNameResponse = "", mainPriListId = "", mainPriListName = "", subbc_id = "", maincat_id = "", subcategory_name = "", image_path = "";
    ProgressDialog mProgressDialog;
    private JSONObject jsonLeadObj, jsonObj1;
    ArrayList<MainPriListModeDAO> mainCatModeDAOArrayList;
    Spinner displayMainCategoryName;
    boolean status;
    String message = "", flag = "", updateResponse = "", myPlaceListResponse = "";
    List<AllPriceListSubCatModeDAO> data;
    SubCatListAdpter subCatListAdpter;
    RecyclerView sCatList;
    LinearLayout editfhideshow, fhideshow, closeForm, editcloseForm;
    static SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private FloatingActionButton fab;
    ToggleButton onoffTongleButton;
    AllSubPricListAdpter allSubPricListAdpter;
    RecyclerView allSubCatList;

    ImageView profilePic, editprofilePic;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final String TAG = AddSubPriceListActivity.class.getSimpleName();
    //photo
    //new camera images
    public static final int MEDIA_TYPE_IMAGE = 1;
    String userChoosenTask = "";
    int PICK_IMAGE_MULTIPLE = 1;
    private int REQUEST_CAMERA = 0;
    private Uri fileUri; // file url to store image/video
    int cf = 0;
    static String fileName = "";
    static File destination;
    String imageEncoded;
    static List<String> imagesEncodedList;
    // Declare variables
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    ArrayList<Uri> mArrayUri;
    public ArrayList<String> map = new ArrayList<String>();
    Button submit, edit, delete;
    int count = 0;
    EditText titleEdtTxt, descEdtTxt, priceEdtTxt, edittitleEdtTxt, editdescEdtTxt, editpriceEdtTxt;
    MultipartEntity entity;
    private ProgressDialog dialog;
    JSONObject jsonLeadObjReq;
    String addRequestAttachResponse = "", discontinueResponse = "", id = "", title = "", desc = "", price = "", edittitle = "", editdesc = "", editprice = "", editimagepath = "", sublistid = "";
    private static Listener mListener;
    JSONArray jsonArray;
    int flagau = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.floating_allsubcat);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        allSubCatList = (RecyclerView) findViewById(R.id.sCatList);
        addCatName = (ImageView) findViewById(R.id.addCatName);
        modifyCatName = (ImageView) findViewById(R.id.modifyCatName);
        fhideshow = (LinearLayout) findViewById(R.id.fhideshow);
        closeForm = (LinearLayout) findViewById(R.id.closeForm);
        editfhideshow = (LinearLayout) findViewById(R.id.editfhideshow);
        editcloseForm = (LinearLayout) findViewById(R.id.editcloseForm);
        editCatName = (ImageView) findViewById(R.id.editCatName);
        mainCatModeDAOArrayList = new ArrayList<>();
        data = new ArrayList<>();
        displayMainCategoryName = (Spinner) findViewById(R.id.displayMainCategoryName);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Intent intent = getIntent();
        subbc_id = intent.getStringExtra("subbc_id");
        maincat_id = intent.getStringExtra("maincat_id");
        subcategory_name = intent.getStringExtra("subcategory_name");
        image_path = intent.getStringExtra("image_path");
        fileName = "";
        profilePic = (ImageView) findViewById(R.id.profilePic);
        submit = (Button) findViewById(R.id.submit);
        titleEdtTxt = (EditText) findViewById(R.id.titleEdtTxt);
        descEdtTxt = (EditText) findViewById(R.id.descEdtTxt);
        priceEdtTxt = (EditText) findViewById(R.id.priceEdtTxt);
        editprofilePic = (ImageView) findViewById(R.id.editprofilePic);
        edit = (Button) findViewById(R.id.edit);
        delete = (Button) findViewById(R.id.delete);
        edittitleEdtTxt = (EditText) findViewById(R.id.edittitleEdtTxt);
        editdescEdtTxt = (EditText) findViewById(R.id.editdescEdtTxt);
        editpriceEdtTxt = (EditText) findViewById(R.id.editpriceEdtTxt);
        //  Toast.makeText(getApplicationContext(), "" + subbc_id, Toast.LENGTH_SHORT).show();
        addCatName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    prefEditor.putString("subbc_id", subbc_id);
                    prefEditor.commit();
                    MainCatEntryView mainCatEntryView = new MainCatEntryView();
                    mainCatEntryView.show(getSupportFragmentManager(), "mainCatEntryView");

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            synchronized (this) {
                new initMainCatNameSpinner().execute();
                //new getSubCatList().execute();
            }
        } else {

            Toast.makeText(getApplicationContext(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }
        MainCatEntryView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                new initMainCatNameSpinner().execute();
            }
        });
        UDMainCatEntryView.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                new initMainCatNameSpinner().execute();
            }
        });
        AddModifyPriceListCategoryActivity.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(AddModifyPriceListCategoryActivity.this).isOnline()) {
                    fhideshow.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    new getSubCatList().execute();
                } else {

                    Toast.makeText(AddModifyPriceListCategoryActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mainPriListId.equals("0")) {
                  /*  Intent intent = new Intent(AddModifyPriceListCategoryActivity.this, AddSubPriceListActivity.class);
                    intent.putExtra("subbc_id", subbc_id);
                    intent.putExtra("maincat_id", maincat_id);
                    intent.putExtra("mainPriListId", mainPriListId);
                    intent.putExtra("mainPriListName", mainPriListName);
                    intent.putExtra("subcategory_name", subcategory_name);
                    intent.putExtra("image_path", image_path);
                    startActivity(intent);*/

                    fab.setVisibility(View.GONE);
                    fhideshow.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getApplicationContext(), "Please select main Category", Toast.LENGTH_SHORT).show();
                }
            }
        });

        closeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fhideshow.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    flagau = 0;
                    if (checkAndRequestPermissions()) {
                        selectImage();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleEdtTxt.getText().toString().trim();
                desc = descEdtTxt.getText().toString();
                price = priceEdtTxt.getText().toString();
                if (validate(fileName, title)) {
                    flagau = 0;
                    if (mArrayUri.size() > 0) {
                        // Toast.makeText(AddSubPriceListActivity.this, "" + mArrayUri.size(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < imagesEncodedList.size(); i++) {
                            map.add(imagesEncodedList.get(i).toString());
                        }
                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                    }
                } else {

                }
            }
        });
        editCatName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    prefEditor.putString("mainPriListId", mainPriListId);
                    prefEditor.putString("mainPriListName", mainPriListName);
                    prefEditor.commit();
                    UDMainCatEntryView udMainCatEntryView = new UDMainCatEntryView();
                    udMainCatEntryView.show(getSupportFragmentManager(), "udMainCatEntryView");

                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        editcloseForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editfhideshow.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

        editprofilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    if (editimagepath.contains("http")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddModifyPriceListCategoryActivity.this);
                        builder.setMessage(res.getString(R.string.jduwi))
                                .setCancelable(false)
                                .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        if (AppStatus.getInstance(AddModifyPriceListCategoryActivity.this).isOnline()) {
                                            new deleteImage().execute();
                                        }


                                    }
                                })
                                .setNegativeButton(res.getString(R.string.jno), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();

                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        // alert.setTitle("Publish Advertisement?");
                        alert.show();
                    } else {
                        flagau = 1;
                        if (checkAndRequestPermissions()) {
                            selectImage();
                        }
                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddModifyPriceListCategoryActivity.this);
                builder.setMessage(res.getString(R.string.jduwd))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (AppStatus.getInstance(AddModifyPriceListCategoryActivity.this).isOnline()) {
                                    new deleteDateLocation().execute();
                                }


                            }
                        })
                        .setNegativeButton(res.getString(R.string.jno), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                // alert.setTitle("Publish Advertisement?");
                alert.show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editimagepath.contains("http")) {
                    edittitle = edittitleEdtTxt.getText().toString();
                    editdesc = editdescEdtTxt.getText().toString();
                    editprice = editpriceEdtTxt.getText().toString();
                    if (validate(fileName, edittitle)) {
                        flagau = 1;
                        if (mArrayUri.size() > 0) {
                            // Toast.makeText(AddSubPriceListActivity.this, "" + mArrayUri.size(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < imagesEncodedList.size(); i++) {
                                map.add(imagesEncodedList.get(i).toString());
                            }
                            new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                        }
                    }

                } else {
                    edittitle = edittitleEdtTxt.getText().toString();
                    editdesc = editdescEdtTxt.getText().toString();
                    editprice = editpriceEdtTxt.getText().toString();
                    if (validate(editimagepath, edittitle)) {
                        jsonLeadObjReq = new JSONObject() {
                            {
                                try {

                                    put("image_path", editimagepath);
                                    put("title", edittitle);
                                    put("description", editdesc);
                                    put("rate", editprice);
                                    put("link", "");
                                    put("id", sublistid);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("json exception", "json exception" + e);
                                }
                            }
                        };
                        Thread objectThread = new Thread(new Runnable() {
                            public void run() {
                                // TODO Auto-generated method stub

                                final WebClient serviceAccess = new WebClient();
                                updateResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEREQUESTATTACHMENTPR, jsonLeadObjReq);
                                Log.i("resp", "syncDataesponse" + updateResponse);
                                final Handler handler = new Handler(Looper.getMainLooper());
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() { // This thread runs in the UI
                                            @Override
                                            public void run() {
                                                if (updateResponse.compareTo("") != 0) {

                                                    try {
                                                        JSONObject jObject = new JSONObject(updateResponse);
                                                        status = jObject.getBoolean("status");
                                                        if (status) {
                                                            editfhideshow.setVisibility(View.GONE);
                                                            new getSubCatList().execute();
                                                        }
                                                    } catch (JSONException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                };

                                new Thread(runnable).start();
                            }
                        });
                        objectThread.start();
                    }
                }
            }
        });

    }

    @Override
    public void onDeetails(int postion) {
        fab.setVisibility(View.GONE);
        editfhideshow.setVisibility(View.VISIBLE);
        data.get(postion).getDescription();
        try {
            Picasso.get().load(data.get(postion).getImage_path()).into(editprofilePic);
        } catch (Exception e) {

        }
        sublistid = data.get(postion).getId();
        editimagepath = data.get(postion).getImage_path();
        edittitleEdtTxt.setText(data.get(postion).getTitle());
        editdescEdtTxt.setText(data.get(postion).getDescription());
        editpriceEdtTxt.setText(data.get(postion).getRate());
        //Toast.makeText(getApplicationContext(), "Hello" + data.get(postion).getDescription(), Toast.LENGTH_SHORT).show();

    }

    private class deleteDateLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AddModifyPriceListCategoryActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Deleting ...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("id", sublistid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            discontinueResponse = serviceAccess.SendHttpPost(Config.URL_DELETESUBMAINPRICELISTBYBUSINESS, jsonLeadObj);
            Log.i("resp", "discontinueResponse" + discontinueResponse);
            if (discontinueResponse.compareTo("") != 0) {
                status = true;
                if (isJSONValid(discontinueResponse)) {
                    try {
                        JSONObject jObject = new JSONObject(discontinueResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        jsonArray = new JSONArray(discontinueResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    // Toast.makeText(context, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {
                status = false;
                // Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {
                Toast.makeText(AddModifyPriceListCategoryActivity.this, message, Toast.LENGTH_LONG).show();
                editfhideshow.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                sublistid = "";
                editimagepath = "";
                edittitleEdtTxt.setText("");
                editdescEdtTxt.setText("");
                editpriceEdtTxt.setText("");
                new getSubCatList().execute();

            } else {
                Toast.makeText(AddModifyPriceListCategoryActivity.this, message, Toast.LENGTH_LONG).show();


            }

        }
    }

    private class deleteImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AddModifyPriceListCategoryActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Deleting ...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("id", sublistid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            discontinueResponse = serviceAccess.SendHttpPost(Config.URL_DELETESUBMAINIMAGEBYBUSINESS, jsonLeadObj);
            Log.i("resp", "discontinueResponse" + discontinueResponse);
            if (discontinueResponse.compareTo("") != 0) {
                status = true;
                if (isJSONValid(discontinueResponse)) {
                    try {
                        JSONObject jObject = new JSONObject(discontinueResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        jsonArray = new JSONArray(discontinueResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    // Toast.makeText(context, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {
                status = false;
                // Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {
                Toast.makeText(AddModifyPriceListCategoryActivity.this, message, Toast.LENGTH_LONG).show();
                editimagepath = "";
                editprofilePic.setImageBitmap(null);
                editprofilePic.setImageResource(R.drawable.contact_img);
                new getSubCatList().execute();
            } else {
                Toast.makeText(AddModifyPriceListCategoryActivity.this, message, Toast.LENGTH_LONG).show();


            }

        }
    }

    private class initMainCatNameSpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AddModifyPriceListCategoryActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
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
                        put("subbc_id", subbc_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            mainCatNameResponse = serviceAccess.SendHttpPost(Config.URL_GETALLMAINPRICELISTBYBID, jsonLeadObj);
            Log.i("resp", "leadListResponse" + mainCatNameResponse);
            mainCatModeDAOArrayList.clear();
            mainCatModeDAOArrayList.add(new MainPriListModeDAO("0", "Select Category", "0"));

            if (mainCatNameResponse.compareTo("") != 0) {
                if (isJSONValid(mainCatNameResponse)) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                JSONObject jsonObject = new JSONObject(mainCatNameResponse);
                                status = jsonObject.getBoolean("status");
                                if (status) {
                                    if (!jsonObject.isNull("dataList")) {
                                        JSONArray LeadSourceJsonObj = jsonObject.getJSONArray("dataList");

                                        for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                            JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);
                                            mainCatModeDAOArrayList.add(new MainPriListModeDAO(json_data.getString("id"), json_data.getString("category_name"), json_data.getString("status")));

                                        }
                                    }
                                }

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
                            //    Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //   Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            ArrayAdapter<MainPriListModeDAO> adapter = new ArrayAdapter<MainPriListModeDAO>(AddModifyPriceListCategoryActivity.this, android.R.layout.simple_spinner_dropdown_item, mainCatModeDAOArrayList);
            // MyAdapter adapter = new MyAdapter(StudentsListActivity.this,R.layout.spinner_item,locationlist);
            displayMainCategoryName.setAdapter(adapter);
            displayMainCategoryName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                    MainPriListModeDAO LeadSource = (MainPriListModeDAO) parent.getSelectedItem();
                    // Toast.makeText(getApplicationContext(), "Source ID: " + LeadSource.getId() + ",  Source Name : " + LeadSource.getItemName(), Toast.LENGTH_SHORT).show();
                    mainPriListId = LeadSource.getId();
                    new getSubCatList().execute();
                    mainPriListName = LeadSource.getCategory_name();
                    if (!mainPriListId.equals("0")) {
                        fab.setVisibility(View.VISIBLE);
                        editCatName.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.GONE);
                        editCatName.setVisibility(View.GONE);
                    }
                  /*  if (LeadSource.getStatus().equals("1")) {
                        onoffTongleButton.setChecked(true);
                    } else {
                        onoffTongleButton.setChecked(false);
                    }*/

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }


            });


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
                        put("mainPriListId", mainPriListId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();

            Log.i("json", "json" + jsonLeadObj);
            myPlaceListResponse = serviceAccess.SendHttpPost(Config.URL_GETALLSUBPRICELISTBUSINESSID, jsonLeadObj);
            Log.i("resp", "myPlaceListResponse" + myPlaceListResponse);
            data.clear();
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
                                        data = jsonHelper.parseAllSubPriListByIdList(myPlaceListResponse);
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
                            Toast.makeText(AddModifyPriceListCategoryActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddModifyPriceListCategoryActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (data.size() > 0) {
                allSubCatList.setLayoutManager(new LinearLayoutManager(AddModifyPriceListCategoryActivity.this));
                allSubPricListAdpter = new AllSubPricListAdpter(AddModifyPriceListCategoryActivity.this, data, AddModifyPriceListCategoryActivity.this);
                allSubCatList.setAdapter(allSubPricListAdpter);
                allSubPricListAdpter.notifyDataSetChanged();

            } else {
                allSubCatList.setLayoutManager(new LinearLayoutManager(AddModifyPriceListCategoryActivity.this));
                allSubPricListAdpter = new AllSubPricListAdpter(AddModifyPriceListCategoryActivity.this, data, AddModifyPriceListCategoryActivity.this);
                allSubCatList.setAdapter(allSubPricListAdpter);
                allSubPricListAdpter.notifyDataSetChanged();
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

    //

    public boolean validate(String image_name, String title) {
        boolean isValidate = false;
        if (image_name.equals("")) {
            Toast.makeText(AddModifyPriceListCategoryActivity.this, res.getString(R.string.jpstsui), Toast.LENGTH_LONG).show();
        } else if (title.equals("")) {
            Toast.makeText(AddModifyPriceListCategoryActivity.this, "Please Enter Title", Toast.LENGTH_LONG).show();
        } else {
            isValidate = true;
        }
        return isValidate;
    }

    //profile photo
    private void selectImage() {
        final CharSequence[] items = {res.getString(R.string.jtp), res.getString(R.string.jtcfl)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddModifyPriceListCategoryActivity.this);
        builder.setTitle(res.getString(R.string.japh));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddModifyPriceListCategoryActivity.this);

                if (items[item].equals(res.getString(R.string.jtp))) {
                    userChoosenTask = res.getString(R.string.jtp);
                    if (result)
                        cf = 1;
                    cameraIntent();


                } else if (items[item].equals(res.getString(R.string.jtcfl))) {
                    userChoosenTask = res.getString(R.string.jtcfl);
                    if (result)
                        cf = 2;
                    galleryIntent();


                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // pickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, PICK_IMAGE_MULTIPLE);


    }

    private void cameraIntent() {

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    mArrayUri = new ArrayList<Uri>();
                    imagesEncodedList = new ArrayList<String>();
                    mArrayUri.add(mImageUri);
                    if (!mImageUri.toString().contains("mediakey")) {
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);
                        cursor.close();
                        Log.v("LOG_TAG", "imageEncoded" + imageEncoded);
                        Log.v("LOG_TAG", "Selected Images" + mImageUri);
                        // Create a String array for FilePathStrings
                        FilePathStrings = new String[1];
                        // Create a String array for FileNameStrings
                        FileNameStrings = new String[1];
                        imagesEncodedList.add(getPathFromUri(AddModifyPriceListCategoryActivity.this, mImageUri));
                        FileNameStrings[0] = getFileName(mImageUri);
                        fileName = getFileName(mImageUri);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(AddModifyPriceListCategoryActivity.this, mImageUri), options);
                        Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(AddModifyPriceListCategoryActivity.this, mImageUri), bitmap);

                        if (flagau == 0) {
                            profilePic.setImageBitmap(orientedBitmap);
                        } else {
                            editprofilePic.setImageBitmap(orientedBitmap);
                        }
                        /*if (mArrayUri.size() > 0) {
                            for (int i = 0; i < imagesEncodedList.size(); i++) {
                                map.add(imagesEncodedList.get(i).toString());
                            }
                            new BusinessProfileSettingActivity.ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                            if (!tempimageName.equals("")) {
                                new BusinessProfileSettingActivity.deleteDisContinue().execute();
                            }
                        }*/
                    } else {
                        Toast.makeText(AddModifyPriceListCategoryActivity.this, res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            } else if (requestCode == REQUEST_CAMERA) {
                //  onCaptureImageResult(data);

                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity(true);
                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            res.getString(R.string.junc), Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            res.getString(R.string.jsunc), Toast.LENGTH_SHORT)
                            .show();
                }


            } else {
                Toast.makeText(this, res.getString(R.string.jeunp),
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Exception", "" + e);
            Toast.makeText(this, res.getString(R.string.jsw), Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //new camera images


    private void launchUploadActivity(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {

            mArrayUri = new ArrayList<Uri>();
            imagesEncodedList = new ArrayList<String>();
            mArrayUri.add(Uri.fromFile(destination));
            BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            //1
            File myFile = new File(fileUri.getPath());
            int imageRotation = getImageRotation(myFile);
            if (imageRotation != 0)
                bitmap = getBitmapRotatedByDegree(bitmap, imageRotation);

            FilePathStrings = new String[1];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[1];
            imagesEncodedList.add("" + destination);// imagesEncodedList.add(destination);
            FilePathStrings[0] = "" + destination;
            // Get the name image file
            FileNameStrings[0] = fileName;
            fileName = getFileName(Uri.fromFile(destination));
            if (flagau == 0) {
                profilePic.setImageBitmap(bitmap);
            } else {
                editprofilePic.setImageBitmap(bitmap);
            }
            //  Toast.makeText(getApplicationContext(), "Uploading image", Toast.LENGTH_SHORT).show();
            /*if (mArrayUri.size() > 0) {
                for (int i = 0; i < imagesEncodedList.size(); i++) {
                    map.add(imagesEncodedList.get(i).toString());
                }
                new BusinessProfileSettingActivity.ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
            }*/

        } else {

        }
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {


        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // Internal sdcard location
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "AlertMeUBusiness");
        // Create the storage directory if it does not exist
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.d(TAG, "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {

            String fileName = "B_" + preferences.getString("business_user_id", "") + "_pic_" + System.currentTimeMillis() + ".png";
            mediaFile = new File(folder.getPath() + File.separator + fileName);
            destination = new File(folder.getPath(), fileName);

        } else {
            return null;
        }

        return mediaFile;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    class ImageUploadTask extends AsyncTask<String, Void, String> {
        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(AddModifyPriceListCategoryActivity.this, "", res.getString(R.string.jpw), true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                File file = new File(map.get(i));
                File f = CompressFile.getCompressedImageFile(file, AddModifyPriceListCategoryActivity.this);
                Bitmap bitmap = decodeFile(f.getAbsolutePath());
                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                //  scaled.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();
                if (cf == 1) {
                    fileName = "PL_" + preferences.getString("business_user_id", "") + "_cam_" + System.currentTimeMillis() + ".png";
                }
                if (cf == 2) {
                    fileName = "PL_" + preferences.getString("business_user_id", "") + "_lib_" + System.currentTimeMillis() + ".png";
                }
                entity.addPart("user_id", new StringBody("199"));
                entity.addPart("fileName", new StringBody(fileName));
                entity.addPart("club_image", new ByteArrayBody(data, "image/*", params[1]));
                // entity.addPart("club_image", new FileBody(file, "image/jpeg", params[1]));
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                sResponse = EntityUtils.getContentCharSet(response.getEntity());

                System.out.println("sResponse : " + sResponse);
            } catch (Exception e) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Log.e(e.getClass().getName(), e.getMessage(), e);

            }
            if (flagau == 0) {
                jsonLeadObjReq = new JSONObject() {
                    {
                        try {

                            put("image_path", Config.URL_AlertMeUImage + "uploads/" + fileName);
                            put("business_user_id", preferences.getString("business_user_id", ""));
                            put("title", title);
                            put("description", desc);
                            put("rate", price);
                            put("link", "");
                            put("mainid_pricelist", mainPriListId);
                            put("subbc_id", subbc_id);
                            put("maincat_id", maincat_id);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("json exception", "json exception" + e);
                        }
                    }
                };

                WebClient serviceAccess = new WebClient();
                Log.i("json", "json" + jsonLeadObjReq);
                addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_ADDREQUESTATTACHMENTPR, jsonLeadObjReq);
                Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
            } else {
                jsonLeadObjReq = new JSONObject() {
                    {
                        try {

                            put("image_path", Config.URL_AlertMeUImage + "uploads/" + fileName);
                            put("title", edittitle);
                            put("description", editdesc);
                            put("rate", editprice);
                            put("link", "");
                            put("id", sublistid);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("json exception", "json exception" + e);
                        }
                    }
                };
                Thread objectThread = new Thread(new Runnable() {
                    public void run() {
                        // TODO Auto-generated method stub
                        final WebClient serviceAccess = new WebClient();
                        updateResponse = serviceAccess.SendHttpPost(Config.URL_UPDATEREQUESTATTACHMENTPR, jsonLeadObjReq);
                        Log.i("resp", "syncDataesponse" + updateResponse);
                        final Handler handler = new Handler(Looper.getMainLooper());
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() { // This thread runs in the UI
                                    @Override
                                    public void run() {
                                        if (updateResponse.compareTo("") != 0) {

                                            try {
                                                JSONObject jObject = new JSONObject(updateResponse);
                                                status = jObject.getBoolean("status");
                                                if (status) {
                                                    editfhideshow.setVisibility(View.GONE);
                                                    new getSubCatList().execute();
                                                }
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        };

                        new Thread(runnable).start();
                    }
                });
                objectThread.start();
            }
            return sResponse;
        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    //  Toast.makeText(getApplicationContext(), sResponse + " Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                    count++;
                    if (count < map.size()) {
                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));

                    } else {
                        fileName = "";
                        titleEdtTxt.setText("");
                        descEdtTxt.setText("");
                        priceEdtTxt.setText("");
                        mArrayUri.clear();
                        imagesEncodedList.clear();
                        map.clear();
                        count = 0;
                        profilePic.setImageBitmap(null);
                        profilePic.setImageResource(R.drawable.contact_img);
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        Toast.makeText(AddModifyPriceListCategoryActivity.this, res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();
                        mListener.messageReceived(addRequestAttachResponse);
                    }

                }

            } catch (Exception e) {
                Toast.makeText(AddModifyPriceListCategoryActivity.this, e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    //
    private boolean checkAndRequestPermissions() {

        int writepermission = ContextCompat.checkSelfPermission(AddModifyPriceListCategoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(AddModifyPriceListCategoryActivity.this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(AddModifyPriceListCategoryActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //finish();
                        //else any one or both the permissions are not granted
                        selectImage();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddModifyPriceListCategoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(AddModifyPriceListCategoryActivity.this, Manifest.permission.CAMERA)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddModifyPriceListCategoryActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddModifyPriceListCategoryActivity.this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:in.alertmeu.a4b")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    private static int getImageRotation(final File imageFile) {

        ExifInterface exif = null;
        int exifRotation = 0;

        try {
            exif = new ExifInterface(imageFile.getPath());
            exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;
        else
            return exifToDegrees(exifRotation);
    }

    private static int exifToDegrees(int rotation) {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;

        return 0;
    }

    private static Bitmap getBitmapRotatedByDegree(Bitmap bitmap, int rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static void bindListener(Listener listener) {
        mListener = listener;
    }
}