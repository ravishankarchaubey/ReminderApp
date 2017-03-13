package database.android.com.reminderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by RAVISHANKAR CHAUBEY on 16-04-2016.
 */
public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Receiver Received",Toast.LENGTH_LONG).show();
    }
}
