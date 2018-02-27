package com.evaquint.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evaquint.android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

/**
 * Created by henry on 9/10/2017.
 */

public class UserProfileFrag extends Fragment{
    private View view;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        this.mAuth = FirebaseAuth.getInstance();

        init_page();
        return view;
    }

    public void init_page(){

        ((TextView) view.findViewById(R.id.user_profile_name)).setText(mAuth.getCurrentUser().getDisplayName());
        if(mAuth.getCurrentUser().getPhotoUrl()!=null && !mAuth.getCurrentUser().getPhotoUrl().toString().isEmpty()){
            Picasso.with(getActivity()).load(mAuth.getCurrentUser().getPhotoUrl())
                    .into(((ImageView) view.findViewById(R.id.user_profile_image)));
        }
        ((TextView) view.findViewById(R.id.user_profile_name)).setText(mAuth.getCurrentUser().getDisplayName());
        ((TextView) view.findViewById(R.id.userEmail)).setText(mAuth.getCurrentUser().getEmail());
        ((TextView) view.findViewById(R.id.userPhone)).setText(mAuth.getCurrentUser().getPhoneNumber());
        ((ImageView) view.findViewById(R.id.user_profile_edit_name)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }
        );
    }
//        ((ImageView) view.findViewById(R.id.user_profile_image)).setImageURI(mAuth.getCurrentUser().getPhotoUrl());
}