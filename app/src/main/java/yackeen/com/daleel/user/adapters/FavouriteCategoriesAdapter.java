package yackeen.com.daleel.user.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;
import yackeen.com.daleel.user.models.FavouriteCategoriesModel;

import static yackeen.com.daleel.constants.Constants.UN_FOLLOW_CATEGORY;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class FavouriteCategoriesAdapter extends RecyclerView.Adapter<FavouriteCategoriesAdapter.ViewHolder> {

    private static final String TAG = "FavouriteCategoriesAdapter";
    private Context context;
    private List<FavouriteCategoriesModel> list = new ArrayList<>();
    private PrefManager manager;
    private User user;

    public FavouriteCategoriesAdapter(Context context, List<FavouriteCategoriesModel> list) {
        this.context = context;
        this.list = list;
        manager = new PrefManager(context);
        user = manager.getUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_cat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final FavouriteCategoriesModel model = list.get(position);
        holder.name.setText(model.getName());
        holder.unFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> headers = new HashMap<>();
                params.put("UserID", user.getId());
                params.put("CategoryID", model.getId());
                headers.put("SecurityToken", user.getToken());
                FetchData fetchData = new FetchData(context, TAG, null, UN_FOLLOW_CATEGORY,
                        Request.Method.POST, params, headers);
                fetchData.getData(new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Toast.makeText(context, context.getResources().getString(R.string.category_removed), Toast.LENGTH_SHORT).show();
                        list.remove(list.get(position));
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView unFollow;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.categoryName);
            unFollow = itemView.findViewById(R.id.unFollowCat);
        }
    }
}

