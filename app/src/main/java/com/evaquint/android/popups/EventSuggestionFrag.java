package com.evaquint.android.popups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.map.EventLocatorFrag;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.evaquint.android.utils.dataStructures.firebase_listener;
import com.evaquint.android.utils.database.GeofireDBHelper;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;
import static com.evaquint.android.utils.dataStructures.EventCategories.getEventCategories;

public class EventSuggestionFrag extends DialogFragment {
    private TextView mRecText;
    private TextView mRecommendation;
    private UserDB user;
    private Button mRunAgain;
    private Button mContinue;
    private String lastSuggestion;
    private ArrayList<String> userInterests;
    private Map<String, ArrayList<String>> categories;
    private Map<String,ArrayList<String>> friendsInterests;
    private Map<String,Integer> nearbyInterests;
    private ArrayList<String> friendsList;
    private View view;
    private ArrayList<String> nearbyUsers;
    private String userID;
    public ArrayList<String> pastSuggested;

    final GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

    public EventSuggestionFrag() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

   /* public static EventSuggestionFrag newInstance(String address, LatLng location, String eventID) {
        EventSuggestionFrag frag = new EventSuggestionFrag();
        Bundle args = new Bundle();
        args.putString("address", address);
        args.putDouble("latitude", location.latitude);
        args.putDouble("longitude",location.longitude);
        args.putString("eventID",eventID);
        frag.setArguments(args);
        return frag;
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((EventLocatorFrag)getTargetFragment()).setPopupOpen();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);

        getDialog().getWindow().setLayout(width, height);
        getDialog().setCanceledOnTouchOutside(true);

        this.view =  inflater.inflate(R.layout.fragment_popup_recommendation, container);
        view.setPadding(50,0,50,50);
        mRecText =  (TextView) view.findViewById(R.id.recText);
        mRecommendation = (TextView) view.findViewById(R.id.recommendation);
        mContinue = (Button) view.findViewById(R.id.contButton);
        mRunAgain = (Button) view.findViewById(R.id.reroll);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to venue recommendation then to popup quick event
                Intent i = new Intent();
                i.putExtra("suggestion", lastSuggestion);
                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, i);
                dismiss();
            }
        });

        mRunAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int suggestionType = (int) Math.floor(Math.random()*5);
                suggestEvent(suggestionType);
            }
        });
        init();
        return view;
    }

    @Override
    public void onDestroyView() {
        ((EventLocatorFrag)getTargetFragment()).setPopupClosed();
        super.onDestroyView();
    }


    public void init(){
        getEventCategories(new firebase_listener(){
            @Override
            public void onUpdate(Object data, Fragment fragment){
                categories = (HashMap<String, ArrayList<String>>) data;
                Log.i("cat",categories.toString());
                // need to init user + interests, friends interests, geofire query interests.
                assignUser();
                suggestEvent(4);
            }

            @Override
            public void onCancel(){
                //Do something when cancel
            }
        }, this);
//        Log.i("cat",categories.toString());
//        categories = new EventCategories().getCategories();
        nearbyUsers = new ArrayList<String>();
        pastSuggested = new ArrayList<String>();
        //Log.i("cat:",categories.toString());


    }
    public void assignUser(){

        Log.i("p1", "called ");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.i("user", userID);
        if(!userID.isEmpty()&&userID!=null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        Log.i("datasnapshot", dataSnapshot.toString());
                        user = dataSnapshot.getValue(UserDB.class);
                        if(user.getInterests()!=null){
                            userInterests = new ArrayList<>(user.getInterests());
                        }
                        if(user.getFriends()!=null){
                            friendsInterests = new HashMap<String,ArrayList<String>>();
                            friendsList = new ArrayList<>(user.getFriends());
                        }
                        nearbyInterests = new HashMap<String, Integer>();
                    }

                    assignFriends();
                    assignNearby();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());
                }
            });
        }
    }
    public void assignFriends(){

        Log.i("p2", "called ");
        if(friendsList!=null){
            for(String fuserID: friendsList){
                if(!fuserID.isEmpty()&&fuserID!=null){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(fuserID);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                                Log.i("datasnapshot", dataSnapshot.toString());
                                UserDB fuser = dataSnapshot.getValue(UserDB.class);
                                if(fuser.getInterests()!=null){
                                    friendsInterests.put(fuser.getFirstName(),new ArrayList<String>(fuser.getInterests()));
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("error: ", "onCancelled", databaseError.toException());
                        }
                    });
                }
            }
        }
    }
    public void assignNearby(){

        Log.i("p3", "called ");

        Log.i("p3", " "+user.getLat());
        Log.i("p3", " "+user.getLon());
        if(user.getLat()!=0 && user.getLon()!=0){
        GeofireDBHelper helper = new GeofireDBHelper("users");
        final GeoQuery surroundingUsers = helper.queryAtLocation(new LatLng(user.getLat(),user.getLon()),10);
        surroundingUsers.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                String nuserID = key;

                Log.i("key",nuserID);
                if(!nuserID.isEmpty()&&nuserID!=null&&nuserID!=userID){
                    nearbyUsers.add(nuserID);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                surroundingUsers.removeAllListeners();
                getNearbyInterests();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }
    }
    public void getNearbyInterests(){
        Log.i("p4","ay");
        if(nearbyUsers!=null){

            for(String userID : nearbyUsers){
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("interests");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                            Log.i("datasnapshot", dataSnapshot.toString());
                            List<String> interests = dataSnapshot.getValue(t);
                            for(String interest: interests){
                                if(nearbyInterests.get(interest)==null){
                                    nearbyInterests.put(interest,1);
                                }else{
                                    nearbyInterests.put(interest,nearbyInterests.get(interest)+1);
                                }
                            }

                            Log.i("nearby", nearbyInterests.toString());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error: ", "onCancelled", databaseError.toException());
                    }
                });
            }
        }
        int firstSuggestion = (int) Math.floor(Math.random()*5);
        suggestEvent(firstSuggestion);

    }


    public void suggestEvent(int suggestionType){
               /* Fundamentally different than recommending an event to go to (this recommends events to make)
        * Ways to recommend people events
        *   1. Based off user's interests
        *   2. Based off user's friends interests
        *   3. Based off surrounding peoples interests
        *   4. Randomly based off all event types
        *
        *Set the recommendation text specific to each type.
        *
        * Ways to better algorithm:
        *  1. find the recommendations contained between recommendations 1-4
        *  2. add an arraylist of already shown recommendations and check if the returned one is in that list and run again
        *
        * */
        Log.i("suggestion",""+suggestionType);

