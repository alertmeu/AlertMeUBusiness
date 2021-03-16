package in.alertmeu.a4b.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.AdsPreviewActivity;
import in.alertmeu.a4b.activity.EditAdsPreviewActivity;
import in.alertmeu.a4b.adapter.AdsSubCatListAdpter;
import in.alertmeu.a4b.adapter.GridViewAdapter;
import in.alertmeu.a4b.models.DateTimeDAO;
import in.alertmeu.a4b.models.DiscountDAO;
import in.alertmeu.a4b.models.FileNameDAO;
import in.alertmeu.a4b.models.MainCatModeDAO;
import in.alertmeu.a4b.models.RunAddDAO;
import in.alertmeu.a4b.models.SubCatModeDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.ExifUtil;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.WebClient;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.BitmapFactory.decodeFile;


public class AddNewAdsFragment2 extends Fragment {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    ProgressDialog mProgressDialog1;
    JSONObject jsonLeadObj, jsonLeadObj1, syncJsonObject, jsonObjectSync, jsonNotifyObj, jsonObject, jsonObj;
    JSONArray jsonArray, jsonArraySync;
    String alertResponse = "", alertSubResponse = "", bc_id = "", sub_id = "", syncDataesponse = "", balanceAmountResponse = "";
    ArrayList<MainCatModeDAO> mainCatModeDAOArrayList;
    ArrayList<SubCatModeDAO> subCatModeDAOArrayList;
    EditText edttitle, edtDescpritipon, describeLimitations, startTime, expectedDate;
    String title = "", description = "", response = "", qrCodeResponse = "", main_cat = "", editfalg = "", bid = "", image_path = "", deleteLogoResponse = "", cost = "", AmountResponse = "", discountads = "";
    View v;
    Button takePhotoCamera, takePhotoGallery, genRateQRCode, btnnext;
    String userChoosenTask = "";
    private Uri fileUri; // file url to store image
    private int REQUEST_CAMERA = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    int PICK_IMAGE_MULTIPLE = 1;
    static String fileName = "";
    static int ifg = 0;
    static File destination;
    // LogCat tag
    private static final String TAG = AddNewAdsFragment2.class.getSimpleName();
    static final Integer CAMERA = 0x1;

    ArrayList<String> imagesEncodedList;
    ArrayList<Uri> mArrayUri;
    String imageEncoded;
    private String[] FilePathStrings;
    private String[] FileNameStrings;


    GridView grid;
    ProgressDialog progressDoalog;

    String link = "";
    Thread thread;
    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    ImageView imageView, deleteimage;
    Button sharebtn, uploadbtn;
    String final_image = "";
    ArrayList<FileNameDAO> al = new ArrayList<FileNameDAO>();
    FileNameDAO f = new FileNameDAO();
    private ProgressDialog dialog;
    Bitmap result;
    GridViewAdapter adapter;
    ArrayList<DateTimeDAO> selectTime = new ArrayList<DateTimeDAO>();
    ArrayList<DiscountDAO> discountArr = new ArrayList<DiscountDAO>();
    ArrayList<RunAddDAO> runAdd = new ArrayList<RunAddDAO>();
    boolean status, status2;
    int count = 0, rounding_scale;
    double discount, notification_charge;
    public ArrayList<String> map = new ArrayList<String>();
    JSONObject jsonLeadObjReq;

    String addImageLocationResponse = "", message = "", id = "", addRequestAttachResponse = "", describe_limitations = "", price_formula = "", special_message = "", s_flag = "0";

