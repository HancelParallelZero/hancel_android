package org.parallelzero.hancel.System;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.models.Ring;
import org.parallelzero.hancel.models.Track;
import org.parallelzero.hancel.services.TrackLocationService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by izel on 4/11/15.
 */
public class Storage {

    private static final String PREF_GENERAL_PANIC_ALERT = "lastPanic";
    private static final String PREF_SAVE_RINGS = "ringsSaved";
    private static final String PREF_SHARE_LOCATION = "shareLocationEnable";
    private static final String PREF_TRACK_ID = "trackId";
    private static final String PREF_TRACKING_TARGET = "trackingTarget";
    private static final String PREF_FIRST_INTRO = "firsIntro";
    private static final String PREF_SAVE_TRACKERS = "saveTrackers";
    private static final String PREF_CURRENT_ALIAS = "currentAlias";
    private static final String PREF_CURRENT_COLOR = "currentColor";

    public static void setLastPanicAlertDate(Context ctx, String time) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(PREF_GENERAL_PANIC_ALERT, time);
        ed.commit();
    }

    public static String getLastPanicAlertDate(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(PREF_GENERAL_PANIC_ALERT, ctx.getString(R.string.no_panic_alert));
    }

    public static ArrayList<Ring> getRings(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String ringJson = preferences.getString(PREF_SAVE_RINGS, "");
        if (ringJson.equals("")) return new ArrayList<>();
        else {
            Type listType = new TypeToken<ArrayList<Ring>>() {
            }.getType();
            return new Gson().fromJson(ringJson, listType);
        }
    }

    public static void saveRings(Context ctx, ArrayList<Ring> rings) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SAVE_RINGS, new Gson().toJson(rings));
        editor.commit();
    }

    public static void saveRing(Context ctx, Ring ring) {
        ArrayList<Ring> rings = getRings(ctx);
        rings.add(ring);
        saveRings(ctx, rings);
    }

    public static void removeRing(Context ctx, Ring ring) {
        ArrayList<Ring> rings = getRings(ctx);
        Iterator<Ring> it = rings.iterator();
        while (it.hasNext()) {
            if (it.next().getName().equals(ring.getName())) it.remove();
        }
        saveRings(ctx, rings);
    }

    public static void enableRing(Context ctx, Ring ring, boolean enable) {
        ArrayList<Ring> rings = getRings(ctx);
        Iterator<Ring> it = rings.iterator();
        while (it.hasNext()) {
            Ring item = it.next();
            if (item.getName().equals(ring.getName())) item.setEnable(enable);
        }
        saveRings(ctx, rings);
    }

    public static void setShareLocationEnable(Context ctx, boolean shareLocation) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_SHARE_LOCATION, shareLocation);
        editor.commit();
    }

    public static boolean isShareLocationEnable(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(PREF_SHARE_LOCATION, false);
    }

    public static void setTrackId(Context ctx, String trackId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_TRACK_ID, trackId);
        editor.commit();
    }

    public static String getTrackId(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(PREF_TRACK_ID, null);
    }

    public static void setTargetTracking(Context ctx, String path) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_TRACKING_TARGET, path);
        editor.commit();
    }

    public static String getTargetTracking(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(PREF_TRACKING_TARGET, null);
    }

    public static ArrayList<Ring> getRingsEnable(Context ctx) {
        ArrayList<Ring> rings = getRings(ctx);
        Iterator<Ring> it = rings.iterator();
        while (it.hasNext()) if (!it.next().isEnable()) it.remove();
        return rings;
    }

    public static boolean isFirstIntro(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(PREF_FIRST_INTRO, true);
    }

    public static void setFirstIntro(Context ctx, boolean enable) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean(PREF_FIRST_INTRO, enable);
        ed.commit();
    }

    public static ArrayList<Track> getTrackers(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String ringJson = preferences.getString(PREF_SAVE_TRACKERS, "");
        if (ringJson.equals("")) return new ArrayList<>();
        else {
            Type listType = new TypeToken<ArrayList<Track>>() {
            }.getType();
            return new Gson().fromJson(ringJson, listType);
        }
    }

    public static void saveTrackers(Context ctx,ArrayList<Track>trackers){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SAVE_TRACKERS, new Gson().toJson(trackers));
        editor.commit();
    }

    @Deprecated
    public static void addTracker(Context ctx, String trackId, String alias) {
        ArrayList<Track> trackers = getTrackers(ctx);
        trackers.add(new Track(trackId, alias));
        saveTrackers(ctx, trackers);
    }


    synchronized public static void addTracker(Context ctx, Track track) {
        ArrayList<Track> trackers = getTrackers(ctx);
        trackers.add(track);
        saveTrackers(ctx, trackers);
    }

    synchronized public static void updateTracker(Context ctx, Track track) {
        ArrayList<Track> trackers = getTrackers(ctx);
        Iterator<Track> it = trackers.iterator();
        while (it.hasNext()){
            Track currentTrack = it.next();
            if(currentTrack.trackId.equals(track.trackId)){
                trackers.remove(trackers.indexOf(currentTrack));
                trackers.add(track);
            }
        }
        saveTrackers(ctx, trackers);
    }

    public static boolean isOldTracker(Context ctx,String trackerId) {
        ArrayList<Track> trackers = getTrackers(ctx);
        Iterator<Track> it = trackers.iterator();
        while (it.hasNext())if(it.next().trackId.equals(trackerId))return true;
        return false;
    }

    public static String getCurrentAlias(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(PREF_CURRENT_ALIAS, "");
    }

    public static void setCurrentAlias(Context ctx, String alias){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(PREF_CURRENT_ALIAS, alias);
        ed.commit();
    }

    public static int getCurrentColor(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getInt(PREF_CURRENT_COLOR, Color.MAGENTA);
    }
}
