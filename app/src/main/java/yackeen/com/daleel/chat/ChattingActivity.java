package yackeen.com.daleel.chat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yackeen.com.daleel.R;
import yackeen.com.daleel.chat.adapter.ChattingAdapter;
import yackeen.com.daleel.chat.model.ConversationModel;
import yackeen.com.daleel.chat.signalr.SignalRService;
import yackeen.com.daleel.connection.FetchData;
import yackeen.com.daleel.connection.VolleyCallBack;
import yackeen.com.daleel.manager.PrefManager;
import yackeen.com.daleel.user.User;

import static yackeen.com.daleel.constants.Constants.CHAT_MESSAGES;

public class ChattingActivity extends AppCompatActivity {
    private static final String TAG = "Fawzy.chattingActivity";
    PrefManager manager;
    User user;
    private Context mContext;
    private SignalRService mService;
    private boolean mBound = false;
    private EditText message;
    private ServiceConnection mConnection;
    private ImageView send;
    private int CaseID;
    private ProgressBar progress;
    private FetchData fetchData;
    private TextView noDataFound;
    private List<ConversationModel> messagesList;
    private RecyclerView recyclerView;
    private ChattingAdapter adapter;
    private MessagesBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        FirebaseCrash.log("Here comes the exception!");
        FirebaseCrash.report(new Exception("oops!"));

        CaseID = getIntent().getIntExtra("CaseID", 0);
        setConnection();
        startSignlar();
        mContext = this;
        setUser();
        setToolbar();
        setViews();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("CaseName"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setReceiver() {
        receiver = new MessagesBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(SignalRService.BROADCAST_ACTION));
    }

    private void setUser() {
        manager = new PrefManager(this);
        user = manager.getUser();
    }

    private void startSignlar() {
        Intent intent = new Intent(this, SignalRService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void setViews() {
        send = findViewById(R.id.send);
        message = findViewById(R.id.edit_message);
        progress = findViewById(R.id.progress);
        noDataFound = findViewById(R.id.noData);
        setRecycler();
        if (!manager.isLoggedIn()) {
            showNoData();
        } else getChatMessages();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void showNoData() {
        noDataFound.setVisibility(View.VISIBLE);
    }

    private void getChatMessages() {
        User user = manager.getUser();
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> headers = new HashMap<>();
        params.put("UserID", user.getId());
        params.put("CaseId", CaseID + "");
        headers.put("SecurityToken", user.getToken());
        fetchData = new FetchData(this, TAG, progress, CHAT_MESSAGES,
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
        List<ConversationModel> list = new ArrayList<>();
        for (int i = 0; i < chatThreadsList.length(); i++) {
            try {
                JSONObject object = chatThreadsList.getJSONObject(i);
                ConversationModel model = new ConversationModel();
                model.setIsAdmin(object.getBoolean("isAdmin"));
                model.setMessage(object.getString("Message"));
                model.setId(i);
                Log.e("fawzy.chatting", "pos= " + i + " ,message= " + model.getMessage());
                list.add(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        messagesList = new ArrayList<>(list);
        updateRecycler();
    }

    private void updateRecycler() {
        adapter.setList(messagesList);
    }

    public void setRecycler() {
        recyclerView = findViewById(R.id.recycler);
        messagesList = new ArrayList<>();
        adapter = new ChattingAdapter(this, messagesList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
//        layoutManager.setReverseLayout(true);
        recyclerView.scrollToPosition(messagesList.size());
        recyclerView.setAdapter(adapter);
        setRecyclerAnimator();
    }

    private void setRecyclerAnimator() {
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
    }

    private void sendMessage() {
        // if (mBound) {
        // Call a method from the SignalRService.
        // However, if this call were something that might hang, then this request should
        // occur in a separate thread to avoid slowing down the activity performance.

        if (message != null && message.getText().length() > 0) {
            String mesg = message.getText().toString();
            Log.e(TAG, "sendingMessage: " + mesg + " ,userId= " + user.getId());
//            newMsgAdded(mesg, false);
            if (mService != null)
                mService.sendMessage(mesg, user.getId());
            message.setText("");
        }
    }

    private void newMsgAdded(String mesg, boolean isAdmin) {
        ConversationModel model = new ConversationModel();
        model.setMessage(mesg);
        model.setIsAdmin(isAdmin);
        model.setId(messagesList.size());
        messagesList.add(0, model);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
//        scrollRecycler();
    }

    private void setConnection() {
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to SignalRService, cast the IBinder and get SignalRService instance
                SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                Log.d(TAG, "onServiceConnected: Connected Success");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
                Log.d(TAG, "onServiceDisconnected");
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        setReceiver();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MessagesBroadcastReceiver extends BroadcastReceiver {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            String get_message = intent.getStringExtra("message").toString();
            Log.e("fawzy.MessagesBroadcastReceiver", "onReceive massage= " + get_message + " ,isAdmin= " + intent.getBooleanExtra("isAdmin", false));
            newMsgAdded(get_message, intent.getBooleanExtra("isAdmin", false));
        }
    }
}
