package net.foucry.pilldroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Created by jfoucry on 6/23/16.
 * Publish notification
 */


public class NotificationPublisher extends BroadcastReceiver {

    private static String TAG = NotificationPublisher.class.getName();
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    /**
     * onReceive notification
     * @param Context context
     * @param Intent intent
     */
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Receive notification");

        String channel_id = createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.pharmacy))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.pharmacy)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    /**
     * createNotificationChannelid for android API >= 28
     * @param Context context
     * @return String channel_id
     */
    public static String createNotificationChannel(Context context) {

        Log.d(TAG, "start create notification channel");
        // The id of the channel.
        String channelId = "Channel_id";

        // The user-visible name of the channel.
        CharSequence channelName = context.getString(R.string.app_name);
        // The user-visible description of the channel.
        String channelDescription = "Pilldroid Alert";
        int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        //            int channelLockscreenVisibility = Notification.;

        // Initializes NotificationChannel.
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName,
                channelImportance);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableVibration(true);
        //            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

        // Adds NotificationChannel to system. Attempting to create an existing notification
        // channel with its original values performs no operation, so it's safe to perform the
        // below sequence.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);

        return channelId;
    }
}