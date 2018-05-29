package yackeen.com.daleel.volunteer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.volunteer.Models.CategoryModel;

/**
 * Created by Abdelrhman Walid on 4/20/2017.
 */

class VolunteerCategAdapter extends RecyclerView.Adapter<VolunteerCategAdapter.ViewHolder> {


    private static final String TAG = "AllCasesAdapter";
    private Context context;
    private List<CategoryModel> list = new ArrayList<>();

    public VolunteerCategAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    public List<CategoryModel> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_volunt_categ, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
        final CategoryModel model = list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private CheckBox check;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text);
            check = view.findViewById(R.id.check);
        }

        void bind(final int position) {
            name.setText(list.get(position).getName());
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    list.get(position).setChecked(isChecked);
                }
            });
        }

    }
}
