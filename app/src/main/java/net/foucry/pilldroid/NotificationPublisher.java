package net.foucry.pilldroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by jfoucry on 6/23/16.
 */
public class NotificationPublisher extends BroadcastReceiver {

    private static String TAG = Thread.currentThread().getStackTrace()[1].getMethodName();
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID,0);
        notificationManager.notify(id, notification);
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(400);

    }
}
