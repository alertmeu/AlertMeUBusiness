package in.alertmeu.a4b.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.models.AdsImagesModeDAO;
import in.alertmeu.a4b.utils.Listener;


public class AdsImagesListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<AdsImagesModeDAO> data;
    AdsImagesModeDAO current;
    ;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    private static Listener mListener;

    // create constructor to innitilize context and data sent from MainActivity
    public AdsImagesListAdpter(Context context, List<AdsImagesModeDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_ads_img_details, parent, false);
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
        if (!current.getImage_path().equals("")) {
            try {
                Picasso.get().load(current.getImage_path()).into(myHolder.subimage);
            } catch (Exception e) {

            }

        } else {
            //  viewHolderChild.subimage.setImageResource(R.drawable.default_sub_category);
            myHolder.subimage.setImageDrawable(context.getResources().getDrawable(R.drawable.default_sub_category));

        }
        myHolder.subimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                mListener.messageReceived(current.getImage_path());

            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {


        ImageView subimage;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            subimage = (ImageView) itemView.findViewById(R.id.subimage);
        }

    }


    public static void bindListener(Listener listener) {
        mListener = listener;
    }

    // method to access in activity after updating selection
    public List<AdsImagesModeDAO> getSservicelist() {
        return data;
    }

}
