package net.foucry.pilldroid;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by jacques on 17/09/16.
 */
public class PillDroidJobService extends JobService {
    private  static final String TAG = "JobService";

    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            Toast.makeText( getApplicationContext(), "PillDroid - Calcul nouveau stocks", Toast.LENGTH_SHORT).show();
//            MedicamentListActivity.newStockCalculation(getApplicationContext());

            jobFinished( (JobParameters) msg.obj,false);
            return true;
        }
    });

    @Override
    public boolean onStartJob (JobParameters params) {
        Log.i(TAG, "on Start Job: " + params.getJobId());
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1,params));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(1);
        return false;
    }


}

