package in.alertmeu.a4b.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.ExifUtil;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.WebClient;

import static android.graphics.BitmapFactory.decodeFile;

public class ContactUsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ImageView btnSend;
    EditText descEdtTxt;
    ImageView imageOne, imageTwo, imageThree;
    String desc = "", balanceAmountResponse = "";
    String imageOneName = "", imageTwoName = "", imageThreename = "", addContactUsResponse = "", message = "", img_1 = "", img_2 = "", img_3 = "";
    Bitmap myBitmap;
    Uri picUri;

    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    public ArrayList<String> map = new ArrayList<String>();
    static List<String> imagesEncodedList;
    ProgressDialog mProgressDialog;
    private JSONObject jsonLeadObj, jsonLeadObj1, jsonLeadObjReq;
    JSONArray jsonArray;
    boolean status;
    int count = 0, clc = 0;
    private ProgressDialog dialog;
    MultipartEntity entity;
    String localTime;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    WebView webview;
    TextView website, Phone, Email;
    ProgressDialog mProgressDialog1;
    JSONObject jsonObj;
    String phone, email;

    private Uri fileUri1 = null, fileUri2 = null, fileUri3 = null; // file url to store image
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = ContactUsActivity.class.getSimpleName();
    int ccnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_contact_us);
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        btnSend = (ImageView) findViewById(R.id.btnSend);
        descEdtTxt = (EditText) findViewById(R.id.descEdtTxt);
        imageOne = (ImageView) findViewById(R.id.imageOne);
        imageTwo = (ImageView) findViewById(R.id.imageTwo);
        imageThree = (ImageView) findViewById(R.id.imageThree);
        imagesEncodedList = new ArrayList<String>();
        website = (TextView) findViewById(R.id.website);
        Phone = (TextView) findViewById(R.id.Phone);
        Email = (TextView) findViewById(R.id.Email);
        webview = new WebView(this);
        if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
            new dueFeesAvailable().execute();
        } else {

            Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        localTime = date.format(currentLocalTime);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {

                    desc = descEdtTxt.getText().toString().trim();
                    if (!desc.equals("")) {
                        if (clc == 0) {

                            mArrayUri.clear();
                            imagesEncodedList.clear();
                            if (!img_1.equals("")) {
                                mArrayUri.add(fileUri1);
                                imagesEncodedList.add(getPathFromUri(ContactUsActivity.this, fileUri1));
                            }
                            if (!img_2.equals("")) {
                                mArrayUri.add(fileUri2);
                                imagesEncodedList.add(getPathFromUri(ContactUsActivity.this, fileUri2));
                            }
                            if (!img_3.equals("")) {
                                mArrayUri.add(fileUri3);
                                imagesEncodedList.add(getPathFromUri(ContactUsActivity.this, fileUri3));
                            }
                            new addContactUsDetails().execute();
                            clc++;
                        }
                        // Toast.makeText(getApplicationContext(), imageOneName + "," + imageTwoName + "," + imageThreename + "size:" + mArrayUri.size(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jed), Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccnt = 1;
                if (checkAndRequestPermissions()) {
                    if (imageOneName.equals("")) {

                    } else {

                    }
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);*/
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pickPhoto.setType("image/*");
                    startActivityForResult(pickPhoto, 200);
                }
            }
        });
        imageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccnt = 2;
                if (checkAndRequestPermissions()) {
                    if (imageTwoName.equals("")) {

                    } else {
                    }
              /*  Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 300);*/
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pickPhoto.setType("image/*");
                    startActivityForResult(pickPhoto, 300);
                }
            }
        });
        imageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccnt = 3;
                if (checkAndRequestPermissions()) {
                    if (imageThreename.equals("")) {

                    } else {
                    }
               /* Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 400);*/
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pickPhoto.setType("image/*");
                    startActivityForResult(pickPhoto, 400);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == 200 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    // mArrayUri.add(mImageUri);
                    fileUri1 = mImageUri;
                    if (!mImageUri.toString().contains("mediakey")) {
                        picUri = mImageUri;
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        cursor.close();
                        File myFile = new File(mImageUri.getPath());
                        Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(ContactUsActivity.this, mImageUri));
                        //  imagesEncodedList.add(getPathFromUri(ContactUsActivity.this, mImageUri));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // down sizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 1;
                        final Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(ContactUsActivity.this, mImageUri), options);
                        //
                        Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(ContactUsActivity.this, mImageUri), bitmap);

                        imageOne.setImageBitmap(orientedBitmap);
                        img_1 = "CU_" + preferences.getString("business_user_id", "") + "_lib_2_" + System.currentTimeMillis() + ".png";
                        imageOneName = Config.URL_AlertMeUImage + "uploads/" + img_1;
                    } else {
                        Toast.makeText(ContactUsActivity.this, res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            } else if (requestCode == 300 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    //    mArrayUri.add(mImageUri);
                    fileUri2 = mImageUri;
                    if (!mImageUri.toString().contains("mediakey")) {
                        picUri = mImageUri;
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);


                        cursor.close();


                        File myFile = new File(mImageUri.getPath());

                        Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(ContactUsActivity.this, mImageUri));

                        // imagesEncodedList.add(getPathFromUri(ContactUsActivity.this, mImageUri));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // down sizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 1;
                        final Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(ContactUsActivity.this, mImageUri), options);
                        //
                        Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(ContactUsActivity.this, mImageUri), bitmap);

                        imageTwo.setImageBitmap(orientedBitmap);
                        img_2 = "CU_" + preferences.getString("business_user_id", "") + "_lib_1_" + System.currentTimeMillis() + ".png";
                        imageTwoName = Config.URL_AlertMeUImage + "uploads/" + img_2;
                    } else {
                        Toast.makeText(ContactUsActivity.this, res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            } else if (requestCode == 400 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    //   mArrayUri.add(mImageUri);
                    fileUri3 = mImageUri;
                    if (!mImageUri.toString().contains("mediakey")) {
                        picUri = mImageUri;
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        cursor.close();
                        File myFile = new File(mImageUri.getPath());
                        Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(ContactUsActivity.this, mImageUri));
                        //  imagesEncodedList.add(getPathFromUri(ContactUsActivity.this, mImageUri));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // down sizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 1;
                        final Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(ContactUsActivity.this, mImageUri), options);
                        //
                        Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(ContactUsActivity.this, mImageUri), bitmap);
                        imageThree.setImageBitmap(orientedBitmap);
                        img_3 = "CU_" + preferences.getString("business_user_id", "") + "_lib_1_" + System.currentTimeMillis() + ".png";
                        imageThreename = Config.URL_AlertMeUImage + "uploads/" + img_3;
                    } else {
                        Toast.makeText(ContactUsActivity.this, res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                    }

                } else {

                }
            } else {
                Toast.makeText(this, res.getString(R.string.jeunp), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Exception", "" + e);
            Toast.makeText(this, res.getString(R.string.jsw), Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * ------------ Helper Methods ----------------------
     */

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


    private class addContactUsDetails extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(ContactUsActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage(res.getString(R.string.jsq));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {


            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", preferences.getString("business_user_id", ""));
                        put("app_type", "2");
                        put("description", desc);
                        put("image_one_path", imageOneName);
                        put("image_two_path", imageTwoName);
                        put("image_three_path", imageThreename);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            addContactUsResponse = serviceAccess.SendHttpPost(Config.URL_ADDCONTACTUS, jsonLeadObj);
            Log.i("resp", "addContactUsResponse" + addContactUsResponse);


            if (addContactUsResponse.compareTo("") != 0) {
                if (isJSONValid(addContactUsResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(addContactUsResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");

                        jsonArray = new JSONArray(addContactUsResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(ContactUsActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(ContactUsActivity.this, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            //  mProgressDialog.dismiss();
            if (status) {

                if (mArrayUri.size() > 0) {
                    for (int i = 0; i < imagesEncodedList.size(); i++) {
                        map.add(imagesEncodedList.get(i).toString());
                    }
                    new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                } else {
                    descEdtTxt.setText("");
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jtcu), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

        }
    }

    class ImageUploadTask extends AsyncTask<String, Void, String> {

        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(ContactUsActivity.this, "", res.getString(R.string.jpw), true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                Bitmap bitmap = decodeFile(map.get(i));
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 1000, true);
                File file = new File(map.get(i));
                // HttpClient httpClient = new DefaultHttpClient();
                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();
                String in = "";
                if (count == 0) {
                    in = img_1;
                }
                if (count == 1) {
                    in = img_2;
                }
                if (count == 2) {
                    in = img_3;
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();

                entity.addPart("user_id", new StringBody("199"));
                entity.addPart("fileName", new StringBody(in));
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
                        Toast.makeText(getApplicationContext(), res.getString(R.string.jtcu), Toast.LENGTH_SHORT).show();
                        finish();

                    }

                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
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

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
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

    private class dueFeesAvailable extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog1 = new ProgressDialog(ContactUsActivity.this);
            // Set progressdialog title
            //    mProgressDialog1.setTitle(res.getString(R.string.jpw));
            //   mProgressDialog1.setIndeterminate(true);
            //   mProgressDialog1.setCancelable(false);
            //  mProgressDialog1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            jsonObj = new JSONObject() {
                {
                    try {
                        put("country_code", preferences.getString("country_code", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj1);
            balanceAmountResponse = serviceAccess.SendHttpPost(Config.URL_GETSUNOBYC, jsonObj);
            Log.i("resp", "balanceAmountResponse" + balanceAmountResponse);


            if (balanceAmountResponse.compareTo("") != 0) {
                if (isJSONValid(balanceAmountResponse)) {
                    JSONArray leadJsonObj = null;
                    try {
                        JSONObject jObject = new JSONObject(balanceAmountResponse);
                        status = jObject.getBoolean("status");
                        if (status) {
                            JSONArray introJsonArray = jObject.getJSONArray("balanceamount");
                            for (int i = 0; i < introJsonArray.length(); i++) {
                                JSONObject introJsonObject = introJsonArray.getJSONObject(i);
                                phone = introJsonObject.getString("phone_no");
                                email = introJsonObject.getString("email_id");
                            }


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    //  Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                }
            } else {

                //  Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // mProgressDialog1.dismiss();
            Phone.setText("Phone / WhatsApp:" + phone);
            Email.setText("Email:" + email);
        }
    }

    private boolean checkAndRequestPermissions() {

        int writepermission = ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(ContactUsActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //finish();
                        if (ccnt == 1) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            pickPhoto.setType("image/*");
                            startActivityForResult(pickPhoto, 200);
                        } else if (ccnt == 2) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            pickPhoto.setType("image/*");
                            startActivityForResult(pickPhoto, 300);
                        } else if (ccnt == 3) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            pickPhoto.setType("image/*");
                            startActivityForResult(pickPhoto, 400);
                        }
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ContactUsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
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
        new AlertDialog.Builder(ContactUsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ContactUsActivity.this);
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
}

