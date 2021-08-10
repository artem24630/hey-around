package ru.atproduction.heyaround;

import android.os.Parcel;
import android.os.Parcelable;

public class User {
    private String id;
    private String name;
    private String email;
    private String idAuth;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void setIdAuth(String uid) {
        idAuth = uid;
    }
    public String getIdAuth(){
        return idAuth;
    }
}
