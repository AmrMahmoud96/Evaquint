package com.evaquint.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.evaquint.android.firebase.Authenticator.AuthenticationConnector;

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
        if ((new AuthenticationConnector()).isAuthenticated()){
            intent = new Intent(this, MainActivity.class);
        } else
            intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
        finish();
    }

}