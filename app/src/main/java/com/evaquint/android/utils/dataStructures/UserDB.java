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
    public String firstName;
    public String lastName;
    public String picture;
    public List<String> interests;
    public String dateCreated;

    public UserDB(String firstName,String lastName, String picture,String email, String phone, List<String> interests, String dateCreated) {
        this.firstName =firstName;
        this.lastName = lastName;
        this.picture = picture;
        this.email = email;
        this.phone = phone;
        this.interests = interests;
        this.dateCreated = dateCreated;
    }
}
