package com.evaquint.android.utils.dataStructures;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Amr on 12/01/2017.
 */

public class EventDB implements Serializable{
    public String eventID;
    public String eventTitle;
    public String eventHost;
    public LatLng location;
    public List<String> categorizations;
    public String address;
    public long timeInMillis;
    public boolean eventPrivate;
    public Map<String, String> invited;
    public List<String> attendees;
    public DetailedEvent details;

    public EventDB(
                      String eventID,
                      String eventTitle,
                   String eventHost,
                   long timeInMillis,
                   String address,
                   LatLng location,
                   List<String> categorizations,
                   boolean eventPrivate,
                   Map<String, String> invited,
                   List<String> attendees,
                   DetailedEvent details) {
        this.eventID  = eventID;
        this.categorizations=categorizations;
        this.timeInMillis = timeInMillis;
        this.location = location;
        this.address = address;
        this.eventHost = eventHost;
        this.details = details;
        this.invited = invited;
        this.eventTitle = eventTitle;
        this.eventPrivate = eventPrivate;
        this.attendees=attendees;
    }
}
