package com.evaquint.android.utils.database;

import android.util.Log;

import com.evaquint.android.utils.code.DatabaseValues;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

/**
 * Created by henry on 8/22/2017.
 */

public class UserDBHelper {
    DBConnector dbConnector;
    SimpleDateFormat df;

    public UserDBHelper() {
        dbConnector = new DBConnector(USER_TABLE);
        df = new SimpleDateFormat("E, MMM d, yyyy hh:mm aa");
    }

    public void addUser(FirebaseUser fUser) {
        dbConnector.writeToDB(fUser.getUid(), new UserDB(fUser.getDisplayName().split(" ")[0], fUser.getDisplayName().split(" ")[1],0, fUser.getPhotoUrl().toString(), fUser.getEmail(),fUser.getPhoneNumber(), new ArrayList<String>(),Calendar.getInstance().getTimeInMillis(),  new HashMap<String, String>(),new HashMap<String, String>(), Arrays.asList(""),0,0,0,0));
    }

    public void addUser(String uid, UserDB user) {
        dbConnector.writeToDB(uid, user);
    }

    public boolean addEntry(DatabaseValues subPath, String value){
        String path = USER_TABLE.getName() + "/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + subPath.getName()
                + "/" + value;
        return dbConnector.writeToDB(path, value);
    }
    public void addEventHosted(String userID, String eventID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("eventsHosted").child(eventID);
        ref.setValue(eventID);
    }
    public void addEventAttended(String userID, String eventID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("eventsAttended").child(eventID);
        ref.setValue(eventID);
    }
    public void removeEventAttended(String userID, String eventID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("eventsAttended").child(eventID);
        ref.removeValue();
    }
    public void removeEventHosted(String userID, String eventID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("eventsHosted").child(eventID);
        ref.removeValue();
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
    public UserDB getUser(String userID){
        final UserDB[] user = new UserDB[1];
//need to check if USERID is in database or not
        if(!userID.isEmpty()&&userID!=null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        Log.i("datasnapshot", dataSnapshot.toString());
                        user[0] = dataSnapshot.getValue(UserDB.class);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());
                }
            });
        }


        return user[0];
    }


}
