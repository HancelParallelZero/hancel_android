package org.parallelzero.hancel.models;

import android.location.Location;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/9/15.
 */
public class Track {

    public String trackId;

    public Location loc;

    public String alias;


    public Track(String trackId, String alias) {
        this.trackId=trackId;
        this.alias=alias;
    }

    public Track() {

    }

    @Override
    public String toString() {
        return "track: "+trackId+" alias:"+alias+" loc:"+loc;
    }
}
