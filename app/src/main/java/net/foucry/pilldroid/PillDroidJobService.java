package net.foucry.pilldroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;
import java.util.List;


/**
 * Created by jacques on 17/09/16.
 */

public class PillDroidJobService extends JobService {
    private  static final String TAG = JobService.class.getName();
    private boolean jobCancelled = false;
    private String CHANNEL_ID = getString(R.string.app_name);
    private DBHelper dbHelper = new DBHelper(this);


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        createNotificationChannel();
        doBackgroundWork(params);

        return true;
    }

    /**
     * Grab sorted list of medicaments
     * test dateAlert of the first of the list
     * if dateAlert < now
     *  schedule notification
     * @param params JobParameters
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
            scheduleNotification(delay);
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
     * @param delay long - date for the notification in millisecond
     */
    private void scheduleNotification(long delay) {
        Log.d(TAG, "scheduleNotification delay == " + delay);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = 666;
        notificationManager.notify(notificationId, builder.build());

    }

    /**
     * createNotificationChannelid for android API >= 28
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