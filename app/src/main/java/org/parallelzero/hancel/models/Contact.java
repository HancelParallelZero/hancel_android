package org.parallelzero.hancel.models;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class Contact {

    public Uri photoUri;

    public String name;

    public String phone;

    public Bitmap photo;

    public Contact(String name, String phone, Bitmap photo) {
        this.name=name;
        this.phone=phone;
        this.photo=photo;
    }


    public Contact(String name, String phone, Uri uri) {
        this.name=name;
        this.phone=phone;
        this.photoUri =uri;
    }


    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "name: "+name+" phone:"+phone;
    }

    public String getPhone() {
        return phone;
    }

    public Bitmap getPhoto() {
        return photo;
    }
}
