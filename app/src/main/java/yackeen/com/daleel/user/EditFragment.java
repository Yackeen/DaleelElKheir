package yackeen.com.daleel.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import yackeen.com.daleel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private static final String TAG = "EditFragment";
    RecyclerView recyclerView;
    private ProgressBar progress, progressCat;

    public EditFragment() {
        // Required empty public constructor
    }

    //jjj
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        progress = view.findViewById(R.id.progress);
        progressCat = view.findViewById(R.id.progressCat);
        recyclerView = view.findViewById(R.id.recycler);


        return view;
    }

}
