package com.evaquint.android.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.evaquint.android.HomeActivity;
import com.evaquint.android.R;
import com.evaquint.android.utils.dataStructures.EventDB;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.evaquint.android.utils.database.EventDBHelper;
import com.evaquint.android.utils.database.GeofireDBHelper;
import com.evaquint.android.utils.database.UserDBHelper;
import com.evaquint.android.utils.storage.PhotoDownloadHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

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
    private EventDBHelper eventDBHelper;

    private ImageView eventPicture;

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
        this.view = inflater.inflate(R.layout.fragment_temp_event_page, container, false);

        ((HomeActivity)getActivity()).disableMenuButton();

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
        eventDBHelper = new EventDBHelper();

        eventPicture = view.findViewById(R.id.eventPicture);


        this.event = (EventDB) getArguments().getSerializable("event");
        List<String> pictures = event.details.getPictures();
        if(pictures!=null){
            try {
                String picURL = pictures.get(0);
                if (picURL != null && !picURL.isEmpty()) {
                    //  eventPic.setImageBitmap(getImageBitmap(picURL));
                    Picasso.with(view.getContext()).load(event.details.getPictures().get(0)).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(eventPicture);
                }
                Log.i("downloaded", "true");
            } catch (Exception e) {
                Log.e("event image error", e.getMessage());
            }
        }
        if(this.event !=null && this.event.eventHost!=null && !this.event.eventHost.isEmpty()){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(this.event.eventHost);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        Log.i("event host", dataSnapshot.toString());
                        host = dataSnapshot.getValue(UserDB.class);
                        hostName.setText(host.getFirstName()+ " " +host.getLastName());
                        hostRating.setStepSize((float)0.1);
                        hostRating.setRating((float)host.getHostRating()/(float) host.getRaters());
                        hostRating.setIsIndicator(true); // disabled the rating bar
                        if(!host.getPicture().equals("default")){
                            PhotoDownloadHelper pdh = new PhotoDownloadHelper();
                            hostPicture.setImageBitmap(pdh.getImageBitmap(host.getPicture()));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());
                }
            });
        }
        init_page();
        return view;
    }

    public void init_page(){
        // note capacity will be attendees/cap and not there if it is 0
    if(event!=null){
        Log.i("event",event.toString());
        //get and set user.
        eventTitleField.setText(event.eventTitle);
        dateField.setText(df.format(event.timeInMillis));
        locationField.setText(event.address);
        descriptionField.setText(event.details.getDescription());


        if(event.details.getCapacity()==0){
            attendeesField.setText(""+event.attendees.size());
        }else{
            attendeesField.setText(event.attendees.size()+"/"+event.details.getCapacity());
        }
        if(event.eventPrivate){
            //change this
            attendeesField.setText(event.attendees.size());
           // attendeesField.setText(event.attendees.size()+"/"+event.invited.size());
        }


        if(event.details.getAgeRestriction()==0){
            ageRestrictionField.setVisibility(View.INVISIBLE);
        }

        final String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if( currUserID.equals(event.eventHost)){
            mEventPageBtn.setText("Cancel Event");
            mEventPageBtn.setBackgroundColor(Color.RED);
        }else{
            if(event.attendees.contains(currUserID)){
                mEventPageBtn.setText("Unregister");
                mEventPageBtn.setBackgroundColor(Color.RED);
            }else{
                mEventPageBtn.setText("Register");
                mEventPageBtn.setBackgroundColor(Color.GREEN);
            }


        }
        mEventPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = mEventPageBtn.getText().toString();
                if(buttonText.equalsIgnoreCase("Register")){
                    new UserDBHelper().addEventAttended(currUserID,event.eventID);
                    eventDBHelper.addAttendee(event.eventID,currUserID);
                    mEventPageBtn.setText("Unregister");
                    mEventPageBtn.setBackgroundColor(Color.RED);
                }else if(buttonText.equalsIgnoreCase("Unregister")){
                    eventDBHelper.removeAttendee(event.eventID,currUserID);
                    new UserDBHelper().removeEventAttended(currUserID,event.eventID);
                    mEventPageBtn.setText("Register");
                    mEventPageBtn.setBackgroundColor(Color.GREEN);
                }else if(buttonText.equalsIgnoreCase("Cancel Event")){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirmation")
                            .setMessage("Do you really want to cancel the event?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new UserDBHelper().removeEventHosted(currUserID,event.eventID);
                                    new EventDBHelper().removeEvent(event.eventID);
                                    new GeofireDBHelper("events").removeEvent(event.eventID);
                                    getFragmentManager().popBackStack();
                                    Toast.makeText(getActivity(), "Event Deleted.",
                                            Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton("No", null).show();
                    // confirm to cancel or not
                    //delete event (what about people attending.)
                }
            }
        });

        //if(event.attendees)

        //TODO @Amr the following line is a null reference, check the names
//        attendeesField.setText(event.attendees.size());

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