    MultipartEntity entity;
    CheckBox cmyList, cshowOnMap, cnearByClients;
    String map_status = "0", mylist_status = "0", nearclient_status = "0";
    AdsSubCatListAdpter subCatListAdpter;
    RecyclerView mainCatList;
    ArrayList<String> subCatArrayList;
    ArrayList<String> mainCatArrayList;
    ArrayList<String> subCatNameArrayList;
    ArrayList<String> subCatNameArrayListTemp;
    ArrayList<String> mainCatAList = null;
    ArrayList<String> subCatAList = null;
    int i;
    //time calculations
    String tunit = "1", tunit1 = "", stime = "", sdate = "", tsign = "", edate = "", etime = "", totalamount = "", openflag = "0", repost = "", currency_sign = "", n_flag = "1", balance_amount = "0.0", defalut_image = "";
    Date date;
    int thours = 0;
    TextView tamount, sprice;
    ArrayAdapter<RunAddDAO> adapterRunAdd;
    Spinner spinnerRunAdd;
    LinearLayout showHideLayout, linkClick;
    TextView catHideShow;
    double rate = 1.0;
    CheckBox notificationFlag, sponsorFlag;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    static String b_id;
    int clickpic = 0;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    InitSubCategorySpinner asyncTask = null;
    ArrayAdapter<DiscountDAO> adapter1;
    Spinner spinnerDiscount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loadLanguage();
        v = inflater.inflate(R.layout.fragment_add_new_ads, container, false);
        res = getResources();
        preferences = getActivity().getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        b_id = preferences.getString("business_user_id", "");
        spinnerRunAdd = (Spinner) v.findViewById(R.id.spinnerRunAdd);
        notificationFlag = (CheckBox) v.findViewById(R.id.notificationFlag);
        sponsorFlag = (CheckBox) v.findViewById(R.id.sponsorFlag);
        Intent intent = getActivity().getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            mainCatAList = (ArrayList<String>) args.getSerializable("mainCat");
            subCatAList = (ArrayList<String>) args.getSerializable("subCat");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        sdate = format.format(cal.getTime());
        takePhotoCamera = (Button) v.findViewById(R.id.takePhotoCamera);
        takePhotoGallery = (Button) v.findViewById(R.id.takePhotoGallery);
        genRateQRCode = (Button) v.findViewById(R.id.genRateQRCode);
        uploadbtn = (Button) v.findViewById(R.id.uploadbtn);
        imageView = (ImageView) v.findViewById(R.id.imageView);
        deleteimage = (ImageView) v.findViewById(R.id.deleteimage);
        edttitle = (EditText) v.findViewById(R.id.edttitle);
        edtDescpritipon = (EditText) v.findViewById(R.id.edtDescpritipon);
        describeLimitations = (EditText) v.findViewById(R.id.describeLimitations);
        mainCatList = (RecyclerView) v.findViewById(R.id.mainCatList);
        startTime = (EditText) v.findViewById(R.id.startTime);
        expectedDate = (EditText) v.findViewById(R.id.expectedDate);
        tamount = (TextView) v.findViewById(R.id.tamount);
        sprice = (TextView) v.findViewById(R.id.sprice);
        catHideShow = (TextView) v.findViewById(R.id.catHideShow);
        btnnext = (Button) v.findViewById(R.id.btnnext);
        showHideLayout = (LinearLayout) v.findViewById(R.id.showHideLayout);
        subCatModeDAOArrayList = new ArrayList<>();
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            fileName = "";
            new initMainCategorySpinner().execute();
        } else {
            Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        repost = preferences.getString("repost", "");
        editfalg = preferences.getString("editflag", "");

        if (editfalg.equals("0")) {
            if (preferences.getString("image_path", "").contains("http")) {
                takePhotoCamera.setVisibility(View.GONE);
                takePhotoGallery.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                deleteimage.setVisibility(View.VISIBLE);

            } else {
                deleteimage.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                takePhotoCamera.setVisibility(View.VISIBLE);
                takePhotoGallery.setVisibility(View.VISIBLE);

            }

            edttitle.setText(preferences.getString("title", ""));
            edtDescpritipon.setText(preferences.getString("description", ""));
            describeLimitations.setText(preferences.getString("limitations", ""));
            bid = preferences.getString("bid", "");
            stime = preferences.getString("s_time", "");
            sdate = preferences.getString("s_date", "");
            etime = preferences.getString("e_time", "");
            edate = preferences.getString("e_date", "");
            tunit1 = preferences.getString("tunit1", "");
            tsign = preferences.getString("tsign", "");
            image_path = preferences.getString("image_path", "");
            fileName = image_path;

            //  Picasso.with(getActivity()).load(preferences.getString("image_path", "")).noPlaceholder().into((ImageView) imageView);
            try {
                Picasso.get().load(preferences.getString("image_path", "")).into(imageView);
            } catch (Exception e) {

            }
        } else {
            takePhotoCamera.setVisibility(View.VISIBLE);
            takePhotoGallery.setVisibility(View.VISIBLE);
        }
        deleteimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(res.getString(R.string.jduwi))
                            .setCancelable(false)
                            .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    try {
                                        if (preferences.getString("modifyflag", "").equals("1")) {
                                            imageView.setVisibility(View.GONE);
                                            deleteimage.setVisibility(View.GONE);
                                            image_path = "";
                                            fileName = "";
                                            takePhotoCamera.setVisibility(View.VISIBLE);
                                            takePhotoGallery.setVisibility(View.VISIBLE);
                                        } else {
                                            new deleteImage().execute();
                                        }


                                    } catch (Exception ex) {
                                        Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
                                    }
                                    dialog.cancel();
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
                    alert.setTitle(res.getString(R.string.jdem));
                    alert.show();

                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });
        takePhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickpic = 1;
                if (checkAndRequestPermissions()) {

                    cameraIntent();
                }
            }
        });
        takePhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickpic = 2;
                if (checkAndRequestPermissions()) {

                    galleryIntent();
                }
            }
        });
        genRateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    if (!fileName.equals("")) {
                        //
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Description");
                        alertDialog.setMessage("Enter Description");

                        final EditText input = new EditText(getActivity());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        alertDialog.setView(input);
                        // alertDialog.setIcon(R.drawable.msg_img);

                        alertDialog.setPositiveButton("SAVE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        link = input.getText().toString().trim();
                                        if (!link.equals("")) {
                                            dialog.cancel();
                                            //  new ImagegenrateTask().execute();


                                        } else {
                                            Toast.makeText(getActivity(), "Please enter description", Toast.LENGTH_LONG).show();
                                            dialog.cancel();
                                        }
                                    }
                                });

                        alertDialog.setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.show();

                    } else {
                        Toast.makeText(getActivity(), "Please take a photo", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = edttitle.getText().toString().trim();
                description = edtDescpritipon.getText().toString().trim();
                describe_limitations = describeLimitations.getText().toString().trim();
                if (validate(stime, sdate, tsign, title, fileName)) {
                    List<SubCatModeDAO> stList = ((AdsSubCatListAdpter) subCatListAdpter).getSservicelist();
                    String data1 = "";
                    String data2 = "";
                    String data3 = "";
                    subCatArrayList = new ArrayList<>();
                    mainCatArrayList = new ArrayList<>();
                    subCatNameArrayList = new ArrayList<>();
                    for (int i = 0; i < stList.size(); i++) {
                        SubCatModeDAO serviceListDAO = stList.get(i);
                        if (serviceListDAO.isSelected() == true) {
                            data1 = serviceListDAO.getId().toString();
                            data2 = serviceListDAO.getBc_id().toString();
                            data3 = serviceListDAO.getSubcategory_name().toString();
                            subCatArrayList.add(data1);
                            mainCatArrayList.add(data2);
                            subCatNameArrayList.add(data3);

                        }
                    }
                    new addLocationDataDetails().execute();
                }
                // Toast.makeText(getActivity(),""+subCatArrayList.size(),Toast.LENGTH_SHORT).show();
            }
        });
        runAdd.clear();
        runAdd.add(new RunAddDAO("0", res.getString(R.string.jsts)));
        // runAdd.add(new RunAddDAO("1", "1 " + res.getString(R.string.jhrs)));
        runAdd.add(new RunAddDAO("2", "2 " + res.getString(R.string.jhrss)));
        // runAdd.add(new RunAddDAO("3", "3 " + res.getString(R.string.jhrss)));
        runAdd.add(new RunAddDAO("4", "4 " + res.getString(R.string.jhrss)));
        //  runAdd.add(new RunAddDAO("6", "6 " + res.getString(R.string.jhrss)));
        runAdd.add(new RunAddDAO("8", "8 " + res.getString(R.string.jhrss)));
        runAdd.add(new RunAddDAO("12", "12 " + res.getString(R.string.jhrss)));
        // runAdd.add(new RunAddDAO("16", "16 " + res.getString(R.string.jhrss)));
        // runAdd.add(new RunAddDAO("24", "24 " + res.getString(R.string.jhrss)));
        //  runAdd.add(new RunAddDAO("2", "2 " + res.getString(R.string.xday)));
        runAdd.add(new RunAddDAO("1", "1 " + res.getString(R.string.jweek)));
        // runAdd.add(new RunAddDAO("2", "2 " + res.getString(R.string.jweeks)));
        //    runAdd.add(new RunAddDAO("1", "1 " + res.getString(R.string.jmonth)));

        selectTime.clear();
        selectTime.add(new DateTimeDAO("1", res.getString(R.string.jrn)));
        selectTime.add(new DateTimeDAO("2", res.getString(R.string.tday)));
        selectTime.add(new DateTimeDAO("3", res.getString(R.string.ttom)));
        selectTime.add(new DateTimeDAO("4", res.getString(R.string.tood)));

        Spinner spinnerCustom = (Spinner) v.findViewById(R.id.spinnerSelectTime);
        ArrayAdapter<DateTimeDAO> adapter = new ArrayAdapter<DateTimeDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, selectTime);
        spinnerCustom.setAdapter(adapter);
        if (editfalg.equals("0")) {
            selectSpinnerItemByValue(spinnerCustom, tunit1);

        }
        spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                DateTimeDAO alertTypeDAO = (DateTimeDAO) parent.getSelectedItem();
                //Toast.makeText(getApplicationContext(), "Source ID: " + alertTypeDAO.getId() + ",  Source Name : " + alertTypeDAO.getAlert_name(), Toast.LENGTH_SHORT).show();
                tunit1 = alertTypeDAO.getTimings();
                tunit = alertTypeDAO.getUnit();
                      /*  sdate="";
                        stime="";
                        edate="";
                        etime="";*/
                if (!editfalg.equals("0")) {
                    adapterRunAdd = new ArrayAdapter<RunAddDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, runAdd);
                    spinnerRunAdd.setAdapter(adapterRunAdd);
                    adapterRunAdd.notifyDataSetChanged();
                }
                if (openflag.equals("1")) {
                    adapterRunAdd = new ArrayAdapter<RunAddDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, runAdd);
                    spinnerRunAdd.setAdapter(adapterRunAdd);
                    adapterRunAdd.notifyDataSetChanged();

                }
                if (tunit.equals("4")) {
                    startTime.setVisibility(View.VISIBLE);
                    expectedDate.setVisibility(View.VISIBLE);
                    if (openflag.equals("0")) {
                        startTime.setText(stime);
                        expectedDate.setText(sdate);
                        openflag = "1";
                    } else {
                        startTime.setText("");
                        stime = "";
                        sdate = "";
                    }
                } else {
                    expectedDate.setVisibility(View.GONE);
                }
                if (tunit.equals("1")) {
                    startTime.setVisibility(View.GONE);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
                    stime = dateformat.format(c.getTime());

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    sdate = format.format(cal.getTime());
                    openflag = "1";
                }
                if (tunit.equals("2")) {
                    startTime.setVisibility(View.VISIBLE);

                    if (openflag.equals("0")) {
                        startTime.setText(stime);
                        long t = getTimeDifferenceInMillis(sdate + " " + stime);
                        // long t=getTimeDifferenceInMillis("2019-11-18 16:10:00");
                        if (t > 0) {
                            startTime.setText(stime);
                        } else {
                            startTime.setText("");
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        sdate = format.format(cal.getTime());
                        openflag = "1";
                    } else {
                        startTime.setText("");
                        stime = "";
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        sdate = format.format(cal.getTime());
                    }

                }
                if (tunit.equals("3")) {
                    startTime.setVisibility(View.VISIBLE);

                    if (openflag.equals("0")) {
                        startTime.setText(stime);
                        openflag = "1";
                    } else {
                        startTime.setText("");
                        stime = "";
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        sdate = format.format(cal.getTime());
                        String oldDate = sdate;
                        System.out.println("Date before Addition: " + oldDate);
                        //Specifying date format that matches the given date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        try {
                            //Setting the date to the given date
                            c.setTime(sdf.parse(oldDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //Number of Days to add
                        c.add(Calendar.DAY_OF_MONTH, 1);
                        //Date after adding the days to the given date
                        sdate = sdf.format(c.getTime());
                    }
                }
                // Toast.makeText(getActivity(), "sdate" + sdate+" time "+stime, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }


        });


        adapterRunAdd = new ArrayAdapter<RunAddDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, runAdd);
        spinnerRunAdd.setAdapter(adapterRunAdd);
        if (editfalg.equals("0")) {
            selectSpinnerItemByValue(spinnerRunAdd, tsign);
        }
        spinnerRunAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                RunAddDAO alertTypeDAO = (RunAddDAO) parent.getSelectedItem();
                //Toast.makeText(getApplicationContext(), "Source ID: " + alertTypeDAO.getId() + ",  Source Name : " + alertTypeDAO.getAlert_name(), Toast.LENGTH_SHORT).show();
                if (!alertTypeDAO.getUnit().equals("0")) {
                    tsign = alertTypeDAO.getTimings();
                    if (tsign.contains(res.getString(R.string.jhrs))) {
                        thours = Integer.parseInt(alertTypeDAO.getUnit());

                    } else if (tsign.contains(res.getString(R.string.jhrss))) {
                        thours = Integer.parseInt(alertTypeDAO.getUnit());

                    } else if (tsign.contains(res.getString(R.string.xday))) {
                        thours = (Integer.parseInt(alertTypeDAO.getUnit()) * 24);
                    } else if (tsign.contains(res.getString(R.string.jweek))) {
                        thours = (Integer.parseInt(alertTypeDAO.getUnit()) * 24 * 7);
                    } else if (tsign.contains(res.getString(R.string.jweeks))) {
                        thours = (Integer.parseInt(alertTypeDAO.getUnit()) * 24 * 14);
                    } else if (tsign.contains(res.getString(R.string.jmonth))) {
                        thours = (Integer.parseInt(alertTypeDAO.getUnit()) * 24 * 30);
                    }
                    Date myDateTime = null;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    //Parse your string to SimpleDateFormat
                    try {
                        myDateTime = simpleDateFormat.parse(sdate + " " + stime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (validateDateTime(stime, sdate)) {
                        Calendar cal = Calendar.getInstance(); // creates calendar
                        cal.setTime(myDateTime); // sets calendar time/date
                        cal.add(Calendar.HOUR_OF_DAY, thours); // adds one hour
                        cal.getTime(); // returns new date object, one hour in the future
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
                        edate = sdf.format(cal.getTime());
                        etime = tdf.format(cal.getTime());
                        //  Toast.makeText(getActivity(), "end date " + edate + " end time " + etime, Toast.LENGTH_SHORT).show();

                        /*if (AppStatus.getInstance(getActivity()).isOnline()) {
                            new dueFeesAvailable().execute();
                        } else {

                            Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                        }*/

                        if (discount > 0) {

                            BigDecimal bd = new BigDecimal((Math.sqrt(thours) * rate) * ((100 - discount) / 100)).setScale(rounding_scale, RoundingMode.HALF_UP);
                            cost = "" + bd.doubleValue();
                            sprice.setVisibility(View.VISIBLE);
                            sprice.setText(special_message);
                        } else {
                            sprice.setVisibility(View.GONE);
                            BigDecimal bd = new BigDecimal(Math.sqrt(thours) * rate).setScale(rounding_scale, RoundingMode.HALF_UP);
                            cost = "" + bd.doubleValue();
                        }

                        // cost=""+(Math.round(Math.sqrt(thours)*rate+notification_charge*10));
                        if (preferences.getInt("provalid", 0) == 0) {
                            totalamount = cost;
                            tamount.setText(res.getString(R.string.jtai) + " " + currency_sign + (totalamount));

                        } else {
                            BigDecimal bd = new BigDecimal(Double.parseDouble(cost) * .5).setScale(rounding_scale, RoundingMode.HALF_UP);
                            totalamount = "" + bd.doubleValue();
                            //  totalamount = "" + (Double.parseDouble(cost) * .5).setScale(rounding_scale, RoundingMode.HALF_UP);
                            tamount.setText(res.getString(R.string.jtai) + " " + currency_sign + (totalamount));
                        }
                    }
                } else {
                    tsign = "";
                    totalamount = "";
                    tamount.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }


        });

        startTime.setOnClickListener(new View.OnClickListener() {
            private int mHours, mMinutes;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                mHours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                mMinutes = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        String output = String.format("%02d:%02d", hourOfDay, minute);
                        Log.d("output", output);
                        stime = output;
                        startTime.setText(output);
                    }
                }, mHours, mMinutes, false);
                mTimePickerDialog.setTitle(res.getString(R.string.jsta));
                mTimePickerDialog.show();

                adapterRunAdd = new ArrayAdapter<RunAddDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, runAdd);
                spinnerRunAdd.setAdapter(adapterRunAdd);
                adapterRunAdd.notifyDataSetChanged();
            }
        });

        expectedDate.setOnClickListener(new View.OnClickListener() {
            private int mYear, mMonth, mDay;
            private SimpleDateFormat dateFormatter;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                        mcurrentDate.set(selectedyear, selectedmonth, selectedday);
                        expectedDate.setText(dateFormatter.format(mcurrentDate.getTime()));
                        sdate = format.format(mcurrentDate.getTime());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle(res.getString(R.string.jstag));
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDatePicker.show();

                adapterRunAdd = new ArrayAdapter<RunAddDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, runAdd);
                spinnerRunAdd.setAdapter(adapterRunAdd);
                adapterRunAdd.notifyDataSetChanged();

            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefEditor.putInt("ifg", ifg);
                prefEditor.commit();

                if (!editfalg.equals("0")) {
                    title = edttitle.getText().toString().trim();
                    description = edtDescpritipon.getText().toString().trim();
                    describe_limitations = describeLimitations.getText().toString().trim();
                    if (validate(stime, sdate, tsign, title, fileName)) {
                        List<SubCatModeDAO> stList = ((AdsSubCatListAdpter) subCatListAdpter).getSservicelist();
                        String data1 = "";
                        String data2 = "";
                        String data3 = "";
                        subCatArrayList = new ArrayList<>();
                        mainCatArrayList = new ArrayList<>();
                        subCatNameArrayList = new ArrayList<>();
                        for (int i = 0; i < stList.size(); i++) {
                            SubCatModeDAO serviceListDAO = stList.get(i);
                            if (serviceListDAO.isSelected() == true) {
                                data1 = serviceListDAO.getId().toString();
                                data2 = serviceListDAO.getBc_id().toString();
                                data3 = serviceListDAO.getSubcategory_name().toString();
                                subCatArrayList.add(data1);
                                mainCatArrayList.add(data2);
                                subCatNameArrayList.add(data3);

                            }
                        }
                        WebClient webClient = new WebClient();
                        String subcatname = webClient.convertArrayListToStringWithComma(subCatNameArrayList);
                        String subcatid = webClient.convertArrayListToStringWithComma(subCatArrayList);
                        //subCatArrayList.clear();
                        //  subCatArrayList.add(subcatid);
                        if (!subcatname.equals("")) {
                            Intent intent = new Intent(getActivity(), AdsPreviewActivity.class);
                            Bundle args = new Bundle();
                            args.putSerializable("ARRAYLIST", (Serializable) subCatArrayList);
                            args.putSerializable("ARRAYLIST1", (Serializable) mainCatArrayList);
                            args.putSerializable("ARRAYLIST2", (Serializable) mArrayUri);
                            args.putSerializable("ARRAYLIST3", (Serializable) imagesEncodedList);
                            args.putSerializable("ARRAYLIST4", (Serializable) subCatNameArrayList);
                            intent.putExtra("BUNDLE", args);
                            intent.putExtra("description", description);
                            intent.putExtra("title", title);
                            intent.putExtra("flag", "advertisement");
                            intent.putExtra("describe_limitations", describe_limitations);
                            intent.putExtra("rq_code", link);
                            intent.putExtra("sdate", sdate);
                            intent.putExtra("stime", stime);
                            intent.putExtra("edate", edate);
                            intent.putExtra("etime", etime);
                            intent.putExtra("status", "2");
                            intent.putExtra("fileName", fileName);
                            intent.putExtra("tamount", totalamount);
                            intent.putExtra("main_cat", main_cat);
                            intent.putExtra("bc_id", bc_id);
                            intent.putExtra("subcatid", subcatid);
                            intent.putExtra("subcatname", subcatname);
                            intent.putExtra("tunit", tunit);
                            intent.putExtra("tunit1", tunit1);
                            intent.putExtra("tsign", tsign);
                            intent.putExtra("nflag", n_flag);
                            intent.putExtra("s_flag", s_flag);
                            intent.putExtra("balance_amount", balance_amount);
                            intent.putExtra("thours", "" + thours);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), res.getString(R.string.jpssc), Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    title = edttitle.getText().toString();
                    description = edtDescpritipon.getText().toString();
                    describe_limitations = describeLimitations.getText().toString();
                    if (validate(stime, sdate, tsign, title, fileName)) {
                        List<SubCatModeDAO> stList = ((AdsSubCatListAdpter) subCatListAdpter).getSservicelist();
                        String data1 = "";
                        String data2 = "";
                        String data3 = "";
                        subCatArrayList = new ArrayList<>();
                        mainCatArrayList = new ArrayList<>();
                        subCatNameArrayList = new ArrayList<>();
                        for (int i = 0; i < stList.size(); i++) {
                            SubCatModeDAO serviceListDAO = stList.get(i);
                            if (serviceListDAO.isSelected() == true) {
                                data1 = serviceListDAO.getId().toString();
                                data2 = serviceListDAO.getBc_id().toString();
                                data3 = serviceListDAO.getSubcategory_name().toString();
                                subCatArrayList.add(data1);
                                mainCatArrayList.add(data2);
                                subCatNameArrayList.add(data3);

                            }
                        }
                        WebClient webClient = new WebClient();
                        String subcatname = webClient.convertArrayListToStringWithComma(subCatNameArrayList);
                        String subcatid = webClient.convertArrayListToStringWithComma(subCatArrayList);
                        if (!subcatname.equals("")) {
                            Intent intent = new Intent(getActivity(), EditAdsPreviewActivity.class);
                            Bundle args = new Bundle();
                            args.putSerializable("ARRAYLIST", (Serializable) subCatArrayList);
                            args.putSerializable("ARRAYLIST1", (Serializable) mainCatArrayList);
                            args.putSerializable("ARRAYLIST2", (Serializable) mArrayUri);
                            args.putSerializable("ARRAYLIST3", (Serializable) imagesEncodedList);
                            args.putSerializable("ARRAYLIST4", (Serializable) subCatNameArrayList);
                            intent.putExtra("BUNDLE", args);
                            intent.putExtra("description", description);
                            intent.putExtra("title", title);
                            intent.putExtra("flag", "advertisement");
                            intent.putExtra("describe_limitations", describe_limitations);
                            intent.putExtra("rq_code", link);
                            intent.putExtra("sdate", sdate);
                            intent.putExtra("stime", stime);
                            intent.putExtra("edate", edate);
                            intent.putExtra("etime", etime);
                            intent.putExtra("status", "2");
                            intent.putExtra("fileName", fileName);
                            intent.putExtra("tamount", totalamount);
                            intent.putExtra("main_cat", main_cat);
                            intent.putExtra("subcatid", subcatid);
                            intent.putExtra("bc_id", bc_id);
                            intent.putExtra("subcatname", subcatname);
                            intent.putExtra("tunit", tunit);
                            intent.putExtra("tunit1", tunit1);
                            intent.putExtra("tsign", tsign);
                            intent.putExtra("nflag", n_flag);
                            intent.putExtra("s_flag", s_flag);
                            intent.putExtra("balance_amount", balance_amount);
                            intent.putExtra("thours", "" + thours);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), res.getString(R.string.jpssc), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            }
        });


//
        AdsSubCatListAdpter.bindListener(new Listener() {
            @Override
            public void messageReceived(String messageText) {
                if (AppStatus.getInstance(getActivity()).isOnline()) {
                    List<SubCatModeDAO> stList = ((AdsSubCatListAdpter) subCatListAdpter).getSservicelist();
                    String data3 = "";
                    subCatNameArrayList = new ArrayList<>();
                    for (int i = 0; i < stList.size(); i++) {
                        SubCatModeDAO serviceListDAO = stList.get(i);
                        if (serviceListDAO.isSelected() == true) {

                            data3 = serviceListDAO.getSubcategory_name().toString();
                            subCatNameArrayList.add(data3);

                        }
                    }
                    WebClient webClient = new WebClient();
                    String subcatname = webClient.convertArrayListToStringWithComma(subCatNameArrayList);
                    //     Toast.makeText(getActivity(), subcatname, Toast.LENGTH_SHORT).show();
                    //  String temp1 = edttitle.getText().toString().trim();
                    edttitle.setText("");
                    edttitle.setText(discountads + main_cat + "(" + subcatname + ")");
                } else {

                    Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });
        notificationFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationFlag.isChecked()) {
                    // Toast.makeText(getActivity(), "true", Toast.LENGTH_SHORT).show();
                    n_flag = "1";
                } else {
                    //  Toast.makeText(getActivity(), "false", Toast.LENGTH_SHORT).show();
                    n_flag = "0";
                }
            }
        });
        sponsorFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sponsorFlag.isChecked()) {
                    // Toast.makeText(getActivity(), "true", Toast.LENGTH_SHORT).show();
                    s_flag = "1";
                } else {
                    //  Toast.makeText(getActivity(), "false", Toast.LENGTH_SHORT).show();
                    s_flag = "0";
                }
            }
        });
        //new changes

        startTime.setVisibility(View.GONE);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
        stime = dateformat.format(c.getTime());

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal1 = Calendar.getInstance();
        sdate = format1.format(cal1.getTime());
        openflag = "1";

        discountArr.clear();
        discountArr.add(new DiscountDAO("0", res.getString(R.string.xsdiscount)));
        discountArr.add(new DiscountDAO("1", "10%"));
        discountArr.add(new DiscountDAO("2", "20%"));
        discountArr.add(new DiscountDAO("3", "30%"));
        discountArr.add(new DiscountDAO("3", "40%"));
        discountArr.add(new DiscountDAO("3", "50%"));
        spinnerDiscount = (Spinner) v.findViewById(R.id.spinnerDiscount);
        adapter1 = new ArrayAdapter<DiscountDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, discountArr);
        spinnerDiscount.setAdapter(adapter1);
        spinnerDiscount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                DiscountDAO alertTypeDAO = (DiscountDAO) parent.getSelectedItem();
                //Toast.makeText(getApplicationContext(), "Source ID: " + alertTypeDAO.getId() + ",  Source Name : " + alertTypeDAO.getAlert_name(), Toast.LENGTH_SHORT).show();
                // tunit1 = alertTypeDAO.getDiscount();
                //  tunit = alertTypeDAO.getUnit();
                if (!alertTypeDAO.getUnit().equals("0")) {
                    String t = edttitle.getText().toString();
                    String t2 = t.replace(discountads, "");
                    discountads = alertTypeDAO.getDiscount() + " off :";
                    edttitle.setText(discountads + t2);
                } else {
                    String t = edttitle.getText().toString().replace(discountads, "");
                    edttitle.setText(t);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }


        });
        return v;
    }

    private boolean validate(String stime, String sdate, String tsign, String title, String final_image) {
        boolean isValidate = false;
        if (stime.trim().equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jpst), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (sdate.trim().equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jpsd), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (tsign.trim().equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jpsts), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (title.trim().equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jpstsu), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (final_image.trim().equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jpstsui), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else {
            isValidate = true;
        }
        return isValidate;
    }

    private boolean validateDateTime(String stime, String sdate) {
        boolean isValidate = false;
        if (stime.trim().equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jpst), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (sdate.trim().equals("")) {
            Toast.makeText(getActivity(), res.getString(R.string.jpsd), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else {
            isValidate = true;
        }
        return isValidate;
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    private void galleryIntent() {
        //Intent intent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

       /* Intent intent = new Intent();
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);*/
        // galleryIntent.setDataAndType(Uri.fromFile(mImsgeFileName), "image/*");
        // galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //   startActivity(galleryIntent);

        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // pickPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, PICK_IMAGE_MULTIPLE);

      /*  Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_MULTIPLE);*/


    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;


        if (type == MEDIA_TYPE_IMAGE) {
            ifg = 1;
            String fileName = "A_" + b_id + "_ad_" + System.currentTimeMillis() + ".png";
            mediaFile = new File(folder.getPath() + File.separator + fileName);
            destination = new File(folder.getPath(), fileName);

        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == getActivity().RESULT_OK && null != data) {
                // Get the Image from data
                //  Toast.makeText(getActivity(), ""+data.getData(), Toast.LENGTH_LONG).show();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getData() != null) {
                    mArrayUri = new ArrayList<Uri>();
                    imagesEncodedList = new ArrayList<String>();
                    Uri mImageUri = data.getData();
                    mArrayUri.add(mImageUri);
                    if (!mImageUri.toString().contains("mediakey")) {
                        // Get the cursor
                        Cursor cursor = getActivity().getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageEncoded = cursor.getString(columnIndex);

                        cursor.close();
                        Log.v("LOG_TAG", "imageEncoded" + imageEncoded);
                        Log.v("LOG_TAG", "Selected Images" + mImageUri);
                        // Create a String array for FilePathStrings


                        //File myFile = new File(mImageUri.getPath());

                        //Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(getActivity(), mImageUri));

                        imagesEncodedList.add(getPathFromUri(getActivity(), mImageUri));
                        //  imagesEncodedList.add(getRealPathFromUri(mImageUri));

                        // Get the path of the image file
                        // FilePathStrings = new String[1];
                        // FileNameStrings = new String[1];
                        //  FilePathStrings[0] = getPathFromUri(getActivity(), mImageUri);
                        // Get the name image file
                        //  FileNameStrings[0] = getFileName(mImageUri);
                        fileName = getFileName(mImageUri);
                        ifg = 2;
                        // FileNameDAO f1 = new FileNameDAO(getFileName(mImageUri), getPathFromUri(CreateQRCodeActivity.this, mImageUri));
                        //al.add(f1);
                        //  f.setFile_name(getFileName(mImageUri));
                        //  f.setFile_path(getPathFromUri(getActivity(), mImageUri));
                        // Locate the GridView in gridview_main.xml
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // down sizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 1;
                        Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(getActivity(), mImageUri), options);
                        // Bitmap bitmap = Bitmap.createScaledBitmap(getPathFromUri(getActivity(), mImageUri), 250, 250, true);
                        // Bitmap bitmap = BitmapFactory.decodeFile(getRealPathFromUri(mImageUri), options);

                   /* //1
                    int imageRotation = getImageRotation(myFile);
                    if (imageRotation != 0)
                        bitmap = getBitmapRotatedByDegree(bitmap, imageRotation);
                    //*/

                        Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(getActivity(), mImageUri), bitmap);
                        //Bitmap orientedBitmap = ExifUtil.rotateBitmap(getRealPathFromUri(mImageUri), bitmap);
                        //   grid = (GridView) v.findViewById(R.id.gridview);
                        //grid.setVisibility(View.VISIBLE);
                        // Pass String arrays to LazyAdapter Class
                        //   adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
                        // Set the LazyAdapter to the GridView
                        //  grid.setAdapter(adapter);

                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(orientedBitmap);
                        //  new ImagegenrateTaskGallery().execute();
                    } else {
                        Toast.makeText(getActivity(), res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    /*if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(getPathFromUri(getActivity(), uri));
                            Log.v("LOG_TAG", "imageEncoded" + imageEncoded);
                            cursor.close();

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        // Create a String array for FilePathStrings
                        FilePathStrings = new String[mArrayUri.size()];
                        // Create a String array for FileNameStrings
                        FileNameStrings = new String[mArrayUri.size()];
                        for (int i = 0; i < mArrayUri.size(); i++) {
                            File myFile = new File(mArrayUri.get(i).getPath());

                            Log.d("LOG_TAG", "imageToUpload" + imagesEncodedList.get(i));
                            // Get the path of the image file
                            // FilePathStrings[i] = myFile.getAbsolutePath();
                            FilePathStrings[i] = getPathFromUri(getActivity(), mArrayUri.get(i));
                            // Get the name image file
                            FileNameStrings[i] = getFileName(mArrayUri.get(i));
                        }
                        // Locate the GridView in gridview_main.xml
                        grid = (GridView) v.findViewById(R.id.gridview);
                        // Pass String arrays to LazyAdapter Class
                        adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
                        // Set the LazyAdapter to the GridView
                        grid.setAdapter(adapter);


                    }
            */
                }
            } else if (requestCode == REQUEST_CAMERA) {
                //  onCaptureImageResult(data);

                if (resultCode == getActivity().RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity(true);


                } else if (resultCode == getActivity().RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getActivity(), res.getString(R.string.junc), Toast.LENGTH_SHORT).show();
                   /* if (ifg == 1) {
                        fileName = "";
                    }*/
                } else {
                    // failed to capture image
                    Toast.makeText(getActivity(), res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                }


            } else {
                // fileName = "";
                Toast.makeText(getActivity(), res.getString(R.string.jeunp), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Exception", "" + e);
            Toast.makeText(getActivity(), res.getString(R.string.jsw), Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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

    private void launchUploadActivity(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            mArrayUri = new ArrayList<Uri>();
            imagesEncodedList = new ArrayList<String>();
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
            //
            mArrayUri.add(Uri.fromFile(destination));

            FilePathStrings = new String[1];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[1];

            imagesEncodedList.add("" + destination);// imagesEncodedList.add(destination);


            // Get the path of the image file

            FilePathStrings[0] = "" + destination;
            // Get the name image file
            FileNameStrings[0] = fileName;
            fileName = getFileName(Uri.fromFile(destination));
            // Locate the GridView in gridview_main.xml
            // grid = (GridView) v.findViewById(R.id.gridview);
            //  grid.setVisibility(View.VISIBLE);
            // Pass String arrays to LazyAdapter Class
            //   adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
            // Set the LazyAdapter to the GridView
            //   grid.setAdapter(adapter);
            // Capture gridview item click
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
            // new ImagegenrateTask().execute();

        } else {

        }
    }


    private class initMainCategorySpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            // mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            // mProgressDialog.setMessage(res.getString(R.string.jsql));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
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
            alertResponse = serviceAccess.SendHttpPost(Config.URL_GETALLMAINCATEGORYBYBUSINESSUSER, jsonLeadObj);
            JSONObject jsonObja = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            AmountResponse = serviceAccess.SendHttpPost(Config.URL_GETBALANCEAMOUNT, jsonObja);
            Log.i("resp", "AmountResponse" + AmountResponse);
            if (isJSONValid(AmountResponse)) {
                // JSONArray leadJsonObj = null;
                try {
                    JSONObject jObject = new JSONObject(AmountResponse);
                    status = jObject.getBoolean("status");
                    if (status) {
                        balance_amount = jObject.getString("balanceamount");
                    } else {
                        balance_amount = "0.0";
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            mainCatModeDAOArrayList = new ArrayList<>();
            Log.i("resp", "alertResponse" + alertResponse);
            try {
                if (alertResponse.compareTo("") != 0) {
                    if (isJSONValid(alertResponse)) {

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    // mainCatModeDAOArrayList.add(new MainCatModeDAO("0", "Select Category"));
                                    JSONArray LeadSourceJsonObj = new JSONArray(alertResponse);
                                    for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                        JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);
                                        if (preferences.getString("ulang", "").equals("en")) {
                                            mainCatModeDAOArrayList.add(new MainCatModeDAO(json_data.getString("id"), json_data.getString("category_name"), json_data.getString("currency_sign"), json_data.getString("ads_pricing"), json_data.getString("discount"), json_data.getString("notification_charge"), json_data.getString("price_formula"), json_data.getString("rounding_scale"), json_data.getString("special_message"), json_data.getString("defalut_image")));
                                        } else if (preferences.getString("ulang", "").equals("hi")) {
                                            mainCatModeDAOArrayList.add(new MainCatModeDAO(json_data.getString("id"), json_data.getString("category_name_hindi"), json_data.getString("currency_sign"), json_data.getString("ads_pricing"), json_data.getString("discount"), json_data.getString("notification_charge"), json_data.getString("price_formula"), json_data.getString("rounding_scale"), json_data.getString("special_message_hindi"), json_data.getString("defalut_image")));
                                        }

                                    }

                                    jsonArray = new JSONArray(alertResponse);

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
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // mProgressDialog.dismiss();
            if (mainCatModeDAOArrayList.size() > 0) {
                catHideShow.setVisibility(View.GONE);
                showHideLayout.setVisibility(View.VISIBLE);
            } else {
                showHideLayout.setVisibility(View.GONE);
                catHideShow.setVisibility(View.VISIBLE);
            }
            if (mainCatModeDAOArrayList.size() > 0) {

                Spinner spinnerCustom = (Spinner) v.findViewById(R.id.spinnerMainCategory);
                ArrayAdapter<MainCatModeDAO> adapter = new ArrayAdapter<MainCatModeDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mainCatModeDAOArrayList);
                spinnerCustom.setAdapter(adapter);
                if (mainCatAList != null) {
                    for (int i = 0; i < mainCatAList.size(); i++) {
                        selectSpinnerItemByValue(spinnerCustom, mainCatAList.get(i));
                    }
                }
                spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //  ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                        MainCatModeDAO alertTypeDAO = (MainCatModeDAO) parent.getSelectedItem();
                        //Toast.makeText(getApplicationContext(), "Source ID: " + alertTypeDAO.getId() + ",  Source Name : " + alertTypeDAO.getAlert_name(), Toast.LENGTH_SHORT).show();
                        bc_id = alertTypeDAO.getId();
                        main_cat = alertTypeDAO.getCategory_name();
                        defalut_image = alertTypeDAO.getDefalut_image();
                        fileName = defalut_image;


                        if (!fileName.equals("")) {

                            //  Picasso.with(getActivity()).load(preferences.getString("image_path", "")).noPlaceholder().into((ImageView) imageView);
                            try {
                                if (editfalg.equals("0")) {
                                    if (preferences.getString("image_path", "").contains("http")) {
                                        mArrayUri = new ArrayList<Uri>();
                                        mArrayUri.add(Uri.parse(preferences.getString("image_path", "")));
                                        fileName = preferences.getString("image_path", "");
                                    } else {
                                        imageView.setVisibility(View.VISIBLE);
                                        Picasso.get().load(defalut_image).into(imageView);
                                        mArrayUri = new ArrayList<Uri>();
                                        mArrayUri.add(Uri.parse(defalut_image));
                                    }
                                } else {
                                    imageView.setVisibility(View.VISIBLE);
                                    Picasso.get().load(defalut_image).into(imageView);
                                    mArrayUri = new ArrayList<Uri>();
                                    mArrayUri.add(Uri.parse(defalut_image));
                                }
                            } catch (Exception e) {

                            }
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                        if (!bc_id.equals("0")) {
                            mainCatList.setVisibility(View.VISIBLE);
                            // takePhotoGallery.setVisibility(View.VISIBLE);
                            //takePhotoCamera.setVisibility(View.VISIBLE);
                            // uploadbtn.setVisibility(View.VISIBLE);
                            //rate = Double.parseDouble(alertTypeDAO.getAds_pricing());
                            // discount = Double.parseDouble(alertTypeDAO.getDiscount());
                            notification_charge = Double.parseDouble(alertTypeDAO.getNotification_charge());
                            price_formula = alertTypeDAO.getPrice_formula();
                            rounding_scale = Integer.parseInt(alertTypeDAO.getRounding_scale());
                            if (preferences.getString("ulang", "").equals("en")) {
                                special_message = alertTypeDAO.getSpecial_message();
                            } else if (preferences.getString("ulang", "").equals("hi")) {
                                special_message = alertTypeDAO.getSpecial_message();
                            }
                            if (preferences.getInt("provalid", 0) == 0) {
                                rate = Double.parseDouble(alertTypeDAO.getAds_pricing());
                                discount = Double.parseDouble(alertTypeDAO.getDiscount());
                            } else {
                                rate = 0.0;
                                discount = 0.0;
                            }
                            currency_sign = alertTypeDAO.getCurrency_sign();
                            prefEditor.putString("currency_sign", currency_sign);
                            prefEditor.commit();
                            edttitle.setText(discountads + alertTypeDAO.getCategory_name());
                            asyncTask = new InitSubCategorySpinner();
                            asyncTask.execute();
                            // new InitSubCategorySpinner().execute();

                        } else {
                            mainCatList.setVisibility(View.GONE);
                            bc_id = "";
                            main_cat = "";
                            currency_sign = "";
                            rate = 1.0;
                            takePhotoGallery.setVisibility(View.GONE);
                            takePhotoCamera.setVisibility(View.GONE);
                            //  uploadbtn.setVisibility(View.GONE);
                        }

                        if (thours != 0) {
                          /*  if (AppStatus.getInstance(getActivity()).isOnline()) {
                                new dueFeesAvailable().execute();
                            } else {

                                Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                            }*/
                            if (discount > 0) {
                                BigDecimal bd = new BigDecimal((Math.sqrt(thours) * rate) * ((100 - discount) / 100)).setScale(rounding_scale, RoundingMode.HALF_UP);
                                cost = "" + bd.doubleValue();
                                sprice.setVisibility(View.VISIBLE);
                                sprice.setText(special_message);
                            } else {
                                BigDecimal bd = new BigDecimal(Math.sqrt(thours) * rate).setScale(rounding_scale, RoundingMode.HALF_UP);
                                cost = "" + bd.doubleValue();
                                sprice.setVisibility(View.GONE);
                            }
                            //  (Math.sqrt(hours)*ads_pricing + notification_charge * shoppers_nearby)
                            if (preferences.getInt("provalid", 0) == 0) {
                                totalamount = cost;
                                tamount.setText(res.getString(R.string.jtai) + " " + currency_sign + (totalamount));

                            } else {
                                BigDecimal bd = new BigDecimal(Double.parseDouble(cost) * .5).setScale(rounding_scale, RoundingMode.HALF_UP);
                                totalamount = "" + bd.doubleValue();
                                //  totalamount = "" + Double.parseDouble(cost) * .5;
                                tamount.setText(res.getString(R.string.jtai) + " " + currency_sign + (totalamount));
                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }


                });

            } else {
                // Close the progressdialog

            }
        }
    }

    //
    private class InitSubCategorySpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            // mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage(res.getString(R.string.jsql));
            //  mProgressDialog.setIndeterminate(true);
            //  mProgressDialog.setCancelable(false);
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj1 = new JSONObject() {
                {
                    try {
                        put("business_user_id", preferences.getString("business_user_id", ""));
                        put("bc_id", bc_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            alertSubResponse = serviceAccess.SendHttpPost(Config.URL_GETALLSUBCATEGORYBUSINESSUSERBYID, jsonLeadObj1);
            Log.i("resp", "alertResponse" + alertResponse);
            try {
                if (alertSubResponse.compareTo("") != 0) {
                    if (isJSONValid(alertSubResponse)) {

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    subCatModeDAOArrayList.clear();

                                    JSONArray LeadSourceJsonObj = new JSONArray(alertSubResponse);
                                    String temp = "";
                                    WebClient webClient = new WebClient();
                                    subCatNameArrayListTemp = new ArrayList<>();
                                    for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                        JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);

                                        if (subCatAList != null) {
                                            if (preferences.getString("ulang", "").equals("en")) {
                                                if (subCatAList.contains(json_data.getString("subcategory_name"))) {
                                                    if (preferences.getString("ulang", "").equals("en")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name"), true, json_data.getString("image_path")));
                                                        //  temp = json_data.getString("subcategory_name");

                                                        subCatNameArrayListTemp.add(json_data.getString("subcategory_name"));
                                                    } else if (preferences.getString("ulang", "").equals("hi")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name_hindi"), true, json_data.getString("image_path")));
                                                        //  temp = json_data.getString("subcategory_name");

                                                        subCatNameArrayListTemp.add(json_data.getString("subcategory_name_hindi"));

                                                    }

                                                } else {
                                                    if (preferences.getString("ulang", "").equals("en")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name"), false, json_data.getString("image_path")));
                                                    } else if (preferences.getString("ulang", "").equals("hi")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name_hindi"), false, json_data.getString("image_path")));

                                                    }

                                                }
                                            } else if (preferences.getString("ulang", "").equals("hi")) {
                                                if (subCatAList.contains(json_data.getString("subcategory_name_hindi"))) {
                                                    if (preferences.getString("ulang", "").equals("en")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name"), true, json_data.getString("image_path")));
                                                        //  temp = json_data.getString("subcategory_name");

                                                        subCatNameArrayListTemp.add(json_data.getString("subcategory_name"));
                                                    } else if (preferences.getString("ulang", "").equals("hi")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name_hindi"), true, json_data.getString("image_path")));
                                                        //  temp = json_data.getString("subcategory_name");

                                                        subCatNameArrayListTemp.add(json_data.getString("subcategory_name_hindi"));

                                                    }

                                                } else {
                                                    if (preferences.getString("ulang", "").equals("en")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name"), false, json_data.getString("image_path")));
                                                    } else if (preferences.getString("ulang", "").equals("hi")) {
                                                        subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name_hindi"), false, json_data.getString("image_path")));

                                                    }

                                                }
                                            }
                                        } else {
                                            if (preferences.getString("ulang", "").equals("en")) {
                                                subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name"), false, json_data.getString("image_path")));
                                            } else if (preferences.getString("ulang", "").equals("hi")) {
                                                subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name_hindi"), false, json_data.getString("image_path")));

                                            }

                                        }
                                    }
                                    temp = webClient.convertArrayListToStringWithComma(subCatNameArrayListTemp);
                                    String temp1 = edttitle.getText().toString().trim();
                                    edttitle.setText(temp1 + "(" + temp + ")");
                                    jsonArray = new JSONArray(alertSubResponse);

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
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            //   mProgressDialog.dismiss();

            if (subCatModeDAOArrayList.size() > 0) {
                subCatListAdpter = new AdsSubCatListAdpter(getActivity(), subCatModeDAOArrayList);
                mainCatList.setAdapter(subCatListAdpter);
                mainCatList.setLayoutManager(new LinearLayoutManager(getActivity()));
                subCatListAdpter.notifyDataSetChanged();

            } else {

                // Close the progressdialog


            }


        }
    }


    class ImagegenrateTask extends AsyncTask<String, Void, String> {
        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "Image Processing",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                bitmap = TextToImageEncode(link);

                // imageView.setImageBitmap(bitmap);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/AlertMeUBusiness");
                myDir.mkdirs();
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (WriterException e) {
                e.printStackTrace();
            }

            // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFCKS" + File.separator + "ashok.jpg";
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeUBusiness" + File.separator + fileName;

            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            String bmp1 = "" + bmp;
            Log.d("bytes", "" + bmp1);


            if (!bmp1.equals("null")) {
                // bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                // imageView.setImageBitmap(bmp);
                result = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
                Canvas canvas = new Canvas(result);
                canvas.drawBitmap(bmp, 0f, 0f, null);
                canvas.drawBitmap(bitmap, 100, 100, null);

                result.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/AlertMeUBusiness");
                myDir.mkdirs();
                Random generator = new Random();
                int n = 1000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                final_image = fname;
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    result.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // grid.setVisibility(View.GONE);
                //  FileNameDAO f2 = new FileNameDAO(final_image, getPathFromUri(CreateQRCodeActivity.this, Uri.fromFile(file)));
                // al.add(f2);

                f.setFile_name(final_image);
                f.setFile_path(getPathFromUri(getActivity(), Uri.fromFile(file)));
                mArrayUri.add(Uri.fromFile(file));
                imagesEncodedList.add("" + file);


            } else {

                Log.d("bytes", "" + bmp);
            }

            return sResponse = "success";
        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    for (int i = 0; i < al.size(); i++) {
                        FilePathStrings[i] = al.get(i).getFile_path();
                        // Get the name image file
                        FileNameStrings[i] = al.get(i).getFile_name();
                    }
                    // Locate the GridView in gridview_main.xml
                    grid = (GridView) v.findViewById(R.id.gridview);
                    grid.setVisibility(View.VISIBLE);
                    // Pass String arrays to LazyAdapter Class
                    adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
                    // Set the LazyAdapter to the GridView
                    grid.setAdapter(adapter);

                    imageView.setImageBitmap(result);
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    class ImagegenrateTaskGallery extends AsyncTask<String, Void, String> {
        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "Image Processing",
                    "Please wait...", true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                bitmap = TextToImageEncode(link);

                // imageView.setImageBitmap(bitmap);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/AlertMeUBusiness");
                myDir.mkdirs();
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (WriterException e) {
                e.printStackTrace();
            }

            // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AFCKS" + File.separator + "ashok.jpg";
            // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeUBusiness" + File.separator + fileName;
            String filePath = FilePathStrings[0];
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            String bmp1 = "" + bmp;
            Log.d("bytes", "" + bmp1);


            if (!bmp1.equals("null")) {
                // bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                // imageView.setImageBitmap(bmp);
                result = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
                Canvas canvas = new Canvas(result);
                canvas.drawBitmap(bmp, 0f, 0f, null);
                canvas.drawBitmap(bitmap, 100, 100, null);

                result.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/AlertMeUBusiness");
                myDir.mkdirs();
                Random generator = new Random();
                int n = 1000;
                n = generator.nextInt(n);
                String fname = "Image-" + n + ".jpg";
                final_image = fname;
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    result.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // grid.setVisibility(View.GONE);
                //  FileNameDAO f2 = new FileNameDAO(final_image, getPathFromUri(CreateQRCodeActivity.this, Uri.fromFile(file)));
                // al.add(f2);

                f.setFile_name(final_image);
                f.setFile_path(getPathFromUri(getActivity(), Uri.fromFile(file)));
                mArrayUri.add(Uri.fromFile(file));
                imagesEncodedList.add("" + file);


            } else {

                Log.d("bytes", "" + bmp);
            }

            return sResponse = "success";
        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (sResponse != null) {
                    for (int i = 0; i < al.size(); i++) {
                        FilePathStrings[i] = al.get(i).getFile_path();
                        // Get the name image file
                        FileNameStrings[i] = al.get(i).getFile_name();
                    }
                    // Locate the GridView in gridview_main.xml
                    grid = (GridView) v.findViewById(R.id.gridview);
                    grid.setVisibility(View.VISIBLE);
                    // Pass String arrays to LazyAdapter Class
                    adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
                    // Set the LazyAdapter to the GridView
                    grid.setAdapter(adapter);

                    // imageView.setImageBitmap(result);
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private class addLocationDataDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.juload));
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
                        put("description", description);
                        put("title", title);
                        put("flag", "advertisement");
                        put("describe_limitations", describe_limitations);
                        put("rq_code", link);
                        put("sdate", sdate);
                        put("stime", stime);
                        put("edate", edate);
                        put("etime", etime);
                        put("status", "2");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };


            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            addImageLocationResponse = serviceAccess.SendHttpPost(Config.URL_ADDBUSINESSDATA, jsonLeadObj);
            Log.i("resp", "addRequestChangeResponse" + addImageLocationResponse);


            if (addImageLocationResponse.compareTo("") != 0) {
                if (isJSONValid(addImageLocationResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addImageLocationResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        id = jObject.getString("id");

                        if (status) {

                            jsonArraySync = new JSONArray();
                            for (i = 0; i < subCatArrayList.size(); i++) {
                                jsonObjectSync = new JSONObject();
                                try {
                                    jsonObjectSync.put("business_user_id", preferences.getString("business_user_id", ""));
                                    jsonObjectSync.put("business_main_category_id", mainCatArrayList.get(i));
                                    jsonObjectSync.put("business_subcategory_id", subCatArrayList.get(i));
                                    jsonObjectSync.put("business_id", id);
                                    jsonArraySync.put(jsonObjectSync);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                jsonNotifyObj = new JSONObject() {
                                    {
                                        try {

                                            put("description", description);
                                            put("title", title);
                                            put("business_user_id", preferences.getString("business_user_id", ""));
                                            put("business_main_category_id", mainCatArrayList.get(i));
                                            put("business_subcategory_id", subCatArrayList.get(i));
                                            put("units_for_area", preferences.getInt("units_for_area", 0));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e("json exception", "json exception" + e);
                                        }
                                    }
                                };
                                response = serviceAccess.SendHttpPost(Config.URL_SENDBUSINESSTOUSERNOTIFICATION, jsonNotifyObj);
                                Log.i("json", "response" + response);
                            }

                            try {
                                syncJsonObject = new JSONObject();
                                syncJsonObject.put("shopperData", jsonArraySync);
                                Log.d("shopperData", "" + syncJsonObject);


                                Thread objectThread = new Thread(new Runnable() {
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        final WebClient serviceAccess = new WebClient();

                                        syncDataesponse = serviceAccess.SendHttpPost(Config.URL_SHOPPERCATSYNCDATA, syncJsonObject);
                                        Log.i("response", "response" + response);
                                        Log.i("resp", "syncDataesponse" + syncDataesponse);
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                handler.post(new Runnable() { // This thread runs in the UI
                                                    @Override
                                                    public void run() {
                                                        if (syncDataesponse.compareTo("") == 0) {

                                                        } else {

                                                            try {
                                                                JSONObject jObject = new JSONObject(syncDataesponse);
                                                                status = jObject.getBoolean("status");
                                                                if (status) {

                                                                    Toast.makeText(getActivity(), res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();
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


                            } catch (JSONException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                        jsonArray = new JSONArray(addImageLocationResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {


                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                if (mArrayUri.size() > 0) {
                    for (int i = 0; i < imagesEncodedList.size(); i++) {
                        map.add(imagesEncodedList.get(i).toString());
                    }
                    new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                } else {

                }

            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();


            }

        }
    }

    class ImageUploadTask extends AsyncTask<String, Void, String> {
        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "",
                    res.getString(R.string.jpw), true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = "https://www.alertmeu.com/LocationImages/uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                Bitmap bitmap = decodeFile(map.get(i));
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 1000, true);
                File file = new File(map.get(i));
                // HttpClient httpClient = new DefaultHttpClient();
                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();

                entity.addPart("user_id", new StringBody("199"));
                entity.addPart("club_id", new StringBody("10"));
                entity.addPart("club_image", new ByteArrayBody(data, "image/jpeg", params[1]));
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
            String temp = getFileName(mArrayUri.get(count));
            final String image_status;
            if (temp.contains("Image-")) {
                image_status = "1";
            } else {
                image_status = "0";
            }
            jsonLeadObjReq = new JSONObject() {
                {
                    try {

                        put("image_path", "https://www.alertmeu.com/LocationImages/uploads/" + getFileName(mArrayUri.get(count)));
                        put("business_id", id);
                        put("image_status", image_status);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObjReq);
            addRequestAttachResponse = serviceAccess.SendHttpPost(Config.URL_ADDREQUESTATTACHMENT, jsonLeadObjReq);
            Log.i("resp", "addRequestAttachResponse" + addRequestAttachResponse);
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
                        // new ImageUploadTask().execute(count + "", "hm" + count + ".jpg");

                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));

                    } else {


                        getActivity().finish();
                        Toast.makeText(getActivity(), res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();
                        //  mListener.messageReceived(final_image);
                       /* String root = Environment.getExternalStorageDirectory().toString();
                        File f = new File(root + "/AlertMeUBusiness/" + final_image);
                        Uri imageUri = Uri.parse("file://" + f.getAbsolutePath());
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://goo.gl/XsT5Rv");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Share to "));*/
                        Intent intent = new Intent(getActivity(), TabsFragmentActivity.class);
                        intent.putExtra("active", 1);
                        startActivity(intent);
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }

        }
    }

    private class deleteImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            mProgressDialog.setMessage(res.getString(R.string.jdem));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {

                        put("path", image_path.replace("https://www.alertmeu.com/", ""));
                        put("business_id", bid);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            deleteLogoResponse = serviceAccess.SendHttpPost(Config.URL_DELETEBUSINESSADIMAGE, jsonLeadObj);
            Log.i("resp", "deleteLogoResponse" + deleteLogoResponse);
            if (deleteLogoResponse.compareTo("") != 0) {
                if (isJSONValid(deleteLogoResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(deleteLogoResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        jsonArray = new JSONArray(deleteLogoResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {
                imageView.setVisibility(View.GONE);
                deleteimage.setVisibility(View.GONE);
                image_path = "";
                fileName = "";
                takePhotoCamera.setVisibility(View.VISIBLE);
                takePhotoGallery.setVisibility(View.VISIBLE);

            }

        }
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
    private void selectSpinnerItemByValue(Spinner spinner, String value) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public long getTimeDifferenceInMillis(String dateTime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long currentTime = new Date().getTime();
        long endTime = 0;

        try {
            //Parsing the user Inputed time ("yyyy-MM-dd HH:mm:ss")
            endTime = dateFormat.parse(dateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

        if (endTime > currentTime)
            return endTime - currentTime;
        else
            return 0;
    }

//

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

    private class dueFeesAvailable extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog1 = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog1.setTitle(res.getString(R.string.jpw));
            mProgressDialog1.setIndeterminate(true);
            mProgressDialog1.setCancelable(false);
            mProgressDialog1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonObj = new JSONObject() {
                {
                    try {
                        put("aTime", thours);
                        put("aCost", rate);
                        put("aShoppers", 10);
                        put("discount", discount);
                        put("acountryCode", preferences.getString("country_code", ""));
                        put("anotifyFlag", n_flag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            balanceAmountResponse = serviceAccess.SendHttpPost(Config.URL_GETCOSTBYCAT, jsonObj);
            Log.i("resp", "balanceAmountResponse" + balanceAmountResponse);
            if (balanceAmountResponse.compareTo("") != 0) {
                if (isJSONValid(balanceAmountResponse)) {
                    JSONArray leadJsonObj = null;
                    try {
                        JSONObject jObject = new JSONObject(balanceAmountResponse);
                        status = jObject.getBoolean("status");
                        if (status) {
                            cost = jObject.getString("balanceamount");
                        } else {
                            cost = "0.0";
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(getActivity(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog1.dismiss();
            if (preferences.getInt("provalid", 0) == 0) {
                totalamount = cost;
                tamount.setText(res.getString(R.string.jtai) + currency_sign + (totalamount));

            } else {
                totalamount = "" + Double.parseDouble(cost) * .5;
                tamount.setText(res.getString(R.string.jtai) + currency_sign + (totalamount));
            }
            //  totalamount =""+ Double.parseDouble(cost);
            //  tamount.setText(res.getString(R.string.jtai) + currency_sign + (cost));


        }
    }

    //
    private boolean checkAndRequestPermissions() {

        int writepermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            // ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
                        if (clickpic == 1) {
                            cameraIntent();
                        }
                        if (clickpic == 2) {
                            galleryIntent();
                        }
                        //else any one or both the permissions are not granted
                    } else {

                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
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
                                                    getActivity().finish();
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
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                        getActivity().finish();
                    }
                });
        dialog.show();
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getLangCode() {
        SharedPreferences preferences = getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "");
        return langCode;
    }

    private void loadLanguage() {
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    //


}
