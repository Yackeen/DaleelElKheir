package yackeen.com.daleel.home;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.allcases.DetailActivity;
import yackeen.com.daleel.allcases.model.CaseModel;


/**
 * Created by fawzy on 1/2/18.
 */
public class AdapterViewpager extends PagerAdapter implements View.OnClickListener {
    private static final String TAG = "AdapterViewpager";
    private Context context;
    private List<CaseModel> list = new ArrayList<>();
    private int index;

    public AdapterViewpager(Context context, List<CaseModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size() > 5 ? 5 : list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_newsfeed_pager, container, false);

        ImageView thumbnail;
        TextView requiredAmount, caseName, more, percentage;
        ProgressBar progress;
//        if (view == null){
        requiredAmount = view.findViewById(R.id.requiredAmount);
        caseName = view.findViewById(R.id.caseName);
        more = view.findViewById(R.id.caseDetail);
        percentage = view.findViewById(R.id.casePercentage);
        progress = view.findViewById(R.id.progressBar);
        thumbnail = view.findViewById(R.id.thumbnail);
        RelativeLayout parent = view.findViewById(R.id.layout);
//        }
        final CaseModel model = list.get(position);
        caseName.setText(model.getName());
        requiredAmount.setText(model.getRequiredAmount());
        double val = Double.valueOf(model.getCurrentAmount()) / Double.valueOf(model.getRequiredAmount()) * 100.0;
        percentage.setText(String.valueOf(val));
        progress.setProgress((int) val);
//        byte[] decodedImg = Base64.decode(model.getImage(), Base64.DEFAULT);
        Glide.with(context).load(model.getImage()).into(thumbnail);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("caseId", model.getId());
                intent.putExtra("caseName", model.getName());
                intent.putExtra("date", model.getDueDate());
                intent.putExtra("department", model.getCategory());
                intent.putExtra("organization", model.getOrganization());
                intent.putExtra("place", model.getCity());
                intent.putExtra("image", model.getImage());
                intent.putExtra("requiredAmount", model.getRequiredAmount());
                intent.putExtra("currentAmount", model.getCurrentAmount());
                intent.putExtra("description", model.getDescription());
                intent.putExtra("sharedLink", model.getSharedLink());
                context.startActivity(intent);
            }
        });
        notifyDataSetChanged();
        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public void onClick(View v) {

    }
}
