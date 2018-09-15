package com.evaquint.android.utils.dataStructures;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by henry on 8/19/2017.
 */

@IgnoreExtraProperties
public class UserDB {
    private List<String> friends;
    private String dob;
    private String email;
    private String phone;
    private double lat;
    private double lon;
    private String firstName;
    private String lastName;
    private String picture;
    private int hostRating;
    private int raters;
    private List<String> interests;
    private Map<String,String> eventsHosted;
    private Map<String,String> eventsAttending;
    private String dateCreated;

    public int getHostRating() {
        return hostRating;
    }

    public void setHostRating(int hostRating) {
        this.hostRating = hostRating;
    }

    public int getRaters() {
        return raters;
    }

    public void setRaters(int raters) {
        this.raters = raters;
    }

    public UserDB(String firstName, String lastName, String dob, String picture, String email, String phone, List<String> interests, String dateCreated, Map<String,String> eventsAttending, Map<String,String>eventsHosted, List<String>friends, int hostRating, int raters,double lat, double lon) {
        this.firstName =firstName;
        this.dob = dob;
        this.raters=raters;
        this.hostRating = hostRating;

        this.friends=friends;
        this.lastName = lastName;
        this.eventsHosted = eventsHosted;
        this.eventsAttending = eventsAttending;
        this.picture = picture;
        this.email = email;
        this.phone = phone;
        this.interests = interests;
        this.dateCreated = dateCreated;
        this.lat = lat;
        this.lon = lon;
    }

    public UserDB(){

    }

    public String getDob(){return dob;}
    public void setDob(String dob){this.dob=dob;}
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

    public Map<String,String> getEventsHosted() {
        return eventsHosted;
    }

    public void setEventsHosted(Map<String,String> eventsHosted) {
        this.eventsHosted = eventsHosted;
    }

    public Map<String,String> getEventsAttending() {
        return eventsAttending;
    }

    public void setEventsAttending(Map<String,String> eventsAttending) {
        this.eventsAttending = eventsAttending;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
