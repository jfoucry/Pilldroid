package net.foucry.pilldroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Created by jfoucry on 6/23/16.
 * Publish notification
 */


public class NotificationPublisher extends BroadcastReceiver {

    private static final String TAG = NotificationPublisher.class.getName();

    public static String NOTIFICATION_ID = "notification_id";
    public static String KEY_MESSAGE = "key_message";
    public static String KEY_TITLE = "key_title";
    public static String KEY_EXPAND = "key_expand";
    public static String KEY_SOUND = "key_sound";
    public static String KEY_MULTIPLE = "key_multiple";
    public static String APP_NAME = "PillDroid";

    /**
     * onReceive notification
     * @param Context context
     * @param Intent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Receive notification");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        String message = intent.getStringExtra(KEY_MESSAGE);
        String title = intent.getStringExtra(KEY_TITLE);
        boolean isEnabledExpand = intent.getBooleanExtra(KEY_EXPAND, false);
        boolean isEnableSound = intent.getBooleanExtra(KEY_SOUND, false);
        boolean isEnabledMultiple = intent.getBooleanExtra(KEY_MULTIPLE, false);

        String channel_id = createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.pharmacy))
                .setChannelId(APP_NAME)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.pharmacy)))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (isEnableSound) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
        }


        notificationManager.notify(notificationId, builder.build());
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