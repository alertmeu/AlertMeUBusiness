package in.alertmeu.a4b.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

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
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.adapter.GridViewAdapter;
import in.alertmeu.a4b.adapter.SubCatListAdpter;
import in.alertmeu.a4b.models.FileNameDAO;
import in.alertmeu.a4b.models.MainCatModeDAO;
import in.alertmeu.a4b.models.SubCatModeDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.WebClient;

import static android.graphics.BitmapFactory.decodeFile;


public class AddNewAdsFragment1 extends Fragment {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    JSONObject jsonLeadObj, jsonLeadObj1, syncJsonObject, jsonObjectSync, jsonNotifyObj, jsonObject;
    JSONArray jsonArray, jsonArraySync;
    String alertResponse = "", alertSubResponse = "", bc_id = "", sub_id = "", syncDataesponse = "";
    ArrayList<MainCatModeDAO> mainCatModeDAOArrayList;
    ArrayList<SubCatModeDAO> subCatModeDAOArrayList;
    EditText edttitle, edtDescpritipon, describeLimitations;
    String title = "", description = "", response = "", qrCodeResponse = "";
    View v;


    Button takePhotoCamera, takePhotoGallery, genRateQRCode;

    String userChoosenTask = "";
    private Uri fileUri; // file url to store image
    private int REQUEST_CAMERA = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    int PICK_IMAGE_MULTIPLE = 1;
    static String fileName = "";
    static File destination;
    // LogCat tag
    private static final String TAG = AddNewAdsFragment1.class.getSimpleName();
    static final Integer CAMERA = 0x1;

    ArrayList<String> imagesEncodedList = new ArrayList<String>();
    ;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    String imageEncoded;
    private String[] FilePathStrings;
    private String[] FileNameStrings;


    GridView grid;
    ProgressDialog progressDoalog;

    String link = "";
    Thread thread;
    public final static int QRcodeWidth = 500;
    Bitmap bitmap;
    ImageView imageView;
    Button sharebtn, uploadbtn;
    String final_image = "";
    ArrayList<FileNameDAO> al = new ArrayList<FileNameDAO>();
    FileNameDAO f = new FileNameDAO();
    private ProgressDialog dialog;
    Bitmap result;
    GridViewAdapter adapter;


    boolean status, status2;
    int count = 0;
    public ArrayList<String> map = new ArrayList<String>();
    JSONObject jsonLeadObjReq;

    String addImageLocationResponse = "", message = "", id = "", addRequestAttachResponse = "", describe_limitations = "";

