package yackeen.com.daleel.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import yackeen.com.daleel.R;
import yackeen.com.daleel.allcases.AllCasesActivity;
import yackeen.com.daleel.bloodbank.BloodBanksActivity;
import yackeen.com.daleel.conact.ContactActivity;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.events.AllEventsActivity;
import yackeen.com.daleel.events.EventsFragment;
import yackeen.com.daleel.home.listener.ChosenId;
import yackeen.com.daleel.home.listener.OnHomeStarted;
import yackeen.com.daleel.home.model.SpinnerAdapterModel;
import yackeen.com.daleel.login.LoginActivity;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.notification.Config;
import yackeen.com.daleel.notification.NotificationUtils;
import yackeen.com.daleel.organization.AllOrganization;
import yackeen.com.daleel.settings.SettingsActivity;
import yackeen.com.daleel.user.ClickListener;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.ADD_TOKEN;
import static yackeen.com.daleel.constants.Constants.EVENT_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.FILTERED_CASES;
import static yackeen.com.daleel.constants.Constants.FILTERED_EVENT;
import static yackeen.com.daleel.constants.Constants.FILTERED_ORGANIZATION;
import static yackeen.com.daleel.constants.Constants.GET_CATEGORIES;
import static yackeen.com.daleel.constants.Constants.GET_GOVERNORATE;
import static yackeen.com.daleel.constants.Constants.GET_ORGANIZATIONS;
import static yackeen.com.daleel.constants.Constants.GET_REGION;
import static yackeen.com.daleel.constants.Constants.HOME_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.LOG_OUT;
import static yackeen.com.daleel.constants.Constants.ORGANIZATION_FRAGMENT;
import static yackeen.com.daleel.constants.Constants.UPDATE_DEVICE_TOKEN;
import static yackeen.com.daleel.home.BottomNavListener.showFragment;
import static yackeen.com.daleel.notification.Config.CASE_NOTIFICATION;
import static yackeen.com.daleel.notification.Config.EVENT_NOTIFICATION;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnHomeStarted,
        View.OnClickListener {

    private static final String TAG = "Fawzy.MainActivity";
    private DrawerLayout drawer;
    private PrefManager manager;
    private String catId = "", orgId = "", locationId = "", placId = "";
    private Spinner orgSpin;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Spinner catSpin;
    private Spinner locationSpin;
    private Spinner locationPlaceSpinner;
    private BottomNavigationView bottomNav;
    private String TOKEN_URL;
    private boolean backTapped;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        manager = new PrefManager(this);
        bottomNav = findViewById(R.id.bottomNav);
        centrizeIcons();

        setToolbar();
        setLogout();
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavListener(this, orgSpin));
        BottomNavViewHelper.removeShiftMode(bottomNav);

        showFragment(new NewsFeedFragment(), getResources().getString(R.string.news_feed), HOME_FRAGMENT);
        setReceiver();
        displayFirebaseRegId();
    }

    private void setReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    Log.e(TAG, "onReceive: " + message);
                }
            }
        };
    }

    private void centrizeIcons() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNav.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
    }

    private void displayFirebaseRegId() {
        if (getIntent().getBooleanExtra("logout", false))
            //in case the user just logged out so we don't want any access on token
            return;
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        TOKEN_URL = "";
        HashMap<String, String> params = new HashMap<>();
        if (manager.isTokenAdded()) {
            TOKEN_URL = UPDATE_DEVICE_TOKEN;
            params.put("UserKey", manager.getUserKey());
        } else {
            TOKEN_URL = ADD_TOKEN;
        }

        params.put("DeviceTokenKey", regId);

        FetchData fetchData = new FetchData(getApplicationContext(), TAG, null,
                TOKEN_URL, Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {

                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        Log.e(TAG, "onSuccess: Success");
                        manager.setTokenAdded(true);
                        if (TOKEN_URL.equals(ADD_TOKEN)) {
                            manager.setUserKey(jsonObject.getJSONObject("Response").getString("UserKey"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.e(TAG, "displayFirebaseRegId: " + regId);
        else
            Log.e(TAG, "displayFirebaseRegId: error");

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());


        if (manager.getNotificationType() != null) {
            Log.e(TAG, "onResume: Fragment should be fired!!" + manager.getNotificationType());
            if (manager.getNotificationType().equals(EVENT_NOTIFICATION)) {
                showFragment(new EventsFragment(), getResources().getString(R.string.events),
                        EVENT_FRAGMENT);
                bottomNav.setSelectedItemId(R.id.event);
                manager.setNotificationType(null);
            } else if (manager.getNotificationType().equals(CASE_NOTIFICATION)) {
                showFragment(new NewsFeedFragment(), getResources().getString(R.string.home),
                        HOME_FRAGMENT);
                bottomNav.setSelectedItemId(R.id.home);
                manager.setNotificationType(null);
            }
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void setSpinner(final Spinner spinner, String URL, final String hint, int type) {
        final List<SpinnerAdapterModel> catList = new ArrayList<>();

        HashMap<String, String> params = new HashMap<>();

        if (type == 1)
            params.put("GovernorateID", locationId);

        FetchData fetchData = new FetchData(MainActivity.this, TAG, null,
                URL, Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    SpinnerAdapterModel defaultModel = new SpinnerAdapterModel();
                    defaultModel.setName(hint);
                    defaultModel.setId("");
                    catList.add(defaultModel);
                    if (isSuccess) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            SpinnerAdapterModel model = new SpinnerAdapterModel();
                            model.setId(jsonArray.getJSONObject(i).getString("ID"));
                            model.setName(jsonArray.getJSONObject(i).getString("Name"));
                            catList.add(model);
                        }

                        FilterSpinnerAdapter adapter = new FilterSpinnerAdapter(MainActivity.this,
                                hint, catList);

                        adapter.setAdapter(spinner, new ChosenId() {
                            @Override
                            public void theChosenId(String categoryId, String organizationId, String locId, String placeID, Spinner spinner1) {

                                if (spinner.getId() == R.id.catSpinner) {
                                    catId = categoryId;
                                } else if (spinner.getId() == R.id.orgSpinner) {
                                    orgId = organizationId;
                                } else if (spinner.getId() == R.id.locationSpinner) {
                                    locationId = locId;
                                    if (!"".equals(locationId))
                                        setSpinner(locationPlaceSpinner, GET_REGION, getResources().getString(R.string.choose_region), 1);
                                } else if (spinner.getId() == R.id.locationPlaceSpinner) {
                                    placId = placeID;
                                }
//                                Log.e(TAG, "theChosenId: " + catId + ", " + orgId + ", " + locationId);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setDrawer(toolbar);
    }

    private void setLogout() {
        LinearLayout signLayout = findViewById(R.id.signLayout);
        TextView signTV = findViewById(R.id.signTV);
        signTV.setText(manager.isLoggedIn() ? getResources().getString(R.string.sign_out) : getResources().getString(R.string.sign_in));
        signLayout.setOnClickListener(this);
    }


    private void setDrawer(Toolbar toolbar) {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.tog);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        // Perform navigation item
        navigationView = findViewById(R.id.nav_view);
        NavigationView filterNav = findViewById(R.id.nav_view2);
        View view = navigationView.getHeaderView(0);
        View filterView = filterNav.getHeaderView(0);
        filterNav.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        catSpin = filterView.findViewById(R.id.catSpinner);
        orgSpin = filterView.findViewById(R.id.orgSpinner);
        locationSpin = filterView.findViewById(R.id.locationSpinner);
        locationPlaceSpinner = filterView.findViewById(R.id.locationPlaceSpinner);
        TextView sort = filterView.findViewById(R.id.sort);
        //Click listener for sort button
        sort.setOnClickListener(this);

        setSpinners();

        TextView userName, userEmail;
        ImageView pic = view.findViewById(R.id.userThumbnail);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);

        // Set item visibility depend on user statue
        if (!manager.isLoggedIn()) {
            userName.setVisibility(View.GONE);
            pic.setVisibility(View.GONE);
            userEmail.setVisibility(View.GONE);
        } else {
            User user = manager.getUser();
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());
            byte[] decodedImg = Base64.decode(user.getThumbnail(), Base64.DEFAULT);
//            Log.e(TAG, "setDrawer:username " + user.getName()+"image: "+user.getThumbnail());
            Glide.with(this).load(decodedImg)
                    .placeholder(getResources().getDrawable(R.drawable.prof)).dontAnimate().into(pic);

        }
        pic.setOnClickListener(new ClickListener(this));
        userName.setOnClickListener(new ClickListener(this));
        userEmail.setOnClickListener(new ClickListener(this));
    }

    private void setSpinners() {
        setSpinner(catSpin, GET_CATEGORIES, getResources().getString(R.string.chooseCat), 0);
        setSpinner(orgSpin, GET_ORGANIZATIONS, getResources().getString(R.string.choose_org), 0);
        setSpinner(locationSpin, GET_GOVERNORATE, getResources().getString(R.string.choose_place), 0);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (bottomNav.getSelectedItemId() != R.id.home) {
                showFragment(new NewsFeedFragment(), getResources().getString(R.string.home),
                        HOME_FRAGMENT);
                bottomNav.setSelectedItemId(R.id.home);
            } else {
                if (!backTapped)
                    startHandler();
                else
                    super.onBackPressed();
            }
        }
    }

    private void startHandler() {
        backTapped = !backTapped;
        Snackbar.make(bottomNav, getResources().getString(R.string.pressBackAgain), Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backTapped = false;
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            case R.id.action_filter:
                drawer.openDrawer(GravityCompat.END);
                break;
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showFragment(new NewsFeedFragment(), getResources().getString(R.string.news_feed), HOME_FRAGMENT);
        } else if (id == R.id.nav_blod_bank) {
            launchActivity(BloodBanksActivity.class);
        } else if (id == R.id.nav_contact) {
            launchActivity(ContactActivity.class);

        } else if (id == R.id.nav_setting) {
            launchActivity(SettingsActivity.class);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchActivity(Class<?> mClass) {
        startActivity(new Intent(this, mClass));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signLayout:
                checkSigning();
                break;
            case R.id.sort:
                if (!"".equals(placId)) {
                    if (getFragmentManager().findFragmentByTag(HOME_FRAGMENT) != null &&
                            getFragmentManager().findFragmentByTag(HOME_FRAGMENT).isVisible()) {
                        sort(FILTERED_CASES, AllCasesActivity.class);
                    } else if (getFragmentManager().findFragmentByTag(ORGANIZATION_FRAGMENT) != null &&
                            getFragmentManager().findFragmentByTag(ORGANIZATION_FRAGMENT).isVisible()) {
                        sortOrganizations();
                    } else if (getFragmentManager().findFragmentByTag(EVENT_FRAGMENT) != null &&
                            getFragmentManager().findFragmentByTag(EVENT_FRAGMENT).isVisible()) {
                        sort(FILTERED_EVENT, AllEventsActivity.class);
                    }
                } else
                    Toast.makeText(this, "Please select region", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sortOrganizations() {
        Intent intent = new Intent(this, AllOrganization.class);
        intent.putExtra("URL", FILTERED_ORGANIZATION);
        intent.putExtra("catId", catId);
        intent.putExtra("locationId", placId);
        startActivity(intent);
    }

    private void sort(String URL, Class<?> clas) {
        Intent intent = new Intent(this, clas);
        intent.putExtra("URL", URL);
        intent.putExtra("type", "Recent");
        intent.putExtra("catId", catId);
        intent.putExtra("orgId", orgId);
        intent.putExtra("locationId", placId);
        startActivity(intent);
    }

    private void checkSigning() {
        if (manager.isLoggedIn()) {
            logoutRequest();
        } else {
            launchActivity(LoginActivity.class);
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private void performLogout() {
        manager.clearSession();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("logout", true);
        startActivity(intent);
        finish();
    }

    private void logoutRequest() {
        ProgressBar progress = findViewById(R.id.progress);
        User user = manager.getUser();
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        params.put("SecurityToken", user.getToken());
        headers.put("SecurityToken", user.getToken());

        FetchData fetchData = new FetchData(getApplicationContext(), TAG, progress,
                LOG_OUT, Request.Method.POST, params, headers);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        Log.e(TAG, "onSuccess: Success");
                        performLogout();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onHomeStarted() {
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    @Override
    public void onHomeStopped() {
        navigationView.getMenu().getItem(0).setChecked(false);
    }
}
