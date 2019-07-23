package com.evaquint.android.utils.database;

import android.util.Log;

import com.evaquint.android.utils.dataStructures.DetailedEvent;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID).child("attendees").child(attendee);
        ref.setValue(attendee);
    }
    public void removeAttendee(String eventID, String attendee){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID).child("attendees").child(attendee);
        ref.removeValue();
    }
    public void removeEvent(String eventID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID);
        ref.removeValue();
    }
    public void addInvite(String eventID, String invite){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID).child("invited").child(invite);
        ref.setValue(invite);
    }
    public void addInvite(String eventID, Map<String,String> invite){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(EVENTS_TABLE.getName()).child(eventID).child("invited");
        for(String i : invite.keySet()) {
            ref.child(i).setValue(i);
        }
    }

    public EventDB structureEventData(DataSnapshot dataSnapshot){
        try{
            String eventID = dataSnapshot.child("eventID").getValue().toString();
            Calendar eventDate = Calendar.getInstance();
            long timeInMillis = dataSnapshot.child("timeInMillis").getValue(long.class);
            eventDate.setTimeInMillis(timeInMillis);
            String eventTitle = dataSnapshot.child("eventTitle").getValue().toString();
            String eventHost = dataSnapshot.child("eventHost").getValue().toString();

            String address = dataSnapshot.child("address").getValue().toString();
            LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class), dataSnapshot.child("location").child("longitude").getValue(double.class));
            boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
            };
            HashMap<String, String> invited = (HashMap<String, String>) dataSnapshot.child("invited").getValue();
            HashMap<String,String> attendees = (HashMap<String, String>)dataSnapshot.child("attendees").getValue();
            DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
            List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);

            final EventDB event = new EventDB(eventID, eventTitle, eventHost, eventDate.getTimeInMillis(), address, location, categorizations, eventPrivate, invited, attendees, details);


            return event;

        }catch (Exception e){
            Log.e("Translation Err","Unable to turn datasnapshot into an Event.");
            return null;
        }
    }
    public EventDB structureEventDataFromString(String data){
            Gson gson = new Gson();
            try{
//                String json = gson.toJson(data);
                JSONObject d = new JSONObject(data);
                String eventID = d.getString("eventID");
                long timeInMillis = d.getLong("timeInMillis");
                String eventTitle = d.getString("eventTitle");
                String eventHost = d.getString("eventHost");
                String address = d.getString("address");
                LatLng location = new LatLng(((JSONObject) d.get("location")).getDouble("latitude"),((JSONObject) d.get("location")).getDouble("longitude"));
                boolean eventPrivate = d.getBoolean("eventPrivate");
                HashMap<String,String> invited = new HashMap<>();
                if(d.has("invited")){
                    invited = gson.fromJson(d.get("invited").toString(), HashMap.class);
                }
                List<String> categorizations = new ArrayList<>();
                HashMap<String,String> attendees =gson.fromJson(d.get("attendees").toString(), HashMap.class);
                DetailedEvent details = gson.fromJson(d.get("details").toString(), DetailedEvent.class);
                if(d.has("categorizations")){
                    categorizations= new ArrayList<String>(Arrays.asList(d.get("categorizations").toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"","").split(",")));
                }
                final EventDB event = new EventDB(eventID, eventTitle, eventHost, timeInMillis, address, location, categorizations, eventPrivate, invited, attendees, details);
                return event;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }



//        try{
//            String eventID = dataSnapshot.child("eventID").getValue().toString();
//            Calendar eventDate = Calendar.getInstance();
//            long timeInMillis = dataSnapshot.child("timeInMillis").getValue(long.class);
//            eventDate.setTimeInMillis(timeInMillis);
//            String eventTitle = dataSnapshot.child("eventTitle").getValue().toString();
//            String eventHost = dataSnapshot.child("eventHost").getValue().toString();
//
//            String address = dataSnapshot.child("address").getValue().toString();
//            LatLng location = new LatLng(dataSnapshot.child("location").child("latitude").getValue(double.class), dataSnapshot.child("location").child("longitude").getValue(double.class));
//            boolean eventPrivate = (boolean) dataSnapshot.child("eventPrivate").getValue();
//            GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
//            };
//            HashMap<String, String> invited = (HashMap<String, String>) dataSnapshot.child("invited").getValue();
//            HashMap<String,String> attendees = (HashMap<String, String>)dataSnapshot.child("attendees").getValue();
//            DetailedEvent details = dataSnapshot.child("details").getValue(DetailedEvent.class);
//            List<String> categorizations = dataSnapshot.child("categorizations").getValue(t);
//
//            final EventDB event = new EventDB(eventID, eventTitle, eventHost, eventDate.getTimeInMillis(), address, location, categorizations, eventPrivate, invited, attendees, details);
//
//
//            return event;
//
//        }catch (Exception e){
//            Log.e("Translation Err","Unable to turn datasnapshot into an Event.");
//            return null;
//        }
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
