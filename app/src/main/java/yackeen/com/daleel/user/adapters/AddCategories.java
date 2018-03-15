package yackeen.com.daleel.user.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.user.models.FavouriteCategoriesModel;

/**
 * Created by Ibrahim on 10/02/2018.
 */

public class AddCategories extends RecyclerView.Adapter<AddCategories.ViewHolder> {


    private Context context;
    private List<FavouriteCategoriesModel> list = new ArrayList<>();

    public AddCategories(Context context, List<FavouriteCategoriesModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_cat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FavouriteCategoriesModel model = list.get(position);

        holder.name.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView changeCategory;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.categoryName);
            changeCategory = itemView.findViewById(R.id.unFollowCat);
        }
    }
}
