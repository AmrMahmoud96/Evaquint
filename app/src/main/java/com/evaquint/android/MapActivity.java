package com.evaquint.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        intent = new Intent(this, HomeActivity.class);
        setContentView(R.layout.activity_map);
    }

    public void goToMessaging(View view) {
        Intent intent = new Intent(this, MessagingActivity.class);
        startActivity(intent);
    }
    public void goToHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }
}
