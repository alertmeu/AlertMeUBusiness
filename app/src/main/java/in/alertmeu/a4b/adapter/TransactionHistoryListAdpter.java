package in.alertmeu.a4b.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.alertmeu.a4b.R;

import in.alertmeu.a4b.models.AdvertisementDAO;
import in.alertmeu.a4b.models.TransactionHistoryDAO;

import in.alertmeu.a4b.utils.Listener;


public class TransactionHistoryListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<TransactionHistoryDAO> data;
    TransactionHistoryDAO current;
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
    private Runnable runnable;
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";

    // create constructor to innitilize context and data sent from MainActivity
    public TransactionHistoryListAdpter(Context context, List<TransactionHistoryDAO> data) {
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
        View view = inflater.inflate(R.layout.layout_transaction_history_details, parent, false);
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
        myHolder.datetime.setText(formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy hh:mm:ss aa", current.getDate_time()));
        myHolder.datetime.setTag(position);
        if (current.getDescription().contains("Ad #")) {
            myHolder.desc.setText(current.getDescription());
            myHolder.desc.setTag(position);
            myHolder.amount.setText("-" + preferences.getString("currency_sign","")+current.getAmount());
            myHolder.amount.setTag(position);
        }
        else {
            myHolder.desc.setText(current.getDescription());
            myHolder.desc.setTag(position);
            myHolder.amount.setText("+" +preferences.getString("currency_sign","")+ current.getAmount());
            myHolder.amount.setTag(position);
        }
        myHolder.PreviousAmt.setText(current.getPrevious_balance());
        myHolder.PreviousAmt.setTag(position);
        myHolder.CurrentAmt.setText(current.getCurrent_balance());
        myHolder.CurrentAmt.setTag(position);

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView desc, datetime, amount, PreviousAmt, CurrentAmt;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            desc = (TextView) itemView.findViewById(R.id.desc);
            datetime = (TextView) itemView.findViewById(R.id.datetime);
            amount = (TextView) itemView.findViewById(R.id.amount);
            PreviousAmt = (TextView) itemView.findViewById(R.id.PreviousAmt);
            CurrentAmt = (TextView) itemView.findViewById(R.id.CurrentAmt);


        }

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
