package yackeen.com.daleel.bloodbank;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;

/**
 * Created by Abdelrhman Walid on 4/20/2017.
 */

class BloodBankAdapter extends RecyclerView.Adapter<BloodBankAdapter.ViewHolder> {


    private Context context;
    private List<BloodBankModel> list = new ArrayList<>();

    public BloodBankAdapter(Context context, List<BloodBankModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_bank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);

        final BloodBankModel model = list.get(position);

        holder.orgName.setText(model.getName());
        holder.region.setText(model.getCity());
        holder.title.setText(model.getTitle());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", model.getId());
                //set Fragmentclass Arguments
                BloodBankDetail fragment = new BloodBankDetail();
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

        TextView orgName, region, title, more;
        CardView layout;

        ViewHolder(View view) {
            super(view);

            orgName = view.findViewById(R.id.orgName);
            region = view.findViewById(R.id.region);
            title = view.findViewById(R.id.title);
            more = view.findViewById(R.id.more);
            layout = view.findViewById(R.id.bankLayout);
        }

        void bind(int position) {

        }

    }
}
