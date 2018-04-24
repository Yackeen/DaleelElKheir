package yackeen.com.daleel.allcases;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.chat.ChattingActivity;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.login.LoginActivity;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.ADD_CASE;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailActivity";
    double val = 0;
    private ImageView share, thumbnail;
    private TextView caseName, organization, date, currentAmount, requiredAmount, description,
            caseStatue, location, category, code;
    private ProgressBar progressBar, progress;
    private PrefManager manager;
    private FloatingActionButton addCase;
    private Toolbar toolbar;
    private AlertDialog.Builder dialog;
    private boolean Joined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        initView();

        final User user = manager.getUser();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Add case to user
        addCase(user);

        //Share case
        share.setOnClickListener(this);

        try {
            String currentCash = getIntent().getExtras().getString("currentAmount");
            String requiredCash = getIntent().getExtras().getString("requiredAmount");
            Joined = getIntent().getExtras().getBoolean("Joined");

            String RU = getIntent().getExtras().getString("type");
            caseStatue.setText(RU);
            assert RU != null;
            if(RU.equals("Recent"))
                caseStatue.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

//            Toast.makeText(this, getIntent().getExtras().getString("type"), Toast.LENGTH_SHORT).show();

            String CaseCode = getIntent().getExtras().getString("CaseCode");
            code.setText(CaseCode);

            if (currentCash == null || currentCash.equals("null"))
                currentCash = "0";
            if (requiredCash == null || requiredCash.equals("null"))
                requiredCash = "0";
//            final byte[] decodedImg = Base64.decode(getIntent().getExtras().getString("image"), Base64.DEFAULT);
            Glide.with(this).load(getIntent().getExtras().getString("image")).into(thumbnail);
            caseName.setText(getIntent().getExtras().getString("caseName"));
            organization.setText(getIntent().getExtras().getString("organization"));
            currentAmount.setText(getIntent().getExtras().getString("currentAmount"));
            requiredAmount.setText(getIntent().getExtras().getString("requiredAmount"));
            description.setText(Html.fromHtml(getIntent().getExtras().getString("description")));
            location.setText(getIntent().getExtras().getString("place"));
            category.setText(getIntent().getExtras().getString("department"));
            val = Double.valueOf(currentCash) /
                    Double.valueOf(requiredCash) * 100.0;

            date.setText(getIntent().getExtras().getString("date"));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        progressBar.setProgress((int) val);

        if (Joined)
            addCase.setImageResource(android.R.drawable.ic_menu_send);

    }

    private void addCase(final User user) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            dialog = new AlertDialog.Builder(this);
        }
        dialog.setTitle(getResources().getString(R.string.please_login))
                .setPositiveButton(getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(DetailActivity.this, LoginActivity.class));
                        finish();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setMessage(getResources().getString(R.string.please_login_first));

        addCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (manager.isLoggedIn()) {
                    if (Joined) {
                        int casID = Integer.parseInt(getIntent().getExtras().getString("caseId"));
                        String casName = getIntent().getExtras().getString("caseName");

                        Intent intent = new Intent(DetailActivity.this, ChattingActivity.class);
                        intent.putExtra("CaseID", casID);
                        intent.putExtra("CaseName", casName);
                        startActivity(intent);
                    } else {
                        HashMap<String, String> params = new HashMap<>();
                        HashMap<String, String> headers = new HashMap<>();
                        params.put("UserID", user.getId());
                        params.put("CaseID", getIntent().getExtras().getString("caseId"));
                        headers.put("SecurityToken", user.getToken());
                        FetchData fetchData = new FetchData(DetailActivity.this, TAG, progress, ADD_CASE,
                                Request.Method.POST, params, headers);
                        fetchData.getData(new VolleyCallBack() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                Log.d(TAG, "onSuccess: " + jsonObject);
                                try {
                                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                                    if (isSuccess) {
                                        String res = jsonObject.getString("Response");
                                        addCase.setImageResource(android.R.drawable.ic_menu_send);

                                        int casID = Integer.parseInt(getIntent().getExtras().getString("caseId"));
                                        String casName = getIntent().getExtras().getString("caseName");

                                        Intent intent = new Intent(DetailActivity.this, ChattingActivity.class);
                                        intent.putExtra("CaseID", casID);
                                        intent.putExtra("CaseName", casName);
                                        startActivity(intent);

                                        Toast.makeText(DetailActivity.this, res, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    dialog.show();
                }
            }
        });
    }

    private void initView() {
        share = findViewById(R.id.shareCase);
        thumbnail = findViewById(R.id.thumbnail);
        caseName = findViewById(R.id.caseName);
        organization = findViewById(R.id.organization);
        date = findViewById(R.id.date);
        currentAmount = findViewById(R.id.currentAmount);
        requiredAmount = findViewById(R.id.requiredAmount);
        description = findViewById(R.id.description);
        caseStatue = findViewById(R.id.caseStatue);
        category = findViewById(R.id.category);
        location = findViewById(R.id.region);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.progress);
        addCase = findViewById(R.id.addCase);
        toolbar = findViewById(R.id.toolbar);
        code = findViewById(R.id.code);
        manager = new PrefManager(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shareCase:
                share();
                break;
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getExtras().getString("sharedLink", "CASE"));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.app_name)));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
