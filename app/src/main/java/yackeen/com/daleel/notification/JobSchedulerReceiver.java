package yackeen.com.daleel.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import static android.os.Looper.getMainLooper;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Ibrahim on 20/02/2018.
 */

public class JobSchedulerReceiver extends BroadcastReceiver {

    private static final String TAG = "JobSchedulerReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {

        MyFirebaseMessagingService.start(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent mIntent = new Intent();
        mIntent.setAction("yackeen.com.daleel.notification.JobSchedulerReceiver");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);

        if (alarmManager == null) return;
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 10000, pendingIntent);
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, 10000, pendingIntent);
        }
    }
}
