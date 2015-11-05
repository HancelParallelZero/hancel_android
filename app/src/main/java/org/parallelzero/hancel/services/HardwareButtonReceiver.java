package org.parallelzero.hancel.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by izel on 4/11/15.
 */
public class HardwareButtonReceiver extends BroadcastReceiver {
    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }

        Intent i = new Intent(context, HardwareButtonService.class);
        i.putExtra("screen_state", screenOff);

        context.startService(i);
    }
}
