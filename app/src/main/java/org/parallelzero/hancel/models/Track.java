package org.parallelzero.hancel.models;

import android.location.Location;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/9/15.
 */
public class Track {

    public String trackId;


    public String alias;

    public int color;

    private boolean enable;

    private String lastUpdate;

    public double lat;

    public double lon;

    public float acu;

    public long upd;

    public Track(String trackId, String alias) {
        this.trackId = trackId;
        this.alias = alias;
    }

    public Track() {

    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    @Override
    public String toString() {
        return "track: " + trackId + " alias:" + alias;
    }

    public String getLastUpdate() {
        // TODO: set last update on track
        lastUpdate = "Wednesday 23, 17:25";
        return lastUpdate;
    }
}
