package org.parallelzero.hancel.models;

import android.graphics.Bitmap;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class Contact {

    public String name;

    public String phone;

    public Bitmap photo;

    public Contact(String name, String phone, Bitmap photo) {
        this.name=name;
        this.phone=phone;
        this.photo=photo;
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
}
