package org.parallelzero.hancel.models;

import java.util.List;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class Ring {

    public String name;

    public String description;

    public List<Contact> contacts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
