package yackeen.com.daleel.conact;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.crash.FirebaseCrash;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;

public class ContactActivity extends AppCompatActivity implements OnDialogStopListner, View.OnClickListener {

    private static final String TAG = "ContactActivity";
    Toolbar toolbar;
    private ProgressBar progressBar;
    private ImageView email, contactBack;
    private FrameLayout content, mailFrameLayout;
    private boolean dialogVisible;
    private ContactDialog contactDialog;


    void showDialog() {
        dialogVisible = true;
//        // Create and show the dialog.
        contactDialog = ContactDialog.newInstance(6);
        getFragmentManager().beginTransaction().replace(R.id.content, contactDialog).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conact);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        toolbar = findViewById(R.id.toolbar);
        email = findViewById(R.id.email);
        content = findViewById(R.id.content);
        mailFrameLayout = findViewById(R.id.frame);
        contactBack = findViewById(R.id.contactBack);

        mailFrameLayout.setOnClickListener(this);
        contactBack.setOnClickListener(this);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content.setVisibility(View.VISIBLE);
                openImageMail();
                showDialog();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.contact_us);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
    }

    private void openImageMail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            email.setImageDrawable(getDrawable(R.drawable.opened));
        } else {
            email.setImageDrawable(getResources().getDrawable(R.drawable.opened));
        }
    }

    private void closeImageMail() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            email.setImageDrawable(getDrawable(R.drawable.email));
        } else {
            email.setImageDrawable(getResources().getDrawable(R.drawable.email));
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onDialogDestroy() {
        dialogVisible = false;
        getFragmentManager().beginTransaction().remove(contactDialog).commit();
        content.setVisibility(View.GONE);
        closeImageMail();
    }

    @Override
    public void onBackPressed() {
        if (dialogVisible)
            onDialogDestroy();
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (dialogVisible)
            onDialogDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
