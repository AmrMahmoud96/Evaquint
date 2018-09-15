package com.evaquint.android.utils.dataStructures;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by henry on 9/11/2018.
 */

public interface firebase_listener {
    public void onUpdate(Object data, Fragment fragment);
    public void onCancel();
}