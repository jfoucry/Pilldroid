package net.foucry.pilldroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Show the toast  like in above screen shot
        Toast.makeText(context, "New stock calculted", Toast.LENGTH_LONG).show();
    }
}
