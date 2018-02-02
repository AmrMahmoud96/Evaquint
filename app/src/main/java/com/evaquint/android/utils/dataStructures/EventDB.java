package com.evaquint.android.utils.dataStructures;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by Amr on 12/01/2017.
 */

@IgnoreExtraProperties
public class EventDB {
    public String eventTitle;
    public String eventHost;
    public String eventLocation;
    public Date eventDate;
    public Time eventTime;
    public boolean eventPrivate;
    public List<String> invited;
    public List<String> attendees;
    public DetailedEvent details;

    public EventDB(String eventTitle,
                   String eventLocation,
                   String eventHost,
                   Date eventDate,
                   Time eventTime,
                   boolean eventPrivate,
                   List<String> invited,
                   List<String> attendees,
                   DetailedEvent details) {
        this.eventHost = eventHost;
        this.details = details;
        this.invited = invited;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.eventTime= eventTime;
        this.eventPrivate = eventPrivate;
        this.attendees=attendees;
    }
}