        if(pastSuggested.size() <=20){


        String recommendation = "";
        if(user!=null){
            //1. Based off user's interests
            if(suggestionType == 1 ){
                mRecText.setText("We saw that you are interested in:");
                if(userInterests!=null&&!userInterests.isEmpty()){
                    int suggestion = (int) Math.floor(Math.random()*userInterests.size());
                    recommendation = userInterests.get(suggestion);
                    userInterests.remove(suggestion);
                }
                //recommendation = "a";
            }
            //2. Based off user's friends interests
            if(suggestionType == 2 && friendsInterests!=null){
                // find most common interests from Hashmap
                // map category to friends interested in it and find max.
                Map<String,ArrayList<String>> total = new HashMap<String,ArrayList<String>>();
                int m = 0;
                for(String friend: friendsInterests.keySet()){
                    for(String cat: friendsInterests.get(friend)){
                        ArrayList<String> f = total.get(cat);
                       if(f== null){
                           f = new ArrayList<String>();
                       }
                       if(!f.contains(friend)){
                           f.add(friend);
                           total.put(cat,f);
                       }
                        if(f.size()>m && !pastSuggested.contains(cat)){
                            recommendation = cat;
                            m = f.size();
                        }

                    }
                }

                mRecText.setText("X, Y, and Z others are interested in:");
                //recommendation = "b";
            }
            //3. Based off surrounding peoples interests
          /*  if(suggestionType == 3){
                Log.i("nearby people" , nearbyUsers.toString());
                Log.i("nearby int",nearbyInterests.toString());
            }*/
            if(suggestionType == 3 && nearbyInterests!=null){
                int max = 0;
                for(String c: nearbyInterests.keySet()){
                    int n = nearbyInterests.get(c);
                    if(n>max && !pastSuggested.contains(c)){
                        max = n;
                        recommendation = c;
                    }
                   // if(nearbyInterests){

                //    }
                }
                if(max == 1){
                    mRecText.setText(max + " person near you is interested in:");
                }else{
                    mRecText.setText(max + " people near you are interested in:");
                }
              //  recommendation = "c";
            }
        }


        //4. Randomly based off all event types
        if(suggestionType == 4 || user == null){
            mRecText.setText("How about trying:");
            ArrayList<String> cat = new ArrayList<>(categories.keySet());
            int catSelect = (int) Math.floor(Math.random()*cat.size());
            String category = cat.get(catSelect);
            if(category.equalsIgnoreCase("other")){
                suggestEvent(suggestionType);
            }else{
                ArrayList<String> subcat = categories.get(category);
                if(subcat!=null){
                    String selection = subcat.get((int)Math.floor(Math.random()*subcat.size()));
                    recommendation = selection;
                }else{
                    recommendation = category;
                }
            }
        }

        if(recommendation.isEmpty()){
            // if nothing was returned, try again with type 4 suggestion.
            suggestEvent(4);
        }else{
            if(recommendation.equalsIgnoreCase(lastSuggestion) || pastSuggested.contains(recommendation)){
                // no re runs of the same type.
                suggestEvent(4);
            }else{
                pastSuggested.add(recommendation);
                mRecommendation.setText(recommendation);
                lastSuggestion = recommendation;
            }
        }

    }
    }
}