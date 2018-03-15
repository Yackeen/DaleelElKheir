package yackeen.com.daleel.bloodbank;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import static yackeen.com.daleel.constants.Constants.BANK_DETAIL;

/**
 * A simple {@link Fragment} subclass.
 */
public class BloodBankDetail extends Fragment {

    private static final String TAG = "BloodBankDetail";
    TextView name, region, title, description;
    RecyclerView recyclerView;
    ProgressBar progress;

    public BloodBankDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_boold_bank_detail, container, false);

        name = view.findViewById(R.id.bankName);
        region = view.findViewById(R.id.region);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.bankDescription);
        recyclerView = view.findViewById(R.id.recycler);
        progress = view.findViewById(R.id.progress);

        HashMap<String, String> params = new HashMap<>();
        params.put("ID", getArguments().getString("id"));
        FetchData fetchData = new FetchData(getActivity(), TAG, progress, BANK_DETAIL,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");

                    if (isSuccess) {
                        List<ContactsModel> data = new ArrayList<>();
                        JSONObject object = jsonObject.getJSONObject("Response");
                        name.setText(object.getString("Name"));
                        title.setText(object.getString("Title"));
                        region.setText(object.getString("City"));
                        description.setText(object.getString("Description"));

                        JSONArray jsonArray = object.getJSONArray("BloodBankContacts");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject contacts = jsonArray.getJSONObject(i);
                            ContactsModel model = new ContactsModel();
                            model.setName(contacts.getString("ContactName"));
                            model.setPhone(contacts.getString("ContactNumber"));
                            data.add(model);
                        }
                        recyclerView = view.findViewById(R.id.recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(new ContactsAdapter(getActivity(), data));
                        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

}
