package in.alertmeu.a4b.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.AdsRePostPreviewActivity;
import in.alertmeu.a4b.activity.FullScreenViewActivity;

import in.alertmeu.a4b.models.AdvertisementDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.Listener;


public class ActiveAdvertisementListAdpter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<AdvertisementDAO> data;
    AdvertisementDAO current;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String user_id = "";
    private static Listener mListener;
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    List<AdvertisementDAO> itemsPendingRemoval = new ArrayList<>();

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<AdvertisementDAO, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    // create constructor to innitilize context and data sent from MainActivity
    public ActiveAdvertisementListAdpter1(Context context, List<AdvertisementDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_active_advertisement_details, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);
        if (myHolder.timer != null) {
            myHolder.timer.cancel();
        }
        long timer = (Long.parseLong(current.getTotal_time())*1000);
        myHolder.timer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                String time = days+" "+"days" +" :" +hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
               // String hms = String.format("%02d:%02d",  TimeUnit.MILLISECONDS.toMinutes(timer) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timer)), TimeUnit.MILLISECONDS.toSeconds(timer) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timer)));
                //myHolder.timing.setText("" + millisUntilFinished / 1000 + " Sec");

                myHolder.timing.setText(time);
                myHolder.timing.setTag(position);
            }

            public void onFinish() {
                myHolder.timing.setText("00:00:00");
                myHolder.timing.setTag(position);
            }
        }.start();

        myHolder.title.setText(current.getTitle());
        myHolder.title.setTag(position);

        myHolder.description.setText(current.getDescription());
        myHolder.description.setTag(position);

        myHolder.totalViews.setText(current.getTotal_views()+" Views");
        myHolder.totalViews.setTag(position);

        myHolder.totalRedeemed.setText(current.getTotal_redeemed()+" Redeemed");
        myHolder.totalRedeemed.setTag(position);

        myHolder.callingButton.setTag(position);
        myHolder.messageButton.setTag(position);
        myHolder.numbers.setTag(position);
        myHolder.whatsappeButton.setTag(position);
        myHolder.numbers.setText(current.getNumbers());
        myHolder.numbers.setTag(position);

       // Picasso.with(context).load(current.getOriginal_image_path()).noPlaceholder().into((ImageView) myHolder.imageView);
        Picasso.get().load(current.getOriginal_image_path()).into(myHolder.imageView);

        myHolder.imageView.setTag(position);
        myHolder.shareButton.setTag(position);

        myHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                if (AppStatus.getInstance(context).isOnline()) {
                    Uri bmpUri = getLocalBitmapUri(myHolder.imageView);
                    if (bmpUri != null) {
                        // Construct a ShareIntent with link to image
                        Intent shareIntent = new Intent();
                        shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, current.getTitle());
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/jpg");
                        context.startActivity(Intent.createChooser(shareIntent, "Share with"));

                    } else {
                        // ...sharing failed, handle error
                    }
                } else {
                    Toast.makeText(context, Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                }
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

        TextView title, description, numbers, timing,totalViews,totalRedeemed;
        ImageView callingButton, messageButton, whatsappeButton, imageView, shareButton;
        CountDownTimer timer;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            timing = (TextView) itemView.findViewById(R.id.timing);
            totalViews= (TextView) itemView.findViewById(R.id.totalViews);
            totalRedeemed= (TextView) itemView.findViewById(R.id.totalRedeemed);
            callingButton = (ImageView) itemView.findViewById(R.id.callingButton);
            messageButton = (ImageView) itemView.findViewById(R.id.messageButton);
            numbers = (TextView) itemView.findViewById(R.id.numbers);
            whatsappeButton = (ImageView) itemView.findViewById(R.id.whatsappeButton);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            shareButton = (ImageView) itemView.findViewById(R.id.shareButton);

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

}
