package yackeen.com.daleel.home;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.allcases.AllCasesActivity;
import yackeen.com.daleel.allcases.model.CaseModel;
import yackeen.com.daleel.circleindicator.CircleIndicator;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.home.listener.OnHomeStarted;

import static yackeen.com.daleel.constants.Constants.ALL_CASES;
import static yackeen.com.daleel.constants.Constants.GET_CASES;
import static yackeen.com.daleel.constants.Constants.URGENT_CASES;


/**
 * Created by Ibrahim on 01/02/2018.
 */

public class NewsFeedFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "NewsFeedFragment";
    ViewPager viewPager;
    private View view;
    private RecyclerView recyclerView;
    private ProgressBar progressBar, urgentProgress;
    private AdapterViewpager adapterViewpager;
    private CircleIndicator indicator;
    private FetchData fetchData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        OnHomeStarted onHomeStarted = (OnHomeStarted) getActivity();
        onHomeStarted.onHomeStarted();
    }

    @Override
    public void onStop() {
        super.onStop();
        OnHomeStarted onHomeStarted = (OnHomeStarted) getActivity();
        onHomeStarted.onHomeStopped();
    }

    private void search(String s) {
        Intent intent = new Intent(getActivity(), AllCasesActivity.class);
        intent.putExtra("URL", ALL_CASES);
        intent.putExtra("searchText", s);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));

        search.setQueryHint(getResources().getString(R.string.searchCases));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: " + s);
                search(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {


                return true;

            }

        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        setViews(view);

        return view;
    }

    public void setViews(View view) {
        TextView moreRecentCases = view.findViewById(R.id.more2);
        TextView moreUrgentCases = view.findViewById(R.id.more);
        progressBar = view.findViewById(R.id.progress);
        urgentProgress = view.findViewById(R.id.urgentProgress);
        indicator = view.findViewById(R.id.indicator);
        moreRecentCases.setOnClickListener(this);
        moreUrgentCases.setOnClickListener(this);
        setRecycler();
        setViewpager(view);
        setScrollView(view);
    }

    private void setViewpager(final View view) {
        viewPager = view.findViewById(R.id.viewPager);
        HashMap<String, String> params = new HashMap<>();
        fetchData = new FetchData(getActivity(), TAG, urgentProgress, URGENT_CASES,
                Request.Method.POST, params, null);

        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {

                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
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
                            model.setSharedLink(object.getString("SharedURL"));
                            model.setCaseCode(object.getString("CaseCode"));
                            data.add(model);
                        }

                        adapterViewpager = new AdapterViewpager(getActivity(), data);
                        viewPager.setAdapter(adapterViewpager);
                        //int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20 * 2, getResources().getDisplayMetrics());
                        // viewPager.setPadding(margin, 0, margin, 0);
                        viewPager.setCurrentItem(2, true);
                        indicator.setViewPager(viewPager);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchData != null)
            fetchData.cancelRequest();
    }

    public void setRecycler() {
        recyclerView = view.findViewById(R.id.recycler);
        HashMap<String, String> params = new HashMap<>();
        fetchData = new FetchData(getActivity(), TAG, progressBar, GET_CASES,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {

                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
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
                            model.setSharedLink(object.getString("SharedURL"));
                            model.setCaseCode(object.getString("CaseCode"));
                            data.add(model);
                        }
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        recyclerView.setAdapter(new NewsFeedAdapter(getActivity(), data));
                        recyclerView.setNestedScrollingEnabled(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    public void setScrollView(View view) {
        NestedScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, 0);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more2:
                launchAllCasesActivity(ALL_CASES);
                break;
            case R.id.more:
                launchAllCasesActivity(URGENT_CASES);
                break;
        }
    }

    private void launchAllCasesActivity(String URL) {
        Intent intent = new Intent(getActivity(), AllCasesActivity.class);
        intent.putExtra("URL", URL);
        startActivity(intent);
    }
}
