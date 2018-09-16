package com.evaquint.android.fragments.signup;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evaquint.android.R;
import com.evaquint.android.utils.dataStructures.UserDB;
import com.evaquint.android.utils.database.UserDBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class SignupInterestsFrag extends Fragment {
    private View view;
    private Activity activity;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        this.view = inflater.inflate(R.layout.fragment_signup_interests, container, false);
        this.activity = getActivity();

        ((Button) view.findViewById(R.id.signup_interests_next_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextFrag();
                    }
                });

        return this.view;
    }

    private boolean validateValues(){
        return false;
    }

    private void nextFrag(){
        createPhoneUser();
        setActiveFragment(SignupInterestsFrag.this, new SignupInviteFrag());
    }
    private void createPhoneUser(){
        FirebaseUser fUser = mAuth.getCurrentUser();
        if(fUser!=null){
            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getArguments().getString("firstName") +" "+ getArguments().getString("lastName"))
                    .build();
            fUser.updateProfile(updateProfile).addOnCompleteListener(activity, null);
            //   userDatabaseHandler.addUser(fUser.getUid(),new UserDB());
            UserDBHelper userDBHelper = new UserDBHelper();
            Bundle userInfo = getArguments();
            userDBHelper.addUser(fUser.getUid(),
                    new UserDB(userInfo.getString("firstName"),userInfo.getString("lastName"),userInfo.getLong("dob"),"default","",userInfo.getString("phoneNumber"), Arrays.asList(""),Calendar.getInstance().getTimeInMillis(),new HashMap<String, String>(), new HashMap<String, String>(), Arrays.asList(""),0,0,0,0));

        }

    }
}

