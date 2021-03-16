package in.alertmeu.a4b.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.AddModifyPriceListCategoryActivity;
import in.alertmeu.a4b.models.AllPriceListSubCatModeDAO;
import in.alertmeu.a4b.models.AllSubCatModeDAO;


public class AllSubPricListAdpter extends RecyclerView.Adapter<AllSubPricListAdpter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<AllPriceListSubCatModeDAO> data;
    AllPriceListSubCatModeDAO current;
    String id, id1;
    int ID;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private OnClickDetails monClickDetails;

    // create constructor to innitilize context and data sent from MainActivity
    public AllSubPricListAdpter(Context context, List<AllPriceListSubCatModeDAO> data, OnClickDetails onClickDetails) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.monClickDetails = onClickDetails;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

    }

    // Inflate the layout when viewholder created
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_allsubpriclist_details, parent, false);
        return new ViewHolder(view, monClickDetails);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        current = data.get(position);
        holder.subimage.setTag(position);

        holder.title.setText(current.getTitle());
        holder.title.setTag(position);
        holder.desc.setText(current.getDescription());
        holder.desc.setTag(position);
        if (preferences.getString("country_code", "").equals("+91")) {
            holder.rate.setText("Rs " + current.getRate());
            holder.rate.setTag(position);
        } else {
            holder.rate.setText("$" + current.getRate());
            holder.rate.setTag(position);
        }

        if (!current.getImage_path().equals("")) {
            try {
                Picasso.get().load(current.getImage_path()).into(holder.subimage);
            } catch (Exception e) {

            }

        } else {
            holder.subimage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_sub_category));
        }


    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, desc, rate;
        ImageView subimage;
        OnClickDetails monClickDetails;

        // create constructor to get widget reference
        public ViewHolder(View itemView, OnClickDetails onClickDetails) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            rate = (TextView) itemView.findViewById(R.id.rate);
            subimage = (ImageView) itemView.findViewById(R.id.subimage);
            monClickDetails = onClickDetails;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            monClickDetails.onDeetails(getAdapterPosition());
        }
    }
    public interface OnClickDetails {
        void onDeetails(int postion);
    }
}
