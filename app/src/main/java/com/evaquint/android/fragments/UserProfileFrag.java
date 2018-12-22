package com.evaquint.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.evaquint.android.HomeActivity;
import com.evaquint.android.R;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

/**
 * Created by henry on 9/10/2017.
 */

public class UserProfileFrag extends Fragment {
    private View view;
    private FirebaseAuth mAuth;
    UserDB user;
    private String uID="";
    public UserProfileFrag(){
    }
    public static UserProfileFrag newInstance(String userID) {
        UserProfileFrag frag = new UserProfileFrag();
        Bundle args = new Bundle();
        args.putString("user", userID);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HomeActivity)getActivity()).disableMenuButton();

        this.view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        this.mAuth = FirebaseAuth.getInstance();
        this.uID = getArguments().getString("user");
        init_page();
        return view;
    }
    /*
    * to update user profile image and photo url
    * FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
        .setDisplayName("Jane Q. User")
        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
        .build();

user.updateProfile(profileUpdates)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User profile updated.");
                }
            }
        });
    * */

    public void init_page(){
        String userID = "";
        if(uID ==null||uID.isEmpty()){
             userID = mAuth.getCurrentUser().getUid();
        }else{
            userID=uID;
        }


        if(!userID.isEmpty()&&userID!=null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null&&dataSnapshot.getValue()!=null){
                        Log.i("datasnapshot", dataSnapshot.toString());
                        user = dataSnapshot.getValue(UserDB.class);
                        ((TextView) view.findViewById(R.id.user_profile_name)).setText(mAuth.getCurrentUser().getDisplayName());
                      // if(mAuth.getCurrentUser().getPhotoUrl()!=null && !mAuth.getCurrentUser().getPhotoUrl().toString().isEmpty()){
                        if(!user.getPicture().equals("default")) {
                            Picasso.with(getActivity()).load(user.getPicture())
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
                        friends.setAdapter(f);*/
                        //friends.set
                       //dataSnapshot.getValue();
                        RatingBar hostRating =(RatingBar) view.findViewById(R.id.hostRating);

                        hostRating.setNumStars(5);
                        if(user.getHostRating() ==0 || user.getRaters()==0){
                            hostRating.setRating(0);
                        }else{
                            hostRating.setRating(user.getHostRating()/user.getRaters());
                        }

//                        ((ImageView) view.findViewById(R.id.user_profile_edit_name)).setOnClickListener(
//                                new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//
//                                    }
//                                }
//                        );
                       // user[0] = dataSnapshot.getValue(UserDB.class);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error: ", "onCancelled", databaseError.toException());
                }
            });
        }


    }
//        ((ImageView) view.findViewById(R.id.user_profile_image)).setImageURI(mAuth.getCurrentUser().getPhotoUrl());
}