package database.android.com.reminderapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends Activity implements LocationListener {

    LocationManager lm;
    int year = -1, day = -1, month = -1, hr = -1, min = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
                /*if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 5, this);
                    return ;
                }*/
                int permissionCheck = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                    {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 5, this);
                    }
                    else
                    {
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                1001);
                    }
                }

            } else {
                Toast.makeText(this, "GPS Disabled", Toast.LENGTH_SHORT).show();
            }
           /* if (lm != null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                Toast.makeText(this, "NPS Enabled", Toast.LENGTH_SHORT).show();
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 1000, 5, this);
            } else
            {
                Toast.makeText(this, "NPS Disabled", Toast.LENGTH_SHORT).show();
            }*/
        } else {
            Toast.makeText(this, "GPS not Supported", Toast.LENGTH_SHORT).show();
        }

        Notification.Builder b = new Notification.Builder(this);
        b.setAutoCancel(true);
        b.setContentTitle("App Started");
        b.setContentText("Trying to detect your Location");
        b.setSmallIcon(R.mipmap.ic_launcher);
        b.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        Intent intent = new Intent(this, ScheduleTaskActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        b.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = b.build();
        manager.notify(2, n);
    }

    public void onDate(View v) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                MainActivity.this.year = year;
                MainActivity.this.month = monthOfYear;
                MainActivity.this.day = dayOfMonth;
            }
        }, year, month, day);
        dpd.show();
    }

    public void onTime(View v) {
        Calendar calendar = Calendar.getInstance();
        int hr = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                MainActivity.this.hr = hourOfDay;
                MainActivity.this.min = minute;
            }
        }, hr, min, false);
        tpd.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==1001)
            {
                Toast.makeText(this,"permission granted",Toast.LENGTH_LONG).show();
                int permissionCheck = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 5, this);
                    Toast.makeText(this,"location listener started",Toast.LENGTH_LONG).show();

                }
            }
            else
            {
                Toast.makeText(this,"permission dinied",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onSchedule(View v) {
        if (year == -1 || month == -1 || day == -1)
            showDialog("Please choose your desire date", "Alert!");
        if (hr == -1 || min == -1)
            showDialog("Please choose your desire time", "Alert!");
        else
            scheduleTask();
    }

    private void scheduleTask() {
        /*GregorianCalendar cal=new GregorianCalendar(year,month,day,hr,min,0);
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this,ScheduleTaskActivity.class);
        PendingIntent pintent=PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pintent);*/

        GregorianCalendar cal = new GregorianCalendar(year, month, day, hr, min, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(".ReminderReceiver");

        PendingIntent pintent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pintent);
        //sendBroadcast(intent);//only for checking that receiver is working or not

        //more than one pending intent activity
        /*Intent intent2 = new Intent(this, ScheduleTaskActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);*/

        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse("http://google.co.in"));
        startActivity(intent1);
    }

    private void showDialog(String msg, String title) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);
        b.setMessage(msg);
        b.setPositiveButton("OK", null);
        b.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Lattitude: " + location.getLatitude() + " Longitude: " + location.getLongitude(), Toast.LENGTH_LONG);

        Notification.Builder b = new Notification.Builder(this);
        b.setAutoCancel(true);
        b.setContentTitle("App Started");
        b.setContentText("Lat:" + location.getLatitude() + "Lon:" + location.getLongitude());
        b.setSmallIcon(R.mipmap.ic_launcher);
        b.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        Intent intent = new Intent(this, ScheduleTaskActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        b.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = b.build();
        manager.notify(1, n);
        /*if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            lm.removeUpdates(this);
            return;
        }*/
        lm.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
