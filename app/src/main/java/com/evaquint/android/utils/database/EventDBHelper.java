package com.evaquint.android.utils.database;

import com.evaquint.android.utils.dataStructures.EventDB;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;

/**
 * Created by henry on 8/22/2017.
 */

public class EventDBHelper {
    DBConnector dbConnector;
    SimpleDateFormat df;
    EventDB event;

    public EventDBHelper() {
         dbConnector = new DBConnector(EVENTS_TABLE);
        df = new SimpleDateFormat("E, MMM d, yyyy hh:mm aa");
    }

    public void addEvent(EventDB event) {
        dbConnector.writeToDB(event.eventID,event);
    }
    public void addTestEvent(String id, EventDB event) {
        dbConnector.writeToDB(id,event);
    }
    public void addAttendee(String eventID, String attendee){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID).child("attendees");
        ref.push().setValue(attendee);
    }
    public void removeAttendee(String eventID, String attendee){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID).child("attendees").child(attendee);
        ref.removeValue();
    }
    public void removeEvent(String eventID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID);
        ref.removeValue();
    }
/*
    public boolean addEntry(DatabaseValues subPath, String value){
        String path = EVENTS_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + value;
        return dbConnector.writeToDB(path, value);
    }

    public boolean deleteEntry(DatabaseValues subPath, String value){
        String path = EVENTS_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + value==null?"":value;
        return dbConnector.deleteFromDB(path, value);
    }

    public boolean listenToEntry(DatabaseValues subPath, String friend){
        ValueEventListener listener;
        String path = EVENTS_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + friend;
        return dbConnector.writeToDB(path, friend);
    }*/

}
