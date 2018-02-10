package com.evaquint.android.utils.dataStructures;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by henry on 8/19/2017.
 */

@IgnoreExtraProperties
public class UserDB {
    private List<String> friends;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String picture;
    private List<String> interests;
    private List<String> eventsHosted;
    private List<String> eventsAttending;
    private String dateCreated;

    public UserDB(String firstName, String lastName, String picture, String email, String phone, List<String> interests, String dateCreated, List<String> eventsAttending, List<String>eventsHosted,List<String>friends) {
        this.firstName =firstName;
        this.friends=friends;
        this.lastName = lastName;
        this.eventsHosted = eventsHosted;
        this.eventsAttending = eventsAttending;
        this.picture = picture;
        this.email = email;
        this.phone = phone;
        this.interests = interests;
        this.dateCreated = dateCreated;
    }

    public UserDB(){

    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getEventsHosted() {
        return eventsHosted;
    }

    public void setEventsHosted(List<String> eventsHosted) {
        this.eventsHosted = eventsHosted;
    }

    public List<String> getEventsAttending() {
        return eventsAttending;
    }

    public void setEventsAttending(List<String> eventsAttending) {
        this.eventsAttending = eventsAttending;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


}
