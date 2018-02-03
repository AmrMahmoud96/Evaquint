package com.evaquint.android.utils.dataStructures;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Amr on 12/01/2017.
 */

@IgnoreExtraProperties
public class EventDB {
    public String eventID;
    public String eventTitle;
    public String eventHost;
    public LatLng location;
    public String address;
    public Calendar eventDate;
    public boolean eventPrivate;
    public List<String> invited;
    public List<String> attendees;
    public DetailedEvent details;

    public EventDB(String eventTitle,
                   String eventHost,
                   Calendar eventDate,
                   String address,
                   LatLng location,
                   boolean eventPrivate,
                   List<String> invited,
                   List<String> attendees,
                   DetailedEvent details) {
        this.eventID  = UUID.randomUUID().toString();
        this.location = location;
        this.address = address;
        this.eventHost = eventHost;
        this.details = details;
        this.invited = invited;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventPrivate = eventPrivate;
        this.attendees=attendees;
    }
}
