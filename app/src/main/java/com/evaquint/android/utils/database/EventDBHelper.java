package com.evaquint.android.utils.database;

import android.util.EventLog;
import android.util.Log;

import com.evaquint.android.utils.code.DatabaseValues;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;
import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

/**
 * Created by henry on 8/22/2017.
 */

public class EventDBHelper {
    DBConnector dbConnector;
    SimpleDateFormat df;

    public EventDBHelper() {
         dbConnector = new DBConnector(EVENTS_TABLE);
        df = new SimpleDateFormat("E, MMM d, yyyy hh:mm aa");
    }

    public void addEvent(EventDB event) {
        dbConnector.writeToDB(event.eventID,event);
    }

    public EventDB retreiveEvent(String eventID){
        //need to check if event ID exists in event db first
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // add variables for each child info.
                Log.i("data: ", dataSnapshot.child("eventTitle").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("error: ", "onCancelled", databaseError.toException());
            }
        });
      //  this.database = FirebaseDatabase.getInstance();
    //    this.mDatabase = database.getReference(value.getName());
        //dbConnector.
        return null;
    }
/*
    public boolean addEntry(DatabaseValues subPath, String value){
        String path = USER_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + value;
        return dbConnector.writeToDB(path, value);
    }

    public boolean deleteEntry(DatabaseValues subPath, String value){
        String path = USER_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + value==null?"":value;
        return dbConnector.deleteFromDB(path, value);
    }

    public boolean listenToEntry(DatabaseValues subPath, String friend){
        ValueEventListener listener;
        String path = USER_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + friend;
        return dbConnector.writeToDB(path, friend);
    }
*/
}
