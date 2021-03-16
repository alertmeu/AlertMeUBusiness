package in.alertmeu.a4b.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.AddModifyPriceListCategoryActivity;
import in.alertmeu.a4b.models.AllSubCatModeDAO;
import in.alertmeu.a4b.models.SubCatModeDAO;
import in.alertmeu.a4b.utils.Config;
import in.alertmeu.a4b.utils.Listener;
import in.alertmeu.a4b.utils.WebClient;


public class AllSubCatListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<AllSubCatModeDAO> data;
    AllSubCatModeDAO current;
    String id, id1;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    // create constructor to innitilize context and data sent from MainActivity
    public AllSubCatListAdpter(Context context, List<AllSubCatModeDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_allsubcat_details, parent, false);
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
        myHolder.subimage.setTag(position);
        myHolder.priceListBySubCat.setTag(position);

        if (preferences.getString("ulang", "").equals("en")) {
            myHolder.notes.setText(current.getSubcategory_name());
            myHolder.notes.setTag(position);
        } else if (preferences.getString("ulang", "").equals("hi")) {
            myHolder.notes.setText(current.getSubcategory_name_hindi());
            myHolder.notes.setTag(position);
        }
        if (!current.getImage_path().equals("")) {
            try {
                Picasso.get().load(current.getImage_path()).into(myHolder.subimage);
            } catch (Exception e) {

            }

        } else {
            myHolder.subimage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_sub_category));

        }
        myHolder.priceListBySubCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                Intent intent = new Intent(context, AddModifyPriceListCategoryActivity.class);
                intent.putExtra("subbc_id", current.getSubbc_id());
                intent.putExtra("maincat_id", current.getMaincat_id());
                if (preferences.getString("ulang", "").equals("en")) {
                    intent.putExtra("subcategory_name", current.getSubcategory_name());
                } else if (preferences.getString("ulang", "").equals("hi")) {
                    intent.putExtra("subcategory_name", current.getSubcategory_name_hindi());
                }
                intent.putExtra("image_path", current.getImage_path());
                context.startActivity(intent);
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
        ImageView priceListBySubCat, subimage;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            notes = (TextView) itemView.findViewById(R.id.comments);
            notes = (TextView) itemView.findViewById(R.id.comments);
            id = (TextView) itemView.findViewById(R.id.id);
            subimage = (ImageView) itemView.findViewById(R.id.subimage);
            priceListBySubCat = (ImageView) itemView.findViewById(R.id.priceListBySubCat);

        }

    }
}
