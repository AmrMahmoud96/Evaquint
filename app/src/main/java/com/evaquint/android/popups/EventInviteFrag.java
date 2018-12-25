package com.evaquint.android.popups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evaquint.android.R;
import com.evaquint.android.utils.Adapter.FriendsListAdapter;
import com.evaquint.android.utils.listeners.CustomItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.evaquint.android.utils.code.DatabaseValues.USER_TABLE;

public class EventInviteFrag extends DialogFragment {
    private View view;
    private String userID;
    private HashMap<String,String> invited;
    private Button mInviteBtn;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public EventInviteFrag() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EventInviteFrag newInstance(String userID) {
        EventInviteFrag frag = new EventInviteFrag();
        Bundle args = new Bundle();
        args.putString("user", userID);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);

        getDialog().getWindow().setLayout(width, height);
        getDialog().setCanceledOnTouchOutside(true);

        this.view =  inflater.inflate(R.layout.fragment_invite_popup, container);
        this.userID =getArguments().getString("user");
        mInviteBtn = (Button) view.findViewById(R.id.invBtn);

        final RecyclerView friends = ((RecyclerView)view.findViewById(R.id.friendsInviteList));
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USER_TABLE.getName()).child(userID).child("friends");
        invited = new HashMap<String,String>();
        new Thread() {
            public void run() {
                try {
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null&&dataSnapshot.getValue()!=null) {
                                Log.i("friends", dataSnapshot.toString());
                                LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
                                friends.setLayoutManager(layoutManager);
                                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                                final List<String> friendsList = dataSnapshot.getValue(t);
                                FriendsListAdapter f = new FriendsListAdapter(friendsList.toArray(new String[0]), new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        String userID = friendsList.get(position);
//                                        Log.i("invite friend",userID);
                                        if(invited.get(userID)!=null){
                                            invited.remove(userID);
                                        }else{
                                            invited.put(userID,userID);
                                        }
//                                        Log.i("invites",invited.toString());
                                        //   Log.i("invited",invited.toString());
                                    }
                                });
                                friends.setAdapter(f);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
        mInviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("invited",invited);
                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, i);
                dismiss();
            }
        });


        return view;
    }




}