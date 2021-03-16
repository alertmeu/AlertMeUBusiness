package in.alertmeu.a4b.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import in.alertmeu.a4b.Fragments.AddNewAdsFragment;
import in.alertmeu.a4b.Fragments.TabsFragmentActivity;
import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.AdsPreviewActivity;
import in.alertmeu.a4b.activity.AdvertisementPostedActivity;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.CompressFile;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.ExifUtil;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.MySSLSocketFactory;
import in.alertmeu.a4b.utils.WebClient;

import static android.graphics.BitmapFactory.decodeFile;


public class UploadImageView extends DialogFragment {
    Context context;
    SharedPreferences preferences;
    Editor prefEditor;
    int count = 0;
    View registerView;
    private static Listener mListener;
    Resources res;
    ImageView imageView;
    Button uploadbtn;
    Button takePhotoCamera, takePhotoGallery;
    int PICK_IMAGE_MULTIPLE = 1;
    private int REQUEST_CAMERA = 0;
    private Uri fileUri; // file url to store image
    public static final int MEDIA_TYPE_IMAGE = 1;
    // LogCat tag
    private static final String TAG = UploadImageView.class.getSimpleName();
    static String b_id;
    static int ifg = 0;
    static File destination;
    ArrayList<String> imagesEncodedList;
    ArrayList<Uri> mArrayUri;
    String imageEncoded;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    static String fileName = "";
    public ArrayList<String> map = new ArrayList<String>();
    MultipartEntity entity;
    private ProgressDialog dialog;
    JSONObject jsonLeadObjReq;
    String addRequestAttachResponse = "", bar_code = "", id = "";
    int clickpic = 0;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        registerView = inflater.inflate(R.layout.dailog_update_img_ads, null);

        context = getActivity();
        Window window = getDialog().getWindow();

        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.CENTER | Gravity.CENTER);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = 50;
        window.setAttributes(params);
        res = getResources();
        preferences = getActivity().getSharedPreferences("Prefrence", getActivity().MODE_PRIVATE);
        prefEditor = preferences.edit();
        b_id = preferences.getString("business_user_id", "");
        id = preferences.getString("adid", "");
        bar_code = preferences.getString("adqrcode", "");
        uploadbtn = (Button) registerView.findViewById(R.id.uploadbtn);
        imageView = (ImageView) registerView.findViewById(R.id.imageView);
        takePhotoCamera = (Button) registerView.findViewById(R.id.takePhotoCamera);
        takePhotoGallery = (Button) registerView.findViewById(R.id.takePhotoGallery);
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
        uploadbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fileName.equals("")) {
                    if (mArrayUri.size() > 0) {
                        for (int i = 0; i < imagesEncodedList.size(); i++) {
                            map.add(imagesEncodedList.get(i).toString());
                        }
                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));
                    }
                } else {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() { // This thread runs in the UI
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), res.getString(R.string.jpstsui), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    };

                    new Thread(runnable).start();
                }
            }
        });
        return registerView;
    }


    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_DOWN) {
                        //Toast.makeText(getActivity(), "Your information is valuable for us and won't be misused.", Toast.LENGTH_SHORT).show();
                        count++;
                        if (count >= 1) {
                            fileName="";
                            dismiss();
                        }
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

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, PICK_IMAGE_MULTIPLE);
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
                        imagesEncodedList.add(getPathFromUri(getActivity(), mImageUri));
                        fileName = getFileName(mImageUri);
                        ifg = 2;

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        Bitmap bitmap = BitmapFactory.decodeFile(getPathFromUri(getActivity(), mImageUri), options);
                        Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(getActivity(), mImageUri), bitmap);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(orientedBitmap);
                        //  new ImagegenrateTaskGallery().execute();
                    } else {
                        Toast.makeText(getActivity(), res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
                    }
                } else {

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

            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);

        } else {

        }
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

    class ImageUploadTask extends AsyncTask<String, Void, String> {
        String sResponse = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "", res.getString(R.string.jpw), true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String url = Config.URL_AlertMeUImage + "uploadLocImageFiles.php";
                int i = Integer.parseInt(params[0]);
                File file = new File(map.get(i));
                File f = CompressFile.getCompressedImageFile(file, getActivity());
                Bitmap bitmap = decodeFile(f.getAbsolutePath());
                HttpClient httpClient = getNewHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                entity = new MultipartEntity();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                //  scaled.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] data = bos.toByteArray();
                if (preferences.getInt("ifg", 0) == 1) {
                    fileName = "A_" + preferences.getString("business_user_id", "") + "_cam_" + bar_code + "_" + System.currentTimeMillis() + ".png";
                }
                if (preferences.getInt("ifg", 0) == 2) {
                    fileName = "A_" + preferences.getString("business_user_id", "") + "_lib_" + bar_code + "_" + System.currentTimeMillis() + ".png";
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
                        put("business_id", id);
                        put("image_status", 0);


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
                        new ImageUploadTask().execute(count + "", getFileName(mArrayUri.get(count)));

                    } else {
                        fileName="";
                        Toast.makeText(getActivity(), res.getString(R.string.jdus), Toast.LENGTH_SHORT).show();
                        dismiss();
                        mListener.messageReceived(addRequestAttachResponse);
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(),
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

    public static void bindListener(Listener listener) {
        mListener = listener;
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
}