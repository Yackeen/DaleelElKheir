package yackeen.com.daleel.events;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yackeen.com.daleel.R;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private static final String TAG = "EventsAdapter";
    private Context context;
    private List<EventsModel> list = new ArrayList<>();

    public EventsAdapter(Context context, List<EventsModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final EventsModel model = list.get(position);
        String time = (String) DateFormat.format("h:mm a", new Date(model.getTime()));
        holder.time.setText(time);
        holder.title.setText(model.getTitle());
        holder.location.setText(model.getLocation());


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", model.getId());
                bundle.putString("desc", model.getDescription());
                bundle.putString("duration", model.getDuration());
                bundle.putString("title", model.getTitle());
                bundle.putString("time", model.getTime());
                bundle.putString("location", model.getLocation());
                bundle.putString("image", model.getImage());
                bundle.putString("mob", model.getMobile());
                bundle.putString("startDate", model.getStartDate());
                bundle.putString("endDate", model.getEndDate());
                bundle.putString("org", model.getOrganization());
                bundle.putString("link", model.getLink());
                //set Fragmentclass Arguments
                EventDetail fragment = new EventDetail();
                fragment.setArguments(bundle);
                showFragment(fragment);

            }
        });

    }

    public void showFragment(Fragment fragment) {

        android.app.FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
        // Using android framework animation to navigate between fragment
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // Replace the new fragment
        ft.replace(R.id.content, fragment, "detailFragment").addToBackStack("this");
        // Start the animated transition.
        ft.commit();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, time, location, duration;
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.eventTitle);
            time = itemView.findViewById(R.id.eventTime);
            location = itemView.findViewById(R.id.eventLocation);
            duration = itemView.findViewById(R.id.eventDuration);
            layout = itemView.findViewById(R.id.eventLayout);
        }
    }
}
