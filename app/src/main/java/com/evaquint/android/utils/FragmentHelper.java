package com.evaquint.android.utils;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.evaquint.android.LoginActivity;
import com.evaquint.android.R;

import static android.content.ContentValues.TAG;

/**
 * Created by henry on 8/22/2017.
 */

public class FragmentHelper {

    public static void setActiveFragment(Fragment activeFragment, Fragment newFrag){
        try {
            FragmentTransaction ft = activeFragment.getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            if(activeFragment==null)
                ft.add(R.id.content_frame, newFrag);
            else
                ft.replace(activeFragment.getId(), newFrag);
            ft.commit();
        } catch (Exception e) {
            Log.d(TAG, "Fragment Switched Failed With Message: " + e.getMessage());
            return;
        }
    }

    public static void setActiveFragment(FragmentManager fragmentManager, Fragment newFrag){
        try {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment activeFragment =  fragmentManager.findFragmentById(R.id.content_frame);
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        if(activeFragment==null)
            ft.add(R.id.content_frame, newFrag);
        else
            ft.replace(activeFragment.getId(), newFrag);
            ft.commit();
        } catch (Exception e) {
            Log.d(TAG, "Fragment Switched Failed With Message: " + e.getMessage());
            return;
        }
    }



}
