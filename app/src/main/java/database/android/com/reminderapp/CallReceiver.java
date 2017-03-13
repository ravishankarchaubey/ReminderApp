package database.android.com.reminderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by RAVISHANKAR CHAUBEY on 16-04-2016.
 */
public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action.equals("android.intent.action.PHONE_STATE"))
            Toast.makeText(context, "Call Received", Toast.LENGTH_LONG).show();
        else if(action.equals("android.provider.Telephony.SMS_RECEIVED"))
            Toast.makeText(context, "SMS Received", Toast.LENGTH_LONG).show();
    }
}
