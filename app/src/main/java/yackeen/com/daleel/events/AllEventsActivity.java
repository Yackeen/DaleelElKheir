package yackeen.com.daleel.events;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;

public class AllEventsActivity extends AppCompatActivity {

    private static final String TAG = "AllEventsActivity";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private String url;
    private TextView noCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        noCases = findViewById(R.id.noCasesMsg);
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
        setTitle(getResources().getString(R.string.all_events));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setRecycler(String URL) {
        recyclerView = findViewById(R.id.recycler);
        ProgressBar progressBar = findViewById(R.id.progress);
        HashMap<String, String> params = new HashMap<>();
        if (getIntent().getExtras().getString("catId") != null) {
            params.put("CategoryID", getIntent().getExtras().getString("catId"));
            params.put("OrganizationID", getIntent().getExtras().getString("orgId"));
            params.put("RegionID", getIntent().getExtras().getString("locationId"));
        }
        FetchData fetchData = new FetchData(AllEventsActivity.this, TAG, progressBar, URL,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");


                    if (isSuccess) {
                        List<EventsModel> data = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            EventsModel model = new EventsModel();
                            model.setId(object.getString("ID"));
                            model.setTitle(object.getString("Title"));
                            model.setLocation(object.getString("Governorate") + " " +
                                    object.getString("Region"));
                            model.setOrganization(object.getString("Organization"));
                            model.setDescription(object.getString("Description"));
                            model.setImage(object.getString("Image"));
                            model.setStartDate(object.getString("StartDate"));
                            model.setEndDate(object.getString("EndDate"));
                            model.setMobile(object.getString("Mobile"));
                            model.setTime(object.getString("StartDate"));
                            if (getIntent().getExtras().getString("searchText") != null) {
                                if (!isContain(object.getString("Title"),
                                        getIntent().getExtras().getString("searchText")))
                                    continue;
                            }
                            data.add(model);
                        }
                        if (data.size() <= 0)
                            noCases.setVisibility(View.VISIBLE);
                        else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllEventsActivity.this));
                            recyclerView.setAdapter(new EventsAdapter(AllEventsActivity.this, data));
                            recyclerView.addItemDecoration(
                                    new DividerItemDecoration(AllEventsActivity.this, DividerItemDecoration.VERTICAL));
                        }
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "JsonException : ", ex);
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


}
