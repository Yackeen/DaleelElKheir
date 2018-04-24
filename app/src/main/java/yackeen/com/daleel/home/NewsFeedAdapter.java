package yackeen.com.daleel.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.allcases.DetailActivity;
import yackeen.com.daleel.allcases.model.CaseModel;

/**
 * Created by Abdelrhman Walid on 4/20/2017.
 */

class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {


    private Context context;
    private List<CaseModel> list = new ArrayList<>();

    public NewsFeedAdapter(Context context, List<CaseModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_newsfeed, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
        final CaseModel model = list.get(position);

        holder.organizationName.setText(model.getOrganization());
        holder.eventName.setText(model.getName());
        holder.category.setText(model.getCategory());
        holder.currentAmount.setText(model.getCurrentAmount());
        holder.requiredAmount.setText(model.getRequiredAmount());

        double val = Double.valueOf(model.getCurrentAmount()) / Double.valueOf(model.getRequiredAmount()) * 100.00;
        holder.percentage.setText(String.valueOf((int) val) + "%");
        holder.progressBar.setProgress((int) val);
//        byte[] decodedString = Base64.decode(model.getImage(), Base64.DEFAULT);

        Glide.with(context).load(model.getImage()).into(holder.thumbnail);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);

                intent.putExtra("caseId", model.getId());
                intent.putExtra("caseName", model.getName());
                intent.putExtra("date", model.getDueDate());
                intent.putExtra("department", model.getCategory());
                intent.putExtra("organization", model.getOrganization());
                intent.putExtra("place", model.getCity());
                intent.putExtra("image", model.getImage());
                intent.putExtra("currentAmount", model.getCurrentAmount());
                intent.putExtra("requiredAmount", model.getRequiredAmount());
                intent.putExtra("description", model.getDescription());
                intent.putExtra("sharedLink", model.getSharedLink());
                intent.putExtra("CaseCode", model.getCaseCode());
                intent.putExtra("Joined", model.isJoined());
                intent.putExtra("type", model.getType());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        Button detail;
        private ImageView thumbnail;
        private TextView eventName, organizationName, category, requiredAmount, currentAmount, percentage;
        private LinearLayout parentLayout;
        private ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            eventName = view.findViewById(R.id.eventName);
            organizationName = view.findViewById(R.id.organizationName);
            parentLayout = view.findViewById(R.id.layout);
            category = view.findViewById(R.id.category);
            requiredAmount = view.findViewById(R.id.requiredAmount);
            currentAmount = view.findViewById(R.id.currentAmount);
            percentage = view.findViewById(R.id.casePercentage);
            progressBar = view.findViewById(R.id.progressBar);
            detail = view.findViewById(R.id.more);
        }

        void bind(int position) {

        }

    }
}
