package com.evaquint.android.utils.dataStructures;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by amrmahmoud on 2018-02-02.
 */

public class DetailedEvent {
    //Description, age restriction, QR code, pictures, capacity, multiple-day events > dates, time periods for the event 

        public String description;
        public int ageRestriction;
    public List<String> QRCodes;
    public List<String> pictures;
    public int capacity;
    public boolean mult_day;
    public List<Date> eventDates;
    public List<Time> eventTimes;

        public DetailedEvent(String description,
                 int ageRestriction,
                 List<String> QRCodes,
                 List<String> pictures,
                 int capacity,
                 boolean mult_day,
                 List<Date> eventDates,
                 List<Time> eventTimes) {
            this.description = description;
            this.mult_day=mult_day;
            this.eventDates = eventDates;
            this.eventTimes = eventTimes;
            this.ageRestriction=ageRestriction;
            this.QRCodes = QRCodes;
            this.pictures=pictures;
            this.capacity = capacity;
        }
        public DetailedEvent(){
            this.description = "";
            this.mult_day = false;
            this.eventTimes = null;
            this.eventDates = null;
            this.capacity= 9999;
            this.pictures = Arrays.asList("default");
            this.QRCodes = null;
            this.ageRestriction = 0;
        }


}
