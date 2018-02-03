package com.evaquint.android.utils.database;

import android.util.Log;

import com.evaquint.android.utils.dataStructures.EventDB;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

import static com.evaquint.android.utils.code.DatabaseValues.EVENTS_TABLE;
import static com.evaquint.android.utils.code.DatabaseValues.GEOFIRE_TABLE;

/**
 * Created by henry on 8/22/2017.
 */

public class GeofireDBHelper {
    GeoFire geoFire;
    //DBConnector dbConnector;
    //SimpleDateFormat df;

    public GeofireDBHelper() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(GEOFIRE_TABLE.getName());
         geoFire = new GeoFire(ref);
       // df = new SimpleDateFormat("E, MMM d, yyyy hh:mm aa");
    }

    public void addEventToGeofire(EventDB event) {
        geoFire.setLocation(event.eventID, new GeoLocation(event.location.latitude, event.location.longitude));
    }

    public GeoQuery queryAtLocation(LatLng location, int radius){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("key","Key: "+key);
                Log.d("Location", "Latitude: "+location.latitude + " Longitude: "+location.longitude);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
      //  geoQuery.
        return geoQuery;
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
