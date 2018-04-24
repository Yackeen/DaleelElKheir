package yackeen.com.daleel.allcases;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import yackeen.com.daleel.allcases.model.CaseModel;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;

public class AllCasesActivity extends AppCompatActivity {
    private static final String TAG = "AllCasesActivity";
    private RecyclerView recyclerView;
    private String url;
    private TextView noCases;
    private PrefManager manager;
    private User user;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_cases);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        manager = new PrefManager(AllCasesActivity.this);
        user = manager.getUser();

        noCases = findViewById(R.id.noCasesMsg);
        try {
            url = getIntent().getExtras().getString("URL");
            type = getIntent().getExtras().getString("type");
        } catch (NullPointerException ex) {
            Log.e(TAG, "onCreate: " + "Error with intent data $URL");
        }
        setToolbar();
        setRecycler(url);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.all_cases));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setRecycler(String URL) {
        recyclerView = findViewById(R.id.recycler);
        ProgressBar progressBar = findViewById(R.id.progress);
        HashMap<String, String> params = new HashMap<>();

        if (manager.isLoggedIn())
            params.put("UserID", user.getId());

        if (getIntent().getExtras().getString("catId") != null) {
            params.put("CategoryID", getIntent().getExtras().getString("catId"));
            params.put("OrganizationID", getIntent().getExtras().getString("orgId"));
            params.put("RegionID", getIntent().getExtras().getString("locationId"));
        }
        FetchData fetchData = new FetchData(AllCasesActivity.this, TAG, progressBar, URL,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {

                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    Log.d(TAG, "onSuccess: " + jsonObject);
                    if (isSuccess) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        List<CaseModel> data = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            CaseModel model = new CaseModel();
                            model.setName(object.getString("Name"));
                            model.setId(object.getString("ID"));
                            model.setCity(object.getString("City"));
                            model.setOrganization(object.getString("Organization"));
                            model.setDueDate(object.getString("DueDate"));
                            model.setImage(object.getString("Image"));
                            model.setGovernorate(object.getString("Governorate"));
                            model.setCategory(object.getString("Category"));
                            model.setCaseType(object.getString("CaseType"));
                            model.setCaseStatue(object.getString("CaseStatus"));
                            model.setRequiredAmount(object.getString("RequiredAmount"));
                            model.setCurrentAmount(object.getString("CurrentAmount"));
                            model.setDescription(object.getString("Description"));
                            model.setCaseCode(object.getString("CaseCode"));
                            model.setJoined(object.getBoolean("Joined"));
                            model.setType(type);
                            if (getIntent().getExtras().getString("searchText") != null) {
                                if (!isContain(object.getString("Name"),
                                        getIntent().getExtras().getString("searchText")))
                                    continue;
                            }

                            data.add(model);
                        }
                        if (data.size() <= 0)
                            noCases.setVisibility(View.VISIBLE);
                        else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllCasesActivity.this));
                            recyclerView.setAdapter(new AllCasesAdapter(AllCasesActivity.this, data));
                            recyclerView.addItemDecoration(
                                    new DividerItemDecoration(AllCasesActivity.this, DividerItemDecoration.VERTICAL));
                        }
                    }
                } catch (JSONException ex) {

                }
            }
        });

    }

    boolean isContain(String title, String searched) {
        return title.toLowerCase().contains(searched.toLowerCase());
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
