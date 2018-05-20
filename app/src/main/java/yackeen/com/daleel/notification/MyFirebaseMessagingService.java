package yackeen.com.daleel.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

import yackeen.com.daleel.home.MainActivity;
import yackeen.com.daleel.manager.PrefManager;

import static yackeen.com.daleel.constants.Constants.NOTIFICATION_ENABLED;
import static yackeen.com.daleel.notification.Config.CASE_NOTIFICATION;
import static yackeen.com.daleel.notification.Config.EVENT_NOTIFICATION;

/**
 * Created by Ibrahim on 19/02/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";

    private NotificationUtils notificationUtils;
    private PrefManager manager;


    public static void start(Context context) {
        Intent startServiceIntent = new Intent(context, MyFirebaseInstanceIdService.class);
        context.startService(startServiceIntent);

        Intent notificationServiceIntent = new Intent(context, MyFirebaseMessagingService.class);
        context.startService(notificationServiceIntent);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // {id=0, body=New Event Added, icon=appicon, type=22, badge=0, title=New Event}
        if (remoteMessage == null)
            return;

        Log.d(TAG, "onMessageReceived: " + remoteMessage + "\n" + remoteMessage.getData().get("type"));
        manager = new PrefManager(getApplicationContext());

        String notificationType = remoteMessage.getData().get("type");
        switch (notificationType) {
            case EVENT_NOTIFICATION:
                manager.setNotificationType(EVENT_NOTIFICATION);
                break;
            case CASE_NOTIFICATION:
                manager.setNotificationType(CASE_NOTIFICATION);
                break;
            default:
                manager.setNotificationType(null);
                break;
        }


        if (manager.getNotificationStatue() == NOTIFICATION_ENABLED)
            handleDataMessage(remoteMessage);
    }

    private void handleDataMessage(RemoteMessage data) {
        Log.e(TAG, "push json: " + data.toString());

        try {

            String title = data.getData().get("title");
            String message = data.getData().get("body");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
                showNotificationMessage(getApplicationContext(), title, message, /*timestamp*/ new Date().toString(),
                        resultIntent);
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(/*imageUrl*/ " ")) {
                    showNotificationMessage(getApplicationContext(), title, message, /*timestamp*/ new Date().toString(), resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, new Date().toString(), resultIntent, " "/*imageUrl*/);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
