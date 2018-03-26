package yackeen.com.daleel.allcases;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.crash.FirebaseCrash;

import yackeen.com.daleel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllCasesFragment extends Fragment {


    public AllCasesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_cases, container, false);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        return view;
    }

}
