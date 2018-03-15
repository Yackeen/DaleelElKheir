package yackeen.com.daleel.user.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;
import yackeen.com.daleel.user.adapters.FavouriteCategoriesAdapter;
import yackeen.com.daleel.user.models.FavouriteCategoriesModel;

import static yackeen.com.daleel.constants.Constants.FAV_CATEGORIES;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteCategories extends Fragment {


    private static final String TAG = "FavouriteCategories";
    ProgressBar progress;
    RecyclerView recyclerView;
    PrefManager manager;
    View tab;
    private FetchData fetchData;

    public FavouriteCategories() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite_categories, container, false);
        progress = view.findViewById(R.id.progress);
        recyclerView = view.findViewById(R.id.recycler);
        tab = view.findViewById(R.id.tab);
        manager = new PrefManager(getActivity());
        User user = manager.getUser();

        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        params.put("UserID", user.getId());
        headers.put("SecurityToken", user.getToken());
        fetchData = new FetchData(getActivity(), TAG, progress, FAV_CATEGORIES,
                Request.Method.POST, params, headers);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                Log.d(TAG, "onSuccess: " + jsonObject);
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        List<FavouriteCategoriesModel> list = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            FavouriteCategoriesModel model = new FavouriteCategoriesModel();
                            JSONObject object = jsonArray.getJSONObject(i);
                            model.setName(object.getString("Name"));
                            model.setId(object.getString("ID"));
                            list.add(model);
                        }
                        if (list.size() <= 0)
                            tab.setVisibility(View.GONE);
                        else {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(new FavouriteCategoriesAdapter(getActivity(), list));
                            recyclerView.setNestedScrollingEnabled(true);
                            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                                    DividerItemDecoration.VERTICAL));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fetchData != null)
            fetchData.cancelRequest();
    }
}
