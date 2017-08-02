package com.evaquint.evaquint;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.evaquint.evaquint.Fragments.EventLocatorFrag;
import com.evaquint.evaquint.Fragments.FeedFrag;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawer, /*toolbar, */R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        this.activeFragment = new EventLocatorFrag();
        fragmentTransaction.add(R.id.content_frame, activeFragment);
        fragmentTransaction.commit();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

        //Closing this.drawer on item click
        Fragment fragment = null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        
        if (id == R.id.nav_event_locator) {
            fragment = new EventLocatorFrag();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            fragmentTransaction.replace(activeFragment.getId(),fragment);
//            loadFragment(fragmentTransaction);
            fragmentTransaction.commit();
            activeFragment=fragment;
        } else if (id == R.id.nav_gallery) {
            fragment = new FeedFrag();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            fragmentTransaction.replace(activeFragment.getId(),fragment);
//            loadFragment(fragmentTransaction);
            fragmentTransaction.commit();
            activeFragment=fragment;
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(final FragmentTransaction ft){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ft.commit();
            }
        }, 200);
    }

}
