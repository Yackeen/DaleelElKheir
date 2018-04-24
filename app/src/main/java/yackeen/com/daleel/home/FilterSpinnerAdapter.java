package yackeen.com.daleel.home;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.home.listener.ChosenId;
import yackeen.com.daleel.home.model.SpinnerAdapterModel;

/**
 * Created by Ibrahim on 08/02/2018.
 */

public class FilterSpinnerAdapter {

    private static final String TAG = "FilterSpinnerAdapter";
    private Context context;
    private String defaultHint;
    private List<SpinnerAdapterModel> list = new ArrayList<>();
    private SpinnerAdapterModel model;


    public FilterSpinnerAdapter(Context context, String defaultHint, List<SpinnerAdapterModel> list) {
        this.context = context;
        this.defaultHint = defaultHint;
        this.list = list;
    }

    public void setAdapter(final Spinner spinner, final ChosenId chosenId) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    ((TextView) v.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                    ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(context.getResources().getColor(R.color.colorAccent));
                    ((TextView) v.findViewById(android.R.id.text1)).setHighlightColor(Color.BLACK);

                }
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(Color.BLACK);
                ((TextView) v.findViewById(android.R.id.text1)).setHighlightColor(Color.BLACK);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(Color.BLACK);
                ((TextView) v.findViewById(android.R.id.text1)).setHighlightColor(Color.BLACK);

                return v;
            }

            @Override
            public int getCount() {
                return list.size(); // you dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < list.size(); i++) {
            adapter.add(list.get(i).getName());
        }
        adapter.add(defaultHint);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String catId = "", orgId = "", locationId = "", placeID = "";
                if (!spinner.getSelectedItem().equals(defaultHint)) {
                    if (spinner.getId() == R.id.catSpinner)
                        catId = list.get(i).getId();
                    else if (spinner.getId() == R.id.orgSpinner)
                        orgId = list.get(i).getId();
                    else if (spinner.getId() == R.id.locationSpinner)
                        locationId = list.get(i).getId();
                    else if (spinner.getId() == R.id.locationPlaceSpinner)
                        placeID = list.get(i).getId();
                }
                chosenId.theChosenId(catId, orgId, locationId, placeID, spinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
