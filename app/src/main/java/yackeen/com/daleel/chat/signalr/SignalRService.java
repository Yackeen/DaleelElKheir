package yackeen.com.daleel.chat.signalr;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import yackeen.com.daleel.manager.PrefManager;

/**
 * Created by Ibrahim on 28/02/2018.
 */

public class SignalRService extends Service {

    public static final String BROADCAST_ACTION = "yackeen.com.daleel.chat";
    private static final String TAG = "fawzy.SignalRService";
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients
    private final String serverUrl = "http://yakensolution.cloudapp.net/DaleelElkheirAdmin";
    private final String CLIENT_METHOD_BROADAST_MESSAGE = "appendNewMessage";
    private final String SERVER_METHOD_SEND = "Send";
    private final String SERVER_METHOD_CONNECT = "Connect";
    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private Object userId;
    private PrefManager prefManger;


    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        prefManger = new PrefManager(this);
        if (prefManger.getUser() != null)
            userId = prefManger.getUser().getId();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public void onDestroy() {
        mHubConnection.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        startSignalR();
        return mBinder;
    }

    /**
     * method for clients (activities)
     */
    public void sendMessage(String message, String userId, String CaseID) {
        Log.e(TAG, "sendMessage: " + userId + ",msg= " + message);
        mHubProxy.invoke(SERVER_METHOD_SEND, "", userId, CaseID, message);
    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("User-Name", "Ibrahim");
            }
        };

        /*/DaleelElkheirAdmin/ChatThread/ChatThreadMessageRoom*/
        mHubConnection = new HubConnection(serverUrl);
        mHubConnection.setCredentials(credentials);
        String SERVER_HUB_CHAT = "letschathub";
        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }

        mHubProxy.invoke(SERVER_METHOD_CONNECT, userId);
        mHubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE, new SubscriptionHandler3<String, Boolean, String>() {
            @Override
            public void run(String user, final Boolean isAdmin, final String msg) {
//                final String finalMsg = msg.toString();
                // display Toast message
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BROADCAST_ACTION);
                        intent.putExtra("message", msg);
                        intent.putExtra("isAdmin", isAdmin);
                        sendBroadcast(intent);
                    }
                });
            }
        }, String.class, Boolean.class, String.class);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

}
