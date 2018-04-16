package yackeen.com.daleel.chat;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.chat.adapter.ChatListAdapter;
import yackeen.com.daleel.chat.model.ChatThreadModel;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.CHAT_THREADS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment implements onThreadTapped {
    private final String TAG = "Fawzy.ChatListFragment";
    private ProgressBar progress;
    private PrefManager manager;
    private User user;
    private FetchData fetchData;
    private TextView noDataFound;
    private List<ChatThreadModel> chatThreads;
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        progress = view.findViewById(R.id.progress);
        noDataFound = view.findViewById(R.id.noData);
        setRecycler(view);
        if (!manager.isLoggedIn()) {
            showNoData();
        } else getChatThreads(view);
        return view;
    }

    private void showNoData() {
        noDataFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        // Unbind from the service
        super.onStop();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new PrefManager(getActivity());
        user = manager.getUser();
    }

    private void getChatThreads(final View view) {
        User user = manager.getUser();
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        params.put("UserID", user.getId());
        headers.put("SecurityToken", user.getToken());
        fetchData = new FetchData(getActivity(), TAG, progress, CHAT_THREADS,
                Request.Method.POST, params, headers);
        fetchData.getData(new VolleyCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    boolean isSuccess = jsonObject.getBoolean("IsSuccess");
                    if (isSuccess) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Response");
                        setChatThreadsList(jsonArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void setChatThreadsList(JSONArray chatThreadsList) {
        List<ChatThreadModel> list = new ArrayList<>();
        for (int i = 0; i < chatThreadsList.length(); i++) {
            try {
                JSONObject object = chatThreadsList.getJSONObject(i);
                ChatThreadModel model = new ChatThreadModel();
                model.setID(object.getInt("ID"));
                model.setCaseName(object.getString("Name"));
                model.setUserID(object.getInt("UserID"));
                model.setImage(object.getString("Image"));
                list.add(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        chatThreads = new ArrayList<>(list);
        updateRecycler();
    }

    private void updateRecycler() {
        adapter.setList(chatThreads);
    }

    public void setRecycler(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        chatThreads = new ArrayList<>();
        adapter = new ChatListAdapter(getActivity(), chatThreads, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchData != null)
            fetchData.cancelRequest();
    }

    @Override
    public void OnThreadTapped(int position) {
        Intent intent = new Intent(getActivity(), ChattingActivity.class);
        intent.putExtra("CaseID", chatThreads.get(position).getID());
        intent.putExtra("CaseName", chatThreads.get(position).getCaseName());
        startActivity(intent);
    }
}
