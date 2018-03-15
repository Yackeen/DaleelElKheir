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
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.allcases.model.CaseModel;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.UN_FOLLOW_ORGANIZATION;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class FollowedCasesAdapter extends RecyclerView.Adapter<FollowedCasesAdapter.ViewHolder> {

    private static final String TAG = "FollowedCasesAdapter";
    private Context context;
    private List<CaseModel> list = new ArrayList<>();
    private PrefManager manager;
    private User user;

    public FollowedCasesAdapter(Context context, List<CaseModel> list) {
        this.context = context;
        this.list = list;
        manager = new PrefManager(context);
        user = manager.getUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.followed_cases_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CaseModel model = list.get(position);
        holder.title.setText(model.getName());
        holder.location.setText(model.getGovernorate());
        holder.department.setText(model.getCategory());
        holder.organization.setText(model.getOrganization());

//        byte[] decodedImg = Base64.decode(model.getImage(), Base64.DEFAULT);
        Glide.with(context).load(model.getImage()).into(holder.thumbnail);


        holder.unFollow.setVisibility(View.GONE);
        holder.unFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> headers = new HashMap<>();
                params.put("UserID", user.getId());
                params.put("OrganizationID", model.getId());
                headers.put("SecurityToken", user.getToken());

                FetchData fetchData = new FetchData(context, TAG, null, UN_FOLLOW_ORGANIZATION,
                        Request.Method.POST, params, headers);
                fetchData.getData(new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
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

        private TextView title, location, department, organization;
        private ImageView thumbnail, unFollow;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.caseName);
            location = itemView.findViewById(R.id.region);
            department = itemView.findViewById(R.id.department);
            organization = itemView.findViewById(R.id.organization);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            unFollow = itemView.findViewById(R.id.unFollowCase);
        }
    }
}
