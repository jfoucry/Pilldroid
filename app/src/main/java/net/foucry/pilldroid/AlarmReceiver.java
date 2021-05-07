package net.foucry.pilldroid;

import android.app.AlarmManager;
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

import java.time.LocalTime;
import java.util.Date;
import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = DrugDetailFragment.class.getName();

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Show the toast  like in above screen shot
        Log.d(TAG, "onReceive");

        // If BOOT_COMPLETED is received we launch an alarm in 10 second in order to
        // start the alarmschedule process.
        final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
        if(intent.getAction().equals(BOOT_COMPLETED_ACTION)){

            /*Intent myIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,  0, myIntent, 0);

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 10);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);*/
            scheduleAlarm(context);
        }
        
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
                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                Log.e(TAG, e.toString());
                //At the level Exception Class handle the error in Exception Table
                // Exception Create That Error  Object and throw it
                //E.g: FileNotFoundException ,etc
                e.printStackTrace();
            }
        }
    }
    public static void scheduleAlarm(Context context) {
        android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
        Date today;
        Date tomorrow;

        if (BuildConfig.DEBUG) {
            calendar.add(android.icu.util.Calendar.HOUR, 1);
            today = calendar.getTime();
            calendar.add(android.icu.util.Calendar.HOUR, 1);
        } else {
            calendar.set(android.icu.util.Calendar.HOUR_OF_DAY, 12);
            today = calendar.getTime();
            calendar.add(android.icu.util.Calendar.DAY_OF_YEAR, 1);
        }
        tomorrow = calendar.getTime();
        LocalTime todayNow = LocalTime.now();

        if (todayNow.isBefore(LocalTime.NOON)) {
            // schedule date = today
            //calendar.setTimeInMillis(dateAtNoon(today).getTime());
            calendar.setTimeInMillis(today.getTime());
        } else {
            // schedule date = tomorrow
            calendar.setTimeInMillis(tomorrow.getTime());
        }

        PendingIntent alarmIntent;

        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp) {
            Log.d(TAG, "Alarm already active");
        }

        /*alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);*/
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

        Log.d(TAG, "Alarm scheduled for " + UtilDate.convertDate(calendar.getTimeInMillis()));

        Toast.makeText(context, "Alarm scheduled for " + UtilDate.convertDate(calendar.getTimeInMillis()), Toast.LENGTH_SHORT).show();
    }
}