package org.parallelzero.hancel.System;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.parallelzero.hancel.R;


/**
 * Created by izel on 4/11/15.
 */
public class HancelPreferences {

    public static final String PREF_GENERAL_PANIC_ALERT = "lastPanic";

    public static void setLastPanicAlertDate(Context context,String time){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(PREF_GENERAL_PANIC_ALERT, time);
        ed.commit();
    }

    public static String getLastPanicAlertDate(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_GENERAL_PANIC_ALERT, context.getString(R.string.no_panic_alert));
    }

}
