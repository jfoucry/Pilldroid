package net.foucry.pilldroid;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;
import java.util.List;

import static net.foucry.pilldroid.NotificationPublisher.NOTIFICATION_ID;


/**
 * Created by jacques on 17/09/16.
 */

public class PillDroidJobService extends JobService {
    private  static final String TAG = JobService.class.getName();
    private boolean jobCancelled = false;

    private String CHANNEL_ID = null;
    private DBHelper dbHelper = new DBHelper(this);

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true;
    }

    /**
     * Grab sorted list of medicaments
     * test dateAlert of the first of the list
     * if dateAlert < now
     *  schedule notification
     * @param JobParameters params
     */
    private void doBackgroundWork(final JobParameters params) {

        if (jobCancelled) {
            return;
        }
        List<Medicament> medicaments = dbHelper.getAllDrugs();

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        long dateSchedule;

        Medicament firstMedicament = null;

        try {
            firstMedicament = medicaments.get(0);
        }
        catch (Exception ignored){}

        if (firstMedicament != null) {
            Date dateAlert = UtilDate.removeDaysToDate(firstMedicament.getAlertThreshold(), firstMedicament.getDateEndOfStock());

            if (dateAlert.getTime() < now.getTime()) {
                dateSchedule = now.getTime() + 120000; // If dateAlert < now we schedule an alert for now + 120 seconds
            } else {
                dateSchedule = dateAlert.getTime(); // If dateAlert > now we use dateAlert as scheduleDate
            }

            long delay = dateSchedule - now.getTime();
            createNotificationChannel();
            scheduleNotification(getApplicationContext(), delay);
        }

        Log.d(TAG, "Job finished");
        jobFinished(params, false);
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    /**
     * Schedule Notification for the delay
     * @param Context context
     * @param long delay - date for the notification in milliseconds
     * @param context
     */
    private void scheduleNotification(Context context, long delay) {
        Log.d(TAG, "scheduleNotification delay == " + delay);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.pharmacy))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = 666;
        notificationManager.notify(notificationId, builder.build());

    }

    /**
     * createNotificationChannelid for android API >= 28
     * @param Context context
     * @return String channel_id
     */
    private void createNotificationChannel() {

        Log.d(TAG, "start create notification channel");
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }
}