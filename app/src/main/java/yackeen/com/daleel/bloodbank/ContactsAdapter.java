package yackeen.com.daleel.bloodbank;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;

/**
 * Created by Ibrahim on 07/02/2018.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    private Context context;
    private List<ContactsModel> list = new ArrayList<>();

    public ContactsAdapter(Context context, List<ContactsModel> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ContactsModel model = list.get(position);

        holder.name.setText(model.getName());
        holder.phone.setText(model.getPhone());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.getPhone()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone;
        ImageView call;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.contactName);
            phone = itemView.findViewById(R.id.contactNumber);
            call = itemView.findViewById(R.id.callContact);
        }
    }
}
