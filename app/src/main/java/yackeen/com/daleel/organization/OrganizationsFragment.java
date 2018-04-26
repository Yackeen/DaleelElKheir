package yackeen.com.daleel.organization;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import yackeen.com.daleel.circleindicator.CircleIndicator;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.organization.model.OrganizationModel;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.FAV_ORGANIZATIONS;
import static yackeen.com.daleel.constants.Constants.ORGANIZATION;
import static yackeen.com.daleel.constants.Constants.UN_FOLLOWED_ORGANIZATION;


/**
 * Created by elmar7om on 01/02/2018.
 */

public class OrganizationsFragment extends Fragment {

    private static final String TAG = "OrganizationsFragment";
    ViewPager viewPager;
    private AdapterViewpager adapterViewpager;
    private RecyclerView recyclerView;
    private ProgressBar progress, progressFollowed;
    private PrefManager manager;
    private TextView followedOrgsText, noFollowedOrgTxt;
    private User user;
    private CircleIndicator indicator;
    private FetchData fetchData;
    String category = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizations, container, false);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        followedOrgsText = view.findViewById(R.id.followedOrgs);
        noFollowedOrgTxt = view.findViewById(R.id.noFollowedOrg);
        manager = new PrefManager(getActivity());
        user = manager.getUser();

        if (!manager.isLoggedIn()) {
            viewPager.setVisibility(View.GONE);
            followedOrgsText.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
        } else setViewpager(view);

        setScrollView(view);

        setRecyclerView(view);

        return view;
    }

    private void setViewpager(final View view) {
        progressFollowed = view.findViewById(R.id.progressFollowed);
        User user = manager.getUser();
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        params.put("UserID", user.getId());
        headers.put("SecurityToken", user.getToken());
        fetchData = new FetchData(getActivity(), TAG, progressFollowed, FAV_ORGANIZATIONS,
                Request.Method.POST, params, headers);
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
                            model.setGovernorate(object.getString("Governorate"));
                            model.setRegion(object.getString("Area"));
                            model.setLogo(object.getString("Logo"));

                            JSONArray categoryArr = object.getJSONArray("Categories");
                            for (int j = 0; j < categoryArr.length(); ++j) {
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

                            list.add(model);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (list.size() == 0) {
                    noFollowedOrgTxt.setVisibility(View.VISIBLE);
                } else {
                    noFollowedOrgTxt.setVisibility(View.GONE);
                }
                // Add View Pager
                adapterViewpager = new AdapterViewpager(getActivity(), list);
                viewPager.setAdapter(adapterViewpager);
                indicator.setViewPager(viewPager);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setQueryHint(getResources().getString(R.string.searchOrgs));
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

    private void search(String s) {
        Intent intent = new Intent(getActivity(), AllOrganization.class);
        intent.putExtra("URL", ORGANIZATION);
        intent.putExtra("searchText", s);
        startActivity(intent);
    }

    private void setRecyclerView(final View view) {
        progress = view.findViewById(R.id.progress);
        String url = "";

        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> params = new HashMap<>();
        if (manager.isLoggedIn()) {
            url = UN_FOLLOWED_ORGANIZATION;
            headers.put("SecurityToken", user.getToken());
            params.put("UserID", user.getId());
        } else {
            url = ORGANIZATION;
        }
        fetchData = new FetchData(getActivity(), TAG, progressFollowed, url,
                Request.Method.POST, params, headers);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.d(TAG, "onSuccess: " + jsonObject);
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
                            model.setGovernorate(object.getString("Governorate"));
                            model.setRegion(object.getString("Area"));
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
                            list.add(model);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Add to recycleView
                recyclerView = view.findViewById(R.id.recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(new OrganizationsAdapter(getActivity(), list));
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchData != null)
            fetchData.cancelRequest();
    }

    public void setScrollView(View view) {
        NestedScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, 0);
    }


}
