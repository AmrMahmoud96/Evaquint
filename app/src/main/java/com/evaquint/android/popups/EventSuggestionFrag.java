package com.evaquint.android.popups;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.fragments.map.EventLocatorFrag;
import com.evaquint.android.utils.dataStructures.EventCategories;
import com.evaquint.android.utils.dataStructures.UserDB;
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
import java.util.List;
import java.util.Map;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

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
        this.view =  inflater.inflate(R.layout.fragment_popup_recommendation, container);
        mRecText =  (TextView) view.findViewById(R.id.recText);
        mRecommendation = (TextView) view.findViewById(R.id.recommendation);
        mContinue = (Button) view.findViewById(R.id.contButton);
        mRunAgain = (Button) view.findViewById(R.id.reroll);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to venue recommendation then to popup quick event

                Log.i("place","");
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
        String[] catArray = getResources().getStringArray(R.array.event_categories);
        categories = new EventCategories(catArray).getCategories();
        //Log.i("cat:",categories.toString());

        // need to init user + interests, friends interests, geofire query interests.

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(!userID.isEmpty()&&userID!=null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        Log.i("datasnapshot", dataSnapshot.toString());
                        user = dataSnapshot.getValue(UserDB.class);
                        userInterests = new ArrayList<>(user.getInterests());
                        friendsList = new ArrayList<>(user.getFriends());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());
                }
            });
        }
        for(String fuserID: friendsList){
            if(!fuserID.isEmpty()&&fuserID!=null){
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(fuserID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                            Log.i("datasnapshot", dataSnapshot.toString());
                            UserDB fuser = dataSnapshot.getValue(UserDB.class);
                            friendsInterests.put(fuser.getFirstName(),new ArrayList<String>(fuser.getInterests()));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("error: ", "onCancelled", databaseError.toException());
                    }
                });
            }
        }
        if(user.getLat()!=0 && user.getLon()!=0){
            GeofireDBHelper helper = new GeofireDBHelper();
            final GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
            final GeoQuery surroundingUsers = helper.queryAtLocation(new LatLng(user.getLat(),user.getLon()),10);
            surroundingUsers.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    String nuserID = key;
                    if(!nuserID.isEmpty()&&nuserID!=null){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(nuserID).child("interests");
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

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("error: ", "onCancelled", databaseError.toException());
                            }
                        });
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
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });

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

        String recommendation = "";
        if(user!=null){
            //1. Based off user's interests
            if(suggestionType == 1 ){
                mRecText.setText("We saw that you are interested in:");
                recommendation = "a";
            }
            //2. Based off user's friends interests
            if(suggestionType == 2 && friendsInterests!=null){
                mRecText.setText("X, Y, and Z others are interested in:");
                recommendation = "b";
            }
            //3. Based off surrounding peoples interests
            if(suggestionType == 3 ){
                mRecText.setText("X people near you are interested in:");
                recommendation = "c";
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
            if(recommendation.equalsIgnoreCase(lastSuggestion)){
                // no re runs of the same type.
                suggestEvent(4);
            }else{
                mRecommendation.setText(recommendation);
                lastSuggestion = recommendation;
            }
        }

    }
}