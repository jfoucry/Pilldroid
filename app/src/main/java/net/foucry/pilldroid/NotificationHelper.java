package net.foucry.pilldroid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

/**
 * Created by jfoucry on 6/23/16.
 * Publish notification
 */


class NotificationHelper extends ContextWrapper {

    private static final String TAG = NotificationHelper.class.getName();
    private NotificationManager notificationManager;
    public final String CHANNEL_ID ="net.foucry.pilldroid";
    public final String CHANNEL_ID_NAME = getString(R.string.app_name);

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    private void createChannels() {

        String description = getString(R.string.channel_description);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        channel.setLightColor(Color.RED);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(channel);
    }
    public Notification.Builder getNotification(String title, String body) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_pill)
                .setAutoCancel(true);
    }

    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if(notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}