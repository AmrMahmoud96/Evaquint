package com.evaquint.android.utils.dataStructures;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by henry on 8/19/2017.
 */

@IgnoreExtraProperties
public class EventDB {
    public String eventTitle;
    public String eventLocation;
    public String eventDescription;
    public Date eventDate;
    public Time eventTime;
    public boolean ageRestriction;
    public boolean eventPrivate;
    public List<String> attendees;
    public List<String> QRCodes;
    public List<String> pictures;
    public int capacity;

    public EventDB(String eventTitle,
                   String eventLocation,
                   String eventDescription,
                   Date eventDate,
                   Time eventTime,
                   boolean ageRestriction,
                   boolean eventPrivate,
                   List<String> attendees,
                   List<String> QRCodes,
                   List<String> pictures,
                   int capacity) {
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventTime= eventTime;
        this.ageRestriction=ageRestriction;
        this.eventPrivate = eventPrivate;
        this.attendees=attendees;
        this.QRCodes = QRCodes;
        this.pictures=pictures;
        this.capacity = capacity;
    }
}
