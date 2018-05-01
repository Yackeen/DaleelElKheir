package yackeen.com.daleel.allcases;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.allcases.model.CaseModel;

import static yackeen.com.daleel.constants.Constants.URGENT_CASE;

/**
 * Created by Abdelrhman Walid on 4/20/2017.
 */

class AllCasesAdapter extends RecyclerView.Adapter<AllCasesAdapter.ViewHolder> {


    private static final String TAG = "AllCasesAdapter";
    private Context context;
    private List<CaseModel> list = new ArrayList<>();

    public AllCasesAdapter(Context context, List<CaseModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_case, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
        final CaseModel model = list.get(position);

        holder.caseName.setText(model.getName());
        holder.date.setText(model.getDueDate());
        holder.department.setText(model.getCategory());
        holder.organization.setText(model.getOrganization());
        holder.region.setText(model.getCity());

        if (model.getCaseType().equals(URGENT_CASE)) {
            holder.urgentCaseLayout.setVisibility(View.VISIBLE);
            model.setType(context.getResources().getString(R.string.urgent));
        } else {
            holder.urgentCaseLayout.setVisibility(View.INVISIBLE);
            model.setType(context.getResources().getString(R.string.recent));
        }

        holder.detail.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("CaseCode", model.getCaseCode());
                intent.putExtra("Joined", model.isJoined());
                intent.putExtra("type", model.getType());
                context.startActivity(intent);
            }
        });

//        final byte[] decodedImg = Base64.decode(model.getImage(), Base64.DEFAULT);
        Glide.with(context).load(model.getImage()).into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView caseName, date, region, department, organization, detail, percentage;
        private ProgressBar progressBar;
        private LinearLayout urgentCaseLayout;

        ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            caseName = view.findViewById(R.id.caseName);
            date = view.findViewById(R.id.date);
            region = view.findViewById(R.id.region);
            department = view.findViewById(R.id.department);
            organization = view.findViewById(R.id.organization);
            detail = view.findViewById(R.id.viewCaseDetail);
            urgentCaseLayout = view.findViewById(R.id.urgentCaseLayout);
        }

        void bind(int position) {

        }

    }
}
