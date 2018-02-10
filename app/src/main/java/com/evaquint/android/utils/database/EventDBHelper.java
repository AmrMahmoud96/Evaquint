package com.evaquint.android.utils.database;

import android.util.EventLog;
import android.util.Log;

import com.evaquint.android.utils.code.DatabaseValues;
import com.evaquint.android.utils.dataStructures.DetailedEvent;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;
import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

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
    public void setEvent(EventDB event){
        this.event = event;
    }
    public EventDB getEvent(){
        return event;
    }

    public EventDB retreiveEvent(final String eventID){
        //need to check if event ID exists in event db first
            if(!eventID.isEmpty()&&eventID!=null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID);
              ValueEventListener listener =  new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                            Log.i("datasnapshot", dataSnapshot.toString());
                            String eventTitle  = dataSnapshot.child("eventTitle").getValue().toString();
                            String eventHost = dataSnapshot.child("eventHost").getValue().toString();
                            Calendar eventDate = Calendar.getInstance();
                            eventDate.setTimeInMillis(dataSnapshot.child("eventDate").child("timeInMillis").getValue(long.class));
                            String address = dataSnapshot.child("address").getValue().toString();
                            LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class),dataSnapshot.child("location").child("longitude").getValue(double.class));
                            boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
                            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                            List<String> invited =  dataSnapshot.child("invited").getValue(t);
                            List<String> attendees = dataSnapshot.child("invited").getValue(t);
                            DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
                            List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);
                        /*Log.i("be2: ",""+details1.isMult_day() );
                        Log.i("be: ", details1.getPictures().toString());
                        String desc = dataSnapshot.child("details").child("description").getValue(String.class);
                        int ageRestriction = dataSnapshot.child("details").child("ageRestriction").getValue(int.class);
                        List<String> QRCodes = dataSnapshot.child("details").child("QRCodes").getValue(t);
                        List<String> pictures = dataSnapshot.child("details").child("pictures").getValue(t);
                        int capacity = dataSnapshot.child("details").child("capacity").getValue(int.class);
                        boolean mult_day = dataSnapshot.child("details").child("mult_day").getValue(boolean.class);
                        DetailedEvent details;
                        if(mult_day){
                            // add multiple-event days stuff
                            details = new DetailedEvent(desc,ageRestriction,QRCodes,pictures,capacity,mult_day,null);
                        }
                        else{
                            details = new DetailedEvent(desc,ageRestriction,QRCodes,pictures,capacity,mult_day,null);
                    }
                        if(details1.equals(details)){
                            Log.i("the same: ","crazy" );
                        }
                        else{
                            Log.i("not the same: ","crazy" );

                        }*/


                            EventDB event = new EventDB(eventTitle,eventHost,eventDate,address,location,categorizations,eventPrivate,invited,attendees,details);

                            setEvent(event);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error: ", "onCancelled", databaseError.toException());
                    }
                };
            ref.addListenerForSingleValueEvent(listener);
        }


      //  this.database = FirebaseDatabase.getInstance();
    //    this.mDatabase = database.getReference(value.getName());
        //dbConnector.
       // Log.i("event title: ", event.toString());
        EventDB event = getEvent();
        return event;
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
