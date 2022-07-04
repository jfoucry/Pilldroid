package net.foucry.pilldroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

/**
 * Created by jacques on 17/09/16.
 */

public class PillDroidJobService extends JobService {
    private  static final String TAG = JobService.class.getName();
    private boolean jobCancelled = false;
    private final DBHelper dbHelper = new DBHelper(this);


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        createNotificationChannel();
        doBackgroundWork(params);

        return false;
    }

    /**
     * Grab sorted list of medicaments
     * test dateAlert of the first of the list
     * if dateAlert < now
     *  schedule notification
     * @param params JobParameters
     */
    private void doBackgroundWork(final JobParameters params) {

        Log.d(TAG,"background job");
        if (jobCancelled) {
            return;
        }
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
                    scheduleNotification();
                } else
                {
                    double dummy = (firstDrug.getStock() - firstDrug.getAlertThreshold());
                    Log.d(TAG, "no notification scheduled " + dummy);
                }
            }
        }

        Log.d(TAG, "Job finished");
        jobFinished(params, true);
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return false;
    }

    /**
     * Schedule Notification for the delay
     */
    void scheduleNotification() {
        Log.d(TAG, "schedule notification");
        createNotificationChannel();
        Intent intent = new Intent(this, DrugListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "PillDroid")
                .setSmallIcon(R.drawable.ic_pill_alarm)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
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
        String CHANNEL_ID = "PillDroid";
        NotificationChannel channel;
        channel = new NotificationChannel(CHANNEL_ID, name, importance);

        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
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
