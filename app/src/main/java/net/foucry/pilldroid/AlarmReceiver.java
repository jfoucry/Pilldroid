package net.foucry.pilldroid;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.zxing.client.android.BuildConfig;

import net.foucry.pilldroid.dao.PrescriptionsDAO;
import net.foucry.pilldroid.databases.PrescriptionDatabase;
import net.foucry.pilldroid.models.Prescription;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmManager.class.getName();

    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Show the toast  like in above screen shot
        Log.d(TAG, "onReceive");

        // If BOOT_COMPLETED is received we launch an alarm in 10 second in order to
        // start the alarmschedule process.

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "StartUpBootReceiver BOOT_COMPLETED");
            scheduleAlarm(context);
        }

        if (BuildConfig.DEBUG) { Toast.makeText(context, "New stock calculated", Toast.LENGTH_LONG).show(); }
        createNotificationChannel(context);
        PrescriptionDatabase prescriptions = PrescriptionDatabase.getInstanceDatabase(context.getApplicationContext());
        PrescriptionsDAO prescriptionsDAO = prescriptions.getPrescriptionsDAO();
        List<Prescription> prescriptionList = prescriptionsDAO.getAllMedics();
        Prescription firstPrescription = null;
        Prescription currentPrescription;

        for (int i=0 ; i < prescriptionList.size(); i++ ) {
            currentPrescription = prescriptionList.get(i);
            currentPrescription.newStock();
            prescriptionsDAO.update(currentPrescription);
        }
        // Sorting list by dateEndOfStock
        prescriptionList = prescriptionsDAO.getAllMedics();     // Reread the database
        Utils.sortPrescriptionList(prescriptionList);

        try {
            firstPrescription = prescriptionList.get(0);
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        if (firstPrescription != null) {
            if (firstPrescription.getTake() != 0) {
                if(firstPrescription.getStock() <= firstPrescription.getAlertThreshold()) {
                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(context, DrugListActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "PillDroid")
                            .setSmallIcon(R.drawable.ic_pill_alarm)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.notification_text))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setColorized(true)
                            .setAutoCancel(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    int notificationId = 666;
                    notificationManager.notify(notificationId, builder.build());
                } else
                {
                    double dummy = (firstPrescription.getStock() - firstPrescription.getAlertThreshold());
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
        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            Log.e(TAG, e.toString());
            //At the level Exception Class handle the error in Exception Table
            // Exception Create That Error  Object and throw it
            //E.g: FileNotFoundException ,etc
            e.printStackTrace();
        }
    }
    public static void scheduleAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        Date today;
        Date tomorrow;
        LocalTime todayNow = LocalTime.now();

        if (BuildConfig.DEBUG) {
            calendar.add(Calendar.MINUTE, 2);
            Date nextSchedule = calendar.getTime();
            calendar.setTimeInMillis(nextSchedule.getTime());
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 11);
            today = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            tomorrow = calendar.getTime();
            if (todayNow.isBefore(LocalTime.NOON)) {
                calendar.setTimeInMillis(today.getTime());
            } else {
                calendar.setTimeInMillis(tomorrow.getTime());
            }
        }

        PendingIntent alarmIntent;

        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,(calendar.getTimeInMillis()),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        Log.d(TAG, "Alarm scheduled for " + UtilDate.convertDate(calendar.getTimeInMillis()));

        if (BuildConfig.DEBUG) { Toast.makeText(context, "Alarm scheduled for " + UtilDate.convertDate(calendar.getTimeInMillis()), Toast.LENGTH_SHORT).show(); }
    }

    public static Boolean isAlarmScheduled(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        return alarmManager.getNextAlarmClock() != null;
    }
}
