package yackeen.com.daleel.organization;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.organization.model.OrganizationModel;

public class AllOrganization extends AppCompatActivity {

    private static final String TAG = "AllOrganization";
    String category = "";
    private RecyclerView recyclerView;
    private ProgressBar progress;
    private TextView noOrgs;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_organization);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        noOrgs = findViewById(R.id.noOrgsMsg);
        try {

            url = getIntent().getExtras().getString("URL");
        } catch (NullPointerException ex) {
            Log.e(TAG, "onCreate: " + "Error with intent data $URL");
        }
        setToolbar();
        setRecycler(url);

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setRecycler(String URL) {
        recyclerView = findViewById(R.id.recycler);
        ProgressBar progressBar = findViewById(R.id.progress);
        HashMap<String, String> params = new HashMap<>();
        if (getIntent().getExtras().getString("catId") != null) {
            params.put("CategoryID", getIntent().getExtras().getString("catId"));
            params.put("RegionID", getIntent().getExtras().getString("locationId"));
        }
        FetchData fetchData = new FetchData(AllOrganization.this, TAG, progressBar, URL,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                List<OrganizationModel> list = new ArrayList<>();

                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            OrganizationModel model = new OrganizationModel();
                            model.setId(object.getString("ID"));
                            model.setName(object.getString("Name"));
                            model.setDescription(object.getString("Description"));
                            model.setLocation(object.getString("Address"));
                            model.setRegion(object.getString("Area"));
                            model.setGovernorate(object.getString("Governorate"));
                            model.setLogo(object.getString("Logo"));

                            JSONArray categoryArr = object.getJSONArray("Categories");
                            for (int j = 0; j < categoryArr.length(); j++) {
                                JSONObject categoryObj = categoryArr.getJSONObject(j);

                                if (j != categoryArr.length() - 1)
                                    category += categoryObj.getString("Name") + ", ";
                                else
                                    category += categoryObj.getString("Name");
                            }

                            if ("".equals(category))
                                category = "null";

                            model.setCategory(category);
                            category = "";

                            if (getIntent().getExtras().getString("searchText") != null) {
                                if (!object.getString("Name").toLowerCase()
                                        .contains(getIntent().getExtras().getString("searchText").toLowerCase()))
                                    continue;
                            }

                            list.add(model);
                        }

                        if (list.size() <= 0)
                            noOrgs.setVisibility(View.VISIBLE);
                        else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllOrganization.this));
                            recyclerView.setAdapter(new AllOrganizationAdapter(AllOrganization.this, list));
                        }
                    }
                } catch (JSONException ex) {

                }
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
