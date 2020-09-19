package net.foucry.pilldroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.icu.util.Calendar;
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
    private String CHANNEL_ID = "PillDroid";
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

        Medicament firstMedicament = null;

        try {
            firstMedicament = medicaments.get(0);
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        // TODO: remove comments when save drugs will wor again
        if (firstMedicament != null) {
            //if (firstMedicament.getPrise() != 0) {
                scheduleNotification();
            //}
        }

        Log.d(TAG, "Job finished");
        jobFinished(params, true);
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    /**
     * Schedule Notification for the delay
     */
    private void scheduleNotification() {
        createNotificationChannel();
        Intent intent = new Intent(this, MedicamentListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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