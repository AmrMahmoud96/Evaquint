package com.evaquint.android.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.evaquint.android.popups.EventAttendeesFrag;
import com.evaquint.android.popups.EventInviteFrag;
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
import java.util.Map;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;
import static com.evaquint.android.utils.code.IntentValues.EVENT_ATTENDEES_FRAGMENT;
import static com.evaquint.android.utils.code.IntentValues.EVENT_INVITES_FRAGMENT;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;


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
    private UserDBHelper userDBHelper;
    private String currUserID;

    private Button inviteBtn;
    private boolean hideBtn=true;

    private ImageView eventPicture;

    private SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM d, yyyy hh:mm aa");

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
        userDBHelper = new UserDBHelper();

        eventPicture = view.findViewById(R.id.eventPicture);


        this.event = (EventDB) getArguments().getSerializable("event");
        currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> pictures = event.details.getPictures();
        hostPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileFrag userProfileFrag = new UserProfileFrag().newInstance(event.eventHost);
                setActiveFragment(EventPageFrag.this, userProfileFrag);
            }
        });

        attendeesField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                EventAttendeesFrag editNameDialogFragment = new EventAttendeesFrag().newInstance(event.attendees);

                editNameDialogFragment.setTargetFragment(getParentFragment(), EVENT_ATTENDEES_FRAGMENT);
                editNameDialogFragment.show(fm, "fragment_attendees_popup");
            }
        });




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
    public void addInviteBtn(){
        if(!event.eventPrivate || event.eventHost==currUserID){
            if(hideBtn){
                if(inviteBtn!=null){
                    inviteBtn.setVisibility(View.INVISIBLE);
                }
                return;
            }
            ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.eventPageLayout);
            ConstraintSet set = new ConstraintSet();
            set.clone(layout);

            //Invite Button
            inviteBtn = new Button(this.getContext());
            inviteBtn.setText("Invite");
            inviteBtn.setId(android.R.id.button1);
            layout.addView(inviteBtn);

            set.connect(inviteBtn.getId(), ConstraintSet.BOTTOM, mEventPageBtn.getId(), ConstraintSet.TOP, 15);
            set.connect(inviteBtn.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,0);
            set.connect(inviteBtn.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,0);
            set.connect(inviteBtn.getId(),ConstraintSet.TOP,descriptionField.getId(),ConstraintSet.BOTTOM);
            set.constrainHeight(inviteBtn.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(inviteBtn.getId(),ConstraintSet.WRAP_CONTENT);
            set.applyTo(layout);

            inviteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getFragmentManager();
                    EventInviteFrag editNameDialogFragment = new EventInviteFrag().newInstance(currUserID);

                    editNameDialogFragment.setTargetFragment(EventPageFrag.this, EVENT_INVITES_FRAGMENT);
                    editNameDialogFragment.show(fm, "fragment_invite_popup");

                }
            });
            inviteBtn.setVisibility(View.VISIBLE);
        }
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
        if(event.details.getDescription().trim().isEmpty()){
            descriptionField.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.descLabel).setVisibility(View.INVISIBLE);
        }



        if(event.details.getCapacity()==0){
            attendeesField.setText(""+event.attendees.size());
        }else{
            attendeesField.setText(event.attendees.size()+"/"+event.details.getCapacity());
        }
        if(event.eventPrivate){
            //change this
            attendeesField.setText(""+event.attendees.size());
           // attendeesField.setText(event.attendees.size()+"/"+event.invited.size());
        }


        if(event.details.getAgeRestriction()==0){
            ageRestrictionField.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.ARLabel).setVisibility(View.INVISIBLE);
        }else{
            ageRestrictionField.setText(String.valueOf(event.details.getAgeRestriction()));
        }

        if( currUserID.equals(event.eventHost)){
            mEventPageBtn.setText("Cancel Event");
            mEventPageBtn.setBackgroundColor(Color.RED);
            hideBtn=false;
        }else{
            if(event.attendees.get(currUserID)!=null){
                mEventPageBtn.setText("Unregister");
                mEventPageBtn.setBackgroundColor(Color.RED);
                hideBtn=false;
            }else{
                mEventPageBtn.setText("Register");
                mEventPageBtn.setBackgroundColor(Color.GREEN);
                hideBtn=true;

            }
        }
        addInviteBtn();

        mEventPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = mEventPageBtn.getText().toString();
                if(buttonText.equalsIgnoreCase("Register")){
                    userDBHelper.addEventAttended(currUserID,event.eventID);
                    eventDBHelper.addAttendee(event.eventID,currUserID);
                    mEventPageBtn.setText("Unregister");
                    mEventPageBtn.setBackgroundColor(Color.RED);
                    hideBtn=false;
                }else if(buttonText.equalsIgnoreCase("Unregister")){
                    eventDBHelper.removeAttendee(event.eventID,currUserID);
                    userDBHelper.removeEventAttended(currUserID,event.eventID);
                    mEventPageBtn.setText("Register");
                    mEventPageBtn.setBackgroundColor(Color.GREEN);
                    hideBtn=true;
                }else if(buttonText.equalsIgnoreCase("Cancel Event")){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirmation")
                            .setMessage("Do you really want to cancel the event?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //delete event (and recursively remove event attended for each person)
                                    userDBHelper.removeEventHosted(currUserID,event.eventID);
                                    for(String a : event.attendees.values()){
                                        Log.i("d-attend",a);
                                        userDBHelper.removeEventAttended(a,event.eventID);
                                    }
                                    eventDBHelper.removeEvent(event.eventID);
                                    new GeofireDBHelper("events").removeEvent(event.eventID);
                                    getFragmentManager().popBackStack();
                                    Toast.makeText(getActivity(), "Event Deleted.",
                                            Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton("No", null).show();
                    // confirm to cancel or not
                }

                addInviteBtn();
            }
        });


    }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EVENT_INVITES_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    Map<String, String> invited = (Map<String, String>) bundle.getSerializable("invited");
                    Log.i("invited",invited.toString());
                    eventDBHelper.addInvite(event.eventID,invited);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //do nothing
                }
                break;
        }
    }

}
