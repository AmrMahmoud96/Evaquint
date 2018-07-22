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
    private HashMap<String,ArrayList<String>> categories;
    public EventCategories(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(CATEGORIES_TABLE.getName());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                    categories =( HashMap<String,ArrayList<String>>)  dataSnapshot.getValue();
                    Log.i("categories",categories.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public HashMap<String, ArrayList<String>> getCategories() {
        return categories;
    }
}
