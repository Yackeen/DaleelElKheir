package yackeen.com.daleel.organization;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
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

import static yackeen.com.daleel.constants.Constants.ORGANIZATION_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.UN_FOLLOW_ORGANIZATION;
import static yackeen.com.daleel.home.BottomNavListener.showFragment;


/**
 * Created by fawzy on 1/2/18.
 */
public class AdapterViewpager extends PagerAdapter implements View.OnClickListener {
    private static final String TAG = "AdapterViewpager";
    private Context context;
    private List<OrganizationModel> list = new ArrayList<>();
    private PrefManager manager;
    private User user;

    public AdapterViewpager(Context context, List<OrganizationModel> list) {
        this.context = context;
        this.list = list;
        manager = new PrefManager(context);
        user = manager.getUser();
    }

    @Override
    public int getCount() {
        return list.size() > 5 ? 5 : list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_orgs_pager, container, false);

        // Init View
        LinearLayout details;
        TextView orgName, orgCategory, orgRegion, orgLocation;
        ImageView thumbnail, unFollow;
        details = view.findViewById(R.id.details);
        orgName = view.findViewById(R.id.orgName);
        orgCategory = view.findViewById(R.id.orgCategory);
        orgRegion = view.findViewById(R.id.orgRegion);
        orgLocation = view.findViewById(R.id.orgAddress);
        thumbnail = view.findViewById(R.id.thumbnail);
        unFollow = view.findViewById(R.id.unFollowOrg);

        // Fetch Data
        final OrganizationModel model = list.get(position);
        orgName.setText(model.getName());
        orgCategory.setText(model.getCategory());
        orgRegion.setText(model.getRegion());
        orgLocation.setText(model.getLocation());

//        final byte[] decodedImg = Base64.decode(model.getLogo(), Base64.DEFAULT);
        Glide.with(context).load(model.getLogo()).into(thumbnail);

        // UnFollow organization
        unFollow.setOnClickListener(new View.OnClickListener() {
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
                        showFragment(new OrganizationsFragment(), context.getResources().getString(R.string.organizations), ORGANIZATION_FRAGMENT);
                    }
                });
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
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

        thumbnail.setOnClickListener(new View.OnClickListener() {
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

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onClick(View v) {

    }
}
