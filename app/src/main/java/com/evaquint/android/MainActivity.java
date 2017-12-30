package com.evaquint.android;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.evaquint.android.fragments.UserProfileFrag;
import com.evaquint.android.fragments.map.EventLocatorFrag;
import com.evaquint.android.fragments.FeedFrag;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import static android.content.ContentValues.TAG;

import static com.evaquint.android.utils.code.IntentValues.PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static com.evaquint.android.utils.view.FragmentHelper.setActiveFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawer, /*toolbar, */R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        initNavHeader(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton menuButton = (ImageButton) findViewById(R.id.main_activity_menu_button);
        menuButton.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

       /* BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_nearby) {
                    setActiveFragment(getSupportFragmentManager(), new EventLocatorFrag());
                }
                else if (tabId == R.id.tab_search) {
                    setActiveFragment(getSupportFragmentManager(), new FeedFrag());
                }
                else if (tabId == R.id.tab_feed) {

                }
            }
        });*/

        setActiveFragment(getSupportFragmentManager(), new EventLocatorFrag());
    }

    private void initNavHeader(NavigationView navigationView){
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_name))
                .setText(mAuth.getCurrentUser().getDisplayName());
        ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_picture))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setActiveFragment(getSupportFragmentManager(), new UserProfileFrag());
                    }
                });
    }

    @Override
    public void onStart(){
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()==null)
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_event_locator) {
            setActiveFragment(getSupportFragmentManager(), new EventLocatorFrag());
        } else if (id == R.id.nav_gallery) {
            setActiveFragment(getSupportFragmentManager(), new FeedFrag());
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            FirebaseAuth.getInstance().addAuthStateListener(
                    new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                // User is signed in
                                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                            } else {
                                // User is signed out
                                Log.d(TAG, "onAuthStateChanged:signed_out");
                                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                            }
                            // ...
                        }
                    });
        }

        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
