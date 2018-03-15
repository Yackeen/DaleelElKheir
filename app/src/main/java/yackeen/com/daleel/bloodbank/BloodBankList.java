package yackeen.com.daleel.bloodbank;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BloodBankList extends Fragment {

    private static final String TAG = "BloodBankList";
    private RecyclerView recyclerView;
    private ProgressBar progress;

    public BloodBankList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_blood_bank_list, container, false);
        progress = view.findViewById(R.id.progress);

        String URL = getArguments().getString("url");

        HashMap<String, String> params = new HashMap<>();
        if (getArguments().getString("location") != null)
            params.put("RegionID", getArguments().getString("location"));
        FetchData fetchData = new FetchData(getActivity(), TAG, progress,
                URL /*BLOOD_BANKS*/, Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        List<BloodBankModel> data = new ArrayList<>();

                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            BloodBankModel model = new BloodBankModel();
                            model.setId(object.getString("ID"));
                            model.setName(object.getString("Name"));
                            model.setCity(object.getString("City"));
                            model.setTitle(object.getString("Title"));
                            model.setGovernorate(object.getString("Governorate"));
                            if (getArguments().getString("searchedText") != null)
                                if (!object.getString("Name").toLowerCase()
                                        .contains(getArguments().getString("searchedText")))
                                    continue;

                            data.add(model);
                        }
                        recyclerView = view.findViewById(R.id.recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(new BloodBankAdapter(getActivity(), data));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

}
