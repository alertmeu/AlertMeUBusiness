package in.alertmeu.a4b.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.List;

import in.alertmeu.a4b.R;

import in.alertmeu.a4b.models.MainCatModeDAO;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.WebClient;
import in.alertmeu.a4b.view.SubCatDetailsView;


public class MainCatListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<MainCatModeDAO> data;
    MainCatModeDAO current;
    String id, id1;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    ProgressDialog mProgressDialog;
    private JSONObject jsonLeadObj;
    JSONArray jsonArray;
    boolean status;
    String message = "";
    String msg = "";
    String deleteResponse = "", businessCatResponse = "";


    // create constructor to innitilize context and data sent from MainActivity
    public MainCatListAdpter(Context context, List<MainCatModeDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_maincat_details, parent, false);
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

        myHolder.notes.setTag(position);
        if (preferences.getString("ulang", "").equals("en")) {
            if (current.getChecked_status().equals("0")) {
                myHolder.notes.setText(current.getCategory_name().trim());
                myHolder.notes.setTextColor(Color.parseColor("#000000"));

            } else {
                myHolder.notes.setText(current.getCategory_name().trim() + "(" + current.getChecked_status() + ")");
                myHolder.notes.setTextColor(Color.parseColor("#23A566"));
            }
        } else if (preferences.getString("ulang", "").equals("hi")) {
            if (current.getChecked_status().equals("0")) {
                myHolder.notes.setText(current.getCategory_name_hindi().trim());
                myHolder.notes.setTextColor(Color.parseColor("#000000"));

            } else {
                myHolder.notes.setText(current.getCategory_name_hindi().trim() + "(" + current.getChecked_status() + ")");
                myHolder.notes.setTextColor(Color.parseColor("#23A566"));
            }
        }
        myHolder.id.setText(current.getId());
        myHolder.id.setTag(position);
        myHolder.layout_quotation.setTag(position);
        if (!current.getImage_path().equals("")) {
            // ImageLoader imageLoader = new ImageLoader(context);
            //  imageLoader.DisplayImage(current.getImage_path(), myHolder.mimage);
            //  myHolder.mimage.setTag(position);
            //  Picasso.with(context).load(current.getImage_path()).noPlaceholder().into((ImageView) myHolder.mimage);
            try {
                Picasso.get().load(current.getImage_path()).into(myHolder.mimage);
            } catch (Exception e) {

            }
          /*  ImageloaderNew imageLoader = new ImageloaderNew(context);
            myHolder.mimage.setTag(current.getImage_path());
            imageLoader.DisplayImage(current.getImage_path(), context,myHolder.mimage);*/
        } else {
            myHolder.mimage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_category));
            myHolder.mimage.setTag(position);


        }
       /* myHolder.chkBox.setTag(position);
        myHolder.chkBox.setChecked(data.get(position).isSelected());
        myHolder.chkBox.setTag(data.get(position));
        if (current.getChecked_status().equals("1")) {
            myHolder.chkBox.setChecked(true);
        } else {
            myHolder.chkBox.setChecked(false);
        }
        myHolder.chkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                MainCatModeDAO contact = (MainCatModeDAO) cb.getTag();

                contact.setSelected(cb.isChecked());
                data.get(pos).setSelected(cb.isChecked());

                if (cb.isChecked()) {
                    //Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked() + data.get(pos).getId(), Toast.LENGTH_LONG).show();
                    Config.VALUE.add(data.get(pos).getId());
                    id = data.get(pos).getId();
                    myHolder.chkBox.setChecked(true);
                    new submitData().execute();
                } else if (!cb.isChecked()) {
                    // Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked() + data.get(pos).getId(), Toast.LENGTH_LONG).show();
                    id1 = data.get(pos).getId();
                    myHolder.chkBox.setChecked(false);
                    new deleteSale().execute();
                    Config.VALUE.remove(data.get(pos).getId());

                }


            }
        });*/
        myHolder.layout_quotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                if (preferences.getString("ulang", "").equals("en")) {
                    prefEditor.putString("m_name_cat", current.getCategory_name());
                } else if (preferences.getString("ulang", "").equals("hi")) {
                    prefEditor.putString("m_name_cat", current.getCategory_name_hindi());
                }
                prefEditor.putString("m_id_l", current.getId());
                prefEditor.commit();
                // Toast.makeText(context, "Clicked on Checkbox: " + current.getId(), Toast.LENGTH_LONG).show();
                SubCatDetailsView subCatDetailsView = new SubCatDetailsView();
                subCatDetailsView.show(((AppCompatActivity) context).getSupportFragmentManager(), "subCatDetailsView");

            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView txt_date, notes, id;
        CheckBox chkBox;
        ImageView mimage;
        LinearLayout layout_quotation;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            notes = (TextView) itemView.findViewById(R.id.comments);
            notes = (TextView) itemView.findViewById(R.id.comments);
            id = (TextView) itemView.findViewById(R.id.id);
            chkBox = (CheckBox) itemView.findViewById(R.id.chkBox);
            mimage = (ImageView) itemView.findViewById(R.id.mimage);
            layout_quotation = (LinearLayout) itemView.findViewById(R.id.layout_quotation);

        }

    }

    private class submitData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //  mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            //   mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //   mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //   mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("bc_id", id);
                        put("business_user_id", preferences.getString("business_user_id", ""));


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();
            Log.i("json", "json" + jsonLeadObj);
            businessCatResponse = serviceAccess.SendHttpPost(Config.URL_SAVEMAINCAT, jsonLeadObj);
            Log.i("resp", "businessCatResponse" + businessCatResponse);


            if (businessCatResponse.compareTo("") != 0) {
                if (isJSONValid(businessCatResponse)) {


                    try {

                        JSONObject jsonObject = new JSONObject(businessCatResponse);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {


                    Toast.makeText(context, "Please check your webservice", Toast.LENGTH_LONG).show();


                }
            } else {

                Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                // mProgressDialog.dismiss();
            } else {
                // Close the progressdialog
                //  mProgressDialog.dismiss();

            }
        }
    }

    //
    private class deleteSale extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            //  mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            //mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //  mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("bc_id", id1);
                        put("business_user_id", preferences.getString("business_user_id", ""));


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };

            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            deleteResponse = serviceAccess.SendHttpPost(Config.URL_DELETEMAINCATEGORY, jsonLeadObj);
            Log.i("resp", "leadListResponse" + deleteResponse);


            if (deleteResponse.compareTo("") != 0) {
                if (isJSONValid(deleteResponse)) {

                    try {

                        JSONObject jObject = new JSONObject(deleteResponse);
                        status = jObject.getBoolean("status");
                        message = jObject.getString("message");
                        jsonArray = new JSONArray(deleteResponse);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    Toast.makeText(context, "Please check your network connection", Toast.LENGTH_LONG).show();

                }
            } else {

                Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

            }
            // Close the progressdialog
            // mProgressDialog.dismiss();
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


}
