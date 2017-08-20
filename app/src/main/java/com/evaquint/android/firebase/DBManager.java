package com.evaquint.android.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by henry on 8/19/2017.
 */

public class DBManager {

    FirebaseDatabase database;
    DatabaseReference mDatabase;

    public DBManager (){
        this.database = FirebaseDatabase.getInstance();
        this.mDatabase = database.getReference();
    }

    public DBManager (String ref){
        this.database = FirebaseDatabase.getInstance();
        this.mDatabase = database.getReference(ref);
    }

    public void writeToDB(String targ, Object item){
        mDatabase.child(targ).setValue(item);
    }

    public String readFromDB(String targ){
        // Read from the database
        database.getReference().child(targ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return null;
    }
}
