package com.evaquint.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by henry on 8/3/2017.
 */


public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in and make a decision to go to login/main
        processUserData();
    }

    private boolean checkAuthStatus(){
        return mAuth.getCurrentUser()!=null;
    }

    private void processUserData(){
        Intent intent;
        if (checkAuthStatus()) {
            intent = new Intent(this, MainActivity.class);
        } else
            intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
        finish();
    }

}