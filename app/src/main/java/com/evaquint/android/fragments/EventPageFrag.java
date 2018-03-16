package com.evaquint.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evaquint.android.R;
import com.evaquint.android.utils.dataStructures.EventDB;

/**
 * Created by amrmahmoud on 2018-03-16.
 */

public class EventPageFrag  extends Fragment{
    private View view;
    //private FirebaseAuth mAuth;
    //UserDB user;
    private EventDB event;

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

        this.event = (EventDB) getArguments().getSerializable("event");
        init_page();
        return view;
    }

    public void init_page(){


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
