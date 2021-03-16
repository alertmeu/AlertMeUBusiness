package in.alertmeu.a4b.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.models.ShowAllReviewsDAO;
import in.alertmeu.a4b.utils.AppStatus;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Constant;
import in.alertmeu.a4b.utils.WebClient;


public class BusinessReviewListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private Context context;
    private LayoutInflater inflater;
    List<ShowAllReviewsDAO> data;
    ShowAllReviewsDAO current;
    int number = 1, clickflag = 1;
    Resources res;
    private static final String FILE_NAME = "file_lang";
    private static final String KEY_LANG = "key_lang";
    JSONObject jsonLeadObj;
    String updateStatusResponse = "", msg = "", user_id = "", id = "", inappropriate = "";
    boolean status;
    String localTime;
    AlertDialog dialog = null;
    // create constructor to innitilize context and data sent from MainActivity
    public BusinessReviewListAdpter(Context context, List<ShowAllReviewsDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        res = context.getResources();
        loadLanguage(context);
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();


    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_allreviews_details, parent, false);
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
        myHolder.textViewName.setText(current.getFirst_name());
        myHolder.textViewName.setTag(position);

        myHolder.avgratingStar.setRating(Float.parseFloat(current.getRating_star()));
        myHolder.avgratingStar.setTag(position);

        myHolder.textViewDate.setText(formateDateFromstring("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm:ss", current.getTime_stamp()));
        myHolder.textViewDate.setTag(position);


        if (!current.getUser_review().equals("")) {
            myHolder.textComment.setVisibility(View.VISIBLE);
            myHolder.textComment.setText(current.getUser_review());
            myHolder.textComment.setTag(position);
        } else {
            myHolder.textComment.setVisibility(View.GONE);
            myHolder.textComment.setTag(position);
        }
        if (current.getProfilePath().contains("http")) {
            try {
                Picasso.get().load(current.getProfilePath()).into(myHolder.imageViewIcon);
                myHolder.imageViewIcon.setTag(position);
            } catch (Exception e) {

            }
        }else
        {
            myHolder.imageViewIcon.setImageResource(R.drawable.defuserpro);

        }
        myHolder.flag.setTag(position);
        myHolder.flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                user_id = current.getUser_id();
                id = current.getBusiness_id();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                        Locale.getDefault());
                Date currentLocalTime = calendar.getTime();

                DateFormat date = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
                localTime = date.format(currentLocalTime);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // set the custom layout
                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customLayout = li.inflate(R.layout.write_inappropriatecustom_layout, null);
                builder.setView(customLayout);
                Button btnSubmit = (Button) customLayout.findViewById(R.id.btnSubmit);
                Button btnCancel = (Button) customLayout.findViewById(R.id.btnCancel);
                EditText descEdtTxt = (EditText) customLayout.findViewById(R.id.descEdtTxt);
                ImageView back_arrow1 = customLayout.findViewById(R.id.back_arrow1);
                back_arrow1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }

                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        inappropriate = descEdtTxt.getText().toString();
                        if (AppStatus.getInstance(context).isOnline()) {
                            new initInappropriateUpdate().execute();
                        } else {
                            Toast.makeText(context, Constant.INTERNET_MSG, Toast.LENGTH_SHORT).show();
                        }
                        //  Toast.makeText(getApplicationContext(), descEdtTxt.getText().toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                dialog = builder.create();
                dialog.show();

            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewDate, textComment;
        RatingBar avgratingStar;
        ImageView imageViewIcon, flag;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textComment = (TextView) itemView.findViewById(R.id.textComment);
            avgratingStar = (RatingBar) itemView.findViewById(R.id.avgratingStar);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            flag = (ImageView) itemView.findViewById(R.id.flag);
        }

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

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            //LOGE(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    private class initInappropriateUpdate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(BusinessProfileActivity.this);
            // Set progressdialog title
            //  mProgressDialog.setTitle(res.getString(R.string.jpw));
            // Set progressdialog message
            //  mProgressDialog.setMessage(res.getString(R.string.juds));
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("user_id", user_id);
                        put("business_id", id);
                        put("business_inappropriate", inappropriate);
                        put("t_zone", localTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            updateStatusResponse = serviceAccess.SendHttpPost(Config.URL_ADDUSERBUSTOUSERINAPPROPRIATE, jsonLeadObj);
            Log.i("resp", "updateStatusResponse" + updateStatusResponse);

            if (updateStatusResponse.compareTo("") != 0) {
                if (isJSONValid(updateStatusResponse)) {

                    try {
                        JSONObject jsonObject = new JSONObject(updateStatusResponse);
                        msg = jsonObject.getString("message");
                        status = jsonObject.getBoolean("status");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();
                    return null;
                }
            } else {
                Toast.makeText(context, res.getString(R.string.jpcnc), Toast.LENGTH_LONG).show();

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // mProgressDialog.dismiss();
            if (status) {
                Toast.makeText(context, res.getString(R.string.jdus), Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(BusinessProfileActivity.this, msg, Toast.LENGTH_LONG).show();
            }


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
}
