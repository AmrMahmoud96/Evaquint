package com.evaquint.android.modules;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.evaquint.android.R;

/**
 * Created by henry on 8/7/2017.
 */

public class MaterialButton extends PercentRelativeLayout{
    public MaterialButton(Context context) {
        this(context, null);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.fitted_panel, this);
//        contentFrame = (LinearLayout) findViewById(R.id.content_frame);
    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){
//        if(contentFrame == null){
//            super.addView(child, index, params);
//        }else{
//            contentFrame.addView(child, index, params);
//        }

    }
}
