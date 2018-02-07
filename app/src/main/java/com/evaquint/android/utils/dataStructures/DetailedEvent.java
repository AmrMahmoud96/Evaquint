package com.evaquint.android.utils.dataStructures;

import java.sql.Time;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by amrmahmoud on 2018-02-02.
 */

public class DetailedEvent {
    //Description, age restriction, QR code, pictures, capacity, multiple-day events > dates, time periods for the event 

       private String description;
        private int ageRestriction;
   private List<String> QRCodes;
    private List<String> pictures;
    private int capacity;
    private boolean mult_day;
    private List<Calendar> eventDates;

        public DetailedEvent(String description,
                 int ageRestriction,
                 List<String> QRCodes,
                 List<String> pictures,
                 int capacity,
                 boolean mult_day,
                 List<Calendar> eventDates) {
            this.description = description;
            this.mult_day=mult_day;
            this.eventDates = eventDates;
            this.ageRestriction=ageRestriction;
            this.QRCodes = QRCodes;
            this.pictures=pictures;
            this.capacity = capacity;
        }
        public DetailedEvent(){
            this.description = "";
            this.mult_day = false;
            this.eventDates = null;
            this.capacity= 9999;
            this.pictures = Arrays.asList("default");
            this.QRCodes = null;
            this.ageRestriction = 0;
        }
    public String getDescription(){
        return description;
    }
    public int getAgeRestriction(){
        return ageRestriction;
    }
    public List<String> getPictures(){
        return pictures;
    }
    public List<String> getQRCodes(){
        return QRCodes;
    }
    public int getCapacity(){
        return capacity;
    }
    public List<Calendar> getEventDates(){
        return eventDates;
    }
    public boolean isMult_day(){
        return mult_day;
    }


}
