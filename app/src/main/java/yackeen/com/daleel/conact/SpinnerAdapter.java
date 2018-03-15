package yackeen.com.daleel.conact;

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

import yackeen.com.daleel.R;
import yackeen.com.daleel.home.model.SpinnerAdapterModel;
import yackeen.com.daleel.user.ChosenCat;

/**
 * Created by Ibrahim on 14/02/2018.
 */

public class SpinnerAdapter {

    private static final String TAG = "FilterSpinnerAdapter";
    private Context context;
    private String defaultHint;
    private SpinnerAdapterModel model;


    public SpinnerAdapter(Context context, String defaultHint) {
        this.context = context;
        this.defaultHint = defaultHint;

    }

    public void setAdapter(final Spinner spinner, final ChosenCat chosenCat) {
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
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.add(context.getResources().getString(R.string.inquiry));
        adapter.add(context.getResources().getString(R.string.volunteer));
        adapter.add(defaultHint);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!spinner.getSelectedItem().equals(defaultHint))
                    chosenCat.chosenCategory(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
