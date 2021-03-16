package in.alertmeu.a4b.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import in.alertmeu.a4b.Fragments.TabsFragmentActivity;
import in.alertmeu.a4b.R;

import in.alertmeu.a4b.utils.Config;

public class AdvertisementPostedActivity extends AppCompatActivity {
    ImageView btnHome, btnBusinessList, btnscanbar, imageView;
    TextView txtDescpritipon, mainCat, subCat, txtValidity, txtBarCode1, limitation, txTtitle;
    String title = "", description = "";
    String stime = "", sdate = "", tsign = "", edate = "", etime = "", rq_code = "", describe_limitations = "";
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    LinearLayout limithideshow, deshideshow;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        res = getResources();
        setContentView(R.layout.activity_advertisement_posted);
        btnHome = (ImageView) findViewById(R.id.btnHome);
        btnBusinessList = (ImageView) findViewById(R.id.btnBusinessList);
        btnscanbar = (ImageView) findViewById(R.id.btnscanbar);
        imageView = (ImageView) findViewById(R.id.imageView);
        txTtitle = (TextView) findViewById(R.id.txTtitle);
        txtDescpritipon = (TextView) findViewById(R.id.txtDescpritipon);
        txtValidity = (TextView) findViewById(R.id.txtValidity);
        txtBarCode1 = (TextView) findViewById(R.id.txtBarCode1);
        mainCat = (TextView) findViewById(R.id.mainCat);
        subCat = (TextView) findViewById(R.id.subCat);
        limithideshow = (LinearLayout) findViewById(R.id.limithideshow);
        deshideshow = (LinearLayout) findViewById(R.id.deshideshow);
        limitation = (TextView) findViewById(R.id.limitation);
        btnBusinessList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdvertisementPostedActivity.this, TabsFragmentActivity.class);
                intent.putExtra("active", "1");
                startActivity(intent);
                finish();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdvertisementPostedActivity.this, HomePageActivity.class);
                intent.putExtra("active", "0");
                startActivity(intent);
                finish();
            }
        });
        btnscanbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdvertisementPostedActivity.this, ScanActivity.class);
                intent.putExtra("active", "2");
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        describe_limitations = intent.getStringExtra("describe_limitations");
        sdate = intent.getStringExtra("sdate");
        stime = intent.getStringExtra("stime");
        edate = intent.getStringExtra("edate");
        etime = intent.getStringExtra("etime");
        rq_code = intent.getStringExtra("rq_code");
        String filePath = intent.getStringExtra("filePath");
        txTtitle.setText(title);
        if (description != null && !description.isEmpty()) {
            deshideshow.setVisibility(View.VISIBLE);
            txtDescpritipon.setText(description);
        }

        if (describe_limitations != null && !describe_limitations.isEmpty()) {
            limithideshow.setVisibility(View.VISIBLE);
            limitation.setText(describe_limitations);
        }

        txtValidity.setText(parseTime(stime, "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", sdate) + " to " + parseTime(etime, "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", edate));
        mainCat.setText(intent.getStringExtra("main_cat"));
        subCat.setText(intent.getStringExtra("subcatname"));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (!filePath.contains("http")) {
            Bitmap b = BitmapFactory.decodeFile(filePath, options);
            File myFile = new File(filePath);
            int imageRotation = getImageRotation(myFile);
            if (imageRotation != 0)
                b = getBitmapRotatedByDegree(b, imageRotation);
            imageView.setImageBitmap(b);
        } else {
            try {
                //Picasso.with(AdvertisementPostedActivity.this).load(filePath).noPlaceholder().into((ImageView) imageView);
                Picasso.get().load(filePath).into(imageView);
            } catch (Exception e) {

            }

        }
        txtBarCode1.setText(rq_code);
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

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

    //
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(AdvertisementPostedActivity.this, HomePageActivity.class);
        startActivity(setIntent);
        finish();

    }

    public static String parseTime(String time, String inFormat, String outFormat) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat(outFormat).format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
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
}
