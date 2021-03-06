package in.alertmeu.a4b.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.FullScreenViewActivity;

import in.alertmeu.a4b.models.AdvertisementDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.Utility;
import in.alertmeu.a4b.utils.WebClient;
import in.alertmeu.a4b.view.AddPEntryView;
import in.alertmeu.a4b.view.UploadImageView;


public class ActiveAdvertisementListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<AdvertisementDAO> data;
    AdvertisementDAO current;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String user_id = "", deActiveAdsResponse = "", baid = "";
    boolean status;
    String message = "";
    private static Listener mListener;
    ProgressDialog mProgressDialog;
    private JSONObject jsonLeadObj, jsonLeadObj1;
    JSONArray jsonArray, jsonarray;
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    List<AdvertisementDAO> itemsPendingRemoval = new ArrayList<>();

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<AdvertisementDAO, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    private Runnable runnable;
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";

    // create constructor to innitilize context and data sent from MainActivity
    public ActiveAdvertisementListAdpter(Context context, List<AdvertisementDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        res = context.getResources();
        loadLanguage(context);
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_active_advertisement_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);
        // EVENT_DATE_TIME = current.getE_date() + " " + current.getE_time();
        myHolder.linear_layout_2.setTag(position);

        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date event_date = null;
        try {
            event_date = dateFormat.parse(current.getE_date() + " " + current.getE_time());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Date finalEvent_date = event_date;
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);


                    Date current_date = new Date();
                    if (!current_date.after(finalEvent_date)) {
                        long diff = finalEvent_date.getTime() - current_date.getTime();
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //
                        myHolder.tv_days.setText(String.format("%02d", Days));
                        myHolder.tv_days.setTag(position);
                        myHolder.tv_hour.setText(String.format("%02d", Hours));
                        myHolder.tv_hour.setTag(position);
                        myHolder.tv_minute.setText(String.format("%02d", Minutes));
                        myHolder.tv_minute.setTag(position);
                        myHolder.tv_second.setText(String.format("%02d", Seconds));
                        myHolder.tv_second.setTag(position);


                        myHolder.timing.setText(String.format("%02d", Days) + " " + String.format("%02d", Hours) + " " + String.format("%02d", Minutes) + " " + String.format("%02d", Seconds));
                        myHolder.timing.setTag(position);
                    } else {

                        handler.removeCallbacks(runnable);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 100);
        myHolder.title.setText(current.getTitle());
        myHolder.title.setTag(position);
        if (current.getLikecnt().equals("1")) {
            myHolder.like.setVisibility(View.VISIBLE);
            myHolder.like.setText(res.getString(R.string.jlk) + " " + current.getLikecnt());
            myHolder.like.setTag(position);
        } else if (!current.getLikecnt().equals("1") && !current.getLikecnt().equals("0")) {
            myHolder.like.setVisibility(View.VISIBLE);
            myHolder.like.setText(res.getString(R.string.jlks) + " " + current.getLikecnt());
            myHolder.like.setTag(position);
        } else if (current.getLikecnt().equals("0")) {
            myHolder.like.setVisibility(View.GONE);
        }

        if (current.getDislikecnt().equals("1")) {
            myHolder.dislike.setVisibility(View.VISIBLE);
            myHolder.dislike.setText(res.getString(R.string.jdks) + " " + current.getDislikecnt());
            myHolder.dislike.setTag(position);
        } else if (!current.getDislikecnt().equals("1") && !current.getDislikecnt().equals("0")) {
            myHolder.dislike.setVisibility(View.VISIBLE);
            myHolder.dislike.setText(res.getString(R.string.jdlks) + " " + current.getDislikecnt());
            myHolder.dislike.setTag(position);
        } else if (current.getDislikecnt().equals("0")) {
            myHolder.dislike.setVisibility(View.GONE);
        }


        myHolder.description.setTag(position);
        myHolder.limitation.setTag(position);
        myHolder.totalpaid.setText(res.getString(R.string.jpdm) + preferences.getString("currency_sign", "") + current.getPaid_amount());
        myHolder.totalpaid.setTag(position);

        if (!current.getDescription().equals("")) {
            myHolder.description.setVisibility(View.VISIBLE);
            myHolder.description.setText(current.getDescription());
        } else {
            myHolder.description.setVisibility(View.GONE);
        }
        if (!current.getDescribe_limitations().equals("")) {
            myHolder.limitation.setVisibility(View.VISIBLE);
            myHolder.limitation.setText(res.getString(R.string.xlimi) + current.getDescribe_limitations());
        } else {
            myHolder.limitation.setVisibility(View.GONE);
        }
        myHolder.totalViews.setText(current.getTotal_views() + " " + res.getString(R.string.jview) + current.getActive_user_count() + " " + res.getString(R.string.jnbs));
        myHolder.totalViews.setTag(position);

        myHolder.totalRedeemed.setText(current.getTotal_redeemed() + " " + res.getString(R.string.jreem));
        myHolder.totalRedeemed.setTag(position);
        myHolder.barCodeCat.setText(res.getString(R.string.jbcd) + current.getRq_code());
        myHolder.barCodeCat.setTag(position);
        if (preferences.getString("ulang", "").equals("en")) {
            myHolder.mainCat.setText(res.getString(R.string.jadcat) + current.getBusiness_main_category());
            myHolder.mainCat.setTag(position);

            myHolder.subCat.setText(res.getString(R.string.jadscat) + current.getBusiness_subcategory());
            myHolder.subCat.setTag(position);
        } else if (preferences.getString("ulang", "").equals("hi")) {
            myHolder.mainCat.setText(res.getString(R.string.jadcat) + current.getBusiness_main_category_hindi());
            myHolder.mainCat.setTag(position);

            myHolder.subCat.setText(res.getString(R.string.jadscat) + current.getBusiness_subcategory_hindi());
            myHolder.subCat.setTag(position);
        }
        myHolder.txtValidity.setText(parseTime(current.getS_time(), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", current.getS_date()) + " to " + parseTime(current.getE_time(), "HH:mm", "hh:mm aa") + " on " + formateDateFromstring("yyyy-MM-dd", "dd-MMM-yyyy", current.getE_date()));
        myHolder.txtValidity.setTag(position);


        myHolder.callingButton.setTag(position);
        myHolder.messageButton.setTag(position);
        myHolder.numbers.setTag(position);
        myHolder.whatsappeButton.setTag(position);
        myHolder.numbers.setText(current.getNumbers());
        myHolder.numbers.setTag(position);
        myHolder.DeactivateAd.setTag(position);
        myHolder.uploadImg.setTag(position);

        // ImageLoader imageLoader = new ImageLoader(context);
        //imageLoader.DisplayImage(current.getOriginal_image_path(), myHolder.imageView);
        // Picasso.with(context).load(current.getOriginal_image_path()).noPlaceholder().into((ImageView) myHolder.imageView);
        if (current.getOriginal_image_path().equals("null")) {
            myHolder.imageView.setVisibility(View.GONE);
            myHolder.uploadImg.setVisibility(View.VISIBLE);
        } else {
            try {
                myHolder.uploadImg.setVisibility(View.GONE);
                myHolder.imageView.setVisibility(View.VISIBLE);

                Picasso.get().load(current.getOriginal_image_path()).into(myHolder.imageView);
            } catch (Exception e) {

            }
        }
        myHolder.imageView.setTag(position);
        myHolder.shareButton.setTag(position);

        myHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);

                    if (AppStatus.getInstance(context).isOnline()) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        Uri bmpUri = getLocalBitmapUri(myHolder.imageView);
                        if (bmpUri != null) {
                            // Construct a ShareIntent with link to image
                            Intent shareIntent = new Intent();
                            shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/apps/internaltest/4699689855537704233" + " \n" + current.getTitle());
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "Special promotion on AlertMeU (http://www.alertmeu.com), a platform to find what your are interested in. Download app at (https://play.google.com/store/apps/details?id=in.alertmeu).\n\n" + current.getTitle() + " \n" + current.getDescription() + " \n" + myHolder.txtValidity.getText().toString());

                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/*");
                            // shareIntent.setType("text/plain");
                            context.startActivity(Intent.createChooser(shareIntent, "Share with"));

                        } else {
                            // ...sharing failed, handle error
                        }
                    } else {
                        Toast.makeText(context, Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                    }

            }
        });
        myHolder.DeactivateAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(res.getString(R.string.jreemgh))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.jyes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                baid = current.getId();
                                new removeActiveAds().execute();

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
                alert.setTitle(res.getString(R.string.jrrty));
                alert.show();
            }
        });
        myHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                Intent intent = new Intent(context, FullScreenViewActivity.class);
                intent.putExtra("path", current.getOriginal_image_path());
                context.startActivity(intent);
            }
        });

        myHolder.uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                prefEditor.putString("adid", current.getId());
                prefEditor.putString("adqrcode", current.getRq_code());
                prefEditor.commit();
                UploadImageView uploadImageView = new UploadImageView();
                uploadImageView.show(((FragmentActivity) context).getSupportFragmentManager(), "uploadImageView");

            }
        });
    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        current = data.get(position);
        if (!itemsPendingRemoval.contains(current)) {
            itemsPendingRemoval.add(current);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {

                    remove(data.indexOf(current));

                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(current, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        current = data.get(position);

        user_id = current.getBusiness_user_id();
        ID = position;
        // Toast.makeText(context, "Remove id" + id, Toast.LENGTH_LONG).show();

        if (itemsPendingRemoval.contains(current)) {
            itemsPendingRemoval.remove(current);
        }
        if (data.contains(current)) {
            data.remove(position);
            notifyItemRemoved(position);
        }
        // new deleteSale().execute();
    }

    public boolean isPendingRemoval(int position) {
        current = data.get(position);
        return itemsPendingRemoval.contains(current);
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView title, description, numbers, timing, totalViews, totalRedeemed, mainCat, subCat, barCodeCat, limitation, totalpaid;
        ImageView callingButton, messageButton, whatsappeButton, imageView, shareButton;
        CountDownTimer timer;
        private LinearLayout linear_layout_2;
        private TextView tv_days, tv_hour, tv_minute, tv_second, txtValidity, like, dislike;
        Button DeactivateAd, uploadImg;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            timing = (TextView) itemView.findViewById(R.id.timing);
            totalViews = (TextView) itemView.findViewById(R.id.totalViews);
            totalRedeemed = (TextView) itemView.findViewById(R.id.totalRedeemed);
            callingButton = (ImageView) itemView.findViewById(R.id.callingButton);
            messageButton = (ImageView) itemView.findViewById(R.id.messageButton);
            numbers = (TextView) itemView.findViewById(R.id.numbers);
            limitation = (TextView) itemView.findViewById(R.id.limitation);
            barCodeCat = (TextView) itemView.findViewById(R.id.barCodeCat);
            totalpaid = (TextView) itemView.findViewById(R.id.totalpaid);
            whatsappeButton = (ImageView) itemView.findViewById(R.id.whatsappeButton);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            shareButton = (ImageView) itemView.findViewById(R.id.shareButton);
            linear_layout_2 = itemView.findViewById(R.id.linear_layout_2);
            tv_days = itemView.findViewById(R.id.tv_days);
            tv_hour = itemView.findViewById(R.id.tv_hour);
            tv_minute = itemView.findViewById(R.id.tv_minute);
            tv_second = itemView.findViewById(R.id.tv_second);
            txtValidity = itemView.findViewById(R.id.txtValidity);
            mainCat = itemView.findViewById(R.id.mainCat);
            subCat = itemView.findViewById(R.id.subCat);
            DeactivateAd = itemView.findViewById(R.id.DeactivateAd);
            uploadImg = itemView.findViewById(R.id.uploadImg);
            like = (TextView) itemView.findViewById(R.id.like);
            dislike = (TextView) itemView.findViewById(R.id.dislike);
        }

    }


    public static void bindListener(Listener listener) {
        mListener = listener;
    }

    // method to access in activity after updating selection
    public List<AdvertisementDAO> getSservicelist() {
        return data;
    }

    public String getContactDetails(String phoneNumber1) {
        String searchNumber = phoneNumber1;
        String phoneNumber = "", emailAddress = "", name = "";
        StringBuffer sb = new StringBuffer();
        // Cursor c =  getContentResolver().query(contactData, null, null, null, null);
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(searchNumber));
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        if (c.moveToFirst()) {


            name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1"))
                hasPhone = "true";
            else
                hasPhone = "false";

            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }

            // Find Email Addresses
            Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
            while (emails.moveToNext()) {
                emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
            emails.close();


            sb.append("\nUser Name:--- " + name + " \nCall Type:--- "
                    + " \nMobile Number:--- " + phoneNumber
                    + " \nEmail Id:--- " + emailAddress);
            sb.append("\n----------------------------------");


// add elements to al, including duplicates


            Log.d("curs", name + " num" + phoneNumber + " " + "mail" + emailAddress);
        }
        c.close();
        return name;
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
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

    private class removeActiveAds extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage("Deleting...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("id", baid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            deActiveAdsResponse = serviceAccess.SendHttpPost(Config.URL_DEACTIVATEADBYID, jsonLeadObj);
            Log.i("resp", deActiveAdsResponse);


            if (deActiveAdsResponse.compareTo("") != 0) {
                if (isJSONValid(deActiveAdsResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(deActiveAdsResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        jsonArray = new JSONArray(deActiveAdsResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Close the progressdialog
            mProgressDialog.dismiss();
            if (status) {
                //  removeAt(ID);
                Toast.makeText(context, res.getString(R.string.jadsdec), Toast.LENGTH_LONG).show();
                mListener.messageReceived(message);

            } else {
                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();


            }

        }
    }

    //
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

    private String getLangCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "");
        return langCode;
    }

    private void loadLanguage(Context context) {
        Locale locale = new Locale(getLangCode(context));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }




}
