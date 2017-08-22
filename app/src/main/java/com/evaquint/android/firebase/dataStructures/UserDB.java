package com.evaquint.android.firebase.dataStructures;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by henry on 8/19/2017.
 */

@IgnoreExtraProperties
public class UserDB {
    public List<String> provider;
    public List<String> friend;
    public String email;
    public String phone;
    public String displayName;



    public UserDB() {
        // Default constructor required for calls to DataSnapshot.getValue(UserDB.class)
    }

    public UserDB(List<String> provider, String displayName, String email, String phone) {
        this.provider = provider;
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
    }
}
