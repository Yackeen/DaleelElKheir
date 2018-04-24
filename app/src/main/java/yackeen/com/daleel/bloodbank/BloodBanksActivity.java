package yackeen.com.daleel.bloodbank;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Spinner;
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
import yackeen.com.daleel.home.FilterSpinnerAdapter;
import yackeen.com.daleel.home.listener.ChosenId;
import yackeen.com.daleel.home.model.SpinnerAdapterModel;

import static yackeen.com.daleel.constants.Constants.BLOOD_BANKS;
import static yackeen.com.daleel.constants.Constants.BLOOD_BANKS_FILTER;
import static yackeen.com.daleel.constants.Constants.GET_GOVERNORATE;
import static yackeen.com.daleel.constants.Constants.GET_REGION;

public class BloodBanksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BloodBanksActivity";
    String locationId = "", placId = "";
    private DrawerLayout drawer;
    private Spinner locationPlaceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_banks);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        setToolbar();

        Bundle bundle = new Bundle();
        bundle.putString("url", BLOOD_BANKS);
        loadBloodBanksList(bundle);
    }

    private void loadBloodBanksList(Bundle bundle) {
        BloodBankList bloodBankList = new BloodBankList();
        bloodBankList.setArguments(bundle);
        showFragment(bloodBankList);
    }


    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.blood_banks));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView filterNav = findViewById(R.id.nav_view2);
        View filterView = filterNav.getHeaderView(0);
        filterNav.setNavigationItemSelectedListener(this);
        Spinner locationSpin = filterView.findViewById(R.id.locationSpinner);
        locationPlaceSpinner = filterView.findViewById(R.id.locationPlaceSpinner);
        Spinner catSpin = filterView.findViewById(R.id.catSpinner);
        Spinner orgSpin = filterView.findViewById(R.id.orgSpinner);
        catSpin.setVisibility(View.GONE);
        orgSpin.setVisibility(View.GONE);
        TextView sort = filterView.findViewById(R.id.sort);
        //Click listener for sort button
        setSpinner(locationSpin, GET_GOVERNORATE, getResources().getString(R.string.choose_place));
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("location", placId);
                bundle.putString("url", BLOOD_BANKS_FILTER);
                loadBloodBanksList(bundle);
                drawer.closeDrawer(GravityCompat.END);
            }
        });
    }


    private void setSpinner(final Spinner spinner, String URL, final String hint) {
        final List<SpinnerAdapterModel> catList = new ArrayList<>();

        HashMap<String, String> params = new HashMap<>();
        params.put("GovernorateID", locationId);

        FetchData fetchData = new FetchData(BloodBanksActivity.this, TAG, null,
                URL, Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    SpinnerAdapterModel defaultModel = new SpinnerAdapterModel();
                    defaultModel.setName(hint);
                    defaultModel.setId(" ");
                    catList.add(defaultModel);
                    if (isSuccess) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            SpinnerAdapterModel model = new SpinnerAdapterModel();
                            model.setId(jsonArray.getJSONObject(i).getString("ID"));
                            model.setName(jsonArray.getJSONObject(i).getString("Name"));
                            catList.add(model);
                        }

                        FilterSpinnerAdapter adapter = new FilterSpinnerAdapter(BloodBanksActivity.this,
                                hint, catList);

                        adapter.setAdapter(spinner, new ChosenId() {
                            @Override
                            public void theChosenId(String categoryId, String organizationId, String locID, String placeId, Spinner spinner1) {

                                if (spinner.getId() == R.id.catSpinner) {
                                    //catId = categoryId;
                                } else if (spinner.getId() == R.id.orgSpinner) {
                                    //orgId = organizationId;
                                } else if (spinner.getId() == R.id.locationSpinner) {
                                    locationId = locID;
                                    if (!"".equals(locationId))
                                        setSpinner(locationPlaceSpinner, GET_REGION, getResources().getString(R.string.choose_region));
                                } else if (spinner.getId() == R.id.locationSpinner) {
                                    placId = placeId;
                                }
//                                Log.d(TAG, "theChosenId: " + ", " + locationId);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setQueryHint(getResources().getString(R.string.searchCases));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: " + s);
                //search(s);
                Bundle bundle = new Bundle();
                bundle.putString("url", BLOOD_BANKS);
                bundle.putString("searchedText", s);
                ;
                loadBloodBanksList(bundle);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {


                return true;

            }

        });
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

    public void showFragment(Fragment fragment) {


        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Using android framework animation to navigate between fragment
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        // Replace the new fragment
        ft.replace(R.id.content, fragment, "detailFragment");
        // Start the animated transition.
        ft.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