    MultipartEntity entity;
    CheckBox cmyList, cshowOnMap, cnearByClients;
    String map_status = "0", mylist_status = "0", nearclient_status = "0";
    SubCatListAdpter subCatListAdpter;
    RecyclerView mainCatList;
    ArrayList<String> subCatArrayList;
    ArrayList<String> mainCatArrayList;
    int i;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_new_ads2, container, false);
        preferences = getActivity().getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        takePhotoCamera = (Button) v.findViewById(R.id.takePhotoCamera);
        takePhotoGallery = (Button) v.findViewById(R.id.takePhotoGallery);
        genRateQRCode = (Button) v.findViewById(R.id.genRateQRCode);
        uploadbtn = (Button) v.findViewById(R.id.uploadbtn);
        imageView = (ImageView) v.findViewById(R.id.imageView);
        edttitle = (EditText) v.findViewById(R.id.edttitle);
        edtDescpritipon = (EditText) v.findViewById(R.id.edtDescpritipon);
        describeLimitations = (EditText) v.findViewById(R.id.describeLimitations);
        mainCatList = (RecyclerView) v.findViewById(R.id.mainCatList);
        subCatModeDAOArrayList = new ArrayList<>();
        if (AppStatus.getInstance(getActivity()).isOnline()) {
            new initMainCategorySpinner().execute();
        } else {

            Toast.makeText(getActivity(), Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
        }

        takePhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        takePhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
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
                                            new ImagegenrateTask().execute();


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
                if (validate(link, title, description, final_image)) {
                    List<SubCatModeDAO> stList = ((SubCatListAdpter) subCatListAdpter).getSservicelist();
                    String data1 = "";
                    String data2 = "";
                    subCatArrayList = new ArrayList<>();
                    mainCatArrayList = new ArrayList<>();
                    for (int i = 0; i < stList.size(); i++) {
                        SubCatModeDAO serviceListDAO = stList.get(i);
                        if (serviceListDAO.isSelected() == true) {
                            data1 = serviceListDAO.getId().toString();
                            data2 = serviceListDAO.getBc_id().toString();
                            subCatArrayList.add(data1);
                            mainCatArrayList.add(data2);

                        }
                    }
                    new addLocationDataDetails().execute();
                }
                // Toast.makeText(getActivity(),""+subCatArrayList.size(),Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private boolean validate(String link, String title, String description, String final_image) {
        boolean isValidate = false;
        if (link.trim().equals("")) {
            Toast.makeText(getActivity(), "Please select main category", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (title.trim().equals("")) {
            Toast.makeText(getActivity(), "Please Enter Title", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (description.trim().equals("")) {
            Toast.makeText(getActivity(), "Please Enter Description", Toast.LENGTH_LONG).show();
            isValidate = false;

        } else if (final_image.trim().equals("")) {
            Toast.makeText(getActivity(), "Please select image", Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent();
        intent.setType("image/*");
        // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
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
        File folder = new File(extStorageDirectory, "AlertMeU");
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
            fileName = System.currentTimeMillis() + ".jpg";
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

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    mArrayUri.add(mImageUri);

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


                    File myFile = new File(mImageUri.getPath());

                    Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(getActivity(), mImageUri));

                    imagesEncodedList.add(getPathFromUri(getActivity(), mImageUri));

                    // Get the path of the image file
                    FilePathStrings = new String[1];
                    FileNameStrings = new String[1];
                    FilePathStrings[0] = getPathFromUri(getActivity(), mImageUri);
                    // Get the name image file
                    FileNameStrings[0] = getFileName(mImageUri);
                    // FileNameDAO f1 = new FileNameDAO(getFileName(mImageUri), getPathFromUri(CreateQRCodeActivity.this, mImageUri));
                    //al.add(f1);
                    f.setFile_name(getFileName(mImageUri));
                    f.setFile_path(getPathFromUri(getActivity(), mImageUri));
                    // Locate the GridView in gridview_main.xml
                    grid = (GridView) v.findViewById(R.id.gridview);
                    // Pass String arrays to LazyAdapter Class
                    adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
                    // Set the LazyAdapter to the GridView
                    grid.setAdapter(adapter);

                    new ImagegenrateTaskGallery().execute();

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
            */    }
            } else if (requestCode == REQUEST_CAMERA) {
                //  onCaptureImageResult(data);

                if (resultCode == getActivity().RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity(true);


                } else if (resultCode == getActivity().RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getActivity(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getActivity(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }


            } else {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Exception", "" + e);
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
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

            BitmapFactory.Options options = new BitmapFactory.Options();
            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            mArrayUri.add(Uri.fromFile(destination));

            FilePathStrings = new String[1];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[1];

            imagesEncodedList.add("" + destination);// imagesEncodedList.add(destination);


            // Get the path of the image file

            FilePathStrings[0] = "" + destination;
            // Get the name image file
            FileNameStrings[0] = fileName;

            // Locate the GridView in gridview_main.xml
            grid = (GridView) v.findViewById(R.id.gridview);
            // Pass String arrays to LazyAdapter Class
            adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
            // Set the LazyAdapter to the GridView
            grid.setAdapter(adapter);
            // Capture gridview item click


            new ImagegenrateTask().execute();

        } else {

        }
    }


    private class initMainCategorySpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            alertResponse = serviceAccess.SendHttpPost(Config.URL_GETALLMAINCATEGORYBYBUSINESSUSER, jsonLeadObj);

            Log.i("resp", "alertResponse" + alertResponse);
            if (alertResponse.compareTo("") != 0) {
                if (isJSONValid(alertResponse)) {

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                mainCatModeDAOArrayList = new ArrayList<>();
                                mainCatModeDAOArrayList.add(new MainCatModeDAO("0", "Select Category"));
                                JSONArray LeadSourceJsonObj = new JSONArray(alertResponse);
                                for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                    JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);
                                    mainCatModeDAOArrayList.add(new MainCatModeDAO(json_data.getString("id"), json_data.getString("category_name")));

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
                            Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (alertResponse.compareTo("") != 0) {

                Spinner spinnerCustom = (Spinner) v.findViewById(R.id.spinnerMainCategory);
                ArrayAdapter<MainCatModeDAO> adapter = new ArrayAdapter<MainCatModeDAO>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mainCatModeDAOArrayList);
                spinnerCustom.setAdapter(adapter);
                spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#1c5fab"));
                        MainCatModeDAO alertTypeDAO = (MainCatModeDAO) parent.getSelectedItem();
                        //Toast.makeText(getApplicationContext(), "Source ID: " + alertTypeDAO.getId() + ",  Source Name : " + alertTypeDAO.getAlert_name(), Toast.LENGTH_SHORT).show();
                        bc_id = alertTypeDAO.getId();

                        if (!bc_id.equals("0")) {
                            mainCatList.setVisibility(View.VISIBLE);
                            takePhotoGallery.setVisibility(View.VISIBLE);
                            takePhotoCamera.setVisibility(View.VISIBLE);
                            uploadbtn.setVisibility(View.VISIBLE);
                            new initSubCategorySpinner().execute();
                        } else {
                            mainCatList.setVisibility(View.GONE);
                            bc_id = "";
                            takePhotoGallery.setVisibility(View.GONE);
                            takePhotoCamera.setVisibility(View.GONE);
                            uploadbtn.setVisibility(View.GONE);
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
    private class initSubCategorySpinner extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
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
            qrCodeResponse = serviceAccess.SendHttpPost(Config.URL_GETQRCODEFORBUSINESS, jsonLeadObj1);

            Log.i("resp", "leadListResponse" + alertSubResponse);

            if (alertSubResponse.compareTo("") != 0) {
                if (isJSONValid(alertSubResponse)) {

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                subCatModeDAOArrayList.clear();
                                JSONArray LeadSourceJsonObj = new JSONArray(alertSubResponse);
                                for (int i = 0; i < LeadSourceJsonObj.length(); i++) {
                                    JSONObject json_data = LeadSourceJsonObj.getJSONObject(i);
                                    subCatModeDAOArrayList.add(new SubCatModeDAO(json_data.getString("id"), json_data.getString("bc_id"), json_data.getString("subcategory_name")));
                                }
                                jsonObject = new JSONObject(qrCodeResponse);
                                status = jsonObject.getBoolean("status");
                                if (status) {
                                    link = jsonObject.getString("qrcode");
                                } else {
                                    link = "";
                                }
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
                            Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Please check your network connection.", Toast.LENGTH_LONG).show();
                    }
                });

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();

            if (subCatModeDAOArrayList.size() > 0) {

                subCatListAdpter = new SubCatListAdpter(getActivity(), subCatModeDAOArrayList);
                mainCatList.setAdapter(subCatListAdpter);
                mainCatList.setLayoutManager(new LinearLayoutManager(getActivity()));
                subCatListAdpter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            } else {

                // Close the progressdialog

                mProgressDialog.dismiss();

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
                File myDir = new File(root + "/AlertMeU");
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
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + fileName;

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
                File myDir = new File(root + "/AlertMeU");
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
                File myDir = new File(root + "/AlertMeU");
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
            // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlertMeU" + File.separator + fileName;
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
                File myDir = new File(root + "/AlertMeU");
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
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Uploading...");
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

                                                                    Toast.makeText(getActivity(), "Data save successfully", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(getActivity(), "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(getActivity(), "Please check your network connection.", Toast.LENGTH_LONG).show();
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
            dialog = ProgressDialog.show(getActivity(), "Uploading",
                    "Please wait...", true);
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
                        Toast.makeText(getActivity(), "Data save successfully", Toast.LENGTH_SHORT).show();
                        //  mListener.messageReceived(final_image);
                       /* String root = Environment.getExternalStorageDirectory().toString();
                        File f = new File(root + "/AlertMeU/" + final_image);
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
}
