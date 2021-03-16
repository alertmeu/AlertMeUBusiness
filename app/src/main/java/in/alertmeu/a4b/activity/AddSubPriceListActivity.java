package in.alertmeu.a4b.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.CompressFile;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.ExifUtil;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.Utility;
import in.alertmeu.a4b.utils.WebClient;
import in.alertmeu.a4b.view.UploadImageView;

import static android.graphics.BitmapFactory.decodeFile;

public class AddSubPriceListActivity extends AppCompatActivity {
    static SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String mainPriListId = "", mainPriListName = "", subbc_id = "", maincat_id = "", subcategory_name = "", image_path = "", title = "", desc = "", price = "";

    Resources res;
    ImageView profilePic;
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
    Button submit;
    int count = 0;
    EditText titleEdtTxt, descEdtTxt, priceEdtTxt;
    MultipartEntity entity;
    private ProgressDialog dialog;
    JSONObject jsonLeadObjReq;
    String addRequestAttachResponse = "", bar_code = "", id = "";
    private static Listener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_add_sub_price_list);
        fileName = "";
        preferences = getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        Intent intent = getIntent();
        subbc_id = intent.getStringExtra("subbc_id");
        maincat_id = intent.getStringExtra("maincat_id");
        subcategory_name = intent.getStringExtra("subcategory_name");
        image_path = intent.getStringExtra("image_path");
        mainPriListId = intent.getStringExtra("mainPriListId");
        mainPriListName = intent.getStringExtra("mainPriListName");
        profilePic = (ImageView) findViewById(R.id.profilePic);
        submit = (Button) findViewById(R.id.submit);
        titleEdtTxt = (EditText) findViewById(R.id.titleEdtTxt);
        descEdtTxt = (EditText) findViewById(R.id.descEdtTxt);
        priceEdtTxt = (EditText) findViewById(R.id.priceEdtTxt);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
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
    }

    public boolean validate(String image_name, String title) {
        boolean isValidate = false;
        if (image_name.equals("")) {
            Toast.makeText(AddSubPriceListActivity.this, res.getString(R.string.jpstsui), Toast.LENGTH_LONG).show();
        } else if (title.equals("")) {
            Toast.makeText(AddSubPriceListActivity.this, "Please Enter Title", Toast.LENGTH_LONG).show();
        } else {
            isValidate = true;
        }
        return isValidate;
    }

    //profile photo
    private void selectImage() {
        final CharSequence[] items = {res.getString(R.string.jtp), res.getString(R.string.jtcfl)};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddSubPriceListActivity.this);
        builder.setTitle(res.getString(R.string.japh));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddSubPriceListActivity.this);

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
                        imagesEncodedList.add(getPathFromUri(AddSubPriceListActivity.this, mImageUri));
                        FileNameStrings[0] = getFileName(mImageUri);
                        fileName = getFileName(mImageUri);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(AddSubPriceListActivity.this, mImageUri), options);
                        Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(AddSubPriceListActivity.this, mImageUri), bitmap);
                        profilePic.setImageBitmap(orientedBitmap);
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
                        Toast.makeText(AddSubPriceListActivity.this, res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
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
            profilePic.setImageBitmap(bitmap);
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
            dialog = ProgressDialog.show(AddSubPriceListActivity.this, "", res.getString(R.string.jpw), true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                File file = new File(map.get(i));
                File f = CompressFile.getCompressedImageFile(file, AddSubPriceListActivity.this);
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
                        Toast.makeText(AddSubPriceListActivity.this, res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();
                        finish();
                        mListener.messageReceived(addRequestAttachResponse);
                    }

                }

            } catch (Exception e) {
                Toast.makeText(AddSubPriceListActivity.this, e.getMessage(),
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

        int writepermission = ContextCompat.checkSelfPermission(AddSubPriceListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(AddSubPriceListActivity.this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(AddSubPriceListActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddSubPriceListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(AddSubPriceListActivity.this, Manifest.permission.CAMERA)) {
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
        new AlertDialog.Builder(AddSubPriceListActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddSubPriceListActivity.this);
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