package yackeen.com.daleel.organization;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static yackeen.com.daleel.constants.Constants.ADD_ORGANIZATION;
import static yackeen.com.daleel.constants.Constants.ORGANIZATION_FRAGMENT;
import static yackeen.com.daleel.home.BottomNavListener.showFragment;

/**
 * Created by Ibrahim on 14/02/2018.
 */

public class AllOrganizationAdapter extends RecyclerView.Adapter<AllOrganizationAdapter.ViewHolder> {


    private static final String TAG = "OrganizationsAdapter";
    private Context context;
    private List<OrganizationModel> list = new ArrayList<>();
    private PrefManager manager;
    private User user;

    public AllOrganizationAdapter(Context context, List<OrganizationModel> list) {
        this.context = context;
        this.list = list;
        manager = new PrefManager(context);
        user = manager.getUser();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organization, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);

        final OrganizationModel model = list.get(position);

        holder.name.setText(model.getName());
        holder.location.setText(model.getLocation());
        holder.region.setText(model.getRegion());
        holder.category.setText(model.getCategory());

//        byte[] decodedImg = Base64.decode(model.getLogo(), Base64.DEFAULT);
        Glide.with(context).load(model.getLogo()).into(holder.logo);

        if (!manager.isLoggedIn())
            holder.follow.setVisibility(View.GONE);

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> headers = new HashMap<>();
                params.put("UserID", user.getId());
                params.put("OrganizationID", model.getId());
                headers.put("SecurityToken", user.getToken());
                FetchData fetchData = new FetchData(context, TAG, null, ADD_ORGANIZATION,
                        Request.Method.POST, params, headers);
                fetchData.getData(new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Log.d(TAG, "onSuccess: " + jsonObject);
                        showFragment(new OrganizationsFragment(), context.getResources().getString(R.string.organizations), ORGANIZATION_FRAGMENT);
                    }
                });
            }
        });

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrganizationDetailsActivity.class);
                intent.putExtra("ID", model.getId());
                intent.putExtra("Name", model.getName());
                intent.putExtra("Description", model.getDescription());
                intent.putExtra("Address", model.getLocation());
                intent.putExtra("Governorate", model.getGovernorate());
                intent.putExtra("Area", model.getRegion());
                intent.putExtra("Logo", model.getLogo());
                intent.putExtra("Categories", model.getCategory());
                context.startActivity(intent);
            }
        });

        holder.logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrganizationDetailsActivity.class);
                intent.putExtra("ID", model.getId());
                intent.putExtra("Name", model.getName());
                intent.putExtra("Description", model.getDescription());
                intent.putExtra("Address", model.getLocation());
                intent.putExtra("Area", model.getRegion());
                intent.putExtra("Governorate", model.getGovernorate());
                intent.putExtra("Logo", model.getLogo());
                intent.putExtra("Categories", model.getCategory());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout details;
        TextView name, location, region, category;
        ImageView logo, follow;

        ViewHolder(View view) {
            super(view);
            details = view.findViewById(R.id.details);
            name = view.findViewById(R.id.orgName);
            location = view.findViewById(R.id.orgAddress);
            region = view.findViewById(R.id.orgRegion);
            category = view.findViewById(R.id.orgCategory);
            logo = view.findViewById(R.id.thumbnail);
            follow = view.findViewById(R.id.followOrg);
        }

        void bind(int position) {

        }

    }
}
