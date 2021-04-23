package net.foucry.pilldroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = DrugDetailFragment.class.getName();

    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Show the toast  like in above screen shot
        Log.d(TAG, "onReceive");

               Toast.makeText(context, "New stock calculted", Toast.LENGTH_LONG).show();
        createNotificationChannel(context);
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.getAllDrugs();

        List<Drug> drugs = dbHelper.getAllDrugs();

        Drug firstDrug = null;

        try {
            firstDrug = drugs.get(0);
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        if (firstDrug != null) {
            if (firstDrug.getTake() != 0) {
                if(firstDrug.getStock() < firstDrug.getAlertThreshold()) {
                    nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
                   /* Notification notif = new Notification(R.drawable.ic_pill_alarm, "Crazy About Android...", System.currentTimeMillis());
                    notif.setLatestEventInfo(context, from, message, contentIntent);
                    nm.notify(1, notif);*/

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "PillDroid")
                            .setSmallIcon(R.drawable.ic_pill_alarm)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.notification_text))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    int notificationId = 666;
                    notificationManager.notify(notificationId, builder.build());
                } else
                {
                    double dummy = (firstDrug.getStock() - firstDrug.getAlertThreshold());
                    Log.d(TAG, "no notification scheduled " + dummy);
                }
            }
        }

    }

    private void createNotificationChannel(Context context) {

        Log.d(TAG, "start create notification channel");
        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        String CHANNEL_ID = "PillDroid";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(R.color.led);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        try {
            notificationManager.createNotificationChannel(channel);
        } catch  (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            Log.e(TAG, e.toString());
            //At the level Exception Class handle the error in Exception Table
            // Exception Create That Error  Object and throw it
            //E.g: FileNotFoundException ,etc
            e.printStackTrace();
        }
    }

}
