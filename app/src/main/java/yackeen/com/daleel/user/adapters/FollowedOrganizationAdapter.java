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
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.organization.model.OrganizationModel;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.UN_FOLLOW_ORGANIZATION;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class FollowedOrganizationAdapter extends RecyclerView.Adapter<FollowedOrganizationAdapter.ViewHolder> {

    private static final String TAG = "FollowedOrganizationAda";
    private Context context;
    private List<OrganizationModel> list = new ArrayList<>();
    private PrefManager manager;
    private User user;

    public FollowedOrganizationAdapter(Context context, List<OrganizationModel> list) {
        this.context = context;
        this.list = list;
        manager = new PrefManager(context);
        user = manager.getUser();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_orgs_pager, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final OrganizationModel model = list.get(position);

        holder.orgName.setText(model.getName());
        holder.orgCategory.setText(model.getCategory());
        holder.orgLocation.setText(model.getLocation());
        holder.orgRegion.setText(model.getRegion());
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

//        byte[] decodedImg = Base64.decode(model.getLogo(), Base64.DEFAULT);
        Glide.with(context).load(model.getLogo()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView orgName, orgCategory, orgRegion, orgLocation;
        ImageView thumbnail, unFollow;

        public ViewHolder(View view) {
            super(view);
            orgName = view.findViewById(R.id.orgName);
            orgCategory = view.findViewById(R.id.orgCategory);
            orgRegion = view.findViewById(R.id.orgRegion);
            orgLocation = view.findViewById(R.id.orgAddress);
            thumbnail = view.findViewById(R.id.thumbnail);
            unFollow = view.findViewById(R.id.unFollowOrg);
        }
    }
}
