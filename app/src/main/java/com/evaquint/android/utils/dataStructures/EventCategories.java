package com.evaquint.android.utils.dataStructures;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.evaquint.android.utils.code.DatabaseValues.CATEGORIES_TABLE;

/**
 * Created by amrmahmoud on 2018-03-23.
 */

public class EventCategories {
    public static void getEventCategories(final firebase_listener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(CATEGORIES_TABLE.getName());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null&&dataSnapshot.getValue()!=null && listener !=null){
                    listener.onUpdate((Object) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(listener !=null){
                    listener.onCancel();
                }
            }
        });
    }
}
