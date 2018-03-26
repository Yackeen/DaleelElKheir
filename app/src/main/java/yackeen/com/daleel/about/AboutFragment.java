package yackeen.com.daleel.about;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;

import static yackeen.com.daleel.constants.Constants.ABOUT;

/**
 * Created by elmar7om on 02/02/2018.
 */

public class AboutFragment extends Fragment {
    private static final String TAG = "AboutFragment";
    TextView bloodBankCount, organizationCount, casesCount, eventsCount, about, vision;
    RecyclerView recyclerView;
    ProgressBar progress;
    private FetchData fetchData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        MenuItem itemFilter = menu.findItem(R.id.action_filter);
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        itemFilter.setVisible(false);
        itemSearch.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fetchData != null)
            fetchData.cancelRequest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        initView(view);

        final HashMap<String, String> params = new HashMap<>();
        fetchData = new FetchData(getActivity(), TAG, progress, ABOUT, Request.Method.POST,
                params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");

                    if (isSuccess) {
                        List<AboutModel> list = new ArrayList<>();
                        JSONObject object = jsonObject.getJSONObject("Response");
                        bloodBankCount.setText(object.getString("BloodBankHelpCount")
                                + " " + getResources().getString(R.string.blood_banks));
                        organizationCount.setText(object.getString("OrganizationAcount")
                                + " " + getResources().getString(R.string.organizations));
                        casesCount.setText(object.getString("CasesAcount")
                                + " " + getResources().getString(R.string.casee));
                        eventsCount.setText(object.getString("EventAcount")
                                + " " + getResources().getString(R.string.events));
                        about.setText(Html.fromHtml(object.getString("Beif")));
                        vision.setText(Html.fromHtml(object.getString("Vision")));

                        JSONArray jsonArray = object.getJSONArray("SocailResponsibilities");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject partners = jsonArray.getJSONObject(i);

                            AboutModel model = new AboutModel();
                            model.setName(partners.getString("Name"));
                            model.setImage(partners.getString("Image"));
                            list.add(model);
                        }
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        recyclerView.setAdapter(new AboutAdapter(getActivity(), list));
                        recyclerView.setNestedScrollingEnabled(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return view;
    }

    private void initView(View view) {
        bloodBankCount = view.findViewById(R.id.bloodBanksHelp);
        organizationCount = view.findViewById(R.id.orgs);
        casesCount = view.findViewById(R.id.allCases);
        eventsCount = view.findViewById(R.id.events);
        about = view.findViewById(R.id.aboutDaleel);
        vision = view.findViewById(R.id.ourVision);
        progress = view.findViewById(R.id.progress);
        recyclerView = view.findViewById(R.id.partnerRecycle);
    }


}
