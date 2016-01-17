package org.parallelzero.hancel.models;

import android.graphics.Bitmap;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class Partner {

    public String alias;

    public Bitmap avatar;


    public String last_update;

    public Partner(String alias, String last_update) {
        this.alias=alias;
        this.avatar=avatar;
        this.last_update=last_update;
    }

    public String getAlias() {
        return alias;
    }

    public String getLast_update() {
        return last_update;
    }

    @Override
    public String toString() {
        return "alias: "+alias;
    }

}
