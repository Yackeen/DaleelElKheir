package yackeen.com.daleel.events;


import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;

import static yackeen.com.daleel.constants.Constants.ALL_EVENTS;
import static yackeen.com.daleel.constants.Constants.EVENTS_DATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {


    private static final String TAG = "Fawzy.EventsFragment";
    List<EventsModel> data;
    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private ProgressBar progress;
    private TextView noEvents;
    private View view;
    private FetchData fetchData;
    private Calendar calendar;

    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.e(TAG, "onCreateOptionsMenu: ");
//        MenuItem itemFilter = menu.findItem(R.id.action_filter);
//        MenuItem itemSearch = menu.findItem(R.id.action_search);
//        itemFilter.setVisible(false);
//        itemSearch.setVisible(false);
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setQueryHint(getResources().getString(R.string.searchEvents));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e(TAG, "onQueryTextSubmit: " + s);
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

        Intent intent = new Intent(getActivity(), AllEventsActivity.class);
        intent.putExtra("URL", ALL_EVENTS);
        intent.putExtra("searchText", s);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_events, container, false);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        calendarView = view.findViewById(R.id.eventsPicker);
        recyclerView = view.findViewById(R.id.recycler);
        progress = view.findViewById(R.id.progress);
        noEvents = view.findViewById(R.id.noEvents);
        recyclerView = view.findViewById(R.id.recycler);
        data = new ArrayList<>();

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendarView.setDateSelected(calendar.getTime(), true);
//        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                Log.e("fawzy selected Date", "Year=" + date.getYear() + " Month=" + (date.getMonth() + 1) + " day=" + date.getDay());
                String dateStr = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
                getEvents(dateStr);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getEvents(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH + 1) + "/" +
//                calendar.get(Calendar.YEAR));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchData != null)
            fetchData.cancelRequest();
    }

    private void getEvents(String date) {
        HashMap<String, String> params = new HashMap<>();
        params.put("EventDate", date);
        fetchData = new FetchData(getActivity(), TAG, progress, EVENTS_DATE,
                Request.Method.POST, params, null);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");

                    Log.e(TAG, "onSuccess: " + jsonObject);
                    if (isSuccess) {
                        data.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            EventsModel model = new EventsModel();
                            model.setId(object.getString("ID"));
                            model.setTitle(object.getString("Title"));
                            model.setLocation(object.getString("Governorate") + " " +
                                    object.getString("Region"));
                            model.setOrganization(object.getString("Organization"));
                            model.setDescription(object.getString("Description"));
                            model.setImage(object.getString("Image"));
                            model.setStartDate(object.getString("StartDate"));
                            model.setEndDate(object.getString("EndDate"));
                            model.setMobile(object.getString("Mobile"));
                            model.setTime(object.getString("StartDate"));
                            model.setLink(object.getString("Link"));
                            data.add(model);
                        }
                        if (data.size() == 0)
                            noEvents.setVisibility(View.VISIBLE);

                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(new EventsAdapter(getActivity(), data));
                    }

                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                }
            }
        });
    }

}
