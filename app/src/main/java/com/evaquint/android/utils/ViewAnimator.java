package com.evaquint.android.utils;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.evaquint.android.R;

/**
 * Created by henry on 8/22/2017.
 */

public class ViewAnimator {

    private Animation animShow, animHide;

    public ViewAnimator(Activity a)
    {
        animShow = AnimationUtils.loadAnimation(a, R.anim.slide_up);
        animShow.setFillAfter(true);
        animShow.setDuration(500);
        animHide = AnimationUtils.loadAnimation(a, R.anim.slide_down);
        animHide.setFillAfter(true);
        animHide.setDuration(500);
    }

    public void slideDownThenUp(View view){
        if (view.getVisibility()==View.GONE)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
        view.startAnimation( animHide );
    }

    public void showSlideDown(View view){
        view.setVisibility(View.VISIBLE);
        view.startAnimation( animShow );
    }

    public void hideSlideUp(final View view){
        view.startAnimation( animHide );
        view.setVisibility(View.GONE);
    }

}
