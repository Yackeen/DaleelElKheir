package yackeen.com.daleel.about;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;


/**
 * Created by Ibrahim on 07/02/2018.
 */

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {


    private static final String TAG = "AboutAdapter";
    private Context context;
    private List<AboutModel> list = new ArrayList<>();

    public AboutAdapter(Context context, List<AboutModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_about, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);

        AboutModel model = list.get(position);
//        byte[] decodedImage = Base64.decode(model.getImage(), Base64.DEFAULT);
        Glide.with(context).load(model.getImage()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
        }

        void bind(int position) {

        }

    }
}
