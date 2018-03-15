package yackeen.com.daleel.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.chat.model.ConversationModel;

/**
 * Created by Ibrahim on 21/02/2018.
 */

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {


    private Context context;
    private List<ConversationModel> list = new ArrayList<>();

    public ChattingAdapter(Context context, List<ConversationModel> list) {
        this.context = context;
        this.list = list;
        setHasStableIds(true);
    }

    public void setList(List<ConversationModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateList(List<ConversationModel> list) {
        this.list = list;
    }

    public void addMsg(ConversationModel msg) {
        msg.setId(list.size());
        list.add(0, msg);
//        notifyItemChanged(1);
//        notifyItemChanged(0);
        notifyItemInserted(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        if (pos == 1)
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_admin, parent, false));
        else
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isAdmin())
            return 1;
        else return 2;
    }

    @Override
    public long getItemId(int position) {
        //Return the stable ID for the item at position
        Log.e("fawzy", "in pos= " + position + " ,chatting id= " + list.get(position).getId());
        return list.get(position).getId();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView msg, date;
        private LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.msg);
            date = itemView.findViewById(R.id.date);
            layout = itemView.findViewById(R.id.layout);
        }

        public void bind(int position) {
//            if (list.get(position).isAdmin()) {
//                layout.setGravity(Gravity.END);
//                msg.setBackgroundDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_admin_msg_bg));
//                msg.setTextColor(Color.WHITE);
//            } else if (!list.get(position).isAdmin()) {
//                layout.setGravity(Gravity.START);
//                msg.setBackgroundDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_msg_bg));
//                msg.setTextColor(Color.BLACK);
//            }
            msg.setText(list.get(position).getMessage());
        }
    }
}
