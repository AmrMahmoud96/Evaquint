package com.evaquint.android.utils.dataStructures;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by henry on 8/19/2017.
 */

@IgnoreExtraProperties
public class UserDB {
    public List<String> friends;
    public String email;
    public String phone;
    public String displayName;
    public String picture;
    public List<String> interests;

    public UserDB(String displayName, String picture,String email, String phone, List<String> interests) {
        this.displayName = displayName;
        this.picture = picture;
        this.email = email;
        this.phone = phone;
        this.interests = interests;
    }
}
