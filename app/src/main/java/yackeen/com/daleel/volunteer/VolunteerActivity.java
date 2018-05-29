package yackeen.com.daleel.volunteer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.PostData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.constants.Constants;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.volunteer.Models.CategoryModel;

import static yackeen.com.daleel.constants.Constants.ADD_VOLUNTEER;

public class VolunteerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "VolunteerActivity";
    Toolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private VolunteerCategAdapter adapter;
    private Button submit;
    private EditText name, mail, job, nationality, aboutQuestion, volunteeringMethod, phone;
    private CheckBox sat, sun, mon, tue, wen, thur, fri;
    private StringBuilder weekDays;
    private JSONArray volunteeringCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.volunteer);
        toolbar.setTitleTextColor(Color.WHITE);
        setViews();
        getData(Constants.VOLUNTEER_CATEGORIES);
    }

    private void setViews() {
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        tue = findViewById(R.id.tus);
        mon = findViewById(R.id.mon);
        wen = findViewById(R.id.wen);
        thur = findViewById(R.id.thur);
        fri = findViewById(R.id.fri);
        progressBar = findViewById(R.id.progress);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        job = findViewById(R.id.job);
        nationality = findViewById(R.id.nationality);
        aboutQuestion = findViewById(R.id.how);
        volunteeringMethod = findViewById(R.id.how2);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    private void getData(String URL) {
        recyclerView = findViewById(R.id.recycler);
        PrefManager manager = new PrefManager(this);
        final ProgressBar progressBar = findViewById(R.id.progress);
        HashMap<String, String> params = new HashMap<>();
        params.put("Lang", manager.getAppLanguage());
        FetchData fetchData = new FetchData(VolunteerActivity.this, TAG, progressBar, URL,
                Request.Method.POST, params, null);
        progressBar.setVisibility(View.VISIBLE);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    progressBar.setVisibility(View.GONE);
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    Log.d(TAG, "onSuccess: " + jsonObject);
                    if (isSuccess) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        List<CategoryModel> data = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            CategoryModel model = new CategoryModel();
                            model.setName(object.getString("Name"));
                            model.setId(object.getString("ID"));

                            data.add(model);
                        }
                        updateAdapter(data);
                    }
                } catch (JSONException ex) {

                }
            }
        });

    }

    private void updateAdapter(List<CategoryModel> data) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VolunteerCategAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (validate()) {
            submitRequest();
        }

    }

    private boolean validate() {
        boolean valid = true;
        volunteeringCategories = getCheckedCategories();
        if (name.getText().toString().trim().isEmpty()) {
            valid = false;
            name.setError(getResources().getString(R.string.error_field_required));
        }
        if (mail.getText().toString().trim().isEmpty()) {
            valid = false;
            mail.setError(getResources().getString(R.string.error_field_required));
        }
        if (phone.getText().toString().trim().isEmpty()) {
            valid = false;
            phone.setError(getResources().getString(R.string.error_field_required));
        }
        if (volunteeringCategories == null || volunteeringCategories.length() == 0) {
            valid = false;
            Snackbar.make(sat, R.string.DaysAvailableRequired, Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }

    private void submitRequest() {
        getCheckedWeekDays();
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Accept", "application/json");
//        headers.put("Content-Type", "application/json");
        JSONObject body = new JSONObject();
        try {
            body.put("Name", name.getText().toString());
            body.put("Email", mail.getText().toString());
            body.put("Contact", phone.getText().toString());
            body.put("Job", job.getText().toString());
            body.put("Nationality", nationality.getText().toString());
            body.put("AboutQuestion", aboutQuestion.getText().toString());
            body.put("VoulunteeringMethod", aboutQuestion.getText().toString());
            body.put("DaysAvailable", weekDays.toString());
            body.put("Categories", volunteeringCategories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostData fetchData = new PostData(this, body, ADD_VOLUNTEER,
                Request.Method.POST, progressBar, null);
        try {
            fetchData.post(new VolleyCallBack() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    boolean isSuccess = false;
                    try {
                        isSuccess = jsonObject.getBoolean("IsSuccess");
                        if (isSuccess) {
                            Toast.makeText(VolunteerActivity.this, "sent successfully", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getCheckedWeekDays() {
        weekDays = new StringBuilder();
        if (sat.isChecked())
            weekDays.append("Saturday");
        if (sun.isChecked())
            weekDays.append(" Sunday");
        if (mon.isChecked())
            weekDays.append(" Monday");
        if (tue.isChecked())
            weekDays.append(" Tuesday");
        if (wen.isChecked())
            weekDays.append(" Wednesday");
        if (thur.isChecked())
            weekDays.append(" Thursday");
        if (fri.isChecked())
            weekDays.append(" Friday");
    }

    public JSONArray getCheckedCategories() {
        List<CategoryModel> modelList = adapter.getList();
        List<String> checkedList = new ArrayList<>();
        JSONArray array = new JSONArray();

        for (int i = 0; i < modelList.size(); i++) {
            if (modelList.get(i).isChecked()) {
                checkedList.add(modelList.get(i).getId());
                array.put(Integer.parseInt(modelList.get(i).getId()));
            }
        }
        return array;
    }
}
