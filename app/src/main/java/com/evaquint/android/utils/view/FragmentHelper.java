package com.evaquint.android.utils.view;


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
            setActiveFragment(activeFragment.getFragmentManager(), newFrag, R.id.content_frame);
    }

    public static void setActiveFragment(FragmentManager fragmentManager, Fragment newFrag){
        setActiveFragment(fragmentManager, newFrag, R.id.content_frame);
    }

    public static void setActiveFragment(FragmentManager fragmentManager, Fragment newFrag, int containerID){
        try {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment activeFragment =  fragmentManager.findFragmentById(containerID);
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,R.anim.fade_in, R.anim.fade_out);
            if(activeFragment==null)
                ft.add(containerID, newFrag);
            else
                ft.replace(activeFragment.getId(), newFrag).addToBackStack("testTag");
            ft.commit();
        } catch (Exception e) {
            Log.d(TAG, "Fragment Switched Failed With Message: " + e.getMessage());
            return;
        }
    }



}
