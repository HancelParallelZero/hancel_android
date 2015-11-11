package org.parallelzero.hancel.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.parallelzero.hancel.Config;

import java.util.Calendar;

/**
 * Created by izel on 4/11/15.
 */
public class HardwareButtonReceiver extends BroadcastReceiver {
    public static final String TAG = HardwareButtonReceiver.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    @Override
    public void onReceive(Context context, Intent intent) {

        startScheduleService(context,Config.DEFAULT_INTERVAL);

    }

    public static void startScheduleService(Context context,long repeatTime){

        if(DEBUG) Log.d(TAG, "HardwareButtonReceiver  startScheduleService");

        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, StartStatusServiceReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();

        // Start x seconds after boot completed
        cal.add(Calendar.SECOND, Config.TIME_AFTER_START);

        service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), repeatTime, pending);
    }

    public static void stopSheduleService(Context context){

        if(DEBUG) Log.d(TAG, "HardwareButtonReceiver  stopSheduleService:");

        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, StartStatusServiceReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        service.cancel(pending);
    }
}
