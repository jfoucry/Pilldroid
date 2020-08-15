package net.foucry.pilldroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;
import java.util.List;

import static net.foucry.pilldroid.NotificationPublisher.NOTIFICATION_ID;


/**
 * Created by jacques on 17/09/16.
 */

public class PillDroidJobService extends JobService {
    private  static final String TAG = JobService.class.getName();
    private boolean jobCancelled = false;

    private List<Medicament> medicaments = null;
    private DBHelper dbHelper = new DBHelper(this);

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {

        if (jobCancelled) {
            return;
        }
        medicaments = dbHelper.getAllDrugs();

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

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.KEY_MESSAGE, getString(R.string.pharmacy));
        notificationIntent.putExtra(NotificationPublisher.KEY_TITLE, getString(R.string.app_name));
        notificationIntent.putExtra(NotificationPublisher.KEY_SOUND, true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        }
    }
}