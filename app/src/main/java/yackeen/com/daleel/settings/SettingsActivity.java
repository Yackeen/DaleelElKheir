package yackeen.com.daleel.settings;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.manager.PrefManager;

import static yackeen.com.daleel.constants.Constants.NOTIFICATION_ENABLED;

public class SettingsActivity extends AppCompatActivity {

    Switch langSwitch, notificationSwitch;
    PrefManager manager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // init View
        langSwitch = findViewById(R.id.setLang);
        notificationSwitch = findViewById(R.id.setNotification);

        manager = new PrefManager(this);
        setTitle(getResources().getString(R.string.action_settings));

        setLangSwitch();

        setNotificationSwitch();


    }


    private void setNotificationSwitch() {
        if (manager.getNotificationStatue() == 1)
            notificationSwitch.setChecked(true);
        else notificationSwitch.setChecked(false);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    manager.setAppNotification(NOTIFICATION_ENABLED);
                    compoundButton.setChecked(true);
                } else {
                    manager.setAppNotification(0);
                    compoundButton.setChecked(false);
                }
            }
        });
    }

    private void setLangSwitch() {
        if (manager.getAppLanguage() != null) {
            if (manager.getAppLanguage().equals("en")) {
                langSwitch.setChecked(true);
            } else {
                langSwitch.setChecked(false);
            }
        }
        langSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    manager.setAppLanguage("en");
                    compoundButton.setChecked(true);
                    setLang("en");
                } else {
                    manager.setAppLanguage("ar");
                    compoundButton.setChecked(false);
                    setLang("ar");
                }
            }
        });
    }

    void setLang(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        this.finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
