package yackeen.com.daleel.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.chat.model.ChatThreadModel;
import yackeen.com.daleel.chat.onThreadTapped;
import yackeen.com.daleel.circleimage.CircleImageView;

/**
 * Created by Ibrahim on 21/02/2018.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private final onThreadTapped onThreadTapped;
    private List<ChatThreadModel> list = new ArrayList<>();
    private Context context;

    public ChatListAdapter(Context context, List<ChatThreadModel> list, onThreadTapped onThreadTapped) {
        this.context = context;
        this.list = list;
        this.onThreadTapped = onThreadTapped;
    }

    public void setList(List<ChatThreadModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_thread, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView title, message, date, unRead;
        private LinearLayout containerLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.chatTitle);
//            message = itemView.findViewById(R.id.chatMsg);
//            date = itemView.findViewById(R.id.chatDate);
//            unRead = itemView.findViewById(R.id.chatCount);
            containerLayout = itemView.findViewById(R.id.layout);
        }

        public void bind(final int position) {
//            Log.e("fawzy.ChatList", "image at " + position + "= " + list.get(position).getImage());
            Glide.with(context).load(list.get(position).getImage()).into(image);
            title.setText(list.get(position).getCaseName());
//            message.setText(list.get(position).getMessage());
//            date.setText("yesterday");
//            unRead.setText("2");
            containerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onThreadTapped.OnThreadTapped(position);
                }
            });
        }
    }
}
