package net.foucry.pilldroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by jfoucry on 6/23/16.
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID,0);
        notificationManager.notify(id, notification);
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(400);

    }
}
