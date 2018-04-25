package com.evaquint.android.fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.evaquint.android.R;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.evaquint.android.utils.dataStructures.UserDB;

import java.text.SimpleDateFormat;

/**
 * Created by amrmahmoud on 2018-03-16.
 */

public class EventPageFrag  extends Fragment{
    private View view;
    private EventDB event;

    private TextView eventTitleField;
    private TextView dateField;
    private UserDB host;
    private TextView hostName;
    private RatingBar hostRating;
    private TextView locationField;
    private TextView attendeesField;
    private TextView descriptionField;
    private TextView ageRestrictionField;
    private Button mEventPageBtn;
    private ImageView hostPicture;


    private SimpleDateFormat df = new SimpleDateFormat("E, MMM d, yyyy hh:mm aa");

    public static EventPageFrag newInstance(EventDB event) {
        EventPageFrag fragment = new EventPageFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        fragment.setArguments(bundle);
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_event_page, container, false);

        eventTitleField = view.findViewById(R.id.eventTitle);
        dateField = view.findViewById(R.id.eventDateField);
        hostName = view.findViewById(R.id.eventHostName);
        hostPicture = view.findViewById(R.id.eventHostPicture);
        hostRating = view.findViewById(R.id.eventHostRatingBar);
        locationField = view.findViewById(R.id.eventLocField);
        attendeesField = view.findViewById(R.id.eventAttField);
        ageRestrictionField = view.findViewById(R.id.eventARField);
        descriptionField = view.findViewById(R.id.eventDescField);
        mEventPageBtn = view.findViewById(R.id.eventPageBtn);


        this.event = (EventDB) getArguments().getSerializable("event");
        init_page();
        return view;
    }

    public void init_page(){
        // note capacity will be attendees/cap and not there if it is 0
    if(event!=null){
        //get and set user.
        eventTitleField.setText(event.eventTitle);
        dateField.setText(df.format(event.timeInMillis));
        locationField.setText(event.address);
        attendeesField.setText(event.attendees.size());

        //set logic for detailed description stuff

        //set logic for if the event host is viewing the event.


        //ageRestrictionField.setText(event);


    }

/*
        String userID = mAuth.getCurrentUser().getUid();
        if(!userID.isEmpty()&&userID!=null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        Log.i("datasnapshot", dataSnapshot.toString());
                        user = dataSnapshot.getValue(UserDB.class);
                        ((TextView) view.findViewById(R.id.user_profile_name)).setText(mAuth.getCurrentUser().getDisplayName());
                        if(mAuth.getCurrentUser().getPhotoUrl()!=null && !mAuth.getCurrentUser().getPhotoUrl().toString().isEmpty()){
                            Picasso.with(getActivity()).load(mAuth.getCurrentUser().getPhotoUrl())
                                    .into(((ImageView) view.findViewById(R.id.user_profile_image)));
                        }
                        ((TextView) view.findViewById(R.id.user_profile_name)).setText(user.getFirstName()+" "+user.getLastName());
                        ((TextView) view.findViewById(R.id.userEmail)).setText(user.getEmail());
                        ((TextView) view.findViewById(R.id.userPhone)).setText(user.getPhone());
                        /*RecyclerView friends = ((RecyclerView)view.findViewById(R.id.friendsListV));
                        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
                        friends.setLayoutManager(layoutManager);
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List<String> friendsList =  dataSnapshot.child("friends").getValue(t);

                        Log.i("datasnapshot", friendsList.toArray(new String[0])[0]);
                        FriendsListAdapter f = new FriendsListAdapter(friendsList.toArray(new String[0]));
                        friends.setAdapter(f);
                        //friends.set
                        //dataSnapshot.getValue();
                        RatingBar hostRating =(RatingBar) view.findViewById(R.id.hostRating);

                        hostRating.setNumStars(5);
                        if(user.getHostRating() ==0 || user.getRaters()==0){
                            hostRating.setRating(0);
                        }else{
                            hostRating.setRating(user.getHostRating()/user.getRaters());
                        }

                        ((ImageView) view.findViewById(R.id.user_profile_edit_name)).setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }
                        );
                        // user[0] = dataSnapshot.getValue(UserDB.class);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());
                }
            });
        }
*/

    }
}
