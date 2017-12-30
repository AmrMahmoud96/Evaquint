package com.evaquint.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by henry on 8/3/2017.
 */


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent;
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            intent = new Intent(this, MainActivity.class);
        } else
            intent = new Intent(this, LoginActivity.class);

        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        finish();
    }

}