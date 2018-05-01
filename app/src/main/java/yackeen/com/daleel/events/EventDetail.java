package yackeen.com.daleel.events;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;

import java.text.SimpleDateFormat;
import java.util.Date;

import yackeen.com.daleel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetail extends Fragment {

    private static final String TAG = "EventDetail";
    TextView name, date, time, brief, organization, contact, location, eventLink;
    ProgressBar progress;
    ImageView image;
    ScrollView scrollView;
    String link;

    public EventDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        initView(view);

        name.setText(getArguments().getString("title"));
        brief.setText(getArguments().getString("desc"));
        eventLink.setText(getString(R.string.event_link) + getArguments().getString("link"));

        Log.d(TAG, "onCreateView: " + getArguments().getString("time"));
//        SimpleDateFormat yourDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        date.setText(DateFormat.format("dd-MMM", new Date(yourDateFormat.format(
//                new Date(getArguments().getString("startDate"))))));
        date.setText(getArguments().getString("startDate"));

        time.setText(DateFormat.format("h:mm a", new Date(getArguments().getString("startDate"))) + " - " + DateFormat.format("h:mm a", new Date(getArguments().getString("endDate"))));

        brief.setText(Html.fromHtml(getArguments().getString("desc")));
        contact.setText(getArguments().getString("mob"));
        organization.setText(getArguments().getString("org"));

//        byte[] decodedImg = Base64.decode(getArguments().getString("image"), Base64.DEFAULT);
        Glide.with(getActivity()).load(getArguments().getString("image")).into(image);
        location.setText(getArguments().getString("location"));

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to call " + contact.getText().toString() + " ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contact.getText().toString(), null)));
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing
                    }
                });
                AlertDialog d = builder.create();
                d.show();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to search on google map ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent googleMap = new Intent(Intent.ACTION_VIEW);
                                googleMap.setData(Uri.parse("geo:0,0?q=" + location.getText().toString()));
                                getActivity().startActivity(googleMap);
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing
                    }
                });
                AlertDialog d = builder.create();
                d.show();
            }
        });

        return view;
    }

    private void initView(View view) {
        name = view.findViewById(R.id.eventName);
        date = view.findViewById(R.id.eventDate);
        time = view.findViewById(R.id.eventTime);
        brief = view.findViewById(R.id.eventBrief);
        organization = view.findViewById(R.id.eventOrgName);
        contact = view.findViewById(R.id.eventContact);
        location = view.findViewById(R.id.eventLocation);
        progress = view.findViewById(R.id.progress);
        image = view.findViewById(R.id.eventImg);
        scrollView = view.findViewById(R.id.eventDetailMain);
        eventLink = view.findViewById(R.id.eventLink);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: ");
//        MenuItem itemFilter = menu.findItem(R.id.action_filter);
//        MenuItem itemSearch = menu.findItem(R.id.action_search);
//        itemFilter.setVisible(false);
//        itemSearch.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
