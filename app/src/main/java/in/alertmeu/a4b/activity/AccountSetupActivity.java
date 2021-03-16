package in.alertmeu.a4b.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.ExifUtil;
import in.alertmeu.a4b.utils.WebClient;

public class AccountSetupActivity extends AppCompatActivity {
    EditText edtNameOb, edtMobileOb, edtEmailOb, edtAddressOb;
    TextView txtEmailOb;
    ImageView showimage, takeimage;
    LinearLayout btnNext;
    String nob = "", mob = "", emailob = "", addressob = "", password = "", mobile_no = "", imageName = "", emailId = "";
    Bitmap myBitmap;
    Uri picUri;

    int PICK_IMAGE_MULTIPLE = 1;
    private int REQUEST_CAMERA = 0;
    private Uri fileUri; // file url to store image/video
    //new camera images
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = AccountSetupActivity.class.getSimpleName();
    static String fileName = "";
    static File destination;
    String imageEncoded;
    static List<String> imagesEncodedList;
    // Declare variables
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    ArrayList<Uri> mArrayUri;
    CountryCodePicker ccp;
    JSONObject jsonObj1;
    String checkEmailResponse = "", msg1 = "";
    TextView warning;
    boolean status1;
    ProgressDialog mProgressDialog;
    JSONObject jsonObject;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_account_setup);
        imagesEncodedList = new ArrayList<String>();
        mArrayUri = new ArrayList<Uri>();
        Intent intent = getIntent();
        password = intent.getStringExtra("password");
        mobile_no = intent.getStringExtra("mobile_no");
        emailId = intent.getStringExtra("emailId");
        edtNameOb = (EditText) findViewById(R.id.edtNameOb);
        edtMobileOb = (EditText) findViewById(R.id.edtMobileOb);
        //  edtEmailOb = (EditText) findViewById(R.id.edtEmailOb);
        edtAddressOb = (EditText) findViewById(R.id.edtAddressOb);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);
        showimage = (ImageView) findViewById(R.id.showimage);
        warning = (TextView) findViewById(R.id.warning);
        takeimage = (ImageView) findViewById(R.id.takeimage);
        txtEmailOb = (TextView) findViewById(R.id.txtEmailOb);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(edtMobileOb);
        mob = "";
        // ccp.setNumberAutoFormattingEnabled(true);
        ccp.isValidFullNumber();
        ccp.setCountryPreference(ccp.getDefaultCountryNameCode());

        if (!emailId.equals("")) {
            txtEmailOb.setVisibility(View.VISIBLE);
            //  edtEmailOb.setVisibility(View.GONE);
            txtEmailOb.setText(emailId);
            // edtEmailOb.setText(emailId);

        } else {
            txtEmailOb.setVisibility(View.GONE);
            // edtEmailOb.setVisibility(View.VISIBLE);
            // edtEmailOb.setText(emailId);
        }
        edtMobileOb.setText(mobile_no);
        takeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                galleryIntent();

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nob = edtNameOb.getText().toString();
                //   mob = edtMobileOb.getText().toString();
                //  emailob = edtEmailOb.getText().toString().trim();
                addressob = edtAddressOb.getText().toString();
                if (validatewe(nob, addressob)) {
                    Intent intent = new Intent(AccountSetupActivity.this, AccountSetupLocationActivity.class);
                    intent.putExtra("mobile_no", mobile_no);
                    intent.putExtra("password", password);
                    intent.putExtra("nob", nob);
                    intent.putExtra("emailob", emailId);
                    intent.putExtra("addressob", addressob);
                    intent.putExtra("imageName", imageName);
                    if (picUri != null && !picUri.equals(Uri.EMPTY)) {
                        //doTheThing()
                        intent.putExtra("imageUri", picUri.toString());
                    } else {
                        //followUri is null or empty
                        intent.putExtra("imageUri", "");
                    }
                    startActivity(intent);

                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        imagesEncodedList.clear();
        mArrayUri.clear();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, res.getString(R.string.jsp)), PICK_IMAGE_MULTIPLE);
    }


    private boolean validatewe(String nob, String addressob) {
        boolean isValidate = false;
        if (nob.equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpsbn), Toast.LENGTH_LONG).show();
        } else if (addressob.trim().equals("")) {
            Toast.makeText(getApplicationContext(), res.getString(R.string.jpea), Toast.LENGTH_LONG).show();
            isValidate = false;

        } else {
            isValidate = true;
        }
        return isValidate;
    }

    /**
     * email validation
     */
    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

    public boolean validateEmail(String email) {
        if (!email.contains("@")) {
            return false;
        }
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
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
                    picUri = mImageUri;
                    mArrayUri.add(mImageUri);

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

                    File myFile = new File(mImageUri.getPath());

                    Log.d("LOG_TAG", "imageToUpload" + getPathFromUri(AccountSetupActivity.this, mImageUri));

                    imagesEncodedList.add(getPathFromUri(AccountSetupActivity.this, mImageUri));

                    // Get the path of the image file
                    // FilePathStrings[i] = myFile.getAbsolutePath();
                    // FilePathStrings[0] = getRealPathFromURI(mImageUri);
                    FilePathStrings[0] = getPathFromUri(AccountSetupActivity.this, mImageUri);
                    // Get the name image file
                    FileNameStrings[0] = getFileName(mImageUri);
                    imageName = getFileName(mImageUri);
                    // Toast.makeText(getApplicationContext(), "Uploading image", Toast.LENGTH_SHORT).show();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 1;
                    Bitmap bitmap = BitmapFactory.decodeFile(FilePathStrings[0], options);
                    //
                   /* //1
                    int imageRotation = getImageRotation(myFile);
                    if (imageRotation != 0)
                        bitmap = getBitmapRotatedByDegree(bitmap, imageRotation);
                    //*/

                    Bitmap orientedBitmap = ExifUtil.rotateBitmap(getPathFromUri(AccountSetupActivity.this, mImageUri), bitmap);

                    // Set the decoded bitmap into ImageView
                    showimage.setImageBitmap(orientedBitmap);

                } else {

                }
            } else if (requestCode == REQUEST_CAMERA) {
                //  onCaptureImageResult(data);

                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    // launchUploadActivity(true);


                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(), res.getString(R.string.junc), Toast.LENGTH_SHORT).show();

                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(), res.getString(R.string.jsunc), Toast.LENGTH_SHORT).show();
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
        File folder = new File(extStorageDirectory, "AlertMeU");
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

            // fileName = preferences.getString("first_name", "") + ".jpg";
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

    private class userCheckEmailsCode extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(AccountSetupActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Checking Email Id...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonObj1 = new JSONObject() {
                {
                    try {

                        put("email_id", emailob);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonObj1);
            checkEmailResponse = serviceAccess.SendHttpPost(Config.URL_CHECKINGEMAILADDRESS, jsonObj1);
            Log.i("resp", "registrationResponse" + checkEmailResponse);


            if (checkEmailResponse.compareTo("") != 0) {
                if (isJSONValid(checkEmailResponse)) {


                    try {

                        jsonObject = new JSONObject(checkEmailResponse);
                        status1 = jsonObject.getBoolean("status");
                        msg1 = jsonObject.getString("message");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    // Toast.makeText(getApplicationContext(), "Please check your webservice", Toast.LENGTH_LONG).show();
                }
            } else {

                // Toast.makeText(getApplicationContext(), "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mProgressDialog.dismiss();
            if (status1) {
                warning.setVisibility(View.GONE);
                Intent intent = new Intent(AccountSetupActivity.this, AccountSetupLocationActivity.class);
                intent.putExtra("mobile_no", mobile_no);
                intent.putExtra("password", password);
                intent.putExtra("nob", nob);
                intent.putExtra("emailob", emailId);
                intent.putExtra("addressob", addressob);
                intent.putExtra("imageName", imageName);
                if (picUri != null && !picUri.equals(Uri.EMPTY)) {
                    //doTheThing()
                    intent.putExtra("imageUri", picUri.toString());
                } else {
                    //followUri is null or empty
                    intent.putExtra("imageUri", "");
                }
                startActivity(intent);

            } else {
                //Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();
                warning.setVisibility(View.VISIBLE);
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

    protected boolean isJSONValid(String registrationResponse) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(registrationResponse);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(registrationResponse);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
